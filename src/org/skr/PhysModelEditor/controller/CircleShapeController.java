package org.skr.PhysModelEditor.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import org.skr.PhysModelEditor.PhysWorld;
import org.skr.physmodel.ShapeDescription;

/**
 * Created by rat on 13.06.14.
 */
public class CircleShapeController extends ShapeController{

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
    }

    @Override
    protected void createControlPoints() {
        for ( ShapeDescription shd : fixtureSetDescription.getShapeDescriptions() ) {
            createShapeControlPoints( shd );
        }
    }

    protected void createShapeControlPoints( ShapeDescription shapeDescription ) {
        float x = PhysWorld.get().toView( shapeDescription.getPosition().x );
        float y = PhysWorld.get().toView( shapeDescription.getPosition().y );
        float r = PhysWorld.get().toView( shapeDescription.getRadius() );

        CircleControlPoint cp = new CircleControlPoint( shapeDescription, CpType.CENTER );
        cp.setPos( x, y );
        controlPoints.add( cp );

        cp = new CircleControlPoint( shapeDescription, CpType.RADIUS );
        cp.setPos( x + r, y );
        controlPoints.add( cp );
    }

    private static final Vector2 tmpV = new Vector2();

    @Override
    protected void updateControlPointFromObject(ControlPoint cp) {
        CircleControlPoint controlPoint = ( CircleControlPoint ) cp;
        ShapeDescription shapeDescription = (ShapeDescription) controlPoint.getObject();
        tmpV.set( shapeDescription.getPosition() );
        PhysWorld.get().toView( tmpV );

        switch ( controlPoint.type ) {
            case CENTER:
                controlPoint.setPos( tmpV.x, tmpV.y );
                break;

            case RADIUS:
                float rad = PhysWorld.get().toView( shapeDescription.getRadius() );
                cp.setPos( tmpV.x + rad, tmpV.y );
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

    @Override
    protected void drawShapeDescription( ShapeDescription shd ) {
        drawCircleShape( shd );
    }


    private static final Vector2 f = new Vector2();
    private static final Vector2 v = new Vector2();
    private static final Vector2 lv = new Vector2();

    private void drawCircleShape( ShapeDescription shd ) {

        float x = PhysWorld.get().toView( shd.getPosition().x );
        float y = PhysWorld.get().toView( shd.getPosition().y );
        float r = PhysWorld.get().toView( shd.getRadius() );

        float angle = 0;
        float angleInc = 2 * (float)Math.PI / 20;
        for (int i = 0; i < 20; i++, angle += angleInc) {
            v.set((float)Math.cos(angle) * r + x, (float)Math.sin(angle) * r + y);
            if (i == 0) {
                lv.set(v);
                f.set(v);
                continue;
            }
            shapeRenderer.line(lv.x, lv.y, v.x, v.y);
            lv.set(v);
        }
        shapeRenderer.line(f.x, f.y, lv.x, lv.y);

    }
}
