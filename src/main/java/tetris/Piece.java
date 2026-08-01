package tetris;

public class Piece {
    final int type;
    int x ,y;
    int rotateState;
    int cells [][];
    Piece(int x,int y, int rotateState ,int type){
        this.x=x;
        this.y=y;
        this.rotateState=rotateState;
        cells = copyShape(Puzzle.shape[type]);
        this.type=type;
    }
    private static int[][] copyShape(int[][] base) {
        int[][] copy = new int[base.length][];
        for (int i = 0; i < base.length; i++) {
            copy[i] = base[i].clone();
        }
        return copy;
    }
}
