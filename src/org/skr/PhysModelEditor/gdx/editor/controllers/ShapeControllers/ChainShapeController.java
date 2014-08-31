package org.skr.PhysModelEditor.gdx.editor.controllers.ShapeControllers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import org.skr.PhysModelEditor.gdx.editor.controllers.ShapeController;
import org.skr.gdx.PhysWorld;
import org.skr.gdx.physmodel.ShapeDescription;

/**
 * Created by rat on 14.06.14.
 */
public class ChainShapeController extends ShapeController {


    public class ChainControlPoint extends ShapeControlPoint {

        Vector2 vertex = new Vector2();

        public ChainControlPoint(ShapeDescription shapeDescription, Vector2 vertex) {
            super(shapeDescription);
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

        for ( ShapeDescription shd : getFixtureSetDescription().getShapeDescriptions() ) {
            createShapeControlPoints( shd );
        }
    }

    protected void createShapeControlPoints( ShapeDescription shd ) {
        for ( Vector2 v : shd.getVertices() ) {
            Vector2 viewVertex = PhysWorld.get().viewToPhys( v );

            ChainControlPoint cp = new ChainControlPoint( shd, v );
            cp.setPos( viewVertex.x, viewVertex.y );

            getControlPoints().add( cp );
        }
    }



    private  final static  Vector2 v1 = new Vector2();
    private  final static  Vector2 v2 = new Vector2();

    @Override
    protected void drawShapeDescription(ShapeDescription shd) {

        int c = shd.getVertices().size - 1;
        for ( int i = 0; i < c; i++ ) {
            v1.set( PhysWorld.get().physToView( shd.getVertices().get( i ) )  );
            v2.set( PhysWorld.get().physToView( shd.getVertices().get( i + 1 ) ) );
            getShapeRenderer().line( v1, v2 );
        }

        if ( shd.isLooped() && shd.getVertices().size > 2) {

            v1.set( PhysWorld.get().physToView( shd.getVertices().get( 0 ) )  );
            v2.set( PhysWorld.get().physToView( shd.getVertices().get( shd.getVertices().size - 1 ) ) );
            getShapeRenderer().line( v1, v2 );
        }

    }

    @Override
    public ShapeDescription createNewShape(float x, float y) {
        ShapeDescription shd = new ShapeDescription();
        shd.getVertices().add( new Vector2( x, y ) );
        createShapeControlPoints( shd );
        return shd;
    }

    @Override
    protected void updateShapeFromControlPoint(ControlPoint cp) {
        ChainControlPoint ccp = (ChainControlPoint) cp;
        Vector2 vertex = ccp.getVertex();
        vertex.set( PhysWorld.get().toPhys( cp.getX() ), PhysWorld.get().toPhys(cp.getY()) );
    }

    @Override
    protected void updateControlPointFromObject(ControlPoint cp) {
        ChainControlPoint ccp = (ChainControlPoint) cp;
        Vector2 vertex = ccp.getVertex();
        cp.setPos( PhysWorld.get().toView( vertex.x), PhysWorld.get().toView( vertex.y )  );
//        Gdx.app.log("ChainShapeController.updateControlPointFromObject",
//                "ID:" + cp.getId() + " Pos: " + cp.getX() + " " + cp.getY() + " vertex: " + vertex );
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


    @Override
    public void setLooped(boolean state) {
        if ( getControlPoints().size == 0 )
            return;
        ShapeDescription shd = getShapeDescription( getControlPoints().get( 0 ) );
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
    protected void offsetAllPoints(Vector2 offsetLocal, Vector2 offsetStage) {
        for ( ControlPoint cp : getControlPoints() )
            moveControlPoint( cp, offsetLocal, offsetStage );
    }

    @Override
    protected void addControlPoint( Vector2 localCoord ) {

        if ( getControlPoints().size == 0 )
            return;

        ShapeDescription shd = getShapeDescription( getControlPoints().get( 0 ) );

        Vector2 physCoord = PhysWorld.get().viewToPhys( localCoord );

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

        ShapeDescription shd = getShapeDescription( cp );

        shd.getVertices().removeValue( cp.getVertex(), true );

        removeControlPoint( cp );

        return true;
    }

}
