//LogicCircuit.java
/**
 * Read a logic circuit description from the file given on the command line
 * and then write it out in order to demonstrate that it was read correctly.
 * @author Theo Linnemann 00773130
 * Based on code provided by Douglas Jones
 * @version MP4
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

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

        while (sc.hasNext()) {
            // until the input file is finished
            String command = sc.next();
            if ("gate".equals( command )) {
                String kind = sc.next();
                Gate g = null;
                if ("and".equals(kind)) {
                    g = AndGate.scan(sc);
                } else if ("or".equals(kind)) {
                    g = OrGate.scan(sc);
                } else if ("not".equals(kind)) {
                    g = NotGate.scan( sc );
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
            } else if ("//".equals(command)) {
                sc.nextLine();
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
            readCircuit(new Scanner(new File(args[0])));
            checkCircuit();
            writeCircuit();
            Simulator.run();
        } catch (FileNotFoundException e) {
            Errors.fatal( "Can't open file '" + args[0] + "'" );
        }
    }
}
