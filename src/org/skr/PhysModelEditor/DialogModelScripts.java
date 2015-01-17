package org.skr.PhysModelEditor;

import org.skr.PhysModelEditor.ScriptSourceEditor.DialogPhysScriptSourceEditor;
import org.skr.gdx.physmodel.PhysModel;
import org.skr.gdx.script.PhysScript;
import org.skr.gdx.script.PhysScriptProvider;
import org.skr.gdx.script.PhysScriptSource;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;

/**
 * Created by rat on 02.11.14.
 */
public class DialogModelScripts extends JDialog {
    private JPanel physScriptEditorPanel;
    private JPanel panelScripts;
    private JList listScripts;
    private JButton btnNewScript;
    private JButton btnDuplicateScript;
    private JButton btnRemScript;
    private JTextField tfScriptName;
    private JButton btnSetScriptName;
    private JButton btnEditScript;
    private JButton btnOk;
    private JButton btnCancel;


    protected static String defaultScriptString =
                    "#New Script source code\n" +
                    "\n" +
                    "function init() {}\n" +
                    "function run() {}";


    MainGui mainGui;
    PhysModel model;
    PhysScriptProvider provider;
    boolean accept;
    PhysScript selectedScript;

    DefaultListModel<PhysScript> modelScriptsListModel = new DefaultListModel<PhysScript>();

    protected static final DialogPhysScriptSourceEditor dlgSourceEditor = new DialogPhysScriptSourceEditor();

    public DialogModelScripts() {
        setContentPane(physScriptEditorPanel);
        setModal(true);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeDialog();
            }
        });

        physScriptEditorPanel.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeDialog();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        listScripts.setModel(modelScriptsListModel);

        btnNewScript.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                newModelScript();
            }
        });
        btnDuplicateScript.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                duplicateModelScript();
            }
        });
        btnRemScript.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                removeModelScript();
            }
        });

        listScripts.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                processScriptListSelection();
            }
        });

        btnSetScriptName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setScriptName();
            }
        });
        btnEditScript.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                editScriptSource();
            }
        });
        tfScriptName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setScriptName();
            }

        });
        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                accept = true;
                closeDialog();
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                closeDialog();
            }
        });
    }

    public static DialogPhysScriptSourceEditor getDlgSourceEditor() {
        return dlgSourceEditor;
    }

    private void closeDialog() {
        dispose();
    }

    public void execute( PhysModel model, PhysScript script ) {
        accept = false;
        setModel( model );
        loadGui();
        pack();
        if ( script != null )
            listScripts.setSelectedValue( script, true );

        setVisible( true );
        if ( !accept )
            selectedScript = null;
    }

    public void setMainGui(MainGui mainGui) {
        this.mainGui = mainGui;
    }

    public PhysScript getSelectedScript() {
        return selectedScript;
    }

    public PhysModel getModel() {
        return model;
    }

    public void setModel(PhysModel model) {
        this.model = model;
        this.provider = model.getScriptProvider();
    }


    protected void loadGui() {
        loadModelScriptList();
    }

    protected void newModelScript() {
        PhysScriptSource src = new PhysScriptSource();
        src.setSourceText( defaultScriptString );
        PhysScript script = provider.addScript("new Script", src);
        modelScriptsListModel.addElement( script );
        listScripts.updateUI();
        mainGui.makeHistorySnapshot();
    }

    protected void duplicateModelScript() {
        if ( selectedScript == null )
            return ;
        PhysScriptSource src = new PhysScriptSource( selectedScript.getSource() );

        PhysScript script = provider.addScript( selectedScript.getName() + "_cpy", src );
        modelScriptsListModel.addElement( script );
        listScripts.updateUI();
        mainGui.makeHistorySnapshot();
    }

    protected void removeModelScript() {
        //TODO: add Yes/No dialog
        if ( selectedScript == null )
            return;
        if ( provider.removeScript( selectedScript ) ) {
            modelScriptsListModel.removeElement( selectedScript );
            listScripts.updateUI();
        }
        //todo: look up for all items to remove
    }

    protected void loadModelScriptList( ) {
        modelScriptsListModel.clear();
        for ( PhysScript script : provider.getScripts() )
            modelScriptsListModel.addElement( script );
    }

    protected void processScriptListSelection() {
        selectedScript = (PhysScript) listScripts.getSelectedValue();
        if ( selectedScript == null )
            return;
        tfScriptName.setText(selectedScript.getName());
    }

    protected void setScriptName() {
        if ( selectedScript == null )
            return;
        selectedScript.setName(tfScriptName.getText());
        listScripts.updateUI();
    }

    protected void editScriptSource() {
        if ( selectedScript == null )
            return;
        dlgSourceEditor.execute( selectedScript, provider );
    }

}
