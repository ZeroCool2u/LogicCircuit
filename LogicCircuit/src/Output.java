//OutputGate.java
/**
 * Output extends Gate to provide simple visual output of the circuits behavior. *
 *
 * @author Theo Linnemann; based on code provided by Professor Doug Jones
 * @see Simulator
 * @see Gate
 */

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Output extends Gate {
    static LinkedList<Output> outputs = new LinkedList<>();
    private static List<String> inputs = Arrays.asList("in");
    private static String[] trueArray = {" |   ", "  _| ", " |-  ", " ,=' ", " |=  "};
    private static String[] falseArray = {" |=  ", " ,=' ", " |-  ", "  _| ", " |   "};
    private int changes;

    private Output() {
        //Prevents outsiders from using the initializer.
    }

    public static Gate scan(Scanner sc) {
        Output g = new Output();
        g.scan(sc, inputs);
        outputs.add(g);

        // tickle this gate so it triggers its initial event
        g.inputChange(0, 0, false);

        if (g.name == null) {
            g = null;
            return g;       //If this line is executed, g always returns as null. This is by design.
        } else {
            return g;
        }
    }

    public void printOutput(int changes, boolean current) {
        if (!outputs.isEmpty()) {
            for (Output o : outputs) {
                System.out.append(o.name);
            }
            System.out.println();
        }

        while (Simulator.moreEvents()) {
            for (Output o : outputs) {
                if (o.changes > 4) o.changes = 4 - (changes & 1);
                if (current) {
                    System.out.append(trueArray[changes]);
                } else {
                    System.out.append(falseArray[changes]);
                }
            }
            System.out.println();
        }
    }

    public void inputChange(float t, int i, boolean v) {
        inputValue[i] = v;
        outputValue = v;
        Simulator.schedule(
                t + delay,
                (float time) -> this.outputChange(time, outputValue)
        );
        outputValue = !v;

    }

    public void outputChange(float t, boolean v) {
        /** Simulation event service routine called
         *  when the output of this gate changes at time t to value v.
         *  At this point, output changes are gate type independent.
         */

        changes++;

        Simulator.schedule(t + delay, (float time) -> this.printOutput(changes, v));
    }

    public String toString() {
        /** Prints output gate information.
         */
        return "output " + name + ' ' + delay;
    }

}
