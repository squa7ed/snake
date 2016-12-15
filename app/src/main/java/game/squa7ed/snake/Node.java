package game.squa7ed.snake;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

/**
 * Created by Squa7ed on 16-12-7.
 * Class for snake body and food.
 */
class Node extends Point
{
    private int size;
    private int color;

    Node(float x, float y, int size, int color)
    {
        super(x, y);
        this.size = size;
        this.color = color;
    }

    Node(Node node)
    {
        this(node.getX(), node.getY(), node.getSize(), node.getColor());
    }

    int getSize()
    {
        return size;
    }

    void setSize(int size)
    {
        if (size > 0)
        {
            this.size = size;
        }
    }

    int getColor()
    {
        return color;
    }

    boolean collidesWith(@NonNull Node node, boolean isFood)
    {
        int correction = 0;
        if (isFood)
        {
            correction = this.size / 2;
        }
        return Constants.getDistanceBetween(this, node) <= this.size / 2 + node.getSize() / 2 + correction;
    }

    Node clone(Node node)
    {
        this.setPosition(node.getX(), node.getY());
        this.size = node.getSize();
        this.color = node.getColor();
        return this;
    }

    void draw(Canvas canvas, Paint paint)
    {
        paint.setColor(color);
        canvas.drawCircle(getX(), getY(), size / 2, paint);
    }

    void setColor(int color)
    {
        this.color = color;
    }
}
