//Output.java

/**
 * Output extends Gate to provide simple visual output of the circuits behavior.
 *
 * @author Theo Linnemann; based on code provided by Professor Doug Jones
 */
public class Output extends Gate {

    public void inputChange(float t, int i, boolean v) {

    }

    public String toString() {
        /** Convert an intersection back to its textual description
         */
        return "gate and " + name + ' ' + delay;
    }

}
