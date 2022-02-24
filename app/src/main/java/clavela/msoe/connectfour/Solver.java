package clavela.msoe.connectfour;

public class Solver {
    private long nodeCount;

    public Solver() {
        nodeCount = 0;
    }

    public int negamax(Position p, int alpha, int beta){
        assert(alpha < beta);
        nodeCount++;

        if(p.nbMoves() == Position.WIDTH * Position.HEIGHT){
            return 0;
        }

        for (int x = 0; x < Position.WIDTH; x++) {
            if(p.canPlay(x) && p.isWinningMove(x)){
                return (Position.WIDTH * Position.HEIGHT + 1 - p.nbMoves())/2;
            }
        }

        int max = (Position.WIDTH * Position.HEIGHT-1 - p.nbMoves())/2;
        if (beta > max){
            beta = max;
            if(alpha >= beta){
                return beta;
            }
        }

        for (int x = 0; x < Position.WIDTH; x++) {
            if(p.canPlay(x)){
                Position p2 = new Position(p);
                p2.play(x);

                int score = -negamax(p2, -beta, -alpha);
                if(score >= beta){
                    return score;
                }
                if(score > alpha){
                    alpha = score;
                }
            }
        }
        return alpha;
    }

    public int Solve(Position p){
        return Solve(p, false);
    }

    public int Solve(Position p, boolean weak){
        nodeCount = 0;
        if(weak){
            return negamax(p, -1, 1);
        } else {
            return negamax(p, -Position.WIDTH*Position.HEIGHT/2, Position.WIDTH*Position.HEIGHT/2);
        }
    }

    public long getNodeCount(){
        return nodeCount;
    }
}
