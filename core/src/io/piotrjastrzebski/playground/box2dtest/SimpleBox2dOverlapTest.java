package io.piotrjastrzebski.playground.box2dtest;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import io.piotrjastrzebski.playground.BaseScreen;
import io.piotrjastrzebski.playground.GameReset;
import io.piotrjastrzebski.playground.PlaygroundGame;

/**
 * Created by PiotrJ on 31/07/15.
 */
public class SimpleBox2dOverlapTest extends BaseScreen {
	public final static float VP_WIDTH = 40;
	public final static float VP_HEIGHT = 22.5f;
	public final static float SCALE = 32f;
	public final static float INV_SCALE = 1.f/32f;
	private final Texture box;

	World world;
	Array<Box> boxes = new Array<>();
	Box2DDebugRenderer debugRenderer;
	boolean debugDraw = true;

	public SimpleBox2dOverlapTest (GameReset game) {
		super(game);
		gameCamera = new OrthographicCamera();
		gameViewport = new ExtendViewport(VP_WIDTH, VP_HEIGHT, gameCamera);
		debugRenderer = new Box2DDebugRenderer();
		debugRenderer.setDrawAABBs(true);
		world = new World(new Vector2(0, -10), true);
		box = new Texture("badlogic.jpg");

		createBounds();
		reset();
	}

	Body groundBody;

	private void createBounds () {
		float halfWidth = VP_WIDTH / 2f - 0.5f;
		float halfHeight = VP_HEIGHT / 2f - 0.5f;
		ChainShape chainShape = new ChainShape();
		chainShape.createLoop(new float[] {-halfWidth, -halfHeight, halfWidth, -halfHeight,
			halfWidth, halfHeight, -halfWidth, halfHeight});
		BodyDef chainBodyDef = new BodyDef();
		chainBodyDef.type = BodyDef.BodyType.StaticBody;
		groundBody = world.createBody(chainBodyDef);
		groundBody.createFixture(chainShape, 0);
		chainShape.dispose();
	}

	private void reset () {
		if (mouseJoint != null) {
			world.destroyJoint(mouseJoint);
			mouseJoint = null;
		}
		for (Box box : boxes) {
			world.destroyBody(box.body);
		}
		boxes.clear();
	}

	private void spawnDynamicBoxes (int count) {
		for (int i = 0; i < count; i++) {
			float x = MathUtils.random(-15, 15);
			float y = MathUtils.random(-8, 8);
			float rotation = MathUtils.random(90);
			createBox(x, y, rotation, box, true);
		}
	}

	private void spawnStaticBoxes (int count) {
		for (int i = 0; i < count; i++) {
			float x = MathUtils.random(-15, 15);
			float y = MathUtils.random(-8, 8);
			float rotation = MathUtils.random(90);
			createBox(x, y, rotation, box, false);
		}
	}

	private void createBox (float x, float y, float rotation, Texture texture, boolean dynamic) {
		Box box = new Box(x, y, rotation, texture);

		BodyDef def = new BodyDef();
		def.type = dynamic?BodyDef.BodyType.DynamicBody: BodyDef.BodyType.StaticBody;
		def.position.set(x, y);
		def.angle = rotation * MathUtils.degreesToRadians;
		box.body = world.createBody(def);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(box.width / 2, box.height / 2);
		box.body.createFixture(shape, 1);
		box.body.setUserData(box);
		shape.dispose();

		boxes.add(box);
	}

	FPSLogger fpsLogger = new FPSLogger();
	@Override public void render (float delta) {
		super.render(delta);
		world.step(1f / 60f, 6, 4);

		for (Box box : boxes) {
			box.update();
		}
		draw();
		fpsLogger.log();
	}

	Rectangle lastTouch = new Rectangle();
	private void draw () {
		if (debugDraw) {
			debugRenderer.render(world, gameCamera.combined);
		}
		batch.setProjectionMatrix(gameCamera.combined);
		batch.begin();
		for (Box box : boxes) {
			box.draw(batch);
		}
		batch.end();

		renderer.setProjectionMatrix(gameCamera.combined);
		renderer.begin(ShapeRenderer.ShapeType.Line);

		renderer.setColor(Color.MAGENTA);
		renderer.rect(lastTouch.x, lastTouch.y, lastTouch.width, lastTouch.height);
		renderer.end();
	}

	private class Box {
		public Body body;
		public Texture texture;
		public float x;
		public float y;
		public float rot;
		float width;
		float height;
		int srcWidth;
		int srcHeight;
		Color tint = new Color(Color.WHITE);

		public Box (float x, float y, float rotation, Texture texture) {
			this.x = x;
			this.y = y;
			this.rot = rotation;
			this.texture = texture;
			srcWidth = texture.getWidth();
			width = srcWidth * INV_SCALE /8;
			srcHeight = texture.getHeight();
			height = srcHeight * INV_SCALE /8;
		}

		public void update () {
			Vector2 position = body.getPosition();
			x = position.x;
			y = position.y;
			rot = body.getAngle() * MathUtils.radiansToDegrees;
		}

		public void draw (Batch batch) {
			batch.setColor(tint);
			batch.draw(texture, x - width / 2, y - height / 2, width / 2, height / 2, width, height, 1, 1, rot, 0, 0, srcWidth,
				srcHeight, false, false);
			batch.setColor(Color.WHITE);
		}
	}

	Body hitBody;
	Vector3 testPoint = new Vector3();
	QueryCallback callback = new QueryCallback() {
		@Override
		public boolean reportFixture(Fixture fixture) {
			if (fixture.getBody() == groundBody)
				return true;

			if (fixture.testPoint(testPoint.x, testPoint.y)) {
				hitBody = fixture.getBody();
				return false;
			} else
				return true;
		}
	};

	private MouseJoint mouseJoint;
	@Override public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		gameCamera.unproject(testPoint.set(screenX, screenY, 0));

		// ask the world which bodies are within the given
		// bounding box around the mouse pointer
		hitBody = null;
		world.QueryAABB(callback, testPoint.x - 0.1f, testPoint.y - 0.1f,
			testPoint.x + 0.1f, testPoint.y + 0.1f);

		// if we hit something we create a new mouse joint
		// and attach it to the hit body.
		if (hitBody != null) {
			MouseJointDef def = new MouseJointDef();
			def.bodyA = groundBody;
			def.bodyB = hitBody;
			def.collideConnected = true;
			def.target.set(testPoint.x, testPoint.y);
			def.maxForce = 1000.0f * hitBody.getMass();

			mouseJoint = (MouseJoint) world.createJoint(def);
			hitBody.setAwake(true);
		}

		for (Box box : boxes) {
			box.tint.set(Color.WHITE);
		}

		float size = VP_WIDTH * .1f;
		world.QueryAABB(new QueryCallback() {
			@Override public boolean reportFixture (Fixture fixture) {
				if (fixture.getBody() == groundBody)
					return true;
				Object userData = fixture.getBody().getUserData();
				if (userData instanceof Box) {
					Box box = (Box)userData;
					box.tint.set(Color.RED);
				}
				return true;
			}
		}, testPoint.x - size, testPoint.y - size, testPoint.x + size, testPoint.y + size);

		lastTouch.set(testPoint.x - size, testPoint.y - size, size * 2, size * 2);

		return super.touchDown(screenX, screenY, pointer, button);
	}
	Vector2 target = new Vector2();

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		gameCamera.unproject(testPoint.set(x, y, 0));
		target.set(testPoint.x, testPoint.y);
		// if a mouse joint exists we simply update
		// the target of the joint based on the new
		// mouse coordinates
		if (mouseJoint != null) {
			mouseJoint.setTarget(target);
		}
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		// if a mouse joint exists we simply destroy it
		if (mouseJoint != null) {
			world.destroyJoint(mouseJoint);
			mouseJoint = null;
		}
		return false;
	}

	@Override public boolean keyDown (int keycode) {
		if (keycode == Input.Keys.F5) {
			spawnDynamicBoxes(50);
		}
		if (keycode == Input.Keys.F6) {
			spawnStaticBoxes(50);
		}
		if (keycode == Input.Keys.F8) {
			reset();
		}
		if (keycode == Input.Keys.Z) {
			debugDraw = !debugDraw;
		}
		return super.keyDown(keycode);
	}

	@Override public void dispose () {
		super.dispose();
		box.dispose();
	}
	public static void main (String[] args) {
		PlaygroundGame.start(args, SimpleBox2dOverlapTest.class);
	}
}
