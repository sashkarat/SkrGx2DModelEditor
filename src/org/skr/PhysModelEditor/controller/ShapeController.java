package org.skr.PhysModelEditor.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import org.skr.PhysModelEditor.PhysWorld;
import org.skr.physmodel.BodyItem;
import org.skr.physmodel.FixtureSet;
import org.skr.physmodel.FixtureSetDescription;
import org.skr.physmodel.ShapeDescription;

/**
 * Created by rat on 12.06.14.
 */

public abstract class ShapeController extends Controller {

    public static class ShapeControlPoint extends ControlPoint {

        public ShapeControlPoint(ShapeDescription shapeDescription ) {
            super( shapeDescription );
        }

        public ShapeDescription getShapeDescription() {
            return (ShapeDescription) getObject();
        }
    }


    public interface ShapeControllerListener {
        public void controlPointChanged( ShapeDescription shapeDescription, ControlPoint controlPoint );
        public void positionChanged( ShapeDescription shapeDescription );
        public void radiusChanged( ShapeDescription shapeDescription );
    }

    protected static ShapeControllerListener staticShapeControllerListener = null;

    public static void setStaticShapeControllerListener(ShapeControllerListener staticShapeControllerListener) {
        ShapeController.staticShapeControllerListener = staticShapeControllerListener;
    }


    @Override
    protected boolean updateSelection(Vector2 coords) {
        super.updateSelection(coords);

        if ( selectedControlPoint != null ) {
            ShapeDescription shd = getShapeDescription( selectedControlPoint );
            if ( shd == null )
                return true;
            notifyListenerControlPointChanged( shd, selectedControlPoint );
            return true;
        }

        return false;
    }

    protected void notifyListenerControlPointChanged( ShapeDescription shapeDescription,
                                                      ControlPoint controlPoint ) {
        if ( staticShapeControllerListener == null )
            return;
        staticShapeControllerListener.controlPointChanged( shapeDescription, controlPoint );
    }

    protected void notifyListenerPositionChanged( ShapeDescription shapeDescription ) {
        if ( staticShapeControllerListener == null )
            return;
        staticShapeControllerListener.positionChanged( shapeDescription );
    }

    protected void notifyListenerRadiusChanged( ShapeDescription shapeDescription ) {
        if ( staticShapeControllerListener == null )
            return;
        staticShapeControllerListener.radiusChanged( shapeDescription );
    }

    FixtureSetDescription fixtureSetDescription;
    BodyItem bodyItem;

    public ShapeController(Stage stage) {
        super(stage);
        setSelectionMode( ControlPointSelectionMode.SELECT_BY_CLICK );
    }

    protected abstract void createControlPoints();

    public void loadFromFixtureSet( FixtureSet fixtureSet ) {
        fixtureSetDescription = fixtureSet.getDescription();
        bodyItem = fixtureSet.getBodyItem();
        controlPoints.clear();
        createControlPoints();
    }

    public FixtureSetDescription getFixtureSetDescription() {
        return fixtureSetDescription;
    }

    @Override
    protected void translateRendererToObject() {
        if ( fixtureSetDescription == null )
            return;
        shapeRenderer.translate( bodyItem.getX(), bodyItem.getY() , 0);
        shapeRenderer.rotate(0, 0, 1, bodyItem.getRotation() );
    }

    @Override
    protected Vector2 stageToObject(Vector2 stageCoord) {
        return BodyItem.stageToBodyItemLocal( bodyItem, stageCoord );
    }


    protected abstract  void drawShapeDescription( ShapeDescription shd );

    @Override
    protected void draw() {
        if ( fixtureSetDescription == null )
            return;
        shapeRenderer.setColor( 1, 0, 0, 1f);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for ( ShapeDescription shd : fixtureSetDescription.getShapeDescriptions() )
            drawShapeDescription( shd );

        shapeRenderer.setColor( 0, 1, 0.2f, 1);

        shapeRenderer.end();

        drawControlPoints();
    }

    @Override
    protected boolean onMouseClicked(Vector2 localCoord, Vector2 stageCoord) {
        boolean leftCtrl = Gdx.input.isKeyPressed( Input.Keys.CONTROL_LEFT );
        boolean leftShift = Gdx.input.isKeyPressed( Input.Keys.SHIFT_LEFT );
        boolean leftAlt = Gdx.input.isKeyPressed( Input.Keys.ALT_LEFT );

        if ( !leftShift && !leftAlt && leftCtrl) {
            addControlPoint( localCoord );
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
        return fixtureSetDescription;
    }

    protected ShapeDescription getShapeDescription(ControlPoint cp ) {
        if ( !(cp instanceof ShapeControlPoint) )
            return null;
        return ((ShapeControlPoint)cp).getShapeDescription();
    }


    public abstract ShapeDescription createNewShape( float x, float y );

    protected abstract void updateShapeFromControlPoint( ControlPoint cp);

    public  void addNewShape(float x, float y) {
        if ( fixtureSetDescription == null )
            return;
        ShapeDescription shd = createNewShape( x, y );

        if ( shd == null )
            return;

        if ( fixtureSetDescription.getShapeDescriptions().contains(shd, true) )
            return;
        fixtureSetDescription.getShapeDescriptions().add( shd );
    }

    public void setControlPointPosition(float physX, float physY) {
        if ( selectedControlPoint == null )
            return;
        selectedControlPoint.setPos( PhysWorld.get().toView( physX ), PhysWorld.get().toView( physY ) );

        updateShapeFromControlPoint( selectedControlPoint );
    }

    public void setRadius( float physR ) {
        if ( selectedControlPoint == null )
            return;
        ShapeDescription shd = (ShapeDescription) selectedControlPoint.getObject();
        shd.setRadius( physR );
        updateControlPointFromObject(selectedControlPoint);
    }

    public void setLooped( boolean state ) {
        // dumb
    }

    public void setAutoTessellate( boolean state ) {
        // dumb
    }

    protected void shapeDeleted( ShapeDescription shd ) {
        // dumb
    }

    private static final Array< ControlPoint > cpTmp = new Array<ControlPoint>();

    public void deleteCurrentShape() {

        if ( selectedControlPoint == null )
            return;
        ShapeDescription shd = getShapeDescription( selectedControlPoint );

        cpTmp.clear();

        for ( ControlPoint cp : controlPoints ) {
            if ( shd == cp.getObject() )
                cpTmp.add(cp);
        }

        for ( ControlPoint cp : cpTmp )
            removeControlPoint( cp );

        selectedControlPoint = null;

        if ( fixtureSetDescription.getShapeDescriptions().removeValue( shd, true) ) {
            shapeDeleted( shd );
        }

    }

    protected abstract void getShapeViewCenter( ControlPoint cp );
    protected abstract void offsetAllPoints( Vector2 offsetLocal, Vector2 offsetStage);

    @Override
    protected void movePosControlPoint(ControlPoint cp, Vector2 offsetLocal, Vector2 offsetStage) {
        offsetAllPoints( offsetLocal, offsetStage );
    }

    @Override
    protected void updatePosControlPointFromObject(ControlPoint cp) {
        getShapeViewCenter( cp );
    }


}
