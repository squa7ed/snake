package game.squa7ed.snake;

import java.util.LinkedList;

/**
 * Created by Squa7ed on 16-12-7.
 * Snake.
 */
class Snake extends LinkedList<Node> {
    private final String name;
    private final int id;
    private final boolean isAi;
    private int length;
    private int killCount;
    private int speed;
    private boolean hunting;
    private Point direction;

    Snake(String name, int id, int color, boolean isAi, Point head, Point direction) {
        super();
        this.name = name;
        this.id = id;
        this.isAi = isAi;
        this.length = Constants.DEFAULT_SNAKE_LENGTH;
        this.killCount = 0;
        this.speed = Constants.DEFAULT_SPEED;
        this.hunting = false;
        this.direction = new Point(0,0);
        setDirection(direction);
        for (int i = 0; i < Constants.DEFAULT_SNAKE_SIZE; i++) {
            add(new Node(head.getX() + i * this.direction.getX(),
                    head.getY() + i * this.direction.getY(),
                    Constants.snakeBodySize, color));
        }
    }

    void move() {
        for (int i = 0; i < speed; i++) {
            peekLast().setPosition(peekFirst().getX() + direction.getX(),
                    peekFirst().getY() + direction.getY());
            addFirst(removeLast());
        }
    }

    void eat(Node food) {
        if (food.getSize() == Constants.foodSize) {
            length++;
            if (length % Constants.GROW_PER_FOOD == 0) {
                add(new Node(peekLast()));
            }
        } else {
            length += Constants.SNAKE_BODY_FOOD_VALUE;
            add(food.clone(peekLast()));
        }
    }

    void die() {
        length = 0;
        for (Node body : this) {
            body.setSize(Constants.deadSnakeBodySize);
        }
    }

    String getName() {
        return name;
    }

    int getId() {
        return id;
    }

    boolean isAi() {
        return isAi;
    }

    boolean isAlive() {
        return length > 0;
    }

    int getLength() {
        return length;
    }

    int getKillCount() {
        return killCount;
    }

    void setSpeed(int speed) {
        this.speed = speed;
    }

    void setKillCount() {
        killCount++;
    }

    Point getDirection() {
        return direction;
    }

    void setDirection(Point direction) {
        setDirection(direction.getX(), direction.getY());
    }


    //TODO Make snakes unable to do sharp turns.
    void setDirection(float cos, float sin) {
        this.direction.setPosition(cos * Constants.snakeBodyDistance, sin * Constants.snakeBodyDistance);
    }

    void hunt(Snake snake) {
        if (snake == null || snake.isEmpty()) {
            hunting = false;
            speed = Constants.DEFAULT_SPEED;
            return;
        }
        hunting = true;
        speed = Constants.ACCELERATE;
        Node food = snake.iterator().next();
        Node head = peekFirst();
        float d = Constants.getDistanceBetween(head, food);
        setDirection((food.getX() - head.getX()) / d,
                (food.getY() - head.getY()) / d);
    }

    boolean isHunting() {
        return hunting;
    }
}
