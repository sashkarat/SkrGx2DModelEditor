package org.skr.PhysModelEditor.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.scenes.scene2d.Stage;
import org.skr.PhysModelEditor.PhysWorld;
import org.skr.physmodel.BodyItem;

/**
 * Created by rat on 05.07.14.
 */
public class BodyItemController extends Controller {

    BodyItem bodyItem;
    boolean enableMassCorrection = false;
    ControlPoint comPoint;

    public BodyItemController(Stage stage) {
        super(stage);
        comPoint = new ControlPoint( null );
        controlPoints.add( comPoint );
        comPoint.setVisible( enableMassCorrection );
        setEnableBbControl( false );
    }

    public BodyItem getBodyItem() {
        return bodyItem;
    }

    public void setBodyItem(BodyItem bodyItem) {
        this.bodyItem = bodyItem;
        setEnableMassCorrection( false );
    }

    public boolean isEnableMassCorrection() {
        return enableMassCorrection;
    }

    public void setEnableMassCorrection(boolean enableMassCorrection) {
        this.enableMassCorrection = enableMassCorrection;
        comPoint.setVisible( this.enableMassCorrection );
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
        if ( bodyItem == null )
            return;
        if ( bodyItem.getBody().getType() != BodyDef.BodyType.DynamicBody ) {
            cp.setVisible( false );
            return;
        }
        if ( !cp.isVisible() && enableMassCorrection )
            cp.setVisible( true );
        Vector2 c = bodyItem.getBody().getLocalCenter();
        PhysWorld.get().toView(c);
        cp.setPos(c.x, c.y);
    }

    @Override
    protected void moveControlPoint(ControlPoint cp, Vector2 offsetLocal, Vector2 offsetStage) {
        if ( !enableMassCorrection )
            return;

        if ( bodyItem == null )
            return;
        if ( bodyItem.getBody().getType() != BodyDef.BodyType.DynamicBody )
            return;

        cp.offsetPos( offsetLocal.x, offsetLocal.y );
        Vector2 c = bodyItem.getBody().getLocalCenter();
        PhysWorld.get().toPhys( offsetLocal );

        float l = c.len();
        c.add( offsetLocal );
        float l2 = c.len();

        float z = l/l2;

        MassData md = bodyItem.getBody().getMassData();
//        Gdx.app.log("BodyItemController.moveControlPoint", "MassData: M=" + md.mass +
//        " I=" + md.I + " C=" + md.center + " z=" + z );
        md.center.set(c);
        md.I /= ( z * z );
        bodyItem.getBody().setMassData( md );
        bodyItem.setOverrideMassData( true );
    }

    @Override
    protected void movePosControlPoint(ControlPoint cp, Vector2 offsetLocal, Vector2 offsetStage) {
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

    @Override
    protected void updatePosControlPointFromObject(ControlPoint cp) {
        cp.setPos( 0, 0);
    }


    public void setWorldCenterOfMass(float x, float y) {

        if ( !enableMassCorrection )
            return;

        Vector2 c = bodyItem.getBody().getWorldCenter();
        float l = c.len();
        c.set( x, y );
        float l2 = c.len();

        float z = l/l2;

        MassData md = bodyItem.getBody().getMassData();
//        Gdx.app.log("BodyItemController.moveControlPoint", "MassData: M=" + md.mass +
//        " I=" + md.I + " C=" + md.center + " z=" + z );
        md.center.set( c );
        md.I /= ( z * z );
        bodyItem.getBody().setMassData( md );
        bodyItem.setOverrideMassData( true );
    }

    public void resetMassData() {
        bodyItem.getBody().resetMassData();
        bodyItem.setOverrideMassData( false );
    }
}
