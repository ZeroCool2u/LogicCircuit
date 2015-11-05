//OrGate.java
/**
 * Or gates area subclass of gate in the logic circuit simulation.
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

class OrGate extends Gate {
    private static List<String> inputs = Arrays.asList("in1", "in2");

    private OrGate() {
    } // prevent outsiders from using the initializer

    public static Gate scan(Scanner sc) {
        OrGate g = new OrGate();
        g.scan(sc, inputs);
        if (g.name == null) g = null;
        return g;
    }

    public String toString() {
        /** Convert an or gate back to its textual description
         */
        return "gate or " + name + ' ' + delay;
    }

    public void inputChange(float t, int i, boolean v) {
        /** Simulation event service routine called
         *  when input i of this or gate changes at time t to value v.
         */

        boolean newValue = false;
        inputValue[i] = v;
        for (boolean vi : inputValue) if (vi) newValue = true;

        if (newValue != outputValue) {
            outputValue = newValue;
            Simulator.schedule(
                    t + delay,
                    (float time)
                            -> this.outputChange(time, outputValue)
            );
        }
    }
}