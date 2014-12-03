package org.skr.PhysModelEditor;

import org.skr.PhysModelEditor.PolisySourceEditor.DialogPhysPolicySourceEditor;
import org.skr.gdx.physmodel.PhysModel;
import org.skr.gdx.physmodel.bodyitem.BiScContainer;
import org.skr.gdx.physmodel.bodyitem.BiScSet;
import org.skr.gdx.physmodel.bodyitem.BodyItem;
import org.skr.gdx.policy.PhysPolicy;
import org.skr.gdx.policy.PhysPolicyProvider;
import org.skr.gdx.policy.PhysPolicySlot;
import org.skr.gdx.policy.PhysPolicySource;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;

/**
 * Created by rat on 02.11.14.
 */
public class DialogModelPolicies extends JDialog {
    private JPanel physPolicyEditorPanel;
    private JPanel panelPolicies;
    private JList listPolicies;
    private JButton btnNewPolicy;
    private JButton btnDuplicatePolicy;
    private JButton btnRemPolicy;
    private JButton btnSetBodyItemPolicy;
    private JButton btnRemBodyItemPolicy;
    private JTextField tfPolicyName;
    private JButton btnSetPolicyName;
    private JButton btnEditPolicy;
    private JList listBodyItems;
    private JLabel lblBodyItemOnCollisionPolicy;


    protected static String defaultPolicyString =
                    "//Only #DEFINE instructions enabled here\n" +
                    "\n" +
                    "#INIT_SECTION\n" +
                    "//Allocate 2 registers in policy slot:\n" +
                    "AllocReg 2;\n" +
                    "//TODO: Place other initialization code here.\n" +
                    "\n" +
                    "#MAIN_SECTION\n" +
                    "//TODO: Place main code here.\n";

    PhysModel model;
    PhysPolicyProvider provider;
    BodyItem currentBodyItem;

    DefaultListModel<PhysPolicySource> modelPoliciesListModel = new DefaultListModel<PhysPolicySource>();
    DefaultListModel<BodyItem> modelBodyItemListModel = new DefaultListModel<BodyItem>();

    public interface SaveModelRequestListener {
        public void saveModel();
    }

    SaveModelRequestListener saveModelRequestListener;

    protected static final DialogPhysPolicySourceEditor dlgSourceEditor = new DialogPhysPolicySourceEditor();

    public DialogModelPolicies() {
        setContentPane(physPolicyEditorPanel);
        setModal(true);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeDialog();
            }
        });

        physPolicyEditorPanel.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeDialog();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        listPolicies.setModel( modelPoliciesListModel );
        listBodyItems.setModel( modelBodyItemListModel );

        btnNewPolicy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                newModelPolicySource();
            }
        });
        btnDuplicatePolicy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                duplicateModelBodyItemPolicy();
            }
        });
        btnRemPolicy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                removeModelPolicy();
            }
        });
        btnSetBodyItemPolicy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setPolicyToBodyItem();
            }
        });
        btnRemBodyItemPolicy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                removePolicyFromBodyItem();
            }
        });

        listPolicies.addListSelectionListener( new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                processPoliciesListSelection();
            }
        });

        listBodyItems.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                processBodyItemListSelection();
            }
        });

        btnSetPolicyName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setPolicyName();
            }
        });
        btnEditPolicy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                editPolicySource();
            }
        });
        tfPolicyName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setPolicyName();
            }

        });
    }

    private void closeDialog() {
        dispose();
    }

    public void execute( PhysModel model ) {
        setModel( model );
        loadGui();
        pack();
        setVisible( true );
    }

    public SaveModelRequestListener getSaveModelRequestListener() {
        return saveModelRequestListener;
    }

    public void setSaveModelRequestListener(SaveModelRequestListener saveModelRequestListener) {
        this.saveModelRequestListener = saveModelRequestListener;
    }

    public PhysModel getModel() {
        return model;
    }

    public void setModel(PhysModel model) {
        this.model = model;
        this.provider = model.getPolicyProvider();
    }


    public BodyItem getCurrentBodyItem() {
        return currentBodyItem;
    }

    public void setCurrentBodyItem(BodyItem currentBodyItem) {
        this.currentBodyItem = currentBodyItem;
        if ( currentBodyItem != null ) {
            setModel(currentBodyItem.getModel());
        }
    }


    protected void loadGui() {
        loadModelPolicyList();
        loadBodyItemsList();
    }

    protected void newModelPolicySource() {
        PhysPolicySource src = new PhysPolicySource("new Policy");
        src.setSourceText( defaultPolicyString );
        if ( !provider.addSource( src ) )
            return;
        modelPoliciesListModel.addElement( src );
        listPolicies.updateUI();
    }

    protected void duplicateModelBodyItemPolicy() {

    }

    protected void removeModelPolicy() {
        //TODO: add Yes/No dialog
        PhysPolicySource src = (PhysPolicySource) listPolicies.getSelectedValue();
        PhysPolicy policy = provider.findPolicy( src );
        if ( provider.removeSource( src ) ) {
            modelPoliciesListModel.removeElement( src );
            listPolicies.updateUI();
        }
        removePolicyFromBodyItem( policy );
    }

    protected void setPolicyToBodyItem() {
        BodyItem bodyItem = (BodyItem) listBodyItems.getSelectedValue();
        if ( bodyItem == null )
            return;
        PhysPolicySource src = (PhysPolicySource) listPolicies.getSelectedValue();
        if ( src == null )
            return;
        PhysPolicy policy = provider.findPolicy( src );
        if ( policy == null )
            return;
        bodyItem.setOnCollisionPolicy( policy );
        processBodyItemListSelection();
    }

    protected void removePolicyFromBodyItem() {
        //TODO: add yes/no dialog
        BodyItem bi = (BodyItem) listBodyItems.getSelectedValue();
        if ( bi.getOnCollisionPolicySlot() == null )
            return;
        bi.setOnCollisionPolicy( null );
        processBodyItemListSelection();
    }

    protected void loadModelPolicyList( ) {
        modelPoliciesListModel.clear();
        for ( PhysPolicySource src : provider.getSourcesArray() )
            modelPoliciesListModel.addElement( src );
    }

    protected void loadBodyItemsList() {
        modelBodyItemListModel.clear();
        BiScContainer scCont = model.getScBodyItems();
        for ( Integer id : scCont.getIdsSet() ) {
            BiScSet bset = (BiScSet) scCont.getContent( id );
            for ( BodyItem bi : bset.getBodyItems() ) {
                modelBodyItemListModel.addElement( bi );
            }
        }
    }

    protected void removePolicyFromBodyItem( PhysPolicy policy ) {
        BiScContainer scCont = model.getScBodyItems();
        for ( Integer id : scCont.getIdsSet() ) {
            BiScSet bset = (BiScSet) scCont.getContent( id );
            for ( BodyItem bi : bset.getBodyItems() ) {
                if ( bi.getOnCollisionPolicySlot() == null )
                    continue;
                if ( bi.getOnCollisionPolicySlot().getPolicy() == policy )
                    bi.setOnCollisionPolicy( null );
            }
        }
        processBodyItemListSelection();
    }

    protected void processPoliciesListSelection() {
        PhysPolicySource p = (PhysPolicySource) listPolicies.getSelectedValue();
        if ( p == null )
            return;
        tfPolicyName.setText( p.getName() );
    }

    protected void processBodyItemListSelection() {
        BodyItem bodyItem = (BodyItem) listBodyItems.getSelectedValue();
        if ( bodyItem == null ) {
            lblBodyItemOnCollisionPolicy.setText("none");
            return;
        }
        PhysPolicySlot slot = bodyItem.getOnCollisionPolicySlot();
        if ( slot == null || slot.getPolicy() == null ) {
            lblBodyItemOnCollisionPolicy.setText("none");
            return;
        }
        lblBodyItemOnCollisionPolicy.setText( slot.getPolicy().toString() );
        listPolicies.setSelectedValue(slot.getPolicy().getSource(), true );
    }

    protected void setPolicyName() {
        PhysPolicySource p = (PhysPolicySource) listPolicies.getSelectedValue();
        if ( p== null )
            return;
        p.setName( tfPolicyName.getText() );
        listPolicies.updateUI();
    }

    protected void editPolicySource() {
        PhysPolicySource src = (PhysPolicySource) listPolicies.getSelectedValue();
        dlgSourceEditor.execute( src, provider );
    }

}
