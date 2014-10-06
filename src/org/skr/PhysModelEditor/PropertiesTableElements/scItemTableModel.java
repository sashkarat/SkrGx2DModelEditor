package org.skr.PhysModelEditor.PropertiesTableElements;

import com.badlogic.gdx.utils.Array;
import org.skr.gdx.SelectableContent.ScContainer;

import javax.swing.*;

/**
 * Created by rat on 14.09.14.
 */
public class scItemTableModel extends PropertiesBaseTableModel {

    ScContainer.Handler handler;

    private enum Property_ {

        Id(PropertyType.NUMBER),
        Name(PropertyType.STRING),
        Selected(PropertyType.BOOLEAN);


        private PropertyType propertyType;
        private DataRole dataRole = DataRole.DEFAULT;

        Property_(PropertyType propertyType) {
            this.propertyType = propertyType;
        }

        Property_(PropertyType propertyType, DataRole dataRole) {
            this.propertyType = propertyType;
            this.dataRole = dataRole;
        }

        static Property_[] values = Property_.values();
    }


    static Property_ getProperty( int rowIndex ) {
        return Property_.values[ rowIndex ];
    }

    public scItemTableModel(JTree modelJTree) {
        super(modelJTree);
    }

    public ScContainer.Handler getHandler() {
        return handler;
    }

    public void setHandler(ScContainer.Handler handler) {
        this.handler = handler;
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
        return getProperty( rowIndex ).propertyType ;
    }

    @Override
    public DataRole getDataRole(int rowIndex) {
        return getProperty( rowIndex ).dataRole;
    }

    @Override
    public int getPropertiesCount() {
        return Property_.values.length;
    }

    @Override
    public boolean isPropertyEditable(int rowIndex) {
        return true;
    }

    @Override
    public Object getPropertyName(int rowIndex) {
        return getProperty( rowIndex ).name();
    }

    @Override
    public Object getPropertyValue(int rowIndex) {

        switch (getProperty(rowIndex)) {
            case Id:
                return handler.id;
            case Name:
                String name = handler.container.findNameById( handler.id );
                if ( name == null )
                    return "";
                return name;
            case Selected:
                return handler.container.isContentSelected( handler.id );
        }

        return null;
    }

    @Override
    public void setProperty(Object aValue, int rowIndex) {

        switch (getProperty(rowIndex)) {
            case Id:
                Integer newId = Integer.valueOf( (int) Math.abs( (Float) aValue ) );
                if ( handler.container.changeId( handler.id, newId ) )
                    handler.id = newId;
                break;
            case Name:
                handler.container.setContentName( handler.id, (String) aValue );
                break;
            case Selected:
                if ( handler.container.isContentSelected( handler.id ) &&
                        !((Boolean)aValue) ) {
                    handler.container.deselectCurrentContent();
                } else {
                    handler.container.selectContent(handler.id);
                }
                break;
        }
    }
}
