package dummydata.android.spinWheel;

public class WheelItem {
    public long topText;
    public String splashTopText;
    public String secondaryText;
    public int secondaryTextOrientation;
    public int icon;
    public int color;
    public int textColor;

    public WheelItem() {
    }

    public WheelItem(long topText, String secondaryText, int textColor, int color) {
        this.topText = topText;
        this.secondaryText = secondaryText;
        this.textColor = textColor;
        this.color = color;
    }

}
