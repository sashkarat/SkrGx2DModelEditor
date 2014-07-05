package org.skr.PhysModelEditor.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import org.skr.PhysModelEditor.PhysWorld;
import org.skr.PhysModelEditor.PolygonRefinement;
import org.skr.physmodel.ShapeDescription;

/**
 * Created by rat on 15.06.14.
 */


public class PolygonShapeController extends ShapeController {

    private static final Color firstColor  = new Color( 1f, 1, 0, 1);

    ShapeDescription shd;
    boolean autoTessellate = false;
    Array< Array<Vector2> > polygons = null;

    public class PolygonControlPoint extends ShapeControlPoint {

        public PolygonControlPoint(ShapeDescription shapeDescription) {
            super(shapeDescription);
        }
    }

    public PolygonShapeController(Stage stage) {
        super(stage);
    }


    @Override
    public void setAutoTessellate(boolean state) {
        autoTessellate = state;

        if ( autoTessellate )
            tessellatePolygon();
    }

    @Override
    protected void createControlPoints() {

        Array< Array<Vector2> > polygons = new Array<Array<Vector2>>();

        for ( ShapeDescription shd : fixtureSetDescription.getShapeDescriptions() ) {
            polygons.add( shd.getVertices() );
        }

        Array< Vector2 > border = PolygonRefinement.mergePolygons( polygons );

        if ( border.size == 0 )
            return;

        fixtureSetDescription.getShapeDescriptions().clear();

        shd = new ShapeDescription();
        fixtureSetDescription.getShapeDescriptions().add(shd);
        controlPoints.clear();

        PolygonControlPoint cp = new PolygonControlPoint(shd);
        cp.setPos( border.first().x, border.first().y );
        cp.setColor( firstColor );

        controlPoints.add( cp );

        for ( int i = 1; i < border.size; i++ ) {
            addControlPoint( border.get( i ) );
        }
    }



    protected Vector2 getVertex( Array< Vector2 > polygon, int index ) {
        if ( polygon.size == 0 )
            return null;
        return polygon.get( index % polygon.size );

    }

    protected void drawPolygons() {
        if ( polygons == null )
            return;

        shapeRenderer.setColor( 0.5f, 0.5f, 0, 1);

        for ( Array<Vector2> polygon : polygons) {

            for ( int i = 0; i < polygon.size; i++) {
                shapeRenderer.line( getVertex(polygon, i), getVertex( polygon, i+1) );
            }

        }

    }

    @Override
    protected void drawShapeDescription(ShapeDescription shd) {

        drawPolygons();

        shapeRenderer.setColor( 0.8f, 0.2f, 0, 1);

        if ( controlPoints.size < 3)
            return;

        for ( int i = 0; i < controlPoints.size; i++ ) {
            ControlPoint cpA = getControlPoint( i );
            ControlPoint cpB = getControlPoint( i + 1 );
            shapeRenderer.line( cpA.getX(), cpA.getY(), cpB.getX(), cpB.getY());
        }

    }

    @Override
    public ShapeDescription createNewShape(float x, float y) {

        if ( shd != null)
            return null;

        shd = new ShapeDescription();

        PolygonControlPoint cp = new PolygonControlPoint( shd );
        cp.setColor( firstColor );

        cp.setPos( PhysWorld.get().toView( x ), PhysWorld.get().toView( y ) );

        controlPoints.add( cp );

        return shd;
    }

    @Override
    protected void shapeDeleted(ShapeDescription shd) {
        polygons = null;
        if ( this.shd == shd)
            this.shd = null;
        updateFixtureSetDescription();
    }

    @Override
    protected void updateShapeFromControlPoint(ControlPoint cp) {
        // does nothing
    }

    @Override
    protected void updateControlPointFromShape(ControlPoint cp) {
        // does nothing
    }

    @Override
    protected void removeControlPoint(ControlPoint cp) {
        Color c = cp.getColor();
        super.removeControlPoint(cp);
        if ( c == firstColor && controlPoints.size > 0 ) {
            controlPoints.get( 0 ).setColor( firstColor );
        }
    }

    @Override
    protected void moveControlPoint(ControlPoint cp, Vector2 offsetLocal, Vector2 offsetStage) {
        ShapeDescription shapeDescription = getShapeDescription( cp );
        cp.offsetPos( offsetLocal.x, offsetLocal.y );
        updateShapeFromControlPoint( cp );
        notifyListenerControlPointChanged( shapeDescription, cp );
        if ( autoTessellate )
            tessellatePolygon();
    }

    @Override
    protected void rotateAtControlPoint(ControlPoint cp, float angle) {

    }

    @Override
    protected void addControlPoint(Vector2 localCoord ) {

        if ( controlPoints.size == 0 )
            return;

        ShapeDescription shd = getShapeDescription( controlPoints.get( 0 ) );

        PolygonControlPoint cp = new PolygonControlPoint( shd );
        cp.setPos( localCoord.x, localCoord.y );

        if ( selectedControlPoint == null ) {
            controlPoints.add(cp);
        } else {
            int indexOf = controlPoints.indexOf( selectedControlPoint, true );
            if ( indexOf < 0 ) {
                controlPoints.add(cp);
            } else {
                controlPoints.insert( indexOf, cp );
            }
        }

        if ( autoTessellate )
            tessellatePolygon();
    }

    @Override
    protected void removeControlPoint(Vector2 localCoord ) {

        if ( controlPoints.size < 2 )
            return;

        PolygonControlPoint cp = null;

        for ( ControlPoint p : controlPoints) {
            if ( p.contains(localCoord) ) {
                cp = (PolygonControlPoint) p;
                break;
            }
        }

        if ( cp != null ) {

            if ( controlPoints.size == 1)
                return;

            removeControlPoint(cp);
            if (autoTessellate)
                tessellatePolygon();
        }
    }

    public void updateFixtureSetDescription() {
        if ( fixtureSetDescription == null )
            return;
        fixtureSetDescription.getShapeDescriptions().clear();

        if ( polygons == null )
            return;


        for ( Array<Vector2> polygon :  polygons ) {
            ShapeDescription shdesc = new ShapeDescription();
            shdesc.setVertices( polygon );
            fixtureSetDescription.getShapeDescriptions().add( shdesc );
        }

        if ( fixtureSetDescription.getShapeDescriptions().size > 0 ) {
            this.shd = fixtureSetDescription.getShapeDescriptions().get(0);
        } else {
            this.shd = null;
        }

        for ( ControlPoint cp : controlPoints ) {
            cp.setObject( this.shd );
        }

    }

    @Override
    public void tessellatePolygon() {

        if ( controlPoints.size < 3) {
            polygons = null;
        }

        Array< Vector2 > border = new Array<Vector2>();

        for ( int i = 0; i < controlPoints.size; i++) {
            ControlPoint cp = getControlPoint( i );
            border.add( new Vector2( cp.getX(), cp.getY() ) );
        }
        polygons = PolygonRefinement.cutPolygon( border );

        updateFixtureSetDescription();
    }
}
