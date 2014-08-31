package org.skr.PhysModelEditor.gdx.editor.controllers;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import org.skr.gdx.PhysWorld;
import org.skr.gdx.editor.Controller;
import org.skr.gdx.physmodel.BodyItem;
import org.skr.gdx.physmodel.FixtureSet;
import org.skr.gdx.physmodel.JointItemDescription;
import org.skr.gdx.physmodel.PhysModel;

/**
 * Created by rat on 12.07.14.
 */

public class JointCreatorController extends Controller {


    PhysModel model;
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
        public void bodyASelected( BodyItem bi );
        public void bodyBSelected( BodyItem bi );
    }

    BodyItem selectedBodyItemA = null;
    BodyItem selectedBodyItemB = null;

    BodyItemSelectionListener bodyItemSelectionListener;

    Mode mode = Mode.NoPoints;

    public BodyItemSelectionListener getBodyItemSelectionListener() {
        return bodyItemSelectionListener;
    }

    public void setBodyItemSelectionListener(BodyItemSelectionListener bodyItemSelectionListener) {
        this.bodyItemSelectionListener = bodyItemSelectionListener;
    }

    boolean selectBodyItemA = true;

    public void setSelectedBodyItem( BodyItem bi ) {
        if (selectBodyItemA) {
            selectedBodyItemA = bi;
            selectBodyItemA = false;
            if ( bodyItemSelectionListener != null )
                bodyItemSelectionListener.bodyASelected( bi );
        } else {
            selectedBodyItemB = bi;
            selectBodyItemA = true;
            if ( bodyItemSelectionListener != null )
                bodyItemSelectionListener.bodyBSelected( bi );
        }
    }

    public BodyItem getSelectedBodyItemA() {
        return selectedBodyItemA;
    }

    public void setSelectedBodyItemA(BodyItem selectedBodyItemA) {
        this.selectedBodyItemA = selectedBodyItemA;
    }

    public BodyItem getSelectedBodyItemB() {
        return selectedBodyItemB;
    }

    public void setSelectedBodyItemB(BodyItem selectedBodyItemB) {
        this.selectedBodyItemB = selectedBodyItemB;
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

    public JointCreatorController(Stage stage) {
        super(stage);
        createControlPoints();
        setPosControlPoint( null );
        setEnableBbControl( false );
    }

    public PhysModel getModel() {
        return model;
    }

    public void setModel(PhysModel model) {
        this.model = model;
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

        if ( selectedBodyItemA != null ) {
            drawBodyItemSelection(selectedBodyItemA, cA );
        }
        if ( selectedBodyItemB != null ) {
            drawBodyItemSelection(selectedBodyItemB, cB );
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

        this.mode = mode;

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
        if ( button == Input.Buttons.MIDDLE && getSelectedControlPoint() == null ) {
            return processBodyItemSelection( stageCoord );
        }
        return false;
    }

    @Override
    protected boolean onMouseDoubleClicked(Vector2 localCoord, Vector2 stageCoord, int button) {
        if ( button == Input.Buttons.LEFT && getSelectedControlPoint() == null )
            offsetControlPoints( stageCoord );
        return false;
    }


    protected void offsetControlPoints( Vector2 stageCoord ) {
        AnchorControlPoint cp = (AnchorControlPoint) getControlPoints().get(0);
        Vector2 offset = getAveragePosition();
        offset.set( stageCoord.sub(offset));

        cp.offsetPos( offset.x, offset.y);
        updateDescFromCp(cp);
        cp = (AnchorControlPoint) getControlPoints().get(1);
        cp.offsetPos(offset.x, offset.y);
        updateDescFromCp(cp);
        cp = (AnchorControlPoint) getControlPoints().get(3);
        cp.offsetPos(offset.x, offset.y);
        updateDescFromCp(cp);
        cp = (AnchorControlPoint) getControlPoints().get(4);
        cp.offsetPos(offset.x, offset.y);
        updateDescFromCp(cp);
    }

    private Vector2 getAveragePosition() {
        Vector2 pos = new Vector2();

        AnchorControlPoint cp = (AnchorControlPoint) getControlPoints().get(0);
        pos.set( cp.getX(), cp.getY() );
        switch ( mode ) {
            case NoPoints:
                return pos;
            case TwoPointsMode:
                cp = (AnchorControlPoint) getControlPoints().get(1);
                pos.add( cp.getX(), cp.getY() );
                pos.scl( 0.5f, 0.5f );
                break;
            case OnePointMode:
                break;
            case OnePointAndAxisMode:
                break;
            case FourPointsMode:
                cp = (AnchorControlPoint) getControlPoints().get(1);
                pos.add( cp.getX(), cp.getY() );
                cp = (AnchorControlPoint) getControlPoints().get(3);
                pos.add( cp.getX(), cp.getY() );
                cp = (AnchorControlPoint) getControlPoints().get(4);
                pos.add( cp.getX(), cp.getY() );
                pos.scl(0.25f, 0.25f);
                break;
        }

        return pos;
    }

    private boolean processBodyItemSelection( Vector2 stageCoord ) {
        if ( model == null )
            return false;

        Vector2 localC = new Vector2();

        BodyItem selection = null;

        for ( BodyItem bi : model.getBodyItems() ) {
            localC.set(stageCoord);
            bi.parentToLocalCoordinates(localC);
            FixtureSet fs = bi.getFixtureSet( localC );
            if ( fs != null ) {
                selection = bi;
//                Gdx.app.log("JointCreatorController.processBodyItemSelection", " FS: " + fs.getName() );
                break;
            }
        }

        if ( selection == null )
            return false;

//        Gdx.app.log("JointCreatorController.processBodyItemSelection", "BI: " + selection.getName());
        setSelectedBodyItem( selection );
        return true;
    }

}
