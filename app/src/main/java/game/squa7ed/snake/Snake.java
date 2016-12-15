package game.squa7ed.snake;

import java.util.LinkedList;

/**
 * Created by Squa7ed on 16-12-7.
 * Snake.
 */
class Snake extends LinkedList<Node>
{
    private final String name;
    private final int id;
    private final boolean isAi;
    private boolean isAlive;
    private int length;
    private int killCount;
    private int speed;
    private Point direction;

    Snake(String name, int id, int color, boolean isAi, Point head, Point direction)
    {
        super();
        this.name = name;
        this.id = id;
        this.isAi = isAi;
        this.isAlive = true;
        this.length = Constants.SNAKE_LENGTH;
        this.killCount = 0;
        this.speed = Constants.DEFAULT_SPEED;
        this.direction = new Point(0, 0);
        setDirection(direction);
        for (int i = 0; i < Constants.SNAKE_SIZE; i++)
        {
            add(new Node(head.getX() + i * this.direction.getX(),
                         head.getY() + i * this.direction.getY(),
                         Constants.snakeBodySize, color));
        }
    }

    void move()
    {
        for (int i = 0; i < speed; i++)
        {
            peekLast().setPosition(peekFirst().getX() + direction.getX(), peekFirst().getY() + direction.getY());
            addFirst(removeLast());
        }
    }

    void eat(Node food)
    {
        if (food.getSize() == Constants.foodSize)
        {
            length++;
            if (length % Constants.GROW_PER_FOOD == 0)
            {
                add(new Node(peekLast()));
            }
        } else
        {
            length += Constants.SNAKE_BODY_FOOD_VALUE;
            add(food.clone(peekLast()));
        }
    }

    void die()
    {
        isAlive = false;
        for (Node body : this)
        {
            body.setSize(Constants.deadSnakeBodySize);
        }
        //        Adjust head location if it's out of field.
        Node head = peekFirst();
        float x = head.getX(), y = head.getY();
        int radius = head.getSize() / 2;
        if (x - radius <= Constants.fieldLeft)
        {
            x += radius;
        } else if (x + radius >= Constants.fieldRight)
        {
            x -= radius;
        }
        if (y - radius <= Constants.fieldTop)
        {
            y += radius;
        } else if (y + radius >= Constants.fieldBottom)
        {
            y -= radius;
        }
        head.setPosition(x, y);
    }

    String getName() { return name; }

    int getId() { return id; }

    boolean isAi() { return isAi; }

    boolean isAlive() { return isAlive; }

    int getLength() { return length; }

    int getKillCount() { return killCount; }

    int getSpeed() { return speed; }

    void setSpeed(int speed) { this.speed = speed; }

    void setKillCount() { killCount++; }

    public Point getDirection()
    {
        return direction;
    }

    public void setDirection(Point direction)
    {
        this.direction.setPosition(direction.getX() * Constants.snakeMoveDistance,
                                   direction.getY() * Constants.snakeMoveDistance);
    }
}
