import ds.desktop.notify.DesktopNotify;
import ds.desktop.notify.model.NotifyType;

public class NotificationTest {

    public static void main(String[] args) {
        DesktopNotify.showDesktopMessage(
                "This is a notification sucess",
                "With DS Desktop Notify, displaying notifications on the screen is quick and easy!",
                NotifyType.SUCCESS);
        DesktopNotify.showDesktopMessage(
                "This is a notification",
                "With DS Desktop Notify, displaying notifications on the screen is quick and easy!",
                NotifyType.INFORMATION);
        DesktopNotify.showDesktopMessage(
                "This is a notification",
                "With DS Desktop Notify, displaying notifications on the screen is quick and easy!",
                NotifyType.ERROR);
    }
}
