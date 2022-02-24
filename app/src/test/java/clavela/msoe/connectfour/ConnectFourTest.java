package clavela.msoe.connectfour;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Spliterator;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ConnectFourTest {
    private Solver solver;

    private class TestCase{
        private String moveString;
        private int score;

        private TestCase(String line){
            String[] parts = line.split(" ");
            moveString = parts[0];
            score = Integer.parseInt(parts[1]);
        }
    }

    private TestCase[] loadFile(String filename){
        String[] lines = readLines(filename);
        TestCase[] testCases = new TestCase[lines.length];
        for (int i = 0; i < lines.length; i++) {
            testCases[i] = new TestCase(lines[i]);
        }
        return testCases;
    }

    private String[] readLines(String filename){
        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line = br.readLine();
            while(line != null){
                lines.add(line);
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines.toArray(new String[0]);
    }

    void init(){
        solver = new Solver();
    }

    @Test
    public void testEndEasy(){
        init();
        String filename = "src/test/java/clavela/msoe/connectfour/Test_L3_R1";
        TestCase[] testCases = loadFile(filename);
        long startTime = System.currentTimeMillis();
        long nodeCount = 0;
        int[] scores = new int[testCases.length];
        int[] test_scores = new int[testCases.length];
        for (int i = 0; i < test_scores.length; i++) {
            test_scores[i] = testCases[i].score;
        }
        for(int i = 0; i < testCases.length; i++){
            TestCase t = testCases[i];
            Position p = new Position();
            p.play(t.moveString);
            scores[i] = solver.Solve(p, false);
            nodeCount += solver.getNodeCount();
        }
        long endTime = System.currentTimeMillis();
        double msPer = (endTime - startTime)/(double)testCases.length;
        double msPerBoard = (endTime - startTime) / (double)nodeCount;
        System.out.println("Average ms per testcase: " + msPer);
        System.out.println("Average boards per testcase: " + (nodeCount/(double)testCases.length));
        System.out.println("Average boards per second: " + 1000/msPerBoard);
        assertEquals(test_scores, scores);
    }




}