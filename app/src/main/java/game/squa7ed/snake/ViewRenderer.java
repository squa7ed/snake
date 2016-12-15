package game.squa7ed.snake;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.List;

/**
 * Created by squa7ed on 16-12-13.
 * Renderer thread for surfaceView.
 */
class ViewRenderer extends Thread
{
    private static final String TAG = Constants.DEBUG_TAG + "ViewRenderer";
    private final GameField field;
    private final SurfaceHolder surfaceHolder;
    private final Paint paint;
    private final List<Node> foods;
    private final List<Snake> snakes;
    private boolean running;
    private float left;
    private float top;

    ViewRenderer(GameField field, SurfaceHolder surfaceHolder)
    {
        this.field = field;
        this.surfaceHolder = surfaceHolder;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(Constants.snakeBodySize);
        foods = field.getFoods();
        snakes = field.getSnakes();
    }

    @Override
    public void run()
    {
        long t;
        Canvas canvas;
        while (running)
        {
            synchronized (surfaceHolder)
            {
                canvas = surfaceHolder.lockCanvas();
                if (canvas != null)
                {
                    synchronized (field)
                    {
                        t = System.currentTimeMillis();
                        translate(canvas);
                        drawLayer(canvas);
                        drawList(foods, canvas);
                        for (Snake snake : snakes)
                        {
                            drawList(snake, canvas);
                            if (snake.isAlive() && needDrawing(snake.peekFirst()))
                            {
                                Node head = snake.peekFirst();
                                paint.setColor(Color.BLACK);
                                canvas.drawText(snake.getName(), head.getX(), head.getY() - head.getSize(), paint);
                                paint.setColor(Color.WHITE);
                                canvas.drawCircle(head.getX(), head.getY(), head.getSize() / 3, paint);
                                paint.setColor(Color.BLACK);
                                canvas.drawCircle(head.getX(), head.getY(), head.getSize() / 4, paint);
                            }
                        }
                    }
                    surfaceHolder.unlockCanvasAndPost(canvas);
                    Log.d(TAG, "run: calculation and drawing are done in " + (System.currentTimeMillis() - t) + "ms.");
                }
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

    private void translate(Canvas canvas)
    {
        // !! VERY IMPORTANT TO SET HEAD!!
        if (field.getSnake().isAlive())
        {
            Node head;
            if ((head = field.getSnake().peekFirst()) != null)
            {
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
        }
        canvas.translate(-left, -top);
    }

    private void drawLayer(Canvas canvas)
    {
        //        Draw blank areas.
        paint.setColor(Constants.BACKGROUND_COLOR);
        //         Upper blank area.
        canvas.drawRect(0, 0, Constants.viewWidth, Constants.blankHeight, paint);
        //        Right blank area.
        canvas.drawRect(Constants.fieldRight,
                        Constants.blankHeight,
                        Constants.viewWidth,
                        Constants.viewHeight, paint);
        //        Bottom blank area.
        canvas.drawRect(0, Constants.fieldBottom, Constants.fieldRight, Constants.viewHeight, paint);
        //        Left blank area.
        canvas.drawRect(0, Constants.blankHeight, Constants.fieldLeft, Constants.fieldBottom, paint);
        //        Draw field.
        paint.setColor(Constants.FIELD_BACKGROUND_COLOR);
        canvas.drawRect(Constants.fieldLeft,
                        Constants.fieldTop,
                        Constants.fieldRight,
                        Constants.fieldBottom, paint);
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
        return node.getX() >= left - node.getSize() / 2 &&
               node.getX() <= left + Constants.screenWidth &&
               node.getY() >= top - node.getSize() / 2 &&
               node.getY() <= top + Constants.screenHeight;
    }

    boolean isRunning()
    {
        return running;
    }

    void setRunning(boolean running)
    {
        this.running = running;
    }
}