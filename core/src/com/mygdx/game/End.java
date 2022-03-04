package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;

public class End implements Screen {

    final Drop game;
    OrthographicCamera camera;
    Texture background;
    int score;
    int health;

    End(final Drop gam, int score, int health){
        game = gam;
        this.score = score;
        this.health = health;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);
    }
    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        if (this.score >= 5000 && this.health>0){
            background = new Texture("picture/w.jpg");
        } else {
            background = new Texture("picture/l.jpg");

        }
        game.batch.draw(background, 0, 0);
        game.batch.end();
        if (Gdx.input.isTouched()) {
            game.setScreen(new MainMenu(game));
            dispose();
        }

    }
    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}