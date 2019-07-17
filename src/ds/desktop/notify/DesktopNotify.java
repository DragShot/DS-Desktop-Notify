/*
 * DS Desktop Notify
 * A small utility to show small notifications in your Desktop anytime!
 */
package ds.desktop.notify;

import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Main class of DS Desktop Notify. Use it to create and show notifications on
 * the Desktop.
 * @version 0.9
 * @author  DragShot
 */
public class DesktopNotify {
    //Some constants
    public static final int DEFAULT = 0;
    public static final int INFORMATION = 1;
    public static final int WARNING = 2;
    public static final int ERROR = 3;
    public static final int HELP = 4;
    public static final int TIP = 5;
    public static final int INPUT_REQUEST = 6;
    public static final int SUCCESS = 7;
    public static final int FAIL = 8;

    public static final int LEFT_TO_RIGHT = 0;
    public static final int RIGHT_TO_LEFT = 1;
    
    private static NotifyTheme defTheme = NotifyTheme.Dark;
    
    /**
     * Sets the theme to use by default when creating notifications.
     * @param theme The theme to use.
     */
    public static void setDefaultTheme(NotifyTheme theme) {
        DesktopNotify.defTheme = theme;
    }
    
    /**
     * Gets the theme currently in use when creating notifications.
     * @return The theme in use.
     */
    public static NotifyTheme getDefaultTheme() {
        return DesktopNotify.defTheme;
    }
    
    protected static int defTextOrientation = LEFT_TO_RIGHT;

    /**
     * Gets the current default text orientation.
     * @return The text orientation.
     * @see #RIGHT_TO_LEFT
     * @see #LEFT_TO_RIGHT
     */
    public static int getDefaultTextOrientation() {
        return defTextOrientation;
    }

    /**
     * Sets the default text orientation to use for new notifications. Useful
     * for displaying languages that aren't read left-to-right.
     * @param defTextOrientation The new text orientation.
     * @see #RIGHT_TO_LEFT
     * @see #LEFT_TO_RIGHT
     */
    public static void setDefaultTextOrientation(int defTextOrientation) {
        DesktopNotify.defTextOrientation = defTextOrientation;
    }
    
    /**
     * Creates and shows a new notification. If there isn't an instance of the
     * DesktopNotifyDriver thread running, it will be created and started to
     * show your notification window.
     * 
     * This notification does not bring any icon. Notifications created from
     * this method will stay on screen until the user clicks it. No actions can
     * be specified from this method.
     * 
     * @param title         The title, if any.
     * @param message       The message, if any.
     */
    public static void showDesktopMessage(String title, String message) {
        showDesktopMessage(title, message, DEFAULT, null, null, 0);
    }
    
    /**
     * Creates and shows a new notification. If there isn't an instance of the
     * DesktopNotifyDriver thread running, it will be created and started to
     * show your notification window.
     * 
     * A default icon will be used, depending of the type, unless it is DEFAULT.
     * A DEFAULT type notification does not bring an icon by default, no matter
     * how ironic this sounds. Notifications created from this method will stay
     * on screen until the user clicks it. No actions can be specified from this
     * method.
     * 
     * @param title         The title, if any.
     * @param message       The message, if any.
     * @param type          An int that denotes a valid message type. Invalid
     *                      values will be treated as DEFAULT.
     */
    public static void showDesktopMessage(String title, String message,
            int type) {
        showDesktopMessage(title, message, type, null, null, 0);
    }
    
    /**
     * Creates and shows a new notification. If there isn't an instance of the
     * DesktopNotifyDriver thread running, it will be created and started to
     * show your notification window.
     * 
     * A default icon will be used, depending of the type, unless it is DEFAULT.
     * A DEFAULT type notification does not bring an icon by default, no matter
     * how ironic this sounds. Notifications created from this method will stay
     * on screen until the user clicks it, firing the action specified (if any).
     * 
     * @param title         The title, if any.
     * @param message       The message, if any.
     * @param type          An int that denotes a valid message type. Invalid
     *                      values will be treated as DEFAULT.
     * @param action        An ActionListener with the action to execute when
     *                      the user clicks the notification.
     */
    public static void showDesktopMessage(String title, String message,
            int type, ActionListener action) {
        showDesktopMessage(title, message, type, null, action, 0);
    }
    
    /**
     * Creates and shows a new notification. If there isn't an instance of the
     * DesktopNotifyDriver thread running, it will be created and started to
     * show your notification window.
     * 
     * Notifications created from this method will stay on screen until the user
     * clicks it, firing the action specified (if any).
     * 
     * @param title         The title, if any.
     * @param message       The message, if any.
     * @param type          An int that denotes a valid message type. Invalid
     *                      values will be treated as DEFAULT.
     * @param icon          A custom Image to use instead of the default icon
     *                      assigned by type.
     * @param action        An ActionListener with the action to execute when
     *                      the user clicks the notification.
     */
    public static void showDesktopMessage(String title, String message,
            int type, Image icon, ActionListener action) {
        showDesktopMessage(title, message, type, icon, action, 0);
    }
    
    /**
     * Creates and shows a new notification. If there isn't an instance of the
     * DesktopNotifyDriver thread running, it will be created and started to
     * show your notification window.
     * 
     * A default icon will be used, depending of the type, unless it is DEFAULT.
     * A DEFAULT type notification does not bring an icon by default, no matter
     * how ironic this sounds. No actions can be specified from this method.
     * 
     * @param title         The title, if any.
     * @param message       The message, if any.
     * @param type          An int that denotes a valid message type. Invalid
     *                      values will be treated as DEFAULT.
     * @param maxTimeMillis The maximum time in milliseconds that this
     *                      notification will stay on the screen. If set to 0,
     *                      the user will have to close it manually by clicking
     *                      it.
     */
    public static void showDesktopMessage(String title, String message,
            int type, long maxTimeMillis) {
        showDesktopMessage(title, message, type, null, null, maxTimeMillis);
    }
    
    /**
     * Creates and shows a new notification. If there isn't an instance of the
     * DesktopNotifyDriver thread running, it will be created and started to
     * show your notification window.
     * 
     * A default icon will be used, depending of the type, unless it is DEFAULT.
     * A DEFAULT type notification does not bring an icon by default, no matter
     * how ironic this sounds.
     * 
     * @param title         The title, if any.
     * @param message       The message, if any.
     * @param type          An int that denotes a valid message type. Invalid
     *                      values will be treated as DEFAULT.
     * @param maxTimeMillis The maximum time in milliseconds that this
     *                      notification will stay on the screen. If set to 0,
     *                      the user will have to close it manually by clicking
     *                      it. Such value is recommended if you want to add an
     *                      action for it.
     * @param action        An ActionListener with the action to execute when
     *                      the user clicks the notification.
     */
    public static void showDesktopMessage(String title, String message,
            int type, long maxTimeMillis, ActionListener action) {
        showDesktopMessage(title, message, type, null, action, maxTimeMillis);
    }
    
    /**
     * Creates and shows a new notification. If there isn't an instance of the
     * DesktopNotifyDriver thread running, it will be created and started to
     * show your notification window.
     * 
     * Notifications created from this method will stay on screen until the user
     * clicks it. No actions can be specified from this method.
     * 
     * @param title         The title, if any.
     * @param message       The message, if any.
     * @param type          An int that denotes a valid message type. Invalid
     *                      values will be treated as DEFAULT.
     * @param icon          A custom Image to use instead of the default icon
     *                      assigned by type.
     */
    public static void showDesktopMessage(String title, String message,
            int type, Image icon) {
        showDesktopMessage(title, message, type, icon, null, 0);
    }
    
    /**
     * Creates and shows a new notification. If there isn't an instance of the
     * DesktopNotifyDriver thread running, it will be created and started to
     * show your notification window.
     * 
     * @param title         The title, if any.
     * @param message       The message, if any.
     * @param type          An int that denotes a valid message type. Invalid
     *                      values will be treated as DEFAULT.
     * @param icon          A custom Image to use instead of the default icon
     *                      assigned by type.
     * @param maxTimeMillis The maximum time in milliseconds that this
     *                      notification will stay on the screen. If set to 0,
     *                      the user will have to close it manually by clicking
     *                      it. Such value recommended if you want to add an
     *                      action for it.
     * @param action        An ActionListener with the action to execute if
     *                      the user clicks the notification.
     */
    public static void showDesktopMessage(String title, String message,
            int type, Image icon, ActionListener action, long maxTimeMillis) {
        DesktopNotify pane=new DesktopNotify(title, message, type, defTextOrientation, icon);
        pane.setTimeout(maxTimeMillis);
        pane.setAction(action);
        pane.show();
    }
    
    String title;
    String message;
    Image icon;
    int type;
    int orientation;
    
    /**
     * A <code>NotifyTheme</code> object with the color and Font parameters to
     * be used when this notification is being painted.
     */
    NotifyTheme theme;
    
    /**
     * A String array with the lines of the title.
     * It is refreshed automatically when the title is set.
     */
    String[] tlts = new String[0];
    /**
     * A String array with the lines of the message.
     * It is refreshed automatically when the message is set.
     */
    String[] msgs = new String[0];
    
    int w = 0;
    int h = 0;
    boolean visible = false;
    boolean markedForHide = false;
    
    int highl = 0;
    
    long popupStart = 0L;
    long timeOut = 8000L;
    
    /**
     * An action to perform when the notification is clicked.
     */
    ActionListener action;
    
    /**
     * A protected constructor for a DesktopNotify object, called internally.
     * You can use any of the <code>showDesktopMessage()</code> static methods
     * or a {@link NotificationBuilder} in order to create and display a
     * notification instead.
     * @param title       The notification title. Can be {@code null} when the
     *                    notification isn't meant to have one.
     * @param message     The notification message. Can be {@code null} when
     *                    the notification isn't meant to have one.
     * @param orientation The text orientation, either {@link #LEFT_TO_RIGHT} or
     *                    {@link #RIGHT_TO_LEFT}.
     * @param type        The notification type. Check the type contans in the
     *                    class for the ones you can use.
     * @param icon        The icon to display. Can be {@code null} when the
     *                    notification isn't meant to have one.
     */
    protected DesktopNotify(String title, String message, int type,
            int orientation, Image icon){
        this.title = (title==null? "":title);
        this.message = (message==null? "":message);
        this.type = type;
        this.orientation = orientation;
        this.icon = icon;
        this.theme = DesktopNotify.defTheme;
    }

    /**
     * @return The <code>NotifyTheme</code> in use for this notification.
     */
    public NotifyTheme getTheme() {
        return theme;
    }

    /**
     * Allows to set a <code>NotifyTheme</code> for this notification. One of
     * these defines stuff as the colors for text and background, the fonts and
     * even images for the default icons.
     * @param theme The <code>NotifyTheme</code> to use for this notification.
     */
    public void setTheme(NotifyTheme theme) {
        if (theme != null) this.theme = theme;
    }

    /**
     * @return the ActionListener binded to this notification.
     */
    public ActionListener getAction() {
        return action;
    }

    /**
     * Allows to bind an action to this notification. It will be executed when
     * the notification gets clicked. Notifications with an action set will wait
     * for the user to click them.
     * @param action he ActionListener to bind to this notification.
     */
    public void setAction(ActionListener action) {
        this.action = action;
    }
    
    /**
     * Allows to set a timeout for this notification, in milliseconds.
     * After the time is 'out', and if there isn't an action set, the
     * notification will just fade away.
     * @param millis the timeout.
     */
    public void setTimeout(long millis){
        timeOut = millis < 0 ? 0 : millis;
    }
    
    protected long expTime(){
        return timeOut == 0 ? Long.MAX_VALUE : popupStart + timeOut;
    }
    
    /**
     * Polls the Driver in order to show this notification, so you don't have to
     * do it yourself.
     */
    public void show(){
        markedForHide = false;
        DesktopNotifyDriver.postPane(this);
    }
    
    /**
     * Hides this notification immediately if it is already being displayed.
     * The action itself is handled by the Driver, this method only marks this
     * notification for hiding.
     */
    public void hide() {
        markedForHide = true;
    }
    
    protected void setWidth(int w){
        this.w = w;
    }

    /**
     * @return <code>true</code> if this notification is currently being shown.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Used by he driver to mark this notification as visible or not.
     * @param visible {@code true} if the notification is currently visible.
     */
    protected void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    /**
     * The painting routine for the notification goes here.
     * @param x     Where to start painting (X)
     * @param y     Where to start painting (Y)
     * @param hover A boolean that is <code>true</code> if the user is hovering
     *              this notification with the mouse pointer.
     * @param rd    The <code>Graphics2D</code> object to use for painting.
     * @param l     The current time.
     */
    public void render(int x, int y, boolean hover, Graphics2D rd, long l) {
        long i = l - popupStart;
        if (i > 500) i = expTime()-l;
        if (i < 0) i = 0;
        if (i > 500) i = -1;
        rd.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        AffineTransform trans=rd.getTransform();
        rd.translate(x, y);
        if (i != -1) {
            double d = i/500.0;
            rd.translate(w/2-((w/2)*d), h/2-((h/2)*d));
            rd.scale(d, d);
            rd.setComposite(AlphaComposite
                    .getInstance(AlphaComposite.SRC_OVER,(float)d));
        }
        rd.setPaint(new GradientPaint(0, (title.isEmpty()? 0:25),
                /*hover? new Color(50,57,65):*/theme.bgGrad[0],
                0, h, theme.bgGrad[highl+1]/*new Color(theme.bgGrad[0].getRed()+(int)((theme.bgGrad[1].getRed()-theme.bgGrad[0].getRed())*highl/20.0F),
                                theme.bgGrad[0].getGreen()+(int)((theme.bgGrad[1].getGreen()-theme.bgGrad[0].getGreen())*highl/20.0F),
                                theme.bgGrad[0].getBlue()+(int)((theme.bgGrad[1].getBlue()-theme.bgGrad[0].getBlue())*highl/20.0F),
                120+(int)(135*highl/20.0F))*//*hover? new Color(0x3B4B5B):new Color(50,50,50,120)*/, false));
        if (hover && highl<20) highl++;
        if (!hover && highl>0) highl--;
        rd.fillRect(0, 0, w, h);
        rd.setPaint(null);
        rd.setColor(theme.borderColor);
        rd.drawRect(0, 0, w-1, h-1);
        if (i==-1) {
            int titleH = getLineHeight(theme.titleFont);
            int textH = getLineHeight(theme.contentFont);
            if (!title.isEmpty()) {
                rd.setColor(theme.titleColor);
                rd.setFont(theme.titleFont);
                int tX = 5 + ((icon==null && type==0)? 0:38);
                for (int j = 0; j < tlts.length; j++) {
                    if (orientation == RIGHT_TO_LEFT) {
                        FontMetrics ftm = DesktopNotifyDriver.getFontMetrics(theme.titleFont);
                        tX = w - 4 - ((icon==null && type==0)? 0:38) - ftm.stringWidth(tlts[j]);
                    }
                    rd.drawString(tlts[j], tX, 20+(titleH*j));
                }
            }
            if (!message.isEmpty()) {
                rd.setColor(theme.contentColor);
                rd.setFont(theme.contentFont);
                int tX = 6 + ((icon==null && type==0)? 0:38);
                for (int j = 0; j < msgs.length; j++) {
                    if (orientation == RIGHT_TO_LEFT) {
                        FontMetrics ftm = DesktopNotifyDriver.getFontMetrics(theme.contentFont);
                        tX = w - 5 - ((icon==null && type==0)? 0:38) - ftm.stringWidth(msgs[j]);
                    }
                    rd.drawString(msgs[j], tX, 20+(titleH*tlts.length)+(textH*j));
                }
            }
        }
//        if(expTime()==Long.MAX_VALUE){
//            rd.setFont(theme.titleFont);
//            rd.setColor(new Color(120,120,120));
//            rd.drawString("X", w-16, 18);
//        }
        Image icon = this.icon == null ?
                (type == 0 ? null : theme.iconSet[type-1]) : this.icon;
        if (icon != null) {
            rd.drawImage(icon, orientation == RIGHT_TO_LEFT ? (w - 7 - 32) : 6, (h/2)-15, 32, 32, null);
        }
        rd.setTransform(trans);
        rd.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
    }
    
    private int getLineHeight(Font font) {
        FontMetrics ftm = DesktopNotifyDriver.getFontMetrics(font);
        return ftm.getHeight() - ftm.getLeading();
    }
    
    /**
     * Splits the words of the text across lines that fit inside the
     * notification. It is called by the {@link DesktopNotifyDriver}.
     */
    protected void sortMessage() {
        if (!title.isEmpty()) tlts = splitLines(title, theme.titleFont);
        if (!message.isEmpty()) msgs = splitLines(message, theme.contentFont);
        h = 15 + (getLineHeight(theme.titleFont)*tlts.length)
                + (getLineHeight(theme.contentFont)*msgs.length);
    }
    
    private String[] splitLines(String in, Font font) {
        String[] out;
        ArrayList<String> list=new ArrayList();
        String[] strs=in.split("\n");
        StringBuilder builder = new StringBuilder();
        FontMetrics ftm = DesktopNotifyDriver.getFontMetrics(font);
        for(String str:strs){
            String[] words=str.split(" ");
            for(String word:words){
                //System.out.println(str);
                //System.out.println(ftm.stringWidth(builder.toString())+"+"+ftm.stringWidth(str)+"<"+(w-10));
                if (ftm.stringWidth(builder.toString()) + ftm.stringWidth(word)
                        < (w-12-((icon==null && type==0)? 0:38))){
                    builder.append(word).append(" ");
                } else {
                    list.add(builder.toString());
                    builder=new StringBuilder().append(word).append(" ");
                }
            }
            list.add(builder.toString());
            builder.setLength(0);
        }
        out = new String[list.size()];
        out = list.toArray(out);
        return out;
    }
    
    // Integrated Logging
    public static final int DEBUG = 0;
    public static final int NONE = 10;
    
    /**
     * Allows to specify the level allowed for logging messages. Attempts to log
     * messages with a priority level below what's been set will be ignored.
     * @param level The logging priority level to allow as minimum.
     */
    public static void setLogLevel(int level) {
        logger.setLoggingLevel(level);
    }
    
    /**
     * Returns the level allowed for logging messages. Attempts to log
     * messages with a priority level below what's been set will be ignored.
     * @return The logging priority level curently allowed as minimum.
     */
    public static int getLogLevel() {
        return logger.getLoggingLevel();
    }
    
    public static void setLogOutput(OutputStream outStream) {
         logger.setOutput(outStream);
    }
    
    /**
     * Logs a debug message. Parameters can't be {@code null}.
     * @param tag     The tag associated to this message.
     * @param message The message to include.
     */
    public static void logDebug(String tag, String message) {
        logger.post(DEBUG, tag, message, null);
    }

    /**
     * Logs an information message. Parameters can't be {@code null}.
     * @param tag     The tag associated to this message.
     * @param message The message to include.
     */
    public static void logInfo(String tag, String message) {
        logger.post(INFORMATION, tag, message, null);
    }

    /**
     * Logs a warning message. Parameters can't be {@code null}.
     * @param tag     The tag associated to this message.
     * @param message The message to include.
     */
    public static void logWarning(String tag, String message) {
        logger.post(WARNING, tag, message, null);
    }

    /**
     * Logs an error message. Parameters other than the caught throwable can't
     * be {@code null}.
     * @param tag     The tag associated to this message.
     * @param message The message to include.
     * @param tr      A Throable object (an Exception, for example), whose
     *                message and stack trace should be logged too.
     */
    public static void logError(String tag, String message, Throwable tr) {
        logger.post(ERROR, tag, message, tr);
    }
    
    private static final Logger logger = new Logger();
    
    /**
     * Utilitary subclass used for logging.
     */
    private static class Logger {
        private OutputStream outStream;
        private PrintWriter outWritter;
        
        private int level;
        
        Logger() {
            this.level = INFORMATION;
            this.setOutput(System.out);
        }

        /**
         * Lets you specify an OutputStream for use in this Logger.
         * @param outStream The OutputStream you wish to log messages into.
         */
        public synchronized void setOutput(OutputStream outStream) {
            this.outStream = outStream;
            this.outWritter = new PrintWriter(outStream);
        }
    
        /**
         * @return <code>true</code> if a proper output destiny has been set, like
         *         an output file or stream.
         */
        public boolean isReady() {
            return outWritter != null;
        }
    
        private String levelToString(int level) {
            String str = "OTHER";
            switch(level) {
                case DEBUG: str = "DEBUG"; break;
                case INFORMATION: str = "INFO"; break;
                case WARNING: str = "WARNING"; break;
                case ERROR: str = "ERROR"; break;
            }
            return str;
        }
        
        public synchronized void setLoggingLevel(int level) {
            this.level = level;
        }
        
        public synchronized int getLoggingLevel() {
            return this.level;
        }
    
        /**
         * Checks for you if a given level is enabled in this logger.
         * @param level The level to check.
         * @return <code>true</code> if the given level is found to be enabled,
         *         <code>false</code> otherwise.
         */
        public boolean isEnabled(int level) {
            return this.level <= level;
        }
    
        /**
         * Full form of the method. This method lets you post a message into this
         * logger, so it can be logged or not depending of the assigned settings.
         * You can specify a title (tag), message, flag and a Throwable object (like
         * an Exception or Error) to include in the log.
         * @param level   The level to mark this message with.
         * @param tag     The tag associated to this message. It cannot be null.
         * @param message The message to include. It cannot be null.
         * @param tr      A Throwable whose message and stack trace should be
         *                printed.
         */
        public synchronized void post(int level, String tag, String message, Throwable tr) {
            if (tag == null) throw new NullPointerException("Tag please");
            if (message == null) throw new NullPointerException("Message please");
            if (isReady() && isEnabled(level)) {
                StringBuilder builder = new StringBuilder();
                builder.append(levelToString(level)).append(":").append(tag)
                       .append(" at [").append(new java.util.Date().toString())
                       .append("] -> ").append(message.trim());
                if (outStream instanceof PrintStream) {
                    ((PrintStream)outStream).println(builder.toString());
                } else {
                    outWritter.println(builder.toString());
                    outWritter.flush();
                }
                if (tr != null) {
                    if (outStream instanceof PrintStream) {
                        ((PrintStream)outStream).println("Caused by:");
                        tr.printStackTrace((PrintStream)outStream);
                    } else {
                        outWritter.println("Caused by:");
                        tr.printStackTrace(outWritter);
                        outWritter.flush();
                    }
                }
            }
        }
    }
}