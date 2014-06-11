package org.skr.PhysModelEditor.PropertiesTableElements;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;

import javax.swing.*;

/**
 * Created by rat on 08.06.14.
 */
public class FixturePrepertiesTableModel extends PropertiesBaseTableModel {

    private enum Properties_ {
        NOP(PropertyType.STRING);

        private PropertiesBaseTableModel.PropertyType propertyType;
        Properties_(PropertyType propertyType) {
            this.propertyType = propertyType;
        }

        static Properties_[] values = Properties_.values();

        static Array<String> typeNames = new Array<String>();
        static {
            for ( int i = 0; i < BodyDef.BodyType.values().length; i++)
                typeNames.add( BodyDef.BodyType.values()[i].toString() );
        }
    }
    public FixturePrepertiesTableModel(JTree modelJTree) {
        super(modelJTree);
    }


    @Override
    public int getCurrentSelectorIndex(int rowIndex) {
        return 0;
    }




    @Override
    public Array<String> getSelectorArray(int rowIndex) {
        return null;
    }

    @Override
    public PropertyType getPropertyType(int rowIndex) {
        Properties_ property = Properties_.values[rowIndex];
        return property.propertyType;
    }

    @Override
    public int getPropertiesCount() {
        return Properties_.values.length;
    }

    @Override
    public boolean isPropertyEditable(int rowIndex) {
        return true;
    }

    @Override
    public Object getPropertyName(int rowIndex) {
        return null;
    }

    @Override
    public Object getPropertyValue(int rowIndex) {
        if ( rowIndex >= getPropertiesCount() )
            return null;
        return Properties_.values[rowIndex].toString();
    }

    @Override
    public void setProperty(Object aValue, int rowIndex) {

    }
}
