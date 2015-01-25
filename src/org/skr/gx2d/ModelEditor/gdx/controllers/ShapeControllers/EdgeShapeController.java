package org.skr.gx2d.ModelEditor.gdx.controllers.ShapeControllers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import org.skr.gx2d.ModelEditor.gdx.controllers.ShapeController;
import org.skr.gx2d.common.Env;
import org.skr.gx2d.physnodes.physdef.ShapeDefinition;

/**
 * Created by rat on 14.06.14.
 */
public class EdgeShapeController extends ShapeController {


    private enum EdgeCpType {
        VERTEX1,
        VERTEX2
    }


    public class EdgeControlPoint extends ShapeController.ShapeControlPoint {

        EdgeCpType type;

        public EdgeControlPoint(ShapeDefinition shapeDescription, EdgeCpType type) {
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
        for ( ShapeDefinition shd : getFxDef().getShapeDefArray() )
            createShapeControlPoints( shd );
    }

    private static final Vector2 v1 = new Vector2();
    private static final Vector2 v2 = new Vector2();


    protected void createShapeControlPoints( ShapeDefinition shapeDescription ) {

        v1.set( Env.get().world.physToView(shapeDescription.getVertices().get(0)) );
        v2.set( Env.get().world.physToView(shapeDescription.getVertices().get(1)) );


        EdgeControlPoint cp1 = new EdgeControlPoint( shapeDescription, EdgeCpType.VERTEX1 );
        EdgeControlPoint cp2 = new EdgeControlPoint( shapeDescription, EdgeCpType.VERTEX2 );
        cp1.setPos( v1.x, v1.y );
        cp2.setPos( v2.x, v2.y );
        cp2.setColor( new Color( 0, 0.2f, 1f, 1 ) );

        getControlPoints().add( cp1 );
        getControlPoints().add( cp2 );

    }


    @Override
    protected void drawShapeDef(ShapeDefinition shd) {
        v1.set( Env.get().world.physToView(shd.getVertices().get(0)) );
        v2.set( Env.get().world.physToView(shd.getVertices().get(1)) );
        getShapeRenderer().line( v1, v2);
    }


    @Override
    public ShapeDefinition createNewShape(float x, float y) {

        ShapeDefinition shd = new ShapeDefinition();
        shd.getVertices().add( new Vector2(x, y) );
        shd.getVertices().add( new Vector2(x + 1, y) );
        createShapeControlPoints( shd );
        return shd;
    }

    @Override
    protected void updateShapeFromControlPoint(ControlPoint cp) {
        EdgeControlPoint ecp = (EdgeControlPoint) cp;
        ShapeDefinition shd = getShapeDef(cp);

        float x = Env.get().world.toPhys(cp.getX());
        float y = Env.get().world.toPhys(cp.getY());


        switch ( ecp.getType() ) {
            case VERTEX1:
                shd.getVertices().get(0).set( x, y );
                break;
            case VERTEX2:
                shd.getVertices().get(1).set( x, y );
                break;
        }

    }


    private static final Vector2 tmpV = new Vector2();

    @Override
    protected void getShapeViewCenter(ControlPoint cp) {
        tmpV.set(0, 0);
        for ( ControlPoint ccp: getControlPoints() )
            tmpV.add( ccp.getX(), ccp.getY());
        tmpV.scl( 1.0f / getControlPoints().size );
        cp.setPos( tmpV.x, tmpV.y );
    }

    @Override
    protected void offsetAllPoints(Vector2 offsetLocal, Vector2 offsetStage, final Vector2 posLocal, final Vector2 posStage ) {
        for ( ControlPoint cp : getControlPoints() )
            moveControlPoint( cp, offsetLocal, offsetStage, posLocal, posStage );
    }

    @Override
    protected void updateControlPointFromObject(ControlPoint cp) {
        EdgeControlPoint ecp = (EdgeControlPoint) cp;
        ShapeDefinition shd = getShapeDef(cp);

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

        cp.setPos( Env.get().world.toView(x), Env.get().world.toView(y) );

    }

    @Override
    protected void moveControlPoint(ControlPoint cp, Vector2 offsetLocal, Vector2 offsetStage, final Vector2 posLocal, final Vector2 posStage ) {
        ShapeDefinition shapeDescription = getShapeDef(cp);

        cp.offsetPos( offsetLocal.x, offsetLocal.y );

        updateShapeFromControlPoint( cp );

        notifyListenerControlPointChanged( shapeDescription, cp );
    }

    @Override
    protected void rotateAtControlPoint(ControlPoint cp, float angle) {

    }
}
