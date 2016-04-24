package cloudchen.dodgeball;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

    public static MainActivity main;
    private MySurfaceView surfaceView;
    private boolean islocked = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = this;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        surfaceView = new MySurfaceView(this);
        setContentView(surfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (islocked){
            surfaceView.thNotify();
            islocked = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*synchronized (MainActivity.this)
        {
            try {
                synchronized (surfaceView) {
                    surfaceView.wait();
                    islocked = true;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
        surfaceView.thWait();
        islocked = true;
    }

    public void exit() {
        System.exit(0);
    }

    //屏蔽“返回”实体按键,避免程序进入后台;
    @Override
    public void onBackPressed() {

    }
}