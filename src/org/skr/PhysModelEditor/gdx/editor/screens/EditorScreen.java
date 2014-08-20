package org.skr.PhysModelEditor.gdx.editor.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.scenes.scene2d.Actor;
import org.skr.gdx.editor.BaseScreen;
import org.skr.gdx.PhysModelRenderer;
import org.skr.gdx.PhysWorld;
import org.skr.PhysModelEditor.gdx.editor.controllers.*;
import org.skr.gdx.editor.controller.Controller;
import org.skr.gdx.physmodel.BodyItem;
import org.skr.gdx.physmodel.FixtureSet;
import org.skr.gdx.physmodel.PhysModel;

/**
 * Created by rat on 02.06.14.
 */
public class EditorScreen extends BaseScreen {

    public interface BodyItemSelectionListener {
        public void bodyItemSelected( BodyItem bi );
    }

    private PhysModelRenderer modelRenderer;

    private ActorController actorController;
    private BodyItemController bodyItemController;
    private CircleShapeController circleShapeController;
    private EdgeShapeController edgeShapeController;
    private ChainShapeController chainShapeController;
    private PolygonShapeController polygonShapeController;
    private AnchorPointController anchorPointController;
    private Controller currentController = null;
    private BodyItemSelectionListener bodyItemSelectionListener;

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


    public BodyItemSelectionListener getBodyItemSelectionListener() {
        return bodyItemSelectionListener;
    }

    public void setBodyItemSelectionListener(BodyItemSelectionListener bodyItemSelectionListener) {
        this.bodyItemSelectionListener = bodyItemSelectionListener;
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
        if ( res )
            return res;
        if ( button == Input.Buttons.LEFT && currentController != null ) {
            coordV.set( screenX, screenY );
            res =  currentController.touchDown(getStage().screenToStageCoordinates(coordV));
        }
        if ( res )
            return res;

        Gdx.app.log("EditorScreen.touchDown", "Event unprocessed");

        return res;
    }



    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        boolean res = super.touchUp( screenX,screenY, pointer, button );

        if ( res )
            return res;

        if ( button == Input.Buttons.LEFT && currentController != null ) {
            coordV.set( screenX, screenY );
            res = currentController.touchUp( getStage().screenToStageCoordinates(coordV), button );
        }

        if ( res )
            return res;

        Gdx.app.log("EditorScreen.touchUp", "Event unprocessed");

        return res;
    }

    @Override
    protected boolean clicked(int screenX, int screenY, int button) {

        boolean res = false;
        if ( currentController != null ) {
            coordV.set( screenX, screenY );
            res = currentController.mouseClicked( getStage().screenToStageCoordinates(coordV), button );
        }

        if ( res )
            return res;
        if ( button == Input.Buttons.LEFT )  {
            coordV.set( screenX, screenY );
            res = processBodyItemSelection(getStage().screenToStageCoordinates(coordV));
        }

        if ( res )
            return res;
        Gdx.app.log("EditorScreen.clicked", "Event unhandled");
        return false;
    }

    QueryCallback qcb = new QueryCallback() {

        @Override
        public boolean reportFixture(Fixture fixture) {
            Body b = fixture.getBody();
            BodyItem bi = (BodyItem) b.getUserData();

            if ( bodyItemSelectionListener != null )
                bodyItemSelectionListener.bodyItemSelected( bi );

            return false;
        }
    };

    private static final Vector2 localV = new Vector2();

    private boolean processBodyItemSelection(Vector2 stageCoord) {

        //TODO: recode this
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        boolean res = super.touchDragged( screenX, screenY, pointer );

        if ( res )
            return res;

        if ( Gdx.input.isButtonPressed( Input.Buttons.LEFT ) && currentController != null ) {
            coordV.set( screenX, screenY );
            res = currentController.touchDragged( getStage().screenToStageCoordinates(coordV) );
        }
        if ( res )
            return res;

        Gdx.app.log("EditorScreen.touchDragged", "Event unhandled");

        return res;
    }
}
