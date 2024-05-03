package ce326.hw2;

import java.util.ArrayList;
import java.util.List;

public class Ghost extends BoardElement implements Movable {
    private char symbol = '@';
    private int[] currentPosition;
    private boolean checked;

    public Ghost() {
        // this.currentPosition = initialPosition;
    }

    public int[] getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int[] currentPosition) {
        this.currentPosition = currentPosition;
    }

    public boolean getChecked() {
        return this.checked;
    }

    public void setChecked() {
        this.checked = !this.checked;
    }

    @Override
    public List<int[]> moveOptions(Board board) {
        List<int[]> options = new ArrayList<>();

        // Define possible movement directions for the ghost (up, down, left, right)
        int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

        for (int[] dir : directions) {
            int newRow = currentPosition[0] + dir[0];
            int newCol = currentPosition[1] + dir[1];

            // Wrap around the board if out of bounds
            newRow = (newRow + board.getNumRows()) % board.getNumRows();
            newCol = (newCol + board.getNumCols()) % board.getNumCols();

            // Check if the new position is not occupied by another ghost or obstacle
            if (isValidPosition(newRow, newCol, board)) {
                options.add(new int[] { newRow, newCol });
            }
        }

        return options;
    }

    private boolean isValidPosition(int row, int col, Board board) {
        return (!(board.getElement(row, col) instanceof Ghost) && !(board.getElement(row,col) instanceof Obstacle));
    }

    @Override
    public char getSymbol() {
        return symbol;
    }
}
