/*
 * DS Desktop Notify
 * A small utility to show small notifications in your Desktop anytime!
 */
package ds.desktop.notify;

import ds.desktop.notify.model.Notify;
import ds.desktop.notify.model.NotifyDirection;
import ds.desktop.notify.model.NotifyType;
import lombok.Getter;
import lombok.Setter;

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
 *
 * @author DragShot
 * @version 0.9
 */
public class DesktopNotify {

    public static final int LEFT_TO_RIGHT = 0;
    public static final int RIGHT_TO_LEFT = 1;

    public static NotifyTheme defTheme = NotifyTheme.Dark;

    /**
     * Sets the theme to use by default when creating notifications.
     *
     * @param theme The theme to use.
     */
    public static void setDefaultTheme(NotifyTheme theme) {
        DesktopNotify.defTheme = theme;
    }

    /**
     * Gets the theme currently in use when creating notifications.
     *
     * @return The theme in use.
     */
    public static NotifyTheme getDefaultTheme() {
        return DesktopNotify.defTheme;
    }

    protected static NotifyDirection defTextOrientation = NotifyDirection.LEFT_TO_RIGHT;

    /**
     * Gets the current default text orientation.
     *
     * @return The text orientation.
     * @see #RIGHT_TO_LEFT
     * @see #LEFT_TO_RIGHT
     */
    public static NotifyDirection getDefaultTextOrientation() {
        return defTextOrientation;
    }

    /**
     * Sets the default text orientation to use for new notifications. Useful
     * for displaying languages that aren't read left-to-right.
     *
     * @param defTextOrientation The new text orientation.
     * @see #RIGHT_TO_LEFT
     * @see #LEFT_TO_RIGHT
     */
    public static void setDefaultTextOrientation(NotifyDirection defTextOrientation) {
        DesktopNotify.defTextOrientation = defTextOrientation;
    }

    /**
     * Creates and shows a new notification. If there isn't an instance of the
     * DesktopNotifyDriver thread running, it will be created and started to
     * show your notification window.
     * <p>
     * This notification does not bring any icon. Notifications created from
     * this method will stay on screen until the user clicks it. No actions can
     * be specified from this method.
     *
     * @param title   The title, if any.
     * @param message The message, if any.
     */
    public static void showDesktopMessage(String title, String message) {
        showDesktopMessage(title, message, NotifyType.NONE, null, null, 0);
    }

    /**
     * Creates and shows a new notification. If there isn't an instance of the
     * DesktopNotifyDriver thread running, it will be created and started to
     * show your notification window.
     * <p>
     * A default icon will be used, depending of the type, unless it is DEFAULT.
     * A DEFAULT type notification does not bring an icon by default, no matter
     * how ironic this sounds. Notifications created from this method will stay
     * on screen until the user clicks it. No actions can be specified from this
     * method.
     *
     * @param title   The title, if any.
     * @param message The message, if any.
     * @param type    An int that denotes a valid message type. Invalid
     *                values will be treated as DEFAULT.
     */
    public static void showDesktopMessage(String title, String message,
                                          NotifyType type) {
        showDesktopMessage(title, message, type, null, null, 0);
    }

    /**
     * Creates and shows a new notification. If there isn't an instance of the
     * DesktopNotifyDriver thread running, it will be created and started to
     * show your notification window.
     * <p>
     * A default icon will be used, depending of the type, unless it is DEFAULT.
     * A DEFAULT type notification does not bring an icon by default, no matter
     * how ironic this sounds. Notifications created from this method will stay
     * on screen until the user clicks it, firing the action specified (if any).
     *
     * @param title   The title, if any.
     * @param message The message, if any.
     * @param type    An int that denotes a valid message type. Invalid
     *                values will be treated as DEFAULT.
     * @param action  An ActionListener with the action to execute when
     *                the user clicks the notification.
     */
    public static void showDesktopMessage(String title, String message,
                                          NotifyType type, ActionListener action) {
        showDesktopMessage(title, message, type, null, action, 0);
    }

    /**
     * Creates and shows a new notification. If there isn't an instance of the
     * DesktopNotifyDriver thread running, it will be created and started to
     * show your notification window.
     * <p>
     * Notifications created from this method will stay on screen until the user
     * clicks it, firing the action specified (if any).
     *
     * @param title   The title, if any.
     * @param message The message, if any.
     * @param type    An int that denotes a valid message type. Invalid
     *                values will be treated as DEFAULT.
     * @param icon    A custom Image to use instead of the default icon
     *                assigned by type.
     * @param action  An ActionListener with the action to execute when
     *                the user clicks the notification.
     */
    public static void showDesktopMessage(String title, String message,
                                          NotifyType type, Image icon, ActionListener action) {
        showDesktopMessage(title, message, type, icon, action, 0);
    }

    /**
     * Creates and shows a new notification. If there isn't an instance of the
     * DesktopNotifyDriver thread running, it will be created and started to
     * show your notification window.
     * <p>
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
                                          NotifyType type, long maxTimeMillis) {
        showDesktopMessage(title, message, type, null, null, maxTimeMillis);
    }

    /**
     * Creates and shows a new notification. If there isn't an instance of the
     * DesktopNotifyDriver thread running, it will be created and started to
     * show your notification window.
     * <p>
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
                                          NotifyType type, long maxTimeMillis, ActionListener action) {
        showDesktopMessage(title, message, type, null, action, maxTimeMillis);
    }

    /**
     * Creates and shows a new notification. If there isn't an instance of the
     * DesktopNotifyDriver thread running, it will be created and started to
     * show your notification window.
     * <p>
     * Notifications created from this method will stay on screen until the user
     * clicks it. No actions can be specified from this method.
     *
     * @param title   The title, if any.
     * @param message The message, if any.
     * @param type    An int that denotes a valid message type. Invalid
     *                values will be treated as DEFAULT.
     * @param icon    A custom Image to use instead of the default icon
     *                assigned by type.
     */
    public static void showDesktopMessage(String title, String message,
                                          NotifyType type, Image icon) {
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
                                          NotifyType type, Image icon, ActionListener action, long maxTimeMillis) {
        Notify pane = new Notify(title, message, type, defTextOrientation, icon);
        pane.setTimeout(maxTimeMillis);
        pane.setAction(action);
        pane.show();
    }

}