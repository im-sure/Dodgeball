package cloudchen.dodgeball;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.CircleDef;
import org.jbox2d.collision.PolygonDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.ContactListener;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.ContactPoint;
import org.jbox2d.dynamics.contacts.ContactResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 * Created by CloudChen on 2016/3/27.
 */

public class MySurfaceView extends SurfaceView implements Callback, Runnable, ContactListener {

    private final String TAG = "MySurfaceView";
    private Thread th;
    private SurfaceHolder sfh;
    private Canvas canvas;
    private Paint paint;
    private Paint paintalpha;
    private int screenW, screenH;
    private boolean flag;

    //添加一个物理世界
    //屏幕映射到现实世界的比例 30px：1m；
    private final float RATE = 30;
    private final float MYMAXFORCE = 100;
    private final float MAXFORCE = 5000;
    private World world;
    private AABB aabb;
    private Vec2 gravity;
    private Vec2 vForce;
    private Vec2 vEnemyForce;
    private float timeStep = 1f / 60f;
    private int iterations = 10;

    //声明游戏状态
    private final int GAMESTATE_MENU = 0;
    private final int GAMESTATE_PLAY = 1;
    private int gameState = GAMESTATE_MENU;
    private boolean gameIsOver;
    private Integer lives;
    private long time = 0;
    private TimerTask timerTask;
    private Timer timer;
    private boolean isScheduled;
    private boolean allCreated;
    private int collision;
    private final int NONE = 0;
    private final int COLLIDE_TO_RED = 1;
    private final int COLLIDE_TO_BLACK = 2;
    private final int COLLIDE_TO_GREEN = 3;
    private SharedPreferences.Editor editor;
    private SharedPreferences pref;

    //声明游戏物体
    private final float RADIUS = 30;
    private final int enemyNum = 5;
    private Bitmap bmpStart, bmpExit, bmpBack;
    private MyButton btnStart, btnExit, btnBack;
    private Bitmap bmpHeart;
    private Body rectU, rectD, rectL, rectR;
    private Body myBall;
    private Vector<Body> vcBalls;
    private int createEnemyTime = 50;
    private int countTime = 0;
    private int countNum = 0;
    private Random random;
    private int specialBall;
    private final int BLACK = 0;
    private final int GREEN = 1;

    //360°平滑游戏摇杆
    private MyRocker rocker;
    private float smallCenterX, smallCenterY, smallCenterR;
    private float bigCenterX, bigCenterY, bigCenterR;


    public MySurfaceView(Context context) {
        super(context);
        this.setKeepScreenOn(true);
        sfh = this.getHolder();
        sfh.addCallback(this);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Style.FILL);
        paintalpha = new Paint();
        paintalpha.setAntiAlias(true);
        paintalpha.setStyle(Style.FILL);
        paintalpha.setColor(Color.BLACK);
        paintalpha.setAlpha(0x77);
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        aabb = new AABB();
        gravity = new Vec2(0, 0);
        vForce = new Vec2(0, 0);
        vEnemyForce = new Vec2(0, 0);
        aabb.lowerBound.set(-100, -100);
        aabb.upperBound.set(100, 100);
        world = new World(aabb, gravity, true);

        bmpStart = BitmapFactory.decodeResource(getResources(), R.mipmap.start);
        bmpExit = BitmapFactory.decodeResource(getResources(), R.mipmap.exit);
        bmpBack = BitmapFactory.decodeResource(getResources(), R.mipmap.back);
        bmpHeart = BitmapFactory.decodeResource(getResources(), R.mipmap.heart);

        isScheduled = false;
        allCreated = false;
        collision = NONE;
        specialBall = BLACK;
        lives = new Integer(3);
        timerTask = new TimerTask() {
            public void run() {
                time += 1000;
            }
        };
        timer = new Timer();
        vcBalls = new Vector<Body>();
        random = new Random();
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = pref.edit();
    }

    //SurfaceView创建
    public void surfaceCreated(SurfaceHolder holder) {
        if (gameState == GAMESTATE_MENU) {
            screenW = this.getWidth();
            screenH = this.getHeight();
            btnStart = new MyButton(bmpStart, screenW / 2 - bmpStart.getWidth() / 2, screenH / 10 * 7);
            btnExit = new MyButton(bmpExit, screenW / 2 - bmpExit.getWidth() / 2, screenH / 5 * 4);
            btnBack = new MyButton(bmpBack, screenW / 2 - bmpBack.getWidth() / 2, screenH / 10 * 7);
            rectU = createRect(screenW / 20, screenH / 20 + 10, screenW / 10 * 9, 10, 0);
            rectD = createRect(screenW / 20, screenH / 5 * 4, screenW / 10 * 9, 10, 0);
            rectL = createRect(screenW / 20, screenH / 20 + 10, 10, screenH / 20 * 15, 0);
            rectR = createRect(screenW / 20 * 19 - 10, screenH / 20 + 10, 10, screenH / 20 * 15, 0);
            myBall = createCircle(screenW / 2, screenH / 2, RADIUS, 1);
            smallCenterX = screenW / 2;
            smallCenterY = screenH / 10 * 9;
            smallCenterR = screenH / 40;
            bigCenterX = screenW / 2;
            bigCenterY = screenH / 10 * 9;
            bigCenterR = screenH / 15;
            rocker = new MyRocker(smallCenterX, smallCenterY, smallCenterR, bigCenterX, bigCenterY, bigCenterR);

            world.setContactListener(this);
        }
        flag = true;
        th = new Thread(this);
        th.start();
    }

    //在物理世界中添加矩形Body
    public Body createRect(float x, float y, float w, float h, float density) {
        PolygonDef pd = new PolygonDef();
        pd.density = density;
        pd.friction = 0f;
        pd.restitution = 1f;
        pd.setAsBox(w / 2 / RATE, h / 2 / RATE);
        BodyDef bd = new BodyDef();
        bd.position.set((x + w / 2) / RATE, (y + h / 2) / RATE);
        Body body = world.createBody(bd);
        body.m_userData = new MyRect(x, y, w, h);
        body.createShape(pd);
        body.setMassFromShapes();
        return body;
    }

    //在物理世界中添加圆形Body
    public Body createCircle( float x, float y, float r, float density) {
        CircleDef cd = new CircleDef();
        cd.density = density;
        cd.friction = 0f;
        cd.restitution = 1f;
        cd.radius = r / RATE;
        BodyDef bd = new BodyDef();
        bd.position.set(x / RATE, y / RATE);
        Body body = world.createBody(bd);
        body.m_userData = new MyCircle(x, y, r);
        body.createShape(cd);
        body.setMassFromShapes();
        return body;
    }

    //绘制函数
    public void draw() {
        try {
            canvas = sfh.lockCanvas();
            canvas.drawColor(Color.WHITE);
            switch (gameState) {
                case GAMESTATE_MENU:
                    paint.setColor(Color.BLUE);
                    paint.setTextSize(screenW / 5);
                    canvas.drawText("Dodgeball", screenW / 18, screenH / 5, paint);
                    paint.setTextSize(screenW / 20);
                    canvas.drawText("What you control are the FORCE", screenW / 6, screenH / 10 * 3, paint);
                    canvas.drawText("and the DIRECTION of the ball", screenW / 6, screenH / 20 * 7, paint);
                    canvas.drawText("Blue Ball - The ball you control", screenW / 6, screenH / 20 * 9, paint);
                    paint.setColor(Color.RED);
                    canvas.drawText("Red Ball - Your enemy, ESCAPE!", screenW / 6, screenH / 2, paint);
                    paint.setColor(Color.BLACK);
                    canvas.drawText("Black Ball - Destroy all enemies", screenW / 6, screenH / 20 * 11, paint);
                    paint.setColor(Color.GREEN);
                    canvas.drawText("Green Ball - Get one extra life", screenW / 6, screenH / 5 * 3, paint);
                    btnStart.draw(canvas, paint);
                    btnExit.draw(canvas, paint);
                    break;
                case GAMESTATE_PLAY:
                    Date date = new Date(time);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
                    String timeString = simpleDateFormat.format(date);
                    paint.setColor(Color.BLACK);
                    paint.setTextSize(screenW / 10);
                    canvas.drawText(timeString, screenW / 4 * 3, screenH / 20, paint);
                    String livesString = "× " + lives.toString();
                    canvas.drawText(livesString, screenW / 8, screenH / 20, paint);
                    canvas.drawBitmap(bmpHeart, 10, 10, paint);
                    Body body = world.getBodyList();
                    for (int i = 1; i < world.getBodyCount(); i++) {
                        if ((body.m_userData) instanceof MyRect) {
                            MyRect rect = (MyRect) body.m_userData;
                            paint.setColor(Color.BLACK);
                            rect.drawMyRect(canvas, paint);
                        } else if ((body.m_userData) instanceof MyCircle) {
                            MyCircle circle = (MyCircle) body.m_userData;
                            if (circle == myBall.m_userData) {
                                paint.setColor(Color.BLUE);
                            } else if ((countNum == enemyNum) && (circle == vcBalls.elementAt(enemyNum - 1).m_userData)) {
                                switch (specialBall) {
                                    case BLACK:
                                        paint.setColor(Color.BLACK);
                                        break;
                                    case GREEN:
                                        paint.setColor(Color.GREEN);
                                        break;
                                }
                            } else {
                                paint.setColor(Color.RED);
                            }
                            circle.drawMyCircle(canvas, paint);
                        }
                        body = body.m_next;
                    }
                    rocker.drawRocker(canvas, paintalpha);
                    if (gameIsOver) {
                        canvas.drawRect(0, 0, screenW, screenH, paintalpha);
                        editor.putString("currenttime", timeString);
                        if ((timeString.compareTo(pref.getString("record", "")) > 0)) {
                            editor.putString("record", timeString);
                        }
                        paint.setColor(Color.WHITE);
                        paint.setTextSize(screenW / 8);
                        canvas.drawText("Record", screenW / 18, screenH / 5, paint);
                        canvas.drawText(pref.getString("record", ""), screenW / 18, screenH / 10 * 3, paint);
                        canvas.drawText("Curent Time", screenW / 18, screenH / 5 * 2, paint);
                        canvas.drawText(timeString, screenW / 18, screenH / 2, paint);
                        btnBack.draw(canvas, paint);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null)
                sfh.unlockCanvasAndPost(canvas);
        }
    }

    //触屏按键事件处理
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (gameState) {
            case GAMESTATE_MENU:
                if (btnStart.isPressed(event)) {
                    gameState = GAMESTATE_PLAY;
                    timer.purge();
                    if (!isScheduled) {
                        timer.schedule(timerTask, 1000, 1000);
                        isScheduled = true;
                    }
                    //timer.schedule(timerTask, 1000, 1000);
                } else if (btnExit.isPressed(event)) {
                    MainActivity.main.exit();
                }
                break;
            case GAMESTATE_PLAY:
                if (!gameIsOver) {
                    rocker.isRocked(event);
                } else if (btnBack.isPressed(event)) {
                    gameState = GAMESTATE_MENU;
                }
                break;
        }
        return true;
    }



    //游戏逻辑函数
    public void logic() {
        switch (gameState) {
            case GAMESTATE_MENU:
                gameIsOver = false;
                for(Body body1 :vcBalls) {
                    world.destroyBody(body1);
                }
                vcBalls.removeAllElements();
                countNum = 0;
                specialBall = BLACK;
                allCreated = false;
                world.destroyBody(myBall);
                myBall = createCircle(screenW / 2, screenH / 2, RADIUS, 1);
                //vForce.set(0, 0);
                lives = 3;
                time = 0;
                break;
            case GAMESTATE_PLAY:
                if (!gameIsOver) {
                    world.step(timeStep, iterations);
                    vForce.set(MYMAXFORCE * rocker.getHypotenuse() / bigCenterR * (float) Math.cos(rocker.getRad()),
                            MYMAXFORCE * rocker.getHypotenuse() / bigCenterR * (float) Math.sin(rocker.getRad()));
                    myBall.applyForce(vForce, myBall.getWorldCenter());
                    rocker.setZero();
                    Body body = world.getBodyList();
                    for (int i = 1; i < world.getBodyCount(); i++) {
                        if ((body.m_userData) instanceof MyRect) {

                        } else if ((body.m_userData) instanceof MyCircle) {
                            MyCircle circle = (MyCircle) (body.m_userData);
                            circle.setX(body.getPosition().x * RATE);
                            circle.setY(body.getPosition().y * RATE);
                        }
                        body = body.m_next;
                    }
                    if (!allCreated) {
                        countTime++;
                    }
                    if (countTime == createEnemyTime) {
                        countTime = 0;
                        vcBalls.addElement(createCircle(screenW / 2, screenH / 10 + 50, RADIUS, 1));
                        int rand = random.nextInt(120) + 30;
                        double rad = rand / 180d * Math.PI;
                        vEnemyForce.set(MAXFORCE * (float) Math.cos(rad), MAXFORCE * (float) Math.sin(rad));
                        countNum++;
                    }
                    if (((countTime == (createEnemyTime / 2))) && (vcBalls.size() != 0)) {
                        vcBalls.elementAt(countNum - 1).applyForce(vEnemyForce, vcBalls.elementAt(countNum - 1).getWorldCenter());
                        if (countNum == enemyNum) {
                            countTime = 0;
                            allCreated = true;
                        }
                    }
                    switch (collision) {
                        case NONE:
                            break;
                        case COLLIDE_TO_RED:
                            lives -= 1;
                            if (lives == 0) {
                                gameIsOver = true;
                            }
                            break;
                        case COLLIDE_TO_BLACK:
                            for(Body body1 :vcBalls) {
                                world.destroyBody(body1);
                            }
                            vcBalls.removeAllElements();
                            countNum = 0;
                            specialBall = GREEN;
                            allCreated = false;
                            break;
                        case COLLIDE_TO_GREEN:
                            world.destroyBody(vcBalls.elementAt(enemyNum - 1));
                            vcBalls.removeElementAt(enemyNum - 1);
                            lives++;
                            countNum--;
                            specialBall = BLACK;
                            allCreated = false;
                            break;
                    }
                    collision = NONE;
                } else {

                }
                break;
        }
    }

    public void run() {
        while (flag) {
            draw();
            logic();
            try {
                Thread.sleep((long) timeStep * 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        flag = false;
    }

    @Override
    public void add(ContactPoint point) {
        //当前游戏状态为进行游戏时
        if ((gameState == GAMESTATE_PLAY) && !gameIsOver) {
            if(point.shape1.getBody() == myBall) {
                for (Body body : vcBalls) {
                    if (point.shape2.getBody() == body) {
                        if ((countNum == enemyNum) && (body == vcBalls.elementAt(enemyNum - 1))) {
                            switch (specialBall) {
                                case BLACK:
                                    collision = COLLIDE_TO_BLACK;
                                    break;
                                case GREEN:
                                    collision = COLLIDE_TO_GREEN;
                                    break;
                            }
                            return;
                        } else {
                            collision = COLLIDE_TO_RED;
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void persist(ContactPoint point) {

    }

    @Override
    public void remove(ContactPoint point) {

    }

    @Override
    public void result(ContactResult point) {

    }

}
