package io.piotrjastrzebski.playground.bttests.btedittest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTree;

/**
 * Created by EvilEntity on 10/10/2015.
 */
public class ViewTree<E> extends VisTree implements Pool.Poolable, ModelTree.TaskListener<E> {
	private static final String TAG = ViewTree.class.getSimpleName();
	protected Pool<ViewTask<E>> vtPool;
	protected ModelTree<E> model;

	protected DragAndDrop dad;
	private Actor separator;

	public ViewTree () {
		// remove y spacing so we dont have gaps for DaD
		setYSpacing(0);
		separator = new VisImage(VisUI.getSkin().getDrawable("white"));
		separator.setColor(Color.GREEN);
		separator.setHeight(4 * VisUI.getSizes().scaleFactor);
		separator.setVisible(false);
		addActor(separator);

		dad = new DragAndDrop();
		vtPool = new Pool<ViewTask<E>>() {
			@Override protected ViewTask<E> newObject () {
				return new ViewTask<>(ViewTree.this);
			}
		};
		// single selection makes thing simpler for edit
		getSelection().setMultiple(false);
		addListener(new ChangeListener() {
			@Override public void changed (ChangeEvent event, Actor actor) {
				ViewTree.this.changed((ViewTask<E>)getSelection().getLastSelected());
			}
		});
	}

	public void update (float delta) {
		if (viewRoot == null) return;
		viewRoot.update(delta);
	}

	private void changed (ViewTask<E> selection) {
		if (selection != null) {
			for (ViewTaskSelectedListener<E> listener : listeners) {
				listener.selected(selection);
			}
		} else {
			for (ViewTaskSelectedListener<E> listener : listeners) {
				listener.deselected();
			}
		}
	}

	ViewTask<E> viewRoot;
	public void init (ModelTree<E> model) {
		if (this.model != null) reset();
		this.model = model;
		model.addListener(this);
		ModelTask<E> root = model.getRoot();
		add(viewRoot = initVT(root));
		for (ModelTask<E> task : root) {
			addViewTask(viewRoot, task);
		}
		expandAll();
	}

	protected void addViewTask (ViewTask<E> parent, ModelTask<E> task) {
		ViewTask<E> node = initVT(task);
		parent.add(node);
		for (ModelTask<E> child : task) {
			addViewTask(node, child);
		}
	}

	protected ViewTask<E> initVT (ModelTask<E> task) {
		ViewTask<E> out = vtPool.obtain();
		out.init(task);
		modelToView.put(task, out);
		return out;
	}

	protected void freeVT(ViewTask<E> vt) {
		modelToView.remove(vt.getModelTask());
		vtPool.free(vt);
	}

	@Override public void reset () {
		if (model != null) {
			model.removeListener(this);
		}
		model = null;
		listeners.clear();
	}

	protected Array<ViewTaskSelectedListener<E>> listeners = new Array<>();
	public void addListener(ViewTaskSelectedListener<E> listener) {
		if (!listeners.contains(listener, true)) {
			listeners.add(listener);
		}
	}

	public void removeListener(ViewTaskSelectedListener<E> listener) {
		listeners.removeValue(listener, true);
	}

	protected ObjectMap<Class<? extends Task>, Task<E>> classToTask = new ObjectMap<>();

	/**
	 * register given actor as source with task that can be added to the tree
	 */
	public void addSource (Actor source, final Class<? extends Task> task) {
		if (classToTask.containsKey(task)) {
			Gdx.app.log(TAG, "Task class already added: " + task);
			return;
		}
		try {
			Task instance = ClassReflection.newInstance(task);
			classToTask.put(task, instance);
		} catch (ReflectionException e) {
			Gdx.app.error(TAG, "Failed to instantience task " + task, e);
			return;
		}
		dad.addSource(new BTESource(source) {
			@Override public BTEPayload dragStart (InputEvent event, float x, float y, int pointer, BTEPayload out) {
				// TODO should this create a node for this already?
				ModelTask<E> mt = model.pool.obtain();
				mt.init(null, classToTask.get(task).cloneTask());
				ViewTask<E> vt = initVT(mt);
				out.setAsAdd(vt);
				return out;
			}

			@Override public void onDragStop (InputEvent event, float x, float y, int pointer, BTEPayload payload,
				BTETarget target) {
				Gdx.app.log("Add", "OnStop");
				ViewTask vt = payload.getViewTask();
				// vt wasnt added
				if (vt.getParent() == null) {
					Gdx.app.log("Add", "Free" + vt.getModelTask().getName());
					model.pool.free(vt.getModelTask());
					freeVT(vt);
				}
			}
		});
	}

	public void addTrash (Actor trash) {
		dad.addTarget(new BTETarget(trash) {
			@Override public boolean onDrag (BTESource source, BTEPayload payload, float x, float y, int pointer) {
				return payload.hasTarget(BTEPayload.TARGET_TRASH);
			}

			@Override public void onDrop (BTESource source, BTEPayload payload, float x, float y, int pointer) {
				// TODO confirm?
				trash(payload.getViewTask());
			}
		});
	}

	public Actor getSeparator () {
		return separator;
	}

	/**
	 * @return if we can add new vt to target at given drop point
	 */
	public boolean canAddTo (ViewTask<E> vt, ViewTask<E> target, DropPoint to) {
		// we cant add to own children, thats about it
		// some thing might result in broken tree, but it will be indicated
		return vt.findNode(target) == null;
	}

	/**
	 * Add new node to target at dp
	 */
	public void addTo(ViewTask<E> vt, ViewTask<E> target, DropPoint to) {
		// TODO do we want to double check?
		if (!canAddTo(vt, target, to)) {
			Gdx.app.log(TAG, target + " cant be added to " + vt + " at " + to);
			return;
		}
		// TODO change model as well
		// if the view task is already in the tree, remove it
		vt.getModelTask().remove();
		vt.remove();

		Gdx.app.log(TAG, "Add " + vt + " to " + target + " at " + to);
		int id = target.getIndexInParent();
		switch (to) {
		case ABOVE:
			// insert vt before target
			target.getParent().insert(id, vt);
			target.getModelTask().getParent().insert(id, vt.getModelTask());
			break;
		case MIDDLE:
			// add vt to target
			target.add(vt);
			target.getModelTask().add(vt.getModelTask());
			break;
		case BELOW:
			// insert vt after target
			target.getParent().insert(id + 1, vt);
			target.getModelTask().getParent().insert(id, vt.getModelTask());
			break;
		}
	}

	public void trash (ViewTask<E> vt) {
		Gdx.app.log(TAG, "Remove " + vt);
		model.remove(vt.getModelTask());
		freeVT(vt);
		remove(vt);
	}

	ObjectMap<ModelTask<E>, ViewTask<E>> modelToView = new ObjectMap<>();
	@Override public void statusChanged (ModelTask<E> task, Task.Status from, Task.Status to) {
		ViewTask<E> vt = find(viewRoot, task);
		if (vt == null) {
			Gdx.app.log(TAG, "VT for" + task + " in statusChanged not found!");
			return;
		}
		vt.statusChanged(from, to);
	}

	@Override public void validChanged (ModelTask<E> task, boolean valid) {
		ViewTask<E> vt = find(viewRoot, task);
		if (vt == null) {
			Gdx.app.log(TAG, "VT for" + task + " int validChanged not found!");
			return;
		}
		vt.validChanged(valid);
	}

	private ViewTask<E> find(ViewTask<E> parent, ModelTask<E> task) {
		// do we want ref only here?
		if (parent.getModelTask() == task) {
			return parent;
		}
		for (Object node : parent.getChildren()) {
			ViewTask<E> found = find((ViewTask<E>)node, task);
			if (found != null) return found;
 		}
		return null;
	}

	public interface ViewTaskSelectedListener<E> {
		void selected(ViewTask<E> task);
		void deselected();
	}
}
