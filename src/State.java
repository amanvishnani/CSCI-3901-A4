import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class State {
    private Character[][] data;

    private ArrayList<String> words;

    private Map<Integer, ArrayList<Slot>> map;

    public State(FillInPuzzle state) {
        this.data = new Character[state.data.length][];
        for (int i = 0; i < data.length; i++) {
            data[i] = new Character[state.data[i].length];
            System.arraycopy(state.data[i], 0, data[i], 0, data[i].length);
        }
        words = new ArrayList<>();
        this.words.addAll(state.words);
        map = new HashMap<>();
        for (Map.Entry<Integer, ArrayList<Slot>> entry: state.map.entrySet()) {
            if(entry.getKey()==null) {
                continue;
            }
            if(!map.containsKey(entry.getKey())) {
                map.put(entry.getKey(), new ArrayList<>());
            }
            for (Slot s :
                    entry.getValue()) {
                map.get(entry.getKey()).add(s);
            }
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
            puzzle.map = new HashMap<>();
            for (Map.Entry<Integer, ArrayList<Slot>> entry: this.map.entrySet()) {
                if(entry.getKey()==null) {
                    continue;
                }
                if(!puzzle.map.containsKey(entry.getKey())) {
                    puzzle.map.put(entry.getKey(), new ArrayList<>());
                }
                for (Slot s :
                        entry.getValue()) {
                    puzzle.map.get(entry.getKey()).add(s);
                }
            }
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }
}
