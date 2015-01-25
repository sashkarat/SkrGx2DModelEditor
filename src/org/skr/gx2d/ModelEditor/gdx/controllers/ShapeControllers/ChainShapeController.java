package org.skr.gx2d.ModelEditor.gdx.controllers.ShapeControllers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import org.skr.gx2d.ModelEditor.gdx.controllers.ShapeController;
import org.skr.gx2d.common.Env;
import org.skr.gx2d.physnodes.physdef.ShapeDefinition;

/**
 * Created by rat on 14.06.14.
 */
public class ChainShapeController extends ShapeController {


    public class ChainControlPoint extends ShapeControlPoint {

        Vector2 vertex = new Vector2();

        public ChainControlPoint(ShapeDefinition shapeDef, Vector2 vertex) {
            super(shapeDef);
            this.vertex = vertex;
        }

        public Vector2 getVertex() {
            return vertex;
        }

        public void setVertex(Vector2 vertex) {
            this.vertex = vertex;
        }
    }

    public ChainShapeController(Stage stage) {
        super(stage);
    }

    @Override
    protected void createControlPoints() {
        getControlPoints().clear();

        for ( ShapeDefinition shd : getFxDef().getShapeDefArray() ) {
            createShapeControlPoints( shd );
        }
    }

    protected void createShapeControlPoints( ShapeDefinition shd ) {
        for ( Vector2 v : shd.getVertices() ) {
            Vector2 viewVertex = Env.get().world.viewToPhys( v );

            ChainControlPoint cp = new ChainControlPoint( shd, v );
            cp.setPos( viewVertex.x, viewVertex.y );

            getControlPoints().add( cp );
        }
    }



    private  final static  Vector2 v1 = new Vector2();
    private  final static  Vector2 v2 = new Vector2();

    @Override
    protected void drawShapeDef(ShapeDefinition shd) {

        int c = shd.getVertices().size - 1;
        for ( int i = 0; i < c; i++ ) {
            v1.set( Env.get().world.physToView(shd.getVertices().get(i))  );
            v2.set( Env.get().world.physToView(shd.getVertices().get(i + 1)) );
            getShapeRenderer().line( v1, v2 );
        }

        if ( shd.isLooped() && shd.getVertices().size > 2) {

            v1.set( Env.get().world.physToView(shd.getVertices().get(0))  );
            v2.set( Env.get().world.physToView(shd.getVertices().get(shd.getVertices().size - 1)) );
            getShapeRenderer().line( v1, v2 );
        }

    }

    @Override
    public ShapeDefinition createNewShape(float x, float y) {
        ShapeDefinition shd = new ShapeDefinition();
        shd.getVertices().add( new Vector2( x, y ) );
        createShapeControlPoints( shd );
        return shd;
    }

    @Override
    protected void updateShapeFromControlPoint(ControlPoint cp) {
        ChainControlPoint ccp = (ChainControlPoint) cp;
        Vector2 vertex = ccp.getVertex();
        vertex.set( Env.get().world.toPhys(cp.getX()), Env.get().world.toPhys(cp.getY()) );
    }

    @Override
    protected void updateControlPointFromObject(ControlPoint cp) {
        ChainControlPoint ccp = (ChainControlPoint) cp;
        Vector2 vertex = ccp.getVertex();
        cp.setPos( Env.get().world.toView(vertex.x), Env.get().world.toView(vertex.y)  );
//        Gdx.app.log("ChainShapeController.updateControlPointFromObject",
//                "ID:" + cp.getId() + " Pos: " + cp.getX() + " " + cp.getY() + " vertex: " + vertex );
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


    @Override
    public void setLooped(boolean state) {
        if ( getControlPoints().size == 0 )
            return;
        ShapeDefinition shd = getShapeDef(getControlPoints().get(0));
        shd.setLooped( state );
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
    protected void addControlPoint( Vector2 localCoord ) {

        if ( getControlPoints().size == 0 )
            return;

        ShapeDefinition shd = getShapeDef(getControlPoints().get(0));

        Vector2 physCoord = Env.get().world.viewToPhys( localCoord );

        shd.getVertices().add( new Vector2( physCoord ) );
        ChainControlPoint cp = new ChainControlPoint( shd,
                shd.getVertices().get( shd.getVertices().size -1 ) );
        getControlPoints().add( cp );

    }

    @Override
    protected boolean removeControlPoint( Vector2 localCoord ) {

        if ( getControlPoints().size < 2 )
            return false;

        ChainControlPoint cp = null;

        for ( ControlPoint p : getControlPoints()) {
            if ( p.contains(localCoord) ) {
                cp = (ChainControlPoint) p;
                break;
            }
        }

        if ( cp == null )
            return false;

        ShapeDefinition shd = getShapeDef(cp);

        shd.getVertices().removeValue( cp.getVertex(), true );

        removeControlPoint( cp );

        return true;
    }

}
