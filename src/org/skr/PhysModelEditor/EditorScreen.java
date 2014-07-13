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
    private AnchorPointController anchorPointController;
    private Controller currentController = null;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private Batch fontBatch;


    private boolean simulationEnabled = false;

    public EditorScreen() {
        ScreenViewport vp = new ScreenViewport();
        stage = new Stage( vp );
        camera = (OrthographicCamera) stage.getCamera();
        camera.position.set(0, 0, 0);
        modelRenderer = new PhysModelRenderer( PhysWorld.getPrimaryWorld() );
        stage.addActor( modelRenderer );
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        fontBatch = new SpriteBatch();

        actorController = new ActorController( stage );
        bodyItemController = new BodyItemController( stage );
        circleShapeController = new CircleShapeController( stage );
        edgeShapeController = new EdgeShapeController( stage );
        chainShapeController = new ChainShapeController( stage );
        polygonShapeController = new PolygonShapeController( stage );
        anchorPointController = new AnchorPointController( stage );

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

    public boolean isSimulationEnabled() {
        return simulationEnabled;
    }

    public void setSimulationEnabled(boolean simulationEnabled) {
        this.simulationEnabled = simulationEnabled;
    }

    @Override
    public void render(float delta) {


        Gdx.gl20.glClearColor(0, 0.1f, 0.05f, 1);
        Gdx.gl.glClear(Gdx.gl20.GL_COLOR_BUFFER_BIT | Gdx.gl20.GL_DEPTH_BUFFER_BIT);

        if ( simulationEnabled )
            PhysWorld.get().step();

        shapeRenderer.setProjectionMatrix( stage.getBatch().getProjectionMatrix() );
        shapeRenderer.setTransformMatrix( stage.getBatch().getTransformMatrix() );

        int gridDelta = 10;

        if ( camera.zoom > 3 )
            gridDelta = 100;
        if ( camera.zoom > 4)
            gridDelta = 200;
        if ( camera.zoom < 0.25 )
            gridDelta = 1;

        drawGrid( gridDelta, gridDelta, gridDelta * 5);

        drawGridText( gridDelta * 10 );

        stage.act( delta );
        stage.draw();

        PhysWorld.get().debugRender( stage );

        if ( currentController  != null ) {

            currentController.setCameraZoom(camera.zoom);
            currentController.render();

        }

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
