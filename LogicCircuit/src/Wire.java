// Wire.java
/**
 * Wires are used to link gates in a logic circuit simulation
 *
 * @author Theo Linnemann; Baseed on code provided by Professor Doug Jones
 * @version MP4
 * <p>
 * This code depends on:
 * @see Simulator
 * @see Errors
 * @see SyntaxCheck
 * @see Gate
 * @see LogicCircuit
 */

import java.util.Scanner;

class Wire {
    private float delay;    // delay of this wire
    private Gate driven;    // what gate does this wire drive
    private int input;    // what input of driven does this wire drive

    private Gate driver;    // what gate drives this wire

    // initializer -- note:  No outside code uses the default initializer!
    public static Wire scan(Scanner sc) {
        /** Initialize a wire by scanning its description from sc.
         *  Returns null if the description contains errors.
         */
        Wire w = new Wire();
        Wire returnValue = w;

        String srcName = sc.next();
        w.driver = LogicCircuit.findGate(srcName);
        String dstName = sc.next();
        w.driven = LogicCircuit.findGate(dstName);
        String inputName = sc.next();

        if (w.driver == null) {
            Errors.warn(
                    "Wire '" + srcName + "' '" + dstName
                            + "' -- the first name is undefined"
            );
            returnValue = null;
        } else {
            // inform gate that drives this wire that it does so
            w.driver.setDriven(w);
        }

        if (w.driven == null) {
            Errors.warn(
                    "Wire '" + srcName + "' '" + dstName
                            + "' -- the second name is undefined"
            );
            returnValue = null;
        } else {
            w.input = w.driven.inputNumber(inputName);
            if (w.input < 0) {
                Errors.warn(
                        "Wire '" + srcName + "' '" + dstName
                                + "' '" + inputName
                                + "' -- the input name is not allowed"
                );
                returnValue = null;
            }
        }

        if (!sc.hasNextFloat()) {
            Errors.warn(
                    "Wire '" + srcName + "' '" + dstName + "'"
                            + "-- delay not specified"
            );
            returnValue = null;
        } else {
            w.delay = sc.nextFloat();
        }

        SyntaxCheck.lineEnd(
                sc, () -> "wire '" + srcName + "' '" + dstName + "'"
        );
        return returnValue;
    }

    public String toString() {
        /** Convert a wire back to its textual description
         */
        return "wire "
                + driver.name + ' '
                + driven.name + ' '
                + driven.inputName(input) + ' '
                + delay
                ;
    }

    public void inputChange(float t, boolean v) {
        /** Simulation event service routine called
         *  when the input to a wire changes at time t to value v.
         */
        Simulator.schedule(
                t + delay,
                (float time) -> this.outputChange(time, v)
        );
    }

    public void outputChange(float t, boolean v) {
        /** Simulation event service routine called
         *  when the output of a wire changes at time t to value v.
         */
        driven.inputChange(t, input, v);
    }
}