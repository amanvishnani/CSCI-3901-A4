import java.util.Objects;

public class Slot {
    public Integer wordLength;
    public Character direction;
    public Integer row;
    public Integer column;

    public Slot(Integer row, Integer column, Character direction, Integer wordLength) {
        this.wordLength = wordLength;
        this.direction = direction;
        this.row = row;
        this.column = column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Slot)) return false;
        Slot slot = (Slot) o;
        return Objects.equals(wordLength, slot.wordLength) &&
                Objects.equals(direction, slot.direction) &&
                Objects.equals(row, slot.row) &&
                Objects.equals(column, slot.column);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wordLength, direction, row, column);
    }
}
