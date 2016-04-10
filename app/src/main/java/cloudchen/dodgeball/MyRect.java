package cloudchen.dodgeball;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by CloudChen on 2016/3/27.
 */

public class MyRect {

    private float x, y, w, h;

    public MyRect(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public void drawMyRect(Canvas canvas, Paint paint) {
        canvas.drawRect(x, y, x + w, y + h, paint);
    }
}
