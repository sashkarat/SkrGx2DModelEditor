package org.skr.gx2d.ModelEditor.gdx.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import org.skr.gx2d.common.Env;
import org.skr.gx2d.editor.Controller;
import org.skr.gx2d.physnodes.BodyHandler;
import org.skr.gx2d.physnodes.FixtureSet;
import org.skr.gx2d.physnodes.physdef.FixtureSetDefinition;
import org.skr.gx2d.physnodes.physdef.ShapeDefinition;


/**
 * Created by rat on 12.06.14.
 */

public abstract class ShapeController extends Controller {

    public static class ShapeControlPoint extends ControlPoint {

        public ShapeControlPoint(ShapeDefinition shapeDef ) {
            super( shapeDef );
        }

        public ShapeDefinition getShapeDef() {
            return (ShapeDefinition) getObject();
        }
    }


    public interface ShapeControllerListener {
        public void controlPointChanged( ShapeDefinition shapeDescription, ControlPoint controlPoint );
        public void positionChanged( ShapeDefinition shapeDescription );
        public void radiusChanged( ShapeDefinition shapeDescription );
    }

    protected static ShapeControllerListener staticShapeControllerListener = null;

    public static void setStaticShapeControllerListener(ShapeControllerListener staticShapeControllerListener) {
        ShapeController.staticShapeControllerListener = staticShapeControllerListener;
    }


    @Override
    protected boolean updateSelection(Vector2 coords) {
        super.updateSelection(coords);

        if ( getSelectedControlPoint() != null ) {
            ShapeDefinition shd = getShapeDef(getSelectedControlPoint());
            if ( shd == null )
                return true;
            notifyListenerControlPointChanged( shd, getSelectedControlPoint() );
            return true;
        }

        return false;
    }

    protected void notifyListenerControlPointChanged( ShapeDefinition shapeDescription,
                                                      ControlPoint controlPoint ) {
        if ( staticShapeControllerListener == null )
            return;
        staticShapeControllerListener.controlPointChanged( shapeDescription, controlPoint );
    }

    protected void notifyListenerPositionChanged( ShapeDefinition shapeDescription ) {
        if ( staticShapeControllerListener == null )
            return;
        staticShapeControllerListener.positionChanged( shapeDescription );
    }

    protected void notifyListenerRadiusChanged( ShapeDefinition shapeDescription ) {
        if ( staticShapeControllerListener == null )
            return;
        staticShapeControllerListener.radiusChanged( shapeDescription );
    }

    FixtureSetDefinition fxDef;
    BodyHandler bodyHandler;

    public ShapeController(Stage stage) {
        super(stage);
        setSelectionMode( ControlPointSelectionMode.SELECT_BY_CLICK );
    }

    protected abstract void createControlPoints();

    public void loadFromFixtureSet( FixtureSet fixtureSet ) {
        fxDef = fixtureSet.getFsDef();
        bodyHandler = fixtureSet.getBodyHandler();
        getControlPoints().clear();
        createControlPoints();
    }

    public FixtureSetDefinition getFxDef() {
        return fxDef;
    }

    @Override
    protected void translateRendererToObject() {
        if ( fxDef == null )
            return;
        getShapeRenderer().translate(bodyHandler.getX(), bodyHandler.getY(), 0);
        getShapeRenderer().rotate(0, 0, 1, bodyHandler.getRotation() );
    }

    @Override
    protected Vector2 stageToObject(Vector2 stageCoord) {
        return BodyHandler.stageToBodyLocal(bodyHandler, stageCoord);
    }


    protected abstract  void drawShapeDef(ShapeDefinition shd);

    @Override
    protected void drawLocal() {
        if ( fxDef == null )
            return;
        getShapeRenderer().setColor( 1, 0, 0, 1f);

        getShapeRenderer().begin(ShapeRenderer.ShapeType.Line);

        for ( ShapeDefinition shd : fxDef.getShapeDefArray() )
            drawShapeDef(shd);

        getShapeRenderer().setColor( 0, 1, 0.2f, 1);

        getShapeRenderer().end();

        drawControlPoints();
    }

    @Override
    protected boolean onMouseClicked(Vector2 localCoord, Vector2 stageCoord, int button) {

        if ( button != Input.Buttons.LEFT )
            return false;

        boolean leftCtrl = Gdx.input.isKeyPressed( Input.Keys.CONTROL_LEFT );
        boolean leftShift = Gdx.input.isKeyPressed( Input.Keys.SHIFT_LEFT );
        boolean leftAlt = Gdx.input.isKeyPressed( Input.Keys.ALT_LEFT );

        if ( !leftShift && !leftAlt && leftCtrl) {
            addControlPoint( localCoord );
            return true;
        } else if ( !leftShift && leftAlt && !leftCtrl) {
            return removeControlPoint( localCoord );
        }
        return false;
    }

    protected void addControlPoint( Vector2 localCoord ) {
        // dumb
    }

    protected boolean removeControlPoint( Vector2 localCoord ) {
        return false;
    }

    public void tessellatePolygon() {
        // dumb
    }

    public void flush() {
        // dumb
    }

    @Override
    protected Object getControlledObject() {
        return fxDef;
    }

    protected ShapeDefinition getShapeDef(ControlPoint cp) {
        if ( !(cp instanceof ShapeControlPoint) )
            return null;
        return ((ShapeControlPoint)cp).getShapeDef();
    }


    public abstract ShapeDefinition createNewShape( float x, float y );

    protected abstract void updateShapeFromControlPoint( ControlPoint cp);

    public  void addNewShape(float x, float y) {
        if ( fxDef == null )
            return;
        ShapeDefinition shd = createNewShape( x, y );

        if ( shd == null )
            return;

        if ( fxDef.getShapeDefArray().contains(shd, true) )
            return;
        fxDef.getShapeDefArray().add( shd );
    }

    public void setControlPointPosition(float physX, float physY) {
        if ( getSelectedControlPoint() == null )
            return;
        getSelectedControlPoint().setPos( Env.get().world.toView( physX ), Env.get().world.toView(physY) );

        updateShapeFromControlPoint( getSelectedControlPoint() );
    }

    public void setRadius( float physR ) {
        if ( getSelectedControlPoint() == null )
            return;
        ShapeDefinition shd = (ShapeDefinition) getSelectedControlPoint().getObject();
        shd.setRadius( physR );
        updateControlPointFromObject(getSelectedControlPoint());
    }

    public void setLooped( boolean state ) {
        // dumb
    }

    public void setAutoTessellate( boolean state ) {
        // dumb
    }

    protected void shapeDeleted( ShapeDefinition shd ) {
        // dumb
    }

    private static final Array< ControlPoint > cpTmp = new Array<ControlPoint>();

    public void deleteCurrentShape() {

        if ( getSelectedControlPoint() == null )
            return;
        ShapeDefinition shd = getShapeDef(getSelectedControlPoint());

        cpTmp.clear();

        for ( ControlPoint cp : getControlPoints() ) {
            if ( shd == cp.getObject() )
                cpTmp.add(cp);
        }

        for ( ControlPoint cp : cpTmp )
            removeControlPoint( cp );

        setSelectedControlPoint( null );

        if ( fxDef.getShapeDefArray().removeValue( shd, true) ) {
            shapeDeleted( shd );
        }

    }

    protected abstract void getShapeViewCenter( ControlPoint cp );
    protected abstract void offsetAllPoints( Vector2 offsetLocal, Vector2 offsetStage, final Vector2 posLocal, final Vector2 posStage );

    @Override
    protected void movePosControlPoint(ControlPoint cp, Vector2 offsetLocal, Vector2 offsetStage, final Vector2 posLocal, final Vector2 posStage ) {
        offsetAllPoints( offsetLocal, offsetStage, posLocal, posStage );
    }

    @Override
    protected void updatePosControlPointFromObject(ControlPoint cp) {
        getShapeViewCenter( cp );
    }

}
