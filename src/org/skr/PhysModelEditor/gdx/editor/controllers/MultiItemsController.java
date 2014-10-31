package org.skr.PhysModelEditor.gdx.editor.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import org.skr.PhysModelEditor.gdx.editor.screens.EditorScreen;
import org.skr.gdx.PhysWorld;
import org.skr.gdx.editor.Controller;
import org.skr.gdx.physmodel.bodyitem.BodyItem;
import org.skr.gdx.physmodel.bodyitem.fixtureset.FixtureSet;
import org.skr.gdx.utils.RectangleExt;

import java.util.Objects;

/**
 * Created by rat on 27.08.14.
 */
public class MultiItemsController extends Controller {

    Array< Object > items = new Array<Object>();
    EditorScreen.ModelObjectType modelObjectType = EditorScreen.ModelObjectType.OT_None;


    public MultiItemsController(Stage stage) {
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


    protected void drawBodyItems() {
        getShapeRenderer().setColor(0, 1, 0, 1);
        for ( Object obj : items ) {
            BodyItem bi = (BodyItem) obj;
            RectangleExt bb = bi.getBodyItemBoundingBox();
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
            BodyItem bi = fs.getBodyItem();
            RectangleExt bb = bi.getBodyItemBoundingBox();
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
            case OT_BodyItem:
                getPosControlPoint().setVisible( true );
                break;
            case OT_Aag:
                break;
            case OT_FixtureSet:
                break;
            case OT_JointItem:
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
            case OT_BodyItem:
                drawBodyItems();
                break;
            case OT_Aag:
                drawAag();
                break;
            case OT_FixtureSet:
                drawFixtureSets();
                break;
            case OT_JointItem:
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

    protected void movePosCpBodyItems( Vector2 offsetStage ) {
        Vector2 offset = PhysWorld.get().viewToPhys( offsetStage );
        for ( Object obj : items ) {
            BodyItem bi = (BodyItem) obj;
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
            case OT_BodyItem:
                movePosCpBodyItems( offsetStage );
                break;
            case OT_Aag:
                break;
            case OT_FixtureSet:
                break;
            case OT_JointItem:
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

    protected void updatePosCpFromBodyItems( ControlPoint cp ) {
        float x = 0, y = 0;
        for ( Object obj : items ) {
            BodyItem bi = (BodyItem) obj;
            x += bi.getBody().getPosition().x;
            y += bi.getBody().getPosition().y;
        }
        x /= items.size;
        y /= items.size;

        cp.setX(PhysWorld.get().toView( x ) );
        cp.setY(PhysWorld.get().toView( y ) );
    }


    @Override
    protected void updatePosControlPointFromObject(ControlPoint cp) {
        switch ( modelObjectType ) {
            case OT_None:
                break;
            case OT_Model:
                break;
            case OT_BodyItem:
                updatePosCpFromBodyItems( cp );
                break;
            case OT_Aag:
                break;
            case OT_FixtureSet:
                break;
            case OT_JointItem:
                break;
        }
    }
}
