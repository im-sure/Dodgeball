package cloudchen.dodgeball;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

/**
 * Created by CloudChen on 2016/3/27.
 */

public class MyButton {

    private int x, y, w, h;
    private Bitmap bmp;
    private boolean isUp, isDown;

    public MyButton(Bitmap bmp, int x, int y) {
        this.bmp = bmp;
        this.x = x;
        this.y = y;
        this.w = bmp.getWidth();
        this.h = bmp.getHeight();
        isUp = false;
        isDown = false;
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(bmp, x, y, paint);
    }

    public boolean isPressed(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getX() <= x + w && event.getX() >= x) {
                if (event.getY() <= y + h && event.getY() >= y) {
                    isDown = true;
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getX() <= x + w && event.getX() >= x) {
                if (event.getY() <= y + h && event.getY() >= y) {
                    isUp = true;
                }
            }
        }
        if (isUp && isDown) {
            isUp = false;
            isDown = false;
            return true;
        }
        return false;
    }
}
