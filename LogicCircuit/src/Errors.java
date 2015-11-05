//Errors.java
/**
 * Tools for reporting errors, both fatal and non-fatal
 *
 * @author Theo Linnemann; Baseed on code provided by Professor Doug Jones
 * @version MP4
 */

class Errors {
    /** Error reporting framework
     */

    private static int errorCount = 0; //Number of erros encountered when reading input file.



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
        errorCount = errorCount + 1;
        if (errorCount > 20) System.exit(1);
    }

    static int getErrorCount() {
        return errorCount;
    }
}