package cloudchen.dodgeball;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import net.youmi.android.AdManager;
import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;

public class MainActivity extends Activity {

    public static MainActivity main;

    private LinearLayout adLayout;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdManager.getInstance(this).init("8c5f97ccce2fafd1", "12de32e6b06e5af8", false);
        main = this;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        AdView adView = new AdView(this, AdSize.FIT_SCREEN);
        adLayout = (LinearLayout) findViewById(R.id.adLayout);
        adLayout.addView(adView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MySurfaceView.gameIsPaused = false;
        flag = true;
        Thread th = new Thread() {
            @Override
            public void run() {
                super.run();
                while (flag) {
                    if (MySurfaceView.gameIsOver) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adLayout.setVisibility(View.VISIBLE);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adLayout.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            }
        };
        th.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (MySurfaceView.gameState == MySurfaceView.GAMESTATE_PLAY) {
            MySurfaceView.gameIsPaused = true;
        }
        flag = false;
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