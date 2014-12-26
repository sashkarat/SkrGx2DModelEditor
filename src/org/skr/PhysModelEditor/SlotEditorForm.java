package org.skr.PhysModelEditor;

import org.skr.PhysModelEditor.gdx.editor.screens.EditorScreen;
import org.skr.SkrScript.Slot;
import org.skr.gdx.physmodel.PhysModel;
import org.skr.gdx.physmodel.bodyitem.BodyItem;
import org.skr.gdx.script.PhysScriptSource;
import org.skr.gdx.script.PhysSlot;
import org.skr.gdx.script.PhysSlotArray;

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

    private JPanel panelSlotEditorForm;
    private JList listSlotArrayItems;
    private JList listSlotArray;
    private JButton btnAddScript;
    private JPanel btnAddScrpt;

    MainGui mainGui;
    Object slotOwner;

    DefaultListModel< SlotArraysItem > slotArraysItemDefaultListModel = new DefaultListModel<SlotArraysItem>();
    DefaultListModel< Slot > slotArrayDefaultListModel = new DefaultListModel<Slot>();


    public SlotEditorForm() {
        listSlotArrayItems.setModel(slotArraysItemDefaultListModel);
        listSlotArray.setModel(slotArrayDefaultListModel);


        listSlotArrayItems.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                onListSlotArraysSelection();
            }
        });

        btnAddScript.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                addScriptToSlotArray();
            }
        });
    }

    public void setMainGui(MainGui mainGui) {
        this.mainGui = mainGui;
    }

    public void setModelObject(Object obj, EditorScreen.ModelObjectType mot) {

        listSlotArray.clearSelection();
        listSlotArrayItems.clearSelection();

        slotArraysItemDefaultListModel.clear();
        slotArrayDefaultListModel.clear();

        switch ( mot ) {

            case OT_None:
                break;
            case OT_Model:
                PhysModel model = (PhysModel) obj;
                break;
            case OT_BodyItem:
                BodyItem bi = (BodyItem) obj;
                slotArraysItemDefaultListModel.addElement( new SlotArraysItem(bi.getOnCollisionSlots(),
                        "OnCollisionSlots") );
                break;
            case OT_Aag:
                break;
            case OT_FixtureSet:
                break;
            case OT_JointItem:
                break;
        }
    }

    protected void onListSlotArraysSelection() {
        SlotArraysItem item = (SlotArraysItem) listSlotArrayItems.getSelectedValue();
        listSlotArray.clearSelection();
        slotArrayDefaultListModel.clear();
        if ( item == null )
            return;

        for(PhysSlot slot : item.slotArray.getSlots() )
            slotArrayDefaultListModel.addElement( slot );
    }

    private void addScriptToSlotArray() {
        MainGui.getDlgScripts().execute(mainGui.getModel());
        PhysScriptSource src = MainGui.getDlgScripts().getSelectedScriptSource();
        if ( src == null )
            return;

        //todo: dig here
//        PhysSlot slot = new Slo
    }
}
