package cloudchen.dodgeball;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by CloudChen on 2016/3/27.
 */
public class MyCircle {

    float x, y, r;

    public MyCircle(float x, float y, float r) {
        this.x = x;
        this.y = y;
        this.r = r;
    }

    public void drawMyCircle(Canvas canvas, Paint paint) {
        canvas.drawCircle(x, y, r, paint);
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }
}

