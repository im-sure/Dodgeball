package cloudchen.dodgeball;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import net.youmi.android.AdManager;
import net.youmi.android.spot.SpotManager;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

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

        Bmob.initialize(this, "7b4aa127922f003fe4a388b347c442e0");
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

    public static void submit(View view) {
        String name = "CloudChen";
        String feedback = "test";
        if (name.equals("") || feedback.equals("")) {
            return;
        }
        // 创建BmobObject对象
        Feedback feedbackObj = new Feedback();
        feedbackObj.setName(name);
        feedbackObj.setFeedback(feedback);
        feedbackObj.save(main, new SaveListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(
                        main,
                        "submit success",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(
                        main,
                        "submit failure",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void queryAll(View view) {
        // 通过BmobQuery创建一个查询对象
        BmobQuery<Feedback> query = new BmobQuery<Feedback>();
        query.findObjects(main, new FindListener<Feedback>() {
            @Override
            public void onSuccess(List<Feedback> feedbacks) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        main);
                builder.setTitle("Query");
                String str = "";
                for (Feedback feedback : feedbacks) {
                    str += feedback.getName() + ":" +
                            feedback.getFeedback() + "\n";
                }
                builder.setMessage(str);
                builder.create().show();
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    public void exit() {
        System.exit(0);
    }
}