package org.skr.gdx;

import com.badlogic.gdx.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import org.skr.PhysModelEditor.gdx.editor.screens.EditorScreen;
import org.skr.PhysModelEditor.gdx.editor.screens.SimulationScreen;

/**
 * Created by rat on 30.05.14.
 */
public abstract class SkrGdxApplication extends Game {

    private static SkrGdxApplication instance = null;

    TextureAtlas atlas;
    Array<String> regions = new Array<String>();

    Screen currentScreen;



    public interface ChangeAtlasListener {
        public void atlasUpdated( TextureAtlas atlas);

    }

    ChangeAtlasListener changeAtlasListener;

    public SkrGdxApplication() {
        SkrGdxApplication.instance = this;
    }

    public static SkrGdxApplication get() {
        return SkrGdxApplication.instance;
    }

    public Array<String> getRegions() {
        return regions;
    }

    public Screen getCurrentScreen() {
        return currentScreen;
    }

    protected void toggleCurrentScreen(Screen screen) {
        currentScreen = screen;
        setScreen( currentScreen );
    }

    protected  abstract void onCreate();
    protected abstract void onDispose();

    @Override
    public void create() {
        PhysWorld.create(100);
        onCreate();
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


        Gdx.app.log("SkrGdxApplication.loadAtlas", "OK");

        if ( changeAtlasListener != null )
            changeAtlasListener.atlasUpdated( atlas );
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    @Override
    public void dispose() {

        super.dispose();

        onDispose();

        if ( atlas != null ) {
            atlas.dispose();
        }
    }

}
