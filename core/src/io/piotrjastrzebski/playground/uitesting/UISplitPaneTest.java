package io.piotrjastrzebski.playground.uitesting;

import com.badlogic.gdx.graphics.Color;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSplitPane;
import com.kotcrab.vis.ui.widget.VisTable;
import io.piotrjastrzebski.playground.BaseScreen;
import io.piotrjastrzebski.playground.GameReset;
import io.piotrjastrzebski.playground.PlaygroundGame;

/**
 * Created by PiotrJ on 20/06/15.
 */
public class UISplitPaneTest extends BaseScreen {
	public UISplitPaneTest (GameReset game) {
		super(game);
		clear.set(Color.GRAY);
//		stage.setDebugAll(true);


		VisTable tableY = new VisTable();
		tableY.add(new VisLabel("LabelA1")).row();
		tableY.add(new VisLabel("LabelA2")).row();
		tableY.add(new VisLabel("LabelA3")).row();
		tableY.add(new VisLabel("LabelA3")).row();

		VisTable tableA = new VisTable();
		tableA.add(new VisLabel("LabelA1")).row();
		tableA.add(new VisLabel("LabelA2")).row();
		tableA.add(new VisLabel("LabelA3")).row();
		tableA.add(new VisLabel("LabelA3")).row();

		VisTable tableB = new VisTable();
		tableB.add(new VisLabel("LabelB1")).row();
		tableB.add(new VisLabel("LabelB2")).row();
		tableB.add(new VisLabel("LabelB3")).row();
		tableB.add(new VisLabel("LabelB3")).row();


		VisSplitPane splitPaneAB = new VisSplitPane(tableA, tableB, false);
		splitPaneAB.setMinSplitAmount(.1f);

		VisTable tableC = new VisTable();
		tableC.add(new VisLabel("LabelC1")).row();
		tableC.add(new VisLabel("LabelC2")).row();
		tableC.add(new VisLabel("LabelC3")).row();
		tableC.add(new VisLabel("LabelC3")).row();

		VisTable tableD = new VisTable();
		tableD.add(new VisLabel("LabelC1")).row();
		tableD.add(new VisLabel("LabelC2")).row();
		tableD.add(new VisLabel("LabelC3")).row();
		tableD.add(new VisLabel("LabelC3")).row();


		VisSplitPane splitPaneCD = new VisSplitPane(tableC, tableD, false);
		splitPaneCD.setMinSplitAmount(.1f);

		VisSplitPane splitPaneABCD = new VisSplitPane(splitPaneAB, splitPaneCD, false);
		splitPaneABCD.setMinSplitAmount(.1f);
//		splitPaneABC.setSplitAmount(0.66f);

		VisSplitPane splitPaneV = new VisSplitPane(splitPaneABCD, tableY, true);
		splitPaneV.setSplitAmount(.8f);
		splitPaneV.setMinSplitAmount(.1f);



		root.add(splitPaneV).grow();
	}


	@Override public void render (float delta) {
		super.render(delta);
		stage.act(delta);
		stage.draw();
	}

	public static void main (String[] args) {
		PlaygroundGame.start(args, UISplitPaneTest.class);
	}
}
