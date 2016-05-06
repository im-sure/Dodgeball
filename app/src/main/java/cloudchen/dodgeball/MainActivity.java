package cloudchen.dodgeball;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import net.youmi.android.AdManager;
import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;

public class MainActivity extends Activity {

    public static MainActivity main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdManager.getInstance(this).init("8c5f97ccce2fafd1", "12de32e6b06e5af8", true);
        main = this;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        AdView adView = new AdView(this, AdSize.FIT_SCREEN);
        LinearLayout adLayout = (LinearLayout) findViewById(R.id.adLayout);
        adLayout.addView(adView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MySurfaceView.gameIsPaused = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (MySurfaceView.gameState == MySurfaceView.GAMESTATE_PLAY) {
            MySurfaceView.gameIsPaused = true;
        }
    }

    public void exit() {
        System.exit(0);
    }

    //屏蔽“返回”实体按键,避免程序进入后台;
    @Override
    public void onBackPressed() {

    }
}