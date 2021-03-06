//SyntaxCheck.java
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
        if (!((s.length() > 1) && (s.charAt(0) == '/') && (s.charAt(1) == '/')) && !s.isEmpty()) {
            Errors.warn(
                    c.s()
                            + " has non-empty line end '"
                            + s
                            + "'"
            );
        }
    }

    public interface ByName {
        // Typical function call: SyntaxCheck.lineEnd( scanner, ()-> 'a' + 'b' + 'c') Some sort of string concat.
        String s();
    }
}