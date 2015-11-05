//NotGate.java
/**
 * Not gates are a subclass of gate in the logic ciruit simulation.
 *
 * @author Theo Linnemann; Baseed on code provided by Professor Doug Jones
 * @version MP4
 * <p>
 * This code depends on:
 * @see Simulator
 * @see Gate
 */

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

class NotGate extends Gate {
    private static List<String> inputs = Arrays.asList("in");

    private NotGate() {
    } // prevent outsiders from using the initializer

    public static Gate scan(Scanner sc) {
        NotGate g = new NotGate();
        g.scan(sc, inputs);

        // tickle this gate so it triggers its initial event
        g.inputChange(0, 0, false);

        if (g.name == null) g = null;
        return g;
    }

    public String toString() {
        /** Convert an intersection back to its textual description
         */
        return "gate not " + name + ' ' + delay;
    }

    public void inputChange(float t, int i, boolean v) {
        /** Simulation event service routine called
         *  when input i of this not gate changes at time t to value v.
         */

        inputValue[i] = v;
        outputValue = !v;
        Simulator.schedule(
                t + delay,
                (float time) -> this.outputChange(time, outputValue)
        );
        outputValue = !v;
    }
}
