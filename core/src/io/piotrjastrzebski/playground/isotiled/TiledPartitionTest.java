package io.piotrjastrzebski.playground.isotiled;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.*;
import io.piotrjastrzebski.playground.BaseScreen;
import io.piotrjastrzebski.playground.GameReset;
import io.piotrjastrzebski.playground.PlaygroundGame;

import java.util.Comparator;

/**
 * Created by EvilEntity on 07/06/2015.
 */
public class TiledPartitionTest extends BaseScreen {
	public final static float SCALE = 32f; // size of single tile in pixels
	public final static float INV_SCALE = 1.f/SCALE;
	public final static float VP_WIDTH = 1280/SCALE;
	public final static float VP_HEIGHT = 720/SCALE;

	private final static int REGION_SIZE = 8;
	private final static int MAP_WIDTH = REGION_SIZE * 5;
	private final static int MAP_HEIGHT = REGION_SIZE * 3;
	private final static int[] map = new int[] {
		0, 0, 0, 0, 0, 0, 0, 0,  1, 1, 1, 1, 1, 1, 1, 1,  1, 1, 1, 1, 1, 1, 1, 1,  1, 1, 1, 1, 1, 1, 1, 1,  1, 1, 1, 1, 1, 1, 1, 1,
		0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 1,
		0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 1, 1, 1, 1, 1, 0,  0, 1, 1, 2, 1, 1, 0, 0,  1, 1, 1, 1, 1, 1, 1, 1,  0, 0, 0, 0, 0, 0, 0, 1,
		0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 1, 0, 0, 0, 1, 0,  0, 1, 0, 0, 0, 1, 0, 0,  1, 0, 0, 0, 0, 0, 0, 1,  0, 0, 0, 0, 0, 0, 0, 1,
		0, 0, 0, 0, 0, 0, 0, 0,  0, 1, 1, 0, 0, 0, 1, 0,  1, 1, 0, 0, 0, 1, 0, 0,  1, 0, 0, 0, 0, 0, 0, 1,  0, 0, 0, 0, 0, 0, 0, 1,
		0, 0, 0, 0, 0, 0, 0, 0,  0, 1, 0, 0, 1, 1, 1, 0,  2, 0, 0, 1, 1, 1, 0, 0,  1, 0, 1, 1, 1, 0, 0, 1,  0, 0, 0, 0, 0, 0, 0, 1,
		0, 0, 0, 0, 0, 0, 0, 0,  0, 1, 1, 1, 1, 0, 0, 0,  1, 1, 2, 1, 0, 0, 0, 0,  1, 1, 1, 0, 1, 1, 1, 1,  0, 0, 0, 0, 0, 0, 0, 1,
		1, 1, 1, 1, 1, 1, 1, 1,  0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 1,

		1, 0, 0, 0, 0, 0, 0, 0,  1, 1, 1, 1, 1, 1, 1, 1,  1, 1, 1, 1, 2, 1, 1, 1,  0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 1,
		1, 0, 0, 0, 0, 0, 0, 0,  1, 0, 0, 0, 0, 0, 0, 2,  2, 0, 1, 0, 0, 0, 0, 1,  0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 1,
		1, 0, 0, 0, 0, 0, 0, 0,  1, 0, 0, 0, 0, 0, 0, 1,  1, 0, 1, 0, 0, 1, 0, 1,  0, 0, 0, 0, 1, 1, 1, 1,  1, 1, 1, 1, 0, 0, 0, 1,
		1, 0, 0, 0, 0, 0, 0, 0,  1, 0, 0, 0, 0, 0, 0, 1,  0, 0, 1, 0, 0, 1, 0, 1,  0, 0, 0, 0, 1, 0, 0, 0,  0, 0, 0, 2, 0, 0, 0, 1,
		1, 0, 0, 0, 0, 0, 0, 0,  1, 0, 0, 0, 0, 0, 0, 1,  0, 0, 1, 0, 0, 1, 0, 1,  0, 0, 0, 0, 2, 0, 0, 0,  0, 0, 0, 1, 1, 1, 1, 1,
		1, 0, 0, 0, 0, 0, 0, 0,  1, 0, 0, 0, 0, 0, 0, 1,  0, 0, 1, 0, 0, 1, 0, 1,  0, 0, 0, 0, 1, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 1,
		1, 0, 0, 0, 0, 0, 0, 0,  1, 0, 0, 0, 0, 0, 0, 1,  0, 0, 1, 0, 0, 0, 0, 1,  0, 0, 0, 0, 1, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 1,
		1, 0, 0, 0, 0, 0, 0, 0,  1, 1, 1, 1, 1, 1, 1, 1,  1, 1, 1, 1, 1, 1, 1, 1,  0, 0, 0, 0, 1, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 1,

		0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 1,  0, 0, 0, 0, 1, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 1,
		0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 1,  0, 0, 0, 0, 1, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 1,
		0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 1,  0, 0, 0, 0, 1, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 1,
		0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 1,  0, 0, 0, 0, 1, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 1,
		0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 1,  1, 1, 1, 1, 1, 1, 1, 1,  1, 1, 1, 2, 1, 1, 1, 1,
		0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 1,  0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 1,
		0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 1,  0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 1,
		0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 0,  0, 0, 0, 0, 0, 0, 0, 1,  1, 1, 1, 1, 1, 1, 1, 1,  1, 1, 1, 1, 1, 1, 1, 1,
	};

	Array<Tile> tiles = new Array<>();
	Array<Region> regions = new Array<>();

	public TiledPartitionTest (GameReset game) {
		super(game);
		for (int y = 0; y < MAP_HEIGHT; y++) {
			for (int x = 0; x < MAP_WIDTH; x++) {
				Tile tile = new Tile(x + y * MAP_WIDTH, x, y);
				tile.setType(map[x + (MAP_HEIGHT - 1 - y) * MAP_WIDTH]);
				tiles.add(tile);
			}
		}
		for (int x = 0; x < MAP_WIDTH; x += REGION_SIZE) {
			for (int y = 0; y < MAP_HEIGHT; y += REGION_SIZE) {
				regions.add(new Region(x, y));
			}
		}
		gameCamera.position.set(VP_WIDTH / 2, VP_HEIGHT / 2, 0);

		rebuildRegions();
//		regions.get(0).rebuild();
	}

	private void rebuildRegions () {
		for (int i = 0; i < regions.size; i++) {
			regions.get(i).rebuild();
		}
		for (int i = 0; i < regions.size; i++) {
			regions.get(i).rebuildSubs();
		}
	}

	private void rebuildRegion (Vector2 cs) {
		for (int i = 0; i < regions.size; i++) {
			Region region = regions.get(i);
			if (region.bounds.contains(cs)) region.rebuild();
		}
	}

	private void rebuildRegions (Vector2 cs) {
		Array<Region> toRebuild = new Array<>();
		for (int i = 0; i < regions.size; i++) {
			Region region = regions.get(i);
			if (region.bounds.contains(cs)){
				region.rebuild();
				toRebuild.add(region);
				int x = (int)region.bounds.x/REGION_SIZE;
				int y = (int)region.bounds.y/REGION_SIZE;
				rebuildRegion(x -1, y, toRebuild);
				rebuildRegion(x +1, y, toRebuild);
				rebuildRegion(x, y +1, toRebuild);
				rebuildRegion(x, y -1, toRebuild);
				break;
			}
		}

		for (Region region : toRebuild) {
			region.rebuildSubs();
		}
	}

	private void rebuildRegion (int x, int y, Array<Region> toRebuild) {
		if (x < 0) return;
		if (x >= 5) return;
		if (y < 0) return;
		if (y >= 3) return;
		regions.get(x * 3 + y).rebuild();
		toRebuild.add(regions.get(x * 3 + y));
	}

	private Vector2 cs = new Vector2();
	@Override public void render (float delta) {
		super.render(delta);

		if (filling) {
			fillTimer += delta;
			if (fillTimer > .25f) {
				fillTimer-=.25f;
				resumeFloodFill(ffRegion, found);
			}
		}
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		renderer.setProjectionMatrix(gameCamera.combined);
		renderer.begin(ShapeRenderer.ShapeType.Filled);
		for (Tile tile : tiles) {
			tile.render(renderer, delta);
		}

//		Gdx.gl.glEnable(GL20.GL_BLEND);
//		renderer.setColor(1, 0, 1, 0.1f);
//		renderer.begin(ShapeRenderer.ShapeType.Filled);
//		for (Tile tile : found) {
//			renderer.rect(tile.x + .4f, tile.y + .4f, .2f, .2f);
//		}
//		renderer.end();

		for (Region region : regions) {
			region.update(cs);
			region.renderSubs(renderer);
			region.renderEdges(renderer);
		}

		renderer.end();

		renderer.begin(ShapeRenderer.ShapeType.Line);
		for (Region region : regions) {
			region.render(renderer);
		}
		renderer.end();
	}
	Region ffRegion;
	ObjectSet<Tile> found = new ObjectSet<>();
	private void floodFill (int x, int y, Region region, ObjectSet<Tile> found) {
		Tile start = getTile(x, y, region);
		if (start == null) return;
		ffRegion = region;
		targetType = start.type;
		processed.clear();
		queue.clear();
		queue.add(start);
		resumeFloodFill(region, found);
	}

	private void resumeFloodFill (Region region, ObjectSet<Tile> found) {
		int iters = 0;
		while (queue.size > 0) {
			ff(region, found);
			iters++;
			if (iters >= maxIters) {
				filling = true;
				break;
			}
		}
	}

	private void ff(Region region, ObjectSet<Tile> found) {
		Tile tile = queue.removeIndex(0);
		if (tile.type == targetType) {
			if (processed.containsKey(tile.id)) return;
			visitTile(tile, found);
			Tile west = getEdge(tile, -1, region);
			Tile east = getEdge(tile, 1, region);

			for (int i = west.x; i <= east.x; i++) {
				Tile n = getTile(i, west.y, region);
				visitTile(n, found);
				Tile north = getTile(i, west.y + 1, region);
				if (north != null && north.type == targetType) {
					addToQueue(north);
				}
				Tile south = getTile(i, west.y - 1, region);
				if (south != null && south.type == targetType) {
					addToQueue(south);
				}
			}
		}
	}

	private void floodFillFull (int x, int y, Region region, ObjectSet<Tile> found) {
		Tile start = getTile(x, y, region);
		if (start == null) return;
		ffRegion = region;
		targetType = start.type;
		processed.clear();
		queue.clear();
		queue.add(start);
//		int iters = 0;
		while (queue.size > 0) {
			ff(region, found);
		}
	}

	private Tile getEdge (Tile tile, int offset, Region region) {
		while (true) {
			Tile next = getTile(tile.x + offset, tile.y, region);
			if (next != null && next.type == targetType) {
				tile = next;
			} else {
				break;
			}
		}
		return tile;
	}

	boolean filling;
	float fillTimer;
	int maxIters = 4;

	int targetType;
	Array<Tile> queue = new Array<>();
	IntMap<Tile> processed = new IntMap<>();
	private void floodFill (int x, int y, ObjectSet<Tile> found) {
		floodFill(x, y, null, found);
	}

	private void addToQueue (Tile tile) {
		if (tile == null || processed.containsKey(tile.id)) return;
		queue.add(tile);
	}

	private void visitTile (Tile tile, ObjectSet<Tile> found) {
		processed.put(tile.id, tile);
		tile.tint.set(1, .5f, .5f, 1);
		tile.a = 1;
		found.add(tile);
	}

	private void resumeFloodFillSlow (Region region) {
//		int iters = 0;
		while (queue.size > 0) {
			Tile tile = queue.removeIndex(0);
			if (tile.type == targetType) {
				if (processed.containsKey(tile.id)) continue;
				processed.put(tile.id, tile);
				tile.tint.set(1, .5f, .5f, 1);
				tile.a = 1;
				addToQueue(tile.x - 1, tile.y, region);
				addToQueue(tile.x + 1, tile.y, region);
				addToQueue(tile.x, tile.y + 1, region);
				addToQueue(tile.x, tile.y - 1, region);
			}
//			iters++;
//			if (iters >= maxIters) {
//				filling = true;
//				break;
//			}
		}
	}

	private void addToQueue(int x, int y, Region region) {
		addToQueue(getTile(x, y, region));
	}

	private Tile getTile (int x, int y, Region region) {
		if (region == null) {
			if (x < 0 || x >= MAP_WIDTH) return null;
			if (y < 0 || y >= MAP_HEIGHT) return null;
		} else {
			if (x < region.x || x >= region.x + REGION_SIZE) return null;
			if (y < region.y || y >= region.y + REGION_SIZE) return null;
		}
		int index = x + y * MAP_WIDTH;
		if (index < 0 || index >= tiles.size) return null;
		return tiles.get(index);
	}

	private class Tile {
		public int id;
		public int x;
		public int y;
		public Rectangle bounds = new Rectangle();
		public Color color = new Color();
		public Color tint = new Color();
		public int type;

		public Tile (int id, int x, int y) {
			this.id = id;
			this.x = x;
			this.y = y;
			bounds.set(x, y, 1, 1);
			setType(0);
		}

		public void setType (int type) {
			this.type = type;
			switch (type) {
			case 0: // grass
				// some variation so we know wtf is going on
//				color.set(.1f, MathUtils.random(0.7f, .9f), MathUtils.random(.1f, .2f), 1);
				color.set(0, 1, 0, 1);
				break;
			case 1: // wall
				color.set(Color.FIREBRICK);
				break;
			case 2: // door
				color.set(1f, .8f, .6f, 1);
				break;
			}
		}

		Color tmp = new Color();
		float a;
		public void render(ShapeRenderer renderer, float delta) {
			a = MathUtils.clamp(a -= 2f*delta, 0, 1);
			renderer.setColor(tmp.set(color).lerp(tint, a));
			renderer.rect(x, y, 1, 1);
		}

		public void setColor(Color color){
			this.color.set(color);
		}

		@Override public String toString () {
			return "Tile{" +x + ", " + y + ", id="+id+"}";
		}

		@Override public boolean equals (Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			Tile tile = (Tile)o;
			return id == tile.id;
		}

		@Override public int hashCode () {
			return id;
		}
	}

	private class Region {
		public int x;
		public int y;
		public IntArray sides = new IntArray();
		public Rectangle bounds = new Rectangle();
		public Array<Tile> tiles = new Array<>();
		public SubRegion[] subs = new SubRegion[REGION_SIZE * REGION_SIZE];
		public boolean selected;

		public Region (int x, int y) {
			this.x = x;
			this.y = y;
			bounds.set(x, y, REGION_SIZE, REGION_SIZE);
			for (int sx = 0; sx < REGION_SIZE; sx++) {
				for (int sy = 0; sy < REGION_SIZE; sy++) {
					subs[sx + sy * REGION_SIZE] = new SubRegion();
				}
			}
		}

		public void update(Vector2 cs) {
			selected = bounds.contains(cs);
			if (selected) {
				for (SubRegion sub : subs) {
					sub.select(cs);
				}
			} else {
				for (SubRegion sub : subs) {
					sub.selected = false;
				}
			}
			// TODO select neighbours
		}

		private Tile getEdge (Tile tile, int offset, ObjectSet<Tile> available) {
			while (true) {
				Tile next = getTile(tile.x + offset, tile.y, this);
				if (next != null && available.contains(next)) {
					if (next.x > x && next.y > y) {
						Tile left = getTile(next.x - 1, next.y, null);
						Tile south = getTile(next.x, next.y - 1, null);
						if (left != null && left.type == next.type &&
							south != null && south.type == next.type) {
							return next;
						}
					}
					tile = next;
				} else {
					break;
				}
			}
			return tile;
		}

		int ids;
		ObjectSet<Tile> added = new ObjectSet<>();
		ObjectSet<Tile> tmpAdded = new ObjectSet<>();
		public void rebuild () {
			for (int i = 0; i < ids; i++) {
				subs[i].id = -1;
				subs[i].tiles.clear();
				subs[i].edges.clear();
			}
			ids = 0;
			added.clear();
			for (int sx = 0; sx < REGION_SIZE; sx++) {
				for (int sy = 0; sy < REGION_SIZE; sy++) {
					found.clear();
					SubRegion sub = subs[ids];
					sub.id = ids;
					Tile tile = getTile(x + sx, y + sy, this);
					if (tile == null || added.contains(tile)) continue;

					floodFillFull(x + sx, y + sy, this, found);
					added.addAll(found);
					sub.tiles.clear();
					for (Tile f : found) {
						sub.tiles.add(f);
					}
					ids++;
					// TODO find edges of the region
					// if tile is at the edge -> edge
					// if tile is adjacent to tile not in this region -> edge
					// we start at bottom left, like always
					// we want all tiles at left and bottoms edges
					// we want tile +1 at top and right, so they are == to tiles in the next region
					// we dont really need to save the tiles, just hash of edge pos, len and direction
					// x + y + dir + len
					// simple, find all boundaries, but we dont really have enough data from it
					// need some kind of fill probably
					// go up/right from each edge tile? mark used etc
					// probably need to go in both dirs to be safe
					// 2 passes, left/right, up/down?
					// need to figure our most naive solution first!
//					tmpAdded.clear();
//					for (Tile t : sub.tiles) {
//						if (sub.edges.contains(t)) continue;
//						if (t.x > x && t.y > y) {
//							Tile left = getTile(t.x - 1, t.y, null);
//							Tile south = getTile(t.x, t.y - 1, null);
//							if (left != null && left.type == t.type &&
//								south != null && south.type == t.type)
//								continue;
//						}
//						Tile w = getEdge(t, -1, sub.tiles);
//						Tile e = getEdge(t, 1, sub.tiles);
//						for (int i = w.x; i <= e.x; i++) {
//							Tile add = getTile(i - 1, t.y, null);
//							if (add == null || !sub.edges.contains(add)) {
//								sub.edges.add(t);
//							}
//						}
//					}

//					for (Tile t : sub.tiles) {
//						Tile w = getTile(t.x - 1, t.y, null);
//						if (w == null || !sub.tiles.contains(w)) {
//							sub.edges.add(t);
//							continue;
//						}
//						Tile e = getTile(t.x + 1, t.y, null);
//						if (e == null || !sub.tiles.contains(e)) {
//							sub.edges.add(t);
//							continue;
//						}
//						Tile n = getTile(t.x, t.y + 1, null);
//						if (n == null || !sub.tiles.contains(n)) {
//							sub.edges.add(t);
//							continue;
//						}
//						Tile s = getTile(t.x, t.y - 1, null);
//						if (s == null || !sub.tiles.contains(s)) {
//							sub.edges.add(t);
//						}
//					}

//					for (Tile t : sub.tiles) {
//						if (t.x == x || t.x == x + REGION_SIZE -1
//							|| t.y == y || t.y == y + REGION_SIZE -1) {
//							sub.edges.add(t);
//						}
//					}
				}
			}
		}

		public void rebuildSubs() {
			for (int i = 0; i < ids; i++) {
				subs[i].rebuild();
			}
		}

		private float margin = 0.015f;
		public void render (ShapeRenderer renderer) {
			if (selected) {
				renderer.setColor(0, 1, 1, 1);
			} else {
				renderer.setColor(1, .7f, 0, 1);
			}
			renderer.rect(x + margin, y + margin, REGION_SIZE-2*margin, REGION_SIZE-2*margin);
		}

		public void renderSubs (ShapeRenderer renderer) {
			for (int i = 0; i < ids; i++) {
				subs[i].render(renderer);
			}
		}

		public void renderEdges (ShapeRenderer renderer) {
			for (int i = 0; i < ids; i++) {
				subs[i].renderEdges(renderer);
			}
		}

//		private SubRegion getWorld(int x, int y) {
//			x -= this.x;
//			y -= this.y;
//			return subs[x + y * REGION_SIZE];
//		}
//
//		private SubRegion get(int x, int y) {
//			return subs[x + y * REGION_SIZE];
//		}

		public class SubRegion {
			private Rectangle tmp = new Rectangle();
			public int id = -1;
			public boolean selected;
			public Array<Tile> tiles = new Array<>();
			public ObjectSet<Tile> tiles2 = new ObjectSet<>();
			public ObjectSet<Tile> edges = new ObjectSet<>();
			public Array<Edge> edges2 = new Array<>();

			public SubRegion () {

			}

			public void rebuild() {
				edges2.clear();
				tiles2.clear();
				tiles2.addAll(tiles);
				if (tiles2.size != tiles.size) throw new AssertionError("fuck " + tiles2.size + " != " + tiles.size);
				findHorizontalEdges();
				findVerticalEdges();
			}


			private void findHorizontalEdges () {
				tiles.sort(new Comparator<Tile>() {
					@Override public int compare (Tile o1, Tile o2) {
						if (o1.y == o2.y) return o1.x - o2.x;
						return o1.y - o2.y;
					}
				});
				int last = 0;
				///*
				// bottom edges
				while (last < tiles.size) {
					Tile first = tiles.get(last);
					if (first.y == 0) {
						last++;
						continue;
					}
					Tile south = getTile(first.x, first.y - 1, null);
					if (south != null && tiles.contains(south, true)) {
						last++;
						continue;
					}
					int otherSubRegionId = getSubRegionId(south);
					Tile prev = first;
					for (int i = last + 1; i < tiles.size ; i++) {
						Tile tile = tiles.get(i);
						if (prev.y != tile.y) break;
						if (prev.x + 1 != tile.x) break;
						south = getTile(tile.x, tile.y - 1, null);
						if (south != null && tiles.contains(south, true)) break;
						if (otherSubRegionId != getSubRegionId(south)) break;
						prev = tile;
						last = i;
					}
					Edge edge = new Edge(first.x, first.y, prev.x - first.x + 1, true);
					edges2.add(edge);
					last++;
//					break;
				}
				//*/
//				/*
				// top edges
				last = 0;
				while (last < tiles.size) {
					Tile first = tiles.get(last);
					Tile north = getTile(first.x, first.y + 1, null);
					if (north == null || tiles.contains(north, true)) {
						last++;
						continue;
					}
					int otherSubRegionId = getSubRegionId(north);
					Tile prev = first;
					for (int i = last + 1; i < tiles.size ; i++) {
						Tile tile = tiles.get(i);
						if (prev.y != tile.y) break;
						if (prev.x + 1 != tile.x) break;
						north = getTile(tile.x, tile.y + 1, null);
						if (north == null || tiles.contains(north, true)) break;
						if (otherSubRegionId != getSubRegionId(north)) break;
						prev = tile;
						last = i;
					}
					Edge edge = new Edge(first.x, first.y + 1, prev.x - first.x + 1, true);
					edges2.add(edge);
					last++;
//					break;
				}
				//*/
			}

			private void findVerticalEdges() {
				// note maybe work on entire tile grid +1? less sorting
				tiles.sort(new Comparator<Tile>() {
					@Override public int compare (Tile o1, Tile o2) {
						if (o1.x == o2.x) return o1.y - o2.y;
						return o1.x - o2.x;
					}
				});
				int last = 0;
//				/*
				// left edges
				while (last < tiles.size) {
					Tile start = tiles.get(last);
//					if (start.x == 0) {
//						last++;
//						continue;
//					}
					Tile west = getTile(start.x - 1, start.y, null);
					if (west != null && tiles.contains(west, true)) {
						last++;
						continue;
					}
					int otherSubRegionId = getSubRegionId(west);
					Tile end = start;
					for (int i = last + 1; i < tiles.size ; i++) {
						Tile tile = tiles.get(i);
						if (end.x != tile.x) break;
						if (end.y + 1 != tile.y) break;
						west = getTile(tile.x - 1, tile.y, null);
						if (west != null && tiles.contains(west, true)) break;
						if (otherSubRegionId != getSubRegionId(west)) break;
						end = tile;
						last = i;
					}
					Edge edge = new Edge(start.x, start.y, end.y - start.y + 1, false);
					edges2.add(edge);
					last++;
//					break;
				}
				//*/
//				/*
				// right edges
				last = 0;
				while (last < tiles.size) {
					Tile start = tiles.get(last);
					Tile east = getTile(start.x + 1, start.y, null);
					if (east == null || tiles.contains(east, true)) {
						last++;
						continue;
					}
					int otherSubRegionId = getSubRegionId(east);
					Tile end = start;
					for (int i = last + 1; i < tiles.size ; i++) {
						Tile tile = tiles.get(i);
						if (end.x != tile.x) break;
						if (end.y + 1 != tile.y) break;
						east = getTile(tile.x + 1, tile.y, null);
						if (east == null || tiles.contains(east, true)) break;
						if (otherSubRegionId != getSubRegionId(east)) break;
						end = tile;
						last = i;
					}
					Edge edge = new Edge(start.x + 1, start.y, end.y - start.y + 1, false);
					edges2.add(edge);
					last++;
//					break;
				}
				//*/
			}

			public void select (Vector2 cs) {
				for (Tile tile : tiles) {
					if (tmp.set(tile.x, tile.y, 1, 1).contains(cs)) {
						selected = true;
						return;
					}
				}
				selected = false;
			}

			public void renderEdges (ShapeRenderer renderer) {
				if (!selected) return;
				renderer.setColor(1, 1, 0, selected?.75f:.5f);
				for (Edge edge : edges2) {
					edge.render(renderer);
				}
			}

			public void render (ShapeRenderer renderer) {
				for (int i = 0; i < tiles.size; i++) {
					Tile tile = tiles.get(i);
					float c = i/(float)tiles.size;
					if (selected) {
						renderer.setColor(c, c, c, .5f);
//						renderer.setColor(1, 1, 1, .5f);
					} else {
						c = .5f + id /(float)ids/2;
						renderer.setColor(c, c, c, .5f);
					}
//					renderer.rect(tile.x + 0.1f, tile.y + 0.1f, 1 - 0.2f, 1 - 0.2f);
					renderer.rect(tile.x, tile.y, 1, 1);
				}
//				for (Tile tile : tiles) {
//					if (selected) {
//						renderer.setColor(1, 1, 1, .5f);
//					} else {
//						float c = .5f + id /(float)ids/2;
//						renderer.setColor(c, c, c, .5f);
//					}
//					renderer.rect(tile.x + 0.1f, tile.y + 0.1f, 1 - 0.2f, 1 - 0.2f);
//				}
			}


		}
	}

	public class Edge {
		public Array<Region.SubRegion> conencted = new Array<>();
		public int x, y, len;
		public boolean horizontal;
		private Color color = new Color(MathUtils.random(),MathUtils.random(),MathUtils.random(), .75f);

		public Edge (int x, int y, int len, boolean horizontal) {
			this.x = x;
			this.y = y;
			this.len = len;
			this.horizontal = horizontal;
		}

		public void render (ShapeRenderer renderer) {
			renderer.setColor(color);
			renderer.rect(x+0.1f, y+0.1f, (horizontal?len:1)-.2f, (horizontal?1:len)-.2f);
		}

		@Override public boolean equals (Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			Edge edge = (Edge)o;

			if (x != edge.x)
				return false;
			if (y != edge.y)
				return false;
			if (len != edge.len)
				return false;
			return horizontal == edge.horizontal;

		}

		@Override public int hashCode () {
			int result = x;
			result = 31 * result + y;
			result = 31 * result + len;
			result = 31 * result + (horizontal ? 1 : 0);
			return result;
		}
	}
	private int getSubRegionId (Tile tile) {
		// TODO this is obviously dumb
		// do we want region and sub region id hash?
		for (Region region : regions) {
			for (int i = 0; i < region.ids; i++) {
				if (region.subs[i].tiles.contains(tile, true)) {
					return i;
				}
			}
		}
		return -1;
	}

	private Vector3 temp = new Vector3();
	@Override public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		// fairly dumb
		gameCamera.unproject(temp.set(screenX, screenY, 0));
		cs.set(temp.x, temp.y);
		if (button == Input.Buttons.LEFT) {
			// todo create a wall at position
			for (Tile tile : tiles) {
				if (tile.bounds.contains(cs)) {
					int next = tile.type + 1;
					if (next > 2) next = 0;
					tile.setType(next);
					break;
				}
			}
			rebuildRegions(cs);
		} else if (button == Input.Buttons.RIGHT) {
			for (Tile tile : tiles) {
				if (tile.bounds.contains(cs)) {
					int next = tile.type - 1;
					if (next < 0) next = 2;
					tile.setType(next);
					break;
				}
			}
			rebuildRegions(cs);
		} else if (button == Input.Buttons.MIDDLE) {
			for (Region region : regions) {
				if (region.bounds.contains(cs)) {
					found.clear();
					floodFill((int)cs.x, (int)cs.y, region, found);
					break;
				}

			}
		}
		return true;
	}

	@Override public boolean mouseMoved (int screenX, int screenY) {
		gameCamera.unproject(temp.set(screenX, screenY, 0));
		cs.set(temp.x, temp.y);
		return super.mouseMoved(screenX, screenY);
	}

	public static void main (String[] args) {
		PlaygroundGame.start(args, TiledPartitionTest.class);
	}
}
