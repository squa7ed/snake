package game.squa7ed.snake;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

public class GameActivity extends Activity
{
    private static final String TAG = Constants.DEBUG_TAG + "GameActivity";
    private GameField field;
    private Worker worker;
    private Thread workerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_game);
        // Set layout parameters.
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Constants.setParams(dm.widthPixels, dm.heightPixels);
        field = new GameField();
        worker = new Worker(this, field);
        findViewById(R.id.image_view_speed_background).setOnTouchListener(worker);
        findViewById(R.id.image_view_direction_background).setOnTouchListener(worker);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        workerThread = new Thread(worker);
        worker.setRunning(true);
        workerThread.start();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        worker.setRunning(false);
        try
        {
            workerThread.join();
        } catch (InterruptedException e)
        {
            Log.e(TAG, "onStop: ", e);
        }
        workerThread = null;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        worker = null;
        field = null;
    }
}
