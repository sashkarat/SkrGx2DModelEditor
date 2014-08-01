package org.skr.PhysModelEditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import org.skr.PhysModelEditor.PropertiesTableElements.*;
import org.skr.PhysModelEditor.controller.*;
import org.skr.physmodel.*;
import org.skr.physmodel.animatedactorgroup.AagDescription;
import org.skr.physmodel.animatedactorgroup.AnimatedActorGroup;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.JTableHeader;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by rat on 30.05.14.
 */



public class MainGui extends JFrame {


    private JPanel rootPanel;
    private JPanel gdxPanel;
    private JTree treePhysModel;
    private JTabbedPane tabbedPaneEditors;
    private JButton btnNewModel;
    private JButton btnLoadModel;
    private JButton btnSaveModel;
    private JButton btnSaveModelAs;
    private JTextField tfTextureAtlasFile;
    private JButton btnSelectTextureFile;
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
    private JRadioButton rbControllerInput;
    private JRadioButton rbScreenInput;

    private GdxApplication gApp;
    private String currentModelFileName = "";
    private PhysModel model = null;

    private PhysModelPropertiesTableModel physModelPropertiesTableModel;
    private AagPropertiesTableModel aagPropertiesTableModel;
    private BodyPropertiesTableModel bodyPropertiesTableModel;
    private PropertiesCellEditor propertiesCellEditor;
    private FixtureSetPropertiesTableModel fixtureSetPropertiesTableModel;
    private JointPropertiesTableModel jointPropertiesTableModel;

    private JointItemDescription jiDesc;


    public static final class NodeInfo {
        public enum Type {
            ROOT, AAG, BODY_ITEM, FIXTURE_SET, JOINT_ITEM;
        }

        public Object object;
        public Type type;

        public NodeInfo(Object object, Type type) {
            this.object = object;
            this.type = type;
        }

        @Override
        public String toString() {

            switch (this.type) {
                case ROOT:
                    if ( object != null ) {
                        PhysModel model = (PhysModel) object;
                        return "Model: " + model.getName();
                    } else {
                        return "Model: ";
                    }
                case AAG:
                    if ( object != null ) {
                        AnimatedActorGroup ag = (AnimatedActorGroup) object;
                        return "Actor: " + ag.getName();
                    }
                case BODY_ITEM:
                    if ( object != null ) {
                        BodyItem bi = ( BodyItem ) object;
                        return "Body: " + bi.getName();
                    }
                case FIXTURE_SET:
                    FixtureSet fs = ( FixtureSet ) object;
                    return "FixtureSet: " + fs.getName();
                case JOINT_ITEM:
                    JointItem ji = (JointItem) object;
                    return "Joint: " + ji.getName();
            }

            return "";
        }
    }


    MainGui() {

        setGuiElementEnable( panelSimulationControls, false);

        gApp = new GdxApplication();
        final LwjglAWTCanvas gdxCanvas = new LwjglAWTCanvas( gApp );

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(rootPanel);
        gdxPanel.add(gdxCanvas.getCanvas(), BorderLayout.CENTER);
        pack();
        setSize(1280, 800);

        MainGuiWindowListener guiWindowListener = new MainGuiWindowListener();
        addWindowListener( guiWindowListener );

        gdxPanel.requestFocusInWindow();

        ButtonGroup group = new ButtonGroup();
        group.add( rbControllerInput );
        group.add( rbScreenInput );

        physModelPropertiesTableModel = new PhysModelPropertiesTableModel( treePhysModel );
        aagPropertiesTableModel = new AagPropertiesTableModel( treePhysModel );
        bodyPropertiesTableModel = new BodyPropertiesTableModel( treePhysModel );
        fixtureSetPropertiesTableModel = new FixtureSetPropertiesTableModel( treePhysModel );
        jointPropertiesTableModel = new JointPropertiesTableModel( treePhysModel );


        fixtureSetPropertiesTableModel.setShapeTypeListener( new FixtureSetPropertiesTableModel.ShapeTypeListener() {
            @Override
            public void changed(FixtureSet fixtureSet) {
                updateShapeEditorFeatures( fixtureSet );
                GdxApplication.get().getEditorScreen().setModelObject( fixtureSet );
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

        gApp.setChangeAtlasListener( new GdxApplication.ChangeAtlasListener() {
            @Override
            public void atlasUpdated(TextureAtlas atlas) {
                onAtlasLoaded( atlas );
            }
        });

        treePhysModel.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
        treePhysModel.setShowsRootHandles( true );


        setGuiElementEnable(mainSplitPanel, false);
        tabbedPaneEditors.removeAll();


        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                GdxApplication.get().getEditorScreen().getActorController().setControlPointListener(
                        new Controller.controlPointListener() {
                            @Override
                            public void changed(Object controlledObject, Controller.ControlPoint controlPoint) {
                                actorChangedByController((Actor) controlledObject);
                            }
                        }
                );
            }
        });


        Gdx.app.postRunnable( new Runnable() {
            @Override
            public void run() {
                GdxApplication.get().getEditorScreen().getBodyItemController().setControlPointListener(
                        new Controller.controlPointListener() {
                            @Override
                            public void changed(Object controlledObject, Controller.ControlPoint controlPoint) {
                                bodyItemChangedByController((BodyItem) controlledObject);
                            }
                        }
                );
            }
        });

        Gdx.app.postRunnable( new Runnable() {
            @Override
            public void run() {
                jiDesc = GdxApplication.get().getEditorScreen().getAnchorPointController().getDescription();
                GdxApplication.get().getEditorScreen().getAnchorPointController().setControlPointListener(
                        new Controller.controlPointListener() {
                            @Override
                            public void changed(Object controlledObject, Controller.ControlPoint controlPoint) {
                                loadAnchorPointsPosition();
                            }
                        });
            }
        });

        Gdx.app.postRunnable( new Runnable() {
            @Override
            public void run() {
                final BodyItemController ctrl = GdxApplication.get().getEditorScreen().getBodyItemController();
                ctrl.setControlPointListener( new Controller.controlPointListener() {
                    @Override
                    public void changed(Object controlledObject, Controller.ControlPoint controlPoint) {
                        onBodyItemControllerCenterChanged( ctrl );
                    }
                });

            }
        });

        Gdx.app.postRunnable( new Runnable() {
            @Override
            public void run() {
                GdxApplication.get().getEditorScreen().setBodyItemSelectionListener( new EditorScreen.BodyItemSelectionListener() {
                    @Override
                    public void bodyItemSelected(BodyItem bi) {
                        processBodyItemSelection( bi );
                    }
                });
            }
        });

        Gdx.app.postRunnable( new Runnable() {
            @Override
            public void run() {
                GdxApplication.get().getEditorScreen().getAnchorPointController().setBodyItemSelectionListener(
                        new AnchorPointController.BodyItemSelectionListener() {
                            @Override
                            public void bodySelected(BodyItem bi, int id) {
                                selectBodyItemToJointCreator( bi, id );
                            }
                        }
                );
            }
        });

        ShapeController.setStaticShapeControllerListener( new ShapeController.ShapeControllerListener() {
            @Override
            public void controlPointChanged(ShapeDescription shapeDescription, Controller.ControlPoint controlPoint) {
                shapeControlPointChanged( shapeDescription, controlPoint);
            }

            @Override
            public void positionChanged(ShapeDescription shapeDescription) {
                shapePositionChanged( shapeDescription );
            }

            @Override
            public void radiusChanged(ShapeDescription shapeDescription) {
                shapeRadiusChanged( shapeDescription );
            }
        });




        for (JointDef.JointType jt : JointDef.JointType.values() ) {
            comboJointType.addItem( jt );
        }

        uploadTextureAtlas();

        modelToGui();

        // ====================================================================================

        btnNewModel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newModel();
            }
        });
        btnLoadModel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadModel();
            }
        });
        btnSaveModel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveModel(false);
            }
        });
        btnSaveModelAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveModel(true);
            }
        });
        btnSelectTextureFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectTextureAtlasFile();
            }
        });

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
                removeNode();
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
                ShapeController shapeController = GdxApplication.get().getEditorScreen().getCurrentShapeController();
                if ( shapeController == null )
                    return;
                deleteShape( shapeController );
            }
        });
        btnSetControlPointPosition.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ShapeController shapeController = GdxApplication.get().getEditorScreen().getCurrentShapeController();
                if ( shapeController == null )
                    return;
                setControlPointPosition( shapeController );
            }
        });
        chbLooped.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ShapeController shapeController = GdxApplication.get().getEditorScreen().getCurrentShapeController();
                setLooped( chbLooped.isSelected(), shapeController );
            }
        });
        chbAutoTessellate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ShapeController shapeController = GdxApplication.get().getEditorScreen().getCurrentShapeController();
                setAutoTessellate( chbAutoTessellate.isSelected(), shapeController );
            }
        });
        btnUpdateFixtures.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ShapeController shapeController = GdxApplication.get().getEditorScreen().getCurrentShapeController();
                if ( shapeController == null )
                    return;
                updateFixtures( shapeController );
            }
        });
        btnSetRadius.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ShapeController shapeController = GdxApplication.get().getEditorScreen().getCurrentShapeController();
                if ( shapeController == null )
                    return;
                setRadius( shapeController );
            }
        });
        btnTessellatePolygon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ShapeController shapeController = GdxApplication.get().getEditorScreen().getCurrentShapeController();
                tessellatePolygon( shapeController );
            }
        });
        btnSetAnchorA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAnchorPointFromGui(AnchorPointController.AnchorControlPoint.AcpType.typeA );
            }
        });
        btnSetAnchorB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAnchorPointFromGui(AnchorPointController.AnchorControlPoint.AcpType.typeB);
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
                setAnchorPointFromGui( AnchorPointController.AnchorControlPoint.AcpType.typeAxis );
            }
        });
        btnSetGroundAnchorA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAnchorPointFromGui( AnchorPointController.AnchorControlPoint.AcpType.typeC );
            }
        });
        btnSetGroundAnchorB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAnchorPointFromGui( AnchorPointController.AnchorControlPoint.AcpType.typeD );
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
                selectBodyItemToAnchorPointcController( 1 );
            }
        });
    }


    void uploadGuiFromSettings() {
        tfTextureAtlasFile.setText( ApplicationSettings.get().getTextureAtlasFile() );
    }


    void selectTextureAtlasFile() {

        final JFileChooser fch = new JFileChooser();
        int res;

        fch.setCurrentDirectory( new File(ApplicationSettings.get().getLastDirectory()) );

        res = fch.showDialog( null, "Select");

        if ( res != JFileChooser.APPROVE_OPTION )
            return;

        tfTextureAtlasFile.setText(fch.getSelectedFile().getAbsolutePath());

        uploadTextureAtlas();
    }


    void uploadTextureAtlas() {

        File fl = new File( tfTextureAtlasFile.getText() );

        if ( !fl.exists() )
            return;

        Gdx.app.postRunnable( new Runnable() {
            @Override
            public void run() {
                gApp.loadAtlas( tfTextureAtlasFile.getText() );
            }
        });

        ApplicationSettings.get().setTextureAtlasFile(tfTextureAtlasFile.getText());
        ApplicationSettings.get().setLastDirectory( fl.getParent() );
    }

    void onAtlasLoaded( TextureAtlas atlas ) {

        if ( model != null )
            model.uploadAtlas();
    }

    void newModel() {

        PhysWorld.clearPrimaryWorld();

        model = new PhysModel( PhysWorld.getPrimaryWorld() );
        model.setName("noname");
        model.uploadAtlas();

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
        model = PhysModel.loadFromFile( Gdx.files.absolute( fl.getAbsolutePath()) );
        model.uploadAtlas();

        setGuiElementEnable(mainSplitPanel, true);

        modelToGui();

        currentModelFileName = fl.getAbsolutePath();

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

        }
        model.save( Gdx.files.absolute(currentModelFileName) );

    }




    void modelToGui() {
        loadTree();
        physModelPropertiesTableModel.setModel(model);

        GdxApplication.get().getEditorScreen().getModelRenderer().setModel( model );

        updateJointCombos();
    }

    void selectNode( DefaultMutableTreeNode node ) {
        TreePath newPath = new TreePath(node.getPath());
        treePhysModel.setSelectionPath(new TreePath(node.getPath()));
        treePhysModel.scrollPathToVisible(newPath);
        processTreeSelection( null );
    }

    void addNode() {

        if ( treePhysModel.getLastSelectedPathComponent() == null )
            return;

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePhysModel.getLastSelectedPathComponent();

        DefaultTreeModel md = (DefaultTreeModel) treePhysModel.getModel();
        NodeInfo ni = ( NodeInfo ) node.getUserObject();
        DefaultMutableTreeNode newNode = null;
        switch ( ni.type ) {
            case ROOT:

                if ( model.getBackgroundActor() == null ) {
                    int res = showRootAddNodeSelectorDialog();

                    if ( res == 1 ) {
                        newNode = addNewBody( node );
                        break;
                    } else if ( res == 0 ) {
                        newNode = addNewAag( node );
                        break;
                    } else if ( res < 0 ) {
                        return;
                    }
                }

                if ( model.getBackgroundActor() != null ) {
                    newNode = addNewBody( node );
                }
                break;

            case AAG:
                addNewAag( node );
                break;

            case BODY_ITEM:
                BodyItem bi = ( BodyItem ) ni.object;
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

            case FIXTURE_SET:
                return;
            default:
                tableProperties.setModel( null );
        }

        md.nodeChanged( node );
        md.nodeStructureChanged(node);

        if ( newNode != null ) {
            selectNode( newNode );
        }
    }

    DefaultMutableTreeNode addNewBody( DefaultMutableTreeNode parentNode ) {
        BodyItem bi = model.addBodyItem("noname");
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode( new NodeInfo(bi, NodeInfo.Type.BODY_ITEM) );
        parentNode.add( newNode );
        return newNode;

    }


    DefaultMutableTreeNode addNewAag( DefaultMutableTreeNode parentNode ) {

        NodeInfo ni = ( NodeInfo ) parentNode.getUserObject();

        AnimatedActorGroup newAg;

        DefaultMutableTreeNode newNode = null;

        switch ( ni.type ) {
            case ROOT:
                newAg = new AnimatedActorGroup();
                newAg.setName("noname");
                model.setBackgroundActor(newAg);
                newNode = new DefaultMutableTreeNode(new NodeInfo(newAg, NodeInfo.Type.AAG));
                parentNode.add( newNode );
                break;

            case AAG:
                if ( ni.object == null )
                    return null;
                AnimatedActorGroup parentAg = (AnimatedActorGroup) ni.object;
                newAg = new AnimatedActorGroup();
                newAg.setName( "noname" );
                parentAg.addChild( newAg );
                newNode = new DefaultMutableTreeNode( new NodeInfo( newAg, NodeInfo.Type.AAG ) );
                parentNode.add( newNode );
                break;
            case BODY_ITEM:
                if ( ni.object == null )
                    return null;
                BodyItem bi = (BodyItem) ni.object;

                if ( bi.getAagBackground() != null )
                    return null;
                newAg = new AnimatedActorGroup();
                newAg.setName( "noname" );
                bi.setAagBackground( newAg );
                newNode = new DefaultMutableTreeNode( new NodeInfo( newAg, NodeInfo.Type.AAG ) );
                parentNode.add( newNode );
                break;
        }
        return newNode;
    }

    DefaultMutableTreeNode addNewFixtureSet(DefaultMutableTreeNode parentNode) {
        NodeInfo ni = (NodeInfo) parentNode.getUserObject();
        BodyItem bi = ( BodyItem ) ni.object;
        FixtureSet fs = bi.addNewFixtureSet("noname");
        DefaultMutableTreeNode nn = new DefaultMutableTreeNode(new NodeInfo(fs, NodeInfo.Type.FIXTURE_SET));
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

    void removeNode() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePhysModel.getLastSelectedPathComponent();
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();

        NodeInfo ni = ( NodeInfo ) node.getUserObject();

        NodeInfo niParent = null;

        if ( parentNode != null ) {
            niParent = ( NodeInfo ) parentNode.getUserObject();
        }

        DefaultTreeModel md = (DefaultTreeModel) treePhysModel.getModel();

        boolean checkRemovedJoints = false;

        switch ( ni.type ) {

            case ROOT:
                return;
            case AAG:

                if ( niParent.type == NodeInfo.Type.ROOT ) {
                    model.setBackgroundActor(null);
                } else if ( niParent.type == NodeInfo.Type.AAG ) {
                    AnimatedActorGroup parentAg = (AnimatedActorGroup) niParent.object;
                    AnimatedActorGroup ag = (AnimatedActorGroup) ni.object;
                    parentAg.removeChild( ag );
                } else if ( niParent.type == NodeInfo.Type.BODY_ITEM) {
                    BodyItem bi = ( BodyItem ) niParent.object;
                    bi.setAagBackground( null );
                }

                break;
            case BODY_ITEM: {
                BodyItem bi = (BodyItem) ni.object;
                model.removeBody(bi);
                checkRemovedJoints = true;
                break;
            }

            case FIXTURE_SET: {
                BodyItem bi = (BodyItem) niParent.object;
                bi.removeFixtureSet( (FixtureSet) ni.object );

                break;
            }
            case JOINT_ITEM:
                JointItem ji = (JointItem) ni.object;
                model.removeJointItem( ji );
                updateJointCombos();
                break;
        }
        md.removeNodeFromParent( node );
        md.nodeStructureChanged( parentNode );
        if ( checkRemovedJoints) {
            Array< DefaultMutableTreeNode> removeList = new Array<DefaultMutableTreeNode>();
            int c = md.getChildCount( md.getRoot() );
            for ( int i = 0; i < c; i++) {
                node = (DefaultMutableTreeNode) md.getChild( md.getRoot(), i);
                ni = (NodeInfo) node.getUserObject();
                if ( ni.type != NodeInfo.Type.JOINT_ITEM )
                    continue;
                JointItem ji = (JointItem) ni.object;
                if ( model.findJointItem( ji.getId() ) == null )
                    removeList.add( node );
            }

            for ( DefaultMutableTreeNode nd : removeList ) {
                if ( node.getParent() != null )
                    md.removeNodeFromParent( node );
            }
            md.nodeStructureChanged((javax.swing.tree.TreeNode) md.getRoot());
        }
        selectNode( parentNode );
    }

    void processBodyItemSelection( BodyItem bi ) {
        DefaultTreeModel md = (DefaultTreeModel) treePhysModel.getModel();
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) md.getRoot();
        boolean res  = false;
        for ( int i = 0; i < rootNode.getChildCount(); i++ ) {
            DefaultMutableTreeNode chNode = (DefaultMutableTreeNode) rootNode.getChildAt( i );
            NodeInfo ni = (NodeInfo) chNode.getUserObject();
            if ( ni.type != NodeInfo.Type.BODY_ITEM )
                continue;
            BodyItem chBi = (BodyItem) ni.object;
            if ( chBi != bi )
                continue;
            treePhysModel.setSelectionPath(new TreePath( chNode.getPath() ) );
            res = true;
        }

        if ( res )
            processTreeSelection( null );
    }

    void processTreeSelection( TreeSelectionEvent e) {

        propertiesCellEditor.cancelCellEditing();
        tabbedPaneEditors.removeAll();

        setGuiElementEnable(panelShapeEditor, false);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePhysModel.getLastSelectedPathComponent();

        if ( node == null )
            return;

        if ( node.getUserObject() == null )
            return;

        NodeInfo ni = (NodeInfo) node.getUserObject();

        switch ( ni.type ) {
            case ROOT:
                tableProperties.setModel( physModelPropertiesTableModel );
                tabbedPaneEditors.add("Joint creator", panelJointCreator );
                updateJointCreatorGui();
                setGuiElementEnable( panelJointCreator, true);
                updateJointCreatorPanelFeatures();
                break;

            case AAG:
                aagPropertiesTableModel.setAag((AnimatedActorGroup) ni.object);
                tableProperties.setModel( aagPropertiesTableModel );
                tableProperties.updateUI();
                break;

            case BODY_ITEM:
                BodyItem bi = ( BodyItem ) ni.object;
                bodyPropertiesTableModel.setBodyItem( bi );
                tableProperties.setModel( bodyPropertiesTableModel );
                tableProperties.updateUI();
                setGuiElementEnable( panelBodyItemEditor, true);
                tabbedPaneEditors.add("Body Editor", panelBodyItemEditor);
                chbEnableMassCorrection.setSelected( false );
                break;

            case FIXTURE_SET:
                FixtureSet fs = ( FixtureSet ) ni.object;
                fixtureSetPropertiesTableModel.setFixtureSet( fs );
                tableProperties.setModel( fixtureSetPropertiesTableModel );
                tableProperties.updateUI();
                tabbedPaneEditors.add( "Shape Editor", panelShapeEditor );
                setGuiElementEnable( panelShapeEditor, true );
                updateShapeEditorFeatures( fs );
                break;

            case JOINT_ITEM:
                JointItem ji = (JointItem) ni.object;
                jointPropertiesTableModel.setJointItem( ji );
                tableProperties.setModel( jointPropertiesTableModel );
                tableProperties.updateUI();
                break;
            default:
        }

        GdxApplication.get().getEditorScreen().setModelObject(ni.object);
    }


    void processTreeNodeDuplication() {

        propertiesCellEditor.cancelCellEditing();
        tabbedPaneEditors.removeAll();

        setGuiElementEnable(panelShapeEditor, false);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePhysModel.getLastSelectedPathComponent();

        if ( node == null )
            return;

        if ( node.getUserObject() == null )
            return;

        NodeInfo ni = (NodeInfo) node.getUserObject();
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
        DefaultTreeModel md = (DefaultTreeModel) treePhysModel.getModel();
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) md.getRoot();
        NodeInfo parentNi = null;
        if ( parentNode != null ) {
            parentNi = (NodeInfo) parentNode.getUserObject();
        }

        DefaultMutableTreeNode newNode = null;

        switch ( ni.type ) {
            case ROOT:
                return;
            case AAG:

                if ( parentNi.type != NodeInfo.Type.AAG )
                    return;
                AnimatedActorGroup pAag = (AnimatedActorGroup) parentNi.object;
                AnimatedActorGroup aag = (AnimatedActorGroup) ni.object;
                AagDescription aagDesc = aag.getDescription();
                aagDesc.setName( aagDesc.getName() + "Cpy" );
                AnimatedActorGroup newAag = new AnimatedActorGroup( aagDesc );
                pAag.addChild( newAag );
                newNode = createTreeAagNode( parentNode, newAag );
                break;

            case BODY_ITEM:

                BodyItem bi = (BodyItem) ni.object;
                BodyItemDescription bd = bi.createBodyItemDescription();
                bd.setName( bd.getName() + "Cpy");
                bd.setId( -1 );
                BodyItem dupBi = model.addBodyItem( bd );
                newNode = createTreeBodyNode( dupBi );

                break;
            case FIXTURE_SET:

                BodyItem bodyItem = (BodyItem) parentNi.object;
                FixtureSet fs = (FixtureSet) ni.object;
                FixtureSetDescription fd = fs.getDescription();
                FixtureSet newFs = bodyItem.addNewFixtureSet( fd );
                newNode = createTreeFixtureSetNode( parentNode, newFs );

                break;
            case JOINT_ITEM:
                return;


        }

        if ( parentNode != null ) {
            md.nodeChanged(parentNode);
            md.nodeStructureChanged(parentNode);
        }

        if ( newNode != null ) {
            selectNode( newNode );
        }

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

    private DefaultMutableTreeNode createTreeAagNode( DefaultMutableTreeNode parent, AnimatedActorGroup aag ) {

        DefaultMutableTreeNode aagNode = new DefaultMutableTreeNode(
                new NodeInfo( aag, NodeInfo.Type.AAG) );

        for ( int i = 0; i < aag.getChildrenCount(); i++) {
            createTreeAagNode( aagNode, aag.getChild(i) );
        }

        parent.add( aagNode );

        return aagNode;
    }

    private DefaultMutableTreeNode createTreeFixtureSetNode( DefaultMutableTreeNode bodyNode, FixtureSet fs ) {
        DefaultMutableTreeNode fsNode = new DefaultMutableTreeNode(
                new NodeInfo( fs, NodeInfo.Type.FIXTURE_SET) );
        bodyNode.add( fsNode );

        return fsNode;
    }

    private DefaultMutableTreeNode createTreeBodyNode(BodyItem bi ) {

        DefaultTreeModel md = (DefaultTreeModel) treePhysModel.getModel();
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) md.getRoot();

        DefaultMutableTreeNode bodyNode = new DefaultMutableTreeNode(
                new NodeInfo( bi, NodeInfo.Type.BODY_ITEM) );

        if ( bi.getAagBackground() != null ) {
            createTreeAagNode( bodyNode, bi.getAagBackground() );
        }

        for ( FixtureSet fs: bi.getFixtureSets() ) {
            createTreeFixtureSetNode( bodyNode, fs );
        }

        rootNode.add( bodyNode );

        return bodyNode;
    }

    private DefaultMutableTreeNode createTreeJointItemNode( JointItem ji ) {
        DefaultTreeModel md = (DefaultTreeModel) treePhysModel.getModel();
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) md.getRoot();

        DefaultMutableTreeNode jiNode = new DefaultMutableTreeNode(
                new NodeInfo( ji, NodeInfo.Type.JOINT_ITEM ) );

        if ( ji.getAagBackground() != null ) {
            createTreeAagNode( jiNode, ji.getAagBackground() );
        }
        rootNode.add( jiNode );
        return jiNode;
    }

    private void loadTree() {

        if ( model == null ) {
            treePhysModel.setModel( new DefaultTreeModel( null ));
            return;
        }

        DefaultMutableTreeNode root = new DefaultMutableTreeNode( new NodeInfo( model, NodeInfo.Type.ROOT ) );
        treePhysModel.setModel(new DefaultTreeModel(root));

        if ( model.getBackgroundActor() != null ) {
            createTreeAagNode(root, model.getBackgroundActor());
        }

        for ( BodyItem bi : model.getBodyItems() ) {
            createTreeBodyNode( bi );
        }

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
        ShapeController shc = GdxApplication.get().getEditorScreen().getCurrentShapeController();
        if ( shc == null )
            return;

        try {
            float x = Float.valueOf(tfNewShapePosX.getText());
            float y = Float.valueOf(tfNewShapePosY.getText());

            if ( shc instanceof CircleShapeController ) {
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


    void setAnchorPointFromGui( AnchorPointController.AnchorControlPoint.AcpType type ) {
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

        setAnchorPointFromGui(AnchorPointController.AnchorControlPoint.AcpType.typeA);
        setAnchorPointFromGui(AnchorPointController.AnchorControlPoint.AcpType.typeB);
        if ( jiDesc.getType() == JointDef.JointType.PulleyJoint ) {
            setAnchorPointFromGui(AnchorPointController.AnchorControlPoint.AcpType.typeC);
            setAnchorPointFromGui(AnchorPointController.AnchorControlPoint.AcpType.typeD);
        } else if ( jiDesc.getType() == JointDef.JointType.PrismaticJoint ) {
            setAnchorPointFromGui(AnchorPointController.AnchorControlPoint.AcpType.typeAxis);
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


        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePhysModel.getLastSelectedPathComponent();
        DefaultTreeModel md = (DefaultTreeModel) treePhysModel.getModel();
        NodeInfo ni = ( NodeInfo ) node.getUserObject();
        if ( ni.type != NodeInfo.Type.ROOT)
            return;

        JointItem ji = model.addNewJointItem( jiDesc );
        if ( ji == null)
            return;
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new NodeInfo(ji, NodeInfo.Type.JOINT_ITEM));
        node.add( newNode );
        md.nodeChanged( node );
        md.nodeStructureChanged(node);
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

                    GdxApplication.get().getSimulationScreen().setModelDescription( description );
                    GdxApplication.get().toggleSimulationScreen();
                    GdxApplication.get().getSimulationScreen().startSimulation();

                } else {
                    GdxApplication.get().toggleEditorScreen();
                }
            }
        });

    }

    void restartSimulation() {
        Gdx.app.postRunnable( new Runnable() {
            @Override
            public void run() {
                GdxApplication.get().getSimulationScreen().startSimulation();
            }
        });
    }

    void doSimulationStep() {
        Gdx.app.postRunnable( new Runnable() {


            @Override
            public void run() {
                GdxApplication.get().getSimulationScreen().doStep();
            }
        });
    }

    void toggleSimulationPause() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                GdxApplication.get().getSimulationScreen().setPause(chbPauseSimulation.isSelected());
            }
        });
    }

    void toggleGrid() {
        Gdx.app.postRunnable( new Runnable() {
            @Override
            public void run() {
                boolean state = chbDisplayGrid.isSelected();
                GdxApplication.get().getEditorScreen().setDisplayGrid( state );
                GdxApplication.get().getSimulationScreen().setDisplayGrid( state );
            }
        });
    }

    void toggleDebugRender() {
        Gdx.app.postRunnable( new Runnable() {
            @Override
            public void run() {
                boolean state = chbDebugRender.isSelected();
                GdxApplication.get().getEditorScreen().setDoDebugRender(state);
                GdxApplication.get().getSimulationScreen().setDoDebugRender(state);
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
        AnchorPointController ctrlr = GdxApplication.get().getEditorScreen().getAnchorPointController();

        switch ( type ) {
            case Unknown:
                panelJointCreatorFeatures.setVisible( false );
                ctrlr.setMode(AnchorPointController.Mode.NoPoints);
                break;
            case RevoluteJoint:
                ctrlr.setMode(AnchorPointController.Mode.OnePointMode);
                panelAnchorB.setVisible( false );
                break;
            case PrismaticJoint:
                ctrlr.setMode(AnchorPointController.Mode.OnePointAndAxisMode);
                panelAxis.setVisible( true );
                panelAnchorB.setVisible( false );
                break;
            case DistanceJoint:
                ctrlr.setMode(AnchorPointController.Mode.TwoPointsMode);
                break;
            case PulleyJoint:
                panelGroundAnchors.setVisible( true );
                ctrlr.setMode(AnchorPointController.Mode.FourPointsMode );
                panelRatio.setVisible( true );
                break;
            case MouseJoint:
                break;
            case GearJoint:
                ctrlr.setMode(AnchorPointController.Mode.NoPoints);
                panelRatio.setVisible( true );
                panelJoints.setVisible( true );
                panelAnchorA.setVisible( false );
                panelAnchorB.setVisible( false );
                break;
            case WheelJoint:
                ctrlr.setMode(AnchorPointController.Mode.OnePointAndAxisMode);
                panelAxis.setVisible( true );
                panelAnchorB.setVisible( false );
                break;
            case WeldJoint:
                ctrlr.setMode(AnchorPointController.Mode.OnePointMode);
                panelAnchorB.setVisible( false );
                break;
            case FrictionJoint:
                ctrlr.setMode(AnchorPointController.Mode.OnePointMode);
                panelAnchorB.setVisible( false );
                break;
            case RopeJoint:
                ctrlr.setMode(AnchorPointController.Mode.TwoPointsMode);
                break;
            case MotorJoint:
                ctrlr.setMode(AnchorPointController.Mode.NoPoints);
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
        BodyItemController ctrlr = GdxApplication.get().getEditorScreen().getBodyItemController();
        ctrlr.setWorldCenterOfMass( x, y );
    }

    void resetMassData() {
        BodyItemController ctrlr = GdxApplication.get().getEditorScreen().getBodyItemController();
        ctrlr.resetMassData();
    }

    private void setMassCorrectionEnabled() {
        BodyItemController ctrlr = GdxApplication.get().getEditorScreen().getBodyItemController();
        ctrlr.setEnableMassCorrection( chbEnableMassCorrection.isSelected() );

    }

    void selectBodyItemToJointCreator( BodyItem bi,int id ) {
        if ( id == 0 ) {
            comboBodyASelector.setSelectedItem( bi );
        } else {
            comboBodyBSelector.setSelectedItem( bi );
        }
    }



    void selectBodyItemToAnchorPointcController( int selId ) {
        AnchorPointController ctrlr = GdxApplication.get().getEditorScreen().getAnchorPointController();
        if ( selId == 0 ) {
            ctrlr.setSelectedBodyItem((BodyItem) comboBodyASelector.getSelectedItem(), 0);
        } else {
            ctrlr.setSelectedBodyItem((BodyItem) comboBodyBSelector.getSelectedItem(), 1);
        }
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
