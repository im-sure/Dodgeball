package cloudchen.dodgeball;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import net.youmi.android.AdManager;
import net.youmi.android.spot.SpotManager;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class MainActivity extends Activity {

    public static MainActivity main;

    private static long mRecord;
    public static int mQueryState;

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

        Bmob.initialize(this, "7b4aa127922f003fe4a388b347c442e0");

        mRecord = 0;
        mQueryState = 0;
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

    public static void submit(long record) {
        String deviceName = Build.MODEL;
        long worldRecord = record;
        if (deviceName.equals("") || worldRecord == 0) {
            return;
        }
        // 创建BmobObject对象
        WorldRecord worldRecordObj = new WorldRecord();
        worldRecordObj.setDeviceName(deviceName);
        worldRecordObj.setWorldRecord(worldRecord);
        worldRecordObj.save(main, new SaveListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    public static long query() {
        // 通过BmobQuery创建一个查询对象
        final BmobQuery<WorldRecord> query = new BmobQuery<WorldRecord>();
        query.findObjects(main, new FindListener<WorldRecord>() {
            @Override
            public void onSuccess(List<WorldRecord> worldRecords) {
                for (WorldRecord worldRecord : worldRecords) {
                    if (worldRecord.getWorldRecord() > mRecord) {
                        mRecord = worldRecord.getWorldRecord();
                    }
                }
                mQueryState = 1;
            }

            @Override
            public void onError(int i, String s) {
                mQueryState = -1;
            }
        });
        while (mQueryState == 0) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return  mRecord;
    }

    public static int getQueryState() {
        return mQueryState;
    }

    public void exit() {
        System.exit(0);
    }
}