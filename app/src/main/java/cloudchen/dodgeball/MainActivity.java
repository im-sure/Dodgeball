package cloudchen.dodgeball;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import net.youmi.android.AdManager;
import net.youmi.android.spot.SpotManager;

public class MainActivity extends Activity {

    public static MainActivity main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdManager.getInstance(this).init("8c5f97ccce2fafd1", "12de32e6b06e5af8", false);
        main = this;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(new MySurfaceView(this));

        SpotManager.getInstance(this).loadSpotAds();
        SpotManager.getInstance(this).setSpotOrientation(SpotManager.ORIENTATION_PORTRAIT);
        SpotManager.getInstance(this).setAnimationType(SpotManager.ANIM_ADVANCE);
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

    @Override
    protected void onDestroy() {
        SpotManager.getInstance(this).onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (MySurfaceView.gameState == MySurfaceView.GAMESTATE_MENU) {
            System.exit(0);
        } else {
            MySurfaceView.gameState = MySurfaceView.GAMESTATE_MENU;
            MySurfaceView.gameIsOver = false;
            MySurfaceView.isFirstInMenu = true;
            MySurfaceView.timer.cancel();
        }
    }

    public void exit() {
        System.exit(0);
    }
}