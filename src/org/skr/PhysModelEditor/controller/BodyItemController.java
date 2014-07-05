package org.skr.PhysModelEditor.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Stage;
import org.skr.PhysModelEditor.PhysWorld;
import org.skr.physmodel.BodyItem;

/**
 * Created by rat on 05.07.14.
 */
public class BodyItemController extends Controller {

    BodyItem bodyItem;

    public BodyItemController(Stage stage) {
        super(stage);
        controlPoints.add( new ControlPoint( null ) );
    }

    public BodyItem getBodyItem() {
        return bodyItem;
    }

    public void setBodyItem(BodyItem bodyItem) {
        this.bodyItem = bodyItem;
    }

    @Override
    protected void translateRendererToObject() {
        if ( bodyItem == null )
            return;
        shapeRenderer.translate( bodyItem.getX(), bodyItem.getY() , 0);
        shapeRenderer.rotate(0, 0, 1, bodyItem.getRotation() );
    }

    @Override
    protected void draw() {
        shapeRenderer.setColor(0, 1, 0, 1);
        drawControlPoints();
    }

    @Override
    protected Vector2 stageToObject(Vector2 stageCoord) {
        if ( bodyItem == null )
            return null;
        return BodyItem.stageToBodyItemLocal( bodyItem, stageCoord );
    }

    @Override
    protected void updateControlPointFromObject(ControlPoint cp) {

        cp.setPos( 0, 0);

    }

    @Override
    protected void moveControlPoint(ControlPoint cp, Vector2 offsetLocal, Vector2 offsetStage) {
        if ( bodyItem == null )
            return;
        Vector2 pos = bodyItem.getBody().getPosition();
        pos.add(PhysWorld.get().viewToPhys(offsetStage));
        bodyItem.getBody().setTransform( pos, bodyItem.getBody().getAngle() );
    }

    @Override
    protected void rotateAtControlPoint(ControlPoint cp, float angle) {
//        Gdx.app.log("BodyItemController.rotateAtControlPoint", " ang: " + angle);
     }

    @Override
    protected Object getControlledObject() {
        return bodyItem;
    }
}
