package org.skr.PhysModelEditor;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import org.skr.physmodel.FixtureSet;
import org.skr.physmodel.FixtureSetDescription;

/**
 * Created by rat on 12.06.14.
 */

public class ShapeController extends Controller {

    FixtureSetDescription fd;
    Vector2 bodyPos;
    float bodyAngle;

    protected ShapeController(Stage stage) {
        super(stage);
    }


    public void loadFromFixtureSet( FixtureSet fixtureSet ) {
        fd = fixtureSet.getDescription();
        bodyPos = fixtureSet.getBody().getPosition();
        bodyAngle = fixtureSet.getBody().getAngle();
    }

    @Override
    protected void translateRendererToObject() {
        if ( fd == null )
            return;
        Vector2 pos =  PhysWorld.get().physToView( bodyPos );
        shapeRenderer.translate( pos.x, pos.y , 0);
        shapeRenderer.rotate(0, 0, 1, bodyAngle);
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
