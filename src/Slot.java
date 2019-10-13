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

}
