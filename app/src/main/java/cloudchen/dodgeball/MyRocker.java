package cloudchen.dodgeball;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

/**
 * Created by CloudChen on 2016/3/28.
 */
public class MyRocker {

    private float smallCenterX, smallCenterY, smallCenterR;
    private float bigCenterX, bigCenterY, bigCenterR;
    private float hypotenuse;
    private double rad;


    public MyRocker(float smallCenterX, float smallCenterY, float smallCenterR, float bigCenterX, float bigCenterY, float bigCenterR) {
        this.smallCenterX = smallCenterX;
        this.smallCenterY = smallCenterY;
        this.smallCenterR = smallCenterR;
        this.bigCenterX = bigCenterX;
        this.bigCenterY = bigCenterY;
        this.bigCenterR = bigCenterR;
    }

    public void drawRocker(Canvas canvas, Paint paint) {
        canvas.drawCircle(smallCenterX, smallCenterY, smallCenterR, paint);
        canvas.drawCircle(bigCenterX, bigCenterY, bigCenterR, paint);
    }

    public void isRocked(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            smallCenterX = bigCenterX;
            smallCenterY = bigCenterY;
        } else {
            int pointX = (int) event.getX();
            int pointY = (int) event.getY();
            float px = pointX - bigCenterX;
            float py = pointY - bigCenterY;
            hypotenuse = (float) Math.sqrt(Math.pow(px, 2) + Math.pow(py, 2));
            float cosAngle = px / hypotenuse;
            rad = (float) Math.acos(cosAngle);
            if (pointY < bigCenterY) {
                rad = -rad;
            }
            if (Math.sqrt(Math.pow((bigCenterX - (int) event.getX()), 2) + Math.pow((bigCenterY - (int) event.getY()), 2)) <= bigCenterR) {
                smallCenterX = pointX;
                smallCenterY = pointY;
            } else {
                setSmallCircleXY(bigCenterX, bigCenterY, bigCenterR, rad);
            }
        }
    }

    public void setSmallCircleXY(float centerX, float centerY, float R, double rad) {
        smallCenterX = (float) (R * Math.cos(rad)) + centerX;
        smallCenterY = (float) (R * Math.sin(rad)) + centerY;
    }

    public float getHypotenuse() {
        return hypotenuse;
    }

    public double getRad() {
        return rad;
    }

    public void setZero() {
        hypotenuse = 0;
        rad = 0;
    }

    public void resetXY() {
        smallCenterX = bigCenterX;
        smallCenterY = bigCenterY;
    }

}
