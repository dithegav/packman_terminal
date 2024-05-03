package ce326.hw2;

import java.util.ArrayList;
import java.util.List;

public abstract class BoardElement {
    private List<BoardElement> elements;

    public BoardElement() {
        this.elements = new ArrayList<>();
    }

    public List<BoardElement> getElements() {   
        return elements;
    }

    public void addElement(BoardElement element) {
        elements.add(element);
    }

    public void removeElement(BoardElement element) {
        elements.remove(element);
    }

    public void clearElements() {
        elements.clear();
    }

    public char getSymbol() {
        if (elements.isEmpty()) {
            return '-';
        } else {
            // Return symbol of the first element in the list
            return elements.get(0).getSymbol();
        }
    }
}


