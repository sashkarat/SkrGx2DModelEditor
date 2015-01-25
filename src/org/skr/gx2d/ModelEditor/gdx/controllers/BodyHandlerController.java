package org.skr.gx2d.ModelEditor.gdx.controllers;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import org.skr.gx2d.common.Env;
import org.skr.gx2d.editor.Controller;
import org.skr.gx2d.physnodes.BodyHandler;
import org.skr.gx2d.utils.RectangleExt;

/**
 * Created by rat on 05.07.14.
 */
public class BodyHandlerController extends Controller {

    BodyHandler bodyHandler;
    boolean enableMassCorrection = false;
    Controller.ControlPoint comPoint;

    public BodyHandlerController(Stage stage) {
        super(stage);
        comPoint = new ControlPoint( null );
        getControlPoints().add( comPoint );
        comPoint.setVisible( enableMassCorrection );
        for ( ControlPoint cp: getBoundingBoxControlPoints() )
            cp.setVisible( false );
    }

    public BodyHandler getBodyHandler() {
        return bodyHandler;
    }

    public void setBodyHandler(BodyHandler bodyItem) {
        this.bodyHandler = bodyItem;
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
        if ( bodyHandler == null )
            return;
        getShapeRenderer().translate( bodyHandler.getX(), bodyHandler.getY() , 0);
        getShapeRenderer().rotate(0, 0, 1, bodyHandler.getRotation() );
    }

    @Override
    protected void drawStage() {
        getShapeRenderer().begin(ShapeRenderer.ShapeType.Line );
        getShapeRenderer().setColor(1, 1, 0, 1);
        RectangleExt bb = bodyHandler.getBodyBoundingBox();

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
        if ( bodyHandler == null )
            return null;
        return BodyHandler.stageToBodyLocal(bodyHandler, stageCoord);
    }

    @Override
    protected void updateControlPointFromObject(ControlPoint cp) {
        if ( bodyHandler == null )
            return;
        if ( bodyHandler.getBody().getType() != BodyDef.BodyType.DynamicBody ) {
            cp.setVisible( false );
            return;
        }
        if ( !cp.isVisible() && enableMassCorrection )
            cp.setVisible( true );
        Vector2 c = bodyHandler.getBody().getLocalCenter();
        Env.get().world.toView(c);
        cp.setPos(c.x, c.y);
    }

    @Override
    protected void moveControlPoint(ControlPoint cp, Vector2 offsetLocal, Vector2 offsetStage, final Vector2 posLocal, final Vector2 posStage ) {
        if ( !enableMassCorrection )
            return;

        if ( bodyHandler == null )
            return;
        if ( bodyHandler.getBody().getType() != BodyDef.BodyType.DynamicBody )
            return;

        cp.offsetPos( offsetLocal.x, offsetLocal.y );
        Vector2 c = bodyHandler.getBody().getLocalCenter();
        Env.get().world.toPhys(offsetLocal);

        float l = c.len();
        c.add( offsetLocal );
        float l2 = c.len();

        float z = l/l2;

        //todo: think about it

//        MassData md = bodyHandler.getBody().getMassData();
////        Gdx.app.log("BodyHandlerController.moveControlPoint", "MassData: M=" + md.mass +
////        " I=" + md.I + " C=" + md.center + " z=" + z );
//        md.center.set(c);
//        md.I /= ( z * z );
//        bodyHandler.getBody().setMassData( md );
//        bodyHandler.setOverrideMassData( true );
    }


    private static final Vector2 tmpPos = new Vector2();

    @Override
    protected void movePosControlPoint(ControlPoint cp, Vector2 offsetLocal, Vector2 offsetStage,
                                       final Vector2 posLocal, final Vector2 posStage ) {
        if ( bodyHandler == null )
            return;
        cp.setPos( posStage.x, posStage.y );
        snapToGrid( cp, 5, 5, 2 );
        snapTo(cp, 0, 0, 10 );
        tmpPos.set( cp.getX(), cp.getY() );
        Env.get().world.toPhys(tmpPos);
        bodyHandler.getBody().setTransform(tmpPos, bodyHandler.getBody().getAngle());
    }

    @Override
    protected void rotateAtControlPoint(ControlPoint cp, float angle) {
//        Gdx.app.log("BodyHandlerController.rotateAtControlPoint", " ang: " + angle);
     }

    @Override
    protected Object getControlledObject() {
        return bodyHandler;
    }

    @Override
    protected void updatePosControlPointFromObject(ControlPoint cp) {
        cp.setPos( 0, 0);
    }


//    public void setWorldCenterOfMass(float x, float y) {
//
//        if ( !enableMassCorrection )
//            return;
//
//        Vector2 c = bodyHandler.getBody().getWorldCenter();
//        float l = c.len();
//        c.set(x, y);
//        float l2 = c.len();
//
//        float z = l/l2;
//
//        MassData md = bodyHandler.getBody().getMassData();
////        Gdx.app.log("BodyHandlerController.moveControlPoint", "MassData: M=" + md.mass +
////        " I=" + md.I + " C=" + md.center + " z=" + z );
//        md.center.set(c);
//        md.I /= ( z * z );
//        bodyHandler.getBody().setMassData( md );
//        bodyHandler.setOverrideMassData(true);
//    }
//
//    public void resetMassData() {
//        bodyHandler.getBody().resetMassData();
//        bodyHandler.setOverrideMassData(false);
//    }
}
