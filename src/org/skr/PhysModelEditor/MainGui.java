package org.skr.PhysModelEditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.JointDef;
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
import org.skr.gdx.physmodel.PhysModelProcessing;
import org.skr.gdx.physmodel.bodyitem.BiScSet;
import org.skr.gdx.physmodel.bodyitem.BodyItem;
import org.skr.gdx.physmodel.bodyitem.fixtureset.FixtureSet;
import org.skr.gdx.physmodel.jointitem.JointItem;
import org.skr.gdx.physmodel.jointitem.JointItemDescription;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
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
    private JComboBox<BodyItem> comboBodyASelector;
    private JComboBox<BodyItem> comboBodyBSelector;
    private JTextField tfAnchorA_X;
    private JTextField tfAnchorA_Y;
    private JPanel jointTypePanel;
    private JComboBox<JointDef.JointType> comboJointType;
    private JTextField tfAnchorB_Y;
    private JTextField tfAnchorB_X;
    private JButton btnSetAnchorA;
    private JButton btnSetAnchorB;
    private JButton btnCreateJoint;
    private JCheckBox chbCollideConnected;
    private JCheckBox chbSimulation;
    private JPanel panelModelGui;
    private JPanel panelSimulationControls;
    private JCheckBox chbPauseSimulation;
    private JButton btnSimulationStep;
    private JButton btnSimulationRestart;
    private JCheckBox chbDisplayGrid;
    private JCheckBox chbDebugRender;
    private JPanel panelAnchorB;
    private JPanel panelAnchorA;
    private JPanel panelCollideConnected;
    private JPanel panelBodySelector;
    private JPanel panelJointCreatorFeatures;
    private JPanel panelAxis;
    private JButton btnSetAxis;
    private JTextField tfAxis_Y;
    private JTextField tfAxis_X;
    private JPanel panelGroundAnchors;
    private JTextField tfGroundAnchorA_X;
    private JTextField tfGroundAnchorA_Y;
    private JTextField tfGroundAnchorB_X;
    private JTextField tfGroundAnchorB_Y;
    private JButton btnSetGroundAnchorA;
    private JButton btnSetGroundAnchorB;
    private JTextField tfRatio;
    private JPanel panelRatio;
    private JPanel panelJoints;
    private JComboBox<JointItem> comboJoint1;
    private JComboBox<JointItem> comboJoint2;
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

    private SkrGdxAppPhysModelEditor gApp;
    private String currentModelFileName = "";
    private PhysModel model = null;



    private JointItemDescription jiDesc;
    private PhysModelJTreeNode jointsGroupNode;
    private PhysModelJTreeNode bodiesGroupNode;

    private PhysModelStructureGuiProcessing modelStructureGuiProcessing;
    private EditorScreen editorScreen;

    private String textureAtlasFilePath = "";

    private Timer snapshotTimer;

    private boolean modelChanged = false;

    MainGui() {

        chbDebugRender.setSelected( Environment.debugRender );

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

        for (JointDef.JointType jt : JointDef.JointType.values() ) {
            comboJointType.addItem( jt );
        }

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
        btnSetAnchorA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAnchorPointFromGui(JointCreatorController.AnchorControlPoint.AcpType.typeA );
            }
        });
        btnSetAnchorB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAnchorPointFromGui(JointCreatorController.AnchorControlPoint.AcpType.typeB);
            }
        });
        btnCreateJoint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createJoint();
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
        comboJointType.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                updateJointCreatorPanelFeatures();
            }
        });
        btnSetAxis.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAnchorPointFromGui( JointCreatorController.AnchorControlPoint.AcpType.typeAxis );
            }
        });
        btnSetGroundAnchorA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAnchorPointFromGui( JointCreatorController.AnchorControlPoint.AcpType.typeC );
            }
        });
        btnSetGroundAnchorB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAnchorPointFromGui(JointCreatorController.AnchorControlPoint.AcpType.typeD);
            }
        });
        btnDuplicate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processNodeDuplication();
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
        comboBodyASelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectBodyItemForJointCreatorController(0);
            }
        });
        comboBodyBSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectBodyItemForJointCreatorController(1);
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

                editorScreen.getMultiBodyItemsController().setControlPointListener(
                        new Controller.controlPointListener() {
                            @Override
                            public void changed(Object controlledObject, Controller.ControlPoint controlPoint) {
                                bodyItemsChangedByController();
                            }
                });

                editorScreen.getJointCreatorController().setBodyItemSelectionListener(
                        new JointCreatorController.BodyItemSelectionListener() {
                            @Override
                            public void bodyASelected(BodyItem bi) {
                                selectBodyItemA( bi );
                            }
                            @Override
                            public void bodyBSelected(BodyItem bi) {
                                selectBodyItemB(bi);
                            }
                        }
                );

                jiDesc = editorScreen.getJointCreatorController().getDescription();
                editorScreen.getJointCreatorController().setControlPointListener(
                        new Controller.controlPointListener() {
                            @Override
                            public void changed(Object controlledObject, Controller.ControlPoint controlPoint) {
                                loadAnchorPointsPosition();
                            }
                        }
                );

            }
        });

        //TODO: recode this
//        ShapeController.setStaticShapeControllerListener(new ShapeController.ShapeControllerListener() {
//            @Override
//            public void controlPointChanged(ShapeDescription shapeDescription, Controller.ControlPoint controlPoint) {
//                shapeControlPointChanged(shapeDescription, controlPoint);
//            }
//
//            @Override
//            public void positionChanged(ShapeDescription shapeDescription) {
//                shapePositionChanged(shapeDescription);
//            }
//
//            @Override
//            public void radiusChanged(ShapeDescription shapeDescription) {
//                shapeRadiusChanged(shapeDescription);
//            }
//        });
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
                processNodeDuplication();
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
        ApplicationSettings.get().setLastDirectory( fl.getParent() );
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

        updateJointCombos();
        setTitle("PhysModel file: " + currentModelFileName);
    }

    void selectNode( DefaultMutableTreeNode node ) {
        //TODO: update
//        TreePath newPath = new TreePath(node.getPath());
//        treePhysModel.setSelectionPath(new TreePath(node.getPath()));
//        treePhysModel.scrollPathToVisible(newPath);
//        processTreeSelection( );
    }


    int showCloseModelYesNoDialog() {
        return JOptionPane.showOptionDialog(this, "Do you really want to close the current model?", "Close Model",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null );
    }

    public void setupEditors(Object object, EditorScreen.ModelObjectType mot ) {
        tabbedPaneEditors.removeAll();

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
            case OT_JointItems:
                tabbedPaneEditors.add("Joint creator", panelJointCreator );
                updateJointCreatorGui();
                updateJointCreatorPanelFeatures();
                break;
        }

        setGuiElementEnable( tabbedPaneEditors, true );

        editorScreen.setModelObject( object, mot );
    }

    void processNodeDuplication() {
//TODO: update
//        if ( model == null )
//            return;
//
//        propertiesCellEditor.cancelCellEditing();
//        tabbedPaneEditors.removeAll();
//
//        setGuiElementEnable(panelShapeEditor, false);
//
//        TreePath [] selectedPaths = treePhysModel.getSelectionPaths();
//
//        if ( selectedPaths == null )
//            return;
//
//        PhysModelJTreeNode newNode = null;
//
//        for (TreePath tp : selectedPaths ) {
//            PhysModelJTreeNode node = (PhysModelJTreeNode) tp.getLastPathComponent();
//            newNode = duplicateNode( node );
//        }
//
//        PhysModelJTreeNode node = (PhysModelJTreeNode) treePhysModel.getLastSelectedPathComponent();
//
//        if ( node == null )
//            return;
//
//        if ( newNode != null ) {
//            selectNode( newNode );
//        }
//
//        modelChanged = true;
//        makeHistorySnapshot();
    }


    private PhysModelJTreeNode duplicateNode( PhysModelJTreeNode node ) {
        //TODO: update
//        if ( node.getUserObject() == null )
//            return null;
//
//        PhysModelJTreeNode parentNode = (PhysModelJTreeNode) node.getParent();
//        DefaultTreeModel md = (DefaultTreeModel) treePhysModel.getModel();
//
//        PhysModelJTreeNode newNode = null;
//
//        switch ( node.getType() ) {
//            case MODEL:
//                return null;
//            case AAG: {
//
//                    if (parentNode.getType() != PhysModelJTreeNode.Type.AAG)
//                        return null;
//                    AnimatedActorGroup pAag = (AnimatedActorGroup) parentNode.getUserObject();
//                    AnimatedActorGroup aag = (AnimatedActorGroup) node.getUserObject();
//                    AagDescription aagDesc = aag.getDescription();
//                    aagDesc.setName(aagDesc.getName() + "Cpy");
//                    AnimatedActorGroup newAag = new AnimatedActorGroup(aagDesc, SkrGdxApplication.get().getAtlas());
//                    pAag.addChild(newAag);
//                    newNode = createTreeAagNode(parentNode, newAag);
//                }
//                break;
//
//            case AAG_SC:
//                return null;
//            case AAG_SC_SET: {
//                    ScContainer.Handler h = (ScContainer.Handler) node.getUserObject();
//                    AagScContainer cont = (AagScContainer) h.container;
//                    AnimatedActorGroup aag = (AnimatedActorGroup) cont.getContent(h.id);
//                    String name = cont.findNameById( h.id );
//                    AagDescription desc = aag.getDescription();
//                    AnimatedActorGroup newAag = new AnimatedActorGroup(desc, SkrGdxApplication.get().getAtlas() );
//                    Integer newId = cont.generateId();
//                    cont.addContent( newId, newAag );
//                    if ( name != null ) {
//                        cont.setContentName( newId, name + "Cpy");
//                    }
//                    newNode = createTreeAagScNode( parentNode, newId );
//                }
//                break;
//            case BODY_ITEM:
//
//                BodyItem bi = (BodyItem) node.getUserObject();
//                BodyItemDescription bd = bi.createBodyItemDescription();
//                bd.setName( bd.getName() + "Cpy");
//                bd.setId( -1 );
//                BodyItem dupBi = model.addBodyItem( bd );
//                newNode = createTreeBodyItemNode(dupBi);
//
//                break;
//            case BODIES_GROUP:
//                break;
//            case FIXTURE_SET:
//
//                BodyItem bodyItem = (BodyItem) parentNode.getUserObject();
//                FixtureSet fs = (FixtureSet) node.getUserObject();
//                FixtureSetDescription fd = fs.getDescription();
//                FixtureSet newFs = bodyItem.addNewFixtureSet( fd );
//                newNode = createTreeFixtureSetNode( parentNode, newFs );
//
//                break;
//            case JOINT_ITEM:
//                return null;
//
//
//            case JOINTS_GROUP:
//                break;
//        }
//
//        if ( parentNode != null ) {
//            md.nodeChanged(parentNode);
//            md.nodeStructureChanged(parentNode);
//        }
//
//        return newNode;
        return null;
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
        modelStructureGuiProcessing.objectModifiedByController();
        Vector2 center = bodyItem.getBody().getWorldCenter();
        tfMassCenterWorldX.setText("" + center.x );
        tfMassCenterWorldY.setText("" + center.y );
        modelChanged();
    }

    private void bodyItemsChangedByController() {
        modelChanged();
    }

    private void actorChangedByController(Actor actor) {
        modelStructureGuiProcessing.objectModifiedByController();
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

//TODO: update
//    void shapeControlPointChanged( ShapeDescription shapeDescription, Controller.ControlPoint cp ) {
//
//        float x = PhysWorld.get().toPhys( cp.getX() );
//        float y = PhysWorld.get().toPhys( cp.getY() );
//        tfControlPointX.setText( String.valueOf( x ) );
//        tfControlPointY.setText( String.valueOf( y ) );
//
//        if ( fixtureSetPropertiesTableModel.getFixtureSet().getShapeType() == Shape.Type.Circle ) {
//            tfRadius.setText( String.valueOf( shapeDescription.getRadius() ) );
//        }
//    }
//
//    void shapePositionChanged( @SuppressWarnings("UnusedParameters") ShapeDescription shapeDescription ) {
//        // does nothing
//    }
//
//    void shapeRadiusChanged( ShapeDescription shapeDescription ) {
//        tfRadius.setText( String.valueOf( shapeDescription.getRadius() ) );
//    }

    void setLooped(boolean state, ShapeController controller) {
        controller.setLooped( state );
    }

    void setAutoTessellate( final boolean state, final ShapeController controller ) {
        Gdx.app.postRunnable( new Runnable() {
            @Override
            public void run() {
                controller.setAutoTessellate( state );
            }
        });
    }

    void tessellatePolygon( ShapeController controller ) {
        controller.tessellatePolygon();
    }


    void updateJointCreatorGui() {

        BodyItem selA = (BodyItem) comboBodyASelector.getSelectedItem();
        BodyItem selB = (BodyItem) comboBodyBSelector.getSelectedItem();

        comboBodyASelector.removeAllItems();
        comboBodyBSelector.removeAllItems();

        BiScSet bset = model.getScBodyItems().getCurrentSet();
        if ( bset == null )
            return;

        for ( BodyItem bi : bset.getBodyItems() ) {
            comboBodyASelector.addItem( bi );
            comboBodyBSelector.addItem( bi );
        }

        if ( selA != null && bset.getBodyItems().contains( selA, true))
            comboBodyASelector.setSelectedItem( selA );

        if ( selB != null && bset.getBodyItems().contains( selB, true))
            comboBodyBSelector.setSelectedItem( selB );

        tfAnchorA_X.setText("");
        tfAnchorA_Y.setText("");
        tfAnchorB_X.setText("");
        tfAnchorB_Y.setText("");
        tfAxis_X.setText("");
        tfAxis_Y.setText("");
        tfGroundAnchorA_X.setText("");
        tfGroundAnchorA_Y.setText("");
        tfGroundAnchorB_X.setText("");
        tfGroundAnchorB_Y.setText("");
        tfRatio.setText("1.0");
        loadAnchorPointsPosition();
    }

    void loadAnchorPointsPosition() {
        tfAnchorA_X.setText("" + jiDesc.getAnchorA().x );
        tfAnchorA_Y.setText("" + jiDesc.getAnchorA().y );
        tfAnchorB_X.setText("" + jiDesc.getAnchorB().x );
        tfAnchorB_Y.setText("" + jiDesc.getAnchorB().y );
        tfAxis_X.setText("" + jiDesc.getAxis().x);
        tfAxis_Y.setText("" + jiDesc.getAxis().y);
        tfGroundAnchorA_X.setText("" + jiDesc.getGroundAnchorA().x);
        tfGroundAnchorA_Y.setText("" + jiDesc.getGroundAnchorA().y);
        tfGroundAnchorB_X.setText("" + jiDesc.getGroundAnchorB().x);
        tfGroundAnchorB_Y.setText("" + jiDesc.getGroundAnchorB().y);
        tfRatio.setText("" + jiDesc.getRatio() );
    }


    void setAnchorPointFromGui( JointCreatorController.AnchorControlPoint.AcpType type ) {
        Vector2 av = null;
        JTextField tf_x = null;
        JTextField tf_y = null;


        switch ( type ) {
            case typeA:
                av = jiDesc.getAnchorA();
                tf_x = tfAnchorA_X;
                tf_y = tfAnchorA_Y;
                break;
            case typeB:
                av = jiDesc.getAnchorB();
                tf_x = tfAnchorB_X;
                tf_y = tfAnchorB_Y;
                break;
            case typeAxis:
                av = jiDesc.getAxis();
                tf_x = tfAxis_X;
                tf_y = tfAxis_Y;
                break;

            case typeC:
                av = jiDesc.getGroundAnchorA();
                tf_x = tfGroundAnchorA_X;
                tf_y = tfGroundAnchorA_Y;
                break;
            case typeD:
                av = jiDesc.getGroundAnchorB();
                tf_x = tfGroundAnchorB_X;
                tf_y = tfGroundAnchorB_Y;
                break;
        }

        if ( av == null )
            return;

        float x = Float.valueOf(tf_x.getText());
        float y = Float.valueOf( tf_y.getText() );

        av.set( x, y );
    }



    void createJoint() {
        //TODO: update
//        jiDesc.setType((JointDef.JointType) comboJointType.getSelectedItem() );
//
//        BodyItem biA = (BodyItem) comboBodyASelector.getSelectedItem();
//        BodyItem biB = (BodyItem) comboBodyBSelector.getSelectedItem();
//
//        if ( biA == null )
//            return;
//        if ( biB == null )
//            return;
//        if ( biA == biB )
//            return;
//
//        jiDesc.setBodyAId( biA.getId() );
//        jiDesc.setBodyBId( biB.getId() );
//        jiDesc.setCollideConnected(chbCollideConnected.isSelected());
//
//        setAnchorPointFromGui(JointCreatorController.AnchorControlPoint.AcpType.typeA);
//        setAnchorPointFromGui(JointCreatorController.AnchorControlPoint.AcpType.typeB);
//        if ( jiDesc.getType() == JointDef.JointType.PulleyJoint ) {
//            setAnchorPointFromGui(JointCreatorController.AnchorControlPoint.AcpType.typeC);
//            setAnchorPointFromGui(JointCreatorController.AnchorControlPoint.AcpType.typeD);
//        } else if ( jiDesc.getType() == JointDef.JointType.PrismaticJoint ) {
//            setAnchorPointFromGui(JointCreatorController.AnchorControlPoint.AcpType.typeAxis);
//        }
//
//        try {
//            jiDesc.setRatio(Float.valueOf(tfRatio.getText()));
//        } catch (NumberFormatException e) {
//            Gdx.app.error("MainGui.createJoint", "Ratio: " + e.getMessage() );
//            jiDesc.setRatio(1);
//        }
//
//
//        jiDesc.setName("_" + jiDesc.getType());
//        if ( jiDesc.getType() == JointDef.JointType.GearJoint ) {
//            JointItem jiA = (JointItem) comboJoint1.getSelectedItem();
//            JointItem jiB = (JointItem) comboJoint2.getSelectedItem();
//            if ( jiA == null )
//                return;
//            if ( jiB == null )
//                return;
//            jiDesc.setJointAId( jiA.getId() );
//            jiDesc.setJointBId( jiB.getId() );
//        }
//
//        JointItem ji = model.addNewJointItem( jiDesc );
//        if ( ji == null)
//            return;
//
//        DefaultTreeModel md = (DefaultTreeModel) treePhysModel.getModel();
//        PhysModelJTreeNode newNode = new PhysModelJTreeNode(PhysModelJTreeNode.Type.JOINT_ITEM, ji );
//        jointsGroupNode.add( newNode );
//        md.nodeChanged( jointsGroupNode );
//        md.nodeStructureChanged(jointsGroupNode);
//        updateJointCombos();
//
//        selectNode( newNode );
//
//        modelChanged = true;
//        makeHistorySnapshot();

    }

    void updateJointCombos() {
//TODO: update
//        comboJoint1.removeAllItems();
//        comboJoint2.removeAllItems();
//
//        if ( model == null )
//            return;
//
//        for (JointItem ji : model.getJointItems() ) {
//            if ( ji.getJoint().getType() == JointDef.JointType.RevoluteJoint ) {
//                comboJoint1.addItem( ji );
//                comboJoint2.addItem( ji );
//                continue;
//            }
//
//            if ( ji.getJoint().getType() == JointDef.JointType.PrismaticJoint ) {
//                comboJoint2.addItem( ji );
//            }
//        }
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
        Gdx.app.postRunnable( new Runnable() {
            @Override
            public void run() {
                boolean state = chbDisplayGrid.isSelected();
                SkrGdxAppPhysModelEditor.get().getEditorScreen().setDisplayGrid( state );
                SkrGdxAppPhysModelEditor.get().getSimulationScreen().setDisplayGrid( state );
            }
        });
    }

    void toggleDebugRender() {
        Gdx.app.postRunnable( new Runnable() {
            @Override
            public void run() {
                Environment.debugRender = chbDebugRender.isSelected();
            }
        });
    }

    void updateJointCreatorPanelFeatures() {
        panelJointCreatorFeatures.setVisible( true );
        panelAxis.setVisible( false );
        panelGroundAnchors.setVisible( false );
        panelRatio.setVisible( false );
        panelJoints.setVisible( false );
        panelAnchorA.setVisible( true );
        panelAnchorB.setVisible( true );
        JointDef.JointType type = (JointDef.JointType) comboJointType.getSelectedItem();
        JointCreatorController ctrlr = SkrGdxAppPhysModelEditor.get().getEditorScreen().getJointCreatorController();

        switch ( type ) {
            case Unknown:
                panelJointCreatorFeatures.setVisible( false );
                ctrlr.setMode(JointCreatorController.Mode.NoPoints);
                break;
            case RevoluteJoint:
                ctrlr.setMode(JointCreatorController.Mode.OnePointMode);
                panelAnchorB.setVisible( false );
                break;
            case PrismaticJoint:
                ctrlr.setMode(JointCreatorController.Mode.OnePointAndAxisMode);
                panelAxis.setVisible( true );
                panelAnchorB.setVisible( false );
                break;
            case DistanceJoint:
                ctrlr.setMode(JointCreatorController.Mode.TwoPointsMode);
                break;
            case PulleyJoint:
                panelGroundAnchors.setVisible( true );
                ctrlr.setMode(JointCreatorController.Mode.FourPointsMode );
                panelRatio.setVisible( true );
                break;
            case MouseJoint:
                break;
            case GearJoint:
                ctrlr.setMode(JointCreatorController.Mode.NoPoints);
                panelRatio.setVisible( true );
                panelJoints.setVisible( true );
                panelAnchorA.setVisible( false );
                panelAnchorB.setVisible( false );
                break;
            case WheelJoint:
                ctrlr.setMode(JointCreatorController.Mode.OnePointAndAxisMode);
                panelAxis.setVisible( true );
                panelAnchorB.setVisible( false );
                break;
            case WeldJoint:
                ctrlr.setMode(JointCreatorController.Mode.OnePointMode);
                panelAnchorB.setVisible( false );
                break;
            case FrictionJoint:
                ctrlr.setMode(JointCreatorController.Mode.OnePointMode);
                panelAnchorB.setVisible( false );
                break;
            case RopeJoint:
                ctrlr.setMode(JointCreatorController.Mode.TwoPointsMode);
                break;
            case MotorJoint:
                ctrlr.setMode(JointCreatorController.Mode.NoPoints);
                panelAnchorA.setVisible( false );
                panelAnchorB.setVisible( false );
                break;
        }
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

    void selectBodyItemA( BodyItem bi ) {
        comboBodyASelector.setSelectedItem( bi );
    }

    void selectBodyItemB(BodyItem bi) {
        comboBodyBSelector.setSelectedItem( bi );
    }

    void selectBodyItemForJointCreatorController(int selId) {
        //TODO: update
//        JointCreatorController ctrlr = SkrGdxAppPhysModelEditor.get().getEditorScreen().getJointCreatorController();
//        if ( selId == 0 ) {
//            ctrlr.setSelectedBodyItemA((BodyItem) comboBodyASelector.getSelectedItem());
//        } else {
//            ctrlr.setSelectedBodyItemB((BodyItem) comboBodyBSelector.getSelectedItem());
//        }
    }

    void changeSelectionMode() {
        EditorScreen.SelectionMode selMode = (EditorScreen.SelectionMode) comboSelectionMode.getSelectedItem();
        editorScreen.setSelectionMode( selMode );
    }

    void processMirroring() {
//TODO: update
//        if ( model == null )
//            return;
//
//
//        TreePath [] selectedPaths = treePhysModel.getSelectionPaths();
//
//        if ( selectedPaths == null )
//            return;
//
//        PhysModelProcessing.MirrorDirection dir = (PhysModelProcessing.MirrorDirection) comboMirrorDirection.getSelectedItem();
//
//        for ( TreePath tp : selectedPaths ) {
//            PhysModelJTreeNode node = (PhysModelJTreeNode) tp.getLastPathComponent();
//
//            switch ( node.getType() ) {
//                case MODEL:
//                    mirrorModel( dir );
//                    return;
//                case AAG:
//                    mirrorAag( node, dir);
//                    break;
//                case AAG_SC:
//                    return;
//                case AAG_SC_SET:
//                    return;
//                case BODY_ITEM:
//                    mirrorBodyItem( node, dir );
//                    break;
//                case BODIES_GROUP:
//                    break;
//                case FIXTURE_SET:
//                    mirrorFixtureSet( node, dir );
//                    break;
//                case JOINT_ITEM:
//                    break;
//                case JOINTS_GROUP:
//                    break;
//            }
//        }
//
//        modelChanged = true;
//        makeHistorySnapshot();
    }

//TODO: update
//    void mirrorAag( PhysModelJTreeNode node , PhysModelProcessing.MirrorDirection dir ) {
//        AnimatedActorGroup aag = (AnimatedActorGroup) node.getUserObject();
//        AagDescription desc = aag.getDescription();
//        PhysModelProcessing.mirrorAagDescription(desc, dir);
//        aag.loadFromDescription(desc, SkrGdxApplication.get().getAtlas());
//    }
//
//    void mirrorBodyItem( PhysModelJTreeNode node, PhysModelProcessing.MirrorDirection dir ) {
//        BodyItem bi = (BodyItem) node.getUserObject();
//        BodyItemDescription desc = bi.createBodyItemDescription();
//        PhysModelProcessing.mirrorBodyItemDescription(desc, dir);
//        model.removeBody( bi );
//        bi = model.addBodyItem( desc );
//        node.setUserObject(bi);
//        cleanupJointsGroup();
//    }
//
//    void mirrorFixtureSet( PhysModelJTreeNode node, PhysModelProcessing.MirrorDirection dir ) {
//        FixtureSet fs = (FixtureSet) node.getUserObject();
//        FixtureSetDescription fsDesc = fs.getDescription();
//
//        PhysModelProcessing.mirrorFixtureSetDescription( fsDesc, dir );
//
//        BodyItem bi = fs.getBodyItem();
//        bi.removeFixtureSet( fs );
//        fs = bi.addNewFixtureSet( fsDesc );
//
//        node.setUserObject(fs);
//    }
//
//    void mirrorModel( PhysModelProcessing.MirrorDirection dir ) {
//        PhysModel.Description desc = model.getDescription();
//
//        PhysModelProcessing.mirrorModelDescription(desc, dir);
//
//        PhysWorld.clearPrimaryWorld();
//        model = new PhysModel( desc,PhysWorld.getPrimaryWorld(), SkrGdxApplication.get().getAtlas() );
//
//        modelToGui();
//    }

    void exportSelection() {
//TODO: update
//        TreePath [] selectedPaths = treePhysModel.getSelectionPaths();
//
//        if ( selectedPaths == null )
//            return;
//
//
//        PhysModel.Description desc = new PhysModel.Description();
//        desc.setName( model.getName() + "-export");
//        desc.setUuid( UUID.randomUUID() );
//
//        Array<Integer> exportedId = new Array<Integer>();
//
//        for( TreePath tp : selectedPaths ) {
//            PhysModelJTreeNode tn = (PhysModelJTreeNode) tp.getLastPathComponent();
//            switch ( tn.getType() ) {
//                case MODEL:
//                    return;
//                case AAG:
//                    return;
//                case AAG_SC:
//                    return;
//                case AAG_SC_SET:
//                    return;
//                case BODY_ITEM:
//                    BodyItem bi = (BodyItem) tn.getUserObject();
//                    BodyItemDescription bid = bi.createBodyItemDescription();
//                    desc.getBodyDescriptions().add( bid );
//                    exportedId.add( bi.getId() );
//                    break;
//                case BODIES_GROUP:
//                    return;
//                case FIXTURE_SET:
//                    return;
//                case JOINT_ITEM:
//                    return;
//                case JOINTS_GROUP:
//                    return;
//            }
//        }
//
//        for ( JointItem ji : model.getJointItems() ) {
//            int bAId = ji.getBodyAId();
//            int bBId = ji.getBodyBId();
//            if ( !exportedId.contains( bAId, false ) )
//                continue;
//            if ( !exportedId.contains( bBId, false ) )
//                continue;
//            JointItemDescription jid = ji.createJointItemDescription();
//            desc.getJointsDesc().add( jid );
//        }
//
//
//        FileNameExtensionFilter ff = PhysModel.getFileFilter();
//
//        final JFileChooser fch = new JFileChooser();
//        int res;
//
//        fch.setCurrentDirectory( new File( ApplicationSettings.get().getLastDirectory() ) );
//        fch.setFileFilter( ff );
//
//        res = fch.showSaveDialog(this);
//
//        if ( res != JFileChooser.APPROVE_OPTION ) {
//            return;
//        }
//
//        File fl = fch.getSelectedFile();
//
//        ApplicationSettings.get().setLastDirectory( fl.getParent() );
//
//        String exportFileNamePath = fl.getAbsolutePath();
//
//        if ( !exportFileNamePath.toLowerCase().endsWith( "." + ff.getExtensions()[0]) ) {
//            exportFileNamePath += ("." + ff.getExtensions()[0]);
//        }
//        PhysModel.saveModelDescription( desc, Gdx.files.absolute( exportFileNamePath ) );
    }



    void importModel() {
//TODO: update
//        if ( model == null )
//            return;
//
//        FileNameExtensionFilter ff = PhysModel.getFileFilter();
//        final JFileChooser fch = new JFileChooser();
//        int res;
//
//
//        fch.setCurrentDirectory( new File( ApplicationSettings.get().getLastDirectory() ) );
//        fch.setFileFilter( ff );
//
//
//        res = fch.showOpenDialog( this );
//
//        if ( res != JFileChooser.APPROVE_OPTION )
//            return;
//
//        File fl = fch.getSelectedFile();
//
//        if (!fl.exists())
//            return;
//
//        ApplicationSettings.get().setLastDirectory( fl.getParent() );
//
//        PhysModel.Description desc = PhysModel.loadModelDescription( Gdx.files.absolute( fl.getAbsolutePath()) );
//
//        HashMap<Integer, Integer > idRemapTable = new HashMap<Integer, Integer>();
//
//        for ( BodyItemDescription bid : desc.getBodyDescriptions() ) {
//            int id = bid.getId();
//            int newId = BodyItem.genNextId( -1 );
//
//            idRemapTable.put( id, newId );
//            bid.setId( newId );
//
//            BodyItem bi = model.addBodyItem( bid );
//
//            createTreeBodyItemNode( bi );
//        }
//
//        HashMap< Integer, Integer > jidRemapTable = new HashMap<Integer, Integer>();
//
//        for ( JointItemDescription jid : desc.getJointsDesc() ) {
//            int id = jid.getId();
//            int newId = JointItem.genNextId( -1 );
//            jid.setId( newId );
//            jidRemapTable.put( id, newId );
//
//            int bId = jid.getBodyAId();
//            if ( !idRemapTable.containsKey( bId) )
//                continue;
//            jid.setBodyAId( idRemapTable.get( bId) );
//
//            bId = jid.getBodyBId();
//            if ( !idRemapTable.containsKey( bId ) )
//                continue;
//            jid.setBodyBId( idRemapTable.get( bId ) );
//
//            if ( jid.getType() == JointDef.JointType.GearJoint ) {
//                int jId = jid.getJointAId();
//                if ( !jidRemapTable.containsKey( jId ) )
//                    continue;
//                jid.setJointAId( jidRemapTable.get( jId ) );
//
//                jId = jid.getJointBId();
//                if ( ! jidRemapTable.containsKey( jId ) )
//                    continue;
//                jid.setJointBId( jidRemapTable.get( jId ) );
//            }
//
//            JointItem ji = model.addNewJointItem( jid );
//
//            createTreeJointItemNode( ji );
//        }
//
//        modelChanged = true;
//        makeHistorySnapshot();
    }



    void processArrayDuplication() {
        //TODO: update
//        int number = (Integer) spinToolDupNumber.getValue();
//        if ( number == 0 )
//            return;
//        float xOffset;
//        float yOffset;
//        float rotation;
//        try {
//            xOffset = Float.parseFloat(tfToolDupXOffset.getText());
//            yOffset = Float.parseFloat(tfToolDupYOffset.getText());
//            rotation = Float.parseFloat(tfToolDupRotation.getText());
//        } catch ( NumberFormatException e ) {
//            return;
//        }
//
//        TreePath [] selectedPaths = treePhysModel.getSelectionPaths();
//
//        if ( selectedPaths == null )
//            return;
//        for ( TreePath tp : selectedPaths ) {
//            PhysModelJTreeNode node = (PhysModelJTreeNode) tp.getLastPathComponent();
//            switch (node.getType()) {
//                case MODEL:
//                    continue;
//                case AAG:
//                    break;
//                case AAG_SC:
//                    continue;
//                case AAG_SC_SET:
//                    continue;
//                case BODY_ITEM:
//                    break;
//                case BODIES_GROUP:
//                    continue;
//                case FIXTURE_SET:
//                    break;
//                case JOINT_ITEM:
//                    continue;
//                case JOINTS_GROUP:
//                    continue;
//            }
//
//            duplicateNodeAsArray( node, number, xOffset, yOffset, rotation );
//        }
//
//        modelChanged = true;
//        makeHistorySnapshot();
    }

//TODO: update
//    void duplicateNodeAsArray( PhysModelJTreeNode node, int number, float xOffset, float yOffset, float rotation ) {
//
//        for ( int i = 0; i < number; i++ ) {
//
//            PhysModelJTreeNode newNode = duplicateNode( node );
//            if ( newNode == null )
//                continue;
//            switch (newNode.getType()) {
//                case MODEL:
//                    continue;
//                case AAG:
//                    AnimatedActorGroup baseAag = (AnimatedActorGroup) node.getUserObject();
//                    AnimatedActorGroup aag = (AnimatedActorGroup) newNode.getUserObject();
//                    aag.setPosition( baseAag.getX() + i * xOffset, baseAag.getY() + i * yOffset );
//                    aag.setRotation( baseAag.getRotation() + i * rotation );
//                    break;
//                case AAG_SC:
//                    continue;
//                case AAG_SC_SET:
//                    continue;
//                case BODY_ITEM:
//                    BodyItem baseBi = (BodyItem) node.getUserObject();
//                    BodyItem bi = (BodyItem) newNode.getUserObject();
//                    bi.getBody().setTransform(  baseBi.getBody().getPosition().x + i * xOffset,
//                            baseBi.getBody().getPosition().y + i * yOffset,
//                            baseBi.getBody().getAngle() + i * MathUtils.degreesToRadians * rotation );
//
//                    break;
//                case BODIES_GROUP:
//                    continue;
//                case FIXTURE_SET:
//                    break;
//                case JOINT_ITEM:
//                    continue;
//                case JOINTS_GROUP:
////                    continue;
//            }
//
//        }
//
//    }

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
