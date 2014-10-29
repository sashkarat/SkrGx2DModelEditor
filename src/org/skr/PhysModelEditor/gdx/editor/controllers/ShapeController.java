package org.skr.PhysModelEditor.gdx.editor.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import org.skr.gdx.PhysWorld;
import org.skr.gdx.editor.Controller;
import org.skr.gdx.physmodel.ShapeDescription;
import org.skr.gdx.physmodel.bodyitem.BodyItem;
import org.skr.gdx.physmodel.bodyitem.fixtureset.FixtureSet;
import org.skr.gdx.physmodel.bodyitem.fixtureset.FixtureSetDescription;

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

        if ( getSelectedControlPoint() != null ) {
            ShapeDescription shd = getShapeDescription( getSelectedControlPoint() );
            if ( shd == null )
                return true;
            notifyListenerControlPointChanged( shd, getSelectedControlPoint() );
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
        getControlPoints().clear();
        createControlPoints();
    }

    public FixtureSetDescription getFixtureSetDescription() {
        return fixtureSetDescription;
    }

    @Override
    protected void translateRendererToObject() {
        if ( fixtureSetDescription == null )
            return;
        getShapeRenderer().translate( bodyItem.getX(), bodyItem.getY() , 0);
        getShapeRenderer().rotate(0, 0, 1, bodyItem.getRotation() );
    }

    @Override
    protected Vector2 stageToObject(Vector2 stageCoord) {
        return BodyItem.stageToBodyItemLocal( bodyItem, stageCoord );
    }


    protected abstract  void drawShapeDescription( ShapeDescription shd );

    @Override
    protected void drawLocal() {
        if ( fixtureSetDescription == null )
            return;
        getShapeRenderer().setColor( 1, 0, 0, 1f);

        getShapeRenderer().begin(ShapeRenderer.ShapeType.Line);

        for ( ShapeDescription shd : fixtureSetDescription.getShapeDescriptions() )
            drawShapeDescription( shd );

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
        if ( getSelectedControlPoint() == null )
            return;
        getSelectedControlPoint().setPos( PhysWorld.get().toView( physX ), PhysWorld.get().toView( physY ) );

        updateShapeFromControlPoint( getSelectedControlPoint() );
    }

    public void setRadius( float physR ) {
        if ( getSelectedControlPoint() == null )
            return;
        ShapeDescription shd = (ShapeDescription) getSelectedControlPoint().getObject();
        shd.setRadius( physR );
        updateControlPointFromObject(getSelectedControlPoint());
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

        if ( getSelectedControlPoint() == null )
            return;
        ShapeDescription shd = getShapeDescription( getSelectedControlPoint() );

        cpTmp.clear();

        for ( ControlPoint cp : getControlPoints() ) {
            if ( shd == cp.getObject() )
                cpTmp.add(cp);
        }

        for ( ControlPoint cp : cpTmp )
            removeControlPoint( cp );

        setSelectedControlPoint( null );

        if ( fixtureSetDescription.getShapeDescriptions().removeValue( shd, true) ) {
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
