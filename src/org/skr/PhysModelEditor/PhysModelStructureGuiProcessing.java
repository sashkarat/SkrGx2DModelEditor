package org.skr.PhysModelEditor;

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
import org.skr.PhysModelEditor.PropertiesTableElements.*;
import org.skr.PhysModelEditor.gdx.editor.SkrGdxAppPhysModelEditor;
import org.skr.PhysModelEditor.gdx.editor.screens.EditorScreen;
import org.skr.gdx.PhysWorld;
import org.skr.gdx.SelectableContent.ScContainer;
import org.skr.gdx.SkrGdxApplication;
import org.skr.gdx.physmodel.PhysModel;
import org.skr.gdx.physmodel.PhysModelDescription;
import org.skr.gdx.physmodel.ShapeDescription;
import org.skr.gdx.physmodel.animatedactorgroup.AagDescription;
import org.skr.gdx.physmodel.animatedactorgroup.AagScContainer;
import org.skr.gdx.physmodel.animatedactorgroup.AnimatedActorGroup;
import org.skr.gdx.physmodel.bodyitem.*;
import org.skr.gdx.physmodel.bodyitem.fixtureset.FixtureSet;
import org.skr.gdx.physmodel.bodyitem.fixtureset.FixtureSetDescription;
import org.skr.gdx.physmodel.jointitem.JointItem;
import org.skr.gdx.physmodel.jointitem.JointItemDescription;
import org.skr.gdx.physmodel.jointitem.JointItemFactory;
import org.skr.gdx.utils.PhysModelProcessing;

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
public class PhysModelStructureGuiProcessing {

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


        public PhysModelJTreeNode getNodeUnderCursor() {

            Point p = tree.getMousePosition();
            if ( p == null )
                return null;
            TreePath tp = tree.getPathForLocation( p.x, p.y );
            if ( tp == null )
                return null;
            return (PhysModelJTreeNode) tp.getLastPathComponent();
        }

        public PhysModelJTreeNode getNodeForEvent( DropTargetDragEvent dtde ) {
            return getNodeUnderCursor();
        }

        public PhysModelJTreeNode getNodeForEvent( DropTargetDropEvent dtde ) {
            return getNodeUnderCursor();
        }

        public PhysModelJTreeNode getNodeForEvent( DragSourceDragEvent dsde ) {
            return  getNodeUnderCursor();
        }

        public PhysModelJTreeNode getNodeForEvent( DragSourceDropEvent dsde ) {
            return getNodeUnderCursor();
        }

        public boolean isDraggable(PhysModelJTreeNode node) {
            PhysModelJTreeNode parentNode = (PhysModelJTreeNode) node.getParent();

            switch ( node.getType() ) {

                case MODEL:
                    break;
                case BiScSET:
                    return true;
                case AAG:
                    if ( parentNode.getType() == PhysModelJTreeNode.Type.BODY_ITEM )
                        break;
                    return true;
                case AAG_SC:
                    return true;
                case AAG_SC_SET:
                    return true;
                case BODY_ITEM:
                    return true;
                case BODY_ITEM_GROUP:
                    return true;
                case FIXTURE_SET:
                    return true;
                case FIXTURE_SET_GROUP:
                    return true;
                case JOINT_ITEM:
                    break;
                case JOINT_ITEM_GROUP:
                    break;
            }

            return false;
        }

        public boolean isCompliance( PhysModelJTreeNode sourceNode, PhysModelJTreeNode targetNode ) {

            PhysModelJTreeNode.Type sType = sourceNode.getType();
            PhysModelJTreeNode.Type tType = targetNode.getType();

            switch ( sType ) {

                case MODEL:
                    break;
                case BiScSET:
                    if ( tType == PhysModelJTreeNode.Type.MODEL || tType == PhysModelJTreeNode.Type.BiScSET )
                        return true;
                    break;
                case AAG:
                    if ( tType == PhysModelJTreeNode.Type.AAG || tType == PhysModelJTreeNode.Type.AAG_SC
                            || tType == PhysModelJTreeNode.Type.AAG_SC_SET )
                        return true;
                    break;
                case AAG_SC:
                    if ( tType == PhysModelJTreeNode.Type.AAG || tType == PhysModelJTreeNode.Type.AAG_SC
                            || tType == PhysModelJTreeNode.Type.AAG_SC_SET )
                        return true;
                    break;
                case AAG_SC_SET:
                    if ( tType == PhysModelJTreeNode.Type.AAG_SC || tType == PhysModelJTreeNode.Type.AAG )
                        return true;
                    break;
                case BODY_ITEM:
                    if ( tType == PhysModelJTreeNode.Type.BODY_ITEM_GROUP || tType == PhysModelJTreeNode.Type.BiScSET )
                        return true;
                    break;
                case BODY_ITEM_GROUP:
                    if ( tType == PhysModelJTreeNode.Type.BiScSET || tType == PhysModelJTreeNode.Type.BODY_ITEM_GROUP )
                        return true;
                    break;
                case FIXTURE_SET:
                    if ( tType == PhysModelJTreeNode.Type.BODY_ITEM || tType == PhysModelJTreeNode.Type.FIXTURE_SET_GROUP )
                        return true;
                    break;
                case FIXTURE_SET_GROUP:
                    if ( tType == PhysModelJTreeNode.Type.BODY_ITEM || tType == PhysModelJTreeNode.Type.FIXTURE_SET_GROUP )
                        return true;
                    break;
                case JOINT_ITEM:
                    break;
                case JOINT_ITEM_GROUP:
                    break;
            }
            return false;
        }

    }


    static class TreeDragSource implements DragSourceListener, DragGestureListener {

        PhysModelStructureGuiProcessing guiProc;
        JTree sourceTree;
        DragSource source;
        DragGestureRecognizer recognizer;
        TransferableNode transferableNode;
        PhysModelJTreeNode sourceNode;
        TreeNodesComplianceControl nodesComplianceControl;


        TreeDragSource(PhysModelStructureGuiProcessing guiProc, int actions ) {
            this.guiProc = guiProc;
            this.sourceTree = guiProc.getjTreeModel();
            this.nodesComplianceControl = guiProc.getNodesComplianceControl();
            source = new DragSource();
            recognizer = source.createDefaultDragGestureRecognizer( sourceTree, actions, this );
        }

        public PhysModelJTreeNode getSourceNode() {
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
            sourceNode = (PhysModelJTreeNode) selPath.getLastPathComponent();
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

            PhysModelJTreeNode targetNode = nodesComplianceControl.getNodeUnderCursor();
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
        PhysModelStructureGuiProcessing guiProc;

        TreeDropTarget( PhysModelStructureGuiProcessing guiProc ) {
            this.guiProc = guiProc;
            this.targetTree = guiProc.getjTreeModel();
            target = new DropTarget( targetTree, this );
            this.nodesComplianceControl = guiProc.getNodesComplianceControl();
        }


        protected PhysModelJTreeNode getComplianceSrcNode( Transferable tr, PhysModelJTreeNode targetNode ) {
            DataFlavor[] flavors = tr.getTransferDataFlavors();
            try {
                for (int i = 0; i < flavors.length; i++) {
                    if (!tr.isDataFlavorSupported(flavors[i]))
                        continue;
                    PhysModelJTreeNode srcNode = guiProc.getDragSource().getSourceNode();
                    if ( !nodesComplianceControl.isCompliance( srcNode, targetNode ) ) {
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
            PhysModelJTreeNode node = nodesComplianceControl.getNodeForEvent(dtde);
            if ( node == null ) {
                dtde.rejectDrop();
                return;
            }
            PhysModelJTreeNode srcNode = getComplianceSrcNode( dtde.getTransferable(), node );
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

    PhysModel model;
    PhysModelJTreeNode rootNode;
    EditorScreen editorScreen;

    private DefaultTreeModel treeDataModel;
    private PropertiesCellEditor propertiesCellEditor;

    private DefaultTableModel emptyTableModel = new DefaultTableModel();
    private PhysModelPropertiesTableModel physModelPropertiesTableModel;
    private AagPropertiesTableModel aagPropertiesTableModel;
    private scItemTableModel scItemTableModel;
    private BodyPropertiesTableModel bodyPropertiesTableModel;
    private FixtureSetPropertiesTableModel fixtureSetPropertiesTableModel;
    private JointPropertiesTableModel jointPropertiesTableModel;

    private TreeDragSource dragSource;
    private TreeDropTarget dropTarget;
    TreeNodesComplianceControl nodesComplianceControl;

    private PhysModelJTreeNode.Type propCpyNodeType = null;
    private Object propCpyDescRef = null;

    public PhysModelStructureGuiProcessing( final MainGui mainGui ) {
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


        physModelPropertiesTableModel = new PhysModelPropertiesTableModel( this.jTreeModel);
        aagPropertiesTableModel = new AagPropertiesTableModel( this.jTreeModel);
        scItemTableModel = new scItemTableModel( this.jTreeModel);
        bodyPropertiesTableModel = new BodyPropertiesTableModel( this.jTreeModel);
        fixtureSetPropertiesTableModel = new FixtureSetPropertiesTableModel( this.jTreeModel);
        jointPropertiesTableModel = new JointPropertiesTableModel( this.jTreeModel);

        Gdx.app.postRunnable( new Runnable() {
            @Override
            public void run() {
                editorScreen = SkrGdxAppPhysModelEditor.get().getEditorScreen();

                editorScreen.setItemSelectionListener(new EditorScreen.ItemSelectionListener() {
                    @Override
                    public void singleItemSelected(Object object, EditorScreen.ModelObjectType mot) {
                        selectObject( object, mot );
                    }

                    @Override
                    public void itemAddedToSelection(Object object, EditorScreen.ModelObjectType mot, boolean removed) {
                        changeObjectSelection( object, mot, !removed );
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

    public void setModel( PhysModel model ) {
        jTreeModel.removeAll();
        this.model = model;
        if ( model != null )
            loadTree();
    }

    protected void loadTree() {
        rootNode = new PhysModelJTreeNode(PhysModelJTreeNode.Type.MODEL, model );
        loadTreeBiScNodes();
        this.treeDataModel = new DefaultTreeModel( rootNode );
        this.jTreeModel.setModel( treeDataModel );
    }

    protected void loadTreeBiScNodes() {
        for ( Integer id : model.getScBodyItems().getIdsSet() ) {
            ScContainer.Handler handler = new ScContainer.Handler(model.getScBodyItems(), id );
            PhysModelJTreeNode node = new PhysModelJTreeNode(PhysModelJTreeNode.Type.BiScSET, handler );
            loadBiScSetNodes(node);
            rootNode.add( node );
        }
    }

    protected void loadBiScSetNodes(PhysModelJTreeNode parentNode) {
        ScContainer.Handler handler = (ScContainer.Handler) parentNode.getUserObject();
        BiScSet bset = (BiScSet) handler.container.getContent( handler.id );
        PhysModelJTreeNode bNode = new PhysModelJTreeNode(PhysModelJTreeNode.Type.BODY_ITEM_GROUP,
                bset );
        loadBodyItemsNodes( bNode );
        PhysModelJTreeNode jNode = new PhysModelJTreeNode(PhysModelJTreeNode.Type.JOINT_ITEM_GROUP,
                bset );
        loadJointItemsNodes( jNode );
        parentNode.add( bNode );
        parentNode.add( jNode );
    }

    protected void loadBodyItemsNodes( PhysModelJTreeNode parentNode ) {
        BiScSet bset = (BiScSet) parentNode.getUserObject();
        for ( BodyItem bi : bset.getBodyItems() ) {
            PhysModelJTreeNode node = new PhysModelJTreeNode(PhysModelJTreeNode.Type.BODY_ITEM, bi );
            loadBodyItemNodes( node );
            parentNode.add( node );
        }
    }


    protected void loadBodyItemNodes( PhysModelJTreeNode parentNode ) {
        BodyItem bi = (BodyItem) parentNode.getUserObject();
        PhysModelJTreeNode fsgNode = new PhysModelJTreeNode(PhysModelJTreeNode.Type.FIXTURE_SET_GROUP,
                bi );
        loadFixtureSetsNodes( fsgNode );
        parentNode.add( fsgNode );

        PhysModelJTreeNode aagNode = new PhysModelJTreeNode(PhysModelJTreeNode.Type.AAG, bi );
        loadAagNodes(aagNode);
        parentNode.add(aagNode);
    }

    protected void loadFixtureSetsNodes( PhysModelJTreeNode parentNode ) {
        BodyItem bi = (BodyItem) parentNode.getUserObject();
        for ( FixtureSet fs : bi.getFixtureSets() ) {
            parentNode.add( new PhysModelJTreeNode(PhysModelJTreeNode.Type.FIXTURE_SET, fs) );
        }
    }


    protected void loadAagNodes( PhysModelJTreeNode parentNode ) {
        AnimatedActorGroup aag = (AnimatedActorGroup) parentNode.getUserObject();
        for ( Actor a : aag.getChildren() ) {
            if ( !( a instanceof AnimatedActorGroup ) )
                continue;
            if ( aag.getScChildren().contains( a ) )
                continue;
            PhysModelJTreeNode node = new PhysModelJTreeNode(PhysModelJTreeNode.Type.AAG, a );
            loadAagNodes( node );
            parentNode.add( node );
        }
        PhysModelJTreeNode scNode = new PhysModelJTreeNode(PhysModelJTreeNode.Type.AAG_SC, aag );
        loadAagScNodes( scNode );
        parentNode.add( scNode );
    }

    protected void loadAagScNodes( PhysModelJTreeNode parentNode ) {
        AnimatedActorGroup aag = (AnimatedActorGroup) parentNode.getUserObject();
        AagScContainer aagScContainer = aag.getScChildren();
        for ( Integer id : aagScContainer.getIdsSet() ) {
            ScContainer.Handler handler = new ScContainer.Handler( aagScContainer, id );
            PhysModelJTreeNode scSetNode = new PhysModelJTreeNode( PhysModelJTreeNode.Type.AAG_SC_SET, handler );
            loadAagScSetNodes(scSetNode);
            parentNode.add( scSetNode );
        }
    }

    protected void loadAagScSetNodes(PhysModelJTreeNode parentNode) {
        ScContainer.Handler handler = (ScContainer.Handler) parentNode.getUserObject();
        AnimatedActorGroup aag = (AnimatedActorGroup) handler.container.getContent( handler.id );
        PhysModelJTreeNode aagNode = new PhysModelJTreeNode( PhysModelJTreeNode.Type.AAG, aag );
        loadAagNodes( aagNode );
        parentNode.add( aagNode );
    }

    protected void loadJointItemsNodes( PhysModelJTreeNode parentNode ) {
        BiScSet bset = (BiScSet) parentNode.getUserObject();
        for ( JointItem jointItem : bset.getJointItems() ) {
            parentNode.add( new PhysModelJTreeNode(PhysModelJTreeNode.Type.JOINT_ITEM, jointItem ) );
        }
    }

    protected PhysModelJTreeNode getChildNode( PhysModelJTreeNode parentNode, PhysModelJTreeNode.Type childType ) {
        for ( int i = 0; i < parentNode.getChildCount(); i++ ) {
            PhysModelJTreeNode node = (PhysModelJTreeNode) parentNode.getChildAt( i );
            if ( node.getType() == childType )
                return node;
        }
        return null;
    }

    protected PhysModelJTreeNode getFixtureSetGroupNode( PhysModelJTreeNode parentBodyItemNode ) {
        if ( parentBodyItemNode.getType() != PhysModelJTreeNode.Type.BODY_ITEM )
            return null;

        return getChildNode( parentBodyItemNode, PhysModelJTreeNode.Type.FIXTURE_SET_GROUP );
    }


    protected PhysModelJTreeNode getJointGroupNode( PhysModelJTreeNode parentBiScSetNode ) {
        if ( parentBiScSetNode.getType() != PhysModelJTreeNode.Type.BiScSET )
            return null;
        return getChildNode( parentBiScSetNode, PhysModelJTreeNode.Type.JOINT_ITEM_GROUP );
    }


    protected PhysModelJTreeNode getAagScNode( PhysModelJTreeNode parentAagNode ) {
        if ( parentAagNode.getType() != PhysModelJTreeNode.Type.AAG )
            return null;
        return getChildNode( parentAagNode, PhysModelJTreeNode.Type.AAG_SC );
    }

    protected PhysModelJTreeNode getAagNode( PhysModelJTreeNode parentAagScSetNode ) {
        if ( parentAagScSetNode.getType() != PhysModelJTreeNode.Type.AAG_SC_SET )
            return null;
        return getChildNode( parentAagScSetNode, PhysModelJTreeNode.Type.AAG );
    }

    protected void checkTreeSelection() {
        TreePath [] selectionPaths = jTreeModel.getSelectionPaths();
        if ( selectionPaths == null )
            return;
        if ( selectionPaths.length < 2 )
            return;
        PhysModelJTreeNode firstNode = (PhysModelJTreeNode) selectionPaths[0].getLastPathComponent();

        for ( int i = 1; i < selectionPaths.length; i++ ) {
            PhysModelJTreeNode node = (PhysModelJTreeNode) selectionPaths[i].getLastPathComponent();
            if ( node.getType() != firstNode.getType() )
                jTreeModel.removeSelectionPath( selectionPaths[i] );
        }
    }


    protected void selectNode( PhysModelJTreeNode node ) {
        if ( node == null )
            return;
        if ( node.getPath() == null )
            return;
        TreePath path = new TreePath( node.getPath() );
        jTreeModel.setSelectionPath( path );
        jTreeModel.scrollPathToVisible( path );
        processTreeSingleSelection();
    }

    protected void expandToNode( PhysModelJTreeNode node ) {
        if ( node.getChildCount() > 0 ) {
            node = (PhysModelJTreeNode) node.getChildAt(0);
        }

        jTreeModel.scrollPathToVisible( new TreePath( node.getPath() ) );
    }

    protected void expandToObject( Object object ) {
        PhysModelJTreeNode node = findNode( object );
        if ( node == null )
            return;
        expandToNode( node );
    }

    protected void changeNodesSelection( PhysModelJTreeNode node, boolean add ) {
        TreePath path = new TreePath( node.getPath() );
        if ( add ) {
            jTreeModel.addSelectionPath( path );
            jTreeModel.scrollPathToVisible( path );
        } else {
            jTreeModel.removeSelectionPath( path );
        }
        processTreeSelection();
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

    protected void processTreeSingleSelection() {

        PhysModelJTreeNode selNode = (PhysModelJTreeNode) jTreeModel.getLastSelectedPathComponent();

        if ( selNode == null )
            return;
        if ( selNode.getUserObject() == null )
            return;

        EditorScreen.ModelObjectType objectType = EditorScreen.ModelObjectType.OT_None;

        jTableProperties.setModel( emptyTableModel );

        switch ( selNode.getType() ) {
            case MODEL:
                physModelPropertiesTableModel.setModel( model );
                jTableProperties.setModel(physModelPropertiesTableModel);
                objectType = EditorScreen.ModelObjectType.OT_Model;
                break;
            case BiScSET:
                scItemTableModel.setHandler((ScContainer.Handler) selNode.getUserObject() );
                jTableProperties.setModel(scItemTableModel);
                break;
            case AAG:
                aagPropertiesTableModel.setAag((AnimatedActorGroup) selNode.getUserObject());
                jTableProperties.setModel(aagPropertiesTableModel);
                objectType = EditorScreen.ModelObjectType.OT_Aag;
                break;
            case AAG_SC:
                break;
            case AAG_SC_SET:
                scItemTableModel.setHandler((ScContainer.Handler) selNode.getUserObject() );
                jTableProperties.setModel(scItemTableModel);
                break;
            case BODY_ITEM:
                BodyItem bi = ( BodyItem ) selNode.getUserObject();
                bodyPropertiesTableModel.setBodyItem( bi );
                jTableProperties.setModel(bodyPropertiesTableModel);
                objectType = EditorScreen.ModelObjectType.OT_BodyItem;
                break;
            case BODY_ITEM_GROUP:
                break;
            case FIXTURE_SET:
                FixtureSet fs = ( FixtureSet ) selNode.getUserObject();
                fixtureSetPropertiesTableModel.setFixtureSet( fs );
                jTableProperties.setModel( fixtureSetPropertiesTableModel );
                objectType = EditorScreen.ModelObjectType.OT_FixtureSet;
                break;
            case FIXTURE_SET_GROUP:
                break;
            case JOINT_ITEM:
                JointItem ji = (JointItem) selNode.getUserObject();
                jointPropertiesTableModel.setJointItem( ji );
                jTableProperties.setModel( jointPropertiesTableModel );
                objectType = EditorScreen.ModelObjectType.OT_JointItem;
                break;
            case JOINT_ITEM_GROUP:
                objectType = EditorScreen.ModelObjectType.OT_JointItems;
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
            PhysModelJTreeNode node = (PhysModelJTreeNode) tp.getLastPathComponent();
            editorScreen.addModelObject( node.getUserObject() );
        }
    }

    protected static DialogNewJointSelector dlgNewJointSelector = new DialogNewJointSelector();

    public void createNode() {
        TreePath [] selPaths = jTreeModel.getSelectionPaths();
        if ( selPaths == null )
            return;
        if ( selPaths.length > 1 )
            return;
        PhysModelJTreeNode parentNode = (PhysModelJTreeNode) jTreeModel.getLastSelectedPathComponent();
        PhysModelJTreeNode newNode = null;

        switch ( parentNode.getType() ) {

            case MODEL:
                newNode = createNewBiScSet();
                break;
            case BiScSET:
                break;
            case AAG:
                newNode = createAagNode( (AnimatedActorGroup) parentNode.getUserObject() );
                break;
            case AAG_SC:
                newNode = createAagScItem( ( (AnimatedActorGroup) parentNode.getUserObject() ).getScChildren() );
                break;
            case AAG_SC_SET:
                break;
            case BODY_ITEM:
                break;
            case BODY_ITEM_GROUP:
                newNode = createNewBodyItem( (BiScSet) parentNode.getUserObject() );
                break;
            case FIXTURE_SET:
                break;
            case FIXTURE_SET_GROUP:
                newNode = createFixtureSet((BodyItem) parentNode.getUserObject() );
                break;
            case JOINT_ITEM:
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

    public PhysModelJTreeNode createNewBiScSet() {
        BiScSet newSet = new BiScSet( model );
        Integer id = model.getScBodyItems().generateId();
        model.getScBodyItems().addContent(id, newSet );

        ScContainer.Handler handler = new ScContainer.Handler(model.getScBodyItems(), id );
        PhysModelJTreeNode newNode = new PhysModelJTreeNode(PhysModelJTreeNode.Type.BiScSET, handler );
        loadBiScSetNodes(newNode);
        return newNode;
    }

    public PhysModelJTreeNode createNewBodyItem( BiScSet bset ) {
        BodyItem bi = new BodyItem( bset );
        bi.setName( "NewBody");
        bset.addBodyItem( bi );
        PhysModelJTreeNode node = new PhysModelJTreeNode(PhysModelJTreeNode.Type.BODY_ITEM, bi );
        loadBodyItemNodes( node );
        return node;
    }

    public PhysModelJTreeNode createAagNode( AnimatedActorGroup parentAag ) {
        AnimatedActorGroup aag = new AnimatedActorGroup( SkrGdxApplication.get().getAtlas() );
        aag.setName("NewAag");
        parentAag.addChildAag(aag);
        PhysModelJTreeNode node = new PhysModelJTreeNode(PhysModelJTreeNode.Type.AAG, aag);
        loadAagNodes( node );
        return node;
    }

    public PhysModelJTreeNode createAagScItem( AagScContainer container ) {
        AnimatedActorGroup aag = new AnimatedActorGroup( SkrGdxApplication.get().getAtlas() );
        aag.setName("NewAag");
        Integer id = container.generateId();
        container.addContent(id, aag);
        ScContainer.Handler handler = new ScContainer.Handler( container, id );
        PhysModelJTreeNode node = new PhysModelJTreeNode(PhysModelJTreeNode.Type.AAG_SC_SET, handler );
        loadAagScSetNodes(node);
        return node;
    }

    public PhysModelJTreeNode createFixtureSet( BodyItem bi ) {
        FixtureSet fs = new FixtureSet( bi );
        fs.setName("newFixtureSet");
        bi.addFixtureSet( fs );
        return new PhysModelJTreeNode( PhysModelJTreeNode.Type.FIXTURE_SET, fs );
    }

    public void updateFixtures( Array<ShapeDescription> shpDescriptions ) {
        FixtureSet fs = fixtureSetPropertiesTableModel.getFixtureSet();
        fs.createFixtures( shpDescriptions );
        fixtureSetPropertiesTableModel.fireTableDataChanged();
    }

    public PhysModelJTreeNode createEmptyJointItemNode( BiScSet bset, JointDef.JointType type ) {
        JointItem ji = JointItemFactory.create( type, "new_"+type, bset );
        if ( ji == null )
            return null;
        bset.addJointItem( ji );
        return new PhysModelJTreeNode(PhysModelJTreeNode.Type.JOINT_ITEM, ji );
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

    public PhysModelJTreeNode findNode( Object object ) {
        return findNode( rootNode, object );
    }

    protected PhysModelJTreeNode findNode( PhysModelJTreeNode parentNode, Object object ) {
        if ( parentNode.getUserObject() == object )
            return parentNode;
        for ( int index = 0; index < parentNode.getChildCount(); index ++ ) {
            PhysModelJTreeNode tn = (PhysModelJTreeNode) parentNode.getChildAt( index );
            tn = findNode( tn, object );
            if ( tn != null )
                return tn;
        }
        return null;
    }


    protected PhysModelJTreeNode findNode( Object object, PhysModelJTreeNode.Type type ) {
        return findNode( rootNode, object, type );
    }

    protected PhysModelJTreeNode findNode(PhysModelJTreeNode parentNode, Object object, PhysModelJTreeNode.Type type ) {
        if ( parentNode.getUserObject() == object && parentNode.getType() == type ) {
            return parentNode;
        }

        for ( int index = 0; index < parentNode.getChildCount(); index ++ ) {
            PhysModelJTreeNode tn = (PhysModelJTreeNode) parentNode.getChildAt( index );
            tn = findNode( tn, object, type);
            if ( tn != null )
                return tn;
        }
        return null;
    }

    protected PhysModelJTreeNode findNode( Object object, EditorScreen.ModelObjectType mot ) {
        PhysModelJTreeNode foundNode = null;
        switch ( mot ) {
            case OT_None:
                return null;
            case OT_Model:
                foundNode = findNode( object, PhysModelJTreeNode.Type.MODEL );
                break;
            case OT_BodyItem:
                foundNode = findNode( object, PhysModelJTreeNode.Type.BODY_ITEM );
                break;
            case OT_Aag:
                foundNode = findNode( object, PhysModelJTreeNode.Type.AAG );
                break;
            case OT_FixtureSet:
                foundNode = findNode( object, PhysModelJTreeNode.Type.FIXTURE_SET );
                break;
            case OT_JointItems:
                foundNode = findNode( object, PhysModelJTreeNode.Type.JOINT_ITEM );
                break;
            case OT_JointItem:
                foundNode = findNode( object, PhysModelJTreeNode.Type.JOINT_ITEM );
                break;
        }
        return foundNode;
    }

    protected void selectObject( Object object, EditorScreen.ModelObjectType mot ) {
        PhysModelJTreeNode node = findNode( object, mot );
        if ( node == null )
            return;
        selectNode( node );
    }

    protected void changeObjectSelection( Object object, EditorScreen.ModelObjectType mot, boolean add ) {
        PhysModelJTreeNode node = findNode( object, mot );
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

        PhysModelJTreeNode parentNode = null;

        for ( TreePath tp : selectedPaths ) {
            PhysModelJTreeNode node = (PhysModelJTreeNode) tp.getLastPathComponent();
            parentNode = removeNode( node );
            if ( parentNode != null )
                treeDataModel.nodeStructureChanged( parentNode );
        }

        if ( parentNode != null ) {
            selectNode(parentNode);
        }

        mainGui.makeHistorySnapshot();
    }

    protected PhysModelJTreeNode removeNode( PhysModelJTreeNode node ) {

        switch ( node.getType() ) {
            case MODEL:
                break;
            case BiScSET:
                return removeBiScSetNode( node );
            case AAG:
                return removeAagNode( node );
            case AAG_SC:
                break;
            case AAG_SC_SET:
                return removeAagScSetNode( node );
            case BODY_ITEM:
                return removeBodyItemNode( node );
            case BODY_ITEM_GROUP:
                break;
            case FIXTURE_SET:
                return removeFixtureSetNode( node );
            case FIXTURE_SET_GROUP:
                break;
            case JOINT_ITEM:
                return removeJointItemNode( node );
            case JOINT_ITEM_GROUP:
                break;
        }
        return null;
    }

    protected PhysModelJTreeNode removeBiScSetNode( PhysModelJTreeNode node ) {
        PhysModelJTreeNode parentNode = (PhysModelJTreeNode) node.getParent();
        ScContainer.Handler handler = (ScContainer.Handler) node.getUserObject();
        handler.container.removeContent( handler.id );
        node.removeFromParent();
        return parentNode;
    }

    protected PhysModelJTreeNode removeBodyItemNode( PhysModelJTreeNode node ) {
        PhysModelJTreeNode parentNode = (PhysModelJTreeNode) node.getParent();
        BiScSet bset = (BiScSet) parentNode.getUserObject();
        BodyItem bi = (BodyItem) node.getUserObject();

        PhysModelJTreeNode jgNode = getJointGroupNode((PhysModelJTreeNode) parentNode.getParent());
        Array< PhysModelJTreeNode > jiNodesToRemove = new Array<PhysModelJTreeNode>();

        for ( int i = 0; i < jgNode.getChildCount(); i++) {
            PhysModelJTreeNode jiNode = (PhysModelJTreeNode) jgNode.getChildAt( i );
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
        for ( PhysModelJTreeNode jiNode : jiNodesToRemove ) {
            removeJointItemNode( jiNode );
        }
        treeDataModel.nodeStructureChanged( jgNode );
        bset.removeBodyItem(bi);
        node.removeFromParent();
        cleanupJointItemNodes((PhysModelJTreeNode) parentNode.getParent());
        return parentNode;
    }

    protected void cleanupJointItemNodes(PhysModelJTreeNode biScSetNode) {
        PhysModelJTreeNode jiGroupNode = null;
        for ( int i = 0; i < biScSetNode.getChildCount(); i++) {
            PhysModelJTreeNode node = (PhysModelJTreeNode) biScSetNode.getChildAt( i );
            if ( node.getType() == PhysModelJTreeNode.Type.JOINT_ITEM_GROUP ) {
                jiGroupNode = node;
                break;
            }
        }
        if ( jiGroupNode == null )
            return ;

        BiScSet bset = (BiScSet) jiGroupNode.getUserObject();
        Array< PhysModelJTreeNode > nodesToRemove  = new Array<PhysModelJTreeNode>();

        for ( int i = 0; i < jiGroupNode.getChildCount(); i++ ) {
            PhysModelJTreeNode node = (PhysModelJTreeNode) jiGroupNode.getChildAt( i );
            JointItem ji = (JointItem) node.getUserObject();
            if ( !bset.getJointItems().contains( ji, true) ) {
                nodesToRemove.add( node );
            }
        }
        for ( PhysModelJTreeNode node : nodesToRemove )
            node.removeFromParent();
    }

    protected PhysModelJTreeNode removeJointItemNode( PhysModelJTreeNode jiNode ) {
        PhysModelJTreeNode parentNode = (PhysModelJTreeNode) jiNode.getParent();
        JointItem ji = (JointItem) jiNode.getUserObject();
        BiScSet bset = (BiScSet) parentNode.getUserObject();
        bset.removeJointItem( ji );
        jiNode.removeFromParent();
        return parentNode;
    }

    protected PhysModelJTreeNode removeFixtureSetNode( PhysModelJTreeNode fsNode ) {
        PhysModelJTreeNode parentNode = (PhysModelJTreeNode) fsNode.getParent();
        BodyItem bi = (BodyItem) parentNode.getUserObject();
        FixtureSet fs = (FixtureSet) fsNode.getUserObject();
        bi.removeFixtureSet( fs );
        fsNode.removeFromParent();
        return parentNode;
    }

    protected PhysModelJTreeNode removeAagNode( PhysModelJTreeNode aagNode ) {
        PhysModelJTreeNode parentNode = (PhysModelJTreeNode) aagNode.getParent();
        if ( parentNode.getType() == PhysModelJTreeNode.Type.BODY_ITEM )
            return null;
        if ( parentNode.getType() == PhysModelJTreeNode.Type.AAG_SC_SET )
            return null;
        AnimatedActorGroup parentAag = (AnimatedActorGroup) parentNode.getUserObject();
        AnimatedActorGroup aag = (AnimatedActorGroup) aagNode.getUserObject();
        parentAag.removeChild( aag );
        aagNode.removeFromParent();
        return parentNode;
    }

    protected PhysModelJTreeNode removeAagScSetNode( PhysModelJTreeNode aagScSetNode ) {
        PhysModelJTreeNode parentNode = (PhysModelJTreeNode) aagScSetNode.getParent();
        ScContainer.Handler handler = (ScContainer.Handler) aagScSetNode.getUserObject();
        handler.container.removeContent( handler.id );
        aagScSetNode.removeFromParent();
        return parentNode;
    }

    protected void clearNode( PhysModelJTreeNode node ) {
        switch ( node.getType() ) {
            case MODEL:
                clearRootNode();
                break;
            case BiScSET:
                clearBiScNode( node );
                break;
            case AAG:
                clearAagNode( node );
                break;
            case AAG_SC:
                clearAagScNode( node );
                break;
            case AAG_SC_SET:
                clearAagScSetNode( node );
                break;
            case BODY_ITEM:
                clearBodyItemNode( node );
                break;
            case BODY_ITEM_GROUP:
                clearBodyItemGroupNode( node );
                break;
            case FIXTURE_SET:
                return;
            case FIXTURE_SET_GROUP:
                clearFixtureSetGroupNode( node );
            case JOINT_ITEM:
                return;
            case JOINT_ITEM_GROUP:
                clearJointItemGroupNode( node );
        }

        treeDataModel.nodeStructureChanged( node );
    }

    protected void clearRootNode( ) {
        model.clearModel();
        rootNode.removeAllChildren();
        loadTreeBiScNodes();
    }

    protected void clearBiScNode( PhysModelJTreeNode node ) {
        ScContainer.Handler handler = (ScContainer.Handler) node.getUserObject();
        BiScSet bset = (BiScSet) handler.get();
        bset.destroyPhysics();
        node.removeAllChildren();
        loadBiScSetNodes( node );
    }

    protected void clearBodyItemGroupNode( PhysModelJTreeNode node ) {
        BiScSet bset = (BiScSet) node.getUserObject();
        bset.destroyPhysics();
        node.removeAllChildren();
        loadBodyItemsNodes( node );
    }

    protected void clearJointItemGroupNode( PhysModelJTreeNode node ) {
        BiScSet bset = (BiScSet) node.getUserObject();
        bset.removeAllJointItems();
        node.removeAllChildren();
        loadJointItemsNodes( node );
    }

    protected void clearBodyItemNode( PhysModelJTreeNode node ) {
        BodyItem bi = (BodyItem) node.getUserObject();
        bi.removeAllFixtureSets();
        bi.clearAag();
        node.removeAllChildren();
        loadBodyItemNodes( node );
    }

    protected void clearFixtureSetGroupNode( PhysModelJTreeNode node ) {
        BodyItem bi = (BodyItem) node.getUserObject();
        bi.removeAllFixtureSets();
        node.removeAllChildren();
        loadFixtureSetsNodes( node );
    }

    protected void clearAagNode( PhysModelJTreeNode node ) {
        AnimatedActorGroup aag = (AnimatedActorGroup) node.getUserObject();
        aag.clearAag();
        node.removeAllChildren();
        loadAagNodes( node );
    }

    protected void clearAagScNode( PhysModelJTreeNode node ) {
        AnimatedActorGroup aag = (AnimatedActorGroup) node.getUserObject();
        aag.getScChildren().clearContent();
        node.removeAllChildren();
        loadAagScNodes( node );
    }

    protected void clearAagScSetNode( PhysModelJTreeNode node ) {
        ScContainer.Handler handler = (ScContainer.Handler) node.getUserObject();
        AnimatedActorGroup aag = (AnimatedActorGroup) handler.get();
        aag.clearAag();
        node.removeAllChildren();
        loadAagScSetNodes(node);
    }


    protected void clearSourceNode( PhysModelJTreeNode sourceNode ) {
        clearNode(sourceNode);
        treeDataModel.nodeStructureChanged( sourceNode );
    }


    protected void removeSourceNode( PhysModelJTreeNode sourceNode ) {
        PhysModelJTreeNode oldParent = (PhysModelJTreeNode) sourceNode.getParent();
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
            PhysModelJTreeNode node = (PhysModelJTreeNode) selPath.getLastPathComponent();
            if ( node.getParent() == null )
                continue;
            newObject = moveNode( node, (PhysModelJTreeNode) node.getParent(), true);
        }

        PhysModelJTreeNode node = findNode( newObject );
        selectNode( node );
        mainGui.makeHistorySnapshot();
    }

    public Object moveNode( PhysModelJTreeNode sourceNode, PhysModelJTreeNode newParentNode, boolean copy ) {
        if ( sourceNode == null ) {
            return null;
        } else {
            Gdx.app.log("PhysModelStructureGuiProcessing.moveNode", "Source: " + sourceNode + " Target: " + newParentNode );
        }

        PhysModelJTreeNode.Type srcType = sourceNode.getType();
        PhysModelJTreeNode.Type tgtType = newParentNode.getType();
        PhysModelJTreeNode oldParent = (PhysModelJTreeNode) sourceNode.getParent();
        Object newObject = null;


        boolean removeSourceWhenMove = false;
        boolean clearSourceWhenMove = false;

        switch ( srcType ) {
            case MODEL:
                return null;
            case BiScSET:
                removeSourceWhenMove = true;
                if ( tgtType == PhysModelJTreeNode.Type.MODEL) {
                    newObject = copyBiScSetNode(sourceNode);
                } else if ( tgtType == PhysModelJTreeNode.Type.BiScSET ) {
                    if ( ( sourceNode == newParentNode ) && !copy )
                        return null;
                    newObject = copyBiScSetNode( sourceNode, newParentNode );
                } else {
                    return null;
                }
                break;
            case AAG:
                removeSourceWhenMove = true;
                if ( tgtType == PhysModelJTreeNode.Type.AAG ) {
                    AnimatedActorGroup sAag = (AnimatedActorGroup) sourceNode.getUserObject();
                    AnimatedActorGroup tAag = (AnimatedActorGroup) newParentNode.getUserObject();
                    if ( !isAagMovable( sAag, tAag ) && !copy )
                        return null;
                    newObject = copyAagNodeToAagNode( sourceNode, newParentNode );
                } else if (tgtType == PhysModelJTreeNode.Type.AAG_SC) {
                        AnimatedActorGroup sAag = (AnimatedActorGroup) sourceNode.getUserObject();
                        AnimatedActorGroup tAag = (AnimatedActorGroup) newParentNode.getUserObject();
                        if ( !isAagMovable( sAag, tAag ) && !copy )
                            return null;
                        newObject = copyAagNodeToAagScNode( sourceNode, newParentNode );
                } else if ( tgtType == PhysModelJTreeNode.Type.AAG_SC_SET ) {
                    PhysModelJTreeNode tAagNode = getAagNode( newParentNode );
                    if ( tAagNode == null )
                        return null;
                    return moveNode( sourceNode, (PhysModelJTreeNode) newParentNode.getParent(), copy );
                } else {
                    return null;
                }
                break;
            case AAG_SC:
                clearSourceWhenMove = true;
                if ( tgtType == PhysModelJTreeNode.Type.AAG ) {
                    AnimatedActorGroup sAag = (AnimatedActorGroup) sourceNode.getUserObject();
                    AnimatedActorGroup tAag = (AnimatedActorGroup) newParentNode.getUserObject();
                    if (!isAagMovable(sAag, tAag) && !copy)
                        return null;
                    newObject = copyAagScNodeToAagNode(sourceNode, newParentNode);
                } else if ( tgtType == PhysModelJTreeNode.Type.AAG_SC ) {
                    return moveNode(sourceNode, (PhysModelJTreeNode) newParentNode.getParent(), copy);
                } else if ( tgtType == PhysModelJTreeNode.Type.AAG_SC_SET ) {
                    PhysModelJTreeNode aagNode = getAagNode( newParentNode );
                    if ( aagNode == null )
                        return null;
                    return moveNode( sourceNode, aagNode, copy );
                } else {
                    return null;
                }
                break;
            case AAG_SC_SET:
                if ( tgtType == PhysModelJTreeNode.Type.AAG ) {
                    removeSourceWhenMove = true;
                    ScContainer.Handler handler = (ScContainer.Handler) sourceNode.getUserObject();
                    AnimatedActorGroup srcAag = (AnimatedActorGroup) handler.get();
                    AnimatedActorGroup tgtAag = (AnimatedActorGroup) newParentNode.getUserObject();
                    if ( srcAag.isParentOf( tgtAag ) && !copy ) {
                        return null;
                    }
                    newObject = copyAagScSetNodeToAagNode( sourceNode, newParentNode );
                } else if ( tgtType == PhysModelJTreeNode.Type.AAG_SC ) {
                    return moveNode( sourceNode, (PhysModelJTreeNode) newParentNode.getParent(), copy );
                } else if ( tgtType == PhysModelJTreeNode.Type.AAG_SC_SET ) {
                    PhysModelJTreeNode aagNode = getAagNode( newParentNode );
                    if ( aagNode == null )
                        return null;
                    return moveNode( sourceNode, aagNode, copy );
                } else {
                    return null;
                }
                break;
            case BODY_ITEM:
                if ( tgtType == PhysModelJTreeNode.Type.BiScSET ) {
                    removeSourceWhenMove = true;
                    BodyItem bi = (BodyItem) sourceNode.getUserObject();
                    ScContainer.Handler handler = (ScContainer.Handler) newParentNode.getUserObject();
                    BiScSet bset = (BiScSet) handler.get();
                    if ( bset.getBodyItems().contains( bi, true)  && !copy )
                        return false;
                    if ( !copy )
                        editorScreen.setModelObject( null, EditorScreen.ModelObjectType.OT_None );
                    newObject = copyBodyItemNode( sourceNode, newParentNode );

                } else if ( tgtType == PhysModelJTreeNode.Type.BODY_ITEM_GROUP ) {
                    BodyItem bi = (BodyItem) sourceNode.getUserObject();
                    BiScSet bset = (BiScSet) newParentNode.getUserObject();
                    if ( bset.getBodyItems().contains( bi, true)  && !copy )
                        return null;
                     return moveNode(sourceNode, (PhysModelJTreeNode) newParentNode.getParent(), copy );


                } else {
                    return null;
                }

                break;
            case BODY_ITEM_GROUP:
                if ( tgtType == PhysModelJTreeNode.Type.BiScSET ) {
                    clearSourceWhenMove = true;
                    ScContainer.Handler handler = (ScContainer.Handler) newParentNode.getUserObject();
                    BiScSet bset = (BiScSet) handler.get();
                    if ((bset == sourceNode.getUserObject()) && !copy)
                        return null;
                    newObject = copyBodyItemGroupNode(sourceNode, newParentNode);
                } else if ( tgtType == PhysModelJTreeNode.Type.BODY_ITEM_GROUP ) {
                    return moveNode( sourceNode, (PhysModelJTreeNode) newParentNode.getParent(), copy );
                } else {
                    return null;
                }
                break;
            case FIXTURE_SET:
                if ( tgtType == PhysModelJTreeNode.Type.BODY_ITEM ) {
                    removeSourceWhenMove = true;
                    BodyItem bi = (BodyItem) newParentNode.getUserObject();
                    FixtureSet fs = (FixtureSet) sourceNode.getUserObject();
                    if ( bi.getFixtureSets().contains( fs, true ) && !copy )
                        return null;
                    if ( !copy )
                        editorScreen.setModelObject( null, EditorScreen.ModelObjectType.OT_None );
                    newObject = copyFixtureSetNode( sourceNode, newParentNode );
                } else if ( tgtType == PhysModelJTreeNode.Type.FIXTURE_SET_GROUP ) {
                    return moveNode( sourceNode, (PhysModelJTreeNode) newParentNode.getParent(), copy );
                } else {
                    return null;
                }
                break;
            case FIXTURE_SET_GROUP:
                if ( tgtType == PhysModelJTreeNode.Type.BODY_ITEM ) {
                    clearSourceWhenMove = true;
                    BodyItem sBi = (BodyItem) sourceNode.getUserObject();
                    BodyItem tBi = (BodyItem) newParentNode.getUserObject();
                    if ( ( sBi == tBi ) && !copy )
                        return null;
                    newObject = copyFixtureSetGroupNode( sourceNode, newParentNode );
                } else if ( tgtType == PhysModelJTreeNode.Type.FIXTURE_SET_GROUP ) {
                    return moveNode( sourceNode, (PhysModelJTreeNode) newParentNode.getParent(), copy );
                } else {
                    return null;
                }
                break;
            case JOINT_ITEM:
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

    public Object copyBiScSetNode(PhysModelJTreeNode sNode) {
        if ( sNode.getType() != PhysModelJTreeNode.Type.BiScSET )
            return null;

        ScContainer.Handler handler = (ScContainer.Handler) sNode.getUserObject();
        Gdx.app.log("PhysModelStructureGuiProcessing.copyBiScSetNode", "sNode: " + sNode);

        Integer newId = handler.container.generateId();
        BiScContainer container = (BiScContainer) handler.container;
        container.copyContent( handler.id, newId );
        handler = new ScContainer.Handler( container, newId );
        PhysModelJTreeNode newNode = new PhysModelJTreeNode(PhysModelJTreeNode.Type.BiScSET, handler );
        loadBiScSetNodes( newNode );
        rootNode.add( newNode );
        return handler;
    }

    public Object copyBiScSetNode( PhysModelJTreeNode sNode, PhysModelJTreeNode newPNode ) {
        if ( sNode.getType() != PhysModelJTreeNode.Type.BiScSET )
            return null;
        if ( newPNode.getType() != PhysModelJTreeNode.Type.BiScSET )
            return null;

        ScContainer.Handler handler = (ScContainer.Handler) sNode.getUserObject();
        BiScSet bset = (BiScSet) handler.container.getContent( handler.id );

        handler = (ScContainer.Handler) newPNode.getUserObject();
        BiScSet bsetTarget = (BiScSet) handler.container.getContent( handler.id );

        bsetTarget.copyBiScSetContent( bset );
        newPNode.removeAllChildren();
        loadBiScSetNodes( newPNode );
        return bsetTarget;
    }

    public Object copyBodyItemGroupNode(PhysModelJTreeNode sNode, PhysModelJTreeNode newPNode) {
        if ( sNode.getType() != PhysModelJTreeNode.Type.BODY_ITEM_GROUP )
            return null;
        if ( newPNode.getType() != PhysModelJTreeNode.Type.BiScSET )
            return null;
        ScContainer.Handler handler = (ScContainer.Handler) newPNode.getUserObject();
        BiScSet bset = (BiScSet) handler.get();
        BiScSet sBset = (BiScSet) sNode.getUserObject();
        bset.copyBodyItemArray( sBset.getBodyItems() );
        newPNode.removeAllChildren();
        loadBiScSetNodes( newPNode );
        return bset;
    }

    public Object copyBodyItemNode(PhysModelJTreeNode sNode, PhysModelJTreeNode newPNode) {
        if ( sNode.getType() != PhysModelJTreeNode.Type.BODY_ITEM )
            return null;
        if ( newPNode.getType() != PhysModelJTreeNode.Type.BiScSET )
            return null;
        BodyItem bi = (BodyItem) sNode.getUserObject();
        ScContainer.Handler handler = (ScContainer.Handler) newPNode.getUserObject();
        BiScSet bset = (BiScSet) handler.get();
        Object ret = bset.copyBodyItem( bi );
        newPNode.removeAllChildren();
        loadBiScSetNodes( newPNode );
        return ret;
    }

    public Object copyFixtureSetGroupNode( PhysModelJTreeNode sNode, PhysModelJTreeNode newPNode ) {
        if ( sNode.getType() != PhysModelJTreeNode.Type.FIXTURE_SET_GROUP )
            return null;
        if ( newPNode.getType() != PhysModelJTreeNode.Type.BODY_ITEM )
            return null;
        BodyItem bi = (BodyItem) newPNode.getUserObject();
        BodyItem sBi = (BodyItem) sNode.getUserObject();

        bi.copyFixtureSetArray(sBi.getFixtureSets());

        PhysModelJTreeNode fsgNode = getFixtureSetGroupNode( newPNode );
        if ( fsgNode == null )
            return null;

        fsgNode.removeAllChildren();
        loadFixtureSetsNodes( fsgNode );
        return bi;
    }

    public Object copyFixtureSetNode( PhysModelJTreeNode sNode, PhysModelJTreeNode newPNode ) {
        if ( sNode.getType() != PhysModelJTreeNode.Type.FIXTURE_SET )
            return null;
        if ( newPNode.getType() != PhysModelJTreeNode.Type.BODY_ITEM )
            return null;
        FixtureSet fs = (FixtureSet) sNode.getUserObject();
        BodyItem bi = (BodyItem) newPNode.getUserObject();
        FixtureSet newFs = bi.copyFixtureSet( fs );

        PhysModelJTreeNode fsgNode = getFixtureSetGroupNode( newPNode );
        if ( fsgNode == null )
            return null;

        fsgNode.removeAllChildren();
        loadFixtureSetsNodes( fsgNode );
        return newFs;
    }

    public Object copyAagScNodeToAagNode( PhysModelJTreeNode sNode, PhysModelJTreeNode newPNode ) {
        if ( sNode.getType() != PhysModelJTreeNode.Type.AAG_SC )
            return null;
        if ( newPNode.getType() != PhysModelJTreeNode.Type.AAG )
            return null;
        AnimatedActorGroup sAag = (AnimatedActorGroup) sNode.getUserObject();
        AagScContainer sCont = sAag.getScChildren();
        AnimatedActorGroup tAag = (AnimatedActorGroup) newPNode.getUserObject();
        AagScContainer tCont = tAag.getScChildren();
        tCont.copyContainer( sCont );

        PhysModelJTreeNode aagScNode = getAagScNode( newPNode );
        aagScNode.removeAllChildren();
        loadAagScNodes( aagScNode );
        return tAag;
    }

    public Object copyAagScSetNodeToAagNode( PhysModelJTreeNode sNode, PhysModelJTreeNode newPNode ) {
        if ( sNode.getType() != PhysModelJTreeNode.Type.AAG_SC_SET )
            return null;
        if ( newPNode.getType() != PhysModelJTreeNode.Type.AAG )
            return null;
        ScContainer.Handler handler = (ScContainer.Handler) sNode.getUserObject();
        AnimatedActorGroup tAag = (AnimatedActorGroup) newPNode.getUserObject();
        tAag.getScChildren().addContentAsCopy( handler );
        PhysModelJTreeNode scNode = getAagScNode( newPNode );
        if ( scNode == null )
            return null;
        scNode.removeAllChildren();
        loadAagScNodes( scNode );
        return tAag;
    }

    public Object copyAagNodeToAagScNode( PhysModelJTreeNode sNode, PhysModelJTreeNode newPNode ) {
        if ( sNode.getType() != PhysModelJTreeNode.Type.AAG )
            return null;
        if ( newPNode.getType() != PhysModelJTreeNode.Type.AAG_SC )
            return null;
        AnimatedActorGroup sAag = (AnimatedActorGroup) sNode.getUserObject();
        AnimatedActorGroup tAag = (AnimatedActorGroup) newPNode.getUserObject();
        tAag.getScChildren().addContentAsCopy( tAag.getScChildren().generateId(), sAag );
        newPNode.removeAllChildren();
        loadAagScNodes(newPNode);
        return tAag;
    }

    public Object copyAagNodeToAagNode( PhysModelJTreeNode sNode, PhysModelJTreeNode newPNode ) {
        if ( sNode.getType() != PhysModelJTreeNode.Type.AAG )
            return null;
        if ( newPNode.getType() != PhysModelJTreeNode.Type.AAG )
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
            PhysModelJTreeNode node = (PhysModelJTreeNode) selPath.getLastPathComponent();
            objectArray.add( node.getUserObject() );
        }

        for ( Object obj:  objectArray ) {
            PhysModelJTreeNode node = findNode( obj );
            if ( node.getParent() == null )
                continue;
            duplicateNode(node, number, xOffset, yOffset, rotation);

        }


        mainGui.makeHistorySnapshot();


    }


    protected void duplicateNode(PhysModelJTreeNode node, int number, float xOffset, float yOffset, float rotation) {
        if ( node.getParent() == null )
            return;

        PhysModelJTreeNode parentNode = (PhysModelJTreeNode) node.getParent();


        switch (node.getType()) {
            case MODEL:
                return;
            case BiScSET:
                return;
            case AAG:
                if ( parentNode.getType() != PhysModelJTreeNode.Type.AAG )
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
            case BODY_ITEM:
                for ( int i = 0; i < number; i ++ ) {
                    BodyItem bi = (BodyItem) node.getUserObject();
                    BiScSet bset = (BiScSet) parentNode.getUserObject();
                    BodyItem newBi = bset.copyBodyItem( bi );
                    Body body = newBi.getBody();
                    body.setTransform(PhysWorld.get().toPhys(xOffset * (i + 1) )  + body.getPosition().x,
                            PhysWorld.get().toPhys(yOffset * (i + 1) )  + body.getPosition().y,
                            MathUtils.degreesToRadians * rotation * (i + 1) + body.getAngle());
                    newBi.setName( newBi.getName() + "_c" + (i+1) );
                }
                parentNode.removeAllChildren();
                loadBodyItemsNodes(parentNode);
                break;
            case BODY_ITEM_GROUP:
                return;
            case FIXTURE_SET:
                return;
            case FIXTURE_SET_GROUP:
                return;
            case JOINT_ITEM:
                return;
            case JOINT_ITEM_GROUP:
                return;
        }

        treeDataModel.nodeStructureChanged( parentNode );
        mainGui.makeHistorySnapshot();
    }


    public void reloadJointItemNodes() {
        PhysModelJTreeNode node = (PhysModelJTreeNode) jTreeModel.getLastSelectedPathComponent();
        if ( node == null )
            return;
        reloadJointItemNodes( node );
    }

    public void reloadJointItemNodes( PhysModelJTreeNode jointGroupNode ) {
        if ( jointGroupNode.getType() != PhysModelJTreeNode.Type.JOINT_ITEM_GROUP )
            return;
        jointGroupNode.removeAllChildren();
        loadJointItemsNodes( jointGroupNode );
        treeDataModel.nodeStructureChanged( jointGroupNode );
    }

    public void mirrorNode( PhysModelProcessing.MirrorDirection dir ) {
        TreePath [] selectedPaths = jTreeModel.getSelectionPaths();

        if ( selectedPaths == null )
            return;
        boolean res = false;
        for ( TreePath tp : selectedPaths ) {
            PhysModelJTreeNode node = (PhysModelJTreeNode) tp.getLastPathComponent();

            switch ( node.getType() ) {
                case MODEL:
                    mirrorModel( dir );
                    res = true;
                    break;
                case BiScSET:
                    break;
                case AAG:
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
                case BODY_ITEM:
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
                case JOINT_ITEM:
                    break;
                case JOINT_ITEM_GROUP:
                    break;
            }
        }

        if ( res )
            mainGui.makeHistorySnapshot();
    }


    protected boolean mirrorAag( PhysModelJTreeNode aagNode , PhysModelProcessing.MirrorDirection dir ) {
        if ( aagNode.getType() != PhysModelJTreeNode.Type.AAG )
            return false;
        AnimatedActorGroup aag = (AnimatedActorGroup) aagNode.getUserObject();
        AagDescription desc = aag.getAagDescription();
        PhysModelProcessing.mirrorAagDescription(desc, dir);
        aagNode.removeAllChildren();
        aag.loadFromDescription(desc, SkrGdxApplication.get().getAtlas());
        loadAagNodes( aagNode );
        treeDataModel.nodeStructureChanged( aagNode );
        return true;
    }

    protected boolean mirrorBodyItem( PhysModelJTreeNode node, PhysModelProcessing.MirrorDirection dir ) {
        if ( node.getType() != PhysModelJTreeNode.Type.BODY_ITEM )
            return false;
        BodyItem bi = (BodyItem) node.getUserObject();
        BodyItemDescription desc = bi.getBodyItemDescription();
        PhysModelProcessing.mirrorBodyItemDescription(desc, dir);
        bi.destroyPhysics();
        bi.loadFromDescription( desc );
        reloadJointItemNodes();
        return true;
    }

    protected boolean mirrorFixtureSet( PhysModelJTreeNode node, PhysModelProcessing.MirrorDirection dir ) {
        if ( node.getType() != PhysModelJTreeNode.Type.FIXTURE_SET )
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
        model.loadFromDescription( desc );
        rootNode.removeAllChildren();
        loadTreeBiScNodes();
    }





    PhysModelDescription createDescriptionForSelection() {
        PhysModelDescription desc = new PhysModelDescription();
        desc.setName( "export_" + model.getName());

        TreePath [] selectionPaths = jTreeModel.getSelectionPaths();
        if ( selectionPaths == null )
            return null;
        boolean addJoints = true;
        for ( TreePath tp : selectionPaths ) {
            PhysModelJTreeNode node = (PhysModelJTreeNode) tp.getLastPathComponent();

            switch ( node.getType() ) {
                case MODEL:
                    return model.getDescription();
                case BiScSET:
                    setBiScSetNodeToModelDescritpion( node, desc );
                    addJoints = false;
                    break;
                case AAG:
                    break;
                case AAG_SC:
                    break;
                case AAG_SC_SET:
                    break;
                case BODY_ITEM:
                    setBodyItemNodeToModelDesc(node, desc);
                    break;
                case BODY_ITEM_GROUP:
                    setBodyItemGroupNodeToModelDesc(node, desc);
                    break;
                case FIXTURE_SET:
                    break;
                case FIXTURE_SET_GROUP:
                    break;
                case JOINT_ITEM:
                    break;
                case JOINT_ITEM_GROUP:
                    break;
            }
        }
        if ( addJoints )
            setJointItemNodesToModelDesc( desc );
        return desc;
    }

    protected void setBiScSetNodeToModelDescritpion( PhysModelJTreeNode biScSetNode, PhysModelDescription desc ) {
        if ( biScSetNode.getType() != PhysModelJTreeNode.Type.BiScSET )
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

    protected void setBodyItemGroupNodeToModelDesc(PhysModelJTreeNode bigNode, PhysModelDescription desc) {
        if ( bigNode.getType() != PhysModelJTreeNode.Type.BODY_ITEM_GROUP )
            return;
        PhysModelJTreeNode biScNode = (PhysModelJTreeNode) bigNode.getParent();
        setBiScSetNodeToModelDescritpion( biScNode, desc );
    }


    protected void setBodyItemNodeToModelDesc(PhysModelJTreeNode biNode, PhysModelDescription desc) {
        if ( biNode.getType() != PhysModelJTreeNode.Type.BODY_ITEM )
            return;
        PhysModelJTreeNode biGroupNode = (PhysModelJTreeNode) biNode.getParent();
        PhysModelJTreeNode biScSetNode = (PhysModelJTreeNode) biGroupNode.getParent();

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

    protected void setJointItemNodeToModelDesc( PhysModelJTreeNode jiNode, PhysModelDescription desc ) {
        if ( jiNode.getType() != PhysModelJTreeNode.Type.JOINT_ITEM )
            return;
        PhysModelJTreeNode jiGroupNode = (PhysModelJTreeNode) jiNode.getParent();
        PhysModelJTreeNode biScSetNode = (PhysModelJTreeNode) jiGroupNode.getParent();
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

    protected BodyItemDescription findBodyItemDescription( int id, PhysModelDescription desc ) {
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
            PhysModelJTreeNode biScSetNode = (PhysModelJTreeNode) rootNode.getChildAt( i );
            PhysModelJTreeNode jiGroupNode = getJointGroupNode( biScSetNode );
            if ( jiGroupNode == null )
                continue;
            for ( int j = 0; j < jiGroupNode.getChildCount(); j++ ) {
                PhysModelJTreeNode jiNode = (PhysModelJTreeNode) jiGroupNode.getChildAt( j );
                JointItem ji = (JointItem) jiNode.getUserObject();
                int bId = ji.getBodyAId();
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
        model.mergeFromDescription(desc);
        rootNode.removeAllChildren();
        loadTreeBiScNodes();
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
        PhysModelJTreeNode node = (PhysModelJTreeNode) jTreeModel.getLastSelectedPathComponent();

        switch ( node.getType() ) {
            case AAG:
                AnimatedActorGroup aag = (AnimatedActorGroup) node.getUserObject();
                propCpyDescRef = aag.getAagDescription();
                break;
            case BODY_ITEM:
                BodyItem bi = (BodyItem) node.getUserObject();
                propCpyDescRef = bi.getBodyItemDescription();
                break;
            case FIXTURE_SET:
                FixtureSet fs = (FixtureSet) node.getUserObject();
                propCpyDescRef = fs.getDescription();
                break;
            case JOINT_ITEM:
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
            PhysModelJTreeNode node = (PhysModelJTreeNode) tp.getLastPathComponent();
            if ( node.type != propCpyNodeType )
                continue;
            switch ( node.type ) {
                case AAG:
                    pasteAagNodeProperties( node );
                    break;
                case BODY_ITEM:
                    pasteBodyItemNodeProperties( node );
                    break;
                case FIXTURE_SET:
                    pasteFixtureSetNodeProperties( node );
                    break;
                case JOINT_ITEM:
                    pasteJointItemNodeProperties( node );
                    break;
                default:
                    break;
            }
        }
        updatePropertiesTable();
        mainGui.makeHistorySnapshot();
    }


    protected void pasteAagNodeProperties( PhysModelJTreeNode node ) {
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

    protected void pasteBodyItemNodeProperties( PhysModelJTreeNode node ) {
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

    protected void pasteFixtureSetNodeProperties( PhysModelJTreeNode node ) {
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

    protected void pasteJointItemNodeProperties( PhysModelJTreeNode node ) {
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

}
