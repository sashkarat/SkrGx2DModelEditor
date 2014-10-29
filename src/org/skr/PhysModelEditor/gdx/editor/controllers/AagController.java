package org.skr.PhysModelEditor.gdx.editor.controllers;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import org.skr.gdx.editor.Controller;
import org.skr.gdx.physmodel.animatedactorgroup.AnimatedActorGroup;

/**
 * Created by rat on 03.06.14.
 */
public class AagController extends Controller implements AnimatedActorGroup.RenderableUserObject {


    public enum CpType {
        FREE,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        TOP_RIGHT,
        TOP_LEFT
    }

    public static class ActorControlPoint extends ControlPoint {
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


    AnimatedActorGroup aag;

    public AagController(Stage stage) {
        super(stage);

        getControlPoints().add( new ActorControlPoint(CpType.BOTTOM_LEFT) );
        getControlPoints().add( new ActorControlPoint(CpType.BOTTOM_RIGHT) );
        getControlPoints().add( new ActorControlPoint(CpType.TOP_RIGHT) );
        getControlPoints().add( new ActorControlPoint(CpType.TOP_LEFT) );
        setEnableBbControl( false );

    }

    public void setAag(AnimatedActorGroup aag) {
        resetAag();
        this.aag = aag;
        if ( aag != null )
            aag.setRenderableUserObject( this );
    }

    public AnimatedActorGroup getAag() {
        return this.aag;
    }

    public void resetAag() {
        if ( this.aag != null )
            this.aag.setRenderableUserObject( null );
        this.aag = null;
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
        It uses AnimatedActorGroup.RenderableUserObject interface.

        */
    }

    @Override
    protected Vector2 stageToObject(Vector2 stageCoord) {
        if ( this.aag == null )
            return stageCoord;
        return AnimatedActorGroup.stageToActorLocal(this.aag, stageCoord);
    }

    @Override
    protected void updateControlPointFromObject(ControlPoint cp) {

        ActorControlPoint acp = (ActorControlPoint) cp;

        if ( this.aag == null ) {
            return;
        }

        switch ( acp.getType() ) {
            case FREE:
                break;
            case BOTTOM_LEFT:
                cp.setPos( - aag.getWidth() / 2, - aag.getHeight() / 2 );
                break;
            case BOTTOM_RIGHT:
                cp.setPos( aag.getWidth() / 2, - aag.getHeight() / 2 );
                break;
            case TOP_RIGHT:
                cp.setPos( aag.getWidth() / 2, aag.getHeight() / 2 );
                break;
            case TOP_LEFT:
                cp.setPos( - aag.getWidth() / 2, aag.getHeight() / 2 );
                break;
        }

    }

    @Override
    protected void moveControlPoint( ControlPoint cp, Vector2 offsetLocal, Vector2 offsetStage, final Vector2 posLocal, final Vector2 posStage  ) {

        if ( aag == null )
            return;

        ActorControlPoint acp = (ActorControlPoint) cp;

        AnimatedActorGroup.rotateStageToLocal(aag,  offsetStage );

        switch ( acp.getType() ) {
            case FREE:
                cp.offsetPos( offsetStage.x, offsetStage.y );
                break;
            case BOTTOM_LEFT:
                aag.setHeight(aag.getHeight() - offsetStage.y);
                aag.setWidth(aag.getWidth() - offsetStage.x);
                break;
            case BOTTOM_RIGHT:
                aag.setHeight( aag.getHeight() - offsetStage.y );
                aag.setWidth( aag.getWidth() + offsetStage.x  );
                break;
            case TOP_RIGHT:
                aag.setHeight( aag.getHeight() + offsetStage.y );
                aag.setWidth( aag.getWidth() + offsetStage.x  );
                break;
            case TOP_LEFT:
                aag.setHeight( aag.getHeight() + offsetStage.y );
                aag.setWidth( aag.getWidth() - offsetStage.x  );
                break;
        }

    }

    @Override
    protected void movePosControlPoint(ControlPoint cp, Vector2 offsetLocal, Vector2 offsetStage, final Vector2 posLocal, final Vector2 posStage ) {
        if ( aag == null )
            return;
        AnimatedActorGroup.rotateStageToLocal(aag,  offsetStage );
        aag.setX( aag.getX() + offsetStage.x );
        aag.setY( aag.getY() + offsetStage.y );
    }

    @Override
    protected void rotateAtControlPoint(ControlPoint cp, float angle) {
        if ( aag == null )
            return;
        ActorControlPoint acp = (ActorControlPoint) cp;

        switch ( acp.getType() ) {
            case FREE:
                break;
            case BOTTOM_LEFT:
            case BOTTOM_RIGHT:
            case TOP_RIGHT:
            case TOP_LEFT:
                aag.setRotation( aag.getRotation() + angle );
                break;
        }

    }

    @Override
    protected Object getControlledObject() {
        return aag;
    }

    @Override
    protected void updatePosControlPointFromObject(ControlPoint cp) {
        cp.setPos( 0, 0 );
    }

    @Override
    public void render(AnimatedActorGroup aag, Batch batch) {
        if ( aag != this.aag)
            return;

        if ( this.aag == null )
            return;

        batch.end();

        getShapeRenderer().setProjectionMatrix( batch.getProjectionMatrix() );
        getShapeRenderer().setTransformMatrix(batch.getTransformMatrix());
        getShapeRenderer().translate(this.aag.getX(), this.aag.getY(), 0);
        getShapeRenderer().rotate( 0, 0, 1, this.aag.getRotation() );
        getShapeRenderer().setColor( 1, 0.8f, 0.5f, 1);

        getShapeRenderer().begin(ShapeRenderer.ShapeType.Line );
        getShapeRenderer().rect( - this.aag.getWidth() / 2 ,  - this.aag.getHeight() / 2,
                            this.aag.getWidth(), this.aag.getHeight());
        getShapeRenderer().end();

        drawControlPoints();


        batch.begin();
    }
}
