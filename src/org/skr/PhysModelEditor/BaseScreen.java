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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Created by rat on 13.07.14.
 */
public abstract class BaseScreen implements Screen, InputProcessor {


    private Stage stage;
    private OrthographicCamera camera;


    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private Batch fontBatch;


    private boolean displayGrid = true;
    private boolean displayGridText = true;
    private boolean doDebugRender = true;

    public BaseScreen() {
        ScreenViewport vp = new ScreenViewport();
        stage = new Stage( vp );
        camera = (OrthographicCamera) stage.getCamera();
        camera.position.set(0, 0, 0);

        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        fontBatch = new SpriteBatch();
    }

    public Stage getStage() {
        return stage;
    }

    public OrthographicCamera getCamera() {
        return this.camera;
    }

    public boolean isDisplayGrid() {
        return displayGrid;
    }

    public void setDisplayGrid(boolean displayGrid) {
        this.displayGrid = displayGrid;
    }

    public boolean isDisplayGridText() {
        return displayGridText;
    }

    public void setDisplayGridText(boolean displayGridText) {
        this.displayGridText = displayGridText;
    }

    public boolean isDoDebugRender() {
        return doDebugRender;
    }

    public void setDoDebugRender(boolean doDebugRender) {
        this.doDebugRender = doDebugRender;
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

    private static  float downPosX = 0;
    private static  float downPosY = 0;
    private boolean mouseDragged = false;

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        mouseDragged = false;
        if ( button == Input.Buttons.MIDDLE ) {
            downPosX = screenX;
            downPosY = screenY;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if ( !mouseDragged )
            clicked( screenX, screenY, button );
        mouseDragged = false;
        return false;
    }


    protected void clicked( int screenX, int screenY, int button ) {
        //dumb
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        mouseDragged = true;
        if ( Gdx.input.isButtonPressed( Input.Buttons.MIDDLE ) ) {

            float offsetX = screenX - downPosX;
            float offsetY = screenY - downPosY;

            downPosX = screenX;
            downPosY = screenY;

            camera.translate( - offsetX * camera.zoom, offsetY * camera.zoom, 0);

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

        if ( camera.zoom > 100 )
            camera.zoom = 100;

        if ( camera.zoom < 0.0125f )
            camera.zoom = 0.0125f;
        return true;
    }

    protected abstract void draw();

    protected void act( float delta ) {
        // dumb
    }

    void drawGrid( int deltaX, int deltaY, int delta) {

        float z = camera.zoom;

        float w = camera.viewportWidth * z;
        float h = camera.viewportHeight * z;

        float x1 = camera.position.x - w / 2;
        float x2 = x1 + w;

        float y1 = camera.position.y - h / 2;
        float y2 = y1 + h;

        int x = ((int) ( x1 / deltaX )) * deltaX;
        int y = ((int) ( y1 / deltaY )) * deltaY;

        shapeRenderer.setColor( 0.2f, 0.2f, 0.2f, 1f);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        while ( x < x2 ) {
            shapeRenderer.line(x, y1, x, y2);
            x+= deltaX;
        }

        while ( y < y2 ) {
            shapeRenderer.line( x1, y, x2, y );
            y+=deltaY;
        }

        x = ((int) ( x1 / deltaX )) * delta;
        y = ((int) ( y1 / deltaY )) * delta;

        shapeRenderer.setColor( 0.2f, 0.3f, 0, 1);

        while ( x < x2 ) {
            shapeRenderer.line(x, y1, x, y2);
            x+= delta;
        }

        while ( y < y2 ) {
            shapeRenderer.line( x1, y, x2, y );
            y+= delta;
        }


        shapeRenderer.setColor( 0.5f, 0.5f, 0.5f, 1);

        shapeRenderer.line( 0, y1, 0, y2 );
        shapeRenderer.line(x1, 0, x2, 0);

        shapeRenderer.end();

//        Gdx.app.log("EditorScreen.drawGrid", "Camera: " + camera.position + " " + camera.viewportWidth);
    }

    private void drawGridText( int delta ) {

        float z = camera.zoom;

        float w = camera.viewportWidth * z;
        float h = camera.viewportHeight * z;

        float x1 = camera.position.x - w / 2;
        float x2 = x1 + w;

        float y1 = camera.position.y - h / 2;
        float y2 = y1 + h;

        int x = ((int) ( x1 / delta )) * delta;
        int y = ((int) ( y1 / delta )) * delta;


        fontBatch.setProjectionMatrix( camera.projection );
        fontBatch.getProjectionMatrix().scl( camera.zoom );

        float offsetX =  - camera.position.x ;
        float offsetY =  - camera.position.y ;

        fontBatch.begin();

        while ( x < x2 ) {
            font.setColor( 0, 1, 0.2f, 1);
            font.drawMultiLine( fontBatch, " " + x,
                    (x + offsetX) / z, (y2 + offsetY) / z - 2 );
            font.setColor( 0.2f, 0.8f, 1, 1);
            font.drawMultiLine( fontBatch, " " + PhysWorld.get().toPhys(x),
                    (x + offsetX) / z, (y1 + offsetY) / z + 20 );
            x += delta;
        }

        while ( y < y2 ) {
            font.setColor( 0, 1, 0.2f, 1);
            font.drawMultiLine( fontBatch, " " + y,
                    (x1 + offsetX) / z + 2, (y + offsetY) / z );
            font.setColor( 0.2f, 0.8f, 1, 1);
            font.drawMultiLine( fontBatch, " " + PhysWorld.get().toPhys(y),
                    (x2 + offsetX) / z - 35, (y + offsetY) / z );
            y+= delta;
        }

        fontBatch.end();

    }

    protected void debugRender() {
        // stub
    }

    @Override
    public void render(float delta) {

        act( delta );

        Gdx.gl20.glClearColor(0, 0.1f, 0.05f, 1);
        Gdx.gl.glClear(Gdx.gl20.GL_COLOR_BUFFER_BIT | Gdx.gl20.GL_DEPTH_BUFFER_BIT);

        shapeRenderer.setProjectionMatrix( stage.getBatch().getProjectionMatrix() );
        shapeRenderer.setTransformMatrix( stage.getBatch().getTransformMatrix() );

        int gridDelta = 10;

        if ( camera.zoom > 3 )
            gridDelta = 100;
        if ( camera.zoom > 4)
            gridDelta = 200;
        if ( camera.zoom > 8 )
            gridDelta = 1000;
        if ( camera.zoom < 0.25 )
            gridDelta = 1;

        if ( displayGrid )
            drawGrid( gridDelta, gridDelta, gridDelta * 5);

        if ( displayGridText)
            drawGridText( gridDelta * 10 );

        stage.act( delta );
        stage.draw();

        if ( doDebugRender)
            debugRender();

        draw();
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
}
