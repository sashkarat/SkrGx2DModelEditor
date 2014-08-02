package org.skr.PhysModelEditor.gdx.editor;

import com.badlogic.gdx.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import org.skr.PhysModelEditor.gdx.editor.screens.EditorScreen;
import org.skr.PhysModelEditor.gdx.editor.screens.SimulationScreen;
import org.skr.gdx.PhysWorld;

/**
 * Created by rat on 30.05.14.
 */
public class GdxApplication extends Game {

    private static GdxApplication instance = null;

    TextureAtlas atlas;
    Array<String> regions = new Array<String>();
    EditorScreen editorScreen;
    SimulationScreen simulationScreen;
    Screen currentScreen;



    public interface ChangeAtlasListener {
        public void atlasUpdated( TextureAtlas atlas);

    }

    ChangeAtlasListener changeAtlasListener;

    public GdxApplication() {
        GdxApplication.instance = this;
    }

    public static GdxApplication get() {
        return GdxApplication.instance;
    }

    public Array<String> getRegions() {
        return regions;
    }

    @Override
    public void create() {

        PhysWorld.create(100);

        editorScreen = new EditorScreen();
        simulationScreen = new SimulationScreen();
        currentScreen = editorScreen;
        setScreen( editorScreen );


    }

    public EditorScreen getEditorScreen() {
        return editorScreen;
    }

    public SimulationScreen getSimulationScreen() {
        return simulationScreen;
    }

    public void toggleEditorScreen() {
        setScreen( editorScreen );
    }

    public void toggleSimulationScreen() {
        setScreen(simulationScreen);
    }

    @Override
    public void setScreen(Screen screen) {
        super.setScreen(screen);
        if ( screen instanceof InputProcessor ) {
            Gdx.input.setInputProcessor((InputProcessor) screen);
        }
    }

    public void setChangeAtlasListener(ChangeAtlasListener changeAtlasListener) {
        this.changeAtlasListener = changeAtlasListener;
    }

    public void loadAtlas(String absoluteFileName ) {

        if ( atlas != null ) {
            atlas.dispose();
        }

        FileHandle packFile = Gdx.files.absolute( absoluteFileName );

        TextureAtlas.TextureAtlasData atlasData = new TextureAtlas.TextureAtlasData(
                                                        packFile, packFile.parent(), false );

        atlas = new TextureAtlas( atlasData );

        this.regions.clear();

        Array<TextureAtlas.TextureAtlasData.Region> regions = atlasData.getRegions();

        for (TextureAtlas.TextureAtlasData.Region region : regions ) {
            if ( region.index > 1)
                continue;
            this.regions.add( region.name );
        }

        this.regions.sort();


        Gdx.app.log("GdxApplication.loadAtlas", "OK");

        if ( changeAtlasListener != null )
            changeAtlasListener.atlasUpdated( atlas );
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    @Override
    public void dispose() {

        super.dispose();

        editorScreen.dispose();

        if ( atlas != null ) {
            atlas.dispose();
        }
    }

}
