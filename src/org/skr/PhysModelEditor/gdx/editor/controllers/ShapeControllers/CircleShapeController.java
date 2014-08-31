package org.skr.PhysModelEditor.gdx.editor.controllers.ShapeControllers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import org.skr.PhysModelEditor.gdx.editor.controllers.ShapeController;
import org.skr.gdx.PhysWorld;
import org.skr.gdx.physmodel.ShapeDescription;

/**
 * Created by rat on 13.06.14.
 */
public class CircleShapeController extends ShapeController {

    float defaultRadius = 1;

    protected enum CpType {
        CENTER,
        RADIUS
    }

    public static class CircleControlPoint extends ShapeControlPoint {

        CpType type;

        public CircleControlPoint(ShapeDescription shapeDescription, CpType type) {
            super( shapeDescription );
            this.type = type;
        }

        public CpType getType() {
            return type;
        }

        public void setType(CpType type) {
            this.type = type;
        }
    }

    public float getDefaultRadius() {
        return defaultRadius;
    }

    public void setDefaultRadius(float defaultRadius) {
        this.defaultRadius = defaultRadius;
    }

    public CircleShapeController(Stage stage) {
        super(stage);
        setEnableBbControl( false );
    }

    @Override
    protected void createControlPoints() {
        for ( ShapeDescription shd : getFixtureSetDescription().getShapeDescriptions() ) {
            createShapeControlPoints( shd );
        }
    }

    protected void createShapeControlPoints( ShapeDescription shapeDescription ) {
        float x = PhysWorld.get().toView( shapeDescription.getPosition().x );
        float y = PhysWorld.get().toView( shapeDescription.getPosition().y );
        float r = PhysWorld.get().toView( shapeDescription.getRadius() );

        CircleControlPoint cp = new CircleControlPoint( shapeDescription, CpType.CENTER );
        cp.setPos( x, y );
        getControlPoints().add( cp );

        cp = new CircleControlPoint( shapeDescription, CpType.RADIUS );
        cp.setPos( x + r, y );
        getControlPoints().add( cp );
    }

    private static final Vector2 tmpV2 = new Vector2();

    @Override
    protected void updateControlPointFromObject(ControlPoint cp) {
        CircleControlPoint controlPoint = ( CircleControlPoint ) cp;
        ShapeDescription shapeDescription = (ShapeDescription) controlPoint.getObject();
        tmpV2.set( shapeDescription.getPosition() );
        PhysWorld.get().toView( tmpV2 );

        switch ( controlPoint.type ) {
            case CENTER:
                controlPoint.setPos( tmpV2.x, tmpV2.y );
                break;

            case RADIUS:
                float rad = PhysWorld.get().toView( shapeDescription.getRadius() );
                cp.setPos( tmpV2.x + rad, tmpV2.y );
                break;
        }
    }




    @Override
    protected void moveControlPoint(ControlPoint cp, Vector2 offsetLocal, Vector2 offsetStage) {

        CircleControlPoint controlPoint = ( CircleControlPoint ) cp;
        ShapeDescription shapeDescription = (ShapeDescription) controlPoint.getObject();

        cp.offsetPos( offsetLocal.x, offsetLocal.y );

        updateShapeFromControlPoint( cp );

        notifyListenerControlPointChanged( shapeDescription, cp );

    }

    @Override
    protected void rotateAtControlPoint(ControlPoint cp, float angle) {

    }

    @Override
    public ShapeDescription createNewShape(float x, float y) {
        ShapeDescription shd = new ShapeDescription();
        shd.setPosition( new Vector2( x, y) );
        shd.setRadius( defaultRadius );
        createShapeControlPoints( shd );
        return shd;
    }

    @Override
    protected void updateShapeFromControlPoint(ControlPoint cp) {

        CircleControlPoint controlPoint = ( CircleControlPoint ) cp;
        ShapeDescription shapeDescription = (ShapeDescription) controlPoint.getObject();

        tmpV.set( controlPoint.getX(), controlPoint.getY() );
        PhysWorld.get().toPhys( tmpV );

        switch ( controlPoint.type ) {
            case CENTER:
                shapeDescription.setPosition(tmpV);
                notifyListenerPositionChanged( shapeDescription );
                break;

            case RADIUS:
                float rad = Math.abs( shapeDescription.getPosition().dst( tmpV ) );

                shapeDescription.setRadius( rad );
                notifyListenerRadiusChanged( shapeDescription );
                break;
        }
    }

    private static final Vector2 tmpV = new Vector2();

    @Override
    protected void getShapeViewCenter(ControlPoint cp) {

        tmpV.set(0, 0);

        for ( ShapeDescription shd : getFixtureSetDescription().getShapeDescriptions() ) {
            tmpV.add( shd.getPosition() );
        }

        tmpV.scl( 1.0f / getFixtureSetDescription().getShapeDescriptions().size );

        PhysWorld.get().toView( tmpV );

        cp.setPos( tmpV.x, tmpV.y);
    }

    @Override
    protected void offsetAllPoints(Vector2 offsetLocal, Vector2 offsetStage) {
        for ( ControlPoint cp: getControlPoints() ) {
            moveControlPoint( cp, offsetLocal, offsetStage);
        }
    }

    @Override
    protected void drawShapeDescription( ShapeDescription shd ) {
        drawCircleShape( shd );
    }


    private void drawCircleShape( ShapeDescription shd ) {

        float x = PhysWorld.get().toView( shd.getPosition().x );
        float y = PhysWorld.get().toView( shd.getPosition().y );
        float r = PhysWorld.get().toView( shd.getRadius() );

        getShapeRenderer().solidCircle(x, y, r );

    }
}
