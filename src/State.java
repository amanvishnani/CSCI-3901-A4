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
}
