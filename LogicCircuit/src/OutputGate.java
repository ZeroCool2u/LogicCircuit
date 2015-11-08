//OutputGate.java
/**
 * OutputGate extends Gate to provide simple visual output of the circuits behavior. *
 *
 * @author Theo Linnemann; based on code provided by Professor Doug Jones
 * @see Simulator
 * @see Gate
 */

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class OutputGate extends Gate {
    private static List<String> inputs = Arrays.asList("in");
    private String[] trueArray = {" |   ", "  _| ", " |-  ", " ,=' ", " |=  "};
    private String[] falseArray = {" |=  ", " ,=' ", " |-  ", "  _| ", " |   "};


    private OutputGate() {
        //Prevents outsiders from using the initializer.
    }

    public static Gate scan(Scanner sc) {
        OutputGate g = new OutputGate();
        g.scan(sc, inputs);

        // tickle this gate so it triggers its initial event
        g.inputChange(0, 0, false);

        if (g.name == null) g = null;
        return g;

    }

    public void printNextOutChar(int changes, boolean current) {
        if (changes > 4) changes = 4 - (changes & 1);
        if (current) {
            System.out.append(trueArray[changes]);
        } else {
            System.out.append(falseArray[changes]);
        }
    }

    public void inputChange(float t, int i, boolean v) {
        inputValue[i] = v;
        outputValue = !v;
        Simulator.schedule(
                t + delay,
                (float time) -> this.outputChange(time, outputValue)
        );
        outputValue = !v;

    }

    public String toString() {
        /** Generates visual output for the output gate.
         */
        return "gate not " + name + ' ' + delay;
    }

}
