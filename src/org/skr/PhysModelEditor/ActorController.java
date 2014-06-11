package org.skr.PhysModelEditor;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import org.skr.physmodel.animatedactorgroup.AnimatedActorGroup;

/**
 * Created by rat on 03.06.14.
 */
public class ActorController extends Controller implements AnimatedActorGroup.RenderableUserObject {


    Actor actor;

    protected ActorController(Stage stage) {
        super(stage);
        controlPoints.add( new ControlPoint(CpType.BOTTOM_LEFT) );
        controlPoints.add( new ControlPoint(CpType.BOTTOM_RIGHT) );
        controlPoints.add( new ControlPoint(CpType.TOP_RIGHT) );
        controlPoints.add( new ControlPoint(CpType.TOP_LEFT) );
        controlPoints.add( new ControlPoint(CpType.CENTER) );

    }

    public void setActor( Actor actor ) {
        this.actor = actor;
    }

    public Actor getActor() {
        return this.actor;
    }

    @Override
    protected void translateRendererToObject() {
        /*

        We don't need this function here.
        See comments in draw().

         */
    }

    @Override
    protected void draw() {
        /*

        This class does not render it's content by base class way.
        It uses AnimatedActorGroup.RenderableUserObject interface.

        */
    }

    @Override
    protected Vector2 stageToObject(Vector2 stageCoord) {
        if ( this.actor == null )
            return stageCoord;
        return AnimatedActorGroup.stageToActorLocal(this.actor, stageCoord);
    }

    @Override
    protected void updateControlPoint(ControlPoint cp) {

        if ( this.actor == null ) {
            return;
        }

        switch ( cp.getType() ) {
            case FREE:
                break;
            case CENTER:
                cp.setPos( 0, 0 );
                break;
            case BOTTOM_LEFT:
                cp.setPos( - actor.getWidth() / 2, - actor.getHeight() / 2 );
                break;
            case BOTTOM_RIGHT:
                cp.setPos( actor.getWidth() / 2, - actor.getHeight() / 2 );
                break;
            case TOP_RIGHT:
                cp.setPos( actor.getWidth() / 2, actor.getHeight() / 2 );
                break;
            case TOP_LEFT:
                cp.setPos( - actor.getWidth() / 2, actor.getHeight() / 2 );
                break;
        }

    }

    @Override
    protected void moveControlPoint( ControlPoint cp, Vector2 offsetLocal, Vector2 offsetStage ) {

        if ( actor == null )
            return;

        AnimatedActorGroup.rotateStageToLocal( actor,  offsetStage );

        switch ( cp.getType() ) {
            case FREE:
                cp.offsetPos( offsetStage.x, offsetStage.y );
                break;
            case CENTER:
                actor.setX( actor.getX() + offsetStage.x );
                actor.setY( actor.getY() + offsetStage.y );
                break;
            case BOTTOM_LEFT:
                actor.setHeight(actor.getHeight() - offsetStage.y);
                actor.setWidth(actor.getWidth() - offsetStage.x);
                break;
            case BOTTOM_RIGHT:
                actor.setHeight( actor.getHeight() - offsetStage.y );
                actor.setWidth( actor.getWidth() + offsetStage.x  );
                break;
            case TOP_RIGHT:
                actor.setHeight( actor.getHeight() + offsetStage.y );
                actor.setWidth( actor.getWidth() + offsetStage.x  );
                break;
            case TOP_LEFT:
                actor.setHeight( actor.getHeight() + offsetStage.y );
                actor.setWidth( actor.getWidth() - offsetStage.x  );
                break;
        }

    }

    @Override
    protected void rotateAtControlPoint(ControlPoint cp, float angle) {
        if ( actor == null )
            return;
        switch ( cp.getType() ) {
            case FREE:
                break;
            case CENTER:
                break;
            case BOTTOM_LEFT:
            case BOTTOM_RIGHT:
            case TOP_RIGHT:
            case TOP_LEFT:
                actor.setRotation( actor.getRotation() + angle );
                break;
        }

    }

    @Override
    protected Object getControlledObject() {
        return actor;
    }

    @Override
    public void render(AnimatedActorGroup aag, Batch batch) {
        if ( aag != actor )
            return;

        if ( actor == null )
            return;

        batch.end();

        shapeRenderer.setProjectionMatrix( batch.getProjectionMatrix() );
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
        shapeRenderer.translate(actor.getX(), actor.getY(), 0);
        shapeRenderer.rotate( 0, 0, 1, actor.getRotation() );
        shapeRenderer.setColor( 1, 0.8f, 0.5f, 1);


        float zoom = ( (OrthographicCamera) stage.getCamera() ).zoom;
        setCameraZoom(zoom);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line );
        shapeRenderer.rect( - actor.getWidth() / 2 ,  - actor.getHeight() / 2,
                            actor.getWidth(), actor.getHeight());
        shapeRenderer.end();

        drawControlPoints();


        batch.begin();
    }
}
