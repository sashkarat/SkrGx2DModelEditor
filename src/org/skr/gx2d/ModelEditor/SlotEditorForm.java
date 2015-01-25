package org.skr.gx2d.ModelEditor;

import org.skr.SkrScript.Def;
import org.skr.gx2d.ModelEditor.gdx.screens.EditorScreen;
import org.skr.gx2d.model.Model;
import org.skr.gx2d.node.Node;
import org.skr.gx2d.physnodes.BodyHandler;
import org.skr.gx2d.script.NodeScript;
import org.skr.gx2d.script.NodeScriptEE;
import org.skr.gx2d.script.NodeSlot;
import org.skr.gx2d.script.NodeSlotArray;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by rat on 26.12.14.
 */
public class SlotEditorForm {
    public static class SlotArraysItem {
        NodeSlotArray slotArray;
        String tagName;

        public SlotArraysItem(NodeSlotArray slotArray, String tagName) {
            this.slotArray = slotArray;
            this.tagName = tagName;
        }

        @Override
        public String toString() {
            return tagName + "[" + slotArray.getSlots().size + "]";
        }
    }

    public static class SlotArrayListModel extends DefaultListModel<NodeSlot> {
        SlotArraysItem item;

        public void setItem( SlotArraysItem item ) {
            this.item = item;
            clear();
            if ( item == null )
                return;
            for(NodeSlot slot : item.slotArray.getSlots() )
                addElement( slot );
        }

        public void addSlot( NodeSlot slot ) {
            if ( item == null )
                return;
            item.slotArray.getSlots().add( slot );
            addElement( slot );
        }

        public void remSlot( NodeSlot slot ) {
            if ( item == null )
                return;
            if ( ! removeElement( slot ) )
                return;
            item.slotArray.getSlots().removeValue( slot, true );
        }

    }

    private JPanel panelSlotEditorForm;
    private JList listSlotArrayItems;
    private JList listSlotArray;
    private JButton btnAddSlot;
    private JPanel btnAddScrpt;
    private JButton btnRemSlot;
    private JButton btnChangeSlot;

    MainGui mainGui;
    Node slotOwner;
    byte slotOwnerDts;

    DefaultListModel< SlotArraysItem > slotArraysItemDefaultListModel = new DefaultListModel<SlotArraysItem>();
    SlotArrayListModel slotArrayListModel = new SlotArrayListModel();

    public SlotEditorForm() {

        listSlotArrayItems.setModel(slotArraysItemDefaultListModel);
        listSlotArray.setModel(slotArrayListModel);


        listSlotArrayItems.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                onListSlotArrayItemSelection();
            }
        });

        btnAddSlot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                addSlotToSlotArray();
            }
        });
        btnRemSlot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                remSlotFromSlotArray();
            }
        });
        btnChangeSlot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                changeSlot();
            }

        });
    }

    public void setMainGui(MainGui mainGui) {
        this.mainGui = mainGui;
    }

    public void setModelObject(Object obj, EditorScreen.ModelObjectType mot) {

        int idx = listSlotArrayItems.getSelectedIndex();

        listSlotArray.clearSelection();
        listSlotArrayItems.clearSelection();

        slotArraysItemDefaultListModel.clear();
        slotArrayListModel.clear();

        slotOwner = null;
        slotOwnerDts = Def.DTS_NULL;

        switch ( mot ) {

            case OT_None:
                break;
            case OT_Model:
                Model model = (Model) obj;
                slotOwner = model;
                slotOwnerDts = NodeScriptEE.DTS_MODEL;
                break;
            case OT_BodyHandler:
                BodyHandler bh = (BodyHandler) obj;
                /*
                //todo: restore this
                slotArraysItemDefaultListModel.addElement( new SlotArraysItem( bh.getOnCollisionSlots(),
                        "OnCollisionSlots") );
                */
                slotOwner = bh;
                slotOwnerDts = NodeScriptEE.DTS_BODY_HANDLER;
                break;
            case OT_Sprite:
                break;
            case OT_FixtureSet:
                break;
            case OT_JointItem:
                break;
        }

        if ( idx < 0 || idx >= slotArraysItemDefaultListModel.getSize() )
            return;
        listSlotArrayItems.setSelectedIndex( idx );
    }

    protected void onListSlotArrayItemSelection() {
        SlotArraysItem item = (SlotArraysItem) listSlotArrayItems.getSelectedValue();
        listSlotArray.clearSelection();
        slotArrayListModel.setItem( item );
    }


    private void addSlotToSlotArray() {

        SlotArraysItem item = (SlotArraysItem) listSlotArrayItems.getSelectedValue();
        if ( item == null )
            return;
        /* todo: restore this
        MainGui.getDlgScripts().execute(mainGui.getModel(), null);
        NodeScript script = MainGui.getDlgScripts().getSelectedScript();

        if ( script == null )
            return;

        NodeSlot slot = new NodeSlot( slotOwner, slotOwnerDts );
        slot.setScript( script );

        slotArrayListModel.addSlot( slot );
        mainGui.makeHistorySnapshot();
        */
    }


    private void remSlotFromSlotArray() {
        NodeSlot slot = (NodeSlot) listSlotArray.getSelectedValue();
        if ( slot == null )
            return ;
        slotArrayListModel.remSlot( slot );
        mainGui.makeHistorySnapshot();
    }


    private void changeSlot() {
        NodeSlot slot = (NodeSlot) listSlotArray.getSelectedValue();
        if ( slot == null )
            return;
        /* todo: restore this
        MainGui.getDlgScripts().execute(mainGui.getModel(), slot.getPhysScript() );
        NodeScript script = MainGui.getDlgScripts().getSelectedScript();

        if ( script == null )
            return;
        slot.setScript( script );
        listSlotArray.updateUI();
        mainGui.makeHistorySnapshot();
        */
    }
}
