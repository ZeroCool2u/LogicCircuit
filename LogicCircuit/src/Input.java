// Input.java
/**
 * Created by Theo Linnemann on 11/27/15 as part of LogicCircuit.
 */


import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

class Input extends Gate {
    // the following string defines the only name allowed for gate inputs
    private static List<String> inputs = Arrays.asList("in");

    // all input gates in the circuit are listed here
    private static List<Input> inputList = new LinkedList<Input>();

    public static Gate scan(Scanner sc) {

        //Scanning the gate description.
        Input g = new Input();
        g.scan(sc, inputs);
        if (g.name == null) g = null;

        if (Input.inputList != null) g.inputChange(0, 0, false);

        if (g != null) inputList.add(g);
        return g;
    }

    public String toString() {
        return "gate input " + name + ' ' + delay;
    }

    public void inputChange(float t, int i, boolean v) {
        inputValue[i] = v;
        outputValue = v;
        Simulator.schedule(
                t + delay,
                (float time) -> this.outputChange(time, outputValue)
        );
        outputValue = v;
    }
}
