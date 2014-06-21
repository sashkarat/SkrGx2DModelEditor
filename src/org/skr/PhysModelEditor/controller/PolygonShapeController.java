package org.skr.PhysModelEditor.controller;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import org.skr.PhysModelEditor.PhysWorld;
import org.skr.physmodel.ShapeDescription;

/**
 * Created by rat on 15.06.14.
 */


public class PolygonShapeController extends ShapeController {

    Polygon polygon;

    public class PolygonControlPoint extends ShapeControlPoint {

        public PolygonControlPoint(ShapeDescription shapeDescription) {
            super(shapeDescription);
        }
    }

    public PolygonShapeController(Stage stage) {
        super(stage);
    }

    @Override
    protected void createControlPoints() {
        for ( ShapeDescription shd : fixtureSetDescription.getShapeDescriptions() )
            createShapeControlPoints( shd );
    }


    protected void createShapeControlPoints( ShapeDescription shd ) {
        //TODO: implement this
    }

    @Override
    protected void drawShapeDescription(ShapeDescription shd) {

    }

    @Override
    public ShapeDescription createNewShape(float x, float y) {
        ShapeDescription shd = new ShapeDescription();

        PolygonControlPoint cp = new PolygonControlPoint( shd );

        cp.setPos( PhysWorld.get().toView( x ), PhysWorld.get().toView( y ) );

        controlPoints.add( cp );

        return shd;
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
    protected void onLeftCtrlClicked(Vector2 localCoord, Vector2 stageCoord) {

        if ( controlPoints.size == 0 )
            return;

        ShapeDescription shd = getShapeDescription( controlPoints.get( 0 ) );

        PolygonControlPoint cp = new PolygonControlPoint( shd );
        cp.setPos( localCoord.x, localCoord.y );

        controlPoints.add( cp );

    }

    @Override
    protected void onLeftCtrlShiftClicked(Vector2 localCoord, Vector2 stageCoord) {

        if ( controlPoints.size < 2 )
            return;

        PolygonControlPoint cp = null;

        for ( ControlPoint p : controlPoints) {
            if ( p.contains(localCoord) ) {
                cp = (PolygonControlPoint) p;
                break;
            }
        }

        if ( cp == null )
            return;

        int indexOf = controlPoints.indexOf( cp, true );

        if ( indexOf >= 0 )
            controlPoints.removeIndex( indexOf );

    }
}
