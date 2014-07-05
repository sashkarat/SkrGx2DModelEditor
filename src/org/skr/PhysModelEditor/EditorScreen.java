package org.skr.PhysModelEditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.skr.PhysModelEditor.controller.*;
import org.skr.physmodel.BodyItem;
import org.skr.physmodel.FixtureSet;

/**
 * Created by rat on 02.06.14.
 */
public class EditorScreen implements Screen, InputProcessor {

    private  Stage stage;
    private  OrthographicCamera camera;
    private  PhysModelRenderer modelRenderer;
    private ActorController actorController;
    private BodyItemController bodyItemController;
    private CircleShapeController circleShapeController;
    private EdgeShapeController edgeShapeController;
    private ChainShapeController chainShapeController;
    private PolygonShapeController polygonShapeController;
    private Controller currentController = null;
    private ShapeRenderer shapeRenderer;

    public EditorScreen() {
        ScreenViewport vp = new ScreenViewport();
        stage = new Stage( vp );
        camera = (OrthographicCamera) stage.getCamera();
        camera.position.set(0, 0, 0);
        modelRenderer = new PhysModelRenderer();
        stage.addActor( modelRenderer );
        shapeRenderer = new ShapeRenderer();

        actorController = new ActorController( stage );
        bodyItemController = new BodyItemController( stage );
        circleShapeController = new CircleShapeController( stage );
        edgeShapeController = new EdgeShapeController( stage );
        chainShapeController = new ChainShapeController( stage );
        polygonShapeController = new PolygonShapeController( stage );

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

        }

    }

    public ActorController getActorController() {
        return actorController;
    }

    public BodyItemController getBodyItemController() {
        return bodyItemController;
    }

    public ShapeController getCurrentShapeController() {
        if ( currentController == null )
            return null;
        if ( ! (circleShapeController instanceof ShapeController) )
            return null;
        return (ShapeController) currentController;
    }

    @Override
    public void render(float delta) {
        Gdx.gl20.glClearColor(0, 0.1f, 0.05f, 1);
        Gdx.gl.glClear(Gdx.gl20.GL_COLOR_BUFFER_BIT | Gdx.gl20.GL_DEPTH_BUFFER_BIT);

        shapeRenderer.setProjectionMatrix( stage.getBatch().getProjectionMatrix() );
        shapeRenderer.setTransformMatrix( stage.getBatch().getTransformMatrix() );

        drawGrid();

        stage.act( delta );
        stage.draw();

        PhysWorld.get().debugRender( stage );

        if ( currentController  != null ) {

            currentController.setCameraZoom(camera.zoom);
            currentController.render();

        }

    }

    void drawGrid() {

        float gridX = 10;
        float gridY = 10;

        int gridW = 100;
        int gridH = 100;

        float fromX = - gridW/2 * gridX;
        float fromY = - gridH/2 * gridY;

        float x1,y1,x2,y2;

        shapeRenderer.setColor( 0.2f, 0.2f, 0.2f, 1f);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        y1 = fromY;
        y2 = fromY + gridH * gridY - gridY;
        for ( int i = 0; i < gridW; i++) {
            x1 = fromX + i * gridX;
            x2 = x1;
            shapeRenderer.line( x1, y1, x2, y2 );
        }

        x1 = fromX;
        x2 = fromX + gridW * gridX - gridX;
        for ( int j = 0; j < gridH; j++) {
            y1 = fromY + j * gridY;
            y2 = y1;
            shapeRenderer.line( x1, y1, x2, y2 );
        }

        shapeRenderer.setColor( 0.5f, 0.5f, 0.5f, 1);

        x1 = fromX;
        x2 = fromX + gridW * gridX - gridX;
        y1 = y2 = 0;
        shapeRenderer.line( x1, y1, x2, y2 );

        x1 = x2 = 0;
        y1 = fromY;
        y2 = fromY + gridH * gridY - gridY;
        shapeRenderer.line( x1, y1, x2, y2 );

        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, false);
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }


    private static Vector2 coordV = new Vector2();
    private static  float downPosX = 0;
    private static  float downPosY = 0;


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        if ( button == Input.Buttons.MIDDLE ) {
            downPosX = screenX;
            downPosY = screenY;
        } else if ( button == Input.Buttons.LEFT && currentController != null ) {
            coordV.set( screenX, screenY );
            currentController.touchDown(stage.screenToStageCoordinates(coordV));
        }

        return false;
    }



    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        if ( button == Input.Buttons.LEFT && currentController != null ) {
            coordV.set( screenX, screenY );
            currentController.touchUp( stage.screenToStageCoordinates(coordV) );
        }

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        if ( Gdx.input.isButtonPressed( Input.Buttons.MIDDLE ) ) {

            float offsetX = screenX - downPosX;
            float offsetY = screenY - downPosY;

            downPosX = screenX;
            downPosY = screenY;

            camera.translate( - offsetX * camera.zoom, offsetY * camera.zoom, 0);

        } else if ( Gdx.input.isButtonPressed( Input.Buttons.LEFT ) && currentController != null ) {
            coordV.set( screenX, screenY );
            currentController.touchDragged( stage.screenToStageCoordinates(coordV) );
        }


        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        if ( amount > 0)
            camera.zoom *= 2;
        else if (amount < 0)
            camera.zoom /= 2;
        return true;
    }
}
