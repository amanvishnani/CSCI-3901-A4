import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.*;

public class FillInPuzzle {

    Stack<State> stack = new Stack<>();

    public Character[][] data;

    public String[] words;

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
            words = new String[Integer.parseInt(inputs[2])];
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
                words[i] = word;
            }
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }
    public Boolean solve() {
        ArrayList<String> unvisitedWords = new ArrayList(Arrays.asList(words));
        while(!unvisitedWords.isEmpty()) {
            Slot slot = findBestSlot();
            String toProcess = poll(unvisitedWords, slot);
        }
        return true;
    }

    public String poll(ArrayList<String> list, Slot slot) {
        String str = null;
        for (String word : list) {
            if (word.length() != slot.wordLength) {
                continue;
            }

        }
        list.remove(str);
        return str;
    }

    public Slot findBestSlot() {
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

    private Integer getNumOfFilledChars(Slot slot, Integer wordLength) {
        Integer numOfFilledChars = 0;
        if (slot.direction.equals('h')) {
            for (int i = slot.column; i < slot.column + wordLength; i++) {
                if(!data[slot.row][i].equals('_')) {
                    numOfFilledChars++;
                }
            }
        } else {
            for (int i = slot.row; i < slot.row + wordLength; i++) {
                int row = i, column = slot.column;
                if(!data[row][column].equals('_')) {
                    numOfFilledChars++;
                }
            }
        }
        return numOfFilledChars;
    }

    public  void print( PrintWriter outPrintWriter ) {
        for (int i=data.length-1; i>=0; i--) { // Y-Axis
            outPrintWriter.print(i + " " + ((i < 10) ? " " : ""));
            for (int j=0; j<data[i].length; j++) { // X-Axis
                if(data[i][j] != null) {
                    outPrintWriter.print(" "+data[i][j]+" ");
                } else {
                    outPrintWriter.print("###");
                }
            }
            outPrintWriter.println();
            if(i==0) {
                outPrintWriter.print("   ");
                for (int j = 0; j < data[i].length; j++) {
                    outPrintWriter.print(" "+j+" ");
                }
                outPrintWriter.println();
            }
        }
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
            for (int i = slot.column; i < slot.column + slot.wordLength; i++) {
                Character c = data[slot.row][i];
                if (c.equals('_') || c.equals(str.charAt(i-slot.wordLength))) {
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
