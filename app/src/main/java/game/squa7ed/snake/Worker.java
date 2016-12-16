package game.squa7ed.snake;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by squa7ed on 16-12-10.
 * Game logic slash controller.
 */
class Worker extends Handler implements View.OnTouchListener, Runnable
{
    private static final String TAG = Constants.DEBUG_TAG + "Worker";
    private final GameField field;
    private final List<Snake> snakes;
    private final List<Node> foods;
    private final Snake mSnake;
    private final ImageView dIndicator;
    private final ImageView sIndicator;
    private final SurfaceHolder surfaceHolder;
    private boolean running;
    private float left, top;

    Worker(Activity activity, GameField field)
    {
        this.field = field;
        snakes = field.getSnakes();
        foods = field.getFoods();
        mSnake = field.getSnake();
        dIndicator = (ImageView) activity.findViewById(R.id.image_view_direction_indicator);
        sIndicator = (ImageView) activity.findViewById(R.id.image_view_speed_indicator);
        surfaceHolder = ((GameView) activity.findViewById(R.id.gameView)).getHolder();
    }

    @Override
    public void run()
    {
        long t, tt = 0L, st;
        Canvas canvas;
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(Constants.snakeBodySize);
        while (running)
        {
            Log.d(TAG, "run: thread sleep for " + (System.currentTimeMillis() - tt) + " ms.");
            st = System.currentTimeMillis();
            field.move();
            field.check();
            Log.d(TAG, "run: game logic in " + (System.currentTimeMillis() - st) + " ms.");
            synchronized (surfaceHolder)
            {
                if ((canvas = surfaceHolder.lockCanvas()) != null)
                {
                    t = System.currentTimeMillis();
                    tt = t;
                    translate(canvas);
                    Log.d(TAG, "run: draw translate in " + (System.currentTimeMillis() - tt) + " ms.");
                    tt = System.currentTimeMillis();
                    drawLayer(canvas, paint);
                    Log.d(TAG, "run: draw layer in " + (System.currentTimeMillis() - tt) + " ms.");
                    tt = System.currentTimeMillis();
                    drawList(foods, canvas, paint);
                    Log.d(TAG, "run: draw foods in " + (System.currentTimeMillis() - tt) + " ms.");
                    tt = System.currentTimeMillis();
                    for (Snake snake : snakes)
                    {
                        drawList(snake, canvas, paint);
                        if (snake.isAlive() && needDrawing(snake.peekFirst()))
                        {
                            Node head = snake.peekFirst();
                            paint.setColor(Constants.SNAKE_NAME_FONT_COLOR);
                            canvas.drawText(snake.getName(), head.getX(), head.getY() - head.getSize(), paint);
                            paint.setColor(Color.WHITE);
                            canvas.drawCircle(head.getX(), head.getY(), head.getSize() / 3, paint);
                            paint.setColor(Color.BLACK);
                            canvas.drawCircle(head.getX(), head.getY(), head.getSize() / 4, paint);
                        }
                        Log.d(TAG, "run: draw snakes in " + (System.currentTimeMillis() - tt) + " ms.");
                    }
                    surfaceHolder.unlockCanvasAndPost(canvas);
                    Log.d(TAG, "run: draw all in " + (System.currentTimeMillis() - t) + " ms.");
                    Log.d(TAG, "run: all done in " + (System.currentTimeMillis() - st) + " ms.");
                }
            }
            try
            {
                tt = System.currentTimeMillis();
                Thread.sleep(Math.max(0, Constants.SLEEP - System.currentTimeMillis() + st));
            } catch (InterruptedException e)
            {
                Log.e(TAG, "run: ", e);
            }
        }
    }

    private void translate(Canvas canvas)
    {
        // !! VERY IMPORTANT TO SET HEAD!!
        if (field.getSnake().isAlive())
        {
            Node head = field.getSnake().peekFirst();
            if (head.getX() - Constants.screenWidth / 2 <= 0)
            {
                left = 0;
            } else if (head.getX() + Constants.screenWidth / 2 >= Constants.viewWidth)
            {
                left = Constants.viewWidth - Constants.screenWidth;
            } else
            {
                left = head.getX() - Constants.screenWidth / 2;
            }
            if (head.getY() - Constants.screenHeight / 2 <= 0)
            {
                top = 0;
            } else if (head.getY() + Constants.screenHeight / 2 >= Constants.viewHeight)
            {
                top = Constants.viewHeight - Constants.screenHeight;
            } else
            {
                top = head.getY() - Constants.screenHeight / 2;
            }
        }
        canvas.translate(-left, -top);
    }

    private void drawLayer(Canvas canvas, Paint paint)
    {
        long t = System.currentTimeMillis();
        //        Draw blank areas.
        paint.setColor(Constants.BACKGROUND_COLOR);
        //         Upper blank area.
        if (top < Constants.fieldTop)
        {
            canvas.drawRect(0, 0, Constants.viewWidth, Constants.blankHeight, paint);
        }
        Log.d(TAG, "drawLayer: draw upper in " + (System.currentTimeMillis() - t) + " ms.");
        t = System.currentTimeMillis();
        //        Right blank area.
        if (left + Constants.screenWidth > Constants.fieldRight)
        {
            canvas.drawRect(Constants.fieldRight,
                            Constants.blankHeight,
                            Constants.viewWidth,
                            Constants.viewHeight, paint);
        }
        Log.d(TAG, "drawLayer: draw right in " + (System.currentTimeMillis() - t) + " ms.");
        t = System.currentTimeMillis();
        //        Bottom blank area.
        if (top + Constants.screenHeight > Constants.fieldBottom)
        {
            canvas.drawRect(0, Constants.fieldBottom, Constants.fieldRight, Constants.viewHeight, paint);
        }
        Log.d(TAG, "drawLayer: draw bottom in " + (System.currentTimeMillis() - t) + " ms.");
        t = System.currentTimeMillis();
        //        Left blank area.
        if (left < Constants.fieldLeft)
        {
            canvas.drawRect(0, Constants.blankHeight, Constants.fieldLeft, Constants.fieldBottom, paint);
        }
        Log.d(TAG, "drawLayer: draw left in " + (System.currentTimeMillis() - t) + " ms.");
        //        Draw field.
        paint.setColor(Constants.FIELD_BACKGROUND_COLOR);
        t = System.currentTimeMillis();
        canvas.drawRect(left < Constants.fieldLeft ? Constants.fieldLeft : left,
                        top < Constants.fieldTop ? Constants.fieldTop : top,
                        left + Constants.screenWidth < Constants.fieldRight ? left + Constants.screenWidth : Constants.fieldRight,
                        top + Constants.screenHeight < Constants.fieldBottom ? top + Constants.screenHeight : Constants.fieldBottom,
                        paint);
        Log.d(TAG, "drawLayer: draw field in " + (System.currentTimeMillis() - t) + " ms.");
        //        Draw field lines.
        int x = (Constants.fieldWidth % Constants.gap) / 2;
        int y = (Constants.fieldHeight % Constants.gap) / 2;
        paint.setColor(Constants.FIELD_LINE_COLOR);
        //        Draw vertical lines.
        for (x += Constants.fieldLeft; x < Constants.fieldRight; x += Constants.gap)
        {
            canvas.drawLine(x, Constants.fieldTop, x, Constants.fieldBottom, paint);
        }
        //        Draw horizontal lines.
        for (y += Constants.blankHeight; y < Constants.blankHeight + Constants.fieldHeight; y += Constants.gap)
        {
            canvas.drawLine(Constants.fieldLeft, y, Constants.fieldRight, y, paint);
        }
        Log.d(TAG, "drawLayer: draw lines in " + (System.currentTimeMillis() - t) + " ms.");
    }

    private void drawList(List<Node> list, Canvas canvas, Paint paint)
    {
        for (Node node : list)
        {
            if (needDrawing(node))
            {
                node.draw(canvas, paint);
            }
        }
    }

    private boolean needDrawing(Node node)
    {
        return node.getX() >= left - node.getSize() / 2 &&
               node.getX() <= left + Constants.screenWidth &&
               node.getY() >= top - node.getSize() / 2 &&
               node.getY() <= top + Constants.screenHeight;
    }

    void setRunning(boolean running)
    {
        this.running = running;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        if (v.getId() == R.id.image_view_speed_background)
        {
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    sIndicator.setBackgroundResource(R.drawable.speed_indicator_pressed);
                    mSnake.setSpeed(Constants.ACCELERATE);
                    return true;
                case MotionEvent.ACTION_UP:
                    sIndicator.setBackgroundResource(R.drawable.speed_indicator);
                    mSnake.setSpeed(Constants.DEFAULT_SPEED);
                    return false;
            }
        }
        if (v.getId() == R.id.image_view_direction_background)
        {
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    dIndicator.setBackgroundResource(R.drawable.direction_indicator_pressed);
                    setIndicator(event.getX(), event.getY());
                    return true;
                case MotionEvent.ACTION_MOVE:
                    setIndicator(event.getX(), event.getY());
                    return true;
                case MotionEvent.ACTION_UP:
                    dIndicator.setBackgroundResource(R.drawable.direction_indicator);
                    dIndicator.setTranslationX(0);
                    dIndicator.setTranslationY(0);
                    return false;
            }
        }
        return false;
    }

    private void setIndicator(float x, float y)
    {
        float oX = dIndicator.getLeft() + dIndicator.getWidth() / 2;
        float oY = dIndicator.getTop() + dIndicator.getHeight() / 2;
        float r = dIndicator.getLeft();
        float d = Constants.getDistance(oX, oY, x, y);
        if (d != 0)
        {
            dIndicator.setTranslationX(r * (x - oX) / d);
            dIndicator.setTranslationY(r * (y - oY) / d);
            mSnake.setDirection((x - oX) / d, (y - oY) / d);
        }
    }

    @Override
    public void dispatchMessage(Message msg)
    {
        super.dispatchMessage(msg);
    }
}
