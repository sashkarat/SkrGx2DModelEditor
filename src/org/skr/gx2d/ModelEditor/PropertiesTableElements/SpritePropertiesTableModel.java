package org.skr.gx2d.ModelEditor.PropertiesTableElements;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Array;
import org.skr.gx2d.ModelEditor.gdx.SkrGx2DModelEditorGdxApp;
import org.skr.gx2d.common.Env;
import org.skr.gx2d.sprite.Sprite;

import javax.swing.*;

/**
 * Created by rat on 31.05.14.
 */



public class SpritePropertiesTableModel extends PropertiesBaseTableModel {

    static final int PROPERTIES_COUNT = 11;

    static final int PROP_NAME = 0;
    static final int PROP_REGION = 1;
    static final int PROP_FRAMEDURATION = 2;
    static final int PROP_PLAYMODE = 3;
    static final int PROP_WIDTH = 4;
    static final int PROP_HEIGHT = 5;
    static final int PROP_KEEPASPECTRATIO = 6;
    static final int PROP_X = 7;
    static final int PROP_Y = 8;
    static final int PROP_ROT = 9;
    static final int PROP_DRAWABLE = 10;


    static final PropertyType [] propTypes = {
            PropertyType.STRING, PropertyType.SELECTOR,
            PropertyType.NUMBER, PropertyType.SELECTOR,
            PropertyType.NUMBER, PropertyType.NUMBER,
            PropertyType.BOOLEAN,
            PropertyType.NUMBER, PropertyType.NUMBER,
            PropertyType.NUMBER, PropertyType.BOOLEAN };
    static final String [] propNames = {
            "Name",          "Tex. Region",
            "Frm. Duration", "Play Mode",
            "Width",         "Height",
            "Keep Aspect Ratio",
            "X",    "Y",
            "Rotation",      "Drawable" };

    static final Array<String> playModes = new Array<String>();

    static {
        playModes.add("Normal");
        playModes.add("Reversed");
        playModes.add("Loop");
        playModes.add("Loop_Reversed");
        playModes.add("Loop_PingPong");
        playModes.add("Loop_Random");
    }


    static Animation.PlayMode getPlayModeByIndex(int index ) {

        switch ( index ) {
            case 0:
                return Animation.PlayMode.NORMAL;
            case 1:
                return Animation.PlayMode.REVERSED;
            case 2:
                return Animation.PlayMode.LOOP;
            case 3:
                return Animation.PlayMode.LOOP_REVERSED;
            case 4:
                return Animation.PlayMode.LOOP_PINGPONG;
            case 5:
                return Animation.PlayMode.LOOP_RANDOM;
        }

        return Animation.PlayMode.LOOP;
    }

    static int getIndexByPlayMode( Animation.PlayMode playMode ) {
        switch ( playMode ) {
            case NORMAL:
                return 0;
            case REVERSED:
                return 1;
            case LOOP:
                return 2;
            case LOOP_REVERSED:
                return 3;
            case LOOP_PINGPONG:
                return 4;
            case LOOP_RANDOM:
                return 5;
        }
        return 2;
    }

    Sprite sprite;

    public SpritePropertiesTableModel(JTree modelJTree) {
        super(modelJTree);
    }


    public void actorChanged(Sprite sprite ) {
        if ( SpritePropertiesTableModel.this.sprite == sprite ) {
            fireTableDataChanged();
        }
    }


    @Override
    public int getCurrentSelectorIndex(int rowIndex) {

        if ( sprite == null )
            return -1;

        switch (rowIndex) {
            case PROP_REGION:
                Array<String> regions = Env.get().taHandle.getTextureRegionNames();
                int indexOf = regions.indexOf(sprite.getTexRegionName(), false );
                return indexOf;
            case PROP_PLAYMODE:
                return getIndexByPlayMode( sprite.getPlayMode() );
            default:
                return -1;
        }
    }

    private Array <String> regions = new Array<String>();

    @Override
    public Array<String> getSelectorArray(int rowIndex) {

        if ( sprite == null )
            return null;

        switch ( rowIndex ) {
            case PROP_REGION:
                regions.clear();
                regions.add( null );
                regions.addAll(Env.get().taHandle.getTextureRegionNames());
                return regions;
            case PROP_PLAYMODE:
                return playModes;
        }

        return null;
    }

    @Override
    public PropertyType getPropertyType(int rowIndex) {

        if ( rowIndex >= propTypes.length )
            return PropertyType.STRING;
        return propTypes[ rowIndex ];
    }

    @Override
    public DataRole getDataRole(int rowIndex) {
        if ( rowIndex >= propTypes.length )
            return DataRole.DEFAULT;
        if ( propTypes[ rowIndex ] == PropertyType.NUMBER )
            return DataRole.VIEW_COORDINATES;
        return DataRole.DEFAULT;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    @Override
    public void setProperty(Object aValue, int rowIndex) {

        if ( sprite == null )
            return;

        switch ( rowIndex ) {
            case PROP_NAME:
                sprite.setName( (String) aValue );
                break;
            case PROP_REGION:
                {
                    int regIndex = (Integer) aValue;
                    if ( regIndex < 0 ) {
                        sprite.setTexRegionName("");
                    } else {
                        String str = regions.get(regIndex);
                        sprite.setTexRegionName(str);
                    }
//                    if (regIndex < 0) {
//                        sprite.setTextureName("");
//                    } else {
//                        sprite.setTextureName(SkrGdxApplication.get().getRegions().get(regIndex));
//                    }
                    sprite.updateTextureRegion(Env.get().taHandle.getAtlas());
                }
                break;
            case PROP_FRAMEDURATION:
                sprite.setFrameDuration( (Float) aValue );
                sprite.updateTextureRegion(Env.get().taHandle.getAtlas());
                break;
            case PROP_PLAYMODE:
                sprite.setPlayMode(getPlayModeByIndex( (Integer) aValue ) );
                sprite.updateTextureRegion(Env.get().taHandle.getAtlas());
                break;
            case PROP_WIDTH:
                sprite.setWidth( (Float) aValue );
                break;
            case PROP_HEIGHT:
                sprite.setHeight( (Float) aValue );
                break;
            case PROP_KEEPASPECTRATIO:
                sprite.setKeepAspectRatio( (Boolean) aValue );
                break;
            case PROP_X:
                sprite.setX( (Float) aValue );
                break;
            case PROP_Y:
                sprite.setY( (Float) aValue );
                break;
            case PROP_ROT:
                sprite.setRotation( (Float) aValue );
                break;
            case PROP_DRAWABLE:
                sprite.setDrawable( (Boolean) aValue );
                break;
        }

    }

    @Override
    public int getPropertiesCount() {
        return PROPERTIES_COUNT;
    }

    @Override
    public boolean isPropertyEditable(int rowIndex) {
        return true;
    }

    @Override
    public Object getPropertyName(int rowIndex) {
        if ( rowIndex >= propNames.length)
            return null;

        return propNames[rowIndex];
    }

    @Override
    public Object getPropertyValue(int rowIndex) {

        if ( sprite == null ) {
            return null;
        }
        switch ( rowIndex ) {
            case PROP_NAME:
                return sprite.getName();
            case PROP_REGION:
                return sprite.getTexRegionName();
            case PROP_FRAMEDURATION:
                return sprite.getFrameDuration();
            case PROP_PLAYMODE:
                return playModes.get( getIndexByPlayMode( sprite.getPlayMode()) );
            case PROP_WIDTH:
                return sprite.getWidth();
            case PROP_HEIGHT:
                return sprite.getHeight();
            case PROP_KEEPASPECTRATIO:
                return sprite.isKeepAspectRatio();
            case PROP_X:
                return sprite.getX();
            case PROP_Y:
                return sprite.getY();
            case PROP_ROT:
                return  sprite.getRotation();
            case PROP_DRAWABLE:
                return sprite.isDrawable();
        }

        return null;
    }


}
