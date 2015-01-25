package org.skr.gx2d.ModelEditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.joints.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import org.skr.gx2d.ModelEditor.PropertiesTableElements.*;
import org.skr.gx2d.ModelEditor.gdx.SkrGx2DModelEditorGdxApp;
import org.skr.gx2d.ModelEditor.gdx.screens.EditorScreen;
import org.skr.gx2d.model.Model;
import org.skr.gx2d.node.Node;
import org.skr.gx2d.physnodes.BodyHandler;
import org.skr.gx2d.physnodes.FixtureSet;
import org.skr.gx2d.physnodes.JointHandler;
import org.skr.gx2d.physnodes.PhysSet;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.io.IOException;

/**
 * Created by rat on 27.09.14.
 */
public class ModelGuiControl {

    static class TransferableNode implements Transferable {

        public static DataFlavor NODE_FLAVOR = new DataFlavor(TreePath.class, "Tree Path");

        DataFlavor flavors [] = { NODE_FLAVOR };


        TransferableNode() {
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return ( flavor.getRepresentationClass() == TreePath.class );
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if ( isDataFlavorSupported( flavor ) ) {
                return null;
            } else {
                throw new UnsupportedFlavorException( flavor );
            }
        }
    }

    static class TreeNodesComplianceControl {

        JTree tree;

        TreeNodesComplianceControl(JTree tree) {
            this.tree = tree;
        }


        public Gx2dJTreeNode getNodeUnderCursor() {

            Point p = tree.getMousePosition();
            if ( p == null )
                return null;
            TreePath tp = tree.getPathForLocation( p.x, p.y );
            if ( tp == null )
                return null;
            return (Gx2dJTreeNode) tp.getLastPathComponent();
        }

        public Gx2dJTreeNode getNodeForEvent( DropTargetDragEvent dtde ) {
            return getNodeUnderCursor();
        }

        public Gx2dJTreeNode getNodeForEvent( DropTargetDropEvent dtde ) {
            return getNodeUnderCursor();
        }

        public Gx2dJTreeNode getNodeForEvent( DragSourceDragEvent dsde ) {
            return  getNodeUnderCursor();
        }

        public Gx2dJTreeNode getNodeForEvent( DragSourceDropEvent dsde ) {
            return getNodeUnderCursor();
        }

        public boolean isDraggable(Gx2dJTreeNode node) {
            Gx2dJTreeNode parentNode = (Gx2dJTreeNode) node.getParent();

            switch ( node.getType() ) {

                case MODEL:
                    break;
                case PHYS_SET:
                    break;
                case SPRITE:
                    if ( parentNode.getType() == Gx2dJTreeNode.Type.BODY_HANDLER)
                        break;
                    return true;
                case BODY_HANDLER:
                    return true;
                case FIXTURE_SET:
                    return true;
                case JOINT_HANDLER:
                    break;
            }

            return false;
        }

        public boolean isCompliance( Gx2dJTreeNode sourceNode, Gx2dJTreeNode targetNode ) {

            Gx2dJTreeNode.Type sType = sourceNode.getType();
            Gx2dJTreeNode.Type tType = targetNode.getType();

            switch ( sType ) {
                case MODEL:
                    break;
                case PHYS_SET:
                    break;
                case SPRITE:
                    if ( tType == Gx2dJTreeNode.Type.SPRITE )
                        return true;
                    break;
                case BODY_HANDLER:
                    break;
                case FIXTURE_SET:
                    if ( tType == Gx2dJTreeNode.Type.BODY_HANDLER )
                        return true;
                    break;
                case JOINT_HANDLER:
                    break;
            }
            return false;
        }

    }


    static class TreeDragSource implements DragSourceListener, DragGestureListener {

        ModelGuiControl guiProc;
        JTree sourceTree;
        DragSource source;
        DragGestureRecognizer recognizer;
        TransferableNode transferableNode;
        Gx2dJTreeNode sourceNode;
        TreeNodesComplianceControl nodesComplianceControl;


        TreeDragSource(ModelGuiControl guiProc, int actions ) {
            this.guiProc = guiProc;
            this.sourceTree = guiProc.getjTreeModel();
            this.nodesComplianceControl = guiProc.getNodesComplianceControl();
            source = new DragSource();
            recognizer = source.createDefaultDragGestureRecognizer( sourceTree, actions, this );
        }

        public Gx2dJTreeNode getSourceNode() {
            return sourceNode;
        }

        @Override
        public void dragGestureRecognized(DragGestureEvent dge) {
            TreePath selPath = sourceTree.getSelectionPath();
            sourceNode = null;
            if ( selPath == null )
                return;
            if ( selPath.getPathCount() < 1 )
                return;
            sourceNode = (Gx2dJTreeNode) selPath.getLastPathComponent();
            if ( !nodesComplianceControl.isDraggable(sourceNode) ) {
                sourceNode = null;
                return;
            }
            transferableNode = new TransferableNode();
            source.startDrag(dge, DragSource.DefaultMoveDrop, transferableNode, this);
        }

        @Override
        public void dragEnter(DragSourceDragEvent dsde) {
        }

        @Override
        public void dragOver(DragSourceDragEvent dsde) {

            Gx2dJTreeNode targetNode = nodesComplianceControl.getNodeUnderCursor();
            if ( targetNode == null ) {
                dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
                return;
            }

            if ( ! nodesComplianceControl.isCompliance(sourceNode, targetNode) ) {
                dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
                return;
            }
            Cursor cursor = DragSource.DefaultMoveDrop;
            if ( dsde.getDropAction() == DnDConstants.ACTION_COPY ) {
                cursor = DragSource.DefaultCopyDrop;
            }
            dsde.getDragSourceContext().setCursor( cursor );

        }

        @Override
        public void dropActionChanged(DragSourceDragEvent dsde) {

        }

        @Override
        public void dragExit(DragSourceEvent dse) {

        }

        @Override
        public void dragDropEnd(DragSourceDropEvent dsde) {

        }
    }


    static class TreeDropTarget implements DropTargetListener {
        DropTarget target;
        JTree targetTree;
        TreeNodesComplianceControl nodesComplianceControl;
        ModelGuiControl guiProc;

        TreeDropTarget( ModelGuiControl guiProc ) {
            this.guiProc = guiProc;
            this.targetTree = guiProc.getjTreeModel();
            target = new DropTarget( targetTree, this );
            this.nodesComplianceControl = guiProc.getNodesComplianceControl();
        }


        protected Gx2dJTreeNode getComplianceSrcNode( Transferable tr, Gx2dJTreeNode targetNode ) {
            DataFlavor[] flavors = tr.getTransferDataFlavors();
            try {
                for (DataFlavor flavor : flavors) {
                    if (!tr.isDataFlavorSupported(flavor))
                        continue;
                    Gx2dJTreeNode srcNode = guiProc.getDragSource().getSourceNode();
                    if (!nodesComplianceControl.isCompliance(srcNode, targetNode)) {
                        return null;
                    }
                    return srcNode;
                }
            } catch ( Exception e ) {
                return null;
            }
            return null;
        }

        @Override
        public void dragEnter(DropTargetDragEvent dtde) {
        }

        @Override
        public void dragOver(DropTargetDragEvent dtde) {
        }

        @Override
        public void dropActionChanged(DropTargetDragEvent dtde) {

        }

        @Override
        public void dragExit(DropTargetEvent dte) {

        }

        @Override
        public void drop(DropTargetDropEvent dtde) {
            Gx2dJTreeNode node = nodesComplianceControl.getNodeForEvent(dtde);
            if ( node == null ) {
                dtde.rejectDrop();
                return;
            }
            Gx2dJTreeNode srcNode = getComplianceSrcNode( dtde.getTransferable(), node );
            if ( srcNode == null ) {
                dtde.rejectDrop();
                return;
            }

            if ( guiProc.moveNode( srcNode, node, dtde.getDropAction() == DnDConstants.ACTION_COPY ) == null ) {
                dtde.rejectDrop();
                return;
            }

            guiProc.getMainGui().makeHistorySnapshot();

            dtde.dropComplete(true);
        }
    }

    JTree jTreeModel;
    JTable jTableProperties;

    MainGui mainGui;

    Model model;
    Gx2dJTreeNode rootNode;
    EditorScreen editorScreen;

    private DefaultTreeModel treeDataModel;
    private PropertiesCellEditor propertiesCellEditor;

    private DefaultTableModel emptyTableModel = new DefaultTableModel();
    private ModelPropertiesTableModel physModelPropertiesTableModel;
    private SpritePropertiesTableModel aagPropertiesTableModel;
    private BodyPropertiesTableModel bodyPropertiesTableModel;
    private FixtureSetPropertiesTableModel fixtureSetPropertiesTableModel;
    private JointPropertiesTableModel jointPropertiesTableModel;

    private TreeDragSource dragSource;
    private TreeDropTarget dropTarget;
    TreeNodesComplianceControl nodesComplianceControl;

    private Gx2dJTreeNode.Type propCpyNodeType = null;
    private Object propCpyDescRef = null;

    public ModelGuiControl(final MainGui mainGui) {
        this.mainGui = mainGui;

        this.jTreeModel = mainGui.getTreePhysModel();
        this.jTableProperties = mainGui.getTableProperties();


        this.treeDataModel = new DefaultTreeModel( null );
        this.jTreeModel.setModel(treeDataModel);
        this.jTreeModel.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        this.jTreeModel.setShowsRootHandles(true);

        nodesComplianceControl = new TreeNodesComplianceControl( this.jTreeModel );
        dragSource = new TreeDragSource( this, DnDConstants.ACTION_COPY_OR_MOVE );
        dropTarget = new TreeDropTarget( this );


        physModelPropertiesTableModel = new ModelPropertiesTableModel( this.jTreeModel);
        aagPropertiesTableModel = new SpritePropertiesTableModel( this.jTreeModel);
        bodyPropertiesTableModel = new BodyPropertiesTableModel( this.jTreeModel);
        fixtureSetPropertiesTableModel = new FixtureSetPropertiesTableModel( this.jTreeModel);
        jointPropertiesTableModel = new JointPropertiesTableModel( this.jTreeModel);

        Gdx.app.postRunnable( new Runnable() {
            @Override
            public void run() {
                editorScreen = SkrGx2DModelEditorGdxApp.get().getEditorScreen();

                editorScreen.setItemSelectionListener(new EditorScreen.ItemSelectionListener() {
                    @Override
                    public void singleItemSelected(Object object, EditorScreen.ModelObjectType mot) {
                        selectObject( object, mot );
                    }

                    @Override
                    public void itemAddedToSelection(Object object, EditorScreen.ModelObjectType mot, boolean removed) {
                        changeObjectSelection( object, mot, !removed );
                    }

                    @Override
                    public void itemsSelected(Array<? extends Object> objects, EditorScreen.ModelObjectType mot) {
                        selectObjects( objects, mot );
                    }
                });
            }
        });

        fixtureSetPropertiesTableModel.setShapeTypeListener( new FixtureSetPropertiesTableModel.ShapeTypeListener() {
            @Override
            public void changed(FixtureSet fixtureSet) {
                mainGui.setupEditors( fixtureSet, EditorScreen.ModelObjectType.OT_FixtureSet );
            }
        });

        JTableHeader th = this.jTableProperties.getTableHeader();
        mainGui.getPanelProperties().add(th, BorderLayout.NORTH);

        propertiesCellEditor = new PropertiesCellEditor();
        propertiesCellEditor.addCellEditorListener( new CellEditorListener() {
            @Override
            public void editingStopped(ChangeEvent e) {
                mainGui.makeHistorySnapshot();
            }

            @Override
            public void editingCanceled(ChangeEvent e) {
            }
        });


        this.jTableProperties.setDefaultEditor(
                PropertiesBaseTableModel.Property.class,
                propertiesCellEditor );
        this.jTableProperties.setDefaultRenderer(PropertiesBaseTableModel.Property.class,
                new PropertiesTableCellRenderer());


        this.jTreeModel.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                processTreeSelection();
            }
        });
    }


    public MainGui getMainGui() {
        return mainGui;
    }

    public JTree getjTreeModel() {
        return jTreeModel;
    }

    public TreeDragSource getDragSource() {
        return dragSource;
    }

    public TreeDropTarget getDropTarget() {
        return dropTarget;
    }

    public TreeNodesComplianceControl getNodesComplianceControl() {
        return nodesComplianceControl;
    }

    public void setModel( Model model ) {
        jTreeModel.removeAll();
        this.model = model;
        if ( model != null )
            loadModelJTree();
    }

    protected void loadModelJTree() {
        rootNode = new Gx2dJTreeNode(Gx2dJTreeNode.Type.MODEL, model );
        loadPhysSetJNodes();
        this.treeDataModel = new DefaultTreeModel( rootNode );
        this.jTreeModel.setModel( treeDataModel );
    }

    protected void loadPhysSetJNodes() {
        PhysSet ps = model.getPhysSet();
        if ( ps == null )
            return;
        for (Node node : ps ) {
            PhysSet p = (PhysSet) node;
            Gx2dJTreeNode jNode = new Gx2dJTreeNode(Gx2dJTreeNode.Type.PHYS_SET, p );
            loadPhysSetNode( jNode );
            rootNode.add(jNode);
        }
    }

    protected void loadPhysSetNode(Gx2dJTreeNode physSetJNode) {
        PhysSet ps = (PhysSet) physSetJNode.getUserObject();

        //todo: dig here.

        Gx2dJTreeNode bgJNode = new Gx2dJTreeNode(Gx2dJTreeNode.Type.BODY_HANDLER, ps.getBodyHandler() );
        physSetJNode.add( bgJNode );
        Gx2dJTreeNode jgJNode = new Gx2dJTreeNode(Gx2dJTreeNode.Type.JOINT_HANDLER, ps.getJointHandler() );
        physSetJNode.add( jgJNode );

        BodyHandler bh = ps.getBodyHandler();

        if ( bh != null ) {
            for ( Node node : bh ) {
                BodyHandler b = (BodyHandler) node;
                Gx2dJTreeNode bJNode = new Gx2dJTreeNode(Gx2dJTreeNode.Type.BODY_HANDLER, b);
                loadBodyHandlerNode(bJNode);
                bgJNode.add( bJNode );
            }
        }

        JointHandler jh = ps.getJointHandler();
        if ( jh != null ) {
            for ( Node node : jh ) {
                JointHandler j = (JointHandler) node;
                Gx2dJTreeNode jJNode = new Gx2dJTreeNode(Gx2dJTreeNode.Type.JOINT_HANDLER, j );

            }
        }

        Gx2dJTreeNode jJNode = new Gx2dJTreeNode(Gx2dJTreeNode.Type.JOINT_ITEM_GROUP,
                bset );
        loadJointHandlerNode(jJNode);

        physSetJNode.add(bJNode);
        physSetJNode.add(jJNode);
    }



    protected void loadBodyHandlerNode(Gx2dJTreeNode parentNode) {
        BiScSet bset = (BiScSet) parentNode.getUserObject();
        for ( BodyItem bi : bset.getBodyItems() ) {
            Gx2dJTreeNode node = new Gx2dJTreeNode(Gx2dJTreeNode.Type.BODY_HANDLER, bi );
            loadBodyItemNodes( node );
            parentNode.add( node );
        }
    }


    protected void loadBodyItemNodes( Gx2dJTreeNode parentNode ) {
        BodyItem bi = (BodyItem) parentNode.getUserObject();
        Gx2dJTreeNode fsgNode = new Gx2dJTreeNode(Gx2dJTreeNode.Type.FIXTURE_SET_GROUP,
                bi );
        loadFixtureSetsNodes( fsgNode );
        parentNode.add( fsgNode );

        Gx2dJTreeNode aagNode = new Gx2dJTreeNode(Gx2dJTreeNode.Type.SPRITE, bi );
        loadAagNodes(aagNode);
        parentNode.add(aagNode);
    }

    protected void loadFixtureSetsNodes( Gx2dJTreeNode parentNode ) {
        BodyItem bi = (BodyItem) parentNode.getUserObject();
        for ( FixtureSet fs : bi.getFixtureSets() ) {
            parentNode.add( new Gx2dJTreeNode(Gx2dJTreeNode.Type.FIXTURE_SET, fs) );
        }
    }


    protected void loadAagNodes( Gx2dJTreeNode parentNode ) {
        AnimatedActorGroup aag = (AnimatedActorGroup) parentNode.getUserObject();
        for ( Actor a : aag.getChildren() ) {
            if ( !( a instanceof AnimatedActorGroup ) )
                continue;
            if ( aag.getScChildren().contains( a ) )
                continue;
            Gx2dJTreeNode node = new Gx2dJTreeNode(Gx2dJTreeNode.Type.SPRITE, a );
            loadAagNodes( node );
            parentNode.add( node );
        }
        Gx2dJTreeNode scNode = new Gx2dJTreeNode(Gx2dJTreeNode.Type.AAG_SC, aag );
        loadAagScNodes( scNode );
        parentNode.add( scNode );
    }

    protected void loadAagScNodes( Gx2dJTreeNode parentNode ) {
        AnimatedActorGroup aag = (AnimatedActorGroup) parentNode.getUserObject();
        AagScContainer aagScContainer = aag.getScChildren();
        for ( Integer id : aagScContainer.getIdsSet() ) {
            ScContainer.Handler handler = new ScContainer.Handler( aagScContainer, id );
            Gx2dJTreeNode scSetNode = new Gx2dJTreeNode( Gx2dJTreeNode.Type.AAG_SC_SET, handler );
            loadAagScSetNodes(scSetNode);
            parentNode.add( scSetNode );
        }
    }

    protected void loadAagScSetNodes(Gx2dJTreeNode parentNode) {
        ScContainer.Handler handler = (ScContainer.Handler) parentNode.getUserObject();
        AnimatedActorGroup aag = (AnimatedActorGroup) handler.container.getContent( handler.id );
        Gx2dJTreeNode aagNode = new Gx2dJTreeNode( Gx2dJTreeNode.Type.SPRITE, aag );
        loadAagNodes( aagNode );
        parentNode.add( aagNode );
    }

    protected void loadJointHandlerNode(Gx2dJTreeNode parentNode) {
        BiScSet bset = (BiScSet) parentNode.getUserObject();
        for ( JointItem jointItem : bset.getJointItems() ) {
            parentNode.add( new Gx2dJTreeNode(Gx2dJTreeNode.Type.JOINT_HANDLER, jointItem ) );
        }
    }

    protected Gx2dJTreeNode getChildNode( Gx2dJTreeNode parentNode, Gx2dJTreeNode.Type childType ) {
        for ( int i = 0; i < parentNode.getChildCount(); i++ ) {
            Gx2dJTreeNode node = (Gx2dJTreeNode) parentNode.getChildAt( i );
            if ( node.getType() == childType )
                return node;
        }
        return null;
    }

    protected Gx2dJTreeNode getFixtureSetGroupNode( Gx2dJTreeNode parentBodyItemNode ) {
        if ( parentBodyItemNode.getType() != Gx2dJTreeNode.Type.BODY_HANDLER)
            return null;

        return getChildNode( parentBodyItemNode, Gx2dJTreeNode.Type.FIXTURE_SET_GROUP );
    }


    protected Gx2dJTreeNode getJointGroupNode( Gx2dJTreeNode parentBiScSetNode ) {
        if ( parentBiScSetNode.getType() != Gx2dJTreeNode.Type.BiScSET )
            return null;
        return getChildNode( parentBiScSetNode, Gx2dJTreeNode.Type.JOINT_ITEM_GROUP );
    }


    protected Gx2dJTreeNode getAagScNode( Gx2dJTreeNode parentAagNode ) {
        if ( parentAagNode.getType() != Gx2dJTreeNode.Type.SPRITE)
            return null;
        return getChildNode( parentAagNode, Gx2dJTreeNode.Type.AAG_SC );
    }

    protected Gx2dJTreeNode getAagNode( Gx2dJTreeNode parentAagScSetNode ) {
        if ( parentAagScSetNode.getType() != Gx2dJTreeNode.Type.AAG_SC_SET )
            return null;
        return getChildNode( parentAagScSetNode, Gx2dJTreeNode.Type.SPRITE);
    }

    protected void checkTreeSelection() {

        TreePath [] selectionPaths = jTreeModel.getSelectionPaths();
        if ( selectionPaths == null )
            return;
        if ( selectionPaths.length < 2 )
            return;

        Gx2dJTreeNode firstNode = (Gx2dJTreeNode) selectionPaths[0].getLastPathComponent();

        for ( int i = 1; i < selectionPaths.length; i++ ) {
            Gx2dJTreeNode node = (Gx2dJTreeNode) selectionPaths[i].getLastPathComponent();
            if ( node.getType() != firstNode.getType() )
                jTreeModel.removeSelectionPath( selectionPaths[i] );
        }
    }


    protected void selectNode( Gx2dJTreeNode node ) {
        if ( node == null )
            return;
        if ( node.getPath() == null )
            return;
        TreePath path = new TreePath( node.getPath() );
        jTreeModel.setSelectionPath( path );
        jTreeModel.scrollPathToVisible( path );
        processTreeSingleSelection();
    }

    protected void expandToNode( Gx2dJTreeNode node ) {
        if ( node == null )
            return;
        if ( node.getChildCount() > 0 ) {
            node = (Gx2dJTreeNode) node.getChildAt(0);
        }

        jTreeModel.scrollPathToVisible( new TreePath( node.getPath() ) );
    }

    protected void expandToObject( Object object ) {
        Gx2dJTreeNode node = findNode( object );
        if ( node == null )
            return;
        expandToNode(node);
    }

    protected void changeNodesSelection( Gx2dJTreeNode node, boolean add ) {
        TreePath path = new TreePath( node.getPath() );
        if ( add ) {
            jTreeModel.addSelectionPath( path );
            jTreeModel.scrollPathToVisible( path );
        } else {
            jTreeModel.removeSelectionPath( path );
            processTreeSelection();
        }
    }

    protected void processTreeSelection() {
        if ( model == null )
            return;
        propertiesCellEditor.cancelCellEditing();

        checkTreeSelection();

        TreePath [] selectionPaths = jTreeModel.getSelectionPaths();

        if ( selectionPaths == null )
            return;

        if ( selectionPaths.length == 0 )
            return;

        if ( selectionPaths.length == 1 ) {
            processTreeSingleSelection();
        } else {
            processTreeMultiSelection();
        }
    }


    public static EditorScreen.ModelObjectType getModelObjectType( Gx2dJTreeNode.Type type ) {
        switch ( type ) {
            case MODEL:
                return EditorScreen.ModelObjectType.OT_Model;
            case BiScSET:
                break;
            case SPRITE:
                return EditorScreen.ModelObjectType.OT_Aag;
            case AAG_SC:
                break;
            case AAG_SC_SET:
                break;
            case BODY_HANDLER:
                return EditorScreen.ModelObjectType.OT_BodyItem;
            case BODY_ITEM_GROUP:
                break;
            case FIXTURE_SET:
                return EditorScreen.ModelObjectType.OT_FixtureSet;
            case FIXTURE_SET_GROUP:
                break;
            case JOINT_HANDLER:
                return EditorScreen.ModelObjectType.OT_JointItem;
            case JOINT_ITEM_GROUP:
                break;
        }
        return EditorScreen.ModelObjectType.OT_None;
    }

    protected void processTreeSingleSelection() {

        Gx2dJTreeNode selNode = (Gx2dJTreeNode) jTreeModel.getLastSelectedPathComponent();

        if ( selNode == null )
            return;
        if ( selNode.getUserObject() == null )
            return;

        EditorScreen.ModelObjectType objectType = getModelObjectType( selNode.type );

        jTableProperties.setModel( emptyTableModel );

        switch ( selNode.getType() ) {
            case MODEL:
                physModelPropertiesTableModel.setModel( model );
                jTableProperties.setModel(physModelPropertiesTableModel);
                break;
            case BiScSET:
                scItemTableModel.setHandler((ScContainer.Handler) selNode.getUserObject() );
                jTableProperties.setModel(scItemTableModel);
                break;
            case SPRITE:
                aagPropertiesTableModel.setAag((AnimatedActorGroup) selNode.getUserObject());
                jTableProperties.setModel(aagPropertiesTableModel);
                break;
            case AAG_SC:
                break;
            case AAG_SC_SET:
                scItemTableModel.setHandler((ScContainer.Handler) selNode.getUserObject() );
                jTableProperties.setModel(scItemTableModel);
                break;
            case BODY_HANDLER:
                BodyItem bi = ( BodyItem ) selNode.getUserObject();
                bodyPropertiesTableModel.setBodyItem(bi);
                jTableProperties.setModel(bodyPropertiesTableModel);
                break;
            case BODY_ITEM_GROUP:
                break;
            case FIXTURE_SET:
                FixtureSet fs = ( FixtureSet ) selNode.getUserObject();
                fixtureSetPropertiesTableModel.setFixtureSet(fs);
                jTableProperties.setModel( fixtureSetPropertiesTableModel );
                break;
            case FIXTURE_SET_GROUP:
                break;
            case JOINT_HANDLER:
                JointItem ji = (JointItem) selNode.getUserObject();
                jointPropertiesTableModel.setJointItem(ji);
                jTableProperties.setModel( jointPropertiesTableModel );
                break;
        }

        mainGui.setupEditors( selNode.getUserObject(), objectType );
    }

    protected void processTreeMultiSelection() {
        editorScreen.clearSelectedItems();
        TreePath [] selectionPaths = jTreeModel.getSelectionPaths();
        if ( selectionPaths == null )
            return;
        for ( TreePath tp : selectionPaths ) {
            Gx2dJTreeNode node = (Gx2dJTreeNode) tp.getLastPathComponent();
            editorScreen.addModelObject( node.getUserObject(), getModelObjectType( node.type ) );
        }
    }

    protected static DialogNewJointSelector dlgNewJointSelector = new DialogNewJointSelector();

    public void createNode() {
        TreePath [] selPaths = jTreeModel.getSelectionPaths();
        if ( selPaths == null )
            return;
        if ( selPaths.length > 1 )
            return;
        Gx2dJTreeNode parentNode = (Gx2dJTreeNode) jTreeModel.getLastSelectedPathComponent();
        Gx2dJTreeNode newNode = null;

        switch ( parentNode.getType() ) {

            case MODEL:
                newNode = createNewBiScSet();
                break;
            case BiScSET:
                break;
            case SPRITE:
                newNode = createAagNode( (AnimatedActorGroup) parentNode.getUserObject() );
                break;
            case AAG_SC:
                newNode = createAagScItem( ( (AnimatedActorGroup) parentNode.getUserObject() ).getScChildren() );
                break;
            case AAG_SC_SET:
                break;
            case BODY_HANDLER:
                break;
            case BODY_ITEM_GROUP:
                newNode = createNewBodyItem( (BiScSet) parentNode.getUserObject() );
                break;
            case FIXTURE_SET:
                break;
            case FIXTURE_SET_GROUP:
                newNode = createFixtureSet((BodyItem) parentNode.getUserObject() );
                break;
            case JOINT_HANDLER:
                break;
            case JOINT_ITEM_GROUP:
                if ( ! dlgNewJointSelector.execute() )
                    break;
                newNode = createEmptyJointItemNode((BiScSet) parentNode.getUserObject(), dlgNewJointSelector.getSelectedJointType() );
                break;
        }

        if ( newNode == null )
            return;

        parentNode.add( newNode );
        treeDataModel.nodeStructureChanged( parentNode );
        selectNode( newNode );
        mainGui.makeHistorySnapshot();
    }

    public Gx2dJTreeNode createNewBiScSet() {
        BiScSet newSet = new BiScSet( model );
        Integer id = model.getScBodyItems().generateId();
        model.getScBodyItems().addContent(id, newSet );

        ScContainer.Handler handler = new ScContainer.Handler(model.getScBodyItems(), id );
        Gx2dJTreeNode newNode = new Gx2dJTreeNode(Gx2dJTreeNode.Type.BiScSET, handler );
        loadPhysSetNode(newNode);
        return newNode;
    }

    public Gx2dJTreeNode createNewBodyItem( BiScSet bset ) {
        BodyItem bi = new BodyItem( bset );
        bi.setName( "NewBody");
        bset.addBodyItem( bi );
        Gx2dJTreeNode node = new Gx2dJTreeNode(Gx2dJTreeNode.Type.BODY_HANDLER, bi );
        loadBodyItemNodes( node );
        return node;
    }

    public Gx2dJTreeNode createAagNode( AnimatedActorGroup parentAag ) {
        AnimatedActorGroup aag = new AnimatedActorGroup( SkrGdxApplication.get().getAtlas() );
        aag.setName("NewAag");
        parentAag.addChildAag(aag);
        Gx2dJTreeNode node = new Gx2dJTreeNode(Gx2dJTreeNode.Type.SPRITE, aag);
        loadAagNodes( node );
        return node;
    }

    public Gx2dJTreeNode createAagScItem( AagScContainer container ) {
        AnimatedActorGroup aag = new AnimatedActorGroup( SkrGdxApplication.get().getAtlas() );
        aag.setName("NewAag");
        Integer id = container.generateId();
        container.addContent(id, aag);
        ScContainer.Handler handler = new ScContainer.Handler( container, id );
        Gx2dJTreeNode node = new Gx2dJTreeNode(Gx2dJTreeNode.Type.AAG_SC_SET, handler );
        loadAagScSetNodes(node);
        return node;
    }

    public Gx2dJTreeNode createFixtureSet( BodyItem bi ) {
        FixtureSet fs = new FixtureSet( bi );
        fs.setName("newFixtureSet");
        bi.addFixtureSet( fs );
        return new Gx2dJTreeNode( Gx2dJTreeNode.Type.FIXTURE_SET, fs );
    }

    public void updateFixtures( Array<ShapeDescription> shpDescriptions ) {
        FixtureSet fs = fixtureSetPropertiesTableModel.getFixtureSet();
        fs.createFixtures( shpDescriptions );
        fixtureSetPropertiesTableModel.fireTableDataChanged();
    }

    public Gx2dJTreeNode createEmptyJointItemNode( BiScSet bset, JointDef.JointType type ) {
        JointItem ji = JointItemFactory.create( type, "new_"+type, bset );
        if ( ji == null )
            return null;
        bset.addJointItem( ji );
        return new Gx2dJTreeNode(Gx2dJTreeNode.Type.JOINT_HANDLER, ji );
    }

    public void updatePropertiesTable() {
        TableModel tm = jTableProperties.getModel();
        if ( tm == null )
            return;
        if ( tm instanceof PropertiesBaseTableModel ) {
            PropertiesBaseTableModel bm = (PropertiesBaseTableModel) tm;
            bm.fireTableDataChanged();
        }
    }


    public Gx2dJTreeNode findParentNode( Gx2dJTreeNode node, Gx2dJTreeNode.Type type ) {
        if ( node.getParent() == null )
            return null;
        Gx2dJTreeNode parentNode = (Gx2dJTreeNode) node.getParent();
        if ( parentNode.type == type )
            return parentNode;
        return findParentNode( parentNode, type );
    }

    public Array<Gx2dJTreeNode> findAllChildNodes( Gx2dJTreeNode parentNode, Gx2dJTreeNode.Type type, int level ) {
        Array<Gx2dJTreeNode> childNodes = new Array<Gx2dJTreeNode>();

        for ( int i = 0; i < parentNode.getChildCount(); i++ ) {
            Gx2dJTreeNode chNode = (Gx2dJTreeNode) parentNode.getChildAt( i );
            if ( chNode.type == type )
                childNodes.add( chNode );
        }

        if ( level > 0 ) {
            for (int i = 0; i < parentNode.getChildCount(); i++) {
                Gx2dJTreeNode chNode = (Gx2dJTreeNode) parentNode.getChildAt(i);
                childNodes.addAll( findAllChildNodes(chNode, type, level - 1) );
            }
        }

        return childNodes;
    }



    public Gx2dJTreeNode findNode( Object object ) {
        return findNode( rootNode, object );
    }

    protected Gx2dJTreeNode findNode( Gx2dJTreeNode parentNode, Object object ) {
        if ( parentNode.getUserObject() == object )
            return parentNode;
        for ( int index = 0; index < parentNode.getChildCount(); index ++ ) {
            Gx2dJTreeNode tn = (Gx2dJTreeNode) parentNode.getChildAt( index );
            tn = findNode( tn, object );
            if ( tn != null )
                return tn;
        }
        return null;
    }


    protected Gx2dJTreeNode findNode( Object object, Gx2dJTreeNode.Type type ) {
        return findNode( rootNode, object, type );
    }

    protected Gx2dJTreeNode findNode(Gx2dJTreeNode parentNode, Object object, Gx2dJTreeNode.Type type ) {
        if ( parentNode.getUserObject() == object && parentNode.getType() == type ) {
            return parentNode;
        }

        for ( int index = 0; index < parentNode.getChildCount(); index ++ ) {
            Gx2dJTreeNode tn = (Gx2dJTreeNode) parentNode.getChildAt( index );
            tn = findNode( tn, object, type);
            if ( tn != null )
                return tn;
        }
        return null;
    }

    protected Gx2dJTreeNode findNode( Object object, EditorScreen.ModelObjectType mot ) {
        Gx2dJTreeNode foundNode = null;
        switch ( mot ) {
            case OT_None:
                return null;
            case OT_Model:
                foundNode = findNode( object, Gx2dJTreeNode.Type.MODEL );
                break;
            case OT_BodyItem:
                foundNode = findNode( object, Gx2dJTreeNode.Type.BODY_HANDLER);
                break;
            case OT_Aag:
                foundNode = findNode( object, Gx2dJTreeNode.Type.SPRITE);
                break;
            case OT_FixtureSet:
                foundNode = findNode( object, Gx2dJTreeNode.Type.FIXTURE_SET );
                break;
            case OT_JointItem:
                foundNode = findNode( object, Gx2dJTreeNode.Type.JOINT_HANDLER);
                break;
        }
        return foundNode;
    }

    protected void selectObject( Object object, EditorScreen.ModelObjectType mot ) {
        Gx2dJTreeNode node = findNode( object, mot );
        if ( node == null )
            return;
        selectNode( node );
    }


    protected void selectObjects( Array<? extends Object> objects, EditorScreen.ModelObjectType mot ) {

        jTreeModel.clearSelection();

        Array< TreePath > newSelection = new Array<TreePath>();

        for ( Object object: objects ) {
            Gx2dJTreeNode node = findNode( object, mot );
            if ( node == null )
                continue;
            newSelection.add( new TreePath( node.getPath() ) );
        }

        TreePath [] selPaths = new TreePath[ newSelection.size ];
        for ( int i = 0; i < selPaths.length; i++)
            selPaths[ i ] = newSelection.get( i );

        jTreeModel.setSelectionPaths( selPaths );

    }

    protected void changeObjectSelection( Object object, EditorScreen.ModelObjectType mot, boolean add ) {
        Gx2dJTreeNode node = findNode( object, mot );
        if ( node == null )
            return;
        changeNodesSelection( node, add );
    }


    protected int showRemoveNodeYesNoDialog() {
        return JOptionPane.showOptionDialog( mainGui, "Are you sure?", "Remove Node!",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null );
    }

    public void removeNode() {
        if ( model == null )
            return;

        if ( showRemoveNodeYesNoDialog() > 0 )
            return;


        TreePath [] selectedPaths = jTreeModel.getSelectionPaths();
        if ( selectedPaths == null )
            return;

        Gx2dJTreeNode parentNode = null;

        for ( TreePath tp : selectedPaths ) {
            Gx2dJTreeNode node = (Gx2dJTreeNode) tp.getLastPathComponent();
            parentNode = removeNode( node );
            if ( parentNode != null )
                treeDataModel.nodeStructureChanged( parentNode );
        }

        if ( parentNode != null ) {
            selectNode(parentNode);
        }

        mainGui.makeHistorySnapshot();
    }

    protected Gx2dJTreeNode removeNode( Gx2dJTreeNode node ) {

        switch ( node.getType() ) {
            case MODEL:
                break;
            case BiScSET:
                return removeBiScSetNode( node );
            case SPRITE:
                return removeAagNode( node );
            case AAG_SC:
                break;
            case AAG_SC_SET:
                return removeAagScSetNode( node );
            case BODY_HANDLER:
                return removeBodyItemNode( node );
            case BODY_ITEM_GROUP:
                break;
            case FIXTURE_SET:
                return removeFixtureSetNode( node );
            case FIXTURE_SET_GROUP:
                break;
            case JOINT_HANDLER:
                return removeJointItemNode( node );
            case JOINT_ITEM_GROUP:
                break;
        }
        return null;
    }

    protected Gx2dJTreeNode removeBiScSetNode( Gx2dJTreeNode node ) {
        Gx2dJTreeNode parentNode = (Gx2dJTreeNode) node.getParent();
        ScContainer.Handler handler = (ScContainer.Handler) node.getUserObject();
        handler.container.removeContent( handler.id );
        node.removeFromParent();
        return parentNode;
    }

    protected Gx2dJTreeNode removeBodyItemNode( Gx2dJTreeNode node ) {
        Gx2dJTreeNode parentNode = (Gx2dJTreeNode) node.getParent();
        BiScSet bset = (BiScSet) parentNode.getUserObject();
        BodyItem bi = (BodyItem) node.getUserObject();

        Gx2dJTreeNode jgNode = getJointGroupNode((Gx2dJTreeNode) parentNode.getParent());
        Array<Gx2dJTreeNode> jiNodesToRemove = new Array<Gx2dJTreeNode>();

        for ( int i = 0; i < jgNode.getChildCount(); i++) {
            Gx2dJTreeNode jiNode = (Gx2dJTreeNode) jgNode.getChildAt( i );
            JointItem ji = (JointItem) jiNode.getUserObject();
            if ( ji.getBodyAId() == bi.getId() ) {
                jiNodesToRemove.add( jiNode );
                continue;
            }
            if ( ji.getBodyBId() == bi.getId() ) {
                jiNodesToRemove.add( jiNode );
                continue;
            }
        }
        for ( Gx2dJTreeNode jiNode : jiNodesToRemove ) {
            removeJointItemNode( jiNode );
        }
        treeDataModel.nodeStructureChanged( jgNode );
        bset.removeBodyItem(bi);
        node.removeFromParent();
        cleanupJointItemNodes((Gx2dJTreeNode) parentNode.getParent());
        return parentNode;
    }

    protected void cleanupJointItemNodes(Gx2dJTreeNode biScSetNode) {
        Gx2dJTreeNode jiGroupNode = null;
        for ( int i = 0; i < biScSetNode.getChildCount(); i++) {
            Gx2dJTreeNode node = (Gx2dJTreeNode) biScSetNode.getChildAt( i );
            if ( node.getType() == Gx2dJTreeNode.Type.JOINT_ITEM_GROUP ) {
                jiGroupNode = node;
                break;
            }
        }
        if ( jiGroupNode == null )
            return ;

        BiScSet bset = (BiScSet) jiGroupNode.getUserObject();
        Array<Gx2dJTreeNode> nodesToRemove  = new Array<Gx2dJTreeNode>();

        for ( int i = 0; i < jiGroupNode.getChildCount(); i++ ) {
            Gx2dJTreeNode node = (Gx2dJTreeNode) jiGroupNode.getChildAt( i );
            JointItem ji = (JointItem) node.getUserObject();
            if ( !bset.getJointItems().contains( ji, true) ) {
                nodesToRemove.add( node );
            }
        }
        for ( Gx2dJTreeNode node : nodesToRemove )
            node.removeFromParent();
    }

    protected Gx2dJTreeNode removeJointItemNode( Gx2dJTreeNode jiNode ) {
        Gx2dJTreeNode parentNode = (Gx2dJTreeNode) jiNode.getParent();
        JointItem ji = (JointItem) jiNode.getUserObject();
        BiScSet bset = (BiScSet) parentNode.getUserObject();
        bset.removeJointItem( ji );
        jiNode.removeFromParent();
        return parentNode;
    }

    protected Gx2dJTreeNode removeFixtureSetNode( Gx2dJTreeNode fsNode ) {
        Gx2dJTreeNode parentNode = (Gx2dJTreeNode) fsNode.getParent();
        BodyItem bi = (BodyItem) parentNode.getUserObject();
        FixtureSet fs = (FixtureSet) fsNode.getUserObject();
        bi.removeFixtureSet( fs );
        fsNode.removeFromParent();
        return parentNode;
    }

    protected Gx2dJTreeNode removeAagNode( Gx2dJTreeNode aagNode ) {
        Gx2dJTreeNode parentNode = (Gx2dJTreeNode) aagNode.getParent();
        if ( parentNode.getType() == Gx2dJTreeNode.Type.BODY_HANDLER)
            return null;
        if ( parentNode.getType() == Gx2dJTreeNode.Type.AAG_SC_SET )
            return null;
        AnimatedActorGroup parentAag = (AnimatedActorGroup) parentNode.getUserObject();
        AnimatedActorGroup aag = (AnimatedActorGroup) aagNode.getUserObject();
        parentAag.removeChild( aag );
        aagNode.removeFromParent();
        return parentNode;
    }

    protected Gx2dJTreeNode removeAagScSetNode( Gx2dJTreeNode aagScSetNode ) {
        Gx2dJTreeNode parentNode = (Gx2dJTreeNode) aagScSetNode.getParent();
        ScContainer.Handler handler = (ScContainer.Handler) aagScSetNode.getUserObject();
        handler.container.removeContent( handler.id );
        aagScSetNode.removeFromParent();
        return parentNode;
    }

    protected void clearNode( Gx2dJTreeNode node ) {
        switch ( node.getType() ) {
            case MODEL:
                clearRootNode();
                break;
            case BiScSET:
                clearBiScNode( node );
                break;
            case SPRITE:
                clearAagNode( node );
                break;
            case AAG_SC:
                clearAagScNode( node );
                break;
            case AAG_SC_SET:
                clearAagScSetNode( node );
                break;
            case BODY_HANDLER:
                clearBodyItemNode( node );
                break;
            case BODY_ITEM_GROUP:
                clearBodyItemGroupNode( node );
                break;
            case FIXTURE_SET:
                return;
            case FIXTURE_SET_GROUP:
                clearFixtureSetGroupNode( node );
            case JOINT_HANDLER:
                return;
            case JOINT_ITEM_GROUP:
                clearJointItemGroupNode( node );
        }

        treeDataModel.nodeStructureChanged( node );
    }

    protected void clearRootNode( ) {
        model.clearModel();
        rootNode.removeAllChildren();
        loadPhysSetJNodes();
    }

    protected void clearBiScNode( Gx2dJTreeNode node ) {
        ScContainer.Handler handler = (ScContainer.Handler) node.getUserObject();
        BiScSet bset = (BiScSet) handler.get();
        bset.destroyPhysics();
        node.removeAllChildren();
        loadPhysSetNode(node);
    }

    protected void clearBodyItemGroupNode( Gx2dJTreeNode node ) {
        BiScSet bset = (BiScSet) node.getUserObject();
        bset.destroyPhysics();
        node.removeAllChildren();
        loadBodyHandlerNode(node);
    }

    protected void clearJointItemGroupNode( Gx2dJTreeNode node ) {
        BiScSet bset = (BiScSet) node.getUserObject();
        bset.removeAllJointItems();
        node.removeAllChildren();
        loadJointHandlerNode(node);
    }

    protected void clearBodyItemNode( Gx2dJTreeNode node ) {
        BodyItem bi = (BodyItem) node.getUserObject();
        bi.removeAllFixtureSets();
        bi.clearAag();
        node.removeAllChildren();
        loadBodyItemNodes( node );
    }

    protected void clearFixtureSetGroupNode( Gx2dJTreeNode node ) {
        BodyItem bi = (BodyItem) node.getUserObject();
        bi.removeAllFixtureSets();
        node.removeAllChildren();
        loadFixtureSetsNodes( node );
    }

    protected void clearAagNode( Gx2dJTreeNode node ) {
        AnimatedActorGroup aag = (AnimatedActorGroup) node.getUserObject();
        aag.clearAag();
        node.removeAllChildren();
        loadAagNodes( node );
    }

    protected void clearAagScNode( Gx2dJTreeNode node ) {
        AnimatedActorGroup aag = (AnimatedActorGroup) node.getUserObject();
        aag.getScChildren().clearContent();
        node.removeAllChildren();
        loadAagScNodes( node );
    }

    protected void clearAagScSetNode( Gx2dJTreeNode node ) {
        ScContainer.Handler handler = (ScContainer.Handler) node.getUserObject();
        AnimatedActorGroup aag = (AnimatedActorGroup) handler.get();
        aag.clearAag();
        node.removeAllChildren();
        loadAagScSetNodes(node);
    }


    protected void clearSourceNode( Gx2dJTreeNode sourceNode ) {
        clearNode(sourceNode);
        treeDataModel.nodeStructureChanged( sourceNode );
    }


    protected void removeSourceNode( Gx2dJTreeNode sourceNode ) {
        Gx2dJTreeNode oldParent = (Gx2dJTreeNode) sourceNode.getParent();
        removeNode( sourceNode );
        if ( oldParent != null )
            treeDataModel.nodeStructureChanged( oldParent );
    }


    public boolean isAagMovable( AnimatedActorGroup aag, AnimatedActorGroup newParentAag ) {
        if ( aag == newParentAag )
            return false;
        if ( aag.isParentOf( newParentAag ) )
            return false;
        if ( newParentAag == aag.getParent() )
            return false;
        return true;
    }

    public void duplicateNode() {
        TreePath [] selectionPaths = jTreeModel.getSelectionPaths();

        if ( selectionPaths == null )
            return;

        Object newObject = null ;

        for ( TreePath selPath : selectionPaths ) {
            Gx2dJTreeNode node = (Gx2dJTreeNode) selPath.getLastPathComponent();
            if ( node.getParent() == null )
                continue;
            newObject = moveNode( node, (Gx2dJTreeNode) node.getParent(), true);
        }

        Gx2dJTreeNode node = findNode( newObject );
        selectNode( node );
        mainGui.makeHistorySnapshot();
    }

    public Object moveNode( Gx2dJTreeNode sourceNode, Gx2dJTreeNode newParentNode, boolean copy ) {
        if ( sourceNode == null ) {
            return null;
        } else {
//            Gdx.app.log("PhysModelStructureGuiProcessing.moveNode", "Source: " + sourceNode + " Target: " + newParentNode );
        }

        Gx2dJTreeNode.Type srcType = sourceNode.getType();
        Gx2dJTreeNode.Type tgtType = newParentNode.getType();
        Gx2dJTreeNode oldParent = (Gx2dJTreeNode) sourceNode.getParent();
        Object newObject = null;


        boolean removeSourceWhenMove = false;
        boolean clearSourceWhenMove = false;

        switch ( srcType ) {
            case MODEL:
                return null;
            case BiScSET:
                removeSourceWhenMove = true;
                if ( tgtType == Gx2dJTreeNode.Type.MODEL) {
                    newObject = copyBiScSetNode(sourceNode);
                } else if ( tgtType == Gx2dJTreeNode.Type.BiScSET ) {
                    if ( ( sourceNode == newParentNode ) && !copy )
                        return null;
                    newObject = copyBiScSetNode( sourceNode, newParentNode );
                } else {
                    return null;
                }
                break;
            case SPRITE:
                removeSourceWhenMove = true;
                if ( tgtType == Gx2dJTreeNode.Type.SPRITE) {
                    AnimatedActorGroup sAag = (AnimatedActorGroup) sourceNode.getUserObject();
                    AnimatedActorGroup tAag = (AnimatedActorGroup) newParentNode.getUserObject();
                    if ( !isAagMovable( sAag, tAag ) && !copy )
                        return null;
                    newObject = copyAagNodeToAagNode( sourceNode, newParentNode );
                } else if (tgtType == Gx2dJTreeNode.Type.AAG_SC) {
                        AnimatedActorGroup sAag = (AnimatedActorGroup) sourceNode.getUserObject();
                        AnimatedActorGroup tAag = (AnimatedActorGroup) newParentNode.getUserObject();
                        if ( !isAagMovable( sAag, tAag ) && !copy )
                            return null;
                        newObject = copyAagNodeToAagScNode( sourceNode, newParentNode );
                } else if ( tgtType == Gx2dJTreeNode.Type.AAG_SC_SET ) {
                    Gx2dJTreeNode tAagNode = getAagNode( newParentNode );
                    if ( tAagNode == null )
                        return null;
                    return moveNode( sourceNode, (Gx2dJTreeNode) newParentNode.getParent(), copy );
                } else {
                    return null;
                }
                break;
            case AAG_SC:
                clearSourceWhenMove = true;
                if ( tgtType == Gx2dJTreeNode.Type.SPRITE) {
                    AnimatedActorGroup sAag = (AnimatedActorGroup) sourceNode.getUserObject();
                    AnimatedActorGroup tAag = (AnimatedActorGroup) newParentNode.getUserObject();
                    if (!isAagMovable(sAag, tAag) && !copy)
                        return null;
                    newObject = copyAagScNodeToAagNode(sourceNode, newParentNode);
                } else if ( tgtType == Gx2dJTreeNode.Type.AAG_SC ) {
                    return moveNode(sourceNode, (Gx2dJTreeNode) newParentNode.getParent(), copy);
                } else if ( tgtType == Gx2dJTreeNode.Type.AAG_SC_SET ) {
                    Gx2dJTreeNode aagNode = getAagNode( newParentNode );
                    if ( aagNode == null )
                        return null;
                    return moveNode( sourceNode, aagNode, copy );
                } else {
                    return null;
                }
                break;
            case AAG_SC_SET:
                if ( tgtType == Gx2dJTreeNode.Type.SPRITE) {
                    removeSourceWhenMove = true;
                    ScContainer.Handler handler = (ScContainer.Handler) sourceNode.getUserObject();
                    AnimatedActorGroup srcAag = (AnimatedActorGroup) handler.get();
                    AnimatedActorGroup tgtAag = (AnimatedActorGroup) newParentNode.getUserObject();
                    if ( srcAag.isParentOf( tgtAag ) && !copy ) {
                        return null;
                    }
                    newObject = copyAagScSetNodeToAagNode( sourceNode, newParentNode );
                } else if ( tgtType == Gx2dJTreeNode.Type.AAG_SC ) {
                    return moveNode( sourceNode, (Gx2dJTreeNode) newParentNode.getParent(), copy );
                } else if ( tgtType == Gx2dJTreeNode.Type.AAG_SC_SET ) {
                    Gx2dJTreeNode aagNode = getAagNode( newParentNode );
                    if ( aagNode == null )
                        return null;
                    return moveNode( sourceNode, aagNode, copy );
                } else {
                    return null;
                }
                break;
            case BODY_HANDLER:
                if ( tgtType == Gx2dJTreeNode.Type.BiScSET ) {
                    removeSourceWhenMove = true;
                    BodyItem bi = (BodyItem) sourceNode.getUserObject();
                    ScContainer.Handler handler = (ScContainer.Handler) newParentNode.getUserObject();
                    BiScSet bset = (BiScSet) handler.get();
                    if ( bset.getBodyItems().contains( bi, true)  && !copy )
                        return false;
                    if ( !copy )
                        editorScreen.setModelObject( null, EditorScreen.ModelObjectType.OT_None );
                    newObject = copyBodyItemNode( sourceNode, newParentNode );

                } else if ( tgtType == Gx2dJTreeNode.Type.BODY_ITEM_GROUP ) {
                    BodyItem bi = (BodyItem) sourceNode.getUserObject();
                    BiScSet bset = (BiScSet) newParentNode.getUserObject();
                    if ( bset.getBodyItems().contains( bi, true)  && !copy )
                        return null;
                     return moveNode(sourceNode, (Gx2dJTreeNode) newParentNode.getParent(), copy );


                } else {
                    return null;
                }

                break;
            case BODY_ITEM_GROUP:
                if ( tgtType == Gx2dJTreeNode.Type.BiScSET ) {
                    clearSourceWhenMove = true;
                    ScContainer.Handler handler = (ScContainer.Handler) newParentNode.getUserObject();
                    BiScSet bset = (BiScSet) handler.get();
                    if ((bset == sourceNode.getUserObject()) && !copy)
                        return null;
                    newObject = copyBodyItemGroupNode(sourceNode, newParentNode);
                } else if ( tgtType == Gx2dJTreeNode.Type.BODY_ITEM_GROUP ) {
                    return moveNode( sourceNode, (Gx2dJTreeNode) newParentNode.getParent(), copy );
                } else {
                    return null;
                }
                break;
            case FIXTURE_SET:
                if ( tgtType == Gx2dJTreeNode.Type.BODY_HANDLER) {
                    removeSourceWhenMove = true;
                    BodyItem bi = (BodyItem) newParentNode.getUserObject();
                    FixtureSet fs = (FixtureSet) sourceNode.getUserObject();
                    if ( bi.getFixtureSets().contains( fs, true ) && !copy )
                        return null;
                    if ( !copy )
                        editorScreen.setModelObject( null, EditorScreen.ModelObjectType.OT_None );
                    newObject = copyFixtureSetNode( sourceNode, newParentNode );
                } else if ( tgtType == Gx2dJTreeNode.Type.FIXTURE_SET_GROUP ) {
                    return moveNode( sourceNode, (Gx2dJTreeNode) newParentNode.getParent(), copy );
                } else {
                    return null;
                }
                break;
            case FIXTURE_SET_GROUP:
                if ( tgtType == Gx2dJTreeNode.Type.BODY_HANDLER) {
                    clearSourceWhenMove = true;
                    BodyItem sBi = (BodyItem) sourceNode.getUserObject();
                    BodyItem tBi = (BodyItem) newParentNode.getUserObject();
                    if ( ( sBi == tBi ) && !copy )
                        return null;
                    newObject = copyFixtureSetGroupNode( sourceNode, newParentNode );
                } else if ( tgtType == Gx2dJTreeNode.Type.FIXTURE_SET_GROUP ) {
                    return moveNode( sourceNode, (Gx2dJTreeNode) newParentNode.getParent(), copy );
                } else {
                    return null;
                }
                break;
            case JOINT_HANDLER:
                return false;
            case JOINT_ITEM_GROUP:
                return false;
        }

        if ( !copy ) {
            if ( clearSourceWhenMove ) {
                clearSourceNode( sourceNode );
            }
            if ( removeSourceWhenMove ) {
                removeSourceNode(sourceNode);
            }
        }

        treeDataModel.nodeStructureChanged(newParentNode);
        if ( copy ) {
            expandToObject( sourceNode.getUserObject() );
        } else {
            expandToObject(oldParent.getUserObject());
        }

        if ( newObject != null ) {
            expandToObject( newObject );
        }


        return newObject;
    }

    public Object copyBiScSetNode(Gx2dJTreeNode sNode) {
        if ( sNode.getType() != Gx2dJTreeNode.Type.BiScSET )
            return null;

        ScContainer.Handler handler = (ScContainer.Handler) sNode.getUserObject();
        Gdx.app.log("PhysModelStructureGuiProcessing.copyBiScSetNode", "sNode: " + sNode);

        Integer newId = handler.container.generateId();
        BiScContainer container = (BiScContainer) handler.container;
        container.copyContent( handler.id, newId, null );
        handler = new ScContainer.Handler( container, newId );
        Gx2dJTreeNode newNode = new Gx2dJTreeNode(Gx2dJTreeNode.Type.BiScSET, handler );
        loadPhysSetNode(newNode);
        rootNode.add( newNode );
        return handler;
    }

    public Object copyBiScSetNode( Gx2dJTreeNode sNode, Gx2dJTreeNode newPNode ) {
        if ( sNode.getType() != Gx2dJTreeNode.Type.BiScSET )
            return null;
        if ( newPNode.getType() != Gx2dJTreeNode.Type.BiScSET )
            return null;

        ScContainer.Handler handler = (ScContainer.Handler) sNode.getUserObject();
        BiScSet bset = (BiScSet) handler.container.getContent( handler.id );

        handler = (ScContainer.Handler) newPNode.getUserObject();
        BiScSet bsetTarget = (BiScSet) handler.container.getContent( handler.id );

        bsetTarget.copyBiScSetContent( bset, null );
        newPNode.removeAllChildren();
        loadPhysSetNode(newPNode);
        return bsetTarget;
    }

    public Object copyBodyItemGroupNode(Gx2dJTreeNode sNode, Gx2dJTreeNode newPNode) {
        if ( sNode.getType() != Gx2dJTreeNode.Type.BODY_ITEM_GROUP )
            return null;
        if ( newPNode.getType() != Gx2dJTreeNode.Type.BiScSET )
            return null;
        ScContainer.Handler handler = (ScContainer.Handler) newPNode.getUserObject();
        BiScSet bset = (BiScSet) handler.get();
        BiScSet sBset = (BiScSet) sNode.getUserObject();
        bset.copyBodyItemArray( sBset.getBodyItems(), null );
        newPNode.removeAllChildren();
        loadPhysSetNode(newPNode);
        return bset;
    }

    public Object copyBodyItemNode(Gx2dJTreeNode sNode, Gx2dJTreeNode newPNode) {
        if ( sNode.getType() != Gx2dJTreeNode.Type.BODY_HANDLER)
            return null;
        if ( newPNode.getType() != Gx2dJTreeNode.Type.BiScSET )
            return null;
        BodyItem bi = (BodyItem) sNode.getUserObject();
        ScContainer.Handler handler = (ScContainer.Handler) newPNode.getUserObject();
        BiScSet bset = (BiScSet) handler.get();
        Object ret = bset.copyBodyItem( bi, null );
        newPNode.removeAllChildren();
        loadPhysSetNode(newPNode);
        return ret;
    }

    public Object copyFixtureSetGroupNode( Gx2dJTreeNode sNode, Gx2dJTreeNode newPNode ) {
        if ( sNode.getType() != Gx2dJTreeNode.Type.FIXTURE_SET_GROUP )
            return null;
        if ( newPNode.getType() != Gx2dJTreeNode.Type.BODY_HANDLER)
            return null;
        BodyItem bi = (BodyItem) newPNode.getUserObject();
        BodyItem sBi = (BodyItem) sNode.getUserObject();

        bi.copyFixtureSetArray(sBi.getFixtureSets());

        Gx2dJTreeNode fsgNode = getFixtureSetGroupNode( newPNode );
        if ( fsgNode == null )
            return null;

        fsgNode.removeAllChildren();
        loadFixtureSetsNodes( fsgNode );
        return bi;
    }

    public Object copyFixtureSetNode( Gx2dJTreeNode sNode, Gx2dJTreeNode newPNode ) {
        if ( sNode.getType() != Gx2dJTreeNode.Type.FIXTURE_SET )
            return null;
        if ( newPNode.getType() != Gx2dJTreeNode.Type.BODY_HANDLER)
            return null;
        FixtureSet fs = (FixtureSet) sNode.getUserObject();
        BodyItem bi = (BodyItem) newPNode.getUserObject();
        FixtureSet newFs = bi.copyFixtureSet( fs );

        Gx2dJTreeNode fsgNode = getFixtureSetGroupNode( newPNode );
        if ( fsgNode == null )
            return null;

        fsgNode.removeAllChildren();
        loadFixtureSetsNodes( fsgNode );
        return newFs;
    }

    public Object copyAagScNodeToAagNode( Gx2dJTreeNode sNode, Gx2dJTreeNode newPNode ) {
        if ( sNode.getType() != Gx2dJTreeNode.Type.AAG_SC )
            return null;
        if ( newPNode.getType() != Gx2dJTreeNode.Type.SPRITE)
            return null;
        AnimatedActorGroup sAag = (AnimatedActorGroup) sNode.getUserObject();
        AagScContainer sCont = sAag.getScChildren();
        AnimatedActorGroup tAag = (AnimatedActorGroup) newPNode.getUserObject();
        AagScContainer tCont = tAag.getScChildren();
        tCont.copyContainer( sCont );

        Gx2dJTreeNode aagScNode = getAagScNode( newPNode );
        aagScNode.removeAllChildren();
        loadAagScNodes( aagScNode );
        return tAag;
    }

    public Object copyAagScSetNodeToAagNode( Gx2dJTreeNode sNode, Gx2dJTreeNode newPNode ) {
        if ( sNode.getType() != Gx2dJTreeNode.Type.AAG_SC_SET )
            return null;
        if ( newPNode.getType() != Gx2dJTreeNode.Type.SPRITE)
            return null;
        ScContainer.Handler handler = (ScContainer.Handler) sNode.getUserObject();
        AnimatedActorGroup tAag = (AnimatedActorGroup) newPNode.getUserObject();
        tAag.getScChildren().addContentAsCopy( handler );
        Gx2dJTreeNode scNode = getAagScNode( newPNode );
        if ( scNode == null )
            return null;
        scNode.removeAllChildren();
        loadAagScNodes( scNode );
        return tAag;
    }

    public Object copyAagNodeToAagScNode( Gx2dJTreeNode sNode, Gx2dJTreeNode newPNode ) {
        if ( sNode.getType() != Gx2dJTreeNode.Type.SPRITE)
            return null;
        if ( newPNode.getType() != Gx2dJTreeNode.Type.AAG_SC )
            return null;
        AnimatedActorGroup sAag = (AnimatedActorGroup) sNode.getUserObject();
        AnimatedActorGroup tAag = (AnimatedActorGroup) newPNode.getUserObject();
        tAag.getScChildren().addContentAsCopy( tAag.getScChildren().generateId(), sAag );
        newPNode.removeAllChildren();
        loadAagScNodes(newPNode);
        return tAag;
    }

    public Object copyAagNodeToAagNode( Gx2dJTreeNode sNode, Gx2dJTreeNode newPNode ) {
        if ( sNode.getType() != Gx2dJTreeNode.Type.SPRITE)
            return null;
        if ( newPNode.getType() != Gx2dJTreeNode.Type.SPRITE)
            return null;
        AnimatedActorGroup sAag = (AnimatedActorGroup) sNode.getUserObject();
        AnimatedActorGroup tAag = (AnimatedActorGroup) newPNode.getUserObject();
        AnimatedActorGroup aag = AnimatedActorGroup.createFromDescription( sAag.getAagDescription(), sAag.getAtlas() );
        tAag.addChildAag( aag );
        newPNode.removeAllChildren();
        loadAagNodes( newPNode );
        return tAag;
    }

    public void duplicateNode( int number, float xOffset, float yOffset, float rotation ) {
        TreePath [] selectionPaths = jTreeModel.getSelectionPaths();

        if ( selectionPaths == null )
            return;

        Object newObject = null ;

        Array<Object> objectArray = new Array<Object>();

        for ( TreePath selPath : selectionPaths ) {
            Gx2dJTreeNode node = (Gx2dJTreeNode) selPath.getLastPathComponent();
            objectArray.add( node.getUserObject() );
        }

        for ( Object obj:  objectArray ) {
            Gx2dJTreeNode node = findNode( obj );
            if ( node.getParent() == null )
                continue;
            duplicateNode(node, number, xOffset, yOffset, rotation);

        }


        mainGui.makeHistorySnapshot();


    }


    protected void duplicateNode(Gx2dJTreeNode node, int number, float xOffset, float yOffset, float rotation) {
        if ( node.getParent() == null )
            return;

        Gx2dJTreeNode parentNode = (Gx2dJTreeNode) node.getParent();


        switch (node.getType()) {
            case MODEL:
                return;
            case BiScSET:
                return;
            case SPRITE:
                if ( parentNode.getType() != Gx2dJTreeNode.Type.SPRITE)
                    return;
                AnimatedActorGroup parentAag = (AnimatedActorGroup) parentNode.getUserObject();
                AnimatedActorGroup aag = (AnimatedActorGroup) node.getUserObject();
                AagDescription aagDesc = aag.getAagDescription();

                for ( int i = 0; i < number; i++ ) {
                    AnimatedActorGroup newAag = AnimatedActorGroup.createFromDescription( aagDesc, aag.getAtlas() );
                    parentAag.addChildAag( newAag );
                    newAag.setPosition( xOffset * (i+1) + newAag.getX(),
                            yOffset * (i+1) + newAag.getY() );
                    newAag.setRotation( newAag.getRotation() + rotation * (i+1) );
                    newAag.setName( newAag.getName() + "_c" + (i+1) );
                }
                parentNode.removeAllChildren();
                loadAagNodes( parentNode );
                break;
            case AAG_SC:
                return;
            case AAG_SC_SET:
                return;
            case BODY_HANDLER:
                for ( int i = 0; i < number; i ++ ) {
                    BodyItem bi = (BodyItem) node.getUserObject();
                    BiScSet bset = (BiScSet) parentNode.getUserObject();
                    BodyItem newBi = bset.copyBodyItem( bi, null );
                    Body body = newBi.getBody();
                    body.setTransform(PhysWorld.get().toPhys(xOffset * (i + 1) )  + body.getPosition().x,
                            PhysWorld.get().toPhys(yOffset * (i + 1) )  + body.getPosition().y,
                            MathUtils.degreesToRadians * rotation * (i + 1) + body.getAngle());
                    newBi.setName( newBi.getName() + "_c" + (i+1) );
                }
                parentNode.removeAllChildren();
                loadBodyHandlerNode(parentNode);
                break;
            case BODY_ITEM_GROUP:
                return;
            case FIXTURE_SET:
                return;
            case FIXTURE_SET_GROUP:
                return;
            case JOINT_HANDLER:
                return;
            case JOINT_ITEM_GROUP:
                return;
        }

        treeDataModel.nodeStructureChanged( parentNode );
        mainGui.makeHistorySnapshot();
    }


    public void reloadJointItemNodes() {
        Gx2dJTreeNode node = (Gx2dJTreeNode) jTreeModel.getLastSelectedPathComponent();
        if ( node == null )
            return;
        reloadJointItemNodes( node );
    }

    public void reloadJointItemNodes( Gx2dJTreeNode jointGroupNode ) {
        if ( jointGroupNode.getType() != Gx2dJTreeNode.Type.JOINT_ITEM_GROUP )
            return;
        jointGroupNode.removeAllChildren();
        loadJointHandlerNode(jointGroupNode);
        treeDataModel.nodeStructureChanged( jointGroupNode );
    }

    public void mirrorNode( PhysModelProcessing.MirrorDirection dir ) {
        TreePath [] selectedPaths = jTreeModel.getSelectionPaths();

        if ( selectedPaths == null )
            return;
        boolean res = false;
        for ( TreePath tp : selectedPaths ) {
            Gx2dJTreeNode node = (Gx2dJTreeNode) tp.getLastPathComponent();

            switch ( node.getType() ) {
                case MODEL:
                    mirrorModel( dir );
                    res = true;
                    break;
                case BiScSET:
                    break;
                case SPRITE:
                    AnimatedActorGroup aag = (AnimatedActorGroup) node.getUserObject();
                    if ( aag instanceof BodyItem )
                        continue;
                    if ( mirrorAag( node, dir ) )
                        res = true;
                    break;
                case AAG_SC:
                    break;
                case AAG_SC_SET:
                    break;
                case BODY_HANDLER:
                    if ( mirrorBodyItem( node, dir ) )
                        res = true;
                    break;
                case BODY_ITEM_GROUP:
                    break;
                case FIXTURE_SET:
                    if ( mirrorFixtureSet( node, dir ) )
                        res = true;
                    break;
                case FIXTURE_SET_GROUP:
                    break;
                case JOINT_HANDLER:
                    break;
                case JOINT_ITEM_GROUP:
                    break;
            }
        }

        if ( res )
            mainGui.makeHistorySnapshot();
    }


    protected boolean mirrorAag( Gx2dJTreeNode aagNode , PhysModelProcessing.MirrorDirection dir ) {
        if ( aagNode.getType() != Gx2dJTreeNode.Type.SPRITE)
            return false;
        AnimatedActorGroup aag = (AnimatedActorGroup) aagNode.getUserObject();
        AagDescription desc = aag.getAagDescription();
        PhysModelProcessing.mirrorAagDescription(desc, dir);
        aagNode.removeAllChildren();
        aag.loadAagFromDescription(desc, SkrGdxApplication.get().getAtlas());
        loadAagNodes( aagNode );
        treeDataModel.nodeStructureChanged( aagNode );
        return true;
    }

    protected boolean mirrorBodyItem( Gx2dJTreeNode node, PhysModelProcessing.MirrorDirection dir ) {
        if ( node.getType() != Gx2dJTreeNode.Type.BODY_HANDLER)
            return false;
        BodyItem bi = (BodyItem) node.getUserObject();
        BodyItemDescription desc = bi.getBodyItemDescription();
        PhysModelProcessing.mirrorBodyItemDescription(desc, dir);
        bi.destroyPhysics();
        bi.loadFromDescription( desc, null );
        reloadJointItemNodes();
        return true;
    }

    protected boolean mirrorFixtureSet( Gx2dJTreeNode node, PhysModelProcessing.MirrorDirection dir ) {
        if ( node.getType() != Gx2dJTreeNode.Type.FIXTURE_SET )
            return false;
        FixtureSet fs = (FixtureSet) node.getUserObject();
        FixtureSetDescription fsDesc = fs.getDescription();
        PhysModelProcessing.mirrorFixtureSetDescription( fsDesc, dir );

        BodyItem bi = fs.getBodyItem();
        bi.removeFixtureSet( fs );
        fs = bi.addNewFixtureSet( fsDesc );

        node.setUserObject(fs);
        return true;
    }

    void mirrorModel( PhysModelProcessing.MirrorDirection dir ) {
        PhysModelDescription desc = model.getDescription();
        PhysModelProcessing.mirrorModelDescription(desc, dir);
        model.destroyPhysics();
        model.clearModel();
        model.loadFromDescription( desc, null );
        rootNode.removeAllChildren();
        loadPhysSetJNodes();
    }





    PhysModelDescription createDescriptionForSelection() {
        PhysModelDescription desc = new PhysModelDescription();
        desc.setName( "export_" + model.getName());

        TreePath [] selectionPaths = jTreeModel.getSelectionPaths();
        if ( selectionPaths == null )
            return null;
        boolean addJoints = true;
        for ( TreePath tp : selectionPaths ) {
            Gx2dJTreeNode node = (Gx2dJTreeNode) tp.getLastPathComponent();

            switch ( node.getType() ) {
                case MODEL:
                    return model.getDescription();
                case BiScSET:
                    setBiScSetNodeToModelDescritpion( node, desc );
                    addJoints = false;
                    break;
                case SPRITE:
                    break;
                case AAG_SC:
                    break;
                case AAG_SC_SET:
                    break;
                case BODY_HANDLER:
                    setBodyItemNodeToModelDesc(node, desc);
                    break;
                case BODY_ITEM_GROUP:
                    setBodyItemGroupNodeToModelDesc(node, desc);
                    break;
                case FIXTURE_SET:
                    break;
                case FIXTURE_SET_GROUP:
                    break;
                case JOINT_HANDLER:
                    break;
                case JOINT_ITEM_GROUP:
                    break;
            }
        }
        if ( addJoints )
            setJointItemNodesToModelDesc( desc );
        return desc;
    }

    protected void setBiScSetNodeToModelDescritpion( Gx2dJTreeNode biScSetNode, PhysModelDescription desc ) {
        if ( biScSetNode.getType() != Gx2dJTreeNode.Type.BiScSET )
            return;
        ScContainer.Handler handler = (ScContainer.Handler) biScSetNode.getUserObject();
        BiScSet bset = (BiScSet) handler.get();
        BiScSetDescription bsetDesc = bset.getDescription();
        BiScContainerDescription contDesc = desc.getBiScContainerDescription();

        contDesc.getContentMap().put( handler.id.toString(), bsetDesc );
        String name = handler.container.findNameById( handler.id );
        if ( name != null && !name.isEmpty() )
            contDesc.getNamesMap().put( name, handler.id );
        if ( handler.container.getCurrentId().equals( handler.id ) )
            contDesc.setCurrentId( handler.id );

    }

    protected void setBodyItemGroupNodeToModelDesc(Gx2dJTreeNode bigNode, PhysModelDescription desc) {
        if ( bigNode.getType() != Gx2dJTreeNode.Type.BODY_ITEM_GROUP )
            return;
        Gx2dJTreeNode biScNode = (Gx2dJTreeNode) bigNode.getParent();
        setBiScSetNodeToModelDescritpion( biScNode, desc );
    }


    protected void setBodyItemNodeToModelDesc(Gx2dJTreeNode biNode, PhysModelDescription desc) {
        if ( biNode.getType() != Gx2dJTreeNode.Type.BODY_HANDLER)
            return;
        Gx2dJTreeNode biGroupNode = (Gx2dJTreeNode) biNode.getParent();
        Gx2dJTreeNode biScSetNode = (Gx2dJTreeNode) biGroupNode.getParent();

        ScContainer.Handler handler = (ScContainer.Handler) biScSetNode.getUserObject();
        BiScContainerDescription contDesc = desc.getBiScContainerDescription();
        String idStr = handler.id.toString();
        if ( !contDesc.getContentMap().containsKey( idStr ) ) {
            contDesc.getContentMap().put( idStr, new BiScSetDescription() );
            String name = handler.container.findNameById( handler.id );
            if ( name != null && !name.isEmpty() )
                contDesc.getNamesMap().put( name, handler.id );
            if ( handler.container.getCurrentId().equals( handler.id ) )
                contDesc.setCurrentId( handler.id );
        }

        BiScSetDescription biScSetDesc = contDesc.getContentMap().get( idStr );
        BodyItem bi = (BodyItem) biNode.getUserObject();
        BodyItemDescription biDesc = bi.getBodyItemDescription();
        biScSetDesc.getBodyItemDescriptions().add( biDesc );
    }

    protected void setJointItemNodeToModelDesc( Gx2dJTreeNode jiNode, PhysModelDescription desc ) {
        if ( jiNode.getType() != Gx2dJTreeNode.Type.JOINT_HANDLER)
            return;
        Gx2dJTreeNode jiGroupNode = (Gx2dJTreeNode) jiNode.getParent();
        Gx2dJTreeNode biScSetNode = (Gx2dJTreeNode) jiGroupNode.getParent();
        ScContainer.Handler handler = (ScContainer.Handler) biScSetNode.getUserObject();
        BiScContainerDescription contDesc = desc.getBiScContainerDescription();
        String idStr = handler.id.toString();
        if ( !contDesc.getContentMap().containsKey( idStr ) ) {
            contDesc.getContentMap().put( idStr, new BiScSetDescription() );
            String name = handler.container.findNameById( handler.id );
            if ( name != null && !name.isEmpty() )
                contDesc.getNamesMap().put( name, handler.id );
            if ( handler.container.getCurrentId().equals( handler.id ) )
                contDesc.setCurrentId( handler.id );
        }

        BiScSetDescription biScSetDesc = contDesc.getContentMap().get( idStr );
        JointItem ji = (JointItem) jiNode.getUserObject();
        JointItemDescription jiDesc = ji.getJointItemDescription();
        biScSetDesc.getJointItemDescriptions().add( jiDesc );
    }

    protected BodyItemDescription findBodyItemDescription( long id, PhysModelDescription desc ) {
        for ( String idStr : desc.getBiScContainerDescription().getContentMap().keySet() ) {
            BiScSetDescription biScSetDesc = desc.getBiScContainerDescription().getContentMap().get( idStr );
            for ( BodyItemDescription biDesc : biScSetDesc.getBodyItemDescriptions() ) {
                if ( biDesc.getId() == id )
                    return biDesc;
            }
        }
        return null;
    }

    protected void setJointItemNodesToModelDesc( PhysModelDescription desc ) {
        for ( int i = 0; i < rootNode.getChildCount(); i++ ) {
            Gx2dJTreeNode biScSetNode = (Gx2dJTreeNode) rootNode.getChildAt( i );
            Gx2dJTreeNode jiGroupNode = getJointGroupNode( biScSetNode );
            if ( jiGroupNode == null )
                continue;
            for ( int j = 0; j < jiGroupNode.getChildCount(); j++ ) {
                Gx2dJTreeNode jiNode = (Gx2dJTreeNode) jiGroupNode.getChildAt( j );
                JointItem ji = (JointItem) jiNode.getUserObject();
                long bId = ji.getBodyAId();
                if ( bId != -1 && ( findBodyItemDescription( bId, desc ) == null) )
                    continue;
                bId = ji.getBodyBId();
                if ( bId != -1 && ( findBodyItemDescription( bId, desc ) == null) )
                    continue;
                setJointItemNodeToModelDesc( jiNode, desc );
            }
        }
    }

    public void importModelDescription( PhysModelDescription desc ) {
        if ( model == null )
            return;
        model.mergeFromDescription( desc, null );
        rootNode.removeAllChildren();
        loadPhysSetJNodes();
        treeDataModel.nodeStructureChanged( rootNode );
        expandToNode(rootNode);
        mainGui.makeHistorySnapshot();
    }

    public void copyNodeProperties() {

        propCpyNodeType = null;
        propCpyDescRef = null;

        TreePath [] selectionPaths = jTreeModel.getSelectionPaths();
        if ( selectionPaths == null )
            return;
        if ( selectionPaths.length != 1)
            return;
        Gx2dJTreeNode node = (Gx2dJTreeNode) jTreeModel.getLastSelectedPathComponent();

        switch ( node.getType() ) {
            case SPRITE:
                AnimatedActorGroup aag = (AnimatedActorGroup) node.getUserObject();
                propCpyDescRef = aag.getAagDescription();
                break;
            case BODY_HANDLER:
                BodyItem bi = (BodyItem) node.getUserObject();
                propCpyDescRef = bi.getBodyItemDescription();
                break;
            case FIXTURE_SET:
                FixtureSet fs = (FixtureSet) node.getUserObject();
                propCpyDescRef = fs.getDescription();
                break;
            case JOINT_HANDLER:
                JointItem ji = (JointItem) node.getUserObject();
                propCpyDescRef = ji.getJointItemDescription();
                break;
            default:
                return;
        }
        propCpyNodeType = node.type;
    }

    public void pasteNodeProperties() {

        if ( propCpyNodeType == null || propCpyDescRef == null )
            return;

        TreePath [] selectionPaths = jTreeModel.getSelectionPaths();
        if ( selectionPaths == null ) {
            return;
        }

        for ( TreePath tp : selectionPaths ) {
            Gx2dJTreeNode node = (Gx2dJTreeNode) tp.getLastPathComponent();
            if ( node.type != propCpyNodeType )
                continue;
            switch ( node.type ) {
                case SPRITE:
                    pasteAagNodeProperties( node );
                    break;
                case BODY_HANDLER:
                    pasteBodyItemNodeProperties( node );
                    break;
                case FIXTURE_SET:
                    pasteFixtureSetNodeProperties( node );
                    break;
                case JOINT_HANDLER:
                    pasteJointItemNodeProperties( node );
                    break;
                default:
                    break;
            }
        }
        updatePropertiesTable();
        mainGui.makeHistorySnapshot();
    }


    protected void pasteAagNodeProperties( Gx2dJTreeNode node ) {
        final AagDescription desc = (AagDescription) propCpyDescRef;
        final AnimatedActorGroup aag = (AnimatedActorGroup) node.getUserObject();
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                aag.setTextureName(desc.getTextureName());
                aag.setFrameDuration(desc.getFrameDuration());
                aag.setPlayMode(desc.getPlayMode());
                aag.updateTextures(aag.getAtlas());
                aag.setWidth( desc.getWidth() );
                aag.setHeight( desc.getHeight() );
                aag.setKeepAspectRatio( desc.isKeepAspectRatio() );
                aag.setRotation( desc.getRotation() );
                aag.setDrawable( desc.isDrawable() );
            }
        });
    }

    protected void pasteBodyItemNodeProperties( Gx2dJTreeNode node ) {
        final BodyItemDescription desc = (BodyItemDescription) propCpyDescRef;
        final BodyItem bi = (BodyItem) node.getUserObject();

        final Body body = bi.getBody();
        if ( body == null ) {
            return;
        }
        Gdx.app.postRunnable( new Runnable() {
            @Override
            public void run() {
                BodyDef bd = desc.getBodyDef();

                body.setType( bd.type );
                Vector2 pos = body.getPosition();
                body.setTransform( pos.x, pos.y, bd.angle );
                body.setLinearVelocity( bd.linearVelocity );
                body.setAngularVelocity( bd.angularVelocity );
                body.setLinearDamping( bd.linearDamping );
                body.setAngularDamping( bd.angularDamping );
                body.setSleepingAllowed( bd.allowSleep );
                body.setAwake( bd.awake );
                body.setFixedRotation( bd.fixedRotation );
                body.setBullet( bd.bullet );
                body.setActive( bd.active );
                body.setGravityScale( bd.gravityScale );
                if ( desc.isOverrideMassData() ) {
                    body.setMassData( desc.getMassData() );
                }
            }
        });

    }

    protected void pasteFixtureSetNodeProperties( Gx2dJTreeNode node ) {
        final FixtureSetDescription desc = (FixtureSetDescription) propCpyDescRef;
        final FixtureSet fs = (FixtureSet) node.getUserObject();
        Gdx.app.postRunnable( new Runnable() {
            @Override
            public void run() {
                fs.setDensity( desc.getDensity() );
                fs.setRestitution( desc.getRestitution() );
                fs.setFriction( desc.getFriction() );
            }
        });
    }

    protected void pasteJointItemNodeProperties( Gx2dJTreeNode node ) {
        final JointItemDescription desc = (JointItemDescription) propCpyDescRef;
        final JointItem ji = (JointItem) node.getUserObject();
        if ( ji.getJoint() == null )
            return;

        Gdx.app.postRunnable( new Runnable() {
            @Override
            public void run() {
                Joint joint = ji.getJoint();

                switch ( joint.getType() ) {
                    case Unknown:
                        break;
                    case RevoluteJoint:
                        RevoluteJoint revoluteJoint = (RevoluteJoint) joint;
                        revoluteJoint.enableLimit( desc.isEnableLimit() );
                        revoluteJoint.setLimits( desc.getLowerAngle(), desc.getUpperAngle() );
                        revoluteJoint.enableMotor( desc.isEnableMotor() );
                        revoluteJoint.setMotorSpeed( desc.getMotorSpeed() );
                        revoluteJoint.setMaxMotorTorque( desc.getMaxMotorTorque() );
                        break;
                    case PrismaticJoint:
                        PrismaticJoint prismaticJoint = (PrismaticJoint) joint;
                        prismaticJoint.enableLimit( desc.isEnableLimit() );
                        prismaticJoint.setLimits( desc.getLowerTranslation(), desc.getUpperTranslation() );
                        prismaticJoint.enableMotor( desc.isEnableMotor() );
                        prismaticJoint.setMotorSpeed( desc.getMotorSpeed() );
                        prismaticJoint.setMaxMotorForce( desc.getMaxMotorForce() );
                        break;
                    case DistanceJoint:
                        DistanceJoint distanceJoint = (DistanceJoint) joint;
                        distanceJoint.setDampingRatio( desc.getDampingRatio() );
                        distanceJoint.setFrequency( desc.getFrequencyHz() );
                        distanceJoint.setLength( desc.getLength() );
                        break;
                    case PulleyJoint:
                        break;
                    case MouseJoint:
                        break;
                    case GearJoint:
                        GearJoint gearJoint = (GearJoint) joint;
                        gearJoint.setRatio( desc.getRatio() );
                        break;
                    case WheelJoint:
                        WheelJoint wheelJoint = (WheelJoint) joint;
                        wheelJoint.setSpringDampingRatio( desc.getDampingRatio() );
                        wheelJoint.setSpringFrequencyHz( desc.getFrequencyHz() );
                        wheelJoint.enableMotor( desc.isEnableMotor() );
                        wheelJoint.setMaxMotorTorque( desc.getMaxMotorTorque() );
                        wheelJoint.setMotorSpeed( wheelJoint.getMotorSpeed() );
                        break;
                    case WeldJoint:
                        WeldJoint weldJoint = (WeldJoint) joint;
                        weldJoint.setDampingRatio( desc.getDampingRatio() );
                        weldJoint.setFrequency( desc.getFrequencyHz() );
                        break;
                    case FrictionJoint:
                        FrictionJoint frictionJoint = (FrictionJoint) joint;
                        frictionJoint.setMaxForce( desc.getMaxForce() );
                        frictionJoint.setMaxTorque( desc.getMaxTorque() );
                        break;
                    case RopeJoint:
                        RopeJoint ropeJoint = (RopeJoint) joint;
                        ropeJoint.setMaxLength( desc.getMaxLength() );
                        break;
                    case MotorJoint:
                        MotorJoint motorJoint = (MotorJoint) joint;
                        motorJoint.setLinearOffset( desc.getLinearOffset() );
                        motorJoint.setAngularOffset( desc.getAngularOffset() );
                        motorJoint.setMaxForce( desc.getMaxForce() );
                        motorJoint.setMaxTorque( desc.getMaxTorque() );
                        motorJoint.setCorrectionFactor( desc.getCorrectionFactor() );
                        break;
                }
            }
        });
    }

    public void clearSelection() {
        jTreeModel.clearSelection();
        editorScreen.setModelObject(null, EditorScreen.ModelObjectType.OT_None );
    }


    protected void convertSelection( Gx2dJTreeNode.Type type ) {
        TreePath [] selectionsPaths = jTreeModel.getSelectionPaths();
        if ( selectionsPaths == null )
            return;
        if ( selectionsPaths.length == 0 )
            return;
        Gx2dJTreeNode node = (Gx2dJTreeNode) selectionsPaths[0].getLastPathComponent();
        if ( node.type == type )
            return;

        Array<TreePath> newSelections = new Array<TreePath>();

        for ( TreePath tp : selectionsPaths ) {
            node = (Gx2dJTreeNode) tp.getLastPathComponent();
            Gx2dJTreeNode biNode = findParentNode( node, type );
            if ( biNode != null ) {
                newSelections.add( new TreePath( biNode.getPath() ) );
            } else {
                Array<Gx2dJTreeNode> nodes = findAllChildNodes( node, type, 20000 );
                for ( Gx2dJTreeNode sNode : nodes ) {
                    newSelections.add( new TreePath( sNode.getPath() ) );
                }
            }
        }

        if ( newSelections.size == 0 )
            return;

        clearSelection();
        selectionsPaths = new TreePath[ newSelections.size ];
        for( int i = 0; i < selectionsPaths.length; i++) {
            selectionsPaths[i] = newSelections.get( i );
        }
        jTreeModel.setSelectionPaths( selectionsPaths );
    }


    public void convertSelectionToBodyItemSelection() {
        convertSelection(Gx2dJTreeNode.Type.BODY_HANDLER);
    }

    public void convertSelectionToFixtureSetSelection() {
        convertSelection(Gx2dJTreeNode.Type.FIXTURE_SET);
    }

    public void convertSelectionToAagSelection() {
        convertSelection(Gx2dJTreeNode.Type.SPRITE);
    }

    public void convertSelectionToJointItemSelection() {

        TreePath [] selectionPaths = jTreeModel.getSelectionPaths();
        if ( selectionPaths == null )
            return;
        if ( selectionPaths.length == 0 )
            return;
        Gx2dJTreeNode node = (Gx2dJTreeNode) selectionPaths[0].getLastPathComponent();
        if ( node.type == Gx2dJTreeNode.Type.JOINT_HANDLER)
            return;

        Array<JointItem> jiArray = new Array<JointItem>();
        Array< BodyItem > bodyItems = new Array<BodyItem>();


        // find all bodyItems
        for ( TreePath tp : selectionPaths ) {
            node = (Gx2dJTreeNode) tp.getLastPathComponent();

            if ( node.type == Gx2dJTreeNode.Type.BODY_HANDLER) {
                bodyItems.add( (BodyItem) node.getUserObject() );
                continue;
            }

            Gx2dJTreeNode biNode = findParentNode( node, Gx2dJTreeNode.Type.BODY_HANDLER);
            if ( biNode != null ) {
                bodyItems.add( (BodyItem) biNode.getUserObject() );
            } else {
                Array<Gx2dJTreeNode> biNodes = findAllChildNodes( node, Gx2dJTreeNode.Type.BODY_HANDLER, 10 );
                for ( Gx2dJTreeNode fBiNode : biNodes ) {
                    BodyItem bi = (BodyItem) fBiNode.getUserObject();
                    if ( bodyItems.contains( bi, true ) )
                        continue;
                    bodyItems.add(bi);
                }
            }
        }

        // find all JointItems
        for ( BodyItem bi : bodyItems ) {
            BiScSet bset = bi.getBiScSet();
            Array< JointItem > fJiArray = bset.findJointItems( bi );
            for ( JointItem ji : fJiArray ) {
                if ( jiArray.contains( ji, true ) ) {
                    continue;
                }
                jiArray.add( ji );
            }
        }

        clearSelection();

        for ( JointItem ji : jiArray ) {
            Gx2dJTreeNode jiNode = findNode( ji, Gx2dJTreeNode.Type.JOINT_HANDLER);
            if ( jiNode == null ) {
                continue;
            }
            jTreeModel.addSelectionPath(new TreePath(jiNode.getPath()));
        }


        expandToNode((Gx2dJTreeNode) jTreeModel.getLastSelectedPathComponent());
    }

}
