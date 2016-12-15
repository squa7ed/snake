package game.squa7ed.snake;

import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by squa7ed on 16-12-10.
 * Game logic slash controller.
 */
class Logic extends Thread implements View.OnTouchListener
{
    private static final String TAG = Constants.DEBUG_TAG + "Logic";
    private final GameField field;
    private final Snake mSnake;
    private final ImageView directionIndicator;
    private final ImageView speedIndicator;
    private final ImageView directionButton;
    private final Point direction;
    private boolean running;

    Logic(Activity activity, GameField field)
    {
        this.field = field;
        mSnake = field.getSnake();
        directionIndicator = (ImageView) activity.findViewById(R.id.direction_indicator);
        speedIndicator = (ImageView) activity.findViewById(R.id.speed_indicator);
        directionButton = (ImageView) activity.findViewById(R.id.direction_button);
        direction = new Point(0, 0);
    }

    @Override
    public void run()
    {
        long t;
        while (running)
        {
            synchronized (field)
            {
                t = System.currentTimeMillis();
                field.move();
                field.check();
                Log.d(TAG, "run: moving and checking done in " + (System.currentTimeMillis() - t) + "ms.");
            }
            try
            {
                Thread.sleep(Constants.SLEEP);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    boolean isRunning()
    {
        return running;
    }

    void setRunning(boolean running)
    {
        this.running = running;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        if (v.getId() == R.id.speed_button)
        {
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    speedIndicator.setAlpha(0.5f);
                    mSnake.setSpeed(Constants.ACCELERATE);
                    return true;
                case MotionEvent.ACTION_UP:
                    speedIndicator.setAlpha(0.25f);
                    mSnake.setSpeed(Constants.DEFAULT_SPEED);
                    return false;
            }
        }
        if (v.getId() == R.id.direction_button)
        {
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    directionIndicator.setAlpha(0.5f);
                    setDirection(event.getX(), event.getY());
                    setIndicator(event.getX(), event.getY());
                    return true;
                case MotionEvent.ACTION_MOVE:
                    setDirection(event.getX(), event.getY());
                    setIndicator(event.getX(), event.getY());
                    return true;
                case MotionEvent.ACTION_UP:
                    directionIndicator.setAlpha(0.25f);
                    directionIndicator.setTranslationX(0);
                    directionIndicator.setTranslationY(0);
                    return false;
            }
        }
        return false;
    }

    private void setDirection(float x, float y)
    {
        float oX = directionButton.getWidth() / 2;
        float oY = directionButton.getHeight() / 2;
        float d = Constants.getDistance(oX, oY, x, y);
        if (d != 0)
        {
            mSnake.setDirection(direction.setPosition((x - oX) / d, (y - oY) / d));
        }
    }

    private void setIndicator(float x, float y)
    {
        float oX = directionButton.getWidth() / 2;
        float oY = directionButton.getHeight() / 2;
        float r = directionButton.getWidth() / 2 - directionIndicator.getWidth() / 2;
        float d = Constants.getDistance(oX, oY, x, y);
        if (d != 0)
        {
            directionIndicator.setTranslationX(r * (x - oX) / d);
            directionIndicator.setTranslationY((r * (y - oY) / d));
        }
    }
}
