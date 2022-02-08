package clavela.msoe.connectfour;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class GameBoard extends View {
    private Paint gridPaint;

    final int rows = 6;
    final int cols = 7;
    final int connectLength = 4;
    final float rInset = (float) 0.8;

    private final MediaPlayer playPiece = MediaPlayer.create(this.getContext(), R.raw.gamepiece);
    private final MediaPlayer victory = MediaPlayer.create(this.getContext(), R.raw.victory);
    private final Handler mhandler = new Handler();

    private float[] dimensions = {100, 100};
    private float lpad = 0;
    private float tpad = 0;
    private float width = 100;
    private float height = 100;
    private float radius = 0;
    private int lastCol = 0;
    private int currentPlayer = 1;
    private int currentWinner = 0;

    private int[][] boardState;
    private int[] nextPlayer = {1, 2, 1};
    private int[][] moves = {{1, 0}, {1, 1}, {0, 1}, {1, -1}};
    private Paint[] gamePaints = new Paint[3];

    public GameBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

        for (int i = 0; i < gamePaints.length; i++) {
            gamePaints[i] = new Paint();
        }

        gamePaints[0].setColor(Color.WHITE);
        gamePaints[1].setColor(Color.RED);
        gamePaints[2].setColor(Color.YELLOW);
        boardState = new int[rows][cols];
        reset();

        View.OnTouchListener touchListener = new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                lastCol = (int) ((motionEvent.getX() - lpad)/(radius*2));
                if(lastCol >= cols || motionEvent.getX() < lpad){
                    lastCol = -1;
                }
                return false;
            }
        };

        this.setOnTouchListener(touchListener);

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                play(lastCol);
            }
        });
    }

    private void play(int col){
        if(col < 0 || col >= cols){
            return;
        }
        if(boardState[0][col] != 0){
            return;
        }
        for (int row = rows-1; row >= 0; row--) {
            if(boardState[row][col] == 0){
                boardState[row][col] = currentPlayer;
                currentPlayer = nextPlayer[currentPlayer];
                if(is_full()){
                    reset();
                }
                this.invalidate();
                playPiece.start();
                currentWinner = has_winner();
                if(currentWinner != 0){
                    victory.start();
                    mhandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            reset();
                            invalidate();
                        }
                    }, 1000);
                }
                return;
            }
        }
    }

    private void reset(){
        currentWinner = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                boardState[row][col] = 0;
            }
        }
    }

    private int has_winner(){
        for (int i = 0; i < 10; i++) {
            System.out.println();
        }
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col <= cols - connectLength; col++) {
                int player = boardState[row][col];
                if(player != 0) {
                    System.out.println("Checking " + col + ", " + row + ": " + player);
                    for (int[] move : moves) {
                        System.out.println("move = " + move);
                        int length = 1;
                        for (int offset = 1; offset < connectLength; offset++) {
                            int checkrow = row + (move[0] * offset);
                            int checkcol = col + (move[1] * offset);
                            if(checkrow >= 0 && checkcol >= 0 && checkrow < rows && checkcol < cols) {
                                System.out.println(checkcol + ", " + checkrow + ": " + boardState[checkrow][checkcol]);
                                if (boardState[checkrow][checkcol] ==player){
                                    length += 1;
                                }
                            }
                        }
                        if(length == connectLength){
                            return player;
                        }
                    }
                }
            }
        }
        return 0;
    }

    private boolean is_full(){
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if(boardState[row][col] == 0){
                    return false;
                }
            }
        }
        return true;
    }

    private void init(){
        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        float xpad = (float)(getPaddingLeft() + getPaddingRight());
        float ypad = (float)(getPaddingTop() + getPaddingBottom());

        float ww = (float)w - xpad;
        float hh = (float)h - ypad;

        dimensions = new float[]{ww, hh};

        double drawWidth = ww;
        double drawHeight = (drawWidth * rows) / cols;
        lpad = 0;
        tpad = (float) ((hh - drawHeight) / 2);

        if(drawHeight > hh){
            drawHeight = hh;
            drawWidth = (drawHeight * cols) / rows;
            tpad = 0;
            lpad = (float) ((ww - drawWidth) / 2);
        }

        width = (float) drawWidth;
        height = (float) drawHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.setBackgroundColor(gamePaints[currentPlayer].getColor());
        Paint testPaint = new Paint();
        testPaint.setColor(Color.BLUE);
        canvas.drawRect(lpad,tpad, width + lpad, height + tpad, testPaint);

        radius = (width / (float)(cols * 2.0));
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                float cx = (float) (lpad + ((2 * col) + 1) * radius);
                float cy = (float) (tpad + ((2 * row) + 1) * radius);
                Paint paint = gamePaints[boardState[row][col]];
                canvas.drawCircle(cx, cy, radius * rInset, paint);
            }
        }
    }
}
