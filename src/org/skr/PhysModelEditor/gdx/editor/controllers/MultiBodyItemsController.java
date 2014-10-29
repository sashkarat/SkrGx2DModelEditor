package org.skr.PhysModelEditor.gdx.editor.controllers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import org.skr.gdx.PhysWorld;
import org.skr.gdx.editor.Controller;
import org.skr.gdx.physmodel.bodyitem.BodyItem;
import org.skr.gdx.utils.RectangleExt;

/**
 * Created by rat on 27.08.14.
 */
public class MultiBodyItemsController extends Controller {

    Array<BodyItem> bodyItems = new Array<BodyItem>();

    public MultiBodyItemsController(Stage stage) {
        super(stage);
        setEnableBbControl( false );
        ControlPoint cp = getPosControlPoint();
        cp.setColor( new Color(0, 1, 0.5f ,1) );
    }

    public Array<BodyItem> getBodyItems() {
        return bodyItems;
    }

    @Override
    protected void translateRendererToObject() {
    }

    @Override
    protected void drawLocal() {
        getShapeRenderer().setColor(0, 1, 0, 1);
        getShapeRenderer().begin(ShapeRenderer.ShapeType.Line );
        for ( BodyItem bi : bodyItems ) {
            RectangleExt bb = bi.getBodyItemBoundingBox();
            getShapeRenderer().rect( bb.getLeft(), bb.getBottom(),
                    bb.getWidth(), bb.getHeight() );
        }
        getShapeRenderer().end();

        drawControlPoints();



    }

    @Override
    protected Vector2 stageToObject(Vector2 stageCoord) {
        return stageCoord;
    }

    @Override
    protected void updateControlPointFromObject(ControlPoint cp) {

    }

    @Override
    protected void moveControlPoint(ControlPoint cp, Vector2 offsetLocal, Vector2 offsetStage, final Vector2 posLocal, final Vector2 posStage ) {

    }

    @Override
    protected void movePosControlPoint(ControlPoint cp, Vector2 offsetLocal, Vector2 offsetStage, final Vector2 posLocal, final Vector2 posStage ) {



        Vector2 offset = PhysWorld.get().viewToPhys( offsetStage );

        for ( BodyItem bi : bodyItems ) {
            Body b = bi.getBody();
            Vector2 pos = b.getPosition();
            pos.add( offset );
            b.setTransform( pos, b.getAngle() );
        }
    }

    @Override
    protected void rotateAtControlPoint(ControlPoint cp, float angle) {

    }

    @Override
    protected Object getControlledObject() {
        return bodyItems;
    }

    @Override
    protected void updatePosControlPointFromObject(ControlPoint cp) {
        float x = 0, y = 0;
        for ( BodyItem bi : bodyItems ) {
            x += bi.getBody().getPosition().x;
            y += bi.getBody().getPosition().y;
        }
        x /= bodyItems.size;
        y /= bodyItems.size;

        cp.setX(PhysWorld.get().toView( x ) );
        cp.setY(PhysWorld.get().toView( y ) );
    }
}
