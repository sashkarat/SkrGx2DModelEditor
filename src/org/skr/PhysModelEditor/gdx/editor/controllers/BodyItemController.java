package org.skr.PhysModelEditor.gdx.editor.controllers;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import org.skr.gdx.PhysWorld;
import org.skr.gdx.editor.Controller;
import org.skr.gdx.physmodel.bodyitem.BodyItem;
import org.skr.gdx.utils.RectangleExt;

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
        getControlPoints().add( comPoint );
        comPoint.setVisible( enableMassCorrection );
        for ( ControlPoint cp: getBoundingBoxControlPoints() )
            cp.setVisible( false );
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
        getShapeRenderer().translate( bodyItem.getX(), bodyItem.getY() , 0);
        getShapeRenderer().rotate(0, 0, 1, bodyItem.getRotation() );
    }

    @Override
    protected void drawStage() {
        getShapeRenderer().begin(ShapeRenderer.ShapeType.Line );
        getShapeRenderer().setColor(1, 1, 0, 1);
        RectangleExt bb = bodyItem.getBodyItemBoundingBox();

        getShapeRenderer().rect( bb.getLeft(), bb.getBottom(),
                bb.getWidth(), bb.getHeight() );

        getShapeRenderer().end();
    }

    @Override
    protected void drawLocal() {
        getShapeRenderer().setColor(0, 1, 0, 1);
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
