package org.skr.PhysModelEditor;

import org.skr.PhysModelEditor.ScriptSourceEditor.DialogPhysScriptSourceEditor;
import org.skr.gdx.physmodel.PhysModel;
import org.skr.gdx.physmodel.bodyitem.BodyItem;
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

    PhysModel model;
    PhysScriptProvider provider;
    BodyItem currentBodyItem;
    boolean accept;
    PhysScriptSource selectedScriptSrc;

    DefaultListModel<PhysScriptSource> modelScriptsListModel = new DefaultListModel<PhysScriptSource>();

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
                newModelScriptSource();
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



    public void execute( PhysModel model ) {
        accept = false;
        setModel( model );
        loadGui();
        pack();
        setVisible( true );
        if ( !accept )
            selectedScriptSrc = null;
    }

    public PhysScriptSource getSelectedScriptSource() {
        return selectedScriptSrc;
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

    protected void newModelScriptSource() {
        PhysScriptSource src = new PhysScriptSource("new Script");
        src.setSourceText( defaultScriptString );
        if ( !provider.addSource( src ) )
            return;
        modelScriptsListModel.addElement(src);
        listScripts.updateUI();
    }

    protected void duplicateModelScript() {
        //todo: implement
    }

    protected void removeModelScript() {
        //TODO: add Yes/No dialog
        PhysScriptSource src = (PhysScriptSource) listScripts.getSelectedValue();
        PhysScript script = provider.findScript(src);
        if ( provider.removeSource( src ) ) {
            modelScriptsListModel.removeElement(src);
            listScripts.updateUI();
        }
        //todo: look up for all items to remove
    }

    protected void loadModelScriptList( ) {
        modelScriptsListModel.clear();
        for ( PhysScriptSource src : provider.getSourcesArray() )
            modelScriptsListModel.addElement( src );
    }

    protected void processScriptListSelection() {
        selectedScriptSrc = (PhysScriptSource) listScripts.getSelectedValue();
        if ( selectedScriptSrc == null )
            return;
        tfScriptName.setText(selectedScriptSrc.getName());
    }

    protected void setScriptName() {
        PhysScriptSource p = (PhysScriptSource) listScripts.getSelectedValue();
        if ( p== null )
            return;
        p.setName(tfScriptName.getText());
        listScripts.updateUI();
    }

    protected void editScriptSource() {
        PhysScriptSource src = (PhysScriptSource) listScripts.getSelectedValue();
        dlgSourceEditor.execute( src, provider );
    }

}
