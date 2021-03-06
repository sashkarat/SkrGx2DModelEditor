package org.skr.gx2d.ModelEditor.gdx.controllers.ShapeControllers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import org.skr.gx2d.ModelEditor.PolygonRefinement;
import org.skr.gx2d.ModelEditor.gdx.controllers.ShapeController;
import org.skr.gx2d.common.Env;
import org.skr.gx2d.physnodes.physdef.ShapeDefinition;

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
            physPolygon.add(Env.get().world.toPhys(phP));
        }

        return physPolygon;
    }

    private static Array<Vector2> newViewPolygon( Array<Vector2> physPolygon) {
        Array<Vector2> viewPolygon = new Array<Vector2>();

        for ( Vector2 point : physPolygon ) {
            Vector2 vP = new Vector2( point );
            viewPolygon.add(Env.get().world.toView(vP));
        }
        return viewPolygon;
    }

    ShapeDefinition shd;
    boolean autoTessellate = false;
    Array< Array<Vector2> > polygons = null;

    public class PolygonControlPoint extends ShapeControlPoint {

        public PolygonControlPoint(ShapeDefinition shapeDef) {
            super(shapeDef);
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

        getControlPoints().clear();

        if ( getFxDef().getShapeDefArray().size == 0 ) {
            this.shd = null;
            return;
        }


        Array< Array<Vector2> > polygons = new Array<Array<Vector2>>();

        for ( ShapeDefinition shd : getFxDef().getShapeDefArray() ) {
            polygons.add( newViewPolygon( shd.getVertices() ) );
        }

        Array< Vector2 > border = PolygonRefinement.mergePolygons( polygons );

        if ( border == null )
            return;

        if ( border.size == 0 )
            return;

        getFxDef().getShapeDefArray().clear();

        shd = new ShapeDefinition();
        getFxDef().getShapeDefArray().add(shd);


        PolygonControlPoint cp = new PolygonControlPoint(shd);
        cp.setPos( border.first().x, border.first().y );
        cp.setColor( firstColor );

        getControlPoints().add( cp );

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

        getShapeRenderer().setColor( 0.5f, 0.5f, 0, 1);

        for ( Array<Vector2> polygon : polygons) {

            for ( int i = 0; i < polygon.size; i++) {
                getShapeRenderer().line( getVertex(polygon, i), getVertex( polygon, i+1) );
            }

        }

    }

    @Override
    protected void drawShapeDef(ShapeDefinition shd) {

        drawPolygons();

        getShapeRenderer().setColor( 0.8f, 0.2f, 0, 1);

        if ( getControlPoints().size < 3)
            return;

        for ( int i = 0; i < getControlPoints().size; i++ ) {
            ControlPoint cpA = getControlPoint( i );
            ControlPoint cpB = getControlPoint( i + 1 );
            getShapeRenderer().line( cpA.getX(), cpA.getY(), cpB.getX(), cpB.getY());
        }

    }

    @Override
    public ShapeDefinition createNewShape(float x, float y) {

        if ( shd != null)
            return null;

        shd = new ShapeDefinition();
        float size = 1;

        PolygonControlPoint cp = new PolygonControlPoint( shd );
        cp.setColor( firstColor );
        cp.setPos( Env.get().world.toView( x - size/2 ), Env.get().world.toView( y - size/2 ) );
        getControlPoints().add(cp);

        cp = new PolygonControlPoint( shd );
        cp.setPos(Env.get().world.toView(x + size / 2), Env.get().world.toView(y - size / 2));
        getControlPoints().add( cp );

        cp = new PolygonControlPoint( shd );
        cp.setPos(Env.get().world.toView(x + size / 2), Env.get().world.toView(y + size / 2));
        getControlPoints().add( cp );

        cp = new PolygonControlPoint( shd );
        cp.setPos( Env.get().world.toView( x - size/2 ), Env.get().world.toView( y + size/2 ) );
        getControlPoints().add( cp );

        return shd;
    }

    @Override
    protected void shapeDeleted(ShapeDefinition shd) {
        polygons = null;
        if ( this.shd == shd)
            this.shd = null;
        updateFixtureSetDefinition();
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
        boolean backAutoTessellate = autoTessellate;

        autoTessellate = false;


        for ( int i = 0; i < getControlPoints().size; i++ ) {
            moveControlPoint(getControlPoints().get( i ), offsetLocal, offsetStage, posLocal, posStage );
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
        if ( c == firstColor && getControlPoints().size > 0 ) {
            getControlPoints().get( 0 ).setColor( firstColor );
        }
        return res;
    }

    @Override
    protected void moveControlPoint(ControlPoint cp, Vector2 offsetLocal, Vector2 offsetStage, final Vector2 posLocal, final Vector2 posStage ) {
        ShapeDefinition shapeDef = getShapeDef(cp);
        cp.offsetPos( offsetLocal.x, offsetLocal.y );
        updateShapeFromControlPoint( cp );
        notifyListenerControlPointChanged(shapeDef, cp);
        if ( autoTessellate )
            tessellatePolygon();
    }

    @Override
    protected void rotateAtControlPoint(ControlPoint cp, float angle) {

    }

    @Override
    protected void addControlPoint(Vector2 localCoord ) {

        if ( getControlPoints().size == 0 )
            return;

        ShapeDefinition shd = getShapeDef(getControlPoints().get(0));

        PolygonControlPoint cp = new PolygonControlPoint( shd );
        cp.setPos( localCoord.x, localCoord.y );
        checkMinimalDistance( cp );

        if ( getSelectedControlPoint() == null ) {
            getControlPoints().add(cp);
        } else {
            int indexOf = getControlPoints().indexOf( getSelectedControlPoint(), true );
            if ( indexOf < 0 ) {
                getControlPoints().add(cp);
            } else {
                getControlPoints().insert( indexOf, cp );
            }
        }

        if ( autoTessellate )
            tessellatePolygon();
    }

    @Override
    protected boolean removeControlPoint(Vector2 localCoord ) {

        if ( getControlPoints().size < 2 )
            return false;

        PolygonControlPoint cp = null;

        for ( ControlPoint p : getControlPoints()) {
            if ( p.contains(localCoord) ) {
                cp = (PolygonControlPoint) p;
                break;
            }
        }

        boolean res = false;

        if ( cp != null ) {

            if ( getControlPoints().size == 1)
                return false;

            res = removeControlPoint(cp);
            if (autoTessellate && res)
                tessellatePolygon();
        }
        return res;
    }

    public void updateFixtureSetDefinition() {
        if ( getFxDef() == null )
            return;
        getFxDef().getShapeDefArray().clear();

        if ( polygons == null )
            return;


        for ( Array<Vector2> polygon :  polygons ) {
            ShapeDefinition shDef = new ShapeDefinition();
            shDef.setVertices( newPhysPolygon( polygon ) );
            getFxDef().getShapeDefArray().add( shDef );
        }

        if ( getFxDef().getShapeDefArray().size > 0 ) {
            this.shd = getFxDef().getShapeDefArray().get(0);
        } else {
            this.shd = null;
        }

        for ( ControlPoint cp : getControlPoints() ) {
            cp.setObject( this.shd );
        }

    }

    @Override
    public void tessellatePolygon() {

        if ( getControlPoints().size < 3) {
            polygons = null;
        }

        Array< Vector2 > border = new Array<Vector2>();

        for ( int i = 0; i < getControlPoints().size; i++) {
            ControlPoint cp = getControlPoint( i );
            border.add( new Vector2( cp.getX(), cp.getY() ) );
        }
        polygons = PolygonRefinement.cutPolygon(border);

        updateFixtureSetDefinition();
    }

    @Override
    public void flush() {
        tessellatePolygon();
    }

    private static final Vector2 tva = new Vector2();
    private static final Vector2 tvb = new Vector2();

    void checkMinimalDistance( ControlPoint cp ) {

        tvb.set( cp.getX(), cp.getY() );
        float md2 = Env.get().world.toView( minimalDist );

        md2 *= md2;

        for ( ControlPoint cpA : getControlPoints() ) {
            if ( cpA == cp )
                continue;
            tva.set( cpA.getX(), cpA.getY() );

            if ( tva.dst2(tvb) >= md2 )
                continue;

            offsetPointToMinimalDistance(tva, tvb, Env.get().world.toView(minimalDist));
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
