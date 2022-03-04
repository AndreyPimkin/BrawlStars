package com.mygdx.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.Iterator;
import java.util.Random;

public class GameScreen implements Screen {
	final Drop game;
	SpriteBatch batch;
	Texture primoPlayer;
	Texture background;
	Array<Texture> brawls = new Array<>();
	Array<DropBrawl> dropBrawls = new Array<>();
	Rectangle player;
	Music brawlMusic;
	Sound ruf;
	Sound get;
	Sound bull;
	Sound poco;
	Sound gene;
	Sound tnt;


	OrthographicCamera camera;
	int health = 2000;
	int score;
	long startTime;
	long dropTime;
	long finishTime;


	public GameScreen(final Drop kik) {
		this.game = kik;

		background = new Texture("picture/background.jpg");
		primoPlayer = new Texture(Gdx.files.internal("picture/primo.png"));
		brawls.add(new Texture(Gdx.files.internal("picture/poco.png")));
		brawls.add(new Texture(Gdx.files.internal("picture/ruffs.png")));
		brawls.add(new Texture(Gdx.files.internal("picture/gene.png")));
		brawls.add(new Texture(Gdx.files.internal("picture/dynamike.png")));
		brawls.add(new Texture(Gdx.files.internal("picture/bull.png")));
		brawls.add(new Texture(Gdx.files.internal("picture/bank.png")));

		brawlMusic = Gdx.audio.newMusic(Gdx.files.internal("sound/main.mp3"));
		ruf = Gdx.audio.newSound(Gdx.files.internal("sound/death.mp3"));
		poco = Gdx.audio.newSound(Gdx.files.internal("sound/poco_die_01.ogg"));
		bull = Gdx.audio.newSound(Gdx.files.internal("sound/deathBull.ogg"));
		gene = Gdx.audio.newSound(Gdx.files.internal("sound/gene_vo_14.ogg"));
		tnt = Gdx.audio.newSound(Gdx.files.internal("sound/tnt_guy_die_05.ogg"));
		get = Gdx.audio.newSound(Gdx.files.internal("sound/get.ogg"));
		brawlMusic.setLooping(true);


		// создает камеру
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1280, 720);


		spawnPlayer();
		spawnBrawl();
		startTime = System.currentTimeMillis();
	}

	@Override
	public void render (float delta) {
		// очищаем экран темно-синим цветом.
		// Аргументы для glClearColor красный, зеленый
		// синий и альфа компонент в диапазоне [0,1]
		// цвета используемого для очистки экрана.
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		// сообщаем SpriteBatch о системе координат
		// визуализации указанных для камеры.
		game.batch.setProjectionMatrix(camera.combined);


		// начинаем новую серию
		// все капли
		game.batch.begin();
		game.batch.draw(background, 0, 0);

		game.font.draw(game.batch, "Score: " + score, 0, 680);
		game.font.draw(game.batch, "Health: " + health, 0, 700);

		game.batch.draw(primoPlayer, player.x, player.y);

		game.batch.draw(primoPlayer, player.x, player.y);

		for(DropBrawl dropBrawl: dropBrawls) {
			game.batch.draw(dropBrawl.texture, dropBrawl.x, dropBrawl.y);
		}

		game.batch.end();


		// обработка пользовательского ввода
		if(Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			player.x = touchPos.x - 32;
		}

		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
			player.x -= 500 * Gdx.graphics.getDeltaTime();}
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
			player.x += 500 * Gdx.graphics.getDeltaTime();}

		if(player.x < 0){
			player.x = 0;}
		if(player.x > 1280 - player.width){
			player.x = 1280 - player.width;}

		if(TimeUtils.nanoTime() - dropTime > 1000000000){
			spawnBrawl();}

		Iterator<DropBrawl> iterator = dropBrawls.iterator();
		while(iterator.hasNext()) {
			DropBrawl raindrop = iterator.next();
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
			if(raindrop.y + 64 < 0){
				iterator.remove();
			}
			if(raindrop.overlaps(player)) {
				switch (raindrop.type){
					case "poco":
						health += 250;
						poco.play();
						break;
					case "bank":
						score += 50;
						health+=400;
						get.play();
						break;
					case "ruffs":
						score += 150;
						health-=500;
						ruf.play();
						break;
					case "gene":
						score += 120;
						health-=300;
						gene.play();
						break;
					case "dynamike":
						score += 350;
						health-=1000;
						tnt.play();
						break;
					case "bull":
						score += 500;
						health-=1500;
						bull.play();
						break;

				}
				iterator.remove();
			}
		}
		finishTime = System.currentTimeMillis();
		if ((finishTime - startTime > 62000)|| (health < 1)){
			dropBrawls.clear();
			brawlMusic.stop();
			brawls.clear();
			game.setScreen(new End(game, score, health));
		}
	}

	@Override
	public void dispose() {
		primoPlayer.dispose();
		brawlMusic.dispose();
		batch.dispose();
		background.dispose();
	}

	private void spawnBrawl() {
		DropBrawl dropBrawl = new DropBrawl();
		dropBrawl.x = MathUtils.random(0, 1280-70);
		dropBrawl.y = 600; // скорость падения
		dropBrawl.width = 64;
		dropBrawl.height = 64;
		Random random = new Random();
		int randomObjectTexture = random.nextInt(7);
		switch (randomObjectTexture){
			case 0:
				dropBrawl.texture = brawls.get(0);
				dropBrawl.type = "poco";
				break;
			case 1:
				dropBrawl.texture = brawls.get(1);
				dropBrawl.type = "ruffs";
				break;
			case 2:
				dropBrawl.texture = brawls.get(2);
				dropBrawl.type = "gene";
				break;
			case 3:
				dropBrawl.texture = brawls.get(3);
				dropBrawl.type = "dynamike";
				break;
			case 4:
				dropBrawl.texture = brawls.get(4);
				dropBrawl.type = "bull";
				break;
			case 5:
				dropBrawl.texture = brawls.get(5);
				dropBrawl.type = "bank";
				break;
			case 6:
				dropBrawl.texture = brawls.get(5);
				dropBrawl.type = "bank";
				break;
		}
		dropBrawls.add(dropBrawl);
		dropTime = TimeUtils.nanoTime();
	}

	private void spawnPlayer() {
		player = new Rectangle();
		player.x = 340;
		player.y = 0;
		player.width = 96;
		player.height = 128;
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		brawlMusic.play();
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}