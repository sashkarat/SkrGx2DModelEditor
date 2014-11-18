package org.skr.PhysModelEditor;

import com.badlogic.gdx.Gdx;
import org.skr.gdx.policy.PhysPolicyProvider;
import org.skr.gdx.policy.PhysPolicySource;

import javax.swing.*;
import java.awt.event.*;

public class DialogPhysPolicySourceEditor extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JEditorPane epSourceText;
    private JButton btnUpdate;
    protected boolean accept = false;
    protected PhysPolicySource source;
    protected PhysPolicyProvider provider;


    public DialogPhysPolicySourceEditor() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);


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

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                updatePolicy();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updatePolicy();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK ), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        accept = true;
        dispose();
    }

    private void onCancel() {
        //TODO: add yes/no dialog
        accept = false;
        dispose();
    }

    protected void updatePolicy() {
        source.setSourceText( epSourceText.getText() );
        provider.updatePolicy( source );
    }



    public boolean execute(PhysPolicySource source, PhysPolicyProvider provider ) {
        this.source = source;
        this.provider = provider;
        epSourceText.setText( source.getSourceText() );
        pack();
        setSize( 400, 400);
        setVisible( true );
        if ( accept )
            updatePolicy();
        return accept;
    }


}
