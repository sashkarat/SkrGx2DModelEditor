package org.skr.PhysModelEditor;

import org.skr.gdx.physmodel.PhysModel;
import org.skr.gdx.physmodel.bodyitem.BodyItem;
import org.skr.gdx.policy.PhysPolicy;

import javax.swing.*;
import java.awt.event.*;

public class DialogPhysPolicyEditor extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private PhysPolicyEditorForm physPolicyEditorForm;

    public DialogPhysPolicyEditor() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
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
