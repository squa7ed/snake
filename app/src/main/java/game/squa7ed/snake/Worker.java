package game.squa7ed.snake;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
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
    private final TextView textViewSnakeLength;
    private final TextView textViewKillCount;
    private final TextView[] rankNames;
    private final TextView[] rankLengths;
    private final SurfaceHolder surfaceHolder;
    private final Paint paint;
    private boolean running;
    private float left;
    private float top;

    Worker(Activity activity, GameField field)
    {
        this.field = field;
        snakes = field.getSnakes();
        foods = field.getFoods();
        mSnake = field.getSnake();
        dIndicator = (ImageView) activity.findViewById(R.id.image_view_direction_indicator);
        sIndicator = (ImageView) activity.findViewById(R.id.image_view_speed_indicator);
        textViewSnakeLength = (TextView) activity.findViewById(R.id.text_snake_length);
        textViewKillCount = (TextView) activity.findViewById(R.id.text_kill_count);
        rankNames = new TextView[]
                {
                        (TextView) activity.findViewById(R.id.rank_list_name_1),
                        (TextView) activity.findViewById(R.id.rank_list_name_2),
                        (TextView) activity.findViewById(R.id.rank_list_name_3),
                        (TextView) activity.findViewById(R.id.rank_list_name_4),
                        (TextView) activity.findViewById(R.id.rank_list_name_5),
                        };
        rankLengths = new TextView[]
                {
                        (TextView) activity.findViewById(R.id.rank_list_length_1),
                        (TextView) activity.findViewById(R.id.rank_list_length_2),
                        (TextView) activity.findViewById(R.id.rank_list_length_3),
                        (TextView) activity.findViewById(R.id.rank_list_length_4),
                        (TextView) activity.findViewById(R.id.rank_list_length_5),
                        };
        surfaceHolder = ((GameView) activity.findViewById(R.id.gameView)).getHolder();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    public void run()
    {
        long st, tt = 0;
        while (running)
        {
            Log.d(TAG, "run: thread sleep for " + (System.currentTimeMillis() - tt) + " ms.");
            st = System.currentTimeMillis();
            tt = System.currentTimeMillis();
            // Game logic
            logic();
            Log.d(TAG, "run: logic in " + (System.currentTimeMillis() - tt) + " ms.");
            tt = System.currentTimeMillis();
            // Update layout, set rank, snake length, kill count etc.
            update();
            Log.d(TAG, "run: update in " + (System.currentTimeMillis() - tt) + " ms.");
            tt = System.currentTimeMillis();
            // Draw game field, foods, snakes.
            draw();
            Log.d(TAG, "run: draw in " + (System.currentTimeMillis() - tt) + " ms.");
            try
            {
                tt = System.currentTimeMillis();
                Thread.sleep(Math.max(0, Constants.THREAD_SLEEP - System.currentTimeMillis() + st));
            } catch (InterruptedException e)
            {
                Log.e(TAG, "run: ", e);
            }
        }
    }

    private void logic()
    {
        field.move();
        field.check();
        field.rank();
    }

    private void update()
    {
        setRank();
        setLengthAndKillCount();
    }

    private void setRank()
    {
        Message msg = new Message();
        msg.what = Constants.MSG_SET_RANK;
        Bundle data = new Bundle();
        ArrayList<CharSequence> names = new ArrayList<>(5);
        ArrayList<CharSequence> length = new ArrayList<>(5);
        for (int i = 0; i < 5; i++)
        {
            names.add(snakes.get(i).getName());
            length.add(String.valueOf(snakes.get(i).getLength()));
        }
        data.putCharSequenceArrayList(Constants.RANK_SNAKE_NAMES, names);
        data.putCharSequenceArrayList(Constants.RANK_SNAKE_LENGTH, length);
        msg.setData(data);
        synchronized (this) {sendMessage(msg);}
    }

    private void setLengthAndKillCount()
    {
        Message msg = new Message();
        msg.what = Constants.MSG_LENGTH_AND_KILL_COUNT;
        Bundle data = new Bundle();
        data.putInt(Constants.LENGTH, mSnake.getLength());
        data.putInt(Constants.KILL_COUNT, mSnake.getKillCount());
        msg.setData(data);
        synchronized (this) {sendMessage(msg);}
    }

    private void draw()
    {
        Canvas canvas;
        synchronized (surfaceHolder)
        {
            if ((canvas = surfaceHolder.lockCanvas()) != null)
            {
                translate(canvas);
                drawLayer(canvas);
                drawList(foods, canvas);
                for (Snake snake : snakes)
                {
                    drawList(snake, canvas);
                    if (snake.isAlive() && needDrawing(snake.peekFirst()))
                    {
                        paint.setTextAlign(Paint.Align.CENTER);
                        paint.setTextSize(Constants.snakeBodySize * 2 / 3);
                        Node head = snake.peekFirst();
                        paint.setColor(Constants.SNAKE_NAME_FONT_COLOR);
                        canvas.drawText(snake.getName(), head.getX(), head.getY() - head.getSize(), paint);
                        paint.setColor(Color.WHITE);
                        canvas.drawCircle(head.getX(), head.getY(), head.getSize() / 3, paint);
                        paint.setColor(Color.BLACK);
                        canvas.drawCircle(head.getX(), head.getY(), head.getSize() / 4, paint);
                    }
                }
                surfaceHolder.unlockCanvasAndPost(canvas);
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

    private void drawLayer(Canvas canvas)
    {
        //        Draw blank areas.
        paint.setColor(Constants.BLANK_ZONE_BACKGROUND_COLOR);
        //         Upper blank area.
        if (top < Constants.fieldTop)
        {
            canvas.drawRect(0, 0, Constants.viewWidth, Constants.blankHeight, paint);
        }
        //        Right blank area.
        if (left + Constants.screenWidth > Constants.fieldRight)
        {
            canvas.drawRect(Constants.fieldRight,
                            Constants.blankHeight,
                            Constants.viewWidth,
                            Constants.viewHeight, paint);
        }
        //        Bottom blank area.
        if (top + Constants.screenHeight > Constants.fieldBottom)
        {
            canvas.drawRect(0, Constants.fieldBottom, Constants.fieldRight, Constants.viewHeight, paint);
        }
        //        Left blank area.
        if (left < Constants.fieldLeft)
        {
            canvas.drawRect(0, Constants.blankHeight, Constants.fieldLeft, Constants.fieldBottom, paint);
        }
        //        Draw field.
        paint.setColor(Constants.FIELD_BACKGROUND_COLOR);
        canvas.drawRect(left < Constants.fieldLeft ? Constants.fieldLeft : left,
                        top < Constants.fieldTop ? Constants.fieldTop : top,
                        left + Constants.screenWidth < Constants.fieldRight ? left + Constants.screenWidth : Constants.fieldRight,
                        top + Constants.screenHeight < Constants.fieldBottom ? top + Constants.screenHeight : Constants.fieldBottom,
                        paint);
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
    }

    private void drawList(List<Node> list, Canvas canvas)
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
        return node.getX() >= left - node.getSize() + node.getSize() &&
               node.getX() <= left + Constants.screenWidth &&
               node.getY() >= top - node.getSize() &&
               node.getY() <= top + Constants.screenHeight + node.getSize();
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
        switch (msg.what)
        {
            case Constants.MSG_LENGTH_AND_KILL_COUNT:
                setLengthAndKillCount(msg);
                break;
            case Constants.MSG_SET_RANK:
                setRank(msg);
                break;
            default:
                super.dispatchMessage(msg);
                break;
        }
    }

    private void setRank(Message msg)
    {
        Bundle bundle = msg.getData();
        ArrayList<CharSequence> names = bundle.getCharSequenceArrayList(Constants.RANK_SNAKE_NAMES);
        ArrayList<CharSequence> length = bundle.getCharSequenceArrayList(Constants.RANK_SNAKE_LENGTH);
        if (length != null && names != null)
        {
            for (int i = 0; i < 5; i++)
            {
                rankNames[i].setText(names.get(i));
                rankLengths[i].setText(length.get(i));
            }
        }
    }

    private void setLengthAndKillCount(Message msg)
    {
        Bundle bundle = msg.getData();
        int length = bundle.getInt(Constants.LENGTH);
        int killCount = bundle.getInt(Constants.KILL_COUNT);
        textViewSnakeLength.setText(String.valueOf(length));
        textViewKillCount.setText(String.valueOf(killCount));
    }
}
