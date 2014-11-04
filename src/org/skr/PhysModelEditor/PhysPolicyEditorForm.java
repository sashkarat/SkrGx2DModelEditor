package org.skr.PhysModelEditor;

import org.skr.gdx.physmodel.PhysModel;
import org.skr.gdx.physmodel.bodyitem.BodyItem;
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
    private JButton btnEditOnCollisionPolicyPre;
    private JButton btnEditOnCollisionPolicyPost;
    private JList listBodyItems;
    private JLabel lblBodyItemPolicy;
    private JEditorPane epSource;
    private JLabel lblPolicySource;
    private JButton btnSavePolicyCode;

    PhysModel model;
    BodyItem currentBodyItem;
    PhysPolicySource currentSource = null;

    DefaultListModel<PhysPolicySource> modelPoliciesListModel = new DefaultListModel<PhysPolicySource>();

    public PhysPolicyEditorForm() {

        listPolicies.setModel( modelPoliciesListModel );

        btnNewPolicy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                newModelBodyItemPolicy();
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
        btnEditOnCollisionPolicyPre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                editOnCollisionPolicyPre();
            }
        });
        btnEditOnCollisionPolicyPost.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                editOnCollisionPolicyPost();
            }
        });
        btnSavePolicyCode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                saveCurrentSourceText();
            }
        });
    }

    public PhysModel getModel() {
        return model;
    }

    public void setModel(PhysModel model) {
        this.model = model;
    }

    protected PhysModel.ModelPolicySources getPolicySources() {
        if ( model == null )
            return null;
        PhysModel.ModelPolicySources sources = model.getModelPolicySources();
        if ( sources == null ) {
            sources = new PhysModel.ModelPolicySources();
            model.setModelPolicySources( sources );
        }
        return sources;
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
        currentSource = null;
        epSource.setText( "" );
        lblPolicySource.setText( "NONE" );
        loadModelPolicyList();
    }

    protected void newModelBodyItemPolicy() {
        PhysPolicySource policySource = new PhysPolicySource("new Policy");
        getPolicySources().getBodyItemPolicies().add( policySource );
        modelPoliciesListModel.addElement( policySource );
        listPolicies.updateUI();
    }

    protected void duplicateModelBodyItemPolicy() {

    }

    protected void removeModelBodyItemPolicy() {
        //TODO: add Yes/No dialog
        PhysPolicySource p = (PhysPolicySource) listPolicies.getSelectedValue();
        if ( getPolicySources().getBodyItemPolicies().removeValue( p, true ) ) {
            modelPoliciesListModel.removeElement( p );
            listPolicies.updateUI();
        }
        //TODO: check boyItems for removed policy
    }

    protected void setPolicyToBodyItem() {

    }

    protected void removePolicyFromBodyItem() {

    }

    protected void editOnCollisionPolicyPre() {
        currentSource = getPolicySources().getOnCollisionPolicySrcPre();
        switchToEditPolicy();
    }

    protected void editOnCollisionPolicyPost() {
        currentSource = getPolicySources().getOnCollisionPolicySrcPost();
        switchToEditPolicy();
    }

    protected void loadModelPolicyList( ) {
        modelPoliciesListModel.clear();
        for ( PhysPolicySource p : getPolicySources().getBodyItemPolicies() )
            modelPoliciesListModel.addElement( p );
    }

    protected void processPoliciesListSelection() {
        PhysPolicySource p = (PhysPolicySource) listPolicies.getSelectedValue();
        if ( p == null )
            return;
        tfPolicyName.setText( p.getName() );
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
        }
    }
}
