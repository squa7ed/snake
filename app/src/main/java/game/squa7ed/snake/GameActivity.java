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
        workerThread = new Thread(worker);
        findViewById(R.id.image_view_speed_background).setOnTouchListener(worker);
        findViewById(R.id.image_view_direction_background).setOnTouchListener(worker);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        worker.setRunning(true);
        if (workerThread == null)
        {
            workerThread = new Thread(worker);
        }
        if (workerThread.getState() == Thread.State.NEW)
        {
            workerThread.start();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        worker.setRunning(false);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        try
        {
            workerThread.join();
        } catch (InterruptedException e)
        {
            Log.e(TAG, "onDestroy: ", e);
        }
        workerThread = null;
        worker = null;
        field = null;
    }
}
