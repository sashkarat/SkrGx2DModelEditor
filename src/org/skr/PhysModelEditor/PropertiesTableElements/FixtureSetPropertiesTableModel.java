package org.skr.PhysModelEditor.PropertiesTableElements;

import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Array;
import org.skr.gdx.physmodel.bodyitem.fixtureset.FixtureSet;

import javax.swing.*;

/**
 * Created by rat on 11.06.14.
 */
public class FixtureSetPropertiesTableModel extends PropertiesBaseTableModel {

    public interface ShapeTypeListener {
        public void changed( FixtureSet fixtureSet );
    }

    private enum Properties_  {
        Name(PropertyType.STRING),
        Type(PropertyType.SELECTOR),
        Friction(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        Restitution(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        Density(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        FixturesCount(PropertyType.NUMBER, DataRole.PHYS_COORDINATES);

        private PropertyType propertyType;
        private DataRole dataRole = DataRole.DEFAULT;

        Properties_(PropertyType propertyType) {
            this.propertyType = propertyType;
        }

        Properties_(PropertyType propertyType, DataRole dataRole) {
            this.propertyType = propertyType;
            this.dataRole = dataRole;
        }

        static Properties_[] values = Properties_.values();
        static Array<String> shapeTypeNames = new Array<String>();
        static {
            for ( int i = 0; i < Shape.Type.values().length; i++)
                shapeTypeNames.add( Shape.Type.values()[i].toString() );
        }
    }

    private ShapeTypeListener shapeTypeListener;

    private FixtureSet fixtureSet;

    public FixtureSetPropertiesTableModel(JTree modelJTree) {
        super(modelJTree);
    }

    public FixtureSet getFixtureSet() {
        return fixtureSet;
    }

    public void setFixtureSet(FixtureSet fixtureSet) {
        this.fixtureSet = fixtureSet;
    }

    public void setShapeTypeListener(ShapeTypeListener shapeTypeListener) {
        this.shapeTypeListener = shapeTypeListener;
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
    public DataRole getDataRole(int rowIndex) {
        return Properties_.values[rowIndex].dataRole;
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
        if ( property == Properties_.FixturesCount )
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
            case Friction:
                return fixtureSet.getFriction();
            case Restitution:
                return fixtureSet.getRestitution();
            case Density:
                return fixtureSet.getDensity();
            case FixturesCount:
                return fixtureSet.getFixtures().size;
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

                Shape.Type oldType = fixtureSet.getShapeType();
                fixtureSet.setShapeType( Shape.Type.values()[ (Integer) aValue] );

                if ( oldType != fixtureSet.getShapeType() && shapeTypeListener != null )
                    shapeTypeListener.changed( fixtureSet );

                break;
            case Friction:
                fixtureSet.setFriction( (Float) aValue );
                break;
            case Restitution:
                fixtureSet.setRestitution( (Float) aValue );
                break;
            case Density:
                fixtureSet.setDensity( (Float) aValue );
                break;
        }
    }
}
