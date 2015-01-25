package org.skr.gx2d.ModelEditor.gdx.controllers.ShapeControllers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import org.skr.gx2d.ModelEditor.gdx.controllers.ShapeController;
import org.skr.gx2d.common.Env;
import org.skr.gx2d.physnodes.physdef.ShapeDefinition;

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

        public CircleControlPoint(ShapeDefinition shapeDef, CpType type) {
            super( shapeDef );
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
        for ( ShapeDefinition shd : getFxDef().getShapeDefArray() ) {
            createShapeControlPoints( shd );
        }
    }

    protected void createShapeControlPoints( ShapeDefinition shapeDescription ) {
        float x = Env.get().world.toView(shapeDescription.getPosition().x);
        float y = Env.get().world.toView(shapeDescription.getPosition().y);
        float r = Env.get().world.toView(shapeDescription.getRadius());

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
        ShapeDefinition shapeDescription = (ShapeDefinition) controlPoint.getObject();
        tmpV2.set( shapeDescription.getPosition() );
        Env.get().world.toView(tmpV2);

        switch ( controlPoint.type ) {
            case CENTER:
                controlPoint.setPos( tmpV2.x, tmpV2.y );
                break;

            case RADIUS:
                float rad = Env.get().world.toView(shapeDescription.getRadius());
                cp.setPos( tmpV2.x + rad, tmpV2.y );
                break;
        }
    }




    @Override
    protected void moveControlPoint(ControlPoint cp, Vector2 offsetLocal, Vector2 offsetStage,
                                    final Vector2 posLocal, final Vector2 posStage ) {

        CircleControlPoint controlPoint = ( CircleControlPoint ) cp;
        ShapeDefinition shapeDescription = (ShapeDefinition) controlPoint.getObject();

        cp.setPos( posLocal.x, posLocal.y );
        if ( controlPoint.type == CpType.CENTER ) {
            snapTo( controlPoint, 0, 0, 10 );
        }


        updateShapeFromControlPoint( cp );

        notifyListenerControlPointChanged( shapeDescription, cp );

    }

    @Override
    protected void rotateAtControlPoint(ControlPoint cp, float angle) {

    }

    @Override
    public ShapeDefinition createNewShape(float x, float y) {
        ShapeDefinition shd = new ShapeDefinition();
        shd.setPosition( new Vector2( x, y) );
        shd.setRadius( defaultRadius );
        createShapeControlPoints( shd );
        return shd;
    }

    @Override
    protected void updateShapeFromControlPoint(ControlPoint cp) {

        CircleControlPoint controlPoint = ( CircleControlPoint ) cp;
        ShapeDefinition shapeDescription = (ShapeDefinition) controlPoint.getObject();

        tmpV.set( controlPoint.getX(), controlPoint.getY() );
        Env.get().world.toPhys(tmpV);

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

        for ( ShapeDefinition shd : getFxDef().getShapeDefArray() ) {
            tmpV.add( shd.getPosition() );
        }

        tmpV.scl( 1.0f / getFxDef().getShapeDefArray().size );

        Env.get().world.toView(tmpV);

        cp.setPos( tmpV.x, tmpV.y);
    }

    @Override
    protected void offsetAllPoints(Vector2 offsetLocal, Vector2 offsetStage, final Vector2 posLocal, final Vector2 posStage ) {
        for ( ControlPoint cp: getControlPoints() ) {
            moveControlPoint( cp, offsetLocal, offsetStage, posLocal, posStage);
        }
    }

    @Override
    protected void drawShapeDef(ShapeDefinition shd) {
        drawCircleShape( shd );
    }


    private void drawCircleShape( ShapeDefinition shd ) {

        float x = Env.get().world.toView(shd.getPosition().x);
        float y = Env.get().world.toView(shd.getPosition().y);
        float r = Env.get().world.toView(shd.getRadius());

        getShapeRenderer().solidCircle(x, y, r );

    }
}
