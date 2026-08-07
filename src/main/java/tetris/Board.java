package tetris;

import java.util.*;
import java.awt.*;

class Board{
    int[][] grid = new int[App.ROWS][App.COLUMNS];
    Color[][] tile_color = new Color[App.ROWS][App.COLUMNS];
    int puzzleCount = 0;

    int[] puzzleQueue = new int[7];

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
                for(int k = i; k > 0; k--){
                    for(int j = 0; j < App.COLUMNS; j++){
                        grid[k][j] = grid[k - 1][j];
                        tile_color[k][j] = tile_color[k - 1][j];
                    }
                }
                for(int j = 0; j < App.COLUMNS; j++){
                    grid[0][j] = 0;
                    tile_color[0][j] = null;
                }
            }
        }
    }
    public void clear_puzzle(Piece current){
        for(int i = 0; i < current.cells.length; i++){
            for(int j = 0; j < current.cells[i].length; j++){
                if(current.cells[i][j] == 1){
                    grid[current.y + i][current.x + j] = 0;
                }
            }
        }
    }
    public int[][] clear_rot_puzzle(int current_x, int current_y, int cells[][], int[][] temp_grid){
        for(int i = 0; i < cells.length; i++){
            for(int j = 0; j < cells[i].length; j++){
                if(cells[i][j] == 1){
                    temp_grid[current_y + i][current_x + j] = 0;
                }
            }
        }
        return temp_grid;
    }
    public void spawn_puzzle(Piece current){
        for(int i = 0; i < current.cells.length; i++){

            for(int j = 0; j < current.cells[i].length; j++){
                if(current.cells[i][j] == 1){
                        grid[current.y + i][current.x + j] = 1;
                        //tile_color[current.y + i][current.x + j] = Puzzle.COLOR[puzzle];
                        tile_color[current.y + i][current.x + j] = Puzzle.COLOR[current.type];
                }

            }
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
    public boolean canMoveLeft(Piece current){
        if(current.x == 0){
            return false;
        }
        for(int i = 0; i < current.cells.length; i++){
            int first_tile = 0;
            while(true){
                if(current.cells[i][first_tile] == 1){
                    break;
                }
                first_tile++;
            }
            if(grid[current.y + i][current.x + first_tile - 1] == 1){
                return false;
            }

        }
        return true;
    }
    public boolean canMoveRight(Piece current){

        if(current.x + current.cells[0].length >= App.COLUMNS){
            return false;
        }
        for(int i = 0; i < current.cells.length; i++){
            int first_tile = current.cells[0].length - 1;
            while(true){
                if( current.cells[i][first_tile] == 1){
                    break;
                }
                first_tile--;
            }
            if(grid[current.y + i][current.x + first_tile + 1] == 1){
                return false;
            }

        }
        return true;
    }
    public boolean is_landed(Piece current){
        for(int i = 0; i < current.cells[0].length; i++){
            int last_tile = current.cells.length - 1;
            while (true) {
                if(current.cells[last_tile][i] == 1){
                    break;
                }
                last_tile --;
            }
            if(current.y + last_tile >= App.ROWS - 1){
                return true;
            }
            if(grid[current.y + last_tile + 1][current.x + i] == 1){
                return true;
            }
        }
        return false;

    }
    public RotationResult canRotate(Piece current){
        int x = current.x;
        int y = current.y;
        int[][] temp_grid = new int[App.ROWS][App.COLUMNS];
        for(int i = 0; i < App.ROWS; i++){
            for(int j = 0; j < App.COLUMNS; j++){
                temp_grid[i][j] = grid[i][j];
            }
        }
        int[][] temp_tab = new int[current.cells.length][current.cells[0].length];
        temp_tab = current.cells;
        switch (current.type) {
            case 2:
                return new RotationResult(true, x, y, current.cells,current.type);
            case 3:
                if(current.rotateState % 4 == 0){
                        if(x - 1 < 0){
                            return new RotationResult(false, x, y,current.cells,current.type);
                        }
                        temp_grid = clear_rot_puzzle(x, y,current.cells, temp_grid);
                        x-=1;
                        y+=1;
                        temp_tab = rotateMatrix(current.cells);
                        if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                            return new RotationResult(true, x, y,temp_tab,current.type);
                        }
                        else{
                            return new RotationResult(false, x, y,current.cells,current.type);}

                }
                if(current.rotateState % 4 == 1){
                    if(y - 1 < 0){
                        return new RotationResult(false, x, y,current.cells,current.type);
                    }
                    temp_grid = clear_rot_puzzle(x, y,current.cells, temp_grid);
                    y-=1;
                    temp_tab = rotateMatrix(current.cells);
                    if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                        return new RotationResult(true, x, y,temp_tab,current.type);
                    }
                    else{
                        return new RotationResult(false, x, y,current.cells,current.type);}
                }
                if(current.rotateState % 4 == 2){
                    if(x + 2 >= App.COLUMNS){
                        return new RotationResult(false, x, y,current.cells,current.type);
                    }
                    temp_grid = clear_rot_puzzle(x, y,current.cells, temp_grid);
                    temp_tab = rotateMatrix(current.cells);
                    if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                        return new RotationResult(true, x, y,temp_tab,current.type);
                    }
                    else{
                        return new RotationResult(false, x, y, current.cells,current.type);}
                }
                if(current.rotateState % 4 == 3){
                    if(y + 2 >= App.ROWS){
                        return new RotationResult(false, x, y, current.cells,current.type);
                    }
                    temp_grid = clear_rot_puzzle(x, y,current.cells, temp_grid);
                    x+=1;
                    temp_tab = rotateMatrix(current.cells);
                    if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                        return new RotationResult(true, x, y,temp_tab ,current.type);
                    }
                    else{

                        return new RotationResult(false, x, y, current.cells,current.type);}
                }
            break;
            case 4:
                if(current.rotateState % 4 == 0){
                        if(x + 2 >= App.COLUMNS){
                            return new RotationResult(false, x, y, current.cells,current.type);
                        }
                        temp_grid = clear_rot_puzzle(x, y,current.cells, temp_grid);
                        temp_tab = rotateMatrix(current.cells);
                        if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                            return new RotationResult(true, x, y,temp_tab,current.type);
                        }
                        else{
                            return new RotationResult(false, x, y ,current.cells,current.type);}

                }
                if(current.rotateState % 4 == 1){
                    if(y + 2 >= App.ROWS){
                        return new RotationResult(false, x, y,current.cells,current.type);
                    }
                    temp_grid = clear_rot_puzzle(x, y,current.cells, temp_grid);
                    x++;
                    temp_tab = rotateMatrix(current.cells);
                    if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                        return new RotationResult(true, x, y,temp_tab,current.type);
                    }
                    else{
                        return new RotationResult(false, x, y,current.cells,current.type);}
                }
                if(current.rotateState % 4 == 2){
                    if(x - 1 < 0){
                        return new RotationResult(false, x, y,current.cells,current.type);
                    }
                    temp_grid = clear_rot_puzzle(x, y,current.cells, temp_grid);
                    y+=1;
                    x-=1;
                    temp_tab = rotateMatrix(current.cells);
                    if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                        return new RotationResult(true, x, y,temp_tab,current.type);
                    }
                    else{
                        return new RotationResult(false, x, y,current.cells,current.type);}
                }
                if(current.rotateState % 4 == 3){
                    if(y - 1 < 0){
                        return new RotationResult(false, x, y,current.cells,current.type);
                    }
                    temp_grid = clear_rot_puzzle(x, y,current.cells, temp_grid);
                    y-=1;
                    temp_tab = rotateMatrix(current.cells);
                    if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                        return new RotationResult(true, x, y,temp_tab,current.type);
                    }
                    else{
                        return new RotationResult(false, x, y,current.cells,current.type);}
                }
            break;
            case 6:
                if(current.rotateState % 2 == 0){
                    if(y - 1 < 0){
                        return new RotationResult(false, x, y,current.cells,current.type);
                    }
                    temp_grid = clear_rot_puzzle(x, y,current.cells, temp_grid);
                    y-=1;
                    x+=1;
                    temp_tab = rotateMatrix(current.cells);
                    if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                        return new RotationResult(true, x, y,temp_tab,current.type);
                    }
                    else{return new RotationResult(false, x, y,current.cells,current.type);}
                }
                    if(current.rotateState % 2 == 1){
                        if(x - 1 < 0){
                            return new RotationResult(false, x, y,current.cells,current.type);
                        }
                    temp_grid = clear_rot_puzzle(x, y,current.cells, temp_grid);
                        y+=1;
                        x-=1;
                    temp_tab = rotateMatrix(current.cells);
                    if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                        return new RotationResult(true, x, y,temp_tab,current.type);
                    }
                    else{return new RotationResult(false, x, y,current.cells,current.type);}
                }

                break;
                case 5:
                if(current.rotateState % 2 == 0){
                    if(y - 1 < 0){
                        return new RotationResult(false, x, y,current.cells,current.type);
                    }
                    temp_grid = clear_rot_puzzle(x, y,current.cells, temp_grid);
                    y-=1;
                    x+=1;
                    temp_tab = rotateMatrix(current.cells);
                    if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                        return new RotationResult(true, x, y,temp_tab,current.type);
                    }
                    else{return new RotationResult(false, x, y,current.cells,current.type);}
                }
                if(current.rotateState % 2 == 1){
                    if(x - 1 < 0){
                        return new RotationResult(false, x, y,current.cells,current.type);
                    }
                    temp_grid = clear_rot_puzzle(x, y,current.cells, temp_grid);
                    y+=1;
                    x-=1;
                    temp_tab = rotateMatrix(current.cells);
                    if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                        return new RotationResult(true, x, y,temp_tab,current.type);
                    }
                    else{return new RotationResult(false, x, y,current.cells,current.type);}
                }
                break;
                case 1:
                    if(current.rotateState % 4 == 0){
                        if(x + 2 >= App.COLUMNS){
                            return new RotationResult(false, x, y,current.cells,current.type);
                        }
                        temp_grid = clear_rot_puzzle(x, y,current.cells, temp_grid);
                        y-=1;
                        temp_tab = rotateMatrix(current.cells);
                        if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                            return new RotationResult(true, x, y,temp_tab,current.type);
                        }
                        else{return new RotationResult(false, x, y,current.cells,current.type);}

                    }
                    if(current.rotateState % 4 == 1){
                        if(x + 2 >= App.COLUMNS){
                            return new RotationResult(false, x, y,current.cells,current.type);
                        }
                        temp_grid = clear_rot_puzzle(x, y,current.cells, temp_grid);
                        temp_tab = rotateMatrix(current.cells);
                        if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                            return new RotationResult(true, x, y,temp_tab,current.type);
                        }
                        else{return new RotationResult(false, x, y,current.cells,current.type);}
                    }
                    if(current.rotateState % 4 == 2){
                        if(y + 2 >= App.ROWS){
                            return new RotationResult(false, x, y,current.cells,current.type);
                        }
                        temp_grid = clear_rot_puzzle(x, y,current.cells, temp_grid);
                        x+=1;
                        temp_tab = rotateMatrix(current.cells);
                        if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                            return new RotationResult(true, x, y,temp_tab,current.type);
                        }
                        else{return new RotationResult(false, x, y,current.cells,current.type);}
                    }
                    if(current.rotateState % 4 == 3){
                        if(x - 1 <= 0){
                            return new RotationResult(false, x, y,current.cells,current.type);
                        }
                        temp_grid = clear_rot_puzzle(x, y,current.cells, temp_grid);
                        x-=1;
                        y+=1;
                        temp_tab = rotateMatrix(current.cells);
                        if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                            return new RotationResult(true, x, y,temp_tab,current.type);
                        }
                        else{return new RotationResult(false, x, y,current.cells,current.type);}
                    }
                    break;
            case 0:
                if(current.rotateState % 2 == 0){
                    if(x - 2 < 0 || x + 1 >= App.COLUMNS){
                        return new RotationResult(false, x, y,current.cells,current.type);
                    }
                    y+=2;
                    x-=2;
                    temp_tab = rotateMatrix(current.cells);
                    if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                        return new RotationResult(true, x, y,temp_tab,current.type);
                    }
                    else{return new RotationResult(false, x, y,current.cells,current.type);}
                }
                if(current.rotateState % 2 == 1){
                    if(y - 2 < 0 || y + 1 >= App.ROWS){
                        return new RotationResult(false, x, y,current.cells,current.type);
                    }
                    x+=2;
                    y-=2;
                    temp_tab = rotateMatrix(current.cells);
                    if(try_spawn_puzzle(x,y,temp_grid,temp_tab)){
                        return new RotationResult(true, x, y,temp_tab,current.type);
                    }
                    else{return new RotationResult(false, x, y,current.cells,current.type);}
                }
        break;

        }
        return new RotationResult(false, x, y,current.cells,current.type);
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
