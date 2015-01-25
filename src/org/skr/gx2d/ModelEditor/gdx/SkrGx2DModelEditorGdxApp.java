package org.skr.gx2d.ModelEditor.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.LifecycleListener;
import org.skr.gx2d.ModelEditor.gdx.screens.EditorScreen;
import org.skr.gx2d.ModelEditor.gdx.screens.SimulationScreen;
import org.skr.gx2d.editor.Gx2DEditorApplication;

/**
 * Created by rat on 02.08.14.
 */
public class SkrGx2DModelEditorGdxApp extends Gx2DEditorApplication {

    EditorScreen editorScreen;
    SimulationScreen simulationScreen;

    @Override
    protected void onCreate() {

//        Gdx.app.postRunnable();

        Gdx.app.addLifecycleListener( new LifecycleListener() {
            @Override
            public void pause() {
                Gdx.app.log("SkrGx2DModelEditorGdxApp.LifecycleListener.pause", "...");
            }

            @Override
            public void resume() {
                Gdx.app.log("SkrGx2DModelEditorGdxApp.LifecycleListener.resume", "...");
            }

            @Override
            public void dispose() {
                Gdx.app.log("SkrGx2DModelEditorGdxApp.LifecycleListener.dispose", "...");
            }
        });

        editorScreen = new EditorScreen();
        simulationScreen = new SimulationScreen();

        addEditorScreen( editorScreen );
        addEditorScreen( simulationScreen );

        toggleCurrentScreen(editorScreen);

        super.onCreate();
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

    public static SkrGx2DModelEditorGdxApp get() {
        return (SkrGx2DModelEditorGdxApp) Gx2DEditorApplication.inst();
    }


}
