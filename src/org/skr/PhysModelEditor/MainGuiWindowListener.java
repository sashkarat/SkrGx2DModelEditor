package org.skr.PhysModelEditor;

import com.badlogic.gdx.Gdx;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Created by rat on 31.05.14.
 */

public class MainGuiWindowListener implements WindowListener {

    MainGui gui;

    public MainGuiWindowListener(MainGui gui) {
        this.gui = gui;
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        if ( ! gui.onModelClosing() )
            return;
        e.getWindow().dispose();
    }

    @Override
    public void windowClosed(WindowEvent e) {
        Gdx.app.exit();
        gui.getSnapshotTimer().stop();
        ApplicationSettings.save();
        System.exit(0);
    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

}
