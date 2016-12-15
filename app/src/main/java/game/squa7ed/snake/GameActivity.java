package game.squa7ed.snake;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity
{
    private static final String TAG = Constants.DEBUG_TAG + "GameActivity";
    private ViewRenderer renderer;
    private Logic logic;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Set fullscreen, no title, landscape.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.layout_game);
        // Set layout parameters.
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Constants.setParams(dm.widthPixels, dm.heightPixels);
        GameField field = new GameField();
        logic = new Logic(this, field);
        renderer = new ViewRenderer(field, ((GameView) findViewById(R.id.gameView)).getHolder());
        findViewById(R.id.speed_button).setOnTouchListener(logic);
        findViewById(R.id.direction_button).setOnTouchListener(logic);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (renderer != null && !renderer.isRunning())
        {
            renderer.setRunning(true);
            renderer.start();
        }
        if (logic != null && !logic.isRunning())
        {
            logic.setRunning(true);
            logic.start();
        }
    }
}
