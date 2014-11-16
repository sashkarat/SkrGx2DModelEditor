package org.skr.PhysModelEditor.gdx.editor.screens;

import org.skr.gdx.SkrGdxApplication;
import org.skr.gdx.editor.BaseScreen;
import org.skr.gdx.PhysWorld;
import org.skr.gdx.physmodel.PhysModel;
import org.skr.gdx.physmodel.PhysModelDescription;
import org.skr.gdx.scene.PhysModelDescriptionHandler;
import org.skr.gdx.scene.PhysModelItemDescription;
import org.skr.gdx.scene.PhysScene;

/**
 * Created by rat on 13.07.14.
 */
public class SimulationScreen extends BaseScreen {

    PhysModelDescription description;
    PhysScene testScene;

    boolean simulationEnabled = true;

    public SimulationScreen() {
        super();

        testScene = new PhysScene( PhysWorld.getTestWorld() );
        testScene.setViewLeft(-10000);
        testScene.setViewRight(10000);
        testScene.setViewBottom(-10000);
        testScene.setViewTop(10000);
        testScene.initializeScene(getStage());
        testScene.getCameraController().setHoldCameraInsideBorders(false);
        testScene.setActivePhysics( true );
        getStage().addActor(testScene);

    }


    @Override
    public void show() {
        testScene.setAtlas( SkrGdxApplication.get().getAtlas() );
    }

    @Override
    protected void draw() {

    }

    @Override
    protected void debugRender() {
        PhysWorld.get().debugRenderTestWorld(getStage());
    }

    void resetModel() {
        testScene.clearScene();
    }

    public void setModelDescription(PhysModelDescription description) {
        resetModel();
        this.description = description;
    }

    public void startSimulation() {
        if ( description == null ) {
            return;
        }

        resetModel();
        PhysModelDescriptionHandler mdh = new PhysModelDescriptionHandler();
        mdh.setModelDesc( this.description );
        testScene.addModelItem( mdh );
        testScene.initPolicySlots();
    }

    public void setPause(boolean state) {
        testScene.setActivePhysics( !state );
    }

    public void doStep() {
        testScene.doPhysWorldStep();
    }

}
