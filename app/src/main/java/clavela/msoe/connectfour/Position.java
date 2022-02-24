package clavela.msoe.connectfour;

import java.security.KeyStore;

public class Position {
    public static final int WIDTH = 7;
    public static final int HEIGHT = 6;

    private int[][] board;
    private int[] columnHeights;
    private int moves;

    public Position() {
        this.board = new int[WIDTH][HEIGHT];
        this.columnHeights = new int[WIDTH];
    }

    public Position(Position p){
        this.board = p.board.clone();
        this.columnHeights = p.columnHeights.clone();
    }

    public boolean canPlay(int col){
        return columnHeights[col] < HEIGHT;
    }

    public void play(int col){
        this.board[col][this.columnHeights[col]]= 1 + moves%2;
        this.columnHeights[col] += 1;
        moves++;
    }

    public int play(String moveString){
        for (int i = 0; i < moveString.length(); i++) {
            int col = moveString.charAt(i) - '1';
            if(col < 0 || col > Position.WIDTH || !canPlay(col) || isWinningMove(col)){
                return i;
            }
            play(col);
        }
        return moveString.length();
    }

    public boolean isWinningMove(int col){
        int currentPlayer = 1 + moves%2;
        if(columnHeights[col] >= 3
            && board[col][columnHeights[col] - 1] == currentPlayer
            && board[col][columnHeights[col] - 2] == currentPlayer
            && board[col][columnHeights[col] - 3] == currentPlayer){
            return true;
        }

        for(int dy = -1; dy <=1; dy++) {
            int nb = 0;
            for(int dx = -1; dx <=1; dx += 2) {
                int x = col+dx, y = columnHeights[col]+dx*dy;
                while (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT && board[x][y] == currentPlayer) {
                    x += dx;
                    y += dx*dy;
                    nb++;
                }
            }
            if(nb >= 3) return true;
        }

        return false;
    }

    public int nbMoves(){
        return moves;
    }
}
