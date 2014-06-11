package org.skr.PhysModelEditor.PropertiesTableElements;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Array;
import org.skr.physmodel.animatedactorgroup.FixtureSet;

import javax.swing.*;
import java.util.Properties;

/**
 * Created by rat on 11.06.14.
 */
public class FixtureSetTableModel extends PropertiesBaseTableModel {


    private enum Properties_ {

        Name(PropertyType.STRING),
        Type(PropertyType.SELECTOR);

        private PropertyType propertyType;

        Properties_(PropertyType propertyType) {
            this.propertyType = propertyType;
        }

        static Properties_[] values = Properties_.values();
        static Array<String> shapeTypeNames = new Array<String>();
        static {
            for ( int i = 0; i < Shape.Type.values().length; i++)
                shapeTypeNames.add( Shape.Type.values()[i].toString() );
        }
    }

    private FixtureSet fixtureSet;

    public FixtureSetTableModel(JTree modelJTree) {
        super(modelJTree);
    }

    public FixtureSet getFixtureSet() {
        return fixtureSet;
    }

    public void setFixtureSet(FixtureSet fixtureSet) {
        this.fixtureSet = fixtureSet;
    }

    @Override
    public int getCurrentSelectorIndex(int rowIndex) {

        if ( fixtureSet == null )
            return -1;

        switch ( Properties_.values[ rowIndex ] ) {

            case Name:
                return -1;
            case Type:
                return fixtureSet.getShapeType().ordinal();
        }

        return -1;
    }

    @Override
    public Array<String> getSelectorArray(int rowIndex) {
        Properties_ prop = Properties_.values[ rowIndex ];

        if ( prop == Properties_.Type )
            return Properties_.shapeTypeNames;
        return null;
    }

    @Override
    public PropertyType getPropertyType(int rowIndex) {
        return Properties_.values[rowIndex].propertyType;
    }

    @Override
    public int getPropertiesCount() {
        return Properties_.values.length;
    }

    @Override
    public boolean isPropertyEditable(int rowIndex) {
        Properties_ property = Properties_.values[rowIndex];
        if ( fixtureSet == null)
            return false;
        if ( property == Properties_.Type && fixtureSet.getFixtures().size != 0 )
            return false;
        return true;
    }

    @Override
    public Object getPropertyName(int rowIndex) {
        return Properties_.values[ rowIndex ].toString();
    }

    @Override
    public Object getPropertyValue(int rowIndex) {
        if ( fixtureSet == null )
            return null;

        switch ( Properties_.values[rowIndex] ) {

            case Name:
                return fixtureSet.getName();
            case Type:
                return fixtureSet.getShapeType().toString();
        }
        return null;
    }

    @Override
    public void setProperty(Object aValue, int rowIndex) {
        if ( fixtureSet == null )
            return;

        switch (Properties_.values[ rowIndex ]) {

            case Name:
                fixtureSet.setName( (String) aValue );
                break;
            case Type:
                fixtureSet.setShapeType( Shape.Type.values()[ (Integer) aValue] );
                break;
        }
    }
}
