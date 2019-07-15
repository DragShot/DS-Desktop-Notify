/*
 * DS Desktop Notify
 * A small utility to show small notifications in your Desktop anytime!
 */
package ds.desktop.notify.service;

/**
 * A {@code NotifyService} makes it possible to centralize control and display
 * notifications in a single process for the current host (either a physical or
 * virtual machine), in such a way several processes can make use of DS Desktop
 * Notify without colliding with each other, even those where the Java platform
 * is not necesarily present.<br>
 * <br>
 * Handling notifications this way has some limitations, as it is not possible
 * to set any action events or use custom themes and icons. This is because the
 * service is meant to dispatch notification requests coming from different VMs
 * or native processes, being input through command line and/or network, and all
 * the avaialble assets belong only to the process running the service.
 * @author DragShot
 * @since 0.9 (2019-06-18)
 */
public abstract class NotifyService {
    
    /** The port that will be used for instance control. */
    public static final int LISTENING_PORT = 45057;
    
    /** String for the stock dark theme. */
    public static final String DARK_THEME = "dark";
    /** String for the stock light theme. */
    public static final String LIGHT_THEME = "light";
    
    /**
     * Internal instace of the service, the object can be either the real thing
     * or just a remote stub.
     */
    private static NotifyService instance;
    
    /**
     * Returns the currently active notification service for this device. If
     * there isn't any service running, a new service will be started and
     * returned from here.<br>
     * <br>
     * Notification services work in such a way that only one service can be
     * running at a time per host, across several VMs. For this, a socket port
     * is internally reserved and used for communication.
     * @return The notification service.
     */
    public static NotifyService get() {
        if (instance == null) {
            instance = NotifyClient.tryAndGet();
            if (instance == null) {
                instance = new NotifyServer();
                instance.start();
            }
        }
        return instance;
    }
    
    /**
     * Called by the inner system, clears any existing instance data so the
     * service lookup can be done once again.
     */
    protected static void reset() {
        instance = null;
    }
    
    /**
     * Starts the service, in case it hasn't been started yet.
     */
    public abstract void start();
    
    /**
     * Posts a new notification to the service, setting the parameters needed
     * for it.
     * @param title     The title to display.
     * @param message   The message to display.
     * @param type      The notification type (0~7).
     * @param align     The text/layout alignment (0,1).
     * @param timeout   The maximum time in milliseconds the notification will
     *                  stay on screen.
     * @param themeName Either "light" or "dark", refering to the two stock
     *                  themes available.
     * @see #DARK_THEME
     * @see #LIGHT_THEME
     */
    public abstract void postNotification(String title, String message, Integer type, Integer align, Long timeout, String themeName);
    
    /**
     * Stops the service, in case it's running.
     */
    public abstract void stop();
}
