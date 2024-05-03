package ce326.hw2;

public class Meat extends BoardElement implements Eatable {
    private char symbol = 'm';
    private int energy = 14;

    @Override
    public char getSymbol() {
        return symbol;
    }

    @Override
    public int eaten() {
        return energy;
    }
}

