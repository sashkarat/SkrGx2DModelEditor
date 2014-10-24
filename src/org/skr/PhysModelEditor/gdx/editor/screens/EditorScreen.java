package org.skr.PhysModelEditor.gdx.editor.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import org.skr.PhysModelEditor.gdx.editor.controllers.ShapeControllers.ChainShapeController;
import org.skr.PhysModelEditor.gdx.editor.controllers.ShapeControllers.CircleShapeController;
import org.skr.PhysModelEditor.gdx.editor.controllers.ShapeControllers.EdgeShapeController;
import org.skr.PhysModelEditor.gdx.editor.controllers.ShapeControllers.PolygonShapeController;
import org.skr.gdx.editor.BaseScreen;
import org.skr.gdx.PhysModelRenderer;
import org.skr.gdx.PhysWorld;
import org.skr.PhysModelEditor.gdx.editor.controllers.*;
import org.skr.gdx.editor.Controller;
import org.skr.gdx.physmodel.PhysModel;
import org.skr.gdx.physmodel.animatedactorgroup.AnimatedActorGroup;
import org.skr.gdx.physmodel.bodyitem.BiScSet;
import org.skr.gdx.physmodel.bodyitem.BodyItem;
import org.skr.gdx.physmodel.bodyitem.fixtureset.FixtureSet;

/**
 * Created by rat on 02.06.14.
 */
public class EditorScreen extends BaseScreen {

    public static enum SelectionMode {
        DISABLED,
        BODY_ITEM,
        FIXTURE_SET,
        AAG
    }

    public static  enum ModelObjectType {
        OT_None,
        OT_Model,
        OT_BodyItem,
        OT_Aag,
        OT_FixtureSet,
        OT_JointItems,
        OT_JointItem
    }

    public interface ItemSelectionListener {
        public void singleItemSelected(Object object, ModelObjectType mot );
        public void itemAddedToSelection(Object object, ModelObjectType mot, boolean removed );

    }

    private PhysModel model;
    private PhysModelRenderer modelRenderer;
    private SelectionMode selectionMode = SelectionMode.DISABLED;

    private AagController aagController;
    private BodyItemController bodyItemController;
    private CircleShapeController circleShapeController;
    private EdgeShapeController edgeShapeController;
    private ChainShapeController chainShapeController;
    private PolygonShapeController polygonShapeController;
    private JointEditorController jointEditorController;
    private MultiBodyItemsController multiBodyItemsController;
    private Controller currentController = null;
    private ItemSelectionListener itemSelectionListener;

    private Array<Object> selectedItems = new Array<Object>();

    public EditorScreen() {

        super();

        modelRenderer = new PhysModelRenderer();

        getStage().addActor(modelRenderer);

        aagController = new AagController( getStage() );
        bodyItemController = new BodyItemController( getStage() );
        circleShapeController = new CircleShapeController( getStage() );
        edgeShapeController = new EdgeShapeController( getStage() );
        chainShapeController = new ChainShapeController( getStage() );
        polygonShapeController = new PolygonShapeController( getStage() );
        jointEditorController = new JointEditorController( getStage() );
        multiBodyItemsController = new MultiBodyItemsController( getStage() );

    }

    public PhysModel getModel() {
        return model;
    }

    public void setModel(PhysModel model) {
        this.model = model;
        modelRenderer.setModel( model );
    }

    public void removeModel() {
        if ( currentController == multiBodyItemsController )
            multiBodyItemsController.getBodyItems().clear();

        currentController = null;
        selectedItems.clear();

        modelRenderer.removeModel();

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

    public PhysModelRenderer getModelRenderer() {
        return modelRenderer;
    }


    public void setModelObject( Object object, ModelObjectType objectType ) {

        if ( currentController == multiBodyItemsController ) {
            multiBodyItemsController.getBodyItems().clear();
        } else if ( currentController == aagController ) {
            aagController.resetAag();
        }

        currentController = null;
        selectedItems.clear();

        switch ( objectType ) {

            case OT_None:
                return;
            case OT_Model:
                return;
            case OT_BodyItem:
                BodyItem bi = (BodyItem) object;
                bodyItemController.setBodyItem( bi );
                currentController = bodyItemController;
                break;
            case OT_Aag:
                AnimatedActorGroup aag = (AnimatedActorGroup) object;
                aagController.setAag( aag );
                currentController = aagController;
                break;
            case OT_FixtureSet:
                setFixtureSet( (FixtureSet) object );
                break;
            case OT_JointItems:
                break;
            case OT_JointItem:
                currentController = jointEditorController;
                jointEditorController.setJointItem( (org.skr.gdx.physmodel.jointitem.JointItem) object);
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
        multiBodyItemsController.getBodyItems().clear();
        selectedItems.clear();
    }

    public void addModelObject( Object object) {

        currentController = null;

        if ( selectedItems.contains( object, true ) ) {
            return;
        }
        selectedItems.add( object );

        if ( object instanceof BodyItem ) {
            BodyItem bi = (BodyItem) object;
            multiBodyItemsController.getBodyItems().add( bi );
            currentController = multiBodyItemsController;
        }
    }


    public AagController getAagController() {
        return aagController;
    }

    public BodyItemController getBodyItemController() {
        return bodyItemController;
    }

    public JointEditorController getJointEditorController() {
        return jointEditorController;
    }

    public MultiBodyItemsController getMultiBodyItemsController() {
        return multiBodyItemsController;
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

//        Gdx.app.log("EditorScreen.touchDown", "Event unprocessed");

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

    private boolean processSelection(Vector2 stageCoord) {

        switch ( selectionMode ) {
            case DISABLED:
                return false;
            case BODY_ITEM:
                return processBodyItemSelection( stageCoord );
            case FIXTURE_SET:
                return processFixtureSetSelection( stageCoord );
            case AAG:
                return processAagSelection( stageCoord );
        }
        return false;
    }

    private void itemSelected( Object object, ModelObjectType mot ) {

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
            if ( currentController == multiBodyItemsController ) {
                multiBodyItemsController.getBodyItems().removeValue((BodyItem) object, true );
            }
            remove = true;
        }

        if ( itemSelectionListener != null ) {
            itemSelectionListener.itemAddedToSelection(object, mot, remove );
        }
    }

    private boolean processBodyItemSelection( Vector2 stageCoord ) {
        if ( model == null )
            return false;

        Vector2 localC = new Vector2();
        Vector2 localC2 = new Vector2();

        BodyItem selection = null;

        BiScSet currentSet = model.getScBodyItems().getCurrentSet();
        if ( currentSet == null )
            return false;

        for ( BodyItem bi : currentSet.getBodyItems() ) {

            localC.set( stageCoord );
            bi.parentToLocalCoordinates( localC );
            localC2.set( localC );

            FixtureSet fs = bi.getFixtureSet( localC );
            if ( fs != null ) {
                selection = bi;
//                Gdx.app.log("EditorScreen.processBodyItemSelection", " FS: " + fs.getName() );
                break;
            }

            AnimatedActorGroup aag = processAagSelection( localC2, bi );
            if ( aag != null ) {
//                    Gdx.app.log("EditorScreen.processBodyItemSelection", " AAG: " + aag.getName() );
                selection = bi;
                break;
            }
        }

        if ( selection == null )
            return false;

//        Gdx.app.log("EditorScreen.processBodyItemSelection", "BI: " + selection.getName() + " ID: " +
//        selection.getId() );
        itemSelected( selection, ModelObjectType.OT_BodyItem );
        return true;
    }

    private boolean processAagSelection( Vector2 stageCoord ) {
        if ( model == null )
            return false;
        AnimatedActorGroup selectedAag;
        Vector2 localCoord = new Vector2();
        BiScSet bset = model.getScBodyItems().getCurrentSet();
        for ( BodyItem bi: bset.getBodyItems() ) {
            localCoord.set( stageCoord );
//            bi.parentToLocalCoordinates(localCoord);
            selectedAag = processAagSelection( localCoord, bi );
            if ( selectedAag != null ) {
                itemSelected( selectedAag, ModelObjectType.OT_Aag );
                return true;
            }
        }
        return false;
    }

    private AnimatedActorGroup processAagSelection( Vector2 parentCoord, AnimatedActorGroup parentAag ) {

        Vector2 localCoord = new Vector2( parentCoord );

        localCoord = parentAag.parentToLocalCoordinates( localCoord );

        AnimatedActorGroup resAag = parentAag.getAag( localCoord );
//        if ( resAag != null ) {
//            Gdx.app.log("EditorScreen.processAagSelection", " AAG: " + resAag );
//        }
        return resAag;
    }


    private boolean processFixtureSetSelection( Vector2 stageCoord ) {
        if ( model == null )
            return false;
        Vector2 localCoord = new Vector2( stageCoord );

        BiScSet currentSet = model.getScBodyItems().getCurrentSet();
        if ( currentSet == null )
            return false;

        for ( BodyItem bi : currentSet.getBodyItems() ) {
            localCoord.set( stageCoord );
            localCoord = bi.parentToLocalCoordinates( localCoord );
            FixtureSet fs = bi.getFixtureSet( localCoord );
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

        if ( Gdx.input.isButtonPressed( Input.Buttons.LEFT ) && currentController != null ) {
            coordV.set( screenX, screenY );
            res = currentController.touchDragged( getStage().screenToStageCoordinates(coordV) );
        }
        if ( res )
            return res;

//        Gdx.app.log("EditorScreen.touchDragged", "Event unhandled");

        return res;
    }
}
