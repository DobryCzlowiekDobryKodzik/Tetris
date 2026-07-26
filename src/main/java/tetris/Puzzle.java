package tetris;

import java.awt.*;

class Puzzle{
    //public static int rotateState = 0;
    //kolory odpowiadają puzlom cyan to I, magenta to T i tak dalej
    public static final Color[] COLOR = {Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.ORANGE, Color.BLUE, Color.GREEN, Color.RED};
    public static int[][][] shape ={
        {   //I - 0
            {1},
            {1},
            {1},
            {1}
        },
        {   //T - 1
            {1,1,1},
            {0,1,0}
        },
        {   //O - 2
            {1,1},
            {1,1}
        },
        {   //L - 3
            {1,0},
            {1,0},
            {1,1}
        },
        {   //J - 4
            {0,1},
            {0,1},
            {1,1}
        },
        {   //S - 5
            {0,1,1},
            {1,1,0}
        },
        {   //Z - 6
            {1,1,0},
            {0,1,1}
        }
    };
}
