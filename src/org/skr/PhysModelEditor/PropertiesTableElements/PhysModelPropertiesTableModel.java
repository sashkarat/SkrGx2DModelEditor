package org.skr.PhysModelEditor.PropertiesTableElements;

import com.badlogic.gdx.utils.Array;
import org.skr.physmodel.PhysModel;

import javax.swing.*;

/**
 * Created by rat on 31.05.14.
 */
public class PhysModelPropertiesTableModel extends PropertiesBaseTableModel {

    PhysModel model;

    public PhysModelPropertiesTableModel(JTree modelJTree) {
        super(modelJTree);
    }

    @Override
    public int getCurrentSelectorIndex(int rowIndex) {
        return -1;
    }

    @Override
    public Array<String> getSelectorArray(int rowIndex) {
        return null;
    }

    @Override
    public PropertyType getPropertyType(int rowIndex) {

        switch ( rowIndex ) {
            case 0:
                return PropertyType.STRING;
        }

        return PropertyType.STRING;
    }

    public void  setModel( PhysModel model ) {
        this.model = model;
    }


    @Override
    public int getPropertiesCount() {
        return 1;
    }

    @Override
    public Object getPropertyName(int rowIndex) {
        switch ( rowIndex ) {
            case 0:
                return "Name";
            default:
                return "";
        }
    }

    @Override
    public Object getPropertyValue(int rowIndex) {

        if (model == null)
            return null;

        switch ( rowIndex ) {
            case 0:
                return model.getName();
            default:
                return null;
        }
    }

    @Override
    public boolean isPropertyEditable(int rowIndex) {
        return true;
    }

    @Override
    public void setProperty(Object aValue, int rowIndex) {

        if ( model == null )
            return;

        switch ( rowIndex ) {
            case 0:
                model.setName( (String) aValue );
                break;

        }
    }
}
