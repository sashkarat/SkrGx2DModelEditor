package org.skr.PhysModelEditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.joints.GearJoint;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import org.skr.PhysModelEditor.gdx.editor.SkrGdxAppPhysModelEditor;
import org.skr.PhysModelEditor.gdx.editor.controllers.ShapeControllers.CircleShapeController;
import org.skr.gdx.Environment;
import org.skr.gdx.SkrGdxApplication;
import org.skr.gdx.PhysWorld;
import org.skr.PhysModelEditor.gdx.editor.controllers.*;
import org.skr.gdx.editor.Controller;
import org.skr.PhysModelEditor.gdx.editor.screens.EditorScreen;
import org.skr.gdx.physmodel.PhysModel;
import org.skr.gdx.physmodel.PhysModelDescription;
import org.skr.gdx.physmodel.ShapeDescription;
import org.skr.gdx.physmodel.bodyitem.BiScSet;
import org.skr.gdx.physmodel.bodyitem.BodyItem;
import org.skr.gdx.physmodel.bodyitem.fixtureset.FixtureSet;
import org.skr.gdx.physmodel.jointitem.JointItem;
import org.skr.gdx.physmodel.jointitem.JointItemDescription;
import org.skr.gdx.physmodel.jointitem.JointItemFactory;
import org.skr.gdx.utils.PhysModelProcessing;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * Created by rat on 30.05.14.
 */



public class MainGui extends JFrame {


    private JPanel rootPanel;
    private JPanel gdxPanel;
    private JTree treePhysModel;
    private JTabbedPane tabbedPaneEditors;
    private JSplitPane mainSplitPanel;
    private JTable tableProperties;
    private JPanel panelProperties;
    private JButton btnAddNode;
    private JButton btnRemNode;
    private JPanel panelShapeEditor;
    private JPanel panelJointCreator;
    private JButton btnAddShape;
    private JTextField tfNewShapePosX;
    private JTextField tfNewShapePosY;
    private JTextField tfControlPointX;
    private JTextField tfControlPointY;
    private JButton btnSetControlPointPosition;
    private JButton btnDeleteShape;
    private JCheckBox chbLooped;
    private JCheckBox chbAutoTessellate;
    private JPanel panelControllerPosition;
    private JPanel panelShapes;
    private JPanel panelShapeOptions;
    private JButton btnUpdateFixtures;
    private JPanel panelRadius;
    private JTextField tfRadius;
    private JButton btnSetRadius;
    private JButton btnTessellatePolygon;
    private JCheckBox chbSimulation;
    private JPanel panelModelGui;
    private JPanel panelSimulationControls;
    private JCheckBox chbPauseSimulation;
    private JButton btnSimulationStep;
    private JButton btnSimulationRestart;
    private JCheckBox chbDisplayGrid;
    private JCheckBox chbDebugRender;
    private JButton btnDuplicate;
    private JPanel panelBodyItemEditor;
    private JTextField tfMassCenterWorldX;
    private JTextField tfMassCenterWorldY;
    private JButton btnSetMassCenter;
    private JButton btnResetMassData;
    private JCheckBox chbEnableMassCorrection;
    private JComboBox<EditorScreen.SelectionMode> comboSelectionMode;
    private JPanel panelSelectionMode;
    private JPanel panelTools;
    private JTextField tfMirrorAxisX;
    private JTextField tfMirrorAxisY;
    private JButton btnMirrorModel;
    private JComboBox<PhysModelProcessing.MirrorDirection> comboMirrorDirection;
    private JLabel lblTextureAtlasFile;
    private JSpinner spinToolDupNumber;
    private JTextField tfToolDupXOffset;
    private JTextField tfToolDupYOffset;
    private JTextField tfToolDupRotation;
    private JButton btnToolDup;
    private JCheckBox chbBiBBox;
    private JointEditorFrom formJointEditor;
    private JButton btnUpdateJointItem;
    private JButton btnCpyProperties;
    private JButton btnNodeProperties;

    private SkrGdxAppPhysModelEditor gApp;
    private String currentModelFileName = "";
    private PhysModel model = null;



    private PhysModelJTreeNode jointsGroupNode;
    private PhysModelJTreeNode bodiesGroupNode;

    private PhysModelStructureGuiProcessing modelStructureGuiProcessing;
    private EditorScreen editorScreen;

    private String textureAtlasFilePath = "";

    private Timer snapshotTimer;

    private boolean modelChanged = false;

    private static DialogModelPolicies dlgPolicy;

    MainGui() {

        dlgPolicy = new DialogModelPolicies( );

        chbDebugRender.setSelected( Environment.debugRender );
        chbBiBBox.setSelected( Environment.drawBodyItemBBox );

        spinToolDupNumber.setModel( new SpinnerNumberModel(1,0,9999,1) );
        snapshotTimer = new Timer( 2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSnapshotTimer();
            }
        });

        for ( EditorScreen.SelectionMode m : EditorScreen.SelectionMode.values() )
            comboSelectionMode.addItem( m );

        for (PhysModelProcessing.MirrorDirection d : PhysModelProcessing.MirrorDirection.values() )
            comboMirrorDirection.addItem( d );

        setGuiElementEnable( panelSimulationControls, false);

        gApp = new SkrGdxAppPhysModelEditor();
        final LwjglAWTCanvas gdxCanvas = new LwjglAWTCanvas( gApp );
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setContentPane(rootPanel);
        gdxPanel.add(gdxCanvas.getCanvas(), BorderLayout.CENTER);

        pack();
        setSize(1280, 800);


        modelStructureGuiProcessing = new PhysModelStructureGuiProcessing( this );


        snapshotTimer.start();


        MainGuiWindowListener guiWindowListener = new MainGuiWindowListener( this );
        addWindowListener( guiWindowListener );

        gdxPanel.requestFocusInWindow();

        ApplicationSettings.load();
        uploadGuiFromSettings();

        gApp.setChangeAtlasListener( new SkrGdxApplication.ChangeAtlasListener() {
            @Override
            public void atlasUpdated(TextureAtlas atlas) {
                onAtlasLoaded();
            }
        });


        setGuiElementEnable(mainSplitPanel, false);
        tabbedPaneEditors.removeAll();


        setupGdxApp();
        setupHotKeyActions();

        uploadTextureAtlas();

        modelToGui();

        createMenu();

        // ====================================================================================

        btnAddNode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modelStructureGuiProcessing.createNode();
            }
        });
        btnRemNode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modelStructureGuiProcessing.removeNode();
            }
        });
        btnAddShape.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewShape();
            }
        });
        btnDeleteShape.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ShapeController shapeController = SkrGdxAppPhysModelEditor.get().getEditorScreen().getCurrentShapeController();
                if ( shapeController == null )
                    return;
                deleteShape( shapeController );
            }
        });
        btnSetControlPointPosition.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ShapeController shapeController = SkrGdxAppPhysModelEditor.get().getEditorScreen().getCurrentShapeController();
                if ( shapeController == null )
                    return;
                setControlPointPosition( shapeController );
            }
        });
        chbLooped.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ShapeController shapeController = SkrGdxAppPhysModelEditor.get().getEditorScreen().getCurrentShapeController();
                setLooped( chbLooped.isSelected(), shapeController );
            }
        });
        chbAutoTessellate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ShapeController shapeController = SkrGdxAppPhysModelEditor.get().getEditorScreen().getCurrentShapeController();
                setAutoTessellate( chbAutoTessellate.isSelected(), shapeController );
            }
        });
        btnUpdateFixtures.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ShapeController shapeController = SkrGdxAppPhysModelEditor.get().getEditorScreen().getCurrentShapeController();
                if ( shapeController == null )
                    return;
                updateFixtures( shapeController );
            }
        });
        btnSetRadius.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ShapeController shapeController = SkrGdxAppPhysModelEditor.get().getEditorScreen().getCurrentShapeController();
                if ( shapeController == null )
                    return;
                setRadius( shapeController );
            }
        });
        btnTessellatePolygon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ShapeController shapeController = SkrGdxAppPhysModelEditor.get().getEditorScreen().getCurrentShapeController();
                tessellatePolygon( shapeController );
            }
        });

        chbSimulation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleScreen();
            }
        });

        btnSimulationStep.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doSimulationStep();
            }
        });
        btnSimulationRestart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartSimulation();
            }
        });
        chbPauseSimulation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleSimulationPause();
            }
        });
        chbDisplayGrid.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleGrid();
            }
        });
        chbDebugRender.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleDebugRender();
            }
        });

        chbBiBBox.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleDrawBiBBox();
            }
        });


        btnDuplicate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modelStructureGuiProcessing.duplicateNode();
            }
        });
        btnSetMassCenter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setCenterOfMass();
            }
        });
        btnResetMassData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetMassData();
            }
        });
        chbEnableMassCorrection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setMassCorrectionEnabled();
            }
        });

        comboSelectionMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeSelectionMode();
            }
        });
        btnMirrorModel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processMirroring();
            }
        });


        btnToolDup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processArrayDuplication();
            }
        });
        btnUpdateJointItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateJointItem();
            }

        });
        btnCpyProperties.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                modelStructureGuiProcessing.copyNodeProperties();
            }
        });
        btnNodeProperties.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                modelStructureGuiProcessing.pasteNodeProperties();
            }
        });
    }


    void setupGdxApp() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                editorScreen = SkrGdxAppPhysModelEditor.get().getEditorScreen();

                editorScreen.getAagController().setControlPointListener(
                        new Controller.controlPointListener() {
                            @Override
                            public void changed(Object controlledObject, Controller.ControlPoint controlPoint) {
                                actorChangedByController((Actor) controlledObject);
                            }
                        }
                );

                editorScreen.getBodyItemController().setControlPointListener(
                        new Controller.controlPointListener() {
                            @Override
                            public void changed(Object controlledObject, Controller.ControlPoint controlPoint) {
                                bodyItemChangedByController((BodyItem) controlledObject);
                            }
                        }
                );

                editorScreen.getMultiItemsController().setControlPointListener(
                        new Controller.controlPointListener() {
                            @Override
                            public void changed(Object controlledObject, Controller.ControlPoint controlPoint) {
                                bodyItemsChangedByController();
                            }
                });

                formJointEditor.setupGdxApp();

            }
        });

        ShapeController.setStaticShapeControllerListener(new ShapeController.ShapeControllerListener() {
            @Override
            public void controlPointChanged(ShapeDescription shapeDescription, Controller.ControlPoint controlPoint) {
                shapeControlPointChanged(shapeDescription, controlPoint);
            }

            @Override
            public void positionChanged(ShapeDescription shapeDescription) {
                shapePositionChanged(shapeDescription);
            }

            @Override
            public void radiusChanged(ShapeDescription shapeDescription) {
                shapeRadiusChanged(shapeDescription);
            }
        });
    }

    void createMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("Model");

        JMenuItem mnuItem = new JMenuItem("New");
        mnuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK) );
        mnuItem.addActionListener( new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newModel();
            }
        });
        menu.add( mnuItem );

        mnuItem = new JMenuItem("Load");
        mnuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK) );
        mnuItem.addActionListener( new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadModel();
            }
        });
        menu.add(mnuItem);


        mnuItem = new JMenuItem("Save");
        mnuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK) );
        mnuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveModel( false );
            }
        });
        menu.add(mnuItem);


        mnuItem = new JMenuItem("Save As ...");
        mnuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK ) );
        mnuItem.addActionListener( new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveModel( true );
            }
        });
        menu.add(mnuItem);

        menu.addSeparator();

        mnuItem = new JMenuItem("Import Body Items");
        mnuItem.addActionListener( new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                importModel();
            }
        });
        menu.add(mnuItem);

        mnuItem = new JMenuItem("Export selection");
        mnuItem.addActionListener( new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportSelection();
            }
        });
        menu.add(mnuItem);

        menu.addSeparator();

        mnuItem = new JMenuItem("Set Texture Atlas File");
        mnuItem.addActionListener( new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectTextureAtlasFile();
            }
        });
        menu.add( mnuItem );
        menu.addSeparator();
        mnuItem = new JMenuItem("Exit");
        mnuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK) );
        mnuItem.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processExitCall();
            }
        });
        menu.add(mnuItem);

        menuBar.add( menu );

        menu = new JMenu("Edit");

        mnuItem = new JMenuItem("Undo");
        mnuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK ) );
        mnuItem.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                undo();
            }
        });
        menu.add( mnuItem );

        mnuItem = new JMenuItem("Redo");
        mnuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK ) );
        mnuItem.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redo();
            }
        });
        menu.add( mnuItem );

        menu.addSeparator();

        mnuItem = new JMenuItem("Add Item ");
        mnuItem.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modelStructureGuiProcessing.createNode();
            }
        });
        menu.add( mnuItem );

        mnuItem = new JMenuItem("Duplicate Item ");
        mnuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK  ) );
        mnuItem.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modelStructureGuiProcessing.duplicateNode();
            }
        });
        menu.add( mnuItem );

        mnuItem = new JMenuItem("Remove Item ");
        mnuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0  ) );
        mnuItem.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modelStructureGuiProcessing.removeNode();
            }
        });
        menu.add( mnuItem );

        menu.addSeparator();

        mnuItem = new JMenuItem("Copy Item Properties ");
        mnuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
        mnuItem.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                modelStructureGuiProcessing.copyNodeProperties();
            }
        });
        menu.add( mnuItem );

        mnuItem = new JMenuItem("Paste Item Properties ");
        mnuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
        mnuItem.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                modelStructureGuiProcessing.pasteNodeProperties();
            }
        });
        menu.add( mnuItem );
        menuBar.add( menu );

        menu = new JMenu("Selection");
        mnuItem = new JMenuItem("Clear Selection");
        mnuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_MASK));
        mnuItem.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                modelStructureGuiProcessing.clearSelection();
            }
        });
        menu.add( mnuItem );

        JMenu subMnu = new JMenu("Convert Selection");

        mnuItem = new JMenuItem("to BodyItem Selection");
        mnuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.ALT_MASK));
        mnuItem.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                modelStructureGuiProcessing.convertSelectionToBodyItemSelection();
            }
        });
        subMnu.add( mnuItem );

        mnuItem = new JMenuItem("to FixtureSet Selection");
        mnuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.ALT_MASK));
        mnuItem.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                modelStructureGuiProcessing.convertSelectionToFixtureSetSelection();
            }
        });
        subMnu.add( mnuItem );

        mnuItem = new JMenuItem("to Aag Selection");
        mnuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.ALT_MASK));
        mnuItem.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                modelStructureGuiProcessing.convertSelectionToAagSelection();
            }
        });
        subMnu.add( mnuItem );

        mnuItem = new JMenuItem("to JointItem Selection");
        mnuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, InputEvent.ALT_MASK));
        mnuItem.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                modelStructureGuiProcessing.convertSelectionToJointItemSelection();
            }
        });
        subMnu.add( mnuItem );
        menu.add( subMnu );
        menuBar.add( menu );

        menu = new JMenu("Phys Policy");

        mnuItem = new JMenuItem("Model Policies");
        mnuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_MASK ) );
        mnuItem.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                showModelPoliciesDialog();
            }
        });
        menu.add( mnuItem );

        mnuItem = new JMenuItem("BodyItem Policies");
        mnuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_MASK ) );
        mnuItem.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                showBodyItemDialog();
            }
        });
        menu.add( mnuItem );

        menuBar.add( menu );

        setJMenuBar(menuBar);
    }

    void setupHotKeyActions() {
        rootPanel.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setSelectionMode(EditorScreen.SelectionMode.BODY_ITEM );
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_1, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        rootPanel.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setSelectionMode(EditorScreen.SelectionMode.FIXTURE_SET );
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_2, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        rootPanel.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setSelectionMode(EditorScreen.SelectionMode.AAG );
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_3, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        rootPanel.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setSelectionMode(EditorScreen.SelectionMode.DISABLED );
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_0, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }


    public JTable getTableProperties() {
        return tableProperties;
    }

    public JPanel getPanelProperties() {
        return panelProperties;
    }

    public JTree getTreePhysModel() {
        return treePhysModel;
    }

    void uploadGuiFromSettings() {
        textureAtlasFilePath = ApplicationSettings.get().getTextureAtlasFile();
        lblTextureAtlasFile.setText( "Texture Atlas File: " + textureAtlasFilePath );
    }

    public Timer getSnapshotTimer() {
        return snapshotTimer;
    }

    private final static DialogTextureAtlasSelector atlasFileSelector = new DialogTextureAtlasSelector();

    void selectTextureAtlasFile() {

        atlasFileSelector.setTfTextureAtlasFilePath( textureAtlasFilePath );
        atlasFileSelector.setTitle(" Set Texture atlas file ");

        if ( !atlasFileSelector.execute( ApplicationSettings.get().getLastDirectory() ) )
            return;

        textureAtlasFilePath = atlasFileSelector.getTextureAtlasFilePath();
        ApplicationSettings.get().setLastDirectory( atlasFileSelector.getFileDirectory() );
        uploadTextureAtlas();

    }


    void uploadTextureAtlas() {

        File fl = new File( textureAtlasFilePath );

        if ( !fl.exists() )
            return;

        Gdx.app.postRunnable( new Runnable() {
            @Override
            public void run() {
                gApp.loadAtlas( textureAtlasFilePath );
            }
        });

        ApplicationSettings.get().setTextureAtlasFile( textureAtlasFilePath );

        lblTextureAtlasFile.setText( "Texture Atlas File: " + textureAtlasFilePath );
    }

    void onAtlasLoaded() {

        if ( model != null )
            model.updateTextures( SkrGdxApplication.get().getAtlas() );
    }


    void closeModel() {

        if ( model == null )
            return;

        clearHistory();
        model.destroyPhysics();

        PhysWorld.clearPrimaryWorld();

        model = null;
    }

    void newModel() {

        if ( model != null ) {
            if ( showCloseModelYesNoDialog() != 0 )
                return;
            if ( ! onModelClosing() )
                return;
        }


        closeModel();

        model = new PhysModel( PhysWorld.getPrimaryWorld(), SkrGdxApplication.get().getAtlas()  );
        model.setName("new model");
        model.updateTextures();

        currentModelFileName = "";

        setGuiElementEnable(mainSplitPanel, true);

        modelToGui();

        modelChanged = true;
        makeHistorySnapshot();
    }


    void loadModel() {

        if ( model != null ) {
            if ( showCloseModelYesNoDialog() != 0 )
                return;
            if ( !onModelClosing() )
                return;
        }

        FileNameExtensionFilter ff = PhysModel.getFileFilter();
        final JFileChooser fch = new JFileChooser();
        int res;

        fch.setCurrentDirectory(new File(ApplicationSettings.get().getLastDirectory()));
        fch.setFileFilter(ff);


        res = fch.showOpenDialog( this );

        if ( res != JFileChooser.APPROVE_OPTION ) {
            return;
        }

        File fl = fch.getSelectedFile();

        if (!fl.exists()) {
            return;
        }

        ApplicationSettings.get().setLastDirectory(fl.getParent());

        closeModel();

        model = PhysModel.loadFromFile( Gdx.files.absolute( fl.getAbsolutePath()), SkrGdxApplication.get().getAtlas()  );

        setGuiElementEnable(mainSplitPanel, true);

        currentModelFileName = fl.getAbsolutePath();

        modelToGui();

        modelChanged = false;
        makeHistorySnapshot();

    }


    public boolean onModelClosing() {

        if ( model == null )
            return true;

        if ( modelChanged ) {
            int n = JOptionPane.showOptionDialog(null, "Do you want to save the model?", "The model is changed.",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
            if ( n == 0 ) {
                saveModel( false );
            } else if ( n == 2 ) {
                return false;
            }
        }

        return true;
    }

    public void saveModel() {
        saveModel( false );
    }


    protected void saveModel(boolean saveAs) {

        if ( model == null )
            return;


        FileNameExtensionFilter ff = PhysModel.getFileFilter();

        if ( currentModelFileName.isEmpty() || saveAs ) {

            final JFileChooser fch = new JFileChooser();
            int res;

            fch.setCurrentDirectory( new File( ApplicationSettings.get().getLastDirectory() ) );
            fch.setFileFilter( ff );

            res = fch.showSaveDialog(this);

            if ( res != JFileChooser.APPROVE_OPTION )
                return;

            File fl = fch.getSelectedFile();

            ApplicationSettings.get().setLastDirectory( fl.getParent() );

            currentModelFileName = fl.getAbsolutePath();

            if ( !currentModelFileName.toLowerCase().endsWith( "." + ff.getExtensions()[0]) ) {
                currentModelFileName += ("." + ff.getExtensions()[0]);
            }

            if ( saveAs ) {
                model.changeUuid();
            }

            setTitle("PhysModel file: " + currentModelFileName);
        }
        model.save( Gdx.files.absolute(currentModelFileName) );
        modelChanged = false;
    }



    void modelToGui() {
        if ( editorScreen == null )
            return;
        editorScreen.setModel( model );
//        loadTree();

        modelStructureGuiProcessing.setModel( model );

//        updateJointCombos();
        setTitle("PhysModel file: " + currentModelFileName);
    }


    int showCloseModelYesNoDialog() {
        return JOptionPane.showOptionDialog(this, "Do you really want to close current model?", "Close Model",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null );
    }


    public void setupEditors(Object object, EditorScreen.ModelObjectType mot ) {
        tabbedPaneEditors.removeAll();
        editorScreen.setModelObject( object, mot );

        switch ( mot ) {
            case OT_None:
                break;
            case OT_Model:
                tabbedPaneEditors.add( "Tools", panelTools );
                break;
            case OT_BodyItem:
                tabbedPaneEditors.add("Body Editor", panelBodyItemEditor);
                chbEnableMassCorrection.setSelected( false );
                tabbedPaneEditors.add( "Tools", panelTools );
                break;
            case OT_Aag:
                tabbedPaneEditors.add( "Tools", panelTools );
                break;
            case OT_FixtureSet:
                FixtureSet fs = (FixtureSet) object;
                tabbedPaneEditors.add( "Shape Editor", panelShapeEditor );
                setGuiElementEnable( panelShapeEditor, true );
                updateShapeEditorFeatures( fs );
                tabbedPaneEditors.add( "Tools", panelTools );
                setGuiElementEnable( panelTools, true);
                break;
            case OT_JointItem:
                tabbedPaneEditors.add("Joint editor", panelJointCreator );
                formJointEditor.setJointItem((JointItem) object);
                break;
        }

        setGuiElementEnable( tabbedPaneEditors, true );


    }

    private void setGuiElementEnable(Container c, boolean state) {

        Component [] cl = c.getComponents();
        for (Component aCl : cl) {
            if (aCl instanceof Container) {
                setGuiElementEnable((Container) aCl, state);
            } else {
                aCl.setEnabled(state);
            }
        }

        c.setEnabled( state );
    }
    private void bodyItemChangedByController( BodyItem bodyItem ) {
        modelStructureGuiProcessing.updatePropertiesTable();
        Vector2 center = bodyItem.getBody().getWorldCenter();
        tfMassCenterWorldX.setText("" + center.x );
        tfMassCenterWorldY.setText("" + center.y );
        modelChanged();
    }

    private void bodyItemsChangedByController() {
        modelChanged();
    }

    private void actorChangedByController(Actor actor) {
        modelStructureGuiProcessing.updatePropertiesTable();
        modelChanged();
    }




    void updateShapeEditorFeatures(FixtureSet fixtureSet) {

        chbAutoTessellate.setEnabled( false );
        btnTessellatePolygon.setEnabled( false );
        chbLooped.setEnabled( false );
        setGuiElementEnable( panelRadius, false );

        switch ( fixtureSet.getShapeType() ) {

            case Circle:
                setGuiElementEnable( panelRadius, true );
                break;
            case Edge:
                break;
            case Polygon:
                chbAutoTessellate.setEnabled( true );
                btnTessellatePolygon.setEnabled( true );
                break;
            case Chain:
                chbLooped.setEnabled( true );
                break;
        }

    }

    void addNewShape() {
        ShapeController shc = SkrGdxAppPhysModelEditor.get().getEditorScreen().getCurrentShapeController();
        if ( shc == null )
            return;

        try {
            float x = Float.valueOf(tfNewShapePosX.getText());
            float y = Float.valueOf(tfNewShapePosY.getText());

            if ( shc instanceof CircleShapeController) {
                ((CircleShapeController) shc).setDefaultRadius( Float.valueOf( tfRadius.getText() ));
            }

            shc.addNewShape(x, y);

        } catch ( NumberFormatException e ) {
            Gdx.app.log("MainGui.addNewShape()", "Exception: " + e.getMessage() );
        }

    }

    void deleteShape( ShapeController controller ) {
        controller.deleteCurrentShape();
    }

    void setControlPointPosition( ShapeController controller ) {
        try {
            float x = Float.valueOf(tfControlPointX.getText());
            float y = Float.valueOf(tfControlPointY.getText());
            controller.setControlPointPosition(x, y);
        } catch ( NumberFormatException e ) {
            Gdx.app.error("MainGui.setControlPointPosition", e.getMessage() );
        }
    }

    void setRadius( ShapeController controller ) {
        try {
            float r = Float.valueOf(tfRadius.getText());
            controller.setRadius(r);
        } catch ( NumberFormatException e ) {
            Gdx.app.error("MainGui.setRadius", e.getMessage() );
        }
    }

    void updateFixtures( ShapeController controller ) {
        controller.flush();
        modelStructureGuiProcessing.updateFixtures( controller.getFixtureSetDescription().getShapeDescriptions() );
        modelChanged = true;
        makeHistorySnapshot();
    }

    void shapeControlPointChanged( ShapeDescription shapeDescription, Controller.ControlPoint cp ) {
        float x = PhysWorld.get().toPhys( cp.getX() );
        float y = PhysWorld.get().toPhys( cp.getY() );
        tfControlPointX.setText( String.valueOf( x ) );
        tfControlPointY.setText( String.valueOf( y ) );
        tfRadius.setText( String.valueOf( shapeDescription.getRadius() ) );
    }

    void shapePositionChanged( @SuppressWarnings("UnusedParameters") ShapeDescription shapeDescription ) {
        // does nothing
    }

    void shapeRadiusChanged( ShapeDescription shapeDescription ) {
        tfRadius.setText( String.valueOf( shapeDescription.getRadius() ) );
    }

    void setLooped(boolean state, ShapeController controller) {
        controller.setLooped( state );
    }

    void setAutoTessellate( boolean state, ShapeController controller ) {
         controller.setAutoTessellate( state );
    }

    void tessellatePolygon( ShapeController controller ) {
        controller.tessellatePolygon();
    }


    Array< JointItem > findMatchingGearJoints( JointItem jointItem ) {
        Array< JointItem > jointsList = new Array<JointItem>();
        if ( jointItem.getJoint() == null )
            return jointsList;
        if ( jointItem.getJoint().getType() != JointDef.JointType.RevoluteJoint &&
                jointItem.getJoint().getType() != JointDef.JointType.PrismaticJoint )
            return jointsList;
        BiScSet bset = jointItem.getBiScSet();
        for( JointItem ji : bset.getJointItems() ) {
            if ( ji.getJoint() == null )
                continue;
            if ( ji.getJoint().getType() != JointDef.JointType.GearJoint )
                continue;
            GearJoint gj = (GearJoint) ji.getJoint();
            if ( gj.getJoint1() == jointItem.getJoint() ) {
                jointsList.add(ji);
                continue;
            }
            if ( gj.getJoint2() == jointItem.getJoint() ) {
                jointsList.add(ji);
                continue;
            }
        }
        return jointsList;
    }


    void updateJointItem() {

        JointItem ji = formJointEditor.getJointItem();

        Array<JointItem> gearJoints = findMatchingGearJoints( ji );
        Array<JointItemDescription> gearJiDescList = new Array<JointItemDescription>();
        for ( JointItem gji : gearJoints )
            gearJiDescList.add( gji.getJointItemDescription() );

        JointItemFactory.loadFromDescription( ji, formJointEditor.getJointItemDescription() );
        if ( ji.getJoint() == null ) {
            Gdx.app.log("MainGui.updateJointItem", "WARNING! EMPTY JOINT");
            return;
        }

        for ( int i = 0; i < gearJoints.size; i++) {
            JointItem gji = gearJoints.get( i );
            JointItemDescription jiDesc = gearJiDescList.get( i );
            JointItemFactory.loadFromDescription( gji, jiDesc );
        }

    }

    void toggleScreen() {
        boolean simMode = chbSimulation.isSelected();

        if ( simMode ) {
            setGuiElementEnable( panelModelGui, false);
            setGuiElementEnable( panelSimulationControls, true);
        } else {
            setGuiElementEnable( panelModelGui, true );
            setGuiElementEnable( panelSimulationControls, false);
        }

        Gdx.app.postRunnable( new Runnable() {
            @Override
            public void run() {
                boolean simMode = chbSimulation.isSelected();
                if ( simMode ) {

                    PhysModelDescription description = model.getDescription();

                    if ( description == null)
                        return;

                    SkrGdxAppPhysModelEditor.get().getSimulationScreen().setModelDescription( description );
                    SkrGdxAppPhysModelEditor.get().toggleSimulationScreen();
                    SkrGdxAppPhysModelEditor.get().getSimulationScreen().startSimulation();

                } else {
                    SkrGdxAppPhysModelEditor.get().toggleEditorScreen();
                }
            }
        });

    }

    void restartSimulation() {
        Gdx.app.postRunnable( new Runnable() {
            @Override
            public void run() {
                SkrGdxAppPhysModelEditor.get().getSimulationScreen().startSimulation();
            }
        });
    }

    void doSimulationStep() {
        Gdx.app.postRunnable( new Runnable() {


            @Override
            public void run() {
                SkrGdxAppPhysModelEditor.get().getSimulationScreen().doStep();
            }
        });
    }

    void toggleSimulationPause() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                SkrGdxAppPhysModelEditor.get().getSimulationScreen().setPause(chbPauseSimulation.isSelected());
            }
        });
    }

    void toggleGrid() {
        boolean state = chbDisplayGrid.isSelected();
        SkrGdxAppPhysModelEditor.get().getEditorScreen().setDisplayGrid( state );
        SkrGdxAppPhysModelEditor.get().getSimulationScreen().setDisplayGrid( state );

    }

    void toggleDebugRender() {
        Environment.debugRender = chbDebugRender.isSelected();
    }

    void toggleDrawBiBBox() {
        Environment.drawBodyItemBBox = chbBiBBox.isSelected();
    }

    void setCenterOfMass() {
        float x, y;
        try {
            x = Float.valueOf( tfMassCenterWorldX.getText() );
            y = Float.valueOf( tfMassCenterWorldY.getText() );
        } catch ( NumberFormatException e ) {
            return;
        }
        BodyItemController ctrlr = SkrGdxAppPhysModelEditor.get().getEditorScreen().getBodyItemController();
        ctrlr.setWorldCenterOfMass( x, y );
    }

    void resetMassData() {
        BodyItemController ctrlr = SkrGdxAppPhysModelEditor.get().getEditorScreen().getBodyItemController();
        ctrlr.resetMassData();
    }

    private void setMassCorrectionEnabled() {
        BodyItemController ctrlr = SkrGdxAppPhysModelEditor.get().getEditorScreen().getBodyItemController();
        ctrlr.setEnableMassCorrection( chbEnableMassCorrection.isSelected() );

    }

    void changeSelectionMode() {
        EditorScreen.SelectionMode selMode = (EditorScreen.SelectionMode) comboSelectionMode.getSelectedItem();
        editorScreen.setSelectionMode( selMode );
    }

    void processMirroring() {
        if ( model == null )
            return;
        PhysModelProcessing.MirrorDirection dir = (PhysModelProcessing.MirrorDirection) comboMirrorDirection.getSelectedItem();
        modelStructureGuiProcessing.mirrorNode( dir );
    }



    void exportSelection() {
        PhysModelDescription desc = modelStructureGuiProcessing.createDescriptionForSelection();

        FileNameExtensionFilter ff = PhysModel.getFileFilter();

        final JFileChooser fch = new JFileChooser();
        int res;

        fch.setCurrentDirectory( new File( ApplicationSettings.get().getLastDirectory() ) );
        fch.setFileFilter( ff );

        res = fch.showSaveDialog(this);

        if ( res != JFileChooser.APPROVE_OPTION ) {
            return;
        }

        File fl = fch.getSelectedFile();

        ApplicationSettings.get().setLastDirectory( fl.getParent() );

        String exportFileNamePath = fl.getAbsolutePath();

        if ( !exportFileNamePath.toLowerCase().endsWith( "." + ff.getExtensions()[0]) ) {
            exportFileNamePath += ("." + ff.getExtensions()[0]);
        }
        PhysModel.saveModelDescription( desc, Gdx.files.absolute( exportFileNamePath ) );
    }



    void importModel() {
        if ( model == null )
            return;

        FileNameExtensionFilter ff = PhysModel.getFileFilter();
        final JFileChooser fch = new JFileChooser();
        int res;

        fch.setCurrentDirectory( new File( ApplicationSettings.get().getLastDirectory() ) );
        fch.setFileFilter( ff );

        res = fch.showOpenDialog( this );

        if ( res != JFileChooser.APPROVE_OPTION )
            return;

        File fl = fch.getSelectedFile();

        if (!fl.exists())
            return;

        ApplicationSettings.get().setLastDirectory( fl.getParent() );

        PhysModelDescription desc = PhysModel.loadModelDescription( Gdx.files.absolute( fl.getAbsolutePath()) );

        modelStructureGuiProcessing.importModelDescription( desc );

    }

    void processArrayDuplication() {
        int number = (Integer) spinToolDupNumber.getValue();
        if ( number == 0 )
            return;
        float xOffset;
        float yOffset;
        float rotation;
        try {
            xOffset = Float.parseFloat(tfToolDupXOffset.getText());
            yOffset = Float.parseFloat(tfToolDupYOffset.getText());
            rotation = Float.parseFloat(tfToolDupRotation.getText());
        } catch ( NumberFormatException e ) {
            return;
        }
        modelStructureGuiProcessing.duplicateNode( number, xOffset, yOffset, rotation );
    }

    private class ModelState {
        PhysModelDescription description;
        TreePath [] selectionPaths;
    }

    private static boolean snapshotDone = false;

    private Array< ModelState > historyArray = new Array< ModelState >();
    private int historyTail = -1;

    private void clearHistory() {
        historyArray.clear();
        historyTail = -1;
    }


    private void modelChanged() {
        snapshotDone = false;
        modelChanged = true;
    }

    public void makeHistorySnapshot() {
        if ( model == null )
            return;

        if ( historyTail < historyArray.size-1 )
            historyArray.removeRange( historyTail, historyArray.size-1 );

        PhysModelDescription stateDesc = model.getDescription();
        ModelState st = new ModelState();
        st.description = stateDesc;
        st.selectionPaths = treePhysModel.getSelectionPaths();
        historyArray.add( st );
        historyTail = historyArray.size - 1;

//        Gdx.app.log("MainGui.makeHistorySnapshot", "HistoryTail: " + historyTail + " BI Num in state " +
//        stateDesc.getBodyDescriptions().size);

        snapshotDone = true;
    }

    void onSnapshotTimer() {
        if ( model == null )
            return;

        if ( !snapshotDone )
            makeHistorySnapshot();
    }

    void undo() {

        if ( historyTail < 1  ) {
//            Gdx.app.log("MainGui.undo", "History start reached ");
            return;
        }
        PhysWorld.clearPrimaryWorld();
        historyTail--;

        ModelState st = historyArray.get( historyTail );
        PhysModelDescription desc = st.description;

        model = PhysModel.createFromDescription( desc, PhysWorld.getPrimaryWorld(), SkrGdxApplication.get().getAtlas() );
        modelToGui();
        if ( st.selectionPaths != null ) {
            treePhysModel.setSelectionPaths(st.selectionPaths);
            treePhysModel.scrollPathToVisible(st.selectionPaths[st.selectionPaths.length - 1]);
//            processTreeSelection( );
        }
//        Gdx.app.log("MainGui.undo", "HistoryTail: " + historyTail + " BI Num in desc " +
//                desc.getBodyDescriptions().size);
    }

    void redo() {
        if ( historyArray.size < 2 ) {
//            Gdx.app.log("MainGui.redo", "History is too small");
            return;
        }
        if ( historyTail >= historyArray.size - 1 ) {
//            Gdx.app.log("MainGui.undo", "History end reached ");
            return;
        }

        PhysWorld.clearPrimaryWorld();
        historyTail++;
        ModelState st = historyArray.get( historyTail );
        PhysModelDescription desc = st.description;
        model = PhysModel.createFromDescription( desc, PhysWorld.getPrimaryWorld(), SkrGdxApplication.get().getAtlas() );
        modelToGui();
        if ( st.selectionPaths != null ) {
            treePhysModel.setSelectionPaths(st.selectionPaths);
            treePhysModel.scrollPathToVisible(st.selectionPaths[st.selectionPaths.length - 1]);
//            processTreeSelection();
        }
//        Gdx.app.log("MainGui.redo", "HistoryTail: " + historyTail+ " BI Num in desc " +
//                desc.getBodyDescriptions().size);
    }


    void processExitCall() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }


    void setSelectionMode( EditorScreen.SelectionMode mode ) {
        comboSelectionMode.setSelectedItem( mode );
        changeSelectionMode();
    }


    void showModelPoliciesDialog() {
        dlgPolicy.execute(model);
    }

    void showBodyItemDialog() {
        PhysModelJTreeNode node = (PhysModelJTreeNode) treePhysModel.getLastSelectedPathComponent();
        if ( node == null )
            return;
        if ( node.type != PhysModelJTreeNode.Type.BODY_ITEM )
            return;
//        dlgPolicy.display((BodyItem) node.getUserObject());
        //TODO: implement this
    }

    //======================= main ================================

    public static void main(String [] args) {
        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run() {
                MainGui instance = new MainGui();
                instance.setVisible(true);
            }
        });
    }

}
