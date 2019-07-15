/*
 * DS Desktop Notify
 * A small utility to show small notifications in your Desktop anytime!
 */
package ds.desktop.notify;

import ds.desktop.notify.service.NotifyService;

/**
 * Static class that serves as launcher.
 * @author DragShot
 * @since 0.9
 */
public class Launcher {
    
    /**
     * Launch routine. Depending on the received arguments, it will start a
     * background service for notifications, display some information or post a
     * new notification for the background service to display.
     * @param args The set of arguments received from the command line.
     */
    public static void main(String[] args) {
//        args = new String[] {"-h", "start"};
//        args = new String[] {
//            "-t", "This is a notification",
//            "-m", "With DS Desktop Notify, displaying notifications on the screen is quick and easy!",
//            "-p", "7"
//        };
//        args = new String[] {"-h", "stop"};
        if (args.length == 0) {
            Demonstration.main(args);
            return;
        }
        if (assertArg(args, 0, "-v", "--version")) {
            System.out.println("DS Desktop Notify v0.9 Beta");
        } else if (assertArg(args, 0, "-?", "--help")) {
            System.out.println("Command line usage:");
            System.out.println("-> For running as a service: JVM <-h|--host> <start|stop>");
            System.out.println("-> For posting messages to a running service: JVM [(<OPTION> <VALUE>)...]");
            System.out.println("-> For displaying version: JVM <-v|--version>");
            System.out.println();
            System.out.println("Where:");
            System.out.println("-> JVM represents the prefered JVM launch parameters, like \"java -jar DS-Desktop-Notify.jar\".");
            System.out.println("-> OPTION can be one of the following:");
            System.out.println("   -t --title    The title to display");
            System.out.println("   -m --message  The message to display");
            System.out.println("   -p --type     The notification type [0~8], including a default icon");
            System.out.println("   -a --align    The layout orientation [0,1]");
            System.out.println("   -o --timeout  The notification timeout, in milliseconds");
            System.out.println("   -e --theme    The theme to display, either \"light\" or \"dark\"");
            System.out.println("-> VALUE is the value associated to each OPTION");
            System.out.println();
            System.out.println("Notes:");
            System.out.println("-> Usage modes are mutually exclusive. You either control the local service, post a notification or see the version.");
            System.out.println("-> If the jar is called as client but no service is currently running, it will run as a service itself.");
            System.out.println("-> Calls starting the local service will not give control of the execution back until the service is stopped.");
            System.out.println("-> Calls with no arguments will run the default demonstration.");
            System.out.println();
            System.out.println("Examples:");
            System.out.println("java -jar DS-Desktop-Notify.jar --host start");
            System.out.println("java -jar DS-Desktop-Notify.jar -t \"Title\" -m \"This is a notification\" -p 7 -o 5000");
            System.out.println("java -jar DS-Desktop-Notify.jar -m \"This is one paragraph.\\nAnd another one!\" -e light");
        } else if (assertArg(args, 0, "-h", "--host")) {
            String mode = getArg(args, 1);
            if ("start".equals(mode)) {
                NotifyService.get().start();
            } else if ("stop".equals(mode)) {
                NotifyService.get().stop();
            } else {
                System.err.println("Please specify if the host should \"start\" or \"stop\".");
                System.exit(1);
            }
        } else {
            try {
                String title = getValue(args, "-t", "--title"),
                       message = getValue(args, "-m", "--message"),
                       themeName = getValue(args, "-e", "--theme");
                Integer type = Integer.parseInt(nvl(getValue(args, "-p", "--type"), "0")),
                        align = Integer.parseInt(nvl(getValue(args, "-a", "--align"), "0"));
                Long timeout = Long.parseLong(nvl(getValue(args, "-o", "--timeout"), "0"));
                NotifyService.get().postNotification(title, message, type, align, timeout, themeName);
            } catch (NumberFormatException ex) {
                System.err.println("Incorrect argument type - must be numeric.");
                System.err.println(ex.getMessage());
                System.exit(1);
            }
        }
    }
    
    /**
     * Returns {@code value} if there's any, or {@code nullVal} if {@code value}
     * is {@code null}.
     */
    private static String nvl(String value, String nullVal) {
        return value == null ? nullVal : value;
    }
    
    /**
     * Gets the value from the arguments belonging to the first match in a given
     * set of parameters. This is done following the basic syntax {@code
     * <option> <value>}.
     * @param args The received command line args.
     * @param tags The parameters to look up.
     * @return The first value found for any of the parameters supplied in
     *         {@code tags}.
     */
    private static String getValue(String[] args, String... tags) {
        String value = null;
        seek: for (int i = 0; i < args.length; i++) {
            for (String tag : tags) {
                if (tag.equals(args[i]) && i < args.length - 1) {
                    value = args[i + 1].replace("\\r", "\r").replace("\\n", "\n");
                    break seek;
                }
            }
        }
        return value;
    }
    
    /**
     * Verifies if there's an argument at a given position and if it matches any
     * value in the supplied set.
     * @param args   The received command line args.
     * @param pos    The position to look at.
     * @param values The values to compare with.
     * @return {@code true} if there's such argument and it matches with any of
     *         the given values, {@code false} otherwise.
     */
    private static boolean assertArg(String[] args, int pos, String... values) {
        String arg = getArg(args, pos);
        boolean isValid = false;
        if (arg != null) {
            for (String value : values) {
                if (arg.equals(value)) {
                    isValid = true;
                    break;
                }
            }
        }
        return isValid;
    }
    
    /**
     * Retrieves the argument at the given position. If there isn't such
     * argument, {@code null} is returned instead.
     * @param args The received command line args.
     * @param pos  The position to look at.
     * @return The argument requested for, or {@code null} if it was not found.
     */
    private static String getArg(String[] args, int pos) {
        String value = null;
        if (args.length > pos) {
            value = args[pos];
        }
        return value;
    }
}
