package org.skr.PhysModelEditor;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.skr.gdx.policy.PhysPolicyBuilder;
import org.skr.gdx.policy.PhysPolicyProvider;
import org.skr.gdx.policy.PhysPolicySource;

import javax.swing.*;
import java.awt.event.*;

public class DialogPhysPolicySourceEditor extends JDialog {

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private org.fife.ui.rsyntaxtextarea.RSyntaxTextArea taSourceText;
    private JButton btnUpdate;
    private RTextScrollPane tsp;
    protected boolean accept = false;
    protected PhysPolicySource source;

    protected PhysPolicyProvider provider;

    public DialogPhysPolicySourceEditor() {

        taSourceText.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        taSourceText.setAntiAliasingEnabled( true );

        DefaultCompletionProvider completionProvider = new DefaultCompletionProvider();
        for ( String keyStr : PhysPolicyBuilder.getKeywordsMap().keySet() )
            completionProvider.addCompletion( new BasicCompletion( completionProvider, keyStr) );

        AutoCompletion ac = new AutoCompletion( completionProvider );
        ac.install( taSourceText );

        SyntaxScheme ss = taSourceText.getSyntaxScheme();

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
        source.setSourceText( taSourceText.getText() );
        provider.updatePolicy( source );
    }



    public boolean execute(PhysPolicySource source, PhysPolicyProvider provider ) {
        this.source = source;
        this.provider = provider;
        taSourceText.setText(source.getSourceText());
        pack();
        setSize(400, 400);
        setVisible( true );
        if ( accept )
            updatePolicy();
        return accept;
    }


}
