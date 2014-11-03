package org.skr.PhysModelEditor;

import com.badlogic.gdx.utils.Array;
import org.skr.gdx.physmodel.PhysModel;
import org.skr.gdx.physmodel.bodyitem.BodyItem;
import org.skr.gdx.policy.PhysPolicy;

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
    private JComboBox comboPolicyLists;
    private JList listPolicies;
    private JButton btnNewPolicy;
    private JButton btnDuplicatePolicy;
    private JButton btnRemPolicy;
    private JComboBox comboBodyItems;
    private JList listBodyItemPolicies;
    private JButton btnAddPolicyToBodyItem;
    private JButton btnRemPolicyFromBodyItem;
    private JTextField tfPolicyName;
    private JButton btnSetPolicyName;
    private JButton btnEditPolicy;
    private JPanel panelPolicyEditor;
    private JButton btnSave;
    private JButton btnExport;
    private JButton btnImport;

    PhysModel model;
    BodyItem currentBodyItem;

    DefaultListModel<PhysPolicy> modelPoliciesListModel = new DefaultListModel<PhysPolicy>();


    public static enum PolicyListType {
        PLT_onCollisionPre,
        PLT_BodyItems,
        PLT_onCollisionPost
    }

    public PhysPolicyEditorForm() {

        for ( PolicyListType plt : PolicyListType.values() )
            comboPolicyLists.addItem( plt );
        comboPolicyLists.setSelectedItem( PolicyListType.PLT_onCollisionPre );

        listPolicies.setModel( modelPoliciesListModel );

        btnNewPolicy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                newModelPolicy();
            }
        });
        btnDuplicatePolicy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                duplicateModelPolicy();
            }
        });
        btnRemPolicy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                removeModelPolicy();
            }
        });
        btnAddPolicyToBodyItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                addPolicyToBodyItem();
            }
        });
        btnRemPolicyFromBodyItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                removePolicyFromBodyItem();
            }
        });
        comboPolicyLists.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                loadModelPolicyList();
            }
        });

        listPolicies.addListSelectionListener( new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                processPoliciesListSelection();
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
                editPolicy();
            }
        });
    }

    public PhysModel getModel() {
        return model;
    }

    public void setModel(PhysModel model) {
        this.model = model;
    }

    public BodyItem getCurrentBodyItem() {
        return currentBodyItem;
    }

    public void setCurrentBodyItem(BodyItem currentBodyItem) {
        this.currentBodyItem = currentBodyItem;
        if ( currentBodyItem != null ) {
            this.model = currentBodyItem.getModel();
        }
    }

    public void loadGui() {
        tpEditors.setSelectedComponent( panelPolicies );
        loadModelPolicyList();
    }

    protected void newModelPolicy() {
        PhysPolicy p = new PhysPolicy( "newPolicy" );
        getSelectedModelPolicyList().add(p);
        modelPoliciesListModel.addElement(p);
    }

    protected void duplicateModelPolicy() {

    }

    protected void removeModelPolicy() {
        //TODO: add Yes/No dialog
        PhysPolicy p = (PhysPolicy) listPolicies.getSelectedValue();
        if ( getSelectedModelPolicyList().removeValue( p, true ) )
            modelPoliciesListModel.removeElement( p );
        //TODO: check boyItems for removed policy
    }

    protected void addPolicyToBodyItem() {

    }

    protected void removePolicyFromBodyItem() {

    }

    Array<PhysPolicy> getSelectedModelPolicyList() {
        PolicyListType plt = (PolicyListType) comboPolicyLists.getSelectedItem();
        switch ( plt ) {
            case PLT_onCollisionPre:
                return model.getOnCollisionCommonPoliciesPre();
            case PLT_BodyItems:
                return model.getBodyItemPolicies();
            case PLT_onCollisionPost:
                return model.getOnCollisionCommonPoliciesPost();
        }
        return null;
    }

    protected void loadModelPolicyList() {
        loadModelPolicyList( getSelectedModelPolicyList() );
    }



    protected void loadModelPolicyList( Array<PhysPolicy> policies ) {
        modelPoliciesListModel.clear();
        for ( PhysPolicy p : policies )
            modelPoliciesListModel.addElement( p );
    }

    protected void processPoliciesListSelection() {
        PhysPolicy p = (PhysPolicy) listPolicies.getSelectedValue();
        if ( p == null )
            return;
        tfPolicyName.setText( p.getName() );
    }

    protected void setPolicyName() {
        PhysPolicy p = (PhysPolicy) listPolicies.getSelectedValue();
        if ( p== null )
            return;
        p.setName( tfPolicyName.getText() );
        listPolicies.updateUI();
    }

    protected void editPolicy() {
        PhysPolicy p = (PhysPolicy) listPolicies.getSelectedValue();
        tpEditors.setSelectedComponent( panelPolicyEditor );
    }
}
