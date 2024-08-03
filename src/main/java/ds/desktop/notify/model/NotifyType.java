package ds.desktop.notify.model;

import lombok.Getter;

public enum NotifyType {
    INFORMATION(1), WARNING(2), ERROR(3), HELP(4), TIP(5), INPUT_REQUEST(6), SUCCESS(7), FAIL(8), NONE(-1);

    @Getter
    private int imgPos;

    NotifyType(int imgPos) {
        this.imgPos = imgPos;
    }
}
