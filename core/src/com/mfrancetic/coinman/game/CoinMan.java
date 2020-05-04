package com.mfrancetic.coinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class CoinMan extends ApplicationAdapter {
    SpriteBatch batch;
    Texture background;
    Texture[] man;
    int manState;
    int pause = 0;

    // how fast the player is falling
    float gravity = 0.2f;
    float velocity = 0;

    int manY = 0;

    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("bg.png");
        man = new Texture[4];
        man[0] = new Texture("frame-1.png");
        man[1] = new Texture("frame-2.png");
        man[2] = new Texture("frame-3.png");
        man[3] = new Texture("frame-4.png");

        manY = Gdx.graphics.getHeight() / 2;
    }

    @Override
    public void render() {

        startDrawing();
        drawBackground();

        jumpIfTheScreenHasBeenTouched();
        run();
        fallDown();

        drawPlayer();
        endDrawing();
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
        // position the man in the center of the screen
        batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
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
}