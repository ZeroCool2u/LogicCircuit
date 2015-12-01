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
    // the following strings are used only for formatting textual output
    private static final String[] falseArray
            = {" |   ", "  _| ", " |-  ", " ,=' ", " |=  "};
    private static final String[] trueArray
            = {"   | ", " |_  ", "  -| ", " '=, ", "  =| "};

    // the following string defines the only name allowed for gate inputs
    private static List<String> inputs = Arrays.asList("in");

    // all output gates in the circuit are listed here
    private static List<Output> outputList = new LinkedList<Output>();

    // outputs count changes to their inputs since the last output event
    private int changeCount = 0; // this and output value determine display

    /**
     * Output gates display their inputs on the output display once
     * every simulated time unit and perform no useful computation.
     */
    private Output() {
    } // prevent outsiders from using the initializer

    private static void displayHeaders(float t) {
        /** Traverse the list of all outputs and display their
         *  names and then scheduling the first output display event.
         *  All names are output in a 5-space field.
         *  Following the headers, display the associated values.
         */
        for (Output o : outputList) {
            String n = o.name;

            // long names must be truncated
            if (n.length() > 4) {
                n = n.substring(0, 4);
            }

            // output leading blank
            System.out.append(' ');

            // output edited name
            System.out.append(n);

            // output padding up to next column
            if (n.length() < 4) {
                System.out.append(
                        "     ".substring(0, 4 - n.length())
                );
            }
        }
        System.out.println();

        // headers preceed first output of values
        displayOutput(t);
    }

    private static void displayOutput(float t) {
        /** Traverse the list of all outputs and display them
         *  before scheduling the next output display event.
         */
        for (Output o : outputList) {
            o.outputMe();
        }
        System.out.println();

        if (Simulator.moreEvents()) {
            Simulator.schedule(
                    t + 1,
                    (float time) -> displayOutput(time)
            );
        }
    }

    public static Gate scan(Scanner sc) {
        /** This is the public initializer for output gates,
         *  it reads the gate description using the given scanner
         *  and returns the handle for the newly initialized gate.
         */
        // just once, fire up the initial event
        if (outputList.isEmpty()) {
            Simulator.schedule(
                    1,
                    (float time) -> displayHeaders(time)
            );
        }

        // now do the scan as for any other gate
        Output g = new Output();
        g.scan(sc, inputs);
        if (g.name == null) g = null;

        // and finally, remember this in the output list
        if (g != null) outputList.add(g);
        return g;
    }

    private void outputMe() {
        /** Output the value of one wire.
         *  All outputs occupy exactly 5 spaces.
         */
        if (changeCount > 2) changeCount = 4 - (changeCount & 1);
        if (outputValue) {
            System.out.append(trueArray[changeCount]);
        } else {
            System.out.append(falseArray[changeCount]);
        }
        changeCount = 0;
    }

    public void setDriven(Wire w) {
        /** Override default method to prevent attaching anything
         *  to the nonexistant output of an output gate.
         */
        Errors.warn("Gate output " + name + " cannot drive anything");
    }

    public String toString() {
        /** Convert an output gate back to its textual description.
         */
        return "gate output " + name + ' ' + delay;
    }

    public void inputChange(float t, int i, boolean v) {
        /** Record the input changes to this gate so that the
         *  appropriate output can be generated.
         */
        changeCount = changeCount + 1;
        outputValue = v;
    }
}

/*
public class Output extends Gate {
    public static String header = "";
    static LinkedList<Output> outputs = new LinkedList<>();
    private static List<String> inputs = Arrays.asList("in");
    private static String[] trueArray = {" |   ", "  _| ", " |-  ", " ,=' ", " |=  "};
    private static String[] falseArray = {" |=  ", " ,=' ", " |-  ", "  _| ", " |   "};
    private int changes = 0;

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
            header += "  " + System.out.append(g.name) + "  ";
            return g;
        }
    }

    public void printOutput(int changes, boolean current) {
        if (!outputs.isEmpty()) {
            for (Output o : outputs) {
                System.out.append(o.name);
            }
            System.out.println(header);
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
        this.changes++;
        Simulator.schedule(
                t + delay,
                (float time) -> this.outputChange(time, outputValue)
        );
        outputValue = !v;

    }

    public void outputChange(float t, boolean v) {
        *//** Simulation event service routine called
         *  when the output of this gate changes at time t to value v.
         *  At this point, output changes are gate type independent.
 *//*
        Simulator.schedule(t + delay, (float time) -> this.printOutput(changes, v));
    }

    public String toString() {
        *//** Prints output gate information.
 *//*
        return "output " + name + ' ' + delay;
    }

}*/
