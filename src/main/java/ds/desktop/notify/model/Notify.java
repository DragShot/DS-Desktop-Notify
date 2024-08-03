package ds.desktop.notify.model;

import ds.desktop.notify.DesktopNotify;
import ds.desktop.notify.DesktopNotifyDriver;
import ds.desktop.notify.NotificationBuilder;
import ds.desktop.notify.NotifyTheme;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import static ds.desktop.notify.DesktopNotify.*;

@Data
public class Notify {

    private String title;
    private String message;
    private Image icon;
    private NotifyType type;
    private NotifyDirection orientation;

    /**
     * A <code>NotifyTheme</code> object with the color and Font parameters to
     * be used when this notification is being painted.
     */
    private NotifyTheme theme;

    /**
     * A String array with the lines of the title.
     * It is refreshed automatically when the title is set.
     */
    private String[] tlts = new String[0];
    /**
     * A String array with the lines of the message.
     * It is refreshed automatically when the message is set.
     */
    private String[] msgs = new String[0];

    private int w = 0;
    private int h = 0;

    private boolean visible = false;
    private boolean markedForHide = false;
    private int highl = 0;
    private long popupStart = 0L;
    private long timeOut = 8000L;

    /**
     * An action to perform when the notification is clicked.
     * -- SETTER --
     * Allows to bind an action to this notification. It will be executed when
     * the notification gets clicked. Notifications with an action set will wait
     * for the user to click them.
     * <p>
     * <p>
     * -- GETTER --
     *
     * @param action he ActionListener to bind to this notification.
     * @return the ActionListener binded to this notification.
     */
    @Getter
    @Setter
    private ActionListener action;

    /**
     * A protected constructor for a DesktopNotify object, called internally.
     * You can use any of the <code>showDesktopMessage()</code> static methods
     * or a {@link NotificationBuilder} in order to create and display a
     * notification instead.
     *
     * @param title       The notification title. Can be {@code null} when the
     *                    notification isn't meant to have one.
     * @param message     The notification message. Can be {@code null} when
     *                    the notification isn't meant to have one.
     * @param orientation The text orientation, either {@link DesktopNotify#LEFT_TO_RIGHT} or
     *                    {@link DesktopNotify#RIGHT_TO_LEFT}.
     * @param type        The notification type. Check the type contans in the
     *                    class for the ones you can use.
     * @param icon        The icon to display. Can be {@code null} when the
     *                    notification isn't meant to have one.
     */
    public Notify(String title, String message, NotifyType type,
                  NotifyDirection orientation, Image icon) {
        this.title = (title == null ? "" : title);
        this.message = (message == null ? "" : message);
        this.type = type;
        this.orientation = orientation;
        this.icon = icon;
        this.theme = DesktopNotify.defTheme;
    }

    /**
     * Allows to set a <code>NotifyTheme</code> for this notification. One of
     * these defines stuff as the colors for text and background, the fonts and
     * even images for the default icons.
     *
     * @param theme The <code>NotifyTheme</code> to use for this notification.
     */
    public void setTheme(NotifyTheme theme) {
        if (theme != null) this.theme = theme;
    }

    /**
     * Allows to set a timeout for this notification, in milliseconds.
     * After the time is 'out', and if there isn't an action set, the
     * notification will just fade away.
     *
     * @param millis the timeout.
     */
    public void setTimeout(long millis) {
        timeOut = millis < 0 ? 0 : millis;
    }

    public long expTime() {
        return timeOut == 0 ? Long.MAX_VALUE : popupStart + timeOut;
    }

    /**
     * Polls the Driver in order to show this notification, so you don't have to
     * do it yourself.
     */
    public void show() {
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

    /**
     * The painting routine for the notification goes here.
     *
     * @param x     Where to start painting (X)
     * @param y     Where to start painting (Y)
     * @param hover A boolean that is <code>true</code> if the user is hovering
     *              this notification with the mouse pointer.
     * @param rd    The <code>Graphics2D</code> object to use for painting.
     * @param l     The current time.
     */
    public void render(int x, int y, boolean hover, Graphics2D rd, long l) {
        long i = l - popupStart;
        if (i > 500) i = expTime() - l;
        if (i < 0) i = 0;
        if (i > 500) i = -1;
        rd.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        AffineTransform trans = rd.getTransform();
        rd.translate(x, y);
        if (i != -1) {
            double d = i / 500.0;
            rd.translate(w / 2 - ((w / 2) * d), h / 2 - ((h / 2) * d));
            rd.scale(d, d);
            rd.setComposite(AlphaComposite
                    .getInstance(AlphaComposite.SRC_OVER, (float) d));
        }
        rd.setPaint(new GradientPaint(0, (title.isEmpty() ? 0 : 25),
                /*hover? new Color(50,57,65):*/theme.getBgGrad()[0],
                0, h, theme.getBgGrad()[highl + 1], false));
        if (hover && highl < 20) highl++;
        if (!hover && highl > 0) highl--;
        rd.fillRoundRect(0, 0, w, h, 20, 20);
        rd.setPaint(null);
        rd.setColor(theme.getBorderColor());
        rd.drawRoundRect(0, 0, w - 1, h - 1, 20, 20);
        if (i == -1) {
            int titleH = getLineHeight(theme.getTitleFont());
            int textH = getLineHeight(theme.getContentFont());
            if (!title.isEmpty()) {
                rd.setColor(theme.getTitleColor());
                rd.setFont(theme.getTitleFont());
                int tX = 5 + ((icon == null && type == NotifyType.NONE) ? 0 : 38);
                for (int j = 0; j < tlts.length; j++) {
                    if (orientation == NotifyDirection.RIGHT_TO_LEFT) {
                        FontMetrics ftm = DesktopNotifyDriver.getFontMetrics(theme.getTitleFont());
                        tX = w - 4 - ((icon == null && type == NotifyType.NONE) ? 0 : 38) - ftm.stringWidth(tlts[j]);
                    }
                    rd.drawString(tlts[j], tX, 20 + (titleH * j));
                }
            }
            if (!message.isEmpty()) {
                rd.setColor(theme.getContentColor());
                rd.setFont(theme.getContentFont());
                int tX = 6 + ((icon == null && type == NotifyType.NONE) ? 0 : 38);
                for (int j = 0; j < msgs.length; j++) {
                    if (orientation == NotifyDirection.RIGHT_TO_LEFT) {
                        FontMetrics ftm = DesktopNotifyDriver.getFontMetrics(theme.getContentFont());
                        tX = w - 5 - ((icon == null && type == NotifyType.NONE) ? 0 : 38) - ftm.stringWidth(msgs[j]);
                    }
                    rd.drawString(msgs[j], tX, 20 + (titleH * tlts.length) + (textH * j));
                }
            }
        }
//        if(expTime()==Long.MAX_VALUE){
//            rd.setFont(theme.titleFont);
//            rd.setColor(new Color(120,120,120));
//            rd.drawString("X", w-16, 18);
//        }
        Image icon = this.icon == null ?
                (type == NotifyType.NONE ? null : theme.getIconSet()[type.getImgPos()-1]) : this.icon;
        if (icon != null) {
            rd.drawImage(icon, orientation == NotifyDirection.RIGHT_TO_LEFT ? (w - 7 - 32) : 6, (h / 2) - 15, 32, 32, null);
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
    public void sortMessage() {
        if (!title.isEmpty()) tlts = splitLines(title, theme.getTitleFont());
        if (!message.isEmpty()) msgs = splitLines(message, theme.getContentFont());
        h = 15 + (getLineHeight(theme.getTitleFont()) * tlts.length)
                + (getLineHeight(theme.getContentFont()) * msgs.length);
    }

    private String[] splitLines(String in, Font font) {
        String[] out;
        ArrayList<String> list = new ArrayList<>();
        String[] strs = in.split("\n");
        StringBuilder builder = new StringBuilder();
        FontMetrics ftm = DesktopNotifyDriver.getFontMetrics(font);
        for (String str : strs) {
            String[] words = str.split(" ");
            for (String word : words) {
                if (ftm.stringWidth(builder.toString()) + ftm.stringWidth(word)
                        < (w - 12 - ((icon == null && type == NotifyType.NONE) ? 0 : 38))) {
                    builder.append(word).append(" ");
                } else {
                    list.add(builder.toString());
                    builder = new StringBuilder().append(word).append(" ");
                }
            }
            list.add(builder.toString());
            builder.setLength(0);
        }
        out = new String[list.size()];
        out = list.toArray(out);
        return out;
    }
}
