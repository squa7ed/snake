package game.squa7ed.snake;

import android.graphics.Color;

/**
 * Created by Squa7ed on 16-12-7.
 * Constant settings, common methods.
 */
@SuppressWarnings("WeakerAccess")
public class Constants
{
    public static final String DEBUG_TAG = "SQUA7ED-";
    public static final int BACKGROUND_COLOR = Color.DKGRAY;
    public static final int FIELD_BACKGROUND_COLOR = Color.WHITE;
    public static final int FIELD_LINE_COLOR = Color.LTGRAY;
    public static final int SNAKE_LENGTH = 30;
    public static final int GROW_PER_FOOD = 5;
    public static final int SNAKE_SIZE = SNAKE_LENGTH / GROW_PER_FOOD;
    public static final int FOOD_COUNT = 50;
    public static final int AI_SNAKE_COUNT = 14;
    public static final int SNAKE_BODY_FOOD_VALUE = GROW_PER_FOOD;
    public static final int DEFAULT_SPEED = 1;
    public static final int ACCELERATE = 2;
    public static final long SLEEP = 100;
    public static final int AI_CHECK_COUNT = 5;
    public static final String SNAKE_NAME = "Squa7ed";
    public static int screenWidth = 0;
    public static int screenHeight = 0;
    public static int snakeBodySize = 0;
    public static int snakeMoveDistance = 0;
    public static int deadSnakeBodySize = 0;
    public static int foodSize = 0;
    public static int viewWidth = 0;
    public static int viewHeight = 0;
    public static int blankWidth = 0;
    public static int blankHeight = 0;
    public static int fieldLeft = 0;
    public static int fieldTop = 0;
    public static int fieldRight = 0;
    public static int fieldBottom = 0;
    public static int fieldWidth = 0;
    public static int fieldHeight = 0;
    public static int gap = 0;

    public static void setParams(int width, int height)
    {
        screenWidth = width;
        screenHeight = height;
        snakeBodySize = Math.min(screenWidth, screenHeight) / 50;
        snakeMoveDistance = snakeBodySize * 2 / 3;
        deadSnakeBodySize = snakeBodySize * 2 / 3;
        foodSize = snakeBodySize / 2;
        viewWidth = screenWidth * 2;
        viewHeight = screenHeight * 2;
        blankWidth = screenWidth / 4;
        blankHeight = screenHeight / 4;
        fieldWidth = viewWidth - 2 * blankWidth;
        fieldHeight = viewHeight - 2 * blankHeight;
        fieldLeft = blankWidth;
        fieldTop = blankHeight;
        fieldRight = blankWidth + fieldWidth;
        fieldBottom = blankHeight + fieldHeight;
        gap = snakeBodySize;
    }

    public static float getDistanceBetween(Point a, Point b)
    {
        return (float) Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
    }

    public static float getDistance(float x1, float y1, float x2, float y2)
    {
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }
}