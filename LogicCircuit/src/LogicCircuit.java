// LogicCircuit.java
/**
 * Read a logic circuit description from the file given on the command line
 * and then write it out in order to demonstrate that it was read correctly.
 * @author Theo Linnemann 00773130
 * Based on code provided by Douglas Jones
 * @version MP3
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

class Errors {
    /** Error reporting framework
     */
    static void fatal( String message ) {
        /** Report a fatal error with the given message
         */
        System.err.println(message);
        System.exit( 1 );
    }

    static void warn( String message ) {
        /** Report a nonfatal error with the given message
         */
        System.err.println(message);
    }
}

class SyntaxCheck {
    /** Syntax checking support
     */
    static void lineEnd( Scanner sc, String c ) {
        /** Check for end of line on sc,
         *  Use c to provide context in any error message
         */
        String s = sc.nextLine();
        if (!s.isEmpty()) {
            Errors.warn(
                    c
                            + " has non-empty line end '"
                            + s
                            + "'"
            );
        }
    }
}

class Wire {
    /** Wires link gates.
     *  @see Gate
     */
    float delay;    // delay of this wire
    Gate driven;    // what gate does this wire drive
    int input;    // what input of driven does this wire drive

    Gate driver;    // what gate drives this wire

    // initializer -- note:  No outside code uses the default initializer!
    public static Wire scan( Scanner sc ) {
        /** Initialize a wire by scanning its description from sc.
         *  Returns null if the description contains errors.
         */
        Wire w = new Wire();
        Wire returnValue = w;

        String srcName = sc.next();
        w.driver = LogicCircuit.findGate( srcName );
        String dstName = sc.next();
        w.driven = LogicCircuit.findGate( dstName );
        String inputName = sc.next();

        if (w.driver == null) {
            Errors.warn(
                    "Wire '" + srcName + "' '" + dstName
                            + "' -- the first name is undefined"
            );
            returnValue = null;
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
                sc,
                "wire '" + srcName + "' '" + dstName + "'"
                // Bug: the above triggers wasteful computation
        );
        return returnValue;
    }

    public String toString() {
        /** Convert a wire back to its textual description
         */
        return  "wire "
                + driver.name + ' '
                + driven.name + ' '
                + driven.inputName(input) + ' '
                + delay
                ;
    }
}

abstract class Gate {
    /** Gates are driven by and drive wires.
     *  @see Wire
     */
    String name;    // name of this gate; null signals invalid gate

    LinkedList <Wire> driven = new LinkedList <Wire> ();
    LinkedList <Wire> driver = new LinkedList <Wire> ();
    // Bug: the above two lists are unused, but we think we'll need them.

    List<String> inputList;  // the list of allowed input names
    boolean[] inputUsed;      // for each input, is it in use?

    float  delay;	// delay of this gate

    // initializer -- note:  called only by implementing classes
    public void scan(Scanner sc, List<String> inputs) {
        /** Initialize a gate scanning its description from sc.
         *  Returns name = null if the description contains errors.
         */

        name = sc.next();
        String returnName = name;

        inputList = inputs;
        inputUsed = new boolean[inputList.size()];
        for (int i = 0; i < inputUsed.length; i++) {
            inputUsed[i] = false;
        }

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
                sc,
                "Gate '" + name + "'"
                // Bug: the above triggers wasteful computation
        );

        name = returnName;
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
}

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
}

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
        /** Convert an intersection back to its textual description
         */
        return "gate or " + name + ' ' + delay;
    }
}

class NotGate extends Gate {
    private static List<String> inputs = Arrays.asList("in");

    private NotGate() {
    } // prevent outsiders from using the initializer

    public static Gate scan(Scanner sc) {
        NotGate g = new NotGate();
        g.scan(sc, inputs);
        if (g.name == null) g = null;
        return g;
    }

    public String toString() {
        /** Convert an intersection back to its textual description
         */
        return "gate not " + name + ' ' + delay;
    }
}

class Simulator {

    static PriorityQueue<Event> eventSet
            = new PriorityQueue<Event>(
            (Event e1, Event e2) -> Float.compare(e1.time, e2.time)
    );

    static void schedule(float time, Action a) {
        eventSet.add(new Event(time, a));
    }

    static void run() {
        // run the simulation
        while (!eventSet.isEmpty()) {
            eventSet.remove().trigger();
        }
    }

    public interface Action {
        void trigger(float time);
    }

    private static class Event {
        public float time; // the time of this event
        public Action act; // what to do at that time

        Event(float t, Action a) {
            time = t;
            act = a;
        }

        void trigger() {
            act.trigger(time);
        }
    }
}

public class LogicCircuit {
    /** Top level description of a logic circuit made of
     *  some gates connected by some wires.
     *  @see Gate
     *  @see Wire
     */
    static LinkedList <Gate> gates
            = new LinkedList <Gate> ();
    static LinkedList <Wire> wires
            = new LinkedList <Wire> ();

    static Gate findGate( String s ) {
        /** Given s the name of a particular gate
         *  returns null if that gate does not exist,
         *  returns that gate if it exists.
         *  @see Intersection
         */
        // Bug:  Reengineering this to use a hash should be possible
        for (Gate i: gates) {
            if (i.name.equals( s )) {
                return i;
            }
        }
        return null;
    }

    private static void readCircuit( Scanner sc ) {
        /** Read a logic circuit, scanning its description from sc.
         */

        // the different input name sets for different gates
        List <String> in1in2 = Arrays.asList( "in1", "in2" );
        List <String> in = Arrays.asList( "in" );
        // Bug:  Above is the wrong way to do this

        while (sc.hasNext()) {
            // until the input file is finished
            String command = sc.next();
            if ("gate".equals( command )) {
                String kind = sc.next();
                Gate g = null;
                if ("and".equals( kind )) {
                    g = AndGate.scan(sc);
                } else if ("or".equals( kind )) {
                    g = OrGate.scan(sc);
                } else if ("not".equals( kind )) {
                    g = NotGate.scan(sc);
                } else {
                    Errors.warn(
                            "gate '"
                                    + kind
                                    + "' type not supported"
                    );
                    sc.nextLine();
                }
                if (g != null) gates.add( g );
            } else if ("wire".equals( command )) {
                Wire w = Wire.scan( sc );
                if (w != null) wires.add( w );
            } else {
                Errors.warn(
                        "'"
                                + command
                                + "' not a wire or gate"
                );
                sc.nextLine();
            }
        }
    }

    private static void checkCircuit() {
        /** Check the completeness of the logic circuit description.
         */
        for (Gate g : gates) {
            g.checkInputs();
        }
    }

    private static void writeCircuit() {
        /** Write out a textual description of the entire logic circuit.
         *  This routine is scaffolding used during development.
         */
        for (Gate g: gates) {
            System.out.println( g.toString() );
        }
        for (Wire w: wires) {
            System.out.println( w.toString() );
        }
    }

    public static void main(String[] args) {
        /** Create a logic circuit.
         *  The command line argument names the input file.
         *  For now, the output is just a reconstruction of the input.
         */
        if (args.length < 1) {
            Errors.fatal( "Missing filename argument" );
        }
        if (args.length > 1) {
            Errors.fatal( "Extra command-line arguments" );
        }
        try {
            readCircuit( new Scanner( new File( args[0] )));
            checkCircuit();
            writeCircuit();
        } catch (FileNotFoundException e) {
            Errors.fatal( "Can't open file '" + args[0] + "'" );
        }
    }
}