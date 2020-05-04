package com.mfrancetic.coinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
    SpriteBatch batch;
    Texture background;
    Texture[] man;
    Texture dizzyMan;
    int manState;
    int pause = 0;

    // how fast the player is falling
    float gravity = 0.2f;
    float velocity = 0;

    int manY = 0;
    int manX = 0;
    Rectangle playerRectangle;

    int score;
    BitmapFont font;

    Random random;

    int gameState;

    private static final int GAME_LIVE = 1;
    private static final int GAME_NOT_STARTED = 0;
    private static final int GAME_OVER = 2;

    // coins
    ArrayList<Integer> coinXs = new ArrayList<>();
    ArrayList<Integer> coinYs = new ArrayList<>();
    // we use them for collision check
    ArrayList<Rectangle> coinRectangles = new ArrayList<>();
    Texture coin;
    int coinCount;

    // bombs
    ArrayList<Integer> bombXs = new ArrayList<>();
    ArrayList<Integer> bombYs = new ArrayList<>();
    // we use them for collision check
    ArrayList<Rectangle> bombRectangles = new ArrayList<>();
    Texture bomb;
    int bombCount;

    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("bg.png");
        man = new Texture[4];
        man[0] = new Texture("frame-1.png");
        man[1] = new Texture("frame-2.png");
        man[2] = new Texture("frame-3.png");
        man[3] = new Texture("frame-4.png");

        dizzyMan = new Texture("dizzy-1.png");

        manY = Gdx.graphics.getHeight() / 2;

        coin = new Texture("coin.png");
        random = new Random();

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10);

        bomb = new Texture("bomb.png");

        playerRectangle = new Rectangle();
    }

    @Override
    public void render() {
        startDrawing();
        drawBackground();

        checkGameState();
        drawPlayer();
        checkCollisionWithCoin();
        checkCollisionWithBomb();
        drawScore();
        endDrawing();
    }

    private void checkGameState() {
        if (gameState == GAME_LIVE) {
            // game is live
            play();
        } else if (gameState == GAME_NOT_STARTED) {
            // game hasn't started yet
            checkIfGameStarted();
        } else if (gameState == GAME_OVER) {
            // game is over
            checkIfGameStarted();
            resetGame();
        }
    }

    private void checkIfGameStarted() {
        if (Gdx.input.justTouched()) {
            gameState = GAME_LIVE;
        }
    }

    private void play() {
        createBombs();
        createCoins();

        jumpIfTheScreenHasBeenTouched();
        run();
        fallDown();
    }

    private void resetGame() {
        manY = Gdx.graphics.getHeight() / 2;
        score = 0;
        velocity = 0;
        coinXs.clear();
        coinYs.clear();
        coinRectangles.clear();
        coinCount = 0;
        bombXs.clear();
        bombYs.clear();
        bombRectangles.clear();
        bombCount = 0;
    }

    private void drawScore() {
        // draw score in the bottom left corner
        font.draw(batch, String.valueOf(score), 100, 200);
    }

    private void createBombs() {
        // after every 250 times, create a bomb
        if (bombCount < 250) {
            bombCount++;
        } else {
            bombCount = 0;
            makeBomb();
        }
        // clear the bomb rectangles
        bombRectangles.clear();
        // draw all the bombs
        for (int i = 0; i < bombXs.size(); i++) {
            batch.draw(bomb, bombXs.get(i), bombYs.get(i));
            // we assure that the bombs slowly appear - but faster than the coins
            bombXs.set(i, bombXs.get(i) - 6);
            // create a new bomb rectangle
            bombRectangles.add(new Rectangle(bombXs.get(i), bombYs.get(i),
                    bomb.getWidth(), bomb.getHeight()));
        }
    }

    private void createCoins() {
        // after every 100 times, create a coin
        if (coinCount < 100) {
            coinCount++;
        } else {
            coinCount = 0;
            makeCoin();
        }
        // clear the coin rectangles
        coinRectangles.clear();
        // draw all the coins
        for (int i = 0; i < coinXs.size(); i++) {
            batch.draw(coin, coinXs.get(i), coinYs.get(i));
            // we assure that the coins slowly appear
            coinXs.set(i, coinXs.get(i) - 4);
            // create a new coin rectangle
            coinRectangles.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(),
                    coin.getHeight()));
        }
    }

    private void startDrawing() {
        // begin drawing
        batch.begin();
    }

    private void endDrawing() {
        // end drawing
        batch.end();
    }

    private void drawPlayer() {
        manX = Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2;
        // position the man in the center of the screen
        if (gameState == GAME_OVER) {
            batch.draw(dizzyMan, manX, manY);
        } else {
            batch.draw(man[manState], manX, manY);
            playerRectangle = new Rectangle(manX, manY, man[manState].getWidth(), man[manState].getHeight());
        }
    }

    private void fallDown() {
        // velocity = velocity + gravity
        velocity += gravity;
        // slowly the player will be falling down
        manY -= velocity;

        // stop falling at the bottom
        if (manY <= 0) {
            manY = 0;
        }
    }

    private void run() {
        // slows down the "running"
        if (pause < 8) {
            pause++;
        } else {
            pause = 0;
            // switch between the frames, so it looks like the man is running
            if (manState < 3) {
                manState++;
            } else {
                manState = 0;
            }
        }
    }

    private void jumpIfTheScreenHasBeenTouched() {
        // check if the screen has been touched
        if (Gdx.input.justTouched()) {
            // makes the player jump
            velocity = -10;
        }
    }

    private void drawBackground() {
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    private void makeCoin() {
        float coinHeight = random.nextFloat() * Gdx.graphics.getHeight();
        coinYs.add((int) coinHeight);
        coinXs.add(Gdx.graphics.getWidth());
    }

    private void makeBomb() {
        float bombHeight = random.nextFloat() * Gdx.graphics.getHeight();
        bombYs.add((int) bombHeight);
        bombXs.add(Gdx.graphics.getWidth());
    }

    private void checkCollisionWithCoin() {
        for (int i = 0; i < coinRectangles.size(); i++) {
            if (Intersector.overlaps(playerRectangle, coinRectangles.get(i))) {
                score++;
                // avoid colliding with the coin on and on again
                coinRectangles.remove(i);
                coinXs.remove(i);
                coinYs.remove(i);
                break;
            }
        }
    }

    private void checkCollisionWithBomb() {
        for (int i = 0; i < bombRectangles.size(); i++) {
            if (Intersector.overlaps(playerRectangle, bombRectangles.get(i))) {
                gameState = GAME_OVER;
            }
        }
    }
}