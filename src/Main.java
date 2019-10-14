import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Reader;

public class Main {

    public static void main(String[] args) {
        FillInPuzzle puzzle = new FillInPuzzle();
        try {
            FileReader reader = new FileReader("Input2.txt");
            System.out.println(puzzle.loadPuzzle(new BufferedReader(reader)));
            PrintWriter pw = new PrintWriter("Output.txt");
            puzzle.solve();
            puzzle.print(pw);
            pw.close();
            System.out.println(puzzle.choices());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
