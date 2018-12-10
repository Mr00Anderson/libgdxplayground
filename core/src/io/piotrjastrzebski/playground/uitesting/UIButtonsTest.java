package io.piotrjastrzebski.playground.uitesting;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import io.piotrjastrzebski.playground.BaseScreen;
import io.piotrjastrzebski.playground.GameReset;
import io.piotrjastrzebski.playground.PlaygroundGame;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by PiotrJ on 20/06/15.
 */
public class UIButtonsTest extends BaseScreen {
	private final static String TAG = UIButtonsTest.class.getSimpleName();
	final TextButton textButton;

	public UIButtonsTest (GameReset game) {
		super(game);
		clear.set(.5f, .5f, .5f, 1);
		// TODO we want a button that we can long press to do some action
		textButton = new TextButton("PRESS OR HOLD ME", VisUI.getSkin(), "toggle");
		textButton.addActor(new Image(skin.getDrawable("tree-plus")));
		textButton.addListener(new ActorGestureListener(){
			boolean longPress;
			@Override
			public void tap(InputEvent event, float x, float y, int count, int button) {
				if (textButton.isDisabled()) return;
				Gdx.app.log(TAG, "tap");
			}

			@Override
			public boolean longPress(Actor actor, float x, float y) {
				if (textButton.isDisabled()) return false;
				Gdx.app.log(TAG, "long press");
				return true;
			}
		});
		textButton.setTransform(true);
		textButton.setScale(2);
		textButton.setDisabled(true);
		root.add(textButton).expand();
	}

	@Override public void render (float delta) {
		super.render(delta);
		stage.act(delta);
		stage.draw();

		if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
			textButton.setDisabled(!textButton.isDisabled());
		}
	}

	public static void main (String[] args) {
		LwjglApplicationConfiguration config = PlaygroundGame.config();
		config.width /= 2;
		config.height /= 2;
		PlaygroundGame.start(args, config, UIButtonsTest.class);
	}
}
