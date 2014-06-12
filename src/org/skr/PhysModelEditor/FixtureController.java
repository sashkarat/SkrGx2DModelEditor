package org.skr.PhysModelEditor;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import org.skr.physmodel.FixtureSet;
import org.skr.physmodel.FixtureSetDescription;

/**
 * Created by rat on 12.06.14.
 */

public class FixtureController extends Controller {

    FixtureSetDescription fd;

    protected FixtureController(Stage stage) {
        super(stage);
    }


    public void loadFromFixtureSet( FixtureSet fixtureSet ) {
        fd = fixtureSet.getDescription();
    }

    @Override
    protected void translateRendererToObject() {

    }

    @Override
    protected void draw() {

    }

    @Override
    protected Vector2 stageToObject(Vector2 stageCoord) {
        return null;
    }

    @Override
    protected void updateControlPoint(ControlPoint cp) {

    }

    @Override
    protected void moveControlPoint(ControlPoint cp, Vector2 offsetLocal, Vector2 offsetStage) {

    }

    @Override
    protected void rotateAtControlPoint(ControlPoint cp, float angle) {

    }

    @Override
    protected Object getControlledObject() {
        return null;
    }
}
