// SyntaxCheck.java
/**
 * Support for common syntax checking problems like skipping to the next line.
 *
 * @author Theo Linnemann; Baseed on code provided by Professor Doug Jones
 * @version MP4
 * <p>
 * This code depends on:
 * @see Errors
 */

import java.util.Scanner;

class SyntaxCheck {

    static void lineEnd(Scanner sc, ByName c) {
        /** Check for end of line on sc,
         *  Use c to provide context in any error message
         */
        String s = sc.nextLine();
        if (!s.isEmpty()) {
            Errors.warn(
                    c.s()
                            + " has non-empty line end '"
                            + s
                            + "'"
            );
        }
    }

    public interface ByName {
        // crutch to allow lambda evaluation of error message string
        String s();
    }
}