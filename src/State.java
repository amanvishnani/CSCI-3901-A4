import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class State {
    public Character[][] data;

    public ArrayList<String> words;

    public Map<Integer, ArrayList<Slot>> slotMap;

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
}
