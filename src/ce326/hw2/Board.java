package ce326.hw2;

import java.util.List;
import java.util.Queue;
import java.util.LinkedList;

public class Board {
    private int NumRows;
    private int NumCols;
    public int NumGhosts;
    private BoardElement[][] grid;

    public Board(int rows, int cols) {
        this.grid = new BoardElement[rows][cols];
    }

    public void setElement(int row, int col, BoardElement element) {
        if (isValidPosition(row, col)) {
            
            if (element != null){    
                element.addElement(element);
            }
            this.grid[row][col] = element;
        }
    }

    public BoardElement getElement(int row, int col) {
        if (isValidPosition(row, col)) {
            return this.grid[row][col];
        }
        return null;
    }

    public int getNumRows () {
        return NumRows;
    }

    public void setNumRows (int rows) {
        this.NumRows = rows;
    }

    public int getNumCols () {
        return NumCols;
    }

    public void setNumCols (int cols) {
        this.NumCols = cols;
    }

    public void printBoard() {
        
        System.out.print("  ");
        for (int col = 0; col < NumCols; col++) {
            if (NumCols > 9) {
                if (col > 9)
                    System.out.print( (col + 1) + " ");
                else 
                    System.out.print( " " + (col + 1) + " ");
            } else {
                System.out.print((col + 1) + " ");
            }
        }
        System.out.println();
    
       
        for (int row = 0; row < NumRows; row++) {
            // Convert row index to corresponding letter using ASCII value
            char rowLabel = (char) ('A' + row);
            System.out.print(rowLabel + " "); // Display row label (letter A-Z)
            
            for (int col = 0; col < NumCols; col++) {
                BoardElement element = grid[row][col];
                if (element == null) {
                    if (NumCols > 9) {
                        System.out.print(" - ");
                    } else {
                        System.out.print("- ");
                    }
                } else {
                    if (NumCols > 9) {
                        System.out.print(" " + element.getSymbol() + " ");
                    } else {
                        System.out.print(element.getSymbol() + " ");
                    }
                    
                }
            }
            System.out.println();
        }
    }
    
    
    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < grid.length && col >= 0 && col < grid[0].length;
    }

    private int[][] printDistanceGrid(int newRow, int newCol, boolean debugDijkstra) {
        int numRows = getNumRows();
        int numCols = getNumCols();

        int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
    
        int[][] distance = new int[numRows][numCols]; //actual board where the path will be shown
        Queue<int[]> queue = new LinkedList<>();
        boolean[][] visited = new boolean[numRows][numCols]; //check visited spaces
    
        // Initialize distance grid
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                distance[i][j] = Integer.MAX_VALUE;
            }
        }
        
        //init the player position 
        queue.offer(new int[]{newRow, newCol});
        distance[newRow][newCol] = 0;
        visited[newRow][newCol] = true;
    
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int currentRow = current[0];
            int currentCol = current[1];
    
            for (int[] dir : directions) {
                // Wrap 
                int nextRow = (currentRow + dir[0] + numRows) % numRows; 
                int nextCol = (currentCol + dir[1] + numCols) % numCols; 
                
                if (getElement(nextRow, nextCol) instanceof Obstacle) {
                    //skip the obstacles / 
                    // if a one place stays with its init max value then its not accessible to ghosts 
                    // a '?' will be added in that position
                    continue;
                }

                if (!visited[nextRow][nextCol]) {
                    visited[nextRow][nextCol] = true;
                    distance[nextRow][nextCol] = distance[currentRow][currentCol] + 1;
                    queue.offer(new int[]{nextRow, nextCol});
                }
            }
        }
    
        // Print the distance grid
        if (debugDijkstra){
            System.out.println("\nDijkstra Algorithm:");

            System.out.print("  ");
            for (int col = 0; col < NumCols; col++) {
                if (NumCols > 9) {
                    if (col > 9)
                        System.out.print( (col + 1) + " ");
                    else 
                        System.out.print( " " + (col + 1) + " ");
                } else {
                    System.out.print((col + 1) + " ");
                }
            }
            System.out.println();

            for (int i = 0; i < numRows; i++) {

                char rowLabel = (char) ('A' + i);
                System.out.print(rowLabel + " ");

                for (int j = 0; j < numCols; j++) {
                    if (numCols <= 9){
                        if (getElement(i, j) instanceof Obstacle) {
                            System.out.print("# ");
                        } else if (i == newRow && j == newCol) {
                            System.out.print("0 ");
                        } else if (distance[i][j] == Integer.MAX_VALUE) {
                            System.out.print("? ");
                        } else {
                            System.out.print(distance[i][j] + " ");
                        }
                    }
                    else {
                        if (getElement(i, j) instanceof Obstacle) {
                            System.out.print(" # ");
                        } else if (i == newRow && j == newCol) {
                            System.out.print(" 0 ");
                        } else if (distance[i][j] == Integer.MAX_VALUE) {
                            System.out.print(" ? ");
                        } else {
                            if (distance[i][j] > 9){
                                System.out.print(distance[i][j] + " ");
                            }
                            else {
                                System.out.print(" " + distance[i][j] + " ");
                            }
                        }
                    }
                }

                System.out.println();
            }

            System.out.println();
        }

        return distance;
    }

    public int HandlePlayerMove (String move, Boolean debugDijkstra, List<String> moveHistory) {
        int i=0 , j=0;
        boolean validMove = false;

        int row = move.charAt(0) - 'A';
        int col = Integer.parseInt(move.substring(1)) - 1;

        for (i=0; i < NumRows; i++){
            for (j=0; j< NumCols; j++){

                if (getElement(i,j) instanceof Player){
                    break;
                }
            }
            if (getElement(i,j) instanceof Player){
                break;
            }
        }

        Player player = ((Player)getElement(i, j));
        List<int[]> moveOptions = player.moveOptions(this);

        for (int[] list: moveOptions){

            if (list[0] == row && list[1] == col){

                validMove = true;
                break;
            }
        }

        if (!validMove){
            return -1;
        }
        
        if (getElement(row, col) == null || getElement(row, col) instanceof Empty){

            player.setEnergy(player.getEnergy()-1);
            // System.out.println ("\nThe energy of the player is: " + player.getEnergy());
            player.setCurrentPosition(new int[]{row, col});
            setElement(row, col, player);
            setElement(i, j, new Empty());
        }
        else if (getElement(row, col) instanceof Eatable) {

            int energy = player.getEnergy() - 1;
            //add energy to the player according to what food he stepped on
            if (getElement(row, col) instanceof Vegetable){

                energy += ((Vegetable)getElement(row, col)).eaten();
                player.setEnergy(energy);
            }
            else if (getElement(row, col)instanceof Fish){
                energy += ((Fish)getElement(row, col)).eaten();
                player.setEnergy(energy);
            }
            else {
                // the food is meat
                energy += ((Meat)getElement(row, col)).eaten();
                player.setEnergy(energy);
            }

            // System.out.println ("\nThe energy of the player is: " + player.getEnergy());
            player.setCurrentPosition(new int[]{row, col});
            setElement(row, col, player);
            setElement(i, j, new Empty());
        }
        else if (getElement(row, col) instanceof Ghost){
            //you stepped on a ghost before they moved
            //game is over
            player.setCurrentPosition(new int[]{row, col});
            getElement(row, col).getElements().add(player);
            setElement(i, j, new Empty());
            return -42;
        }

        char prevRow = (char) (i + 'A');
        int prevCol = j + 1;

        String prevMove = String.valueOf(prevRow) + prevCol;

        moveHistory.add(prevMove + "-" + move);

        int countWin = 0;
        for (i=0; i < NumRows; i++){
            for (j=0; j< NumCols; j++){

                if (getElement(i, j) instanceof Empty || getElement(i, j) instanceof Obstacle || getElement(i, j) instanceof Player){
                    countWin++;
                }
                else if (getElement(i, j) instanceof Ghost){

                    if (getElement(i, j).getElements().get(0) instanceof Empty){
                        countWin++;
                    }
                }
            }
        }

        if (countWin == (NumRows * NumCols)){
            //YOU WON if you eaten all of the '-'
            return 42;
        }
        System.out.println ("\nThe energy of the player is: " + player.getEnergy());

        int[][] distance;

        distance = printDistanceGrid(row, col, debugDijkstra);

        String Ghostsmove = "";
        //if all is good with the player move its time to handle the ghost moves with Dijkstra
        for (i=0; i < NumRows; i++){
            for (j=0; j< NumCols; j++){

                if (getElement(i,j) instanceof Ghost && !((Ghost)getElement(i,j)).getChecked()){
                    //find all the ghosts and move them using an extra board and dijkstra algorith

                    int dist_temp = distance[i][j];

                    moveOptions = ((Ghost)getElement(i,j)).moveOptions(this);

                    int[] temp = {i,j};
                    for (int[] option : moveOptions) {
                        
                        if (distance[option[0]][option[1]] < dist_temp){
                            temp = option;
                        }
                    }

                    //the move for the ghost has been chosen so add it to the list
                    prevRow = (char)(i + 'A');
                    prevCol = j + 1;

                    prevMove = String.valueOf(prevRow) + prevCol;

                    if (Ghostsmove.length() > 1){
                        Ghostsmove += ",";
                    }
                    
                    Ghostsmove += prevMove + "-" + String.valueOf((char)(temp[0] + 'A')) + (temp[1]+1);

                    if (!(getElement(temp[0], temp[1]) instanceof Ghost)){
                        //if i need to change the position of the ghost 
                        //move it accordingly
                        Ghost ghost = new Ghost();

                        if (getElement(temp[0], temp[1]) == null){

                            ghost.setCurrentPosition(new int[]{temp[0], temp[1]});
                            ghost.setChecked();
                            setElement(temp[0], temp[1], ghost);
                        }
                        else if (getElement(temp[0], temp[1]) instanceof Empty) {

                            ghost.setCurrentPosition(new int[]{temp[0], temp[1]});
                            ghost.setChecked();
                            ghost.addElement(new Empty());
                            setElement(temp[0], temp[1], ghost);
                        }
                        else if (getElement(temp[0], temp[1]) instanceof Eatable) {
                            //if their is food here make sure to write it down
                            ghost.setCurrentPosition(new int[]{temp[0], temp[1]});
                            ghost.setChecked();
                            
                            if (getElement(temp[0], temp[1]) instanceof Vegetable) {
                                ghost.addElement(new Vegetable());
                            }
                            else if (getElement(temp[0], temp[1]) instanceof Fish) {
                                ghost.addElement(new Fish());
                            }
                            else {
                                ghost.addElement(new Meat());
                            }

                            setElement(temp[0], temp[1], ghost);
                        }
                        else {
                            //you are hitting the player directly so game over
                            moveHistory.add(Ghostsmove);
                            return -42;
                        }

                        if (getElement(i, j).getElements().size() == 1){
                            setElement(i, j, null);
                        }
                        else {
                            if (getElement(i, j).getElements().get(0) instanceof Eatable){

                                if (getElement(i, j).getElements().get(0) instanceof Vegetable){
                                    setElement(i, j, new Vegetable());
                                }
                                else if (getElement(i, j).getElements().get(0) instanceof Fish){
                                    setElement(i, j, new Fish());
                                }
                                else {
                                    // the food is meat
                                    setElement(i, j, new Meat());
                                }
                            }
                            else if (getElement(i, j).getElements().get(0) instanceof Empty){
                                setElement(i, j, new Empty());
                            }
                        }
                    }
                    
                }
            }
        }

        moveHistory.add(Ghostsmove);

        for (i=0; i < NumRows; i++){
            for (j=0; j< NumCols; j++){

                if (getElement(i, j) instanceof Ghost){

                    ((Ghost)getElement(i, j)).setChecked();
                }
            }
        }

        if (player.getEnergy() == 0) {
            System.out.println("\n! YOU RAN OUT OF ENERGY ! :(");
            return -42;
        }

        return 0;
    }
}

