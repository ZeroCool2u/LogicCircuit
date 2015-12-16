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

class Output extends Gate {
    // Visual output values.
    private static final String[] trueArray
            = {"   | ", " |_  ", "  -| ", " '=, ", "  =| "};
    private static final String[] falseArray
            = {" |   ", "  _| ", " |-  ", " ,=' ", " |=  "};
    private static List<String> inputs = Arrays.asList("in");
    private static List<Output> outputList = new LinkedList<Output>();
    private int countChanges = 0;

    private Output() {
    } // prevent outsiders from using the initializer (Same as rest of gates.)

    private static void printOutput(float t) {
        for (Output g : outputList) {
            g.singleOutput();
        }
        System.out.println();

        if (Simulator.moreEvents()) {
            Simulator.schedule(t + 1, (float time) -> printOutput(time));
        }
    }

    public static Gate scan(Scanner sc) {
        /** Public initializer for output gate.
         */
        if (outputList.isEmpty()) {
            Simulator.schedule(1, (float time) -> printOutput(time));
        }

        Output g = new Output();

        g.scan(sc, inputs);

        if (g.name == null) g = null;
        if (g != null) outputList.add(g);
        return g;
    }

    private void singleOutput() {
        /** Output the values.
         */
        if (countChanges > 2) countChanges = 4 - (countChanges & 1);
        if (outputValue) {
            System.out.append(trueArray[countChanges]);
        } else {
            System.out.append(falseArray[countChanges]);
        }
        countChanges = 0;
    }

    public String toString() {
        /** Prints description.
         */
        return "gate output " + name + ' ' + delay;
    }

    public void inputChange(float t, int i, boolean v) {
        /** Counts input changes.
         */
        countChanges = countChanges + 1;
        outputValue = v;
    }

    public void setDriven(Wire w) {
        /** Override default method to prevent attaching anything
         *  to the nonexistant output of an output gate.
         */
        Errors.warn("Gate output " + name + " cannot drive anything");
    }
}
