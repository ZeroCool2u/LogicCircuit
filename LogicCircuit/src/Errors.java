// Errors.java
/**
 * Tools for reporting errors, both fatal and non-fatal
 *
 * @author Theo Linnemann; Baseed on code provided by Professor Doug Jones
 * @version MP4
 */

class Errors {
    /** Error reporting framework
     */
    static void fatal(String message) {
        /** Report a fatal error with the given message
         */
        System.err.println(message);
        System.exit(1);
    }

    static void warn(String message) {
        /** Report a nonfatal error with the given message
         */
        System.err.println(message);
    }
}