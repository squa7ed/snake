package game.squa7ed.snake;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Squa7ed on 16-12-4.
 * Game field model.
 */
class GameField
{
    private static final String TAG = Constants.DEBUG_TAG + "GameField";
    private int id;
    private final List<Snake> snakes;
    private final List<Node> foods;
    private final Point direction;
    private final Point position;
    private final Snake mSnake;
    private final Node tNode;

    GameField()
    {
        id = 0;
        snakes = new LinkedList<>();
        foods = new ArrayList<>(Constants.FOOD_COUNT);
        direction = new Point(0, 0);
        position = new Point(0, 0);
        mSnake = new Snake(Constants.DEFAULT_SNAKE_NAME, id, getRandomColor(), false, getRandomPosition(), getRandomDirection());
        tNode = new Node(mSnake.peekFirst());
        snakes.add(mSnake);
        makeupAISnakes(Constants.AI_SNAKE_COUNT);
        initFood();
    }

    private void makeupAISnakes(int count)
    {
        for (int i = 0; i < count; i++)
        {
            id++;
            snakes.add(new Snake("AI-" + id, id, getRandomColor(), true, getRandomPosition(), getRandomDirection()));
        }
    }

    private void initFood()
    {
        for (int i = 0; i < Constants.FOOD_COUNT; i++)
        {
            addFood();
        }
    }

    private void addFood()
    {
        Point position = getRandomPosition();
        Node food = new Node(position.getX(), position.getY(), Constants.foodSize, getRandomColor());
        while (hasCollision(food, foods, false))
        {
            position = getRandomPosition();
            food.setPosition(position.getX(), position.getY());
        }
        foods.add(food);
    }

    private Point getRandomPosition()
    {
        return position.setPosition((float) (Math.random() * (Constants.fieldWidth - 2 * Constants.snakeBodySize)) + Constants.fieldLeft + 2 * Constants.snakeBodySize,
                                    (float) (Math.random() * (Constants.fieldHeight - 2 * Constants.snakeBodySize)) + Constants.fieldTop + 2 * Constants.snakeBodySize);
    }

    void move()
    {
        for (Snake snake : snakes)
        {
            if (snake.isAlive())
            {
                if (snake.isAi())
                {
                    checkForDirection(snake);
                }
                snake.move();
            }
        }
    }

    private void checkForDirection(Snake snake)
    {
        int cnt = 0;
        Point d = snake.getDirection();
        Node head = snake.peekFirst();
        tNode.clone(head);
        while (cnt < Constants.AI_DIRECTION_CHECK_COUNT)
        {
            cnt++;
            //            10% chance of changing current direction if int's not hunting.
            if (Math.random() > 0.9 && !snake.isHunting())
            {
                snake.setDirection(getRandomDirection());
            }
            tNode.setPosition(head.getX() + d.getX(), head.getY() + d.getY());
            if (!isValid(tNode))
            {
                snake.setDirection(getRandomDirection());
                cnt = 0;
                continue;
            }
            boolean hasCollision = false;
            for (Snake tSnake : snakes)
            {
                if (tSnake.getId() != snake.getId())
                {
                    if (hasCollision(tNode, tSnake, false))
                    {
                        if (tSnake.isAlive())
                        {
                            snake.setDirection(getRandomDirection());
                            hasCollision = true;
                            break;
                        } else
                        {
                            break;
                        }
                    }
                }
            }
            if (!hasCollision)
            {
                break;
            }
        }
    }

    void check()
    {
        int dCnt = checkForSnakes();
        checkForFood();
        makeupAISnakes(dCnt);
    }

    private int checkForSnakes()
    {
        // dead snake count holder.
        int cnt = 0;
        Node head;
        // Check for walls.
        for (Snake snake : snakes)
        {
            head = snake.peekFirst();
            if (snake.isAlive())
            {
                if (!isValid(head))
                {
                    snake.die();
                    cnt++;
                }
            }
        }
        // Check for other snakes.
        for (Snake snake : snakes)
        {
            head = snake.peekFirst();
            if (snake.isAlive())
            {
                for (Snake tSnake : snakes)
                {
                    if (snake.getId() != tSnake.getId())
                    {
                        if (tSnake.isAlive())
                        {
                            if (hasCollision(head, tSnake, false))
                            {
                                snake.die();
                                cnt++;
                                tSnake.setKillCount();
                            }
                        } else
                        {
                            Iterator<Node> it = tSnake.iterator();
                            while (it.hasNext())
                            {
                                Node body = it.next();
                                if (head.collidesWith(body, true))
                                {
                                    snake.eat(body);
                                    it.remove();
                                    snake.hunt(tSnake);
                                }
                            }
                        }
                    }
                }
            }
        }
        return cnt;
    }

    private boolean isValid(Node node)
    {
        return node.getX() - node.getSize() / 2 > Constants.fieldLeft &&
               node.getX() + node.getSize() / 2 < Constants.fieldRight &&
               node.getY() - node.getSize() / 2 > Constants.fieldTop &&
               node.getY() + node.getSize() / 2 < Constants.fieldBottom;
    }

    private boolean hasCollision(Node node, List<Node> list, boolean isFood)
    {
        for (Node tNode : list)
        {
            if (!node.equals(tNode) && node.collidesWith(tNode, isFood))
            {
                return true;
            }
        }
        return false;
    }

    private void checkForFood()
    {
        // Check if any snake ate food.
        for (Snake snake : snakes)
        {
            if (snake.isAlive())
            {
                for (Node food : foods)
                {
                    if (snake.peekFirst().collidesWith(food, true))
                    {
                        snake.eat(food);
                        shuffleFood(food);
                    }
                }
            }
        }
    }

    private void shuffleFood(Node food)
    {
        Point p = getRandomPosition();
        food.setPosition(p.getX(), p.getY());
        while (hasCollision(food, foods, false))
        {
            p = getRandomPosition();
            food.setPosition(p.getX(), p.getY());
        }
        food.setColor(getRandomColor());
    }

    private int getRandomColor()
    {
        switch ((int) (Math.random() * Integer.MAX_VALUE) % 10)
        {
            case 1:
                return Color.BLACK;
            case 2:
                return Color.BLUE;
            case 3:
                return Color.CYAN;
            case 4:
                return Color.DKGRAY;
            case 5:
                return Color.GRAY;
            case 6:
                return Color.GREEN;
            case 7:
                return Color.MAGENTA;
            case 8:
                return Color.RED;
            case 9:
                return Color.YELLOW;
        }
        return Color.MAGENTA;
    }

    private Point getRandomDirection()
    {
        float cos = (float) Math.random();
        float sin = (float) Math.sqrt(1 - Math.pow(cos, 2));
        if (Math.random() > 0.5)
        {
            cos = -cos;
        }
        if (Math.random() > 0.5)
        {
            sin = -sin;
        }
        direction.setPosition(cos, sin);
        return direction;
    }

    Snake getSnake()
    {
        return mSnake;
    }

    List<Node> getFoods()
    {
        return foods;
    }

    List<Snake> getSnakes()
    {
        return snakes;
    }

    void rank()
    {
        Collections.sort(snakes, new Comparator<Snake>()
        {
            @Override
            public int compare(Snake aSnake, Snake bSnake)
            {
                return bSnake.getLength() - aSnake.getLength();
            }
        });
    }
}

