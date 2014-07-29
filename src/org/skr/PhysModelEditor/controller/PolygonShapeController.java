package org.skr.PhysModelEditor.controller;

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
    private static float minimalDist = 0.0505f;


    private static Array<Vector2> newPhysPolygon( Array<Vector2> viewPolygon ) {
        Array<Vector2> physPolygon = new Array<Vector2>();

        for ( Vector2 point : viewPolygon ) {
            Vector2 phP = new Vector2( point );
            physPolygon.add(PhysWorld.get().toPhys(phP));
        }

        return physPolygon;
    }

    private static Array<Vector2> newViewPolygon( Array<Vector2> physPolygon) {
        Array<Vector2> viewPolygon = new Array<Vector2>();

        for ( Vector2 point : physPolygon ) {
            Vector2 vP = new Vector2( point );
            viewPolygon.add( PhysWorld.get().toView( vP ) );
        }
        return viewPolygon;
    }

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

        controlPoints.clear();

        if ( fixtureSetDescription.getShapeDescriptions().size == 0 ) {
            this.shd = null;
            return;
        }


        Array< Array<Vector2> > polygons = new Array<Array<Vector2>>();

        for ( ShapeDescription shd : fixtureSetDescription.getShapeDescriptions() ) {
            polygons.add( newViewPolygon( shd.getVertices() ) );
        }

        Array< Vector2 > border = PolygonRefinement.mergePolygons( polygons );

        if ( border == null )
            return;

        if ( border.size == 0 )
            return;

        fixtureSetDescription.getShapeDescriptions().clear();

        shd = new ShapeDescription();
        fixtureSetDescription.getShapeDescriptions().add(shd);


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
        float size = 1;

        PolygonControlPoint cp = new PolygonControlPoint( shd );
        cp.setColor( firstColor );
        cp.setPos( PhysWorld.get().toView( x - size/2 ), PhysWorld.get().toView( y - size/2 ) );
        controlPoints.add( cp );

        cp = new PolygonControlPoint( shd );
        cp.setPos( PhysWorld.get().toView( x + size/2 ), PhysWorld.get().toView( y - size/2 ) );
        controlPoints.add( cp );

        cp = new PolygonControlPoint( shd );
        cp.setPos( PhysWorld.get().toView( x + size/2 ), PhysWorld.get().toView( y + size/2 ) );
        controlPoints.add( cp );

        cp = new PolygonControlPoint( shd );
        cp.setPos( PhysWorld.get().toView( x - size/2 ), PhysWorld.get().toView( y + size/2 ) );
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

    private static final Vector2 tmpV = new Vector2();

    @Override
    protected void getShapeViewCenter(ControlPoint cp) {
        tmpV.set(0, 0);
        for ( ControlPoint ccp: controlPoints )
            tmpV.add( ccp.getX(), ccp.getY());
        tmpV.scl( 1.0f / controlPoints.size );
        cp.setPos( tmpV.x, tmpV.y );
    }

    @Override
    protected void offsetAllPoints(Vector2 offsetLocal, Vector2 offsetStage) {
        boolean backAutoTessellate = autoTessellate;

        autoTessellate = false;


        for ( int i = 0; i < controlPoints.size; i++ ) {
            moveControlPoint(controlPoints.get( i ), offsetLocal, offsetStage);
        }

        autoTessellate = backAutoTessellate;

        if ( autoTessellate )
            tessellatePolygon();
    }

    @Override
    protected void updateShapeFromControlPoint(ControlPoint cp) {
        checkMinimalDistance( cp );
        // does nothing
    }

    @Override
    protected void updateControlPointFromObject(ControlPoint cp) {
        // does nothing
    }

    @Override
    protected boolean removeControlPoint(ControlPoint cp) {
        Color c = cp.getColor();
        boolean res = super.removeControlPoint(cp);
        if ( c == firstColor && controlPoints.size > 0 ) {
            controlPoints.get( 0 ).setColor( firstColor );
        }
        return res;
    }

    @Override
    protected void moveControlPoint(ControlPoint cp, Vector2 offsetLocal, Vector2 offsetStage) {
        ShapeDescription shapeDescription = getShapeDescription(cp);
        cp.offsetPos( offsetLocal.x, offsetLocal.y );
        updateShapeFromControlPoint( cp );
        notifyListenerControlPointChanged(shapeDescription, cp);
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
        checkMinimalDistance( cp );

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
    protected boolean removeControlPoint(Vector2 localCoord ) {

        if ( controlPoints.size < 2 )
            return false;

        PolygonControlPoint cp = null;

        for ( ControlPoint p : controlPoints) {
            if ( p.contains(localCoord) ) {
                cp = (PolygonControlPoint) p;
                break;
            }
        }

        boolean res = false;

        if ( cp != null ) {

            if ( controlPoints.size == 1)
                return false;

            res = removeControlPoint(cp);
            if (autoTessellate && res)
                tessellatePolygon();
        }
        return res;
    }

    public void updateFixtureSetDescription() {
        if ( fixtureSetDescription == null )
            return;
        fixtureSetDescription.getShapeDescriptions().clear();

        if ( polygons == null )
            return;


        for ( Array<Vector2> polygon :  polygons ) {
            ShapeDescription shdesc = new ShapeDescription();
            shdesc.setVertices( newPhysPolygon( polygon ) );
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

    @Override
    public void flush() {
        tessellatePolygon();
    }

    private static final Vector2 tva = new Vector2();
    private static final Vector2 tvb = new Vector2();

    void checkMinimalDistance( ControlPoint cp ) {

        tvb.set( cp.getX(), cp.getY() );
        float md2 = PhysWorld.get().toView( minimalDist );

        md2 *= md2;

        for ( ControlPoint cpA : controlPoints ) {
            if ( cpA == cp )
                continue;
            tva.set( cpA.getX(), cpA.getY() );

            if ( tva.dst2(tvb) >= md2 )
                continue;

            offsetPointToMinimalDistance( tva, tvb, PhysWorld.get().toView( minimalDist ) );
            cp.setPos( tvb.x, tvb.y );
        }
    }

    private static final Vector2 tv = new Vector2();

    private static void offsetPointToMinimalDistance( Vector2 vA, Vector2 vB, float minimalDst ) {
        tv.set( vB ).sub( vA );
        tv.nor().scl( minimalDst );
        vB.set( vA).add( tv );
    }
}
