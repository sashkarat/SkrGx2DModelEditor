package org.skr.gx2d.ModelEditor.gdx.screens;

import org.skr.gx2d.common.Env;
import org.skr.gx2d.editor.AbstractEditorScreen;
import org.skr.gx2d.model.Model;
import org.skr.gx2d.node.NodeFactory;
import org.skr.gx2d.scene.ModelHandler;
import org.skr.gx2d.scene.Scene;

/**
 * Created by rat on 13.07.14.
 */
public class SimulationScreen extends AbstractEditorScreen {
    Scene scene;
    ModelHandler mh;
    String jsonString = "";

    boolean simulationEnabled = true;

    public SimulationScreen() {
        super();


    }

    @Override
    public void create() {
        super.create();
        scene = new Scene();
        scene.setViewLeft(-10000);
        scene.setViewRight(10000);
        scene.setViewBottom(-10000);
        scene.setViewTop(10000);
        Env.get().sceneProvider.addScene( scene );
        mh = new ModelHandler();
        scene.addModelHandler( mh );
        scene.setActivePhysics( false );
    }

    @Override
    public void show() {
        super.show();
        scene.setActivePhysics( true );
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
    protected void draw() {

    }

    @Override
    protected void debugRender() {
//        PhysWorld.get().debugRenderTestWorld(getStage());
    }

    void resetModel() {
        mh.loadModelFromResource();
        mh.constructPhysics();
        mh.constructGraphics();
    }

    public void setModel( Model model ) {
        mh.setJsonString( NodeFactory.getNodeJSonString( model, true ) );
        resetModel();
    }

    public void startSimulation() {
        resetModel();
    }

    public void setPause(boolean state) {
        scene.setActivePhysics(!state);
    }

    public void doStep() {
        scene.doPhysWorldStep();
    }

}
