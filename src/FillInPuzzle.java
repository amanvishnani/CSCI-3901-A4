import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author Aman Vishnani (aman.vishnani@dal.ca) [CSID: vishnani]
 * FillInPuzzle Class
 */
public class FillInPuzzle {

    /**
     * Maintains number of Backtracks
     */
    private int guessCount = 0;

    /**
     * Character grid/matrix represents the structure of puzzle
     */
    public Character[][] data;

    /**
     * Maintains a list of all the words.
     */
    public ArrayList<String> words;

    /**
     * Map representing word length -> list of slot with same length.
     */
    public Map<Integer, ArrayList<Slot>> slotMap;

    /**
     * Map representing word length -> list of words with same length.
     */
    public Map<Integer, ArrayList<String>> wordMap;

    /**
     * Reads input including grid size, number of words, all slots and all words from the input.
     * @param stream the input stream
     * @return true on success
     */
    public Boolean loadPuzzle(BufferedReader stream) {
        this.guessCount = 0;
        try {
            String line = stream.readLine();
            String[] inputs = line.split(" ");
            //return false if input does not have three spaces
            if(inputs.length != 3) {
                return false;
            }
            // return false if inputs are not integers
            if(!isInteger(inputs)) {
                return false;
            }
            // Initialize all variables - object might get reused.
            data = new Character[Integer.parseInt(inputs[0])][Integer.parseInt(inputs[1])];
            words = new ArrayList<>();
            slotMap = new HashMap<>();
            wordMap = new HashMap<>();

            // Read all slots.
            int numberOfWords = Integer.parseInt(inputs[2]);
            for (int i = 0; i < numberOfWords; i++) {
                line = stream.readLine().toLowerCase();
                inputs = line.split(" ");
                // return if it does not have 4 inputs: row, column, word length and direction
                if(inputs.length != 4) {
                    return false;
                }
                // return false if row, column, and word length are not integers.
                if(!isInteger(0, 3, inputs)) {
                    return false;
                }
                Integer column = Integer.parseInt(inputs[0]);
                Integer row = Integer.parseInt(inputs[1]);
                Integer wordLen = Integer.parseInt(inputs[2]);
                // Initialize slotList and wordLis if absent in Map
                ArrayList<Slot> slotList = slotMap.computeIfAbsent(wordLen, k -> new ArrayList<>());
                wordMap.computeIfAbsent(wordLen, k -> new ArrayList<>());
                // Create a slot object, track it and assign blank fields to all the characters in slot.
                if(inputs[3].equals("h")) {
                    slotList.add(new Slot(row, column, 'h', wordLen));
                    for (int j = column; j < wordLen + column; j++) {
                        data[row][j] = '_';
                    }
                } else if(inputs[3].equals("v")) {
                    slotList.add(new Slot(row, column, 'v', wordLen));
                    for (int j = row; j >= (row+1)-wordLen; j--) {
                        data[j][column] = '_';
                    }
                } else {
                    // if direction is neither if v or h return.
                    return false;
                }
            }
            // Read all words.
            for (int i=0; i<numberOfWords; i++) {
                String word = stream.readLine().toLowerCase();
                words.add(word);
                wordMap.get(word.length()).add(word);
            }
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }

    /**
     * Solves the problem recursively.
     * @return true on success.
     */
    public Boolean solve() {
        debugPrint();
        // Find the best slot to start with
        Slot slot = findBestSlot();
        // If slot not found or all words have been visited, problem is solved already.
        if(this.words.isEmpty() || slot==null){
            return true;
        }
        // Find all words of slot length that have not been visited yet.
        ArrayList<String> options = new ArrayList<>(wordMap.get(slot.wordLength));
        // return false if zero options were return
        if(options.isEmpty()) {
            return false;
        }
        // Clone the current state of puzzle
        State lastState = new State(this);
        for (String word : options) {
            if(canFit(slot, word)) {
                // If the word fits in the slot, write that word to the slot.
                fitWord(slot, word);
                // Mutate the state to stop tacking word.
                this.words.remove(word);
                this.wordMap.get(word.length()).remove(word);
                // Solve sub problem recursively.
                Boolean result = solve();
                // return true if sub problem was solved successfully.
                if(result) {
                    return true;
                } else {
                    // backtrack if it faild
                    backtrack(lastState);
                }
            }
        }
        return false; // No Words fit.
    }

    private void backtrack(State lastState) {
        this.restore(lastState);
        this.guessCount++;
        System.out.printf("************* BACKTRACK %d ****************\n", guessCount);
        debugPrint();
    }

    private void fitWord(Slot slot, String word) {
        if(slot.direction.equals('h')) {
            int k = -1;
            for (int i = slot.column; i < slot.column + slot.wordLength; i++) {
                k++;
                data[slot.row][i] = word.charAt(k);
            }
        } else {
            int j = -1;
            for (int i = slot.row; i >= slot.row+1 - slot.wordLength; i--) {
                j++;
                data[i][slot.column] = word.charAt(j);
            }
        }
        this.slotMap.get(slot.wordLength).remove(slot);
    }

    /**
     * Follows two strategies to return the best slot to work with.
     * @return best Slot
     */
    private Slot findBestSlot() {
        /* Strategy 1: Slot which has max chars filled. */
        int maxFilledChars = 0;
        Slot foundSlot = null;
        for (Map.Entry<Integer, ArrayList<Slot>> entry: this.slotMap.entrySet()) {
            ArrayList<Slot> list = entry.getValue();
            for (Slot slot: list) {
                int num = getNumOfFilledChars(slot);
                if(num == slot.wordLength) {
                    continue;
                }
                if(num > maxFilledChars) {
                    maxFilledChars = num;
                    foundSlot = slot;
                }
            }
        }
        if(foundSlot != null) {
            return foundSlot;
        }

        /* Strategy 2: Pick first slot having least options. */
        int minSlot = Integer.MAX_VALUE;
        int slotKey = -1;
        for (Map.Entry<Integer, ArrayList<Slot>> entry: this.slotMap.entrySet()) {
            ArrayList<Slot> list = entry.getValue();
            if (list.isEmpty()) {
                continue;
            }
            if(list.size() < minSlot) {
                minSlot = list.size();
                slotKey = entry.getKey();
            }
        }
        if (slotKey != -1) {
            return this.slotMap.get(slotKey).get(0);
        }
        return null;
    }

    /**
     * Calculates number of characters filled in the slots.
     * @param slot the slot
     * @return the number of fill chars.
     */
    private Integer getNumOfFilledChars(Slot slot) {
        Integer numOfFilledChars = 0;
        if (slot.direction.equals('h')) {
            for (int i = slot.column; i < slot.column + slot.wordLength; i++) {
                if(!data[slot.row][i].equals('_')) {
                    numOfFilledChars++;
                }
            }
        } else {
            for (int i = slot.row; i >= (slot.row+1)-slot.wordLength; i--) {
                int row = i, column = slot.column;
                if(!data[row][column].equals('_')) {
                    numOfFilledChars++;
                }
            }
        }
        return numOfFilledChars;
    }

    private void debugPrint() {
        System.out.println(getPrintableString());
        System.out.flush();
    }

    public void print( PrintWriter outPrintWriter ) {
        String printString = getPrintableString();
        outPrintWriter.print(printString);
    }

    private String getPrintableString() {
        StringBuilder builder = new StringBuilder();
        for (int i=data.length-1; i>=0; i--) { // Y-Axis
            builder.append(i).append(" |").append((i < 10) ? " " : "");
            for (int j=0; j<data[i].length; j++) { // X-Axis
                if(data[i][j] != null) {
                    builder.append(" ").append(data[i][j]).append(" ");
                } else {
                    builder.append("###");
                }
            }
            builder.append("\n");
            if(i==0) {
                builder.append("   ");
                for (int j = 0; j < data[i].length; j++) {
                    builder.append("---");
                }
                builder.append("\n");
                builder.append("    ");
                for (int j = 0; j < data[i].length; j++) {
                    builder.append(" ").append(j).append(" ");
                }
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    public int choices() {
        return this.guessCount;
    }

    public FillInPuzzle() {
        data = new Character[0][0];
    }

    private boolean isInteger(String ...string) {
        return isInteger(0, string.length, string);
    }

    private boolean isInteger(Integer start,  Integer end, String ...string) {
        try {
            for (int i = start; i < end; i++) {
                String str = string[i];
                Integer.parseInt(str);
            }
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }

    public Boolean canFit(Slot slot, String str) {
        if( slot.direction.equals('h')) {
            int k = -1;
            for (int i = slot.column; i < slot.column + slot.wordLength; i++) {
                k++;
                Character c = data[slot.row][i];
                if (c.equals('_') || c.equals(str.charAt(k))) {
                    continue;
                }
                return false;
            }
            return true;
        } else {
            int j = -1;
            for (int i = slot.row; i >= slot.row+1 - slot.wordLength; i--) {
                j++;
                Character c = data[i][slot.column];
                if (c.equals('_') || c.equals(str.charAt(j))) {
                    continue;
                }
                return false;
            }
            return true;
        }
    }

    public boolean restore(State state) {
        FillInPuzzle puzzle = this;
        try {
            puzzle.data = new Character[state.data.length][];
            for (int i = 0; i < state.data.length; i++) {
                puzzle.data[i] = new Character[state.data[i].length];
                System.arraycopy(state.data[i], 0, puzzle.data[i], 0, puzzle.data[i].length);
            }
            puzzle.words = new ArrayList<>();
            puzzle.words.addAll(state.words);
            puzzle.slotMap = new HashMap<>();
            puzzle.wordMap = new HashMap<>();
            for (Map.Entry<Integer, ArrayList<Slot>> entry: state.slotMap.entrySet()) {
                puzzle.slotMap.computeIfAbsent(entry.getKey(), k -> new ArrayList<>(entry.getValue()));
            }
            for (Map.Entry<Integer, ArrayList<String>> entry: state.wordMap.entrySet()) {
                puzzle.wordMap.computeIfAbsent(entry.getKey(), k -> new ArrayList<>(entry.getValue()));
            }
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }
}
