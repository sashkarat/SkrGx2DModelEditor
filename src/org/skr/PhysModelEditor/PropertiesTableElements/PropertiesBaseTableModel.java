package org.skr.PhysModelEditor.PropertiesTableElements;

import com.badlogic.gdx.utils.Array;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * Created by rat on 31.05.14.
 */

public abstract  class PropertiesBaseTableModel extends AbstractTableModel {

    public interface PropertiesChangedListener {
        public void changed();
    }

    public static class Property {
    }

    public enum PropertyType {
        STRING,
        NUMBER,
        BOOLEAN,
        SELECTOR
    }

    JTree modelJTree;
    PropertiesChangedListener propertiesChangedListener;


    public PropertiesBaseTableModel(JTree modelJTree) {
        this.modelJTree = modelJTree;
    }

    public void setPropertiesChangedListener(PropertiesChangedListener propertiesChangedListener) {
        this.propertiesChangedListener = propertiesChangedListener;
    }

    protected void fireJTree() {

        if ( modelJTree != null ) {
            DefaultTreeModel md = (DefaultTreeModel) modelJTree.getModel();
            md.nodeChanged( (DefaultMutableTreeNode) modelJTree.getLastSelectedPathComponent() );
        }
    }


    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if ( columnIndex == 1)
            return Property.class;
        return super.getColumnClass(columnIndex);
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public int getRowCount() {
        return getPropertiesCount();
    }

    @Override
    public String getColumnName(int column) {
        if ( column == 0 ) {
            return "Property";
        } else if ( column == 1 ) {
            return "Value";
        }
        return "";
    }


    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if ( columnIndex == 0) {
            return getPropertyName(rowIndex);
        } else if ( columnIndex == 1 ) {

            return getPropertyValue(rowIndex);
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if ( columnIndex == 0 )
            return false;
        return isPropertyEditable( rowIndex );
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

        if ( columnIndex != 1)
            return;

        setProperty( aValue, rowIndex );

        if ( propertiesChangedListener != null )
            propertiesChangedListener.changed();

        fireJTree();
    }

    public abstract int getCurrentSelectorIndex( int rowIndex );
    public abstract Array<String> getSelectorArray( int rowIndex );
    public abstract PropertyType getPropertyType( int rowIndex );
    public abstract int getPropertiesCount();
    public abstract  boolean isPropertyEditable( int rowIndex);
    public abstract Object getPropertyName( int rowIndex );
    public abstract Object getPropertyValue(int rowIndex);
    public abstract void setProperty( Object aValue, int rowIndex );

}
