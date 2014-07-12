package org.skr.PhysModelEditor.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import org.skr.PhysModelEditor.PhysWorld;
import org.skr.physmodel.JointItemDescription;
import org.skr.physmodel.jointitems.DistanceJointItem;

/**
 * Created by rat on 12.07.14.
 */

public class AnchorPointController extends Controller {


    JointItemDescription jdesc = new JointItemDescription();



    public static class AnchorControlPoint extends ControlPoint {

        public enum AcpType {
            typeA,
            typeB
        }

        AcpType type;

        public AnchorControlPoint(Object object, AcpType type ) {
            super(object);
            this.type = type;
        }
    }


    private void createControlPoints() {
        AnchorControlPoint cp = new AnchorControlPoint( jdesc, AnchorControlPoint.AcpType.typeA );

        jdesc.getAnchorA().set(-1, 0);
        controlPoints.add(cp);
        cp.setColor( new Color(1, 0, 0, 1 ));
        updateControlPointFromObject(cp);

        cp = new AnchorControlPoint( jdesc, AnchorControlPoint.AcpType.typeB );
        jdesc.getAnchorB().set( 1, 0);
        controlPoints.add( cp );
        cp.setColor( new Color( 0, 1, 0, 1 ));
        updateControlPointFromObject( cp );
    }

    public AnchorPointController(Stage stage) {
        super(stage);

        createControlPoints();
    }

    @Override
    protected void translateRendererToObject() {
        // does nothing
    }

    @Override
    protected void draw() {
        drawControlPoints();
    }

    @Override
    protected Vector2 stageToObject(Vector2 stageCoord) {
        return stageCoord;
    }

    @Override
    protected void updateControlPointFromObject(ControlPoint cp) {

        AnchorControlPoint acp = (AnchorControlPoint) cp;


        Vector2 pv = null;

        switch ( acp.type ) {

            case typeA:
                pv = jdesc.getAnchorA();
                break;
            case typeB:
                pv = jdesc.getAnchorB();
                break;
        }

        if ( pv == null )
            return;

        acp.setPos( PhysWorld.get().toView(pv.x), PhysWorld.get().toView(pv.y));
    }

    protected void updateDescFromCp( AnchorControlPoint cp ) {
        Vector2 pv = null;
        switch ( cp.type ) {
            case typeA:
                pv = jdesc.getAnchorA();
                break;
            case typeB:
                pv = jdesc.getAnchorB();
                break;
        }

        if ( pv == null )
            return;

        pv.set( PhysWorld.get().toPhys( cp.getX() ), PhysWorld.get().toPhys( cp.getY() ) );
    }

    @Override
    protected void moveControlPoint(ControlPoint cp, Vector2 offsetLocal, Vector2 offsetStage) {
        cp.setX( cp.getX() + offsetStage.x );
        cp.setY(cp.getY() + offsetStage.y);
        updateDescFromCp((AnchorControlPoint) cp);
    }

    @Override
    protected void rotateAtControlPoint(ControlPoint cp, float angle) {
        // does nothing
    }

    @Override
    protected Object getControlledObject() {
        return jdesc;
    }

    public JointItemDescription getDescription() {
        return jdesc;
    }
}
