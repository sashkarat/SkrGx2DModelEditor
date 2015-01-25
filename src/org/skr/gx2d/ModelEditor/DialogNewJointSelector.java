package org.skr.gx2d.ModelEditor;

import com.badlogic.gdx.physics.box2d.JointDef;

import javax.swing.*;
import java.awt.event.*;

public class DialogNewJointSelector extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox comboJointType;

    private boolean accepted = false;

    public DialogNewJointSelector() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        for (JointDef.JointType type : JointDef.JointType.values() ) {
            if ( type == JointDef.JointType.Unknown || type == JointDef.JointType.MouseJoint )
                continue;
            comboJointType.addItem(type);
        }


        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        setTitle(" Select new Joint Type ");
        pack();
    }

    private void onOK() {
// add your code here
        accepted = true;
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }


    public boolean execute() {
        setVisible( true );
        return accepted;
    }

    public JointDef.JointType getSelectedJointType() {
        return (JointDef.JointType) comboJointType.getSelectedItem();
    }

}
