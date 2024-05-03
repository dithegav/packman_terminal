package ce326.hw2;

public class Vegetable extends BoardElement implements Eatable {
    private char symbol = 'v';
    private int energy = 6;

    @Override
    public char getSymbol() {
        return symbol;
    }

    @Override
    public int eaten() {
        return energy;
    }
}

