package org.skr.gx2d.ModelEditor.PropertiesTableElements;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Created by rat on 13.06.14.
 */
public class PropertiesTableCellRenderer extends DefaultTableCellRenderer {

    public static final Color physCoordinatesColor = new Color(127, 184, 255);
    public static final Color viewCoordinatesColor = new Color(69, 182, 47);


    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {

        Component c = super.getTableCellRendererComponent(table, value, isSelected,
                hasFocus, row, column);

        if ( column == 0 )
            return c;

        if ( isSelected )
            return c;

        if ( table.getModel() == null )
            return c;

        if ( !(table.getModel() instanceof PropertiesBaseTableModel ) )
            return null;



        PropertiesBaseTableModel bmodel = ( PropertiesBaseTableModel ) table.getModel();


        switch ( bmodel.getDataRole( row ) ) {

            case DEFAULT:
                c.setBackground( Color.white );
                break;
            case VIEW_COORDINATES:
                c.setBackground( viewCoordinatesColor );
                break;
            case PHYS_COORDINATES:
                c.setBackground( physCoordinatesColor );
                break;
        }


        return c;
    }
}
