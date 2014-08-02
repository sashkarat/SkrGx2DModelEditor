package org.skr.PhysModelEditor.gdx.editor.screens;

import org.skr.gdx.SkrGdxApplication;
import org.skr.gdx.editor.BaseScreen;
import org.skr.gdx.PhysModelRenderer;
import org.skr.gdx.PhysWorld;
import org.skr.gdx.physmodel.PhysModel;

/**
 * Created by rat on 13.07.14.
 */
public class SimulationScreen extends BaseScreen {

    PhysModelRenderer modelRenderer;
    PhysModel.Description description;
    boolean simulationEnabled = true;

    public SimulationScreen() {
        super();

        modelRenderer = new PhysModelRenderer(PhysWorld.getTestWorld());
        getStage().addActor(modelRenderer);

    }

    @Override
    protected void act(float delta) {
        if ( simulationEnabled )
            doStep();
    }

    @Override
    protected void debugRender() {
        PhysWorld.get().debugRenderTestWorld(getStage());
    }

    @Override
    protected void draw() {

    }

    void resetModel() {
        PhysModel model = modelRenderer.getModel();
        if (model != null)
            PhysModel.destroyPhysics(model);
        modelRenderer.setModel(null);
    }

    public void setModelDescription(PhysModel.Description description) {
        resetModel();
        this.description = description;
    }

    public void startSimulation() {
        resetModel();
        if ( description == null )
            return;
        PhysModel model = new PhysModel( this.description, PhysWorld.getTestWorld(), SkrGdxApplication.get().getAtlas() );
        modelRenderer.setModel(model);
    }

    public void setPause(boolean state) {
        simulationEnabled = !state;
    }

    public void doStep() {
        PhysWorld.get().stepTestWorld();
    }
}
