package org.skr.PhysModelEditor.gdx.editor.controllers;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.scenes.scene2d.Stage;
import org.skr.gdx.PhysWorld;
import org.skr.gdx.editor.controller.Controller;
import org.skr.gdx.physmodel.BodyItem;
import org.skr.gdx.physmodel.JointItemDescription;

/**
 * Created by rat on 12.07.14.
 */

public class AnchorPointController extends Controller {


    JointItemDescription jdesc = new JointItemDescription();

    public enum Mode {
        NoPoints,
        TwoPointsMode,
        OnePointMode,
        OnePointAndAxisMode,
        FourPointsMode
    }

    public static class AnchorControlPoint extends ControlPoint {

        public enum AcpType {
            typeA,
            typeB,
            typeAxis,
            typeC,
            typeD
        }

        AcpType type;

        public AnchorControlPoint(Object object, AcpType type ) {
            super(object);
            this.type = type;
        }
    }

    public static interface BodyItemSelectionListener {
        public void bodySelected( BodyItem bi, int id );
    }

    BodyItem selectedBodyItem_A = null;
    BodyItem selectedBodyItem_B = null;

    BodyItemSelectionListener bodyItemSelectionListener;


    public BodyItemSelectionListener getBodyItemSelectionListener() {
        return bodyItemSelectionListener;
    }

    public void setBodyItemSelectionListener(BodyItemSelectionListener bodyItemSelectionListener) {
        this.bodyItemSelectionListener = bodyItemSelectionListener;
    }

    public void setSelectedBodyItem( BodyItem bi, int selId ) {
        if ( selId == 0 ) {
            selectedBodyItem_A = bi;
        } else {
            selectedBodyItem_B = bi;
        }
    }

    private void createControlPoints() {
        AnchorControlPoint cp = new AnchorControlPoint( jdesc, AnchorControlPoint.AcpType.typeA );

        jdesc.getAnchorA().set(-1, 0);
        getControlPoints().add(cp);
        cp.setColor( new Color(1, 0, 0, 1 ));
        updateControlPointFromObject(cp);

        cp = new AnchorControlPoint( jdesc, AnchorControlPoint.AcpType.typeB );
        jdesc.getAnchorB().set( 1, 0);
        getControlPoints().add( cp );
        cp.setColor( new Color( 0, 1, 0, 1 ));
        updateControlPointFromObject( cp );

        jdesc.getAxis().set( 0, 1);

        cp = new AnchorControlPoint( jdesc, AnchorControlPoint.AcpType.typeAxis );
        getControlPoints().add( cp );
        cp.setColor( new Color( 0, 1, 1, 1 ));
        updateControlPointFromObject( cp );

        cp = new AnchorControlPoint( jdesc, AnchorControlPoint.AcpType.typeC );
        jdesc.getGroundAnchorA().set(-1, 1);
        getControlPoints().add(cp);
        cp.setColor( new Color(1f, 0.2f, 0.7f, 1 ));
        updateControlPointFromObject(cp);

        cp = new AnchorControlPoint( jdesc, AnchorControlPoint.AcpType.typeD );
        jdesc.getGroundAnchorB().set( 1, 1);
        getControlPoints().add( cp );
        cp.setColor( new Color( 0.2f, 1f, 0.7f, 1 ));
        updateControlPointFromObject( cp );
    }

    public AnchorPointController(Stage stage) {
        super(stage);
        createControlPoints();
        setPosControlPoint( null );
        setEnableBbControl( false );
    }

    @Override
    protected void translateRendererToObject() {
        // does nothing
    }

    void drawBodyItemSelection( BodyItem bi, Color c ) {
        Vector2 center = bi.getBody().getWorldCenter();
        PhysWorld.get().toView( center );

        getShapeRenderer().setColor( c );
        getShapeRenderer().begin(ShapeRenderer.ShapeType.Line);
        getShapeRenderer().solidCircle( center.x, center.y, 20 );
        getShapeRenderer().end();
    }

    private static final Color cA = new Color( 1, 0,0,1);
    private static final Color cB = new Color( 0, 1,0,1);

    @Override
    protected void draw() {
        drawControlPoints();

        if ( selectedBodyItem_A != null ) {
            drawBodyItemSelection( selectedBodyItem_A, cA );
        }
        if ( selectedBodyItem_B != null ) {
            drawBodyItemSelection( selectedBodyItem_B, cB );
        }

        if ( getControlPoints().get(2).isVisible() ) {
            ControlPoint ax = getControlPoints().get(2);
            float x = ax.getX() * 1000;
            float y = ax.getY() * 1000;
            getShapeRenderer().begin(ShapeRenderer.ShapeType.Line);
            getShapeRenderer().line( 0,0, x, y );
            getShapeRenderer().line( 0,0, -x, -y );
            getShapeRenderer().end();
        }
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
            case typeAxis:
                pv = jdesc.getAxis();
                break;
            case typeC:
                pv = jdesc.getGroundAnchorA();
                break;
            case typeD:
                pv = jdesc.getGroundAnchorB();
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
            case typeAxis:
                pv = jdesc.getAxis();
                break;
            case typeC:
                pv = jdesc.getGroundAnchorA();
                break;
            case typeD:
                pv = jdesc.getGroundAnchorB();
                break;
        }

        if ( pv == null )
            return;

        pv.set(PhysWorld.get().toPhys(cp.getX()), PhysWorld.get().toPhys(cp.getY()));
    }

    @Override
    protected void moveControlPoint(ControlPoint cp, Vector2 offsetLocal, Vector2 offsetStage) {
        cp.setX( cp.getX() + offsetStage.x );
        cp.setY(cp.getY() + offsetStage.y);
        updateDescFromCp((AnchorControlPoint) cp);
    }

    @Override
    protected void movePosControlPoint(ControlPoint cp, Vector2 offsetLocal, Vector2 offsetStage) {

    }

    @Override
    protected void rotateAtControlPoint(ControlPoint cp, float angle) {
        // does nothing
    }

    @Override
    protected Object getControlledObject() {
        return jdesc;
    }

    @Override
    protected void updatePosControlPointFromObject(ControlPoint cp) {

    }

    public JointItemDescription getDescription() {
        return jdesc;
    }


    public void setMode( Mode mode ) {

        for( ControlPoint cp :  getControlPoints() )
        cp.setVisible( false );

        setControlPointVisible( getControlPoints().get(0), true);

        switch ( mode ) {
            case NoPoints:
                setControlPointVisible( getControlPoints().get(0), false );
                break;
            case TwoPointsMode:
                setControlPointVisible( getControlPoints().get(1), true );
                break;
            case OnePointMode:
                break;
            case OnePointAndAxisMode:
                setControlPointVisible( getControlPoints().get(2), true );
                break;
            case FourPointsMode:
                setControlPointVisible( getControlPoints().get(1), true );
                setControlPointVisible( getControlPoints().get(3), true );
                setControlPointVisible( getControlPoints().get(4), true );
                break;
        }
    }

    @Override
    protected boolean onMouseClicked(Vector2 localCoord, Vector2 stageCoord, int button) {
        if ( button == Input.Buttons.LEFT && getSelectedControlPoint() == null ) {
            //TODO: recode this;
        }

        return false;
    }

    private final static Vector2 localV = new Vector2();


}
