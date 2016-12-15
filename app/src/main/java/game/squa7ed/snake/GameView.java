package game.squa7ed.snake;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Squa7ed on 16-12-4.
 * SurfaceView to draw the field.
 */
class GameView extends SurfaceView implements SurfaceHolder.Callback
{
    private static final String TAG = Constants.DEBUG_TAG + "GameView";

    GameView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
    }
}
