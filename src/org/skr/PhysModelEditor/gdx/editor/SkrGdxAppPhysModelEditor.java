package org.skr.PhysModelEditor.gdx.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.LifecycleListener;
import org.skr.PhysModelEditor.gdx.editor.screens.EditorScreen;
import org.skr.PhysModelEditor.gdx.editor.screens.SimulationScreen;

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

//        Gdx.app.postRunnable();

        Gdx.app.addLifecycleListener( new LifecycleListener() {
            @Override
            public void pause() {
                Gdx.app.log("SkrGdxAppPhysModelEditor.LifecycleListener.pause", "...");
            }

            @Override
            public void resume() {
                Gdx.app.log("SkrGdxAppPhysModelEditor.LifecycleListener.resume", "...");
            }

            @Override
            public void dispose() {
                Gdx.app.log("SkrGdxAppPhysModelEditor.LifecycleListener.dispose", "...");
            }
        });
        editorScreen = new EditorScreen();
        simulationScreen = new SimulationScreen();
        toggleCurrentScreen(editorScreen);
    }

    @Override
    protected void onDispose() {
        Gdx.app.log("SkrGdxAppPhysModelEditor.onDispose", "...");
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
