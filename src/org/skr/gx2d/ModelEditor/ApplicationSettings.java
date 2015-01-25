package org.skr.gx2d.ModelEditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;

/**
 * Created by rat on 31.05.14.
 */
public class ApplicationSettings {

    private static ApplicationSettings instance = null;


    private String lastDirectory = "";
    private String textureAtlasFile = "";


    public ApplicationSettings() {
        instance = this;
    }


    // =========== Getters and Setters ============================

    public String getLastDirectory() {
        return lastDirectory;
    }

    public void setLastDirectory(String lastDirectory) {
        this.lastDirectory = lastDirectory;
    }

    public String getTextureAtlasFile() {
        return textureAtlasFile;
    }

    public void setTextureAtlasFile(String textureAtlasFile) {
        this.textureAtlasFile = textureAtlasFile;
    }

    // ===================== static methods ======================

    static public ApplicationSettings get() {
        if (ApplicationSettings.instance == null)
            new ApplicationSettings();
        return instance;
    }


    static public void save() {
        final Json js = new Json();
        String settingsStr = js.toJson(ApplicationSettings.get(), ApplicationSettings.class);

        Preferences preferences = Gdx.app.getPreferences("SkrPhysModelEditor");
        preferences.putString("json", settingsStr);
//        Gdx.app.log("ApplicationSettings.save", settingsStr);
        Gdx.app.log("ApplicationSettings.save", " OK");

        preferences.flush();

    }

    static public void load() {

        Preferences preferences = Gdx.app.getPreferences("SkrPhysModelEditor");

        String settingsStr = preferences.getString("json");
//        Gdx.app.log("ApplicationSettings.load", settingsStr);

        if (settingsStr.isEmpty()) {
            new ApplicationSettings();
            Gdx.app.log("ApplicationSettings.load", " No settings found. Default loaded");
            return;
        }

        final Json js = new Json();


        try {
            js.fromJson(ApplicationSettings.class, settingsStr);
        } catch (SerializationException e) {
            Gdx.app.log("ApplicationSettings.load", e.getMessage() );
            e.printStackTrace();
            new ApplicationSettings();
            Gdx.app.log("ApplicationSettings.load", " Settings loading failed. Set to default");
            return;
        }

        Gdx.app.log("ApplicationSettings.load", " OK");

    }


}
