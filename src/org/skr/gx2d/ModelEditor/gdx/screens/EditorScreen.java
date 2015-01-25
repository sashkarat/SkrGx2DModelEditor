package org.skr.gx2d.ModelEditor.gdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import org.skr.gx2d.ModelEditor.gdx.controllers.*;
import org.skr.gx2d.ModelEditor.gdx.controllers.ShapeControllers.ChainShapeController;
import org.skr.gx2d.ModelEditor.gdx.controllers.ShapeControllers.CircleShapeController;
import org.skr.gx2d.ModelEditor.gdx.controllers.ShapeControllers.EdgeShapeController;
import org.skr.gx2d.ModelEditor.gdx.controllers.ShapeControllers.PolygonShapeController;
import org.skr.gx2d.common.Env;
import org.skr.gx2d.editor.AbstractEditorScreen;
import org.skr.gx2d.editor.Controller;
import org.skr.gx2d.model.Model;
import org.skr.gx2d.node.Node;
import org.skr.gx2d.physnodes.BodyHandler;
import org.skr.gx2d.physnodes.FixtureSet;
import org.skr.gx2d.physnodes.JointHandler;
import org.skr.gx2d.physnodes.PhysSet;
import org.skr.gx2d.scene.ModelHandler;
import org.skr.gx2d.scene.Scene;
import org.skr.gx2d.sprite.Sprite;
import org.skr.gx2d.utils.RectangleExt;

/**
 * Created by rat on 02.06.14.
 */
public class EditorScreen extends AbstractEditorScreen {

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    public static enum SelectionMode {
        DISABLED,
        BODY_ITEM,
        FIXTURE_SET,
        AAG
    }

    public static  enum ModelObjectType {
        OT_None,
        OT_Model,
        OT_BodyHandler,
        OT_Sprite,
        OT_FixtureSet,
        OT_JointItem
    }

    public interface ItemSelectionListener {
        public void singleItemSelected(Object object, ModelObjectType mot );
        public void itemAddedToSelection(Object object, ModelObjectType mot, boolean removed );
        public void itemsSelected( Array<? extends Object> objects, ModelObjectType mot );
    }

    private Scene scene;
    private ModelHandler modelHandler;
    private Model model;
    private SelectionMode selectionMode = SelectionMode.DISABLED;

    private SpriteController spriteController;
    private BodyHandlerController bodyHandlerController;
    private CircleShapeController circleShapeController;
    private EdgeShapeController edgeShapeController;
    private ChainShapeController chainShapeController;
    private PolygonShapeController polygonShapeController;
    private JointEditorController jointEditorController;
    private MultiController multiController;
    private Controller currentController = null;
    private ItemSelectionListener itemSelectionListener;

    private Array<Object> selectedItems = new Array<Object>();

    @Override
    public void create() {
        super.create();

        scene = new Scene();
        Env.get().sceneProvider.setActiveScene( Env.get().sceneProvider.addScene(scene) );

        modelHandler = new ModelHandler();
        scene.addModelHandler( modelHandler );

        spriteController = new SpriteController( getStage() );
        bodyHandlerController = new BodyHandlerController( getStage() );
        circleShapeController = new CircleShapeController( getStage() );
        edgeShapeController = new EdgeShapeController( getStage() );
        chainShapeController = new ChainShapeController( getStage() );
        polygonShapeController = new PolygonShapeController( getStage() );
        jointEditorController = new JointEditorController( getStage() );
        multiController = new MultiController( getStage() );
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        removeModel();
        this.model = model;
        modelHandler.setModel( model );
        modelHandler.constructPhysics();
        modelHandler.constructGraphics();
    }

    public void removeModel() {
        if ( currentController == multiController)
            multiController.clear();

        currentController = null;
        selectedItems.clear();

        modelHandler.destroyGraphics();
        modelHandler.destroyPhysics();

    }

    public SelectionMode getSelectionMode() {
        return selectionMode;
    }

    public void setSelectionMode(SelectionMode selectionMode) {
        this.selectionMode = selectionMode;
    }

    public ItemSelectionListener getItemSelectionListener() {
        return itemSelectionListener;
    }

    public void setItemSelectionListener(ItemSelectionListener itemSelectionListener) {
        this.itemSelectionListener = itemSelectionListener;
    }


    protected void resetController() {

        if ( currentController == multiController) {
            multiController.clear();
        } else if ( currentController == spriteController) {
            spriteController.resetSprite();
        }
        currentController = null;
    }

    public void setModelObject( Object object, ModelObjectType objectType ) {

        resetController();

        selectedItems.clear();

        switch ( objectType ) {

            case OT_None:
                return;
            case OT_Model:
                return;
            case OT_BodyHandler:
                BodyHandler bh = (BodyHandler) object;
                bodyHandlerController.setBodyHandler(bh);
                currentController = bodyHandlerController;
                break;
            case OT_Sprite:
                Sprite sprite = (Sprite) object;
                spriteController.setSprite(sprite);
                currentController = spriteController;
                break;
            case OT_FixtureSet:
                setFixtureSet( (FixtureSet) object );
                break;
            case OT_JointItem:
                currentController = jointEditorController;
                jointEditorController.setJointHandler( (JointHandler) object );
                break;
        }

        selectedItems.add( object );
    }

    protected void setFixtureSet( FixtureSet fs ) {
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


    public void clearSelectedItems() {
        multiController.clear();
        selectedItems.clear();
    }

    public void addModelObject( Object object, ModelObjectType mot) {
        if ( currentController != multiController) {
            resetController();
            currentController = multiController;
        }
        if ( multiController.getModelObjectType() == ModelObjectType.OT_None )
            multiController.setModelObjectType( mot );

        if ( multiController.getModelObjectType() != mot )
            return;

        if ( selectedItems.contains( object, true ) ) {
            return;
        }
        selectedItems.add(object);
        multiController.addItem(object);

    }


    public SpriteController getSpriteController() {
        return spriteController;
    }

    public BodyHandlerController getBodyHandlerController() {
        return bodyHandlerController;
    }

    public JointEditorController getJointEditorController() {
        return jointEditorController;
    }

    public MultiController getMultiController() {
        return multiController;
    }

    public ShapeController getCurrentShapeController() {
        if ( currentController == null )
            return null;
        if ( currentController instanceof ShapeController )
            return (ShapeController) currentController;
        return null;
    }

//    @Override
//    protected void debugRender() {
//        Env.get().world.debugRender(getStage());
//    }

    private static Vector2 coordV = new Vector2();
    private Vector2 rectPointA = new Vector2();
    private Vector2 rectPointB = new Vector2();
    private boolean rectStarted = false;

    @Override
    protected void draw() {
        if ( currentController  != null ) {
            currentController.setCameraZoom(getCamera().zoom);
            currentController.render();
        }

        if ( rectStarted ) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(0.8f, 0.8f, 0.8f, 1f);
            shapeRenderer.rect( rectPointA.x, rectPointA.y, rectPointB.x - rectPointA.x, rectPointB.y - rectPointA.y );
            shapeRenderer.end();
        }
    }



    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        boolean res = super.touchDown( screenX, screenY, pointer, button);
        if ( res )
            return res;
        if ( button == Input.Buttons.LEFT ) {
            coordV.set( screenX, screenY );
            getStage().screenToStageCoordinates(coordV);
            if ( currentController != null )
                res = currentController.touchDown(coordV);
            if ( !res ) {
                rectPointA.set(coordV);
                rectPointB.set(coordV);
                rectStarted = true;
            }
        }
        if ( res )
            return res;



//        Gdx.app.log("EditorScreen.touchDown", "Event unprocessed");

        return res;
    }



    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        boolean res = super.touchUp( screenX,screenY, pointer, button );

        if ( res )
            return res;

        if ( button == Input.Buttons.LEFT  ) {
            coordV.set( screenX, screenY );
            getStage().screenToStageCoordinates(coordV);
            if ( currentController != null )
                res = currentController.touchUp( coordV, button );
            if ( !res && rectStarted ) {
                rectPointB.set(coordV);
                res = processRectSelection(rectPointA, rectPointB);
            }
        }

        rectStarted = false;

        if ( res )
            return res;

//        Gdx.app.log("EditorScreen.touchUp", "Event unprocessed");

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
            res = processSelection(getStage().screenToStageCoordinates(coordV));
        }

        if ( res )
            return res;
//        Gdx.app.log("EditorScreen.clicked", "Event unhandled");
        return false;
    }

    @Override
    protected boolean doubleClicked(int screenX, int screenY, int button) {

        if ( currentController != null ) {
            coordV.set( screenX, screenY );

            return currentController.mouseDoubleClicked( getStage().screenToStageCoordinates(coordV), button );
        }

        return false;
    }



    protected boolean processRectSelection( Vector2 pA, Vector2 pB ) {

        RectangleExt rect = new RectangleExt( pA, pB );

        if ( currentController != null ) {
            if ( currentController.selectRectangle( rect ) )
                return true;
        }

        boolean res = false;

        switch ( selectionMode ) {
            case DISABLED:
                break;
            case BODY_ITEM:
                res = processBodyHandlerRectSelection( rect );
                break;
            case FIXTURE_SET:
                break;
            case AAG:
                break;
        }

        return res;
    };

    private boolean processBodyHandlerRectSelection( RectangleExt rect ) {
        PhysSet currentSet = model.getPhysSet();
        if ( currentSet == null )
            return false;
        Array< BodyHandler > biArray = new Array<BodyHandler>();
        for ( Node n : currentSet.getBodyHandler() ) {
            BodyHandler bh = (BodyHandler) n;
            Vector2 c = bh.getBodyHandlerWorldCenter();
            if ( rect.contains( c ) )
                biArray.add( bh );
        }
        if ( biArray.size == 0 )
            return false;
        itemsSelectedByRect(biArray, ModelObjectType.OT_BodyHandler);
        return true;
    }

    private boolean processSelection(Vector2 stageCoord) {

        switch ( selectionMode ) {
            case DISABLED:
                return false;
            case BODY_ITEM:
                return processBodyHandlerSelection( stageCoord );
            case FIXTURE_SET:
                return processFixtureSetSelection( stageCoord );
            case AAG:
                return processSpriteSelection(stageCoord);
        }
        return false;
    }


    protected void itemsSelectedByRect( Array<? extends Object> objects, ModelObjectType mot ) {

        if ( !Gdx.input.isKeyPressed( Input.Keys.CONTROL_LEFT ) ) {
            resetController();
            selectedItems.clear();
            if ( itemSelectionListener != null ) {
                itemSelectionListener.itemsSelected( objects, mot );
            }
            return;
        }

        boolean remove;

        for ( Object object : objects ) {
            remove = false;
            if ( selectedItems.contains( object, true) ) {
                remove = true;
            }
            if ( itemSelectionListener != null ) {
                itemSelectionListener.itemAddedToSelection(object, mot, remove );
            }
        }
    }


    protected void itemSelected( Object object, ModelObjectType mot ) {

        boolean remove = false;

        if ( !Gdx.input.isKeyPressed( Input.Keys.CONTROL_LEFT ) ) {
            selectedItems.clear();
            selectedItems.add( object );
            if ( itemSelectionListener != null ) {
                itemSelectionListener.singleItemSelected( object, mot );
            }
            return;
        }


        if ( selectedItems.contains( object, true ) ) {
            selectedItems.removeValue(object, true);
            if ( currentController == multiController) {
                multiController.removeItem(object);
            }
            remove = true;
        }

        if ( itemSelectionListener != null ) {
            itemSelectionListener.itemAddedToSelection(object, mot, remove );
        }
    }

    private boolean processBodyHandlerSelection( Vector2 stageCoord ) {
        if ( model == null )
            return false;

        Vector2 localC = new Vector2();
        Vector2 localC2 = new Vector2();

        BodyHandler selection = null;

        PhysSet currentSet = model.getPhysSet();
        if ( currentSet == null )
            return false;

        for ( Node n: currentSet.getBodyHandler() ) {
            BodyHandler bh = (BodyHandler) n;
            localC.set( stageCoord );
            bh.parentToLocalCoordinates( localC );
            localC2.set( localC );

            FixtureSet fs = bh.getFixtureSet( localC );
            if ( fs != null ) {
                selection = bh;
//                Gdx.app.log("EditorScreen.processBodyHandlerSelection", " FS: " + fs.getName() );
                break;
            }

            Sprite sprite = processSpriteSelection(localC2, bh);
            if ( sprite != null ) {
//                    Gdx.app.log("EditorScreen.processBodyHandlerSelection", " SPRITE: " + sprite.getName() );
                selection = bh;
                break;
            }
        }

        if ( selection == null )
            return false;

//        Gdx.app.log("EditorScreen.processBodyHandlerSelection", "BI: " + selection.getName() + " ID: " +
//        selection.getId() );
        itemSelected( selection, ModelObjectType.OT_BodyHandler);
        return true;
    }

    private boolean processSpriteSelection(Vector2 stageCoord) {
        if ( model == null )
            return false;
        Sprite selectedSprite;
        Vector2 localCoord = new Vector2();
        PhysSet phSet = model.getPhysSet();
        for ( Node n: phSet.getBodyHandler() ) {
            BodyHandler bh = (BodyHandler) n;
            localCoord.set( stageCoord );
//            bh.parentToLocalCoordinates(localCoord);
            selectedSprite = processSpriteSelection(localCoord, bh);
            if ( selectedSprite != null ) {
                itemSelected( selectedSprite, ModelObjectType.OT_Sprite);
                return true;
            }
        }
        return false;
    }

    private Sprite processSpriteSelection(Vector2 parentCoord, Sprite parentSprite) {

        Vector2 localCoord = new Vector2( parentCoord );

        localCoord = parentSprite.parentToLocalCoordinates( localCoord );

        return parentSprite.getSprite( localCoord );
    }


    private boolean processFixtureSetSelection( Vector2 stageCoord ) {
        if ( model == null )
            return false;
        Vector2 localCoord = new Vector2( stageCoord );

        PhysSet currentSet = model.getPhysSet();
        if ( currentSet == null )
            return false;

        for ( Node n: currentSet.getBodyHandler() ) {
            BodyHandler bh = (BodyHandler) n;
            localCoord.set( stageCoord );
            localCoord = bh.parentToLocalCoordinates( localCoord );
            FixtureSet fs = bh.getFixtureSet( localCoord );
            if ( fs == null )
                continue;
//            Gdx.app.log("EditorScreen.processFixtureSetSelection",
//                    "FS: " + fs.getName() );
            itemSelected( fs, ModelObjectType.OT_FixtureSet );
            return  true;
        }

        return false;
    }


    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        boolean res = super.touchDragged( screenX, screenY, pointer );

        if ( res )
            return res;

        if ( Gdx.input.isButtonPressed( Input.Buttons.LEFT )) {
            coordV.set( screenX, screenY );
            getStage().screenToStageCoordinates(coordV);
            if ( currentController != null )
                res = currentController.touchDragged( coordV );
            if ( !res && rectStarted ) {
                rectPointB.set( coordV );
            }
        }
        if ( res )
            return res;

//        Gdx.app.log("EditorScreen.touchDragged", "Event unhandled");

        return res;
    }
}
