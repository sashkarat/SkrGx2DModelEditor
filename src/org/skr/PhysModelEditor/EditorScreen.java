package org.skr.PhysModelEditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Created by rat on 02.06.14.
 */
public class EditorScreen implements Screen, InputProcessor {

    private  Stage stage;
    private  OrthographicCamera camera;
    private  PhysModelRenderer modelRenderer;
    private  ActorController actorController;
    private  Controller currentController = null;

    public EditorScreen() {
        ScreenViewport vp = new ScreenViewport();
        stage = new Stage( vp );
        camera = (OrthographicCamera) stage.getCamera();
        camera.position.set(0, 0, 0);
        modelRenderer = new PhysModelRenderer();
        stage.addActor( modelRenderer );

        actorController = new ActorController( stage );

    }

    public PhysModelRenderer getModelRenderer() {
        return modelRenderer;
    }


    public void setSelectedObject( Object object ) {

        if ( actorController.getActor() != null ) {
            actorController.getActor().setUserObject( null );
        }

        if ( object instanceof Actor ) {

            Actor a = (Actor) object;
            a.setUserObject( actorController );
            actorController.setActor( a );
            currentController = actorController;
        }

    }

    public ActorController getActorController() {
        return actorController;
    }

    @Override
    public void render(float delta) {
        Gdx.gl20.glClearColor(0, 0.1f, 0.05f, 1);
        Gdx.gl.glClear(Gdx.gl20.GL_COLOR_BUFFER_BIT | Gdx.gl20.GL_DEPTH_BUFFER_BIT);

        stage.act( delta );
        stage.draw();

        if ( currentController  != null ) {
            currentController.render();
        }

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
