package tetris;

import java.util.*;
import java.awt.*;

class Board{
    int[][] grid = new int[App.ROWS][App.COLUMNS];
    Color[][] tile_color = new Color[App.ROWS][App.COLUMNS];
    public static int puzzleCount = 0;

    public static int[] puzzleQueue = new int[7];

    public record RotationResult(boolean success, int x, int y, int[][] new_shape,int current_puzzle) {
    }
    public void clear_grid(){
        for(int i = 0; i < App.ROWS; i++){
            for(int j = 0; j < App.COLUMNS; j++){
                grid[i][j]=0;
            }
        }
    }
    public String print_board(){
        String string = "";
        for(int i = 0; i < App.ROWS; i++){
            for( int j = 0; j < App.COLUMNS; j++){
                string+=String.valueOf(grid[i][j]);
            }
            string+="\n";
        }
        return string;
    }
    public int[][] rotateMatrix(int tab[][]){
        
        int rows = tab.length;
        int col = tab[0].length;

        int [][] ret_tab = new int[col][rows];

        for(int i = 0; i < rows; i++){
            for(int j = 0; j < col; j++){
                ret_tab[j][rows - i - 1] = tab[i][j];
            }
        }
        return ret_tab;
    }
    public void removeLine(){
        for(int i = 0; i < App.ROWS; i++){
            boolean remove = true;
            for(int j = 0; j < App.COLUMNS - 1; j++){
                if(grid[i][j] != grid[i][j + 1] || grid[i][j] == 0){
                    remove = false;
                }
            }
            if(remove){
                for(int j = 0; j < App.COLUMNS; j++){
                    grid[i][j] = 0;
                }
                for(int k = i; k > 0; k--){
                    for(int j = 0; j < App.COLUMNS; j++){
                        grid[k][j] = grid[k - 1][j];
                        tile_color[k][j] = tile_color[k - 1][j];
                    }
                }

            }
        }
    }
    public void clear_puzzle(int current_x, int current_y){
        final int x = current_x;
        int puzzle = puzzleQueue[puzzleCount];
        for(int i = 0; i < Puzzle.shape[puzzle].length; i++){
            current_x = x;
            for(int j = 0; j < Puzzle.shape[puzzle][i].length; j++){
                if(Puzzle.shape[puzzle][i][j] == 1){
                    grid[current_y][current_x] = 0;
                }
                current_x++;
            }
            current_y++;
        }
    }
    public int[][] clear_rot_puzzle(int current_x, int current_y, int[][] temp_grid){
        final int x = current_x;
        int puzzle = puzzleQueue[puzzleCount];
        for(int i = 0; i < Puzzle.shape[puzzle].length; i++){
            current_x = x;
            for(int j = 0; j < Puzzle.shape[puzzle][i].length; j++){
                if(Puzzle.shape[puzzle][i][j] == 1){
                    temp_grid[current_y][current_x] = 0;
                }
                current_x++;
            }
            current_y++;
        }
        return temp_grid;
    }
    public void spawn_puzzle(int current_x, int current_y){
        final int x = current_x;
        int puzzle = puzzleQueue[puzzleCount];
        for(int i = 0; i < Puzzle.shape[puzzle].length; i++){
            current_x = x;
            for(int j = 0; j < Puzzle.shape[puzzle][i].length; j++){
                if(Puzzle.shape[puzzle][i][j] == 1){
                        grid[current_y][current_x] = 1;
                        tile_color[current_y][current_x] = Puzzle.COLOR[puzzle];
                }
                current_x++;
            }
            current_y++;
        }
    }
    public boolean try_spawn_puzzle(int current_x, int current_y, int[][] temp_grid, int[][] new_tab){
        final int x = current_x;
        for(int i = 0; i < new_tab.length; i++){
            current_x = x;
            for(int j = 0; j < new_tab[i].length; j++){
                if(new_tab[i][j] == 1){
                        if(temp_grid[current_y][current_x] == 1)
                        {
                            return false;
                        }
                }
                current_x++;
            }
            current_y++;
        }
        return true;
    }
    public void resetMatrix(int tetermino,int count){
            switch(tetermino){
                case 0:
                case 5:
                case 6:
                if(count%2==1){
                    Puzzle.shape[tetermino] = rotateMatrix(Puzzle.shape[tetermino]);
                }
                break;
                case 1:
                case 3:
                case 4:
                for(int i = 0; i <  (4 - count%4);i++){
                    Puzzle.shape[tetermino] = rotateMatrix(Puzzle.shape[tetermino]);
                }
                break;
            }
    }
    public boolean canMoveLeft(int x, int y){
        int current_puzzle = puzzleQueue[puzzleCount];
        if(x == 0){
            return false;
        }
        for(int i = 0; i < Puzzle.shape[current_puzzle].length; i++){
            int first_tile = 0;
            while(true){
                if(Puzzle.shape[current_puzzle][i][first_tile] == 1){
                    break;
                }
                first_tile++;
            }
            if(grid[y + i][x + first_tile - 1] == 1){
                return false;
            }

        }
        return true;
    }
    public boolean canMoveRight(int x, int y){
        int current_puzzle = puzzleQueue[puzzleCount];
        if(x + Puzzle.shape[current_puzzle][0].length >= App.COLUMNS){
            return false;
        }
        for(int i = 0; i < Puzzle.shape[current_puzzle].length; i++){
            int first_tile = Puzzle.shape[current_puzzle][0].length - 1;
            while(true){
                if(Puzzle.shape[current_puzzle][i][first_tile] == 1){
                    break;
                }
                first_tile--;
            }
            if(grid[y + i][x + first_tile + 1] == 1){
                return false;
            }

        }
        return true;
    }
    public boolean is_landed(int x, int y){
        int current_puzzle = puzzleQueue[puzzleCount];
        for(int i = 0; i < Puzzle.shape[current_puzzle][0].length; i++){
            int last_tile = Puzzle.shape[current_puzzle].length - 1;
            while (true) {
                if(Puzzle.shape[current_puzzle][last_tile][i] == 1){
                    break;
                }
                last_tile --;
            }
            if(y + last_tile >= App.ROWS - 1){
                return true;
            }
            if(grid[y + last_tile + 1][x + i] == 1){
                return true;
            }
        }
        return false;

    }
    public RotationResult canRotate(Piece current){
        int x = current.x;
        int y = current.y;
        int current_puzzle = puzzleQueue[puzzleCount];
        int[][] temp_grid = new int[App.ROWS][App.COLUMNS];
        for(int i = 0; i < App.ROWS; i++){
            for(int j = 0; j < App.COLUMNS; j++){
                temp_grid[i][j] = grid[i][j];
            }
        }
        int[][] temp_tab = new int[Puzzle.shape[current_puzzle].length][Puzzle.shape[current_puzzle][0].length];
        temp_tab = Puzzle.shape[current_puzzle];
        switch (current_puzzle) {
            case 2:
                return new RotationResult(true, x, y, Puzzle.shape[current_puzzle],current_puzzle);
            case 3:
                if(current.rotateState % 4 == 0){
                        if(x - 1 < 0){
                            return new RotationResult(false, x, y,Puzzle.shape[current_puzzle],current_puzzle);
                        }
                        temp_grid = clear_rot_puzzle(x, y, temp_grid);
                        x-=1;
                        y+=1;
                        temp_tab = rotateMatrix(Puzzle.shape[current_puzzle]);
                        if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                            return new RotationResult(true, x, y,temp_tab,current_puzzle);
                        }
                        else{
                            return new RotationResult(false, x, y,Puzzle.shape[current_puzzle],current_puzzle);}

                }
                if(current.rotateState % 4 == 1){
                    if(y - 1 < 0){
                        return new RotationResult(false, x, y,Puzzle.shape[current_puzzle],current_puzzle);
                    }
                    temp_grid = clear_rot_puzzle(x, y, temp_grid);
                    y-=1;
                    temp_tab = rotateMatrix(Puzzle.shape[current_puzzle]);
                    if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                        return new RotationResult(true, x, y,temp_tab,current_puzzle);
                    }
                    else{
                        return new RotationResult(false, x, y,Puzzle.shape[current_puzzle],current_puzzle);}
                }
                if(current.rotateState % 4 == 2){
                    if(x + 2 >= App.COLUMNS){
                        return new RotationResult(false, x, y,Puzzle.shape[current_puzzle],current_puzzle);
                    }
                    temp_grid = clear_rot_puzzle(x, y, temp_grid);
                    temp_tab = rotateMatrix(Puzzle.shape[current_puzzle]);
                    if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                        return new RotationResult(true, x, y,temp_tab,current_puzzle);
                    }
                    else{
                        return new RotationResult(false, x, y, Puzzle.shape[current_puzzle],current_puzzle);}
                }
                if(current.rotateState % 4 == 3){
                    if(y + 2 >= App.ROWS){
                        return new RotationResult(false, x, y, Puzzle.shape[current_puzzle],current_puzzle);
                    }
                    temp_grid = clear_rot_puzzle(x, y, temp_grid);
                    x+=1;
                    temp_tab = rotateMatrix(Puzzle.shape[current_puzzle]);
                    if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                        return new RotationResult(true, x, y,temp_tab ,current_puzzle);
                    }
                    else{

                        return new RotationResult(false, x, y, Puzzle.shape[current_puzzle],current_puzzle);}
                }
            break;
            case 4:
                if(current.rotateState % 4 == 0){
                        if(x + 2 >= App.COLUMNS){
                            return new RotationResult(false, x, y, Puzzle.shape[current_puzzle],current_puzzle);
                        }
                        temp_grid = clear_rot_puzzle(x, y, temp_grid);
                        temp_tab = rotateMatrix(Puzzle.shape[current_puzzle]);
                        if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                            return new RotationResult(true, x, y,temp_tab,current_puzzle);
                        }
                        else{
                            return new RotationResult(false, x, y ,Puzzle.shape[current_puzzle],current_puzzle);}

                }
                if(current.rotateState % 4 == 1){
                    if(y + 2 >= App.ROWS){
                        return new RotationResult(false, x, y,Puzzle.shape[current_puzzle],current_puzzle);
                    }
                    temp_grid = clear_rot_puzzle(x, y, temp_grid);
                    x++;
                    temp_tab = rotateMatrix(Puzzle.shape[current_puzzle]);
                    if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                        return new RotationResult(true, x, y,temp_tab,current_puzzle);
                    }
                    else{
                        return new RotationResult(false, x, y,Puzzle.shape[current_puzzle],current_puzzle);}
                }
                if(current.rotateState % 4 == 2){
                    if(x - 1 < 0){
                        return new RotationResult(false, x, y,Puzzle.shape[current_puzzle],current_puzzle);
                    }
                    temp_grid = clear_rot_puzzle(x, y, temp_grid);
                    y+=1;
                    x-=1;
                    temp_tab = rotateMatrix(Puzzle.shape[current_puzzle]);
                    if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                        return new RotationResult(true, x, y,temp_tab,current_puzzle);
                    }
                    else{
                        return new RotationResult(false, x, y,Puzzle.shape[current_puzzle],current_puzzle);}
                }
                if(current.rotateState % 4 == 3){
                    if(y - 1 < 0){
                        return new RotationResult(false, x, y,Puzzle.shape[current_puzzle],current_puzzle);
                    }
                    temp_grid = clear_rot_puzzle(x, y, temp_grid);
                    y-=1;
                    temp_tab = rotateMatrix(Puzzle.shape[current_puzzle]);
                    if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                        return new RotationResult(true, x, y,temp_tab,current_puzzle);
                    }
                    else{
                        return new RotationResult(false, x, y,Puzzle.shape[current_puzzle],current_puzzle);}
                }
            break;
            case 6:
                if(current.rotateState % 2 == 0){
                    if(y - 1 < 0){
                        return new RotationResult(false, x, y,Puzzle.shape[current_puzzle],current_puzzle);
                    }
                    temp_grid = clear_rot_puzzle(x, y, temp_grid);
                    y-=1;
                    x+=1;
                    temp_tab = rotateMatrix(Puzzle.shape[current_puzzle]);
                    if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                        return new RotationResult(true, x, y,temp_tab,current_puzzle);
                    }
                    else{return new RotationResult(false, x, y,Puzzle.shape[current_puzzle],current_puzzle);}
                }
                    if(current.rotateState % 2 == 1){
                        if(x - 1 < 0){
                            return new RotationResult(false, x, y,Puzzle.shape[current_puzzle],current_puzzle);
                        }
                    temp_grid = clear_rot_puzzle(x, y, temp_grid);
                        y+=1;
                        x-=1;
                    temp_tab = rotateMatrix(Puzzle.shape[current_puzzle]);
                    if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                        return new RotationResult(true, x, y,temp_tab,current_puzzle);
                    }
                    else{return new RotationResult(false, x, y,Puzzle.shape[current_puzzle],current_puzzle);}
                }

                break;
                case 5:
                if(current.rotateState % 2 == 0){
                    if(y - 1 < 0){
                        return new RotationResult(false, x, y,Puzzle.shape[current_puzzle],current_puzzle);
                    }
                    temp_grid = clear_rot_puzzle(x, y, temp_grid);
                    y-=1;
                    x+=1;
                    temp_tab = rotateMatrix(Puzzle.shape[current_puzzle]);
                    if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                        return new RotationResult(true, x, y,temp_tab,current_puzzle);
                    }
                    else{return new RotationResult(false, x, y,Puzzle.shape[current_puzzle],current_puzzle);}
                }
                if(current.rotateState % 2 == 1){
                    if(x - 1 < 0){
                        return new RotationResult(false, x, y,Puzzle.shape[current_puzzle],current_puzzle);
                    }
                    temp_grid = clear_rot_puzzle(x, y, temp_grid);
                    y+=1;
                    x-=1;
                    temp_tab = rotateMatrix(Puzzle.shape[current_puzzle]);
                    if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                        return new RotationResult(true, x, y,temp_tab,current_puzzle);
                    }
                    else{return new RotationResult(false, x, y,Puzzle.shape[current_puzzle],current_puzzle);}
                }
                break;
                case 1:
                    if(current.rotateState % 4 == 0){
                        if(x + 2 >= App.COLUMNS){
                            return new RotationResult(false, x, y,Puzzle.shape[current_puzzle],current_puzzle);
                        }
                        temp_grid = clear_rot_puzzle(x, y, temp_grid);
                        y-=1;
                        temp_tab = rotateMatrix(Puzzle.shape[current_puzzle]);
                        if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                            return new RotationResult(true, x, y,temp_tab,current_puzzle);
                        }
                        else{return new RotationResult(false, x, y,Puzzle.shape[current_puzzle],current_puzzle);}

                    }
                    if(current.rotateState % 4 == 1){
                        if(x + 2 >= App.COLUMNS){
                            return new RotationResult(false, x, y,Puzzle.shape[current_puzzle],current_puzzle);
                        }
                        temp_grid = clear_rot_puzzle(x, y, temp_grid);
                        temp_tab = rotateMatrix(Puzzle.shape[current_puzzle]);
                        if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                            return new RotationResult(true, x, y,temp_tab,current_puzzle);
                        }
                        else{return new RotationResult(false, x, y,Puzzle.shape[current_puzzle],current_puzzle);}
                    }
                    if(current.rotateState % 4 == 2){
                        if(y + 2 >= App.ROWS){
                            return new RotationResult(false, x, y,Puzzle.shape[current_puzzle],current_puzzle);
                        }
                        temp_grid = clear_rot_puzzle(x, y, temp_grid);
                        x+=1;
                        temp_tab = rotateMatrix(Puzzle.shape[current_puzzle]);
                        if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                            return new RotationResult(true, x, y,temp_tab,current_puzzle);
                        }
                        else{return new RotationResult(false, x, y,Puzzle.shape[current_puzzle],current_puzzle);}
                    }
                    if(current.rotateState % 4 == 3){
                        if(x - 1 <= 0){
                            return new RotationResult(false, x, y,Puzzle.shape[current_puzzle],current_puzzle);
                        }
                        temp_grid = clear_rot_puzzle(x, y, temp_grid);
                        x-=1;
                        y+=1;
                        temp_tab = rotateMatrix(Puzzle.shape[current_puzzle]);
                        if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                            return new RotationResult(true, x, y,temp_tab,current_puzzle);
                        }
                        else{return new RotationResult(false, x, y,Puzzle.shape[current_puzzle],current_puzzle);}
                    }
                    break;
            case 0:
                if(current.rotateState % 2 == 0){
                    if(x - 2 < 0 || x + 1 >= App.COLUMNS){
                        return new RotationResult(false, x, y,Puzzle.shape[current_puzzle],current_puzzle);
                    }
                    y+=2;
                    x-=2;
                    temp_tab = rotateMatrix(Puzzle.shape[current_puzzle]);
                    if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                        return new RotationResult(true, x, y,temp_tab,current_puzzle);
                    }
                    else{return new RotationResult(false, x, y,Puzzle.shape[current_puzzle],current_puzzle);}
                }
                if(current.rotateState % 2 == 1){
                    if(y - 2 < 0 || y + 1 >= App.ROWS){
                        return new RotationResult(false, x, y,Puzzle.shape[current_puzzle],current_puzzle);
                    }
                    x+=2;
                    y-=2;
                    temp_tab = rotateMatrix(Puzzle.shape[current_puzzle]);
                    if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                        return new RotationResult(true, x, y,temp_tab,current_puzzle);
                    }
                    else{return new RotationResult(false, x, y,Puzzle.shape[current_puzzle],current_puzzle);}
                }
        break;

        }
        return new RotationResult(false, x, y,Puzzle.shape[current_puzzle],current_puzzle);
    }
    //Random tetromino selection
    public static int[] Random7bag(){
        Random rand = new Random();
        int[] array = new int[7];
        array[0] = rand.nextInt(7);
        for(int i = 1; i < 7; i++){
            boolean is_unique = false;
            int number;
            while(!is_unique){
                number = rand.nextInt(7);
                for(int j = 0; j < i; j++){
                    if(number == array[j]){
                        j = -1;
                        number = rand.nextInt(7);
                    }
                }
                is_unique = true;
                array[i] = number;
            }
        }
        return array;
    }

    Board(){
        puzzleQueue = Random7bag();
    }


    }
