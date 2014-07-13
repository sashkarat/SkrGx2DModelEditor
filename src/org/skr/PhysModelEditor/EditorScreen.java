package org.skr.PhysModelEditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.skr.PhysModelEditor.controller.*;
import org.skr.physmodel.BodyItem;
import org.skr.physmodel.FixtureSet;
import org.skr.physmodel.PhysModel;

/**
 * Created by rat on 02.06.14.
 */
public class EditorScreen extends BaseScreen {

    private  PhysModelRenderer modelRenderer;

    private ActorController actorController;
    private BodyItemController bodyItemController;
    private CircleShapeController circleShapeController;
    private EdgeShapeController edgeShapeController;
    private ChainShapeController chainShapeController;
    private PolygonShapeController polygonShapeController;
    private AnchorPointController anchorPointController;
    private Controller currentController = null;


    public EditorScreen() {

        super();

        modelRenderer = new PhysModelRenderer( PhysWorld.getPrimaryWorld() );

        getStage().addActor(modelRenderer);

        actorController = new ActorController( getStage() );
        bodyItemController = new BodyItemController( getStage() );
        circleShapeController = new CircleShapeController( getStage() );
        edgeShapeController = new EdgeShapeController( getStage() );
        chainShapeController = new ChainShapeController( getStage() );
        polygonShapeController = new PolygonShapeController( getStage() );
        anchorPointController = new AnchorPointController( getStage() );

    }


    public PhysModelRenderer getModelRenderer() {
        return modelRenderer;
    }


    public void setModelObject(Object object) {

        currentController = null;

        if ( actorController.getActor() != null ) {
            actorController.getActor().setUserObject( null );
        }

        if ( object instanceof BodyItem ) {
            BodyItem bi = ( BodyItem ) object;
            bodyItemController.setBodyItem( bi );
            currentController = bodyItemController;
            return;
        }

        if ( object instanceof Actor ) {

            Actor a = (Actor) object;
            a.setUserObject( actorController );
            actorController.setActor( a );
            currentController = actorController;
            return;
        }

        if ( object instanceof FixtureSet ) {
            FixtureSet fs = ( FixtureSet ) object;

            switch ( fs.getShapeType() ) {

                case Circle:
                    circleShapeController.loadFromFixtureSet( fs );
                    currentController = circleShapeController;
                    break;
                case Edge:
                    edgeShapeController.loadFromFixtureSet( fs );
                    currentController = edgeShapeController;
                    break;
                case Polygon:
                    polygonShapeController.loadFromFixtureSet( fs );
                    currentController = polygonShapeController;
                    break;
                case Chain:
                    chainShapeController.loadFromFixtureSet( fs );
                    currentController = chainShapeController;
                    break;
            }

            return;
        }

        if ( object instanceof PhysModel ) {
            currentController = anchorPointController;
        }

    }

    public ActorController getActorController() {
        return actorController;
    }

    public BodyItemController getBodyItemController() {
        return bodyItemController;
    }

    public AnchorPointController getAnchorPointController() {
        return anchorPointController;
    }

    public ShapeController getCurrentShapeController() {
        if ( currentController == null )
            return null;
        if ( ! (circleShapeController instanceof ShapeController) )
            return null;
        return (ShapeController) currentController;
    }

    @Override
    protected void debugRender() {
        PhysWorld.get().debugRender( getStage() );
    }

    @Override
    protected void draw() {
        if ( currentController  != null ) {
            currentController.setCameraZoom(getCamera().zoom);
            currentController.render();
        }
    }

    private static Vector2 coordV = new Vector2();

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        boolean res = super.touchDown( screenX, screenY, pointer, button);

        if ( button == Input.Buttons.LEFT && currentController != null ) {
            coordV.set( screenX, screenY );
            currentController.touchDown(getStage().screenToStageCoordinates(coordV));
        }
        return res;
    }



    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        boolean res = super.touchUp( screenX,screenY, pointer, button );

        if ( button == Input.Buttons.LEFT && currentController != null ) {
            coordV.set( screenX, screenY );
            currentController.touchUp( getStage().screenToStageCoordinates(coordV) );
        }

        return res;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        boolean res = super.touchDragged( screenX, screenY, pointer );

        if ( Gdx.input.isButtonPressed( Input.Buttons.LEFT ) && currentController != null ) {
            coordV.set( screenX, screenY );
            currentController.touchDragged( getStage().screenToStageCoordinates(coordV) );
        }
        return res;
    }
}
