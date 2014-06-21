package org.skr.PhysModelEditor.controller;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import org.skr.PhysModelEditor.PhysWorld;
import org.skr.physmodel.ShapeDescription;

/**
 * Created by rat on 14.06.14.
 */
public class EdgeShapeController extends ShapeController {


    private enum EdgeCpType {
        VERTEX1,
        VERTEX2
    }


    public class EdgeControlPoint extends ShapeControlPoint {

        EdgeCpType type;

        public EdgeControlPoint(ShapeDescription shapeDescription, EdgeCpType type) {
            super(shapeDescription);
            this.type = type;
        }

        public EdgeCpType getType() {
            return type;
        }
    }

    public EdgeShapeController(Stage stage) {
        super(stage);


    }

    @Override
    protected void createControlPoints() {
        for ( ShapeDescription shd : fixtureSetDescription.getShapeDescriptions() )
            createShapeControlPoints( shd );
    }

    private static final Vector2 v1 = new Vector2();
    private static final Vector2 v2 = new Vector2();


    protected void createShapeControlPoints( ShapeDescription shapeDescription ) {

        v1.set( PhysWorld.get().physToView( shapeDescription.getVertices().get( 0 ) ) );
        v2.set( PhysWorld.get().physToView( shapeDescription.getVertices().get( 1 ) ) );


        EdgeControlPoint cp1 = new EdgeControlPoint( shapeDescription, EdgeCpType.VERTEX1 );
        EdgeControlPoint cp2 = new EdgeControlPoint( shapeDescription, EdgeCpType.VERTEX2 );
        cp1.setPos( v1.x, v1.y );
        cp2.setPos( v2.x, v2.y );
        cp2.setColor( new Color( 0, 0.2f, 1f, 1 ) );

        controlPoints.add( cp1 );
        controlPoints.add( cp2 );

    }

    @Override
    protected void drawShapeDescription(ShapeDescription shapeDescription) {

        v1.set( PhysWorld.get().physToView( shapeDescription.getVertices().get( 0 ) ) );
        v2.set( PhysWorld.get().physToView( shapeDescription.getVertices().get( 1 ) ) );

        shapeRenderer.line( v1, v2);

    }

    @Override
    public ShapeDescription createNewShape(float x, float y) {

        ShapeDescription shd = new ShapeDescription();
        shd.getVertices().add( new Vector2(x, y) );
        shd.getVertices().add( new Vector2(x + 1, y) );
        createShapeControlPoints( shd );
        return shd;
    }

    @Override
    protected void updateShapeFromControlPoint(ControlPoint cp) {
        EdgeControlPoint ecp = (EdgeControlPoint) cp;
        ShapeDescription shd = getShapeDescription(cp);

        float x = PhysWorld.get().toPhys(cp.getX());
        float y = PhysWorld.get().toPhys(cp.getY());


        switch ( ecp.getType() ) {
            case VERTEX1:
                shd.getVertices().get(0).set( x, y );
                break;
            case VERTEX2:
                shd.getVertices().get(1).set( x, y );
                break;
        }

    }

    @Override
    protected void updateControlPointFromShape( ControlPoint cp ) {
        EdgeControlPoint ecp = (EdgeControlPoint) cp;
        ShapeDescription shd = getShapeDescription( cp );

        float x = 0;
        float y = 0;

        switch ( ecp.getType() ) {
            case VERTEX1:
                x = shd.getVertices().get(0).x;
                y = shd.getVertices().get(0).y;

                break;
            case VERTEX2:
                x = shd.getVertices().get(1).x;
                y = shd.getVertices().get(1).y;
                break;
        }

        cp.setPos( PhysWorld.get().toView( x ), PhysWorld.get().toView( y ) );

    }

    @Override
    protected void moveControlPoint(ControlPoint cp, Vector2 offsetLocal, Vector2 offsetStage) {
        ShapeDescription shapeDescription = getShapeDescription( cp );

        cp.offsetPos( offsetLocal.x, offsetLocal.y );

        updateShapeFromControlPoint( cp );

        notifyListenerControlPointChanged( shapeDescription, cp );
    }

    @Override
    protected void rotateAtControlPoint(ControlPoint cp, float angle) {

    }
}
