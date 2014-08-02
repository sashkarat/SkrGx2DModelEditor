package org.skr.PhysModelEditor.PropertiesTableElements;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Array;
import org.skr.gdx.SkrGdxApplication;
import org.skr.gdx.physmodel.animatedactorgroup.AnimatedActorGroup;

import javax.swing.*;

/**
 * Created by rat on 31.05.14.
 */



public class AagPropertiesTableModel extends PropertiesBaseTableModel {

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

    AnimatedActorGroup aag;

    public AagPropertiesTableModel(JTree modelJTree) {
        super(modelJTree);

        AnimatedActorGroup.setGlobalSizeChangedListener( new AnimatedActorGroup.GlobalSizeChangedListener() {
            @Override
            public void sizeChanged(AnimatedActorGroup aag) {
                actorChanged( aag );
            }
        });
    }


    public void actorChanged(AnimatedActorGroup aag ) {
        if ( AagPropertiesTableModel.this.aag == aag ) {
            fireTableDataChanged();
        }
    }


    @Override
    public int getCurrentSelectorIndex(int rowIndex) {

        if ( aag == null )
            return -1;

        switch (rowIndex) {
            case PROP_REGION:
                Array<String> regions = SkrGdxApplication.get().getRegions();
                int indexOf = regions.indexOf(aag.getTextureName(), false );
                return indexOf;
            case PROP_PLAYMODE:
                return getIndexByPlayMode( aag.getPlayMode() );
            default:
                return -1;
        }
    }

    @Override
    public Array<String> getSelectorArray(int rowIndex) {

        if ( aag == null )
            return null;

        switch ( rowIndex ) {
            case PROP_REGION:
                Array<String> regions = SkrGdxApplication.get().getRegions();
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

    public void setAag(AnimatedActorGroup aag) {
        this.aag = aag;
    }

    @Override
    public void setProperty(Object aValue, int rowIndex) {

        if ( aag == null )
            return;

        switch ( rowIndex ) {
            case PROP_NAME:
                aag.setName( (String) aValue );
                break;
            case PROP_REGION:
                {
                    int regIndex = (Integer) aValue;
                    if (regIndex < 0) {
                        aag.setTextureName("");
                    } else {
                        aag.setTextureName(SkrGdxApplication.get().getRegions().get(regIndex));
                    }
                    aag.updateTextures( SkrGdxApplication.get().getAtlas() );
                }
                break;
            case PROP_FRAMEDURATION:
                aag.setFrameDuration( (Float) aValue );
                aag.updateTextures( SkrGdxApplication.get().getAtlas()  );
                break;
            case PROP_PLAYMODE:
                aag.setPlayMode(getPlayModeByIndex( (Integer) aValue ) );
                aag.updateTextures( SkrGdxApplication.get().getAtlas()  );
                break;
            case PROP_WIDTH:
                aag.setWidth( (Float) aValue );
                break;
            case PROP_HEIGHT:
                aag.setHeight( (Float) aValue );
                break;
            case PROP_KEEPASPECTRATIO:
                aag.setKeepAspectRatio( (Boolean) aValue );
                break;
            case PROP_X:
                aag.setX( (Float) aValue );
                break;
            case PROP_Y:
                aag.setY( (Float) aValue );
                break;
            case PROP_ROT:
                aag.setRotation( (Float) aValue );
                break;
            case PROP_DRAWABLE:
                aag.setDrawable( (Boolean) aValue );
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

        if ( aag == null ) {
            return null;
        }
        switch ( rowIndex ) {
            case PROP_NAME:
                return aag.getName();
            case PROP_REGION:
                return aag.getTextureName();
            case PROP_FRAMEDURATION:
                return aag.getFrameDuration();
            case PROP_PLAYMODE:
                return playModes.get( getIndexByPlayMode( aag.getPlayMode()) );
            case PROP_WIDTH:
                return aag.getWidth();
            case PROP_HEIGHT:
                return aag.getHeight();
            case PROP_KEEPASPECTRATIO:
                return aag.isKeepAspectRatio();
            case PROP_X:
                return aag.getX();
            case PROP_Y:
                return aag.getY();
            case PROP_ROT:
                return  aag.getRotation();
            case PROP_DRAWABLE:
                return aag.isDrawable();
        }

        return null;
    }


}
