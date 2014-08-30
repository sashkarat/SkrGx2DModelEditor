package org.skr.PhysModelEditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import org.skr.PhysModelEditor.PropertiesTableElements.*;
import org.skr.PhysModelEditor.gdx.editor.SkrGdxAppPhysModelEditor;
import org.skr.gdx.SkrGdxApplication;
import org.skr.gdx.PhysWorld;
import org.skr.PhysModelEditor.gdx.editor.controllers.*;
import org.skr.gdx.editor.controller.Controller;
import org.skr.gdx.physmodel.*;
import org.skr.gdx.physmodel.animatedactorgroup.AagDescription;
import org.skr.gdx.physmodel.animatedactorgroup.AnimatedActorGroup;
import org.skr.PhysModelEditor.gdx.editor.screens.EditorScreen;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.UUID;

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
    private JComboBox comboBodyASelector;
    private JComboBox comboBodyBSelector;
    private JTextField tfAnchorA_X;
    private JTextField tfAnchorA_Y;
    private JPanel jointTypePanel;
    private JComboBox comboJointType;
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
    private JComboBox comboJoint1;
    private JComboBox comboJoint2;
    private JButton btnDuplicate;
    private JPanel panelBodyItemEditor;
    private JTextField tfMassCenterWorldX;
    private JTextField tfMassCenterWorldY;
    private JButton btnSetMassCenter;
    private JButton btnResetMassData;
    private JCheckBox chbEnableMassCorrection;
    private JComboBox comboSelectionMode;
    private JPanel panelSelectionMode;
    private JPanel panelTools;
    private JTextField tfMirrorAxisX;
    private JTextField tfMirrorAxisY;
    private JButton btnMirrorModel;
    private JComboBox comboMirrorDirection;
    private JLabel lblTextureAtlasFile;
    private JSpinner spinToolDupNumber;
    private JTextField tfToolDupXOffset;
    private JTextField tfToolDupYOffset;
    private JTextField tfToolDupRotation;
    private JButton btnToolDup;

    private SkrGdxAppPhysModelEditor gApp;
    private String currentModelFileName = "";
    private PhysModel model = null;

    private DefaultTableModel emptyTableModel = new DefaultTableModel();
    private PhysModelPropertiesTableModel physModelPropertiesTableModel;
    private AagPropertiesTableModel aagPropertiesTableModel;
    private BodyPropertiesTableModel bodyPropertiesTableModel;
    private PropertiesCellEditor propertiesCellEditor;
    private FixtureSetPropertiesTableModel fixtureSetPropertiesTableModel;
    private JointPropertiesTableModel jointPropertiesTableModel;

    private JointItemDescription jiDesc;
    private ModelTreeNode jointsGroupNode;
    private ModelTreeNode bodiesGroupNode;

    private EditorScreen editorScreen;

    private String textureAtlasFilePath = "";

    MainGui() {

        spinToolDupNumber.setModel( new SpinnerNumberModel(1,0,9999,1) );

        for ( EditorScreen.SelectionMode m : EditorScreen.SelectionMode.values() )
            comboSelectionMode.addItem( m );

        for (PhysModelProcessing.MirrorDirection d : PhysModelProcessing.MirrorDirection.values() )
            comboMirrorDirection.addItem( d );

        setGuiElementEnable( panelSimulationControls, false);

        gApp = new SkrGdxAppPhysModelEditor();
        final LwjglAWTCanvas gdxCanvas = new LwjglAWTCanvas( gApp );

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(rootPanel);
        gdxPanel.add(gdxCanvas.getCanvas(), BorderLayout.CENTER);



        pack();
        setSize(1280, 800);

        MainGuiWindowListener guiWindowListener = new MainGuiWindowListener();
        addWindowListener( guiWindowListener );

        gdxPanel.requestFocusInWindow();

        treePhysModel.setModel( new DefaultTreeModel( null ));

        physModelPropertiesTableModel = new PhysModelPropertiesTableModel( treePhysModel );
        aagPropertiesTableModel = new AagPropertiesTableModel( treePhysModel );
        bodyPropertiesTableModel = new BodyPropertiesTableModel( treePhysModel );
        fixtureSetPropertiesTableModel = new FixtureSetPropertiesTableModel( treePhysModel );
        jointPropertiesTableModel = new JointPropertiesTableModel( treePhysModel );


        fixtureSetPropertiesTableModel.setShapeTypeListener( new FixtureSetPropertiesTableModel.ShapeTypeListener() {
            @Override
            public void changed(FixtureSet fixtureSet) {
                updateShapeEditorFeatures( fixtureSet );
                SkrGdxAppPhysModelEditor.get().getEditorScreen().setModelObject( fixtureSet );
            }
        });

        JTableHeader th = tableProperties.getTableHeader();
        panelProperties.add(th, BorderLayout.NORTH);
        propertiesCellEditor = new PropertiesCellEditor();
        tableProperties.setDefaultEditor(
                PropertiesBaseTableModel.Property.class,
                propertiesCellEditor );
        tableProperties.setDefaultRenderer(PropertiesBaseTableModel.Property.class,
                new PropertiesTableCellRenderer() );

        ApplicationSettings.load();
        uploadGuiFromSettings();

        gApp.setChangeAtlasListener( new SkrGdxApplication.ChangeAtlasListener() {
            @Override
            public void atlasUpdated(TextureAtlas atlas) {
                onAtlasLoaded( atlas );
            }
        });

        treePhysModel.getSelectionModel().setSelectionMode( TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION );
        treePhysModel.setShowsRootHandles( true );


        setGuiElementEnable(mainSplitPanel, false);
        tabbedPaneEditors.removeAll();


        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                editorScreen = SkrGdxAppPhysModelEditor.get().getEditorScreen();

                editorScreen.getActorController().setControlPointListener(
                        new Controller.controlPointListener() {
                            @Override
                            public void changed(Object controlledObject, Controller.ControlPoint controlPoint) {
                                actorChangedByController((Actor) controlledObject);
                            }
                        }
                );

                editorScreen.setItemSelectionListener( new EditorScreen.ItemSelectionListener() {
                    @Override
                    public void singleItemSelected(Object object) {
                        processSingleItemSelection( object );
                    }

                    @Override
                    public void itemAddedToSelection(Object object, boolean removed) {
                        processItemSelection( object, removed );
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

                editorScreen.getBodyItemController().setControlPointListener(
                        new Controller.controlPointListener() {
                            @Override
                            public void changed(Object controlledObject, Controller.ControlPoint controlPoint) {
                                bodyItemChangedByController((BodyItem) controlledObject);
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

                final BodyItemController ctrl = editorScreen.getBodyItemController();
                ctrl.setControlPointListener( new Controller.controlPointListener() {
                    @Override
                    public void changed(Object controlledObject, Controller.ControlPoint controlPoint) {
                        onBodyItemControllerCenterChanged( ctrl );
                    }
                });
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

        for (JointDef.JointType jt : JointDef.JointType.values() ) {
            comboJointType.addItem( jt );
        }

        uploadTextureAtlas();

        modelToGui();

        // ====================================================================================

        treePhysModel.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                processTreeSelection(e);
            }
        });
        btnAddNode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNode();
            }
        });
        btnRemNode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processNodesRemoving();
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
                processTreeNodeDuplication();
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
                selectBodyItemToAnchorPointcController( 0 );
            }
        });
        comboBodyBSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectBodyItemToAnchorPointcController(1);
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

        createMenu();
        btnToolDup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processArrayDuplication();
            }
        });
    }


    void createMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("Model");

        JMenuItem mnuItem = new JMenuItem("New");
        mnuItem.addActionListener( new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newModel();
            }
        });
        menu.add( mnuItem );

        mnuItem = new JMenuItem("Load");
        mnuItem.addActionListener( new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadModel();
            }
        });
        menu.add(mnuItem);


        mnuItem = new JMenuItem("Save");
        mnuItem.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveModel( false );
            }
        });
        menu.add(mnuItem);


        mnuItem = new JMenuItem("Save As ...");
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

        menuBar.add( menu );

        setJMenuBar(menuBar);
    }

    void uploadGuiFromSettings() {
        textureAtlasFilePath = ApplicationSettings.get().getTextureAtlasFile();
        lblTextureAtlasFile.setText( "Texture Atlas File: " + textureAtlasFilePath );
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

    void onAtlasLoaded( TextureAtlas atlas ) {

        if ( model != null )
            model.uploadAtlas();
    }

    void newModel() {


        PhysWorld.clearPrimaryWorld();

        model = new PhysModel( PhysWorld.getPrimaryWorld(), SkrGdxApplication.get().getAtlas()  );
        model.setName("noname");
        model.uploadAtlas();

        currentModelFileName = "";

        setGuiElementEnable(mainSplitPanel, true);

        modelToGui();
    }



    void loadModel() {

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

        PhysWorld.clearPrimaryWorld();
        model = PhysModel.loadFromFile( Gdx.files.absolute( fl.getAbsolutePath()), SkrGdxApplication.get().getAtlas()  );
        model.uploadAtlas();

        setGuiElementEnable(mainSplitPanel, true);

        currentModelFileName = fl.getAbsolutePath();

        modelToGui();


    }



    void saveModel(boolean saveAs) {
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

    }



    void modelToGui() {
        if ( editorScreen == null )
            return;
        editorScreen.setModel( model );
        loadTree();
        physModelPropertiesTableModel.setModel(model);
        updateJointCombos();
        setTitle("PhysModel file: " + currentModelFileName);
    }

    void selectNode( DefaultMutableTreeNode node ) {
        TreePath newPath = new TreePath(node.getPath());
        treePhysModel.setSelectionPath(new TreePath(node.getPath()));
        treePhysModel.scrollPathToVisible(newPath);
        processTreeSelection( null );
    }

    void addNode() {

        if ( model == null )
            return;

        if ( treePhysModel.getLastSelectedPathComponent() == null )
            return;
        DefaultTreeModel md = (DefaultTreeModel) treePhysModel.getModel();

        TreePath [] selectedPaths = treePhysModel.getSelectionPaths();

        if ( selectedPaths.length > 1 )
            return;

        ModelTreeNode node = (ModelTreeNode) treePhysModel.getLastSelectedPathComponent();

        ModelTreeNode newNode = null;
        switch ( node.getType()) {
            case ROOT:
                if ( model.getBackgroundActor() == null ) {
                        newNode = addNewAag( node );
                } else {
                    return;
                }
                break;
            case AAG:
                newNode = addNewAag( node );
                break;
            case BODY_ITEM:
                BodyItem bi = ( BodyItem ) node.getUserObject();
                if ( bi.getAagBackground() != null ) {
                    newNode = addNewFixtureSet( node );
                } else {
                    int res = showBodyAddNodeSelectorDialog();
                    if ( res == 0 ) {
                        newNode = addNewAag(node);
                    } else if ( res == 1) {
                        newNode = addNewFixtureSet( node );
                    }
                }
                break;
            case BODIES_GROUP:
                newNode = addNewBodyItem(node);
                break;
            case FIXTURE_SET:
                return;
            case JOINT_ITEM:
                break;
            case JOINTS_GROUP:
                break;
            default:
                tableProperties.setModel( null );
        }

        md.nodeChanged( node );
        md.nodeStructureChanged(node);

        if ( newNode != null ) {
            selectNode( newNode );
        }
    }

    ModelTreeNode addNewBodyItem(ModelTreeNode parentNode) {
        BodyItem bi = model.addBodyItem("noname");
        ModelTreeNode newNode = new ModelTreeNode(ModelTreeNode.Type.BODY_ITEM, bi);
        parentNode.add( newNode );
        return newNode;

    }


    ModelTreeNode addNewAag( ModelTreeNode parentNode ) {

        AnimatedActorGroup newAg;

        ModelTreeNode newNode = null;

        switch ( parentNode.getType() ) {
            case ROOT:
                newAg = new AnimatedActorGroup( SkrGdxApplication.get().getAtlas()  );
                newAg.setName("noname");
                model.setBackgroundActor(newAg);
                newNode = new ModelTreeNode(ModelTreeNode.Type.AAG, newAg);
                parentNode.add( newNode );
                break;

            case AAG:
                if ( parentNode.getUserObject() == null )
                    return null;
                AnimatedActorGroup parentAg = (AnimatedActorGroup) parentNode.getUserObject();
                newAg = new AnimatedActorGroup( SkrGdxApplication.get().getAtlas()  );
                newAg.setName( "noname" );
                parentAg.addChild( newAg );
                newNode = new ModelTreeNode(ModelTreeNode.Type.AAG, newAg );
                parentNode.add( newNode );
                break;
            case BODY_ITEM:
                if ( parentNode.getUserObject() == null )
                    return null;
                BodyItem bi = (BodyItem) parentNode.getUserObject();

                if ( bi.getAagBackground() != null )
                    return null;
                newAg = new AnimatedActorGroup( SkrGdxApplication.get().getAtlas()  );
                newAg.setName( "noname" );
                bi.setAagBackground( newAg );
                newNode = new ModelTreeNode(ModelTreeNode.Type.AAG, newAg );
                parentNode.add( newNode );
                break;
        }
        return newNode;
    }

    ModelTreeNode addNewFixtureSet(ModelTreeNode parentNode) {
        BodyItem bi = ( BodyItem ) parentNode.getUserObject();
        FixtureSet fs = bi.addNewFixtureSet("noname");
        ModelTreeNode nn = new ModelTreeNode(ModelTreeNode.Type.FIXTURE_SET, fs);
        parentNode.add( nn );
        return nn;
    }

    int showRootAddNodeSelectorDialog() {

        Object [] options = { "Background Actor ", "Body" };

        int n = JOptionPane.showOptionDialog( this, "Select new node ", "Node Selector",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                options, options[1] );
        return n;
    }

    int showBodyAddNodeSelectorDialog() {
        Object [] options = { "Background Actor ", "Fixture Set" };

        int n = JOptionPane.showOptionDialog( this, "Select new node ", "Node Selector",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                options, options[1] );
        return n;
    }

    int showRemoveNodeYesNoDialog() {
        int n = JOptionPane.showOptionDialog(this, "Are you shure ?", "Remove Node",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null );
        Gdx.app.log("MainGui.showRemoveNodeYesNoDialog", "Res: " + n);
        return n;
    }

    void processNodesRemoving() {

        if ( model == null )
            return;

        if ( showRemoveNodeYesNoDialog() > 0 )
            return;


        TreePath [] selectedPaths = treePhysModel.getSelectionPaths();

        ModelTreeNode parentNode = null;

        for ( TreePath tp : selectedPaths ) {
            ModelTreeNode node = (ModelTreeNode) tp.getLastPathComponent();
            parentNode = removeNode( node );
        }

        cleanupJointsGroup();
        if ( parentNode != null )
            selectNode( parentNode );
    }


    ModelTreeNode removeNode( ModelTreeNode node ) {
        ModelTreeNode parentNode = (ModelTreeNode) node.getParent();
        DefaultTreeModel md = (DefaultTreeModel) treePhysModel.getModel();

        switch ( node.getType() ) {

            case ROOT:
                return null;
            case AAG:

                if ( parentNode.getType() == ModelTreeNode.Type.ROOT ) {
                    model.setBackgroundActor(null);
                } else if ( parentNode.getType() == ModelTreeNode.Type.AAG ) {
                    AnimatedActorGroup parentAg = (AnimatedActorGroup) parentNode.getUserObject();
                    AnimatedActorGroup ag = (AnimatedActorGroup) node.getUserObject();
                    parentAg.removeChild( ag );
                } else if ( parentNode.getType() == ModelTreeNode.Type.BODY_ITEM) {
                    BodyItem bi = ( BodyItem ) parentNode.getUserObject();
                    bi.removeAagBackground( );
                }

                break;
            case BODY_ITEM: {
                BodyItem bi = (BodyItem) node.getUserObject();
                model.removeBody(bi);
                break;
            }

            case BODIES_GROUP:
                return null;
            case FIXTURE_SET: {
                BodyItem bi = (BodyItem) parentNode.getUserObject();
                bi.removeFixtureSet( (FixtureSet) node.getUserObject() );
                break;
            }
            case JOINT_ITEM:
                JointItem ji = (JointItem) node.getUserObject();
                model.removeJointItem( ji );
                updateJointCombos();
                break;
            case JOINTS_GROUP:
                return null;
        }
        parentNode.remove( node );
        md.nodeStructureChanged( parentNode );
        return parentNode;
    }

    void cleanupJointsGroup() {

        Array< ModelTreeNode> removeList = new Array<ModelTreeNode>();

        int c = jointsGroupNode.getChildCount();
        for ( int i = 0; i < c; i++) {
            ModelTreeNode node = (ModelTreeNode) jointsGroupNode.getChildAt(i);
            if ( node.getType() != ModelTreeNode.Type.JOINT_ITEM )
                continue;
            JointItem ji = (JointItem) node.getUserObject();
            if ( model.getJointItems().contains( ji, true ))
                continue;
            removeList.add( node );
        }

        for ( ModelTreeNode nd : removeList ) {
            jointsGroupNode.remove(nd);
        }

        DefaultTreeModel md = (DefaultTreeModel) treePhysModel.getModel();
        md.nodeStructureChanged( jointsGroupNode );
    }

    void processSingleItemSelection ( Object object ) {
        DefaultTreeModel md = (DefaultTreeModel) treePhysModel.getModel();
        ModelTreeNode tn = findNode((ModelTreeNode) md.getRoot(), object );
        if ( tn == null )
            return;
        TreePath tp = new TreePath( tn.getPath() );
        treePhysModel.setSelectionPath(tp);
        treePhysModel.scrollPathToVisible( tp );
        processTreeSelection(null);
    }

    void processItemSelection(Object object, boolean removed ) {
        DefaultTreeModel md = (DefaultTreeModel) treePhysModel.getModel();
        ModelTreeNode tn = findNode((ModelTreeNode) md.getRoot(), object );
        if ( tn == null )
            return;
        TreePath tp = new TreePath( tn.getPath() );
        if ( !removed ) {
            treePhysModel.addSelectionPath(tp);
            treePhysModel.scrollPathToVisible( tp );
        } else {
            treePhysModel.removeSelectionPath( tp );
        }

        processTreeSelection(null);
    }

    ModelTreeNode findNode(ModelTreeNode parentNode, Object object ) {
        if ( parentNode.getUserObject() == object ) {
            return parentNode;
        }

        for ( int index = 0; index < parentNode.getChildCount(); index ++ ) {
            ModelTreeNode tn = (ModelTreeNode) parentNode.getChildAt( index );
            tn = findNode( tn, object);
            if ( tn != null )
                return tn;
        }
        return null;
    }


    void processTreeSelection( TreeSelectionEvent e) {

        propertiesCellEditor.cancelCellEditing();
        tabbedPaneEditors.removeAll();


        tableProperties.setModel( emptyTableModel );
        setGuiElementEnable(panelShapeEditor, false);

        TreePath[] selectionPaths = treePhysModel.getSelectionPaths();

        if ( selectionPaths == null )
            return;

        if ( selectionPaths.length > 1 ) {
            processTreeMultiSelection();
            return;
        }

        ModelTreeNode node = (ModelTreeNode) treePhysModel.getLastSelectedPathComponent();
        DefaultTreeModel md = (DefaultTreeModel) treePhysModel.getModel();

        if ( node == null )
            return;

        if ( node.getUserObject() == null )
            return;

        switch ( node.getType() ) {
            case ROOT:
                tableProperties.setModel( physModelPropertiesTableModel );
                tabbedPaneEditors.add( "Tools", panelTools );
                setGuiElementEnable( panelTools, true);
                break;

            case AAG:
                aagPropertiesTableModel.setAag((AnimatedActorGroup) node.getUserObject());
                tableProperties.setModel( aagPropertiesTableModel );
                tableProperties.updateUI();
                tabbedPaneEditors.add( "Tools", panelTools );
                setGuiElementEnable( panelTools, true);
                break;

            case BODY_ITEM:
                BodyItem bi = ( BodyItem ) node.getUserObject();
                bodyPropertiesTableModel.setBodyItem( bi );
                tableProperties.setModel( bodyPropertiesTableModel );
                tableProperties.updateUI();
                setGuiElementEnable( panelBodyItemEditor, true);
                tabbedPaneEditors.add("Body Editor", panelBodyItemEditor);
                chbEnableMassCorrection.setSelected( false );
                tabbedPaneEditors.add( "Tools", panelTools );
                setGuiElementEnable( panelTools, true);
                break;

            case BODIES_GROUP:
                break;
            case FIXTURE_SET:
                FixtureSet fs = ( FixtureSet ) node.getUserObject();
                fixtureSetPropertiesTableModel.setFixtureSet( fs );
                tableProperties.setModel( fixtureSetPropertiesTableModel );
                tableProperties.updateUI();
                tabbedPaneEditors.add( "Shape Editor", panelShapeEditor );
                setGuiElementEnable( panelShapeEditor, true );
                updateShapeEditorFeatures( fs );
                tabbedPaneEditors.add( "Tools", panelTools );
                setGuiElementEnable( panelTools, true);
                break;

            case JOINT_ITEM:
                JointItem ji = (JointItem) node.getUserObject();
                jointPropertiesTableModel.setJointItem( ji );
                tableProperties.setModel( jointPropertiesTableModel );
                tableProperties.updateUI();
                break;
            case JOINTS_GROUP:
                tabbedPaneEditors.add("Joint creator", panelJointCreator );
                updateJointCreatorGui();
                setGuiElementEnable( panelJointCreator, true);
                updateJointCreatorPanelFeatures();
                break;
            default:
        }

        editorScreen.setModelObject(node.getUserObject() );
    }


    void processTreeMultiSelection() {
        TreePath[] selectionPaths = treePhysModel.getSelectionPaths();
        ModelTreeNode baseNode = (ModelTreeNode) selectionPaths[0].getLastPathComponent();

        Array< TreePath > disabledPaths = new Array<TreePath>();

        for ( TreePath tp : selectionPaths ) {
            ModelTreeNode node = (ModelTreeNode) tp.getLastPathComponent();
            if ( node.getType() != baseNode.getType() ) {
                disabledPaths.add( tp );
            }
        }

        for ( TreePath tp : disabledPaths ) {
            treePhysModel.removeSelectionPath( tp );
        }

        editorScreen.clearSelectedItems();

        for ( TreePath tp : treePhysModel.getSelectionPaths() ) {
            ModelTreeNode node = (ModelTreeNode) tp.getLastPathComponent();
            editorScreen.addModelObject( node.getUserObject() );
        }

    }

    void processTreeNodeDuplication() {

        if ( model == null )
            return;

        propertiesCellEditor.cancelCellEditing();
        tabbedPaneEditors.removeAll();

        setGuiElementEnable(panelShapeEditor, false);

        TreePath [] selectedPaths = treePhysModel.getSelectionPaths();

        ModelTreeNode newNode = null;

        for (TreePath tp : selectedPaths ) {
            ModelTreeNode node = (ModelTreeNode) tp.getLastPathComponent();
            newNode = duplicateNode( node );
        }

        ModelTreeNode node = (ModelTreeNode) treePhysModel.getLastSelectedPathComponent();

        if ( node == null )
            return;

        if ( newNode != null ) {
            selectNode( newNode );
        }
    }


    private ModelTreeNode duplicateNode( ModelTreeNode node ) {
        if ( node.getUserObject() == null )
            return null;

        ModelTreeNode parentNode = (ModelTreeNode) node.getParent();
        DefaultTreeModel md = (DefaultTreeModel) treePhysModel.getModel();
        ModelTreeNode rootNode = (ModelTreeNode) md.getRoot();

        ModelTreeNode newNode = null;

        switch ( node.getType() ) {
            case ROOT:
                return null;
            case AAG:

                if ( parentNode.getType() != ModelTreeNode.Type.AAG )
                    return null;
                AnimatedActorGroup pAag = (AnimatedActorGroup) parentNode.getUserObject();
                AnimatedActorGroup aag = (AnimatedActorGroup) node.getUserObject();
                AagDescription aagDesc = aag.getDescription();
                aagDesc.setName( aagDesc.getName() + "Cpy" );
                AnimatedActorGroup newAag = new AnimatedActorGroup( aagDesc, SkrGdxApplication.get().getAtlas()  );
                pAag.addChild( newAag );
                newNode = createTreeAagNode( parentNode, newAag );
                break;

            case BODY_ITEM:

                BodyItem bi = (BodyItem) node.getUserObject();
                BodyItemDescription bd = bi.createBodyItemDescription();
                bd.setName( bd.getName() + "Cpy");
                bd.setId( -1 );
                BodyItem dupBi = model.addBodyItem( bd );
                newNode = createTreeBodyItemNode(dupBi);

                break;
            case FIXTURE_SET:

                BodyItem bodyItem = (BodyItem) parentNode.getUserObject();
                FixtureSet fs = (FixtureSet) node.getUserObject();
                FixtureSetDescription fd = fs.getDescription();
                FixtureSet newFs = bodyItem.addNewFixtureSet( fd );
                newNode = createTreeFixtureSetNode( parentNode, newFs );

                break;
            case JOINT_ITEM:
                return null;


        }

        if ( parentNode != null ) {
            md.nodeChanged(parentNode);
            md.nodeStructureChanged(parentNode);
        }

        return newNode;
    }

    private void setGuiElementEnable(Container c, boolean state) {

        Component [] cl = c.getComponents();

        for ( int i = 0; i < cl.length; i++) {

            if ( cl[i] instanceof Container) {
                setGuiElementEnable((Container) cl[i], state);
            } else {
                cl[i].setEnabled( state );
            }

        }

        c.setEnabled( state );
    }

    private ModelTreeNode createTreeAagNode( ModelTreeNode parent, AnimatedActorGroup aag ) {

        ModelTreeNode aagNode = new ModelTreeNode( ModelTreeNode.Type.AAG, aag );

        for ( int i = 0; i < aag.getChildrenCount(); i++) {
            createTreeAagNode( aagNode, aag.getChild(i) );
        }

        parent.add( aagNode );

        return aagNode;
    }

    private ModelTreeNode createTreeFixtureSetNode( ModelTreeNode bodyNode, FixtureSet fs ) {
        ModelTreeNode fsNode = new ModelTreeNode(ModelTreeNode.Type.FIXTURE_SET, fs);
        bodyNode.add( fsNode );
        return fsNode;
    }

    private ModelTreeNode createTreeBodyItemNode(BodyItem bi) {

        ModelTreeNode bodyNode = new ModelTreeNode(ModelTreeNode.Type.BODY_ITEM, bi );

        if ( bi.getAagBackground() != null ) {
            createTreeAagNode( bodyNode, bi.getAagBackground() );
        }

        for ( FixtureSet fs: bi.getFixtureSets() ) {
            createTreeFixtureSetNode( bodyNode, fs );
        }

        bodiesGroupNode.add( bodyNode );

        return bodyNode;
    }

    private DefaultMutableTreeNode createTreeJointItemNode( JointItem ji ) {
        ModelTreeNode jiNode = new ModelTreeNode(ModelTreeNode.Type.JOINT_ITEM, ji );
        if ( ji.getAagBackground() != null ) {
            createTreeAagNode( jiNode, ji.getAagBackground() );
        }
        jointsGroupNode.add( jiNode );
        return jiNode;
    }

    private void loadTree() {

        if ( model == null ) {
            treePhysModel.setModel( new DefaultTreeModel( null ));
            return;
        }

        ModelTreeNode root = new ModelTreeNode( ModelTreeNode.Type.ROOT, model );
        treePhysModel.setModel(new DefaultTreeModel(root));

        if ( model.getBackgroundActor() != null ) {
            createTreeAagNode(root, model.getBackgroundActor());
        }

        bodiesGroupNode = new ModelTreeNode(ModelTreeNode.Type.BODIES_GROUP, model.getBodyItems() );
        root.add( bodiesGroupNode );

        for ( BodyItem bi : model.getBodyItems() ) {
            createTreeBodyItemNode(bi);
        }

        jointsGroupNode = new ModelTreeNode(ModelTreeNode.Type.JOINTS_GROUP, model.getJointItems() );
        root.add( jointsGroupNode );


        for ( JointItem ji : model.getJointItems() ) {
            createTreeJointItemNode( ji );
        }
    }

    private void bodyItemChangedByController( BodyItem bodyItem ) {
        bodyPropertiesTableModel.bodyItemChanged( bodyItem );
    }

    private void actorChangedByController(Actor actor) {
        if ( actor instanceof AnimatedActorGroup )
            aagPropertiesTableModel.actorChanged( (AnimatedActorGroup) actor );
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

        FixtureSet fs = fixtureSetPropertiesTableModel.getFixtureSet();
        fs.createFixtures( controller.getFixtureSetDescription().getShapeDescriptions() );
        fixtureSetPropertiesTableModel.fireTableDataChanged();
    }

    void shapeControlPointChanged( ShapeDescription shapeDescription, Controller.ControlPoint cp ) {

        float x = PhysWorld.get().toPhys( cp.getX() );
        float y = PhysWorld.get().toPhys( cp.getY() );
        tfControlPointX.setText( String.valueOf( x ) );
        tfControlPointY.setText( String.valueOf( y ) );

        if ( fixtureSetPropertiesTableModel.getFixtureSet().getShapeType() == Shape.Type.Circle ) {
            tfRadius.setText( String.valueOf( shapeDescription.getRadius() ) );
        }
    }

    void shapePositionChanged( ShapeDescription shapeDescription ) {
        // does nothing
    }

    void shapeRadiusChanged( ShapeDescription shapeDescription ) {
        tfRadius.setText( String.valueOf( shapeDescription.getRadius() ) );
    }

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

        for ( BodyItem bi : model.getBodyItems() ) {
            comboBodyASelector.addItem( bi );
            comboBodyBSelector.addItem( bi );
        }

        if ( selA != null && model.getBodyItems().contains( selA, true))
            comboBodyASelector.setSelectedItem( selA );

        if ( selB != null && model.getBodyItems().contains( selB, true))
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
        jiDesc.setType((JointDef.JointType) comboJointType.getSelectedItem() );

        BodyItem biA = (BodyItem) comboBodyASelector.getSelectedItem();
        BodyItem biB = (BodyItem) comboBodyBSelector.getSelectedItem();

        if ( biA == null )
            return;
        if ( biB == null )
            return;
        if ( biA == biB )
            return;

        jiDesc.setBodyAId( biA.getId() );
        jiDesc.setBodyBId( biB.getId() );
        jiDesc.setCollideConnected(chbCollideConnected.isSelected());

        setAnchorPointFromGui(JointCreatorController.AnchorControlPoint.AcpType.typeA);
        setAnchorPointFromGui(JointCreatorController.AnchorControlPoint.AcpType.typeB);
        if ( jiDesc.getType() == JointDef.JointType.PulleyJoint ) {
            setAnchorPointFromGui(JointCreatorController.AnchorControlPoint.AcpType.typeC);
            setAnchorPointFromGui(JointCreatorController.AnchorControlPoint.AcpType.typeD);
        } else if ( jiDesc.getType() == JointDef.JointType.PrismaticJoint ) {
            setAnchorPointFromGui(JointCreatorController.AnchorControlPoint.AcpType.typeAxis);
        }

        try {
            jiDesc.setRatio(Float.valueOf(tfRatio.getText()));
        } catch (NumberFormatException e) {
            Gdx.app.error("MainGui.createJoint", "Ratio: " + e.getMessage() );
            jiDesc.setRatio(1);
        }


        jiDesc.setName("_" + jiDesc.getType());
        if ( jiDesc.getType() == JointDef.JointType.GearJoint ) {
            JointItem jiA = (JointItem) comboJoint1.getSelectedItem();
            JointItem jiB = (JointItem) comboJoint2.getSelectedItem();
            if ( jiA == null )
                return;
            if ( jiB == null )
                return;
            jiDesc.setJointAId( jiA.getId() );
            jiDesc.setJointBId( jiB.getId() );
        }

        JointItem ji = model.addNewJointItem( jiDesc );
        if ( ji == null)
            return;

        DefaultTreeModel md = (DefaultTreeModel) treePhysModel.getModel();
        ModelTreeNode newNode = new ModelTreeNode(ModelTreeNode.Type.JOINT_ITEM, ji );
        jointsGroupNode.add( newNode );
        md.nodeChanged( jointsGroupNode );
        md.nodeStructureChanged(jointsGroupNode);
        updateJointCombos();

        selectNode( newNode );

    }

    void updateJointCombos() {

        comboJoint1.removeAllItems();
        comboJoint2.removeAllItems();

        if ( model == null )
            return;

        for (JointItem ji : model.getJointItems() ) {
            if ( ji.getJoint().getType() == JointDef.JointType.RevoluteJoint ) {
                comboJoint1.addItem( ji );
                comboJoint2.addItem( ji );
                continue;
            }

            if ( ji.getJoint().getType() == JointDef.JointType.PrismaticJoint ) {
                comboJoint2.addItem( ji );
            }
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
                    PhysModel.Description description = model.getDescription();
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
                boolean state = chbDebugRender.isSelected();
                SkrGdxAppPhysModelEditor.get().getEditorScreen().setDoDebugRender(state);
                SkrGdxAppPhysModelEditor.get().getSimulationScreen().setDoDebugRender(state);
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

    void onBodyItemControllerCenterChanged( BodyItemController controller ) {
        BodyItem bi = controller.getBodyItem();

        Vector2 cntr = bi.getBody().getWorldCenter();

        tfMassCenterWorldX.setText("" + cntr.x );
        tfMassCenterWorldY.setText("" + cntr.y );

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

    void selectBodyItemToAnchorPointcController( int selId ) {
        JointCreatorController ctrlr = SkrGdxAppPhysModelEditor.get().getEditorScreen().getJointCreatorController();
        if ( selId == 0 ) {
            ctrlr.setSelectedBodyItemA((BodyItem) comboBodyASelector.getSelectedItem());
        } else {
            ctrlr.setSelectedBodyItemB((BodyItem) comboBodyBSelector.getSelectedItem());
        }
    }

    void changeSelectionMode() {
        EditorScreen.SelectionMode selMode = (EditorScreen.SelectionMode) comboSelectionMode.getSelectedItem();
        editorScreen.setSelectionMode( selMode );
    }

    void processMirroring() {

        if ( model == null )
            return;


        TreePath [] selectedPaths = treePhysModel.getSelectionPaths();
        PhysModelProcessing.MirrorDirection dir = (PhysModelProcessing.MirrorDirection) comboMirrorDirection.getSelectedItem();

        for ( TreePath tp : selectedPaths ) {
            ModelTreeNode node = (ModelTreeNode) tp.getLastPathComponent();

            switch ( node.getType() ) {
                case ROOT:
                    mirrorModel( dir );
                    return;
                case AAG:
                    mirrorAag( node, dir);
                    break;
                case BODY_ITEM:
                    mirrorBodyItem( node, dir );
                    break;
                case BODIES_GROUP:
                    break;
                case FIXTURE_SET:
                    mirrorFixtureSet( node, dir );
                    break;
                case JOINT_ITEM:
                    break;
                case JOINTS_GROUP:
                    break;
            }
        }
    }

    void mirrorAag( ModelTreeNode node , PhysModelProcessing.MirrorDirection dir ) {
        AnimatedActorGroup aag = (AnimatedActorGroup) node.getUserObject();
        AagDescription desc = aag.getDescription();
        PhysModelProcessing.mirrorAagDescription( desc, dir);
        aag.uploadFromDescription( desc, SkrGdxApplication.get().getAtlas() );
    }

    void mirrorBodyItem( ModelTreeNode node, PhysModelProcessing.MirrorDirection dir ) {
        BodyItem bi = (BodyItem) node.getUserObject();
        BodyItemDescription desc = bi.createBodyItemDescription();
        PhysModelProcessing.mirrorBodyItemDescription( desc, dir );
        model.removeBody( bi );
        bi = model.addBodyItem( desc );
        node.setUserObject( bi );
        cleanupJointsGroup();
    }

    void mirrorFixtureSet( ModelTreeNode node, PhysModelProcessing.MirrorDirection dir ) {
        FixtureSet fs = (FixtureSet) node.getUserObject();
        FixtureSetDescription fsDesc = fs.getDescription();

        PhysModelProcessing.mirrorFixtureSetDescription( fsDesc, dir );

        BodyItem bi = fs.getBodyItem();
        bi.removeFixtureSet( fs );
        fs = bi.addNewFixtureSet( fsDesc );

        node.setUserObject( fs );
    }

    void mirrorModel( PhysModelProcessing.MirrorDirection dir ) {
        PhysModel.Description desc = model.getDescription();

        PhysModelProcessing.mirrorModelDescription(desc, dir);

        PhysWorld.clearPrimaryWorld();
        model = new PhysModel( desc,PhysWorld.getPrimaryWorld(), SkrGdxApplication.get().getAtlas() );

        modelToGui();
    }

    void exportSelection() {

        TreePath [] selectedPaths = treePhysModel.getSelectionPaths();

        if ( selectedPaths == null )
            return;


        PhysModel.Description desc = new PhysModel.Description();
        desc.setName( model.getName() + "-export");
        desc.setUuid( UUID.randomUUID() );

        Array<Integer> exportedId = new Array<Integer>();

        for( TreePath tp : selectedPaths ) {
            ModelTreeNode tn = (ModelTreeNode) tp.getLastPathComponent();
            switch ( tn.getType() ) {
                case ROOT:
                    return;
                case AAG:
                    return;
                case BODY_ITEM:
                    BodyItem bi = (BodyItem) tn.getUserObject();
                    BodyItemDescription bid = bi.createBodyItemDescription();
                    desc.getBodyDescriptions().add( bid );
                    exportedId.add( bi.getId() );
                    break;
                case BODIES_GROUP:
                    return;
                case FIXTURE_SET:
                    return;
                case JOINT_ITEM:
                    return;
                case JOINTS_GROUP:
                    return;
            }
        }

        for ( JointItem ji : model.getJointItems() ) {
            int bAId = ji.getBodyAId();
            int bBId = ji.getBodyBId();
            if ( !exportedId.contains( bAId, false ) )
                continue;
            if ( !exportedId.contains( bBId, false ) )
                continue;
            JointItemDescription jid = ji.createJointItemDescription();
            desc.getJointsDesc().add( jid );
        }


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

        PhysModel.Description desc = PhysModel.loadModelDescription( Gdx.files.absolute( fl.getAbsolutePath()) );

        HashMap<Integer, Integer > idRemapTable = new HashMap<Integer, Integer>();

        for ( BodyItemDescription bid : desc.getBodyDescriptions() ) {
            int id = bid.getId();
            int newId = BodyItem.genNextId( -1 );

            idRemapTable.put( id, newId );
            bid.setId( newId );

            BodyItem bi = model.addBodyItem( bid );

            createTreeBodyItemNode( bi );
        }

        HashMap< Integer, Integer > jidRemapTable = new HashMap<Integer, Integer>();

        for ( JointItemDescription jid : desc.getJointsDesc() ) {
            int id = jid.getId();
            int newId = JointItem.genNextId( -1 );
            jid.setId( newId );
            jidRemapTable.put( id, newId );

            int bId = jid.getBodyAId();
            if ( !idRemapTable.containsKey( bId) )
                continue;
            jid.setBodyAId( idRemapTable.get( bId) );

            bId = jid.getBodyBId();
            if ( !idRemapTable.containsKey( bId ) )
                continue;
            jid.setBodyBId( idRemapTable.get( bId ) );

            if ( jid.getType() == JointDef.JointType.GearJoint ) {
                int jId = jid.getJointAId();
                if ( !jidRemapTable.containsKey( jId ) )
                    continue;
                jid.setJointAId( jidRemapTable.get( jId ) );

                jId = jid.getJointBId();
                if ( ! jidRemapTable.containsKey( jId ) )
                    continue;
                jid.setJointBId( jidRemapTable.get( jId ) );
            }

            JointItem ji = model.addNewJointItem( jid );

            createTreeJointItemNode( ji );
        }
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

        TreePath [] selectedPaths = treePhysModel.getSelectionPaths();

        if ( selectedPaths == null )
            return;
        for ( TreePath tp : selectedPaths ) {
            ModelTreeNode node = (ModelTreeNode) tp.getLastPathComponent();
            switch (node.getType()) {
                case ROOT:
                    continue;
                case AAG:
                    break;
                case BODY_ITEM:
                    break;
                case BODIES_GROUP:
                    continue;
                case FIXTURE_SET:
                    break;
                case JOINT_ITEM:
                    continue;
                case JOINTS_GROUP:
                    continue;
            }

            duplicateNodeAsArray( node, number, xOffset, yOffset, rotation );
        }

    }


    void duplicateNodeAsArray( ModelTreeNode node, int number, float xOffset, float yOffset, float rotation ) {

        for ( int i = 0; i < number; i++ ) {

            ModelTreeNode newNode = duplicateNode( node );
            if ( newNode == null )
                continue;
            switch (newNode.getType()) {
                case ROOT:
                    continue;
                case AAG:
                    AnimatedActorGroup baseAag = (AnimatedActorGroup) node.getUserObject();
                    AnimatedActorGroup aag = (AnimatedActorGroup) newNode.getUserObject();
                    aag.setPosition( baseAag.getX() + i * xOffset, baseAag.getY() + i * yOffset );
                    aag.setRotation( baseAag.getRotation() + i * rotation );
                    break;
                case BODY_ITEM:
                    BodyItem baseBi = (BodyItem) node.getUserObject();
                    BodyItem bi = (BodyItem) newNode.getUserObject();
                    bi.getBody().setTransform(  baseBi.getBody().getPosition().x + i * xOffset,
                            baseBi.getBody().getPosition().y + i * yOffset,
                            baseBi.getBody().getAngle() + i * MathUtils.degreesToRadians * rotation );

                    break;
                case BODIES_GROUP:
                    continue;
                case FIXTURE_SET:
                    break;
                case JOINT_ITEM:
                    continue;
                case JOINTS_GROUP:
                    continue;
            }

        }

    }

    //TODO: implement Undo/Redo by Descriptions Array

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
