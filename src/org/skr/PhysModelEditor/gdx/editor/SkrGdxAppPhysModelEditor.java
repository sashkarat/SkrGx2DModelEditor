package org.skr.PhysModelEditor.gdx.editor;

import org.skr.PhysModelEditor.gdx.editor.screens.EditorScreen;
import org.skr.PhysModelEditor.gdx.editor.screens.SimulationScreen;
import org.skr.gdx.SkrGdxApplication;

/**
 * Created by rat on 02.08.14.
 */
public class SkrGdxAppPhysModelEditor extends SkrGdxApplication {
    EditorScreen editorScreen;
    SimulationScreen simulationScreen;

    public SkrGdxAppPhysModelEditor() {
    }

    @Override
    protected void onCreate() {
        editorScreen = new EditorScreen();
        simulationScreen = new SimulationScreen();
        toggleCurrentScreen(editorScreen);
    }

    @Override
    protected void onDispose() {
        editorScreen.dispose();
        simulationScreen.dispose();
    }

    public EditorScreen getEditorScreen() {
        return editorScreen;
    }

    public SimulationScreen getSimulationScreen() {
        return simulationScreen;
    }

    public void toggleEditorScreen() {
        toggleCurrentScreen(editorScreen);
    }

    public void toggleSimulationScreen() {
        toggleCurrentScreen(simulationScreen);
    }

    public static SkrGdxAppPhysModelEditor get() {
        return (SkrGdxAppPhysModelEditor) SkrGdxApplication.get();
    }
}
