package org.skr.PhysModelEditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.scenes.scene2d.Actor;
import org.skr.PhysModelEditor.PropertiesTableElements.*;
import org.skr.PhysModelEditor.controller.CircleShapeController;
import org.skr.PhysModelEditor.controller.Controller;
import org.skr.PhysModelEditor.controller.ShapeController;
import org.skr.physmodel.BodyItem;
import org.skr.physmodel.ShapeDescription;
import org.skr.physmodel.animatedactorgroup.AnimatedActorGroup;
import org.skr.physmodel.PhysModel;
import org.skr.physmodel.FixtureSet;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.JTableHeader;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
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
    private JPanel panelJointEditor;
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

    private GdxApplication gApp;
    private String currentModelFileName = "";
    private PhysModel model = null;

    private PhysModelPropertiesTableModel physModelPropertiesTableModel;
    private AagPropertiesTableModel aagPropertiesTableModel;
    private BodyPropertiesTableModel bodyPropertiesTableModel;
    private PropertiesCellEditor propertiesCellEditor;
    private FixtureSetPropertiesTableModel fixtureSetPropertiesTableModel;


    public static final class NodeInfo {
        public enum Type {
            ROOT, AAG, BODY_ITEM, FIXTURE_SET;
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
            }

            return "";
        }
    }


    MainGui() {


        gApp = new GdxApplication();
        final LwjglAWTCanvas gdxCanvas = new LwjglAWTCanvas( gApp );

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(rootPanel);
        gdxPanel.add(gdxCanvas.getCanvas(), BorderLayout.CENTER);
        pack();
        setSize(1280, 800);
        addWindowListener( new MainGuiWindowListener() );


        physModelPropertiesTableModel = new PhysModelPropertiesTableModel( treePhysModel );
        aagPropertiesTableModel = new AagPropertiesTableModel( treePhysModel );
        bodyPropertiesTableModel = new BodyPropertiesTableModel( treePhysModel );
        fixtureSetPropertiesTableModel = new FixtureSetPropertiesTableModel( treePhysModel );


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


        Gdx.app.postRunnable( new Runnable() {
            @Override
            public void run() {
                GdxApplication.get().getEditorScreen().getActorController().setControlPointListener(
                        new Controller.controlPointListener() {
                            @Override
                            public void changed(Object controlledObject, Controller.ControlPoint controlPoint) {
                                actorChangedByController( (Actor) controlledObject  );
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
    }

    void uploadGuiFromSettings() {
        tfTextureAtlasFile.setText( ApplicationSettings.get().getTextureAtlasFile() );
    }



    void selectTextureAtlasFile() {

        final JFileChooser fch = new JFileChooser();
        int res;
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
    }

    void onAtlasLoaded( TextureAtlas atlas ) {

        if ( model != null )
            model.uploadAtlas();
    }

    void newModel() {

        model = new PhysModel();
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

    }


    void addNode() {

        if ( treePhysModel.getLastSelectedPathComponent() == null )
            return;

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePhysModel.getLastSelectedPathComponent();

        DefaultTreeModel md = (DefaultTreeModel) treePhysModel.getModel();
        NodeInfo ni = ( NodeInfo ) node.getUserObject();

        switch ( ni.type ) {
            case ROOT:

                if ( model.getBackgroundActor() == null ) {
                    int res = showRootAddNodeSelectorDialog();

                    if ( res == 1 ) {
                        addNewBody( node );
                        break;
                    } else if ( res == 0 ) {
                        addNewAag( node );
                        break;
                    } else if ( res < 0 ) {
                        return;
                    }
                }

                if ( model.getBackgroundActor() != null ) {
                    addNewBody( node );
                }
                break;

            case AAG:
                addNewAag( node );
                break;

            case BODY_ITEM:
                BodyItem bi = ( BodyItem ) ni.object;
                if ( bi.getAagBackground() != null ) {
                    addNewFixtureSet( node );
                } else {
                    int res = showBodyAddNodeSelectorDialog();
                    if ( res == 0 ) {
                        addNewAag(node);
                    } else if ( res == 1) {
                        addNewFixtureSet( node );
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
    }

    void addNewBody( DefaultMutableTreeNode parentNode ) {
        BodyItem bi = model.addNewBodyItem("noname");
        parentNode.add( new DefaultMutableTreeNode( new NodeInfo(bi, NodeInfo.Type.BODY_ITEM) ) );
    }


    void addNewAag( DefaultMutableTreeNode parentNode ) {

        NodeInfo ni = ( NodeInfo ) parentNode.getUserObject();

        AnimatedActorGroup newAg;

        switch ( ni.type ) {
            case ROOT:
                newAg = new AnimatedActorGroup();
                newAg.setName("noname");
                model.setBackgroundActor(newAg);
                parentNode.add(new DefaultMutableTreeNode(new NodeInfo(newAg, NodeInfo.Type.AAG)));
                break;

            case AAG:
                if ( ni.object == null )
                    return;
                AnimatedActorGroup parentAg = (AnimatedActorGroup) ni.object;
                newAg = new AnimatedActorGroup();
                newAg.setName( "noname" );
                parentAg.addChild( newAg );
                parentNode.add( new DefaultMutableTreeNode( new NodeInfo( newAg, NodeInfo.Type.AAG ) ) );
                break;
            case BODY_ITEM:
                if ( ni.object == null )
                    return;
                BodyItem bi = (BodyItem) ni.object;

                if ( bi.getAagBackground() != null )
                    return;
                newAg = new AnimatedActorGroup();
                newAg.setName( "noname" );
                bi.setAagBackground( newAg );
                parentNode.add( new DefaultMutableTreeNode( new NodeInfo( newAg, NodeInfo.Type.AAG ) ) );
                break;
        }

    }

    void addNewFixtureSet(DefaultMutableTreeNode parentNode) {
        NodeInfo ni = (NodeInfo) parentNode.getUserObject();
        BodyItem bi = ( BodyItem ) ni.object;
        FixtureSet fs = bi.addNewFixtureSet("noname");
        parentNode.add(new DefaultMutableTreeNode(new NodeInfo(fs, NodeInfo.Type.FIXTURE_SET)));
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

                break;
            }

            case FIXTURE_SET: {
                BodyItem bi = (BodyItem) niParent.object;
                bi.removeFixtureSet( (FixtureSet) ni.object );

                break;
            }
        }
        md.removeNodeFromParent( node );
        md.nodeStructureChanged( parentNode );
    }



    void processTreeSelection( TreeSelectionEvent e) {

        propertiesCellEditor.cancelCellEditing();
        tabbedPaneEditors.removeAll();

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePhysModel.getLastSelectedPathComponent();

        if ( node == null )
            return;

        if ( node.getUserObject() == null )
            return;

        NodeInfo ni = (NodeInfo) node.getUserObject();

        switch ( ni.type ) {
            case ROOT:
                tableProperties.setModel( physModelPropertiesTableModel );
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

            default:
        }

        GdxApplication.get().getEditorScreen().setModelObject(ni.object);
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

    private void loadTree() {

        if ( model == null ) {
            treePhysModel.setModel( new DefaultTreeModel( null ));
            return;
        }

        DefaultMutableTreeNode root = new DefaultMutableTreeNode( new NodeInfo( model, NodeInfo.Type.ROOT ) );
        treePhysModel.setModel( new DefaultTreeModel( root ) );

        if ( model.getBackgroundActor() != null ) {
            DefaultMutableTreeNode aagNode = new DefaultMutableTreeNode(
                    new NodeInfo( model.getBackgroundActor(), NodeInfo.Type.AAG) );
            root.add( aagNode );
            loadTreeNodeForAag(aagNode);
        }

        if ( model.getBodyItems() != null ) {

            for ( BodyItem bi : model.getBodyItems() ) {
                DefaultMutableTreeNode bodyNode = new DefaultMutableTreeNode(
                        new NodeInfo( bi, NodeInfo.Type.BODY_ITEM) );

                if ( bi.getAagBackground() != null ) {
                    DefaultMutableTreeNode aagNode = new DefaultMutableTreeNode(
                            new NodeInfo( bi.getAagBackground(), NodeInfo.Type.AAG) );
                    loadTreeNodeForAag( aagNode );
                    bodyNode.add( aagNode );
                }

                for ( FixtureSet fs: bi.getFixtureSets() ) {
                    DefaultMutableTreeNode fsNode = new DefaultMutableTreeNode(
                            new NodeInfo( fs, NodeInfo.Type.FIXTURE_SET) );
                    bodyNode.add( fsNode );
                }

                root.add( bodyNode );
            }

        }

    }

    private void actorChangedByController(Actor actor) {
        if ( actor instanceof AnimatedActorGroup )
            aagPropertiesTableModel.actorChanged( (AnimatedActorGroup) actor );
    }

    private void loadTreeNodeForAag(DefaultMutableTreeNode parent) {
        AnimatedActorGroup parentAag = (AnimatedActorGroup) ((NodeInfo)parent.getUserObject()).object;

        for ( int i = 0; i < parentAag.getChildrenCount(); i++) {
            DefaultMutableTreeNode nd = new DefaultMutableTreeNode( new NodeInfo( parentAag.getChild(i), NodeInfo.Type.AAG ) );
            parent.add( nd );
            loadTreeNodeForAag(nd);
        }
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
