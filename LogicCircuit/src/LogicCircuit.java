// LogicCircuit.java
/**
 * Read a logic circuit description from the file given on the command line
 * and then write it out in order to demonstrate that it was read correctly.
 * @author Theo Linnemann 00773130
 * Based on code provided by Douglas Jones
 * @version MP2
 */

import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class Errors {
    /** Error reporting framework
     */
    static void fatal( String message ) {
        /** Report a fatal error with the given message
         */
        System.err.println( "Error: " + message );
        System.exit( 1 );
    }

    static void warn( String message ) {
        /** Report a nonfatal error with the given message
         */
        System.err.println( "Error: " + message );
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
    float  delay;	// delay of this wire
    Gate   driven;	// what gate does this wire drive
    int input;	// what input of driven does this wire drive Note: Originally this var type was String. 

    Gate   driver;	// what gate drives this wire


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
        //w.input = sc.next();
        String stringInput = sc.next();

        for(int i=0; i < w.driven.inputTrans.length; i++){
            if (w.driven.inputTrans[i].equals(stringInput)){
                w.input = i;
            }
            else if( w.driven.inputTrans[i] == null){
                i++;
            }
        }

        if (w.driver == null) {
            Errors.warn(
                    "wire '" + srcName + "' '" + dstName
                            + "' -- the first name is undefined"
            );
            returnValue = null;
        }

        if (w.driven == null) {
            Errors.warn(
                    "wire '" + srcName + "' '" + dstName
                            + "' -- the second name is undefined"
            );
            returnValue = null;
        } else if (w.driven.inputNumber(stringInput) < 0){
            Errors.warn(
                    "wire '" + srcName + "' '" + dstName + "' '"
                            + w.input
                            + "' -- the input name is not allowed"
            );
            returnValue = null;
        }

        if (!sc.hasNextFloat()) {
            Errors.warn(
                    "wire '" + srcName + "' '" + dstName + "'"
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
                + input + ' '
                + delay
                ;
    }
}

abstract class Gate {
    /** Gates are driven by and drive wires.
     *  @see Wire
     */
    String name;	// name of this gate

    LinkedList <Wire> driven = new LinkedList <Wire> ();
    LinkedList <Wire> driver = new LinkedList <Wire> ();

    final String[] inputTrans = {null, "in1", "in2"};
    int[] usedInputs = {0,0}; //0 is False, the input has not been used and 1 is True, the input has been used.

    String gateType;		// the name of the type of this gate
    LinkedList <String> inputList;	// the list of allowed input names
    // Bug: above is the wrong way to handle gate types (and, or, not ... ).
    float  delay;	// delay of this gate

    // initializer -- note:  No outside code uses the default initializer!
    public abstract Gate scan( Scanner sc, String kind, List <String> inputs );

    public String toString() {
        /** Convert an intersection back to its textual description
         */
        return "gate " + gateType + ' ' + name + ' ' + delay;
    }

    public String inputName(int i){
        return inputTrans[i];
    }

    public int inputNumber(String s){
        int indexOfInput = Arrays.asList(inputTrans).indexOf(s);

        if (indexOfInput >= 0){
            usedInputs[indexOfInput] = 1;
            return indexOfInput;
        }
        else return -1;

    }

    public boolean checkInput( String in ) {
        /** Check if in is a permitted input for this gate
         *  and also check off the fact that it has been used.
         *  Bug: this may not be the right way to do this.
         */
        return inputList.remove( in );
    }
}

class ANDGate extends Gate {

    private ANDGate() { }

    public static ANDGate init(Scanner sc, String kind, List<String> inputs) {
        ANDGate newANDGate = new ANDGate();
        newANDGate = newANDGate.scan(sc, kind, inputs);
        return newANDGate;
    }

    @Override
    public ANDGate scan( Scanner sc, String kind,
                         List <String> inputs ) {
        /** Initialize a gate scanning its description from sc.
         *  Returns null if the description contains errors.
         *  Bug: kind is the wrong way to pass the kind of gate.
         *  Bug: inputs should be a function of kind.
         */
        ANDGate g = new ANDGate();
        ANDGate returnValue = g;

        g.gateType = kind;
        g.inputList = new LinkedList <String> ( inputs );
        // Bug: above is the wrong way to handle gate types

        g.name = sc.next();
        if (LogicCircuit.findGate( g.name ) != null) {
            Errors.warn( "gate '" + g.name + "' redefined" );
            returnValue = null;
        }

        if (!sc.hasNextFloat()) {
            Errors.warn(
                    "gate " + kind + " '" + g.name
                            + "' -- delay not specified"
            );
            returnValue = null;
        } else {
            g.delay = sc.nextFloat();
        }

        SyntaxCheck.lineEnd(
                sc,
                "gate " + kind + " '" + g.name + "'"

        );

        return returnValue;
    } }

class ORGate extends Gate {

    public static ORGate init(Scanner sc, String kind, List<String> inputs) {
        ORGate newORGate = new ORGate();
        newORGate = newORGate.scan(sc, kind, inputs);
        return newORGate;
    }

    @Override
    public ORGate scan( Scanner sc, String kind,
                        List <String> inputs ) {
        /** Initialize a gate scanning its description from sc.
         *  Returns null if the description contains errors.
         *  Bug: kind is the wrong way to pass the kind of gate.
         *  Bug: inputs should be a function of kind.
         */
        ORGate g = new ORGate();
        ORGate returnValue = g;

        g.gateType = kind;
        g.inputList = new LinkedList <String> ( inputs );
        // Bug: above is the wrong way to handle gate types

        g.name = sc.next();
        if (LogicCircuit.findGate( g.name ) != null) {
            Errors.warn( "gate '" + g.name + "' redefined" );
            returnValue = null;
        }

        if (!sc.hasNextFloat()) {
            Errors.warn(
                    "gate " + kind + " '" + g.name
                            + "' -- delay not specified"
            );
            returnValue = null;
        } else {
            g.delay = sc.nextFloat();
        }

        SyntaxCheck.lineEnd(
                sc,
                "gate " + kind + " '" + g.name + "'"

        );

        return returnValue;
    }
}

class NOTGate extends Gate {

    public static NOTGate init(Scanner sc, String kind, List<String> inputs) {
        NOTGate newNOTGate = new NOTGate();
        newNOTGate = newNOTGate.scan(sc, kind, inputs);
        return newNOTGate;
    }

    @Override
    public NOTGate scan( Scanner sc, String kind,
                         List <String> inputs ) {
        /** Initialize a gate scanning its description from sc.
         *  Returns null if the description contains errors.
         *  Bug: kind is the wrong way to pass the kind of gate.
         *  Bug: inputs should be a function of kind.
         */
        NOTGate g = new NOTGate();
        NOTGate returnValue = g;

        g.gateType = kind;
        g.inputList = new LinkedList <String> ( inputs );
        // Bug: above is the wrong way to handle gate types

        g.name = sc.next();
        if (LogicCircuit.findGate( g.name ) != null) {
            Errors.warn( "gate '" + g.name + "' redefined" );
            returnValue = null;
        }

        if (!sc.hasNextFloat()) {
            Errors.warn(
                    "gate " + kind + " '" + g.name
                            + "' -- delay not specified"
            );
            returnValue = null;
        } else {
            g.delay = sc.nextFloat();
        }

        SyntaxCheck.lineEnd(
                sc,
                "gate " + kind + " '" + g.name + "'"

        );

        return returnValue;
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
                    g = ANDGate.init( sc, "and", in1in2 );
                } else if ("or".equals( kind )) {
                    g = ORGate.init( sc, "or", in1in2 );
                } else if ("not".equals( kind )) {
                    g = NOTGate.init( sc, "not", in );
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

    private static void writeNetwork() {
        /** Write out a textual description of the entire road network.
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
            writeNetwork();
        } catch (FileNotFoundException e) {
            Errors.fatal( "Can't open file '" + args[0] + "'" );
        }
    }
}
