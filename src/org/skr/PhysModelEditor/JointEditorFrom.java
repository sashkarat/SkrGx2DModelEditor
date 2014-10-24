package org.skr.PhysModelEditor;

import org.skr.PhysModelEditor.gdx.editor.SkrGdxAppPhysModelEditor;
import org.skr.PhysModelEditor.gdx.editor.controllers.JointEditorController;
import org.skr.gdx.physmodel.PhysModel;
import org.skr.gdx.physmodel.bodyitem.BiScSet;
import org.skr.gdx.physmodel.bodyitem.BodyItem;
import org.skr.gdx.physmodel.jointitem.JointItem;
import org.skr.gdx.physmodel.jointitem.JointItemDescription;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by rat on 19.10.14.
 */
public class JointEditorFrom {
    private JPanel panelRootJointEditorForm;
    private JToggleButton btnBodyItemA;
    private JComboBox comboBodyItemA;
    private JToggleButton btnBodyItemB;
    private JComboBox comboBodyItemB;
    private JCheckBox chbCollideConnected;

    JointEditorController jeController;
    JointItem jointItem;

    public JointEditorFrom() {

        btnBodyItemA.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setBodyItemSelectionMode( true, btnBodyItemA.isSelected() );
            }
        });

        btnBodyItemB.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setBodyItemSelectionMode( false, btnBodyItemB.isSelected() );
            }
        });

        comboBodyItemA.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jeController.setBodyItem((BodyItem) comboBodyItemA.getSelectedItem(), true );
            }
        });

        comboBodyItemB.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jeController.setBodyItem((BodyItem) comboBodyItemB.getSelectedItem(), false );
            }
        });
    }

    public void setupGdxApp() {
        jeController = SkrGdxAppPhysModelEditor.get().getEditorScreen().getJointEditorController();
        jeController.setBodyItemSelectionListener( new JointEditorController.BodyItemSelectionListener() {
            @Override
            public void bodyItemSelected(BodyItem bodyItem, boolean isA) {
                if ( isA ) {
                    btnBodyItemA.setSelected(false );
                    comboBodyItemA.setSelectedItem( bodyItem );
                } else {
                    btnBodyItemB.setSelected( false );
                    comboBodyItemB.setSelectedItem( bodyItem );
                }
            }
        });
    }

    public JointItem getJointItem() {
        return jointItem;
    }

    public void setJointItem(JointItem jointItem) {
        comboBodyItemA.removeAllItems();
        comboBodyItemB.removeAllItems();

        this.jointItem = jointItem;
        PhysModel model = jointItem.getBiScSet().getModel();
        BiScSet biScSet = model.getScBodyItems().getCurrentSet();
        if ( biScSet == null )
            return;

        comboBodyItemA.addItem( null );
        comboBodyItemB.addItem( null );

        for (BodyItem bi : biScSet.getBodyItems() ) {
            comboBodyItemA.addItem( bi);
            comboBodyItemB.addItem( bi );
        }

        if ( jointItem.getJoint() != null ) {
            comboBodyItemA.setSelectedItem( jointItem.getBodyItemA() );
            comboBodyItemB.setSelectedItem( jointItem.getBodyItemB() );
        }

    }

    public void setBodyItemSelectionMode( boolean isA, boolean select ) {
        if ( select )
            jeController.setBodyItemSelectionEnabled(select, isA);
    }


    protected void guiToJiDescription( JointItemDescription jiDesc ) {
        jiDesc.setCollideConnected( chbCollideConnected.isSelected() );
    }

    public JointItemDescription getJointItemDescription() {
        JointItemDescription jiDesc = jeController.getJiDesc();

        guiToJiDescription( jiDesc );

        return jiDesc;
    }
}
