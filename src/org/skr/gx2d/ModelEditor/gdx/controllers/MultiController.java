package org.skr.gx2d.ModelEditor.gdx.controllers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import org.skr.gx2d.ModelEditor.gdx.screens.EditorScreen;
import org.skr.gx2d.common.Env;
import org.skr.gx2d.editor.Controller;
import org.skr.gx2d.physnodes.BodyHandler;
import org.skr.gx2d.physnodes.FixtureSet;
import org.skr.gx2d.utils.RectangleExt;

/**
 * Created by rat on 27.08.14.
 */
public class MultiController extends Controller {

    Array< Object > items = new Array<Object>();
    EditorScreen.ModelObjectType modelObjectType = EditorScreen.ModelObjectType.OT_None;

    public MultiController(Stage stage) {
        super(stage);
        setEnableBbControl( false );
        ControlPoint cp = getPosControlPoint();
        cp.setColor( new Color(0, 1, 0.5f ,1) );
    }


    public void clear() {
        items.clear();
        modelObjectType = EditorScreen.ModelObjectType.OT_None;
    }

    public void addItems( Array<Object> items ) {
        for ( Object item : items ) {
            if ( items.contains( item, true ) )
                continue;
            items.add( item );
        }
    }

    public void addItem( Object item ) {
        if ( items.contains( item, true ) )
            return;
        items.add( item );
    }

    public void removeItem( Object item ) {
        items.removeValue( item, true );
    }


    protected void drawBodyHandlers() {
        getShapeRenderer().setColor(0, 1, 0, 1);
        for ( Object obj : items ) {
            BodyHandler bi = (BodyHandler) obj;
            RectangleExt bb = bi.getBodyBoundingBox();
            getShapeRenderer().rect( bb.getLeft(), bb.getBottom(),
                    bb.getWidth(), bb.getHeight() );
        }
    }

    protected void drawAag() {
        //TODO: implement this
    }


    protected void drawFixtureSets() {
        getShapeRenderer().setColor(1, 0, 0, 1);
        for ( Object obj: items ) {
            FixtureSet fs = (FixtureSet) obj;
            BodyHandler bi = fs.getBodyHandler();
            RectangleExt bb = bi.getBodyBoundingBox();
            getShapeRenderer().rect( bb.getLeft(), bb.getBottom(),
                    bb.getWidth(), bb.getHeight() );
        }
    }


    public EditorScreen.ModelObjectType getModelObjectType() {
        return modelObjectType;
    }

    public void setModelObjectType(EditorScreen.ModelObjectType modelObjectType) {
        this.modelObjectType = modelObjectType;

        getPosControlPoint().setVisible( false );

        switch ( modelObjectType ) {

            case OT_None:
                break;
            case OT_Model:
                break;
            case OT_BodyHandler:
                getPosControlPoint().setVisible( true );
                break;
            case OT_Sprite:
                break;
            case OT_FixtureSet:
                break;
            case OT_JointHandler:
                break;
        }
    }

    @Override
    protected void translateRendererToObject() {
    }

    @Override
    protected void drawLocal() {

        getShapeRenderer().begin(ShapeRenderer.ShapeType.Line );
        switch ( modelObjectType ) {

            case OT_None:
                break;
            case OT_Model:
                break;
            case OT_BodyHandler:
                drawBodyHandlers();
                break;
            case OT_Sprite:
                drawAag();
                break;
            case OT_FixtureSet:
                drawFixtureSets();
                break;
            case OT_JointHandler:
                break;
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

    protected void movePosCpBodyHandlers( Vector2 offsetStage ) {
        Vector2 offset = Env.get().world.viewToPhys( offsetStage );
        for ( Object obj : items ) {
            BodyHandler bi = (BodyHandler) obj;
            Body b = bi.getBody();
            Vector2 pos = b.getPosition();
            pos.add(offset);
            b.setTransform(pos, b.getAngle());
        }
    }

    @Override
    protected void movePosControlPoint(ControlPoint cp, Vector2 offsetLocal, Vector2 offsetStage, final Vector2 posLocal, final Vector2 posStage ) {

        switch ( modelObjectType ) {

            case OT_None:
                break;
            case OT_Model:
                break;
            case OT_BodyHandler:
                movePosCpBodyHandlers(offsetStage);
                break;
            case OT_Sprite:
                break;
            case OT_FixtureSet:
                break;
            case OT_JointHandler:
                break;
        }


    }

    @Override
    protected void rotateAtControlPoint(ControlPoint cp, float angle) {

    }

    @Override
    protected Object getControlledObject() {
        return items;
    }

    protected void updatePosCpFromBodyHandlers( ControlPoint cp ) {
        float x = 0, y = 0;
        for ( Object obj : items ) {
            BodyHandler bi = (BodyHandler) obj;
            x += bi.getBody().getPosition().x;
            y += bi.getBody().getPosition().y;
        }
        x /= items.size;
        y /= items.size;

        cp.setX(Env.get().world.toView(x));
        cp.setY(Env.get().world.toView( y ) );
    }


    @Override
    protected void updatePosControlPointFromObject(ControlPoint cp) {
        switch ( modelObjectType ) {
            case OT_None:
                break;
            case OT_Model:
                break;
            case OT_BodyHandler:
                updatePosCpFromBodyHandlers(cp);
                break;
            case OT_Sprite:
                break;
            case OT_FixtureSet:
                break;
            case OT_JointHandler:
                break;
        }
    }
}
