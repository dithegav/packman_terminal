package ce326.hw2;

public class Fish extends BoardElement implements Eatable{
    private char symbol = 'f';
    private int energy = 10;

    @Override
    public char getSymbol() {
        return symbol;
    }

    @Override
    public int eaten() {
        return energy;
    }
}

