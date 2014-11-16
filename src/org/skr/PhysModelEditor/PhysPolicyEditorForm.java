package org.skr.PhysModelEditor;

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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by rat on 02.11.14.
 */
public class PhysPolicyEditorForm {
    private JPanel physPolicyEditorPanel;
    private JTabbedPane tpEditors;
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
    private JPanel panelPolicyEditor;
    private JButton btnExport;
    private JButton btnImport;
    private JList listBodyItems;
    private JEditorPane epSource;
    private JLabel lblPolicySource;
    private JButton btnSavePolicyCode;
    private JLabel lblBodyItemOnCollisionPolicy;
    private JCheckBox chbSaveModel;
    private JCheckBox chbDisplayHelp;

    PhysModel model;
    PhysPolicyProvider provider;
    BodyItem currentBodyItem;
    PhysPolicySource currentSource = null;

    DefaultListModel<PhysPolicySource> modelPoliciesListModel = new DefaultListModel<PhysPolicySource>();
    DefaultListModel<BodyItem> modelBodyItemListModel = new DefaultListModel<BodyItem>();

    public interface SaveModelRequestListener {
        public void saveModel();
    }

    SaveModelRequestListener saveModelRequestListener;

    public PhysPolicyEditorForm() {

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
                removeModelBodyItemPolicy();
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
                editBodyItemPolicy();
            }
        });
        btnSavePolicyCode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                saveCurrentSourceText();
            }
        });
        chbDisplayHelp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                displayHelpText();
            }
        });
    }

    protected void displayHelpText() {
        //TODO: implement something like a help
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

    public void loadGui() {
        tpEditors.setSelectedComponent( panelPolicies );
        currentSource = null;
        epSource.setText( "" );
        lblPolicySource.setText( "NONE" );
        loadModelPolicyList();
        loadBodyItemsList();
    }

    protected void newModelPolicySource() {
        PhysPolicySource src = new PhysPolicySource("new Policy");
        //TODO: fill ip default policy text
        if ( !provider.addSource( src ) )
            return;
        modelPoliciesListModel.addElement( src );
        listPolicies.updateUI();
    }

    protected void duplicateModelBodyItemPolicy() {

    }

    protected void removeModelBodyItemPolicy() {
        //TODO: add Yes/No dialog
        PhysPolicySource src = (PhysPolicySource) listPolicies.getSelectedValue();
        PhysPolicy policy = provider.findPolicy( src );
        if ( provider.removeSource( src ) ) {
            modelPoliciesListModel.removeElement( src );
            listPolicies.updateUI();
        }
        //TODO: check boyItems for removed policy
    }

    protected void setPolicyToBodyItem() {
        BodyItem bodyItem = (BodyItem) listBodyItems.getSelectedValue();
        if ( bodyItem == null )
            return;
        PhysPolicySource ps = (PhysPolicySource) listPolicies.getSelectedValue();
        if ( ps == null )
            return;
    }

    protected void removePolicyFromBodyItem() {

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

    protected void processPoliciesListSelection() {
        PhysPolicySource p = (PhysPolicySource) listPolicies.getSelectedValue();
        if ( p == null )
            return;
        tfPolicyName.setText( p.getName() );
    }

    protected void processBodyItemListSelection() {
        BodyItem bodyItem = (BodyItem) listBodyItems.getSelectedValue();
        PhysPolicySlot slot = bodyItem.getOnCollisionPolicySlot();
        if ( slot == null || slot.getPolicy() == null ) {
            lblBodyItemOnCollisionPolicy.setText("none");
            return;
        }

        lblBodyItemOnCollisionPolicy.setText( slot.getPolicy().toString() );

    }

    protected void setPolicyName() {
        PhysPolicySource p = (PhysPolicySource) listPolicies.getSelectedValue();
        if ( p== null )
            return;
        p.setName( tfPolicyName.getText() );
        listPolicies.updateUI();
    }

    protected void editBodyItemPolicy() {
        currentSource = (PhysPolicySource) listPolicies.getSelectedValue();
        switchToEditPolicy();
    }

    protected void switchToEditPolicy() {
        loadCurrentPolicyToEditor();
        tpEditors.setSelectedComponent( panelPolicyEditor );
    }

    protected void loadCurrentPolicyToEditor() {
        if ( currentSource != null ) {
            lblPolicySource.setText(currentSource.toString());
            epSource.setText( currentSource.getSourceText() );
        } else {
            lblPolicySource.setText( "NONE" );
        }
    }

    private void saveCurrentSourceText() {
        if ( currentSource != null ) {
            currentSource.setSourceText( epSource.getText() );
//            PhysPolicyBuilder.build( null, currentSource );
        }

        if ( saveModelRequestListener != null && chbSaveModel.isSelected() )
            saveModelRequestListener.saveModel();
    }

}
