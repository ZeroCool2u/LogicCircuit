//AndGate.java
/**
 * And gates are a subclass of gate in the logic circuit simulation.
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

class AndGate extends Gate {
    private static List<String> inputs = Arrays.asList("in1", "in2");

    private AndGate() {
    } // prevent outsiders from using the initializer

    public static Gate scan(Scanner sc) {
        AndGate g = new AndGate();
        g.scan(sc, inputs);
        if (g.name == null) g = null;
        return g;
    }

    public String toString() {
        /** Convert an intersection back to its textual description
         */
        return "gate and " + name + ' ' + delay;
    }

    public void inputChange(float t, int i, boolean v) {
        /** Simulation event service routine called
         *  when input i of this and gate changes at time t to value v.
         */

        boolean newValue = true;
        inputValue[i] = v;
        for (boolean vi : inputValue) if (!vi) newValue = false;

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