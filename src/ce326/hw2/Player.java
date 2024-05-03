package ce326.hw2;

import java.util.ArrayList;
import java.util.List;

public class Player extends BoardElement implements Movable {
    private int energy;
    private char symbol = 'X';
    private int[] currentPosition;

    public Player() {
        // this.energy = initialEnergy;
        // this.currentPosition = initialPosition;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public int[] getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int[] currentPosition) {
        this.currentPosition = currentPosition;
    }

    @Override
    public List<int[]> moveOptions(Board board) {
        List<int[]> options = new ArrayList<>();
        int row = currentPosition[0];
        int col = currentPosition[1];

        // Define possible moves (including wrap-around)
        int[][] directions = {
            {0, 1},   // Right
            {0, -1},  // Left
            {1, 0},   // Down
            {-1, 0},  // Up
            {1, 1},   // Diagonal down-right
            {1, -1},  // Diagonal down-left
            {-1, 1},  // Diagonal up-right
            {-1, -1}  // Diagonal up-left
        };

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            newRow = (newRow + board.getNumRows()) % board.getNumRows();
            newCol = (newCol + board.getNumCols()) % board.getNumCols();

            // Check if the new position is valid (not obstructed by an obstacle)
            if (isValidPosition(newRow, newCol, board)) {
                options.add(new int[]{newRow, newCol});
            }
        }

        return options;
    }

    private boolean isValidPosition(int row, int col, Board board) {
        return !(board.getElement(row, col) instanceof Obstacle);
    }

    @Override
    public char getSymbol() {
        return symbol;
    }
}
