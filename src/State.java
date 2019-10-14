import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class State {
    private Character[][] data;

    private ArrayList<String> words;

    private Map<Integer, ArrayList<Slot>> slotMap;

    public Map<Integer, ArrayList<String>> wordMap;

    public State(FillInPuzzle state) {
        this.data = new Character[state.data.length][];
        for (int i = 0; i < this.data.length; i++) {
            data[i] = new Character[state.data[i].length];
            System.arraycopy(state.data[i], 0, data[i], 0, data[i].length);
        }
        this.words = new ArrayList<>();
        this.words.addAll(state.words);
        this.slotMap = new HashMap<>();
        for (Map.Entry<Integer, ArrayList<Slot>> entry: state.slotMap.entrySet()) {
            this.slotMap.computeIfAbsent(entry.getKey(), k -> new ArrayList<>(entry.getValue()));
        }
        this.wordMap = new HashMap<>();
        for (Map.Entry<Integer, ArrayList<String>> entry :
                state.wordMap.entrySet()) {
            this.wordMap.computeIfAbsent(entry.getKey(), k -> new ArrayList<>(entry.getValue()));
        }
    }

    public boolean restore(FillInPuzzle puzzle) {
        try {
            puzzle.data = new Character[this.data.length][];
            for (int i = 0; i < this.data.length; i++) {
                puzzle.data[i] = new Character[this.data[i].length];
                System.arraycopy(this.data[i], 0, puzzle.data[i], 0, puzzle.data[i].length);
            }
            puzzle.words = new ArrayList<>();
            puzzle.words.addAll(this.words);
            puzzle.slotMap = new HashMap<>();
            puzzle.wordMap = new HashMap<>();
            for (Map.Entry<Integer, ArrayList<Slot>> entry: this.slotMap.entrySet()) {
                puzzle.slotMap.computeIfAbsent(entry.getKey(), k -> new ArrayList<>(entry.getValue()));
            }
            for (Map.Entry<Integer, ArrayList<String>> entry: this.wordMap.entrySet()) {
                puzzle.wordMap.computeIfAbsent(entry.getKey(), k -> new ArrayList<>(entry.getValue()));
            }
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }
}
