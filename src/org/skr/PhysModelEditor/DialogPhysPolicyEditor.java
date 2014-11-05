package org.skr.PhysModelEditor;

import org.skr.gdx.physmodel.PhysModel;
import org.skr.gdx.physmodel.bodyitem.BodyItem;

import javax.swing.*;
import java.awt.event.*;

public class DialogPhysPolicyEditor extends JDialog {
    private JPanel contentPane;
    private JButton btnClose;
    private PhysPolicyEditorForm physPolicyEditorForm;
    private MainGui mainGui;

    public DialogPhysPolicyEditor( final MainGui mainGui ) {
        this.mainGui = mainGui;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(btnClose);

        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onOK();
            }
        });

        physPolicyEditorForm.setSaveModelRequestListener( new PhysPolicyEditorForm.SaveModelRequestListener() {
            @Override
            public void saveModel() {
                mainGui.saveModel();
            }
        });

    }

    private void onOK() {
        dispose();
    }

    public void display( PhysModel model ) {
        physPolicyEditorForm.setCurrentBodyItem( null );
        physPolicyEditorForm.setModel( model );
        display();
    }

    public void display( BodyItem bodyItem ) {
        physPolicyEditorForm.setCurrentBodyItem( bodyItem );
        display();
    }

    protected void display() {
        physPolicyEditorForm.loadGui();
        setTitle( "Model: " + physPolicyEditorForm.getModel().getName() + " policies ");
        pack();
        setVisible( true );
    }

}
