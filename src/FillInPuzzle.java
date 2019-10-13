import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.*;

public class FillInPuzzle {

    Stack<State> stack = new Stack<>();

    public Character[][] data;

    public ArrayList<String> words;

    public Map<Integer, ArrayList<Slot>> map;

    public Boolean loadPuzzle(BufferedReader stream) {
        try {
            String line = stream.readLine();
            String[] inputs = line.split(" ");
            if(inputs.length != 3) {
                return false;
            }
            if(!isInteger(inputs)) {
                return false;
            }
            data = new Character[Integer.parseInt(inputs[0])][Integer.parseInt(inputs[1])];
            words = new ArrayList<>();
            map = new HashMap<>();

            int numberOfWords = Integer.parseInt(inputs[2]);
            for (int i = 0; i < numberOfWords; i++) {
                line = stream.readLine().toLowerCase();
                inputs = line.split(" ");
                if(inputs.length != 4) {
                    return false;
                }
                if(!isInteger(0, 3, inputs)) {
                    return false;
                }
                Integer x = Integer.parseInt(inputs[0]);
                Integer y = Integer.parseInt(inputs[1]);
                Integer n = Integer.parseInt(inputs[2]);
                ArrayList<Slot> list = map.get(n);
                if(list == null) {
                    list = new ArrayList<>();
                    map.put(n, list);
                }
                if(inputs[3].equals("h")) {
                    list.add(new Slot(x, y, 'h', n));
                    for (int j = y; j < n+y; j++) {
                        data[x][j] = '_';
                    }
                } else if(inputs[3].equals("v")) {
                    list.add(new Slot(x, y, 'v', n));
                    for (int j = x; j < n+x; j++) {
                        data[j][y] = '_';
                    }
                } else {
                    return false;
                }
            }
            for (int i=0; i<numberOfWords; i++) {
                String word = stream.readLine().toLowerCase();
                words.add(word);
            }
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }
    public Boolean solve() {
        debugPrint();
        ArrayList<String> unvisitedWords = new ArrayList<>(this.words);
        while(!unvisitedWords.isEmpty()) {
            Slot slot = findBestSlot();
            ArrayList<String> options = getRemainingWordsOf(slot.wordLength);
            if(options.isEmpty()) {
                return false;
            }
            for (String word : options) {
                if(canFit(slot, word)) {
                    saveState();
                    fitWord(slot, word);
                    unvisitedWords.remove(word);
                    this.words.remove(word);
                    Boolean result = solve();
                    if(result) {
                        return true;
                    } else {
                        // @TODO: Rollback
                        // continue;
                    }
                }
            }
            return false; // No Words fit.
        }
        return this.words.isEmpty();
    }

    private ArrayList<String> getRemainingWordsOf(int wordLength) {
        ArrayList<String> unvisitedWords = new ArrayList<>(this.words);
        ArrayList<String> wordsWithSlotLen = new ArrayList<>();
        for (String word :
                unvisitedWords) {
            if(word.length() == wordLength) {
                wordsWithSlotLen.add(word);
            }
        }
        return wordsWithSlotLen;
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
            for (int i = slot.row + slot.wordLength - 1; i >= slot.row; i--) {
                j++;
                data[i][slot.column] = word.charAt(j);
            }
        }
    }

    private void saveState() {
        stack.push(new State(this));
    }

    public Slot findBestSlot() {
        /* Strategy 1: Slot which has max chars filled. */
        int maxFilledChars = 0;
        Slot foundSlot = null;
        for (Map.Entry<Integer, ArrayList<Slot>> entry: this.map.entrySet()) {
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
        int minSlot = 9999;
        int slotKey = -1;
        for (Map.Entry<Integer, ArrayList<Slot>> entry: this.map.entrySet()) {
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
            return this.map.get(slotKey).get(0);
        }
        return null;
    }

    private Integer getNumOfFilledChars(Slot slot) {
        Integer numOfFilledChars = 0;
        if (slot.direction.equals('h')) {
            for (int i = slot.column; i < slot.column + slot.wordLength; i++) {
                if(!data[slot.row][i].equals('_')) {
                    numOfFilledChars++;
                }
            }
        } else {
            for (int i = slot.row; i < slot.row + slot.wordLength; i++) {
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
            builder.append(i).append(" ").append((i < 10) ? " " : "");
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
                    builder.append(" ").append(j).append(" ");
                }
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    public int choices() {
        return 0;
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
            for (int i = slot.row + slot.wordLength - 1; i >= slot.row; i--) {
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
}
