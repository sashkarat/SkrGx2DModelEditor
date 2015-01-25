package org.skr.gx2d.ModelEditor.gdx.controllers;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import org.skr.gx2d.editor.Controller;
import org.skr.gx2d.sprite.Sprite;

/**
 * Created by rat on 03.06.14.
 */
public class SpriteController extends Controller implements Sprite.RenderableUserObject {

    public enum CpType {
        FREE,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        TOP_RIGHT,
        TOP_LEFT
    }

    public static class ActorControlPoint extends Controller.ControlPoint {
        private CpType type = CpType.FREE;

        public ActorControlPoint(CpType type) {
            super( null );
            this.type = type;
        }

        public ActorControlPoint(Object object, CpType type) {
            super(object);
            this.type = type;
        }

        public CpType getType() {
            return type;
        }

        public void setType(CpType type) {
            this.type = type;
        }
    }


    Sprite sprite;

    public SpriteController(Stage stage) {
        super(stage);

        getControlPoints().add( new ActorControlPoint(CpType.BOTTOM_LEFT) );
        getControlPoints().add( new ActorControlPoint(CpType.BOTTOM_RIGHT) );
        getControlPoints().add( new ActorControlPoint(CpType.TOP_RIGHT) );
        getControlPoints().add( new ActorControlPoint(CpType.TOP_LEFT) );
        setEnableBbControl( false );

    }

    public void setSprite(Sprite sprite) {
        resetSprite();
        this.sprite = sprite;
        if ( sprite != null )
            sprite.setRenderableUserObject( this );
    }

    public Sprite getSprite() {
        return this.sprite;
    }

    public void resetSprite() {
        if ( this.sprite != null )
            this.sprite.setRenderableUserObject( null );
        this.sprite = null;
    }

    @Override
    protected void translateRendererToObject() {
        /*

        We don't need this function here.
        See comments in draw().

         */
    }

    @Override
    protected void drawLocal() {
        /*

        This class does not render it's content by Controller class way.
        It uses Sprite.RenderableUserObject interface.

        */
    }

    @Override
    protected Vector2 stageToObject(Vector2 stageCoord) {
        if ( this.sprite == null )
            return stageCoord;
        return Sprite.stageToActorLocal(this.sprite, stageCoord);
    }

    @Override
    protected void updateControlPointFromObject(ControlPoint cp) {

        ActorControlPoint acp = (ActorControlPoint) cp;

        if ( this.sprite == null ) {
            return;
        }

        switch ( acp.getType() ) {
            case FREE:
                break;
            case BOTTOM_LEFT:
                cp.setPos( - sprite.getWidth() / 2, - sprite.getHeight() / 2 );
                break;
            case BOTTOM_RIGHT:
                cp.setPos( sprite.getWidth() / 2, - sprite.getHeight() / 2 );
                break;
            case TOP_RIGHT:
                cp.setPos( sprite.getWidth() / 2, sprite.getHeight() / 2 );
                break;
            case TOP_LEFT:
                cp.setPos( - sprite.getWidth() / 2, sprite.getHeight() / 2 );
                break;
        }

    }

    @Override
    protected void moveControlPoint( ControlPoint cp, Vector2 offsetLocal, Vector2 offsetStage, final Vector2 posLocal, final Vector2 posStage  ) {

        if ( sprite == null )
            return;

        ActorControlPoint acp = (ActorControlPoint) cp;

        Sprite.rotateStageToLocal(sprite,  offsetStage );

        switch ( acp.getType() ) {
            case FREE:
                cp.offsetPos( offsetStage.x, offsetStage.y );
                break;
            case BOTTOM_LEFT:
                sprite.setHeight(sprite.getHeight() - offsetStage.y);
                sprite.setWidth(sprite.getWidth() - offsetStage.x);
                break;
            case BOTTOM_RIGHT:
                sprite.setHeight( sprite.getHeight() - offsetStage.y );
                sprite.setWidth( sprite.getWidth() + offsetStage.x  );
                break;
            case TOP_RIGHT:
                sprite.setHeight( sprite.getHeight() + offsetStage.y );
                sprite.setWidth( sprite.getWidth() + offsetStage.x  );
                break;
            case TOP_LEFT:
                sprite.setHeight( sprite.getHeight() + offsetStage.y );
                sprite.setWidth( sprite.getWidth() - offsetStage.x  );
                break;
        }

    }

    @Override
    protected void movePosControlPoint(ControlPoint cp, Vector2 offsetLocal, Vector2 offsetStage, final Vector2 posLocal, final Vector2 posStage ) {
        if ( sprite == null )
            return;
        Sprite.rotateStageToLocal(sprite,  offsetStage );
        sprite.setX( sprite.getX() + offsetStage.x );
        sprite.setY( sprite.getY() + offsetStage.y );
    }

    @Override
    protected void rotateAtControlPoint(ControlPoint cp, float angle) {
        if ( sprite == null )
            return;
        ActorControlPoint acp = (ActorControlPoint) cp;

        switch ( acp.getType() ) {
            case FREE:
                break;
            case BOTTOM_LEFT:
            case BOTTOM_RIGHT:
            case TOP_RIGHT:
            case TOP_LEFT:
                sprite.setRotation( sprite.getRotation() + angle );
                break;
        }

    }

    @Override
    protected Object getControlledObject() {
        return sprite;
    }

    @Override
    protected void updatePosControlPointFromObject(ControlPoint cp) {
        cp.setPos( 0, 0 );
    }

    @Override
    public void render(Sprite sprite, Batch batch) {
        if ( sprite != this.sprite)
            return;

        if ( this.sprite == null )
            return;

        batch.end();

        getShapeRenderer().setProjectionMatrix( batch.getProjectionMatrix() );
        getShapeRenderer().setTransformMatrix(batch.getTransformMatrix());
        getShapeRenderer().translate(this.sprite.getX(), this.sprite.getY(), 0);
        getShapeRenderer().rotate( 0, 0, 1, this.sprite.getRotation() );
        getShapeRenderer().setColor( 1, 0.8f, 0.5f, 1);

        getShapeRenderer().begin(ShapeRenderer.ShapeType.Line);
        getShapeRenderer().rect( - this.sprite.getWidth() / 2 ,  - this.sprite.getHeight() / 2,
                            this.sprite.getWidth(), this.sprite.getHeight());
        getShapeRenderer().end();

        drawControlPoints();


        batch.begin();
    }
}
