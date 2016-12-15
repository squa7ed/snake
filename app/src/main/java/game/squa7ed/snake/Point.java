package game.squa7ed.snake;

/**
 * Created by squa7ed on 16-12-9.
 * base class for nodes.
 */
class Point
{
    private float x;
    private float y;

    Point(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    float getX()
    {
        return x;
    }

    float getY()
    {
        return y;
    }

    Point setPosition(float x, float y)
    {
        this.x = x;
        this.y = y;
        return this;
    }
}
