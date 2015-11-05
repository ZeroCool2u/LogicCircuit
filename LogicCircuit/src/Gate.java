// Gate.java
/**
 * Gates are the active elemets linked by wires in a logic circuit simulation
 *
 * @author Theo Linnemann; Baseed on code provided by Professor Doug Jones
 * @version MP4
 * <p>
 * This code depends on:
 * @see Simulator
 * @see Errors
 * @see SyntaxCheck
 * @see Wire
 */

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

abstract class Gate {
    /** Gates are driven by and drive wires.
     *  @see Wire
     */
    String name;    // name of this gate; null signals invalid gate
    boolean[] inputValue;     // for each input, current value.
    // driven is (eventually) a list of all wires driven by this gate
    boolean outputValue;    // most recent computed output value.
    float delay;    // delay of this gate
    private LinkedList<Wire> driven = new LinkedList<Wire>();
    private List<String> inputList;  // the list of allowed input names
    private boolean[] inputUsed;      // for each input, is it in use?

    // initializer -- note:  called only by implementing classes
    public void scan(Scanner sc, List<String> inputs) {
        /** Initialize a gate scanning its description from sc.
         *  Returns name = null if the description contains errors.
         */

        name = sc.next();
        String returnName = name;

        inputList = inputs;
        inputUsed = new boolean[inputList.size()];
        inputValue = new boolean[inputList.size()];
        for (int i = 0; i < inputUsed.length; i++) {
            inputUsed[i] = false;
            inputValue[i] = false;
        }
        outputValue = false;

        if (LogicCircuit.findGate(name) != null) {
            Errors.warn("Gate '" + name + "' redefined");
            returnName = null;
        }

        if (!sc.hasNextFloat()) {
            Errors.warn(
                    "Gate '" + name
                            + "' -- delay not specified"
            );
            returnName = null;
        } else {
            delay = sc.nextFloat();
        }

        SyntaxCheck.lineEnd(
                sc, () -> "Gate '" + name + "'"
        );

        name = returnName;
    }

    public void setDriven(Wire w) {
        /** Inform this gate that it drives wire w
         */
        driven.add(w);
    }

    public int inputNumber(String in) {
        /** Given an input name, returns the corresponding number.
         *  Also checks that the name has not been used before;
         *  if it has been used or is an illegal name, returns -1.
         */
        int i = inputList.indexOf(in);
        if (i >= 0) {
            if (inputUsed[i]) {
                i = -1; // input already in use
            } else {
                inputUsed[i] = true;
            }
        }
        return i;
    }

    public String inputName(int in) {
        /** Given an input number, returns the corresponding name.
         *  and also check off the fact that it has been used.
         */
        return inputList.get(in);
    }

    public void checkInputs() {
        /** Check to see that all inputs of this gate are connected.
         */
        for (int i = 0; i < inputUsed.length; i++) {
            if (inputUsed[i] == false) {
                Errors.warn(
                        "Gate " + name + ' '
                                + inputList.get(i)
                                + " -- input not connected"
                );
            }
        }
    }

    public abstract String toString();

    /** Convert a gate back to its textual description
     */

    public abstract void inputChange(float t, int i, boolean v);

    /** Simulation event service routine called
     *  when input i of this gate changes at time t to value v.
     *  What to do at an input change depends on the gate type;
     *  it may do nothing, it may schedule an output change.
     */

    public void outputChange(float t, boolean v) {
        /** Simulation event service routine called
         *  when the output of this gate changes at time t to value v.
         *  At this point, output changes are gate type independent.
         */

        System.out.println(
                "Time " + t + " Gate " + name + " changes to " + v + '.'
        );

        for (Wire w : driven) w.inputChange(t, v);
    }
}