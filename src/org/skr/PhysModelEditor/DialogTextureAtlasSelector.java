package org.skr.PhysModelEditor;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;

public class DialogTextureAtlasSelector extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField tfTextureAtlasFilePath;
    private JButton btnBrowse;


    private boolean result;

    public DialogTextureAtlasSelector() {
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
        btnBrowse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                browseFile();
            }
        });
    }

    private void onOK() {

        result = true;

        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }



    public boolean execute( String directoryPath ) {
        result = false;
        pack();
        setVisible( true );
        return result;
    }

    private void browseFile() {

        final JFileChooser fch = new JFileChooser();
        int res;

        fch.setCurrentDirectory( new File(ApplicationSettings.get().getLastDirectory()) );

        res = fch.showDialog( null, "Select");

        if ( res != JFileChooser.APPROVE_OPTION )
            return;

        tfTextureAtlasFilePath.setText(fch.getSelectedFile().getAbsolutePath());
    }


    public String getTextureAtlasFilePath() {
        return tfTextureAtlasFilePath.getText();
    }

    public void setTfTextureAtlasFilePath( String filePath ) {
        tfTextureAtlasFilePath.setText( filePath );
    }

    public static void main(String[] args) {
        DialogTextureAtlasSelector dialog = new DialogTextureAtlasSelector();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
