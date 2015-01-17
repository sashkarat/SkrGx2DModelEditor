package org.skr.PhysModelEditor;

import org.skr.PhysModelEditor.gdx.editor.screens.EditorScreen;
import org.skr.SkrScript.Def;
import org.skr.gdx.physmodel.PhysModel;
import org.skr.gdx.physmodel.bodyitem.BodyItem;
import org.skr.gdx.script.*;

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
        PhysSlotArray slotArray;
        String tagName;

        public SlotArraysItem(PhysSlotArray slotArray, String tagName) {
            this.slotArray = slotArray;
            this.tagName = tagName;
        }

        @Override
        public String toString() {
            return tagName + "[" + slotArray.getSlots().size + "]";
        }
    }

    public static class SlotArrayListModel extends DefaultListModel<PhysSlot > {
        SlotArraysItem item;

        public void setItem( SlotArraysItem item ) {
            this.item = item;
            clear();
            if ( item == null )
                return;
            for(PhysSlot slot : item.slotArray.getSlots() )
                addElement( slot );
        }

        public void addSlot( PhysSlot slot ) {
            if ( item == null )
                return;
            item.slotArray.getSlots().add( slot );
            addElement( slot );
        }

        public void remSlot( PhysSlot slot ) {
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
    Object slotOwner;
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
                PhysModel model = (PhysModel) obj;
                slotOwner = model;
                slotOwnerDts = PhysScriptEE.DTS_MODEL;
                break;
            case OT_BodyItem:
                BodyItem bi = (BodyItem) obj;
                slotArraysItemDefaultListModel.addElement( new SlotArraysItem(bi.getOnCollisionSlots(),
                        "OnCollisionSlots") );
                slotOwner = bi;
                slotOwnerDts = PhysScriptEE.DTS_BODYITEM;
                break;
            case OT_Aag:
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

        MainGui.getDlgScripts().execute(mainGui.getModel(), null);
        PhysScript script = MainGui.getDlgScripts().getSelectedScript();

        if ( script == null )
            return;

        PhysSlot slot = new PhysSlot( slotOwner, slotOwnerDts );
        slot.setScript( script );

        slotArrayListModel.addSlot( slot );
        mainGui.makeHistorySnapshot();
    }


    private void remSlotFromSlotArray() {
        PhysSlot slot = (PhysSlot) listSlotArray.getSelectedValue();
        if ( slot == null )
            return ;
        slotArrayListModel.remSlot( slot );
        mainGui.makeHistorySnapshot();
    }


    private void changeSlot() {
        PhysSlot slot = (PhysSlot) listSlotArray.getSelectedValue();
        if ( slot == null )
            return;
        MainGui.getDlgScripts().execute(mainGui.getModel(), slot.getPhysScript() );
        PhysScript script = MainGui.getDlgScripts().getSelectedScript();
        if ( script == null )
            return;
        slot.setScript( script );
        listSlotArray.updateUI();
        mainGui.makeHistorySnapshot();
    }
}
