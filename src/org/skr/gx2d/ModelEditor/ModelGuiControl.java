package org.skr.gx2d.ModelEditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.joints.*;
import com.badlogic.gdx.utils.Array;
import org.skr.gx2d.ModelEditor.PropertiesTableElements.*;
import org.skr.gx2d.ModelEditor.gdx.SkrGx2DModelEditorGdxApp;
import org.skr.gx2d.ModelEditor.gdx.screens.EditorScreen;
import org.skr.gx2d.model.Model;
import org.skr.gx2d.node.Node;
import org.skr.gx2d.physnodes.*;
import org.skr.gx2d.physnodes.physdef.ShapeDefinition;
import org.skr.gx2d.sprite.Sprite;

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
                case SPRITE_GROUP:
                    break;
                case SPRITE:
                    if ( parentNode.getType() == Gx2dJTreeNode.Type.BODY_HANDLER)
                        break;
                    return true;
                case BH_GROUP:
                    break;
                case BODY_HANDLER:
                    return true;
                case FS_GROUP:
                    break;
                case FIXTURE_SET:
                    return true;
                case JH_GROUP:
                    break;
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
                case SPRITE_GROUP:
                    break;
                case SPRITE:
                    if ( tType == Gx2dJTreeNode.Type.SPRITE )
                        return true;
                    break;
                case BH_GROUP:
                    break;
                case BODY_HANDLER:
                    break;
                case FS_GROUP:
                    break;
                case FIXTURE_SET:
                    if ( tType == Gx2dJTreeNode.Type.BODY_HANDLER )
                        return true;
                    break;
                case JH_GROUP:
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
            this.sourceTree = guiProc.getJTreeModel();
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
            this.targetTree = guiProc.getJTreeModel();
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
    private SpritePropertiesTableModel spritePropertiesTableModel;
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
        spritePropertiesTableModel = new SpritePropertiesTableModel( this.jTreeModel);
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

    public JTree getJTreeModel() {
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
        loadBodyHandlerJNodes( physSetJNode );
        loadJointHandlerJNodes( physSetJNode );
    }

    protected void loadJointHandlerJNodes( Gx2dJTreeNode physSetJNode ) {
        PhysSet ps = (PhysSet) physSetJNode.getUserObject();
        Gx2dJTreeNode jgJNode = new Gx2dJTreeNode(Gx2dJTreeNode.Type.JH_GROUP, ps );
        physSetJNode.add( jgJNode );
        JointHandler jh = ps.getJointHandler();
        if ( jh == null )
            return;
        for ( Node node : jh ) {
            JointHandler j = (JointHandler) node;
            Gx2dJTreeNode jJNode = new Gx2dJTreeNode(Gx2dJTreeNode.Type.JOINT_HANDLER, j );
            jgJNode.add( jJNode );
        }
    }

    protected void loadBodyHandlerJNodes( Gx2dJTreeNode physSetJNode ) {
        PhysSet ps = (PhysSet) physSetJNode.getUserObject();
        Gx2dJTreeNode bgJNode = new Gx2dJTreeNode(Gx2dJTreeNode.Type.BH_GROUP, ps );
        physSetJNode.add( bgJNode );
        BodyHandler bh = ps.getBodyHandler();
        if ( bh == null )
            return;
        for ( Node node : bh ) {
            BodyHandler b = (BodyHandler) node;
            Gx2dJTreeNode bJNode = new Gx2dJTreeNode(Gx2dJTreeNode.Type.BODY_HANDLER, b);
            loadBodyHandlerJNode(bJNode);
            bgJNode.add( bJNode );
        }
    }

    protected void loadBodyHandlerJNode(Gx2dJTreeNode bhJNode) {
        BodyHandler bh = (BodyHandler) bhJNode.getUserObject();
        Gx2dJTreeNode spriteJNode = new Gx2dJTreeNode(Gx2dJTreeNode.Type.SPRITE, bh );
        bhJNode.add( spriteJNode );
        loadFsJNodes(bhJNode);
        loadSpriteJNode(spriteJNode);
    }

    protected void loadFsJNodes( Gx2dJTreeNode bhJNode ) {
        BodyHandler bh = (BodyHandler) bhJNode.getUserObject();
        Gx2dJTreeNode fsgJNode = new Gx2dJTreeNode(Gx2dJTreeNode.Type.FS_GROUP, bh );
        bhJNode.add( fsgJNode );
        FixtureSet fs = bh.getFixtureSet();
        if ( fs == null )
            return;
        for( Node node : fs ) {
            FixtureSet f = (FixtureSet) node;
            Gx2dJTreeNode fsJNode = new Gx2dJTreeNode(Gx2dJTreeNode.Type.FIXTURE_SET, f );
            fsgJNode.add( fsJNode );
        }
    }

    protected void loadSpriteJNode(Gx2dJTreeNode spriteJNode) {
        Sprite sprite = (Sprite) spriteJNode.getUserObject();
        Gx2dJTreeNode sgJNode = new Gx2dJTreeNode(Gx2dJTreeNode.Type.SPRITE_GROUP, sprite );
        Sprite ssprt = sprite.getSubSprite();
        if ( ssprt == null )
            return;
        for ( Node n : ssprt ) {
            Sprite subSprite = (Sprite) n;
            Gx2dJTreeNode ssJNode = new Gx2dJTreeNode(Gx2dJTreeNode.Type.SPRITE, subSprite );
            sgJNode.add( ssJNode );
            loadSpriteJNode( ssJNode );
        }
    }

    protected Gx2dJTreeNode getChildJNode(Gx2dJTreeNode parentNode, Gx2dJTreeNode.Type childType) {
        for ( int i = 0; i < parentNode.getChildCount(); i++ ) {
            Gx2dJTreeNode node = (Gx2dJTreeNode) parentNode.getChildAt( i );
            if ( node.getType() == childType )
                return node;
        }
        return null;
    }

    protected void checkJTreeSelection() {

        TreePath [] selectionPaths = jTreeModel.getSelectionPaths();
        if ( selectionPaths == null )
            return;
        if ( selectionPaths.length < 2 )
            return;

        Gx2dJTreeNode firstJNode = (Gx2dJTreeNode) selectionPaths[0].getLastPathComponent();

        for ( int i = 1; i < selectionPaths.length; i++ ) {
            Gx2dJTreeNode node = (Gx2dJTreeNode) selectionPaths[i].getLastPathComponent();
            if ( node.getType() != firstJNode.getType() )
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

        checkJTreeSelection();

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
            case PHYS_SET:
                return EditorScreen.ModelObjectType.OT_PhysSet;
            case SPRITE_GROUP:
                break;
            case SPRITE:
                return EditorScreen.ModelObjectType.OT_Sprite;
            case BH_GROUP:
                break;
            case BODY_HANDLER:
                return EditorScreen.ModelObjectType.OT_BodyHandler;
            case FS_GROUP:
                break;
            case FIXTURE_SET:
                return EditorScreen.ModelObjectType.OT_FixtureSet;
            case JH_GROUP:
                break;
            case JOINT_HANDLER:
                return EditorScreen.ModelObjectType.OT_JointHandler;
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
            case PHYS_SET:
                break;
            case SPRITE_GROUP:
                break;
            case SPRITE:
                spritePropertiesTableModel.setSprite((Sprite) selNode.getUserObject());
                jTableProperties.setModel(spritePropertiesTableModel);
                break;
            case BH_GROUP:
                break;
            case BODY_HANDLER:
                BodyHandler bh = ( BodyHandler ) selNode.getUserObject();
                bodyPropertiesTableModel.setBodyHandler(bh);
                jTableProperties.setModel(bodyPropertiesTableModel);
                break;
            case FS_GROUP:
                break;
            case FIXTURE_SET:
                FixtureSet fs = ( FixtureSet ) selNode.getUserObject();
                fixtureSetPropertiesTableModel.setFixtureSet(fs);
                jTableProperties.setModel( fixtureSetPropertiesTableModel );
                break;
            case JH_GROUP:
                break;
            case JOINT_HANDLER:
                JointHandler jh = (JointHandler) selNode.getUserObject();
                jointPropertiesTableModel.setJointHandler(jh);
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
        Gx2dJTreeNode parentJNode = (Gx2dJTreeNode) jTreeModel.getLastSelectedPathComponent();
        Gx2dJTreeNode newJNode = null;

        switch ( parentJNode.getType() ) {

            case MODEL:
                newJNode = createNewPhysSetNode( (Model) parentJNode.getUserObject());
                break;
            case PHYS_SET:
                break;
            case SPRITE_GROUP:
                break;
            case SPRITE:
                newJNode = createNewSprite((Sprite) parentJNode.getUserObject());
                break;
            case BH_GROUP:
                newJNode = createNewBodyHandler((PhysSet) parentJNode.getUserObject());
                break;
            case BODY_HANDLER:
                break;
            case FS_GROUP:
                newJNode = createNewFixtureSet((BodyHandler) parentJNode.getUserObject());
                break;
            case FIXTURE_SET:
                break;
            case JH_GROUP:
                if ( ! dlgNewJointSelector.execute() )
                    break;
                newJNode = createNewEmptyJointHandler((PhysSet) parentJNode.getUserObject(), dlgNewJointSelector.getSelectedJointType());
                break;
            case JOINT_HANDLER:
                break;
        }

        if ( newJNode == null )
            return;

        parentJNode.add(newJNode);
        treeDataModel.nodeStructureChanged( parentJNode );
        selectNode( newJNode );
        mainGui.makeHistorySnapshot();
    }

    private Gx2dJTreeNode createNewPhysSetNode(Model model) {
        PhysSet ps = new PhysSet();
        ps.setName("NewPS");
        model.addPhysSet( ps );
        Gx2dJTreeNode psJNode = new Gx2dJTreeNode(Gx2dJTreeNode.Type.PHYS_SET, ps );
        loadPhysSetNode( psJNode );
        return psJNode;
    }

    public Gx2dJTreeNode createNewBodyHandler(PhysSet physSet) {
        BodyHandler bh = new BodyHandler();
        bh.setName( "NewBody");
        physSet.addBodyHandler( bh );
        Gx2dJTreeNode bhJNode = new Gx2dJTreeNode(Gx2dJTreeNode.Type.BODY_HANDLER, bh );
        loadBodyHandlerJNodes( bhJNode );
        return bhJNode;
    }

    public Gx2dJTreeNode createNewSprite(Sprite parentSprite) {
        Sprite sprite = new Sprite();
        sprite.setName("NewSprite");
        parentSprite.addSubSprite(sprite);
        Gx2dJTreeNode spriteJNode = new Gx2dJTreeNode(Gx2dJTreeNode.Type.SPRITE, sprite);
        loadSpriteJNode( spriteJNode );
        return spriteJNode;
    }

    public Gx2dJTreeNode createNewFixtureSet(BodyHandler bh) {
        FixtureSet fs = new FixtureSet();
        fs.setName("newFixtureSet");
        bh.addFixtureSet(fs);
        return new Gx2dJTreeNode( Gx2dJTreeNode.Type.FIXTURE_SET, fs );
    }

    public void updateFixtures( Array<ShapeDefinition> shpDefs ) {
        FixtureSet fs = fixtureSetPropertiesTableModel.getFixtureSet();
        fs.createFixtures( shpDefs );
        fixtureSetPropertiesTableModel.fireTableDataChanged();
    }

    public Gx2dJTreeNode createNewEmptyJointHandler(PhysSet physSet, JointDef.JointType type) {
        JointHandler jh = JointHandlerFactory.create(type);
        if ( jh == null )
            return null;
        physSet.addJointHandler(jh);
        return new Gx2dJTreeNode(Gx2dJTreeNode.Type.JOINT_HANDLER, jh );
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
            case OT_PhysSet:
                foundNode = findNode( object, Gx2dJTreeNode.Type.PHYS_SET );
                break;
            case OT_BodyHandler:
                foundNode = findNode( object, Gx2dJTreeNode.Type.BODY_HANDLER);
                break;
            case OT_Sprite:
                foundNode = findNode( object, Gx2dJTreeNode.Type.SPRITE);
                break;
            case OT_FixtureSet:
                foundNode = findNode( object, Gx2dJTreeNode.Type.FIXTURE_SET );
                break;
            case OT_JointHandler:
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
            case PHYS_SET:
                break;
            case SPRITE_GROUP:
                break;
            case SPRITE:
                return removeAagNode( node );
            case AAG_SC:
                break;
            case AAG_SC_SET:
                return removeAagScSetNode( node );
            case BH_GROUP:
                break;
            case BODY_HANDLER:
                return removeBodyItemNode( node );
            case BODY_ITEM_GROUP:
                break;
            case FS_GROUP:
                break;
            case FIXTURE_SET:
                return removeFixtureSetNode( node );
            case FIXTURE_SET_GROUP:
                break;
            case JH_GROUP:
                break;
            case JOINT_HANDLER:
                return removeJointHandlerNode(node);
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
        BodyHandler bh = (BodyHandler) node.getUserObject();

        Gx2dJTreeNode jgNode = getJhGroupJNode((Gx2dJTreeNode) parentNode.getParent());
        Array<Gx2dJTreeNode> jhNodesToRemove = new Array<Gx2dJTreeNode>();

        for ( int i = 0; i < jgNode.getChildCount(); i++) {
            Gx2dJTreeNode jhNode = (Gx2dJTreeNode) jgNode.getChildAt( i );
            JointHandler jh = (JointHandler) jhNode.getUserObject();
            if ( jh.getBodyAId() == bh.getId() ) {
                jhNodesToRemove.add( jhNode );
                continue;
            }
            if ( jh.getBodyBId() == bh.getId() ) {
                jhNodesToRemove.add( jhNode );
                continue;
            }
        }
        for ( Gx2dJTreeNode jhNode : jhNodesToRemove ) {
            removeJointHandlerNode(jhNode);
        }
        treeDataModel.nodeStructureChanged( jgNode );
        bset.removeBodyItem(bh);
        node.removeFromParent();
        cleanupJointHandlerNodes((Gx2dJTreeNode) parentNode.getParent());
        return parentNode;
    }

    protected void cleanupJointHandlerNodes(Gx2dJTreeNode bhScSetNode) {
        Gx2dJTreeNode jhGroupNode = null;
        for ( int i = 0; i < bhScSetNode.getChildCount(); i++) {
            Gx2dJTreeNode node = (Gx2dJTreeNode) bhScSetNode.getChildAt( i );
            if ( node.getType() == Gx2dJTreeNode.Type.JOINT_ITEM_GROUP ) {
                jhGroupNode = node;
                break;
            }
        }
        if ( jhGroupNode == null )
            return ;

        BiScSet bset = (BiScSet) jhGroupNode.getUserObject();
        Array<Gx2dJTreeNode> nodesToRemove  = new Array<Gx2dJTreeNode>();

        for ( int i = 0; i < jhGroupNode.getChildCount(); i++ ) {
            Gx2dJTreeNode node = (Gx2dJTreeNode) jhGroupNode.getChildAt( i );
            JointHandler jh = (JointHandler) node.getUserObject();
            if ( !bset.getJointHandlers().contains( jh, true) ) {
                nodesToRemove.add( node );
            }
        }
        for ( Gx2dJTreeNode node : nodesToRemove )
            node.removeFromParent();
    }

    protected Gx2dJTreeNode removeJointHandlerNode( Gx2dJTreeNode jhNode ) {
        Gx2dJTreeNode parentNode = (Gx2dJTreeNode) jhNode.getParent();
        JointHandler jh = (JointHandler) jhNode.getUserObject();
        BiScSet bset = (BiScSet) parentNode.getUserObject();
        bset.removeJointHandler(jh);
        jhNode.removeFromParent();
        return parentNode;
    }

    protected Gx2dJTreeNode removeFixtureSetNode( Gx2dJTreeNode fsNode ) {
        Gx2dJTreeNode parentNode = (Gx2dJTreeNode) fsNode.getParent();
        BodyHandler bh = (BodyHandler) parentNode.getUserObject();
        FixtureSet fs = (FixtureSet) fsNode.getUserObject();
        bh.removeFixtureSet( fs );
        fsNode.removeFromParent();
        return parentNode;
    }

    protected Gx2dJTreeNode removeAagNode( Gx2dJTreeNode aagNode ) {
        Gx2dJTreeNode parentNode = (Gx2dJTreeNode) aagNode.getParent();
        if ( parentNode.getType() == Gx2dJTreeNode.Type.BODY_HANDLER)
            return null;
        if ( parentNode.getType() == Gx2dJTreeNode.Type.AAG_SC_SET )
            return null;
        Sprite parentAag = (Sprite) parentNode.getUserObject();
        Sprite aag = (Sprite) aagNode.getUserObject();
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
                clearJointHandlerGroupNode(node);
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
        loadBodyHandlerJNode(node);
    }

    protected void clearJointHandlerGroupNode( Gx2dJTreeNode node ) {
        BiScSet bset = (BiScSet) node.getUserObject();
        bset.removeAllJointHandlers();
        node.removeAllChildren();
        loadJointHandlerJNode(node);
    }

    protected void clearBodyItemNode( Gx2dJTreeNode node ) {
        BodyHandler bh = (BodyHandler) node.getUserObject();
        bh.removeAllFixtureSets();
        bh.clearAag();
        node.removeAllChildren();
        loadBodyItemNodes( node );
    }

    protected void clearFixtureSetGroupNode( Gx2dJTreeNode node ) {
        BodyHandler bh = (BodyHandler) node.getUserObject();
        bh.removeAllFixtureSets();
        node.removeAllChildren();
        loadFixtureSetsNodes( node );
    }

    protected void clearAagNode( Gx2dJTreeNode node ) {
        Sprite aag = (Sprite) node.getUserObject();
        aag.clearAag();
        node.removeAllChildren();
        loadAagNodes( node );
    }

    protected void clearAagScNode( Gx2dJTreeNode node ) {
        Sprite aag = (Sprite) node.getUserObject();
        aag.getScChildren().clearContent();
        node.removeAllChildren();
        loadAagScNodes( node );
    }

    protected void clearAagScSetNode( Gx2dJTreeNode node ) {
        ScContainer.Handler handler = (ScContainer.Handler) node.getUserObject();
        Sprite aag = (Sprite) handler.get();
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


    public boolean isAagMovable( Sprite aag, Sprite newParentAag ) {
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
                    Sprite sAag = (Sprite) sourceNode.getUserObject();
                    Sprite tAag = (Sprite) newParentNode.getUserObject();
                    if ( !isAagMovable( sAag, tAag ) && !copy )
                        return null;
                    newObject = copyAagNodeToAagNode( sourceNode, newParentNode );
                } else if (tgtType == Gx2dJTreeNode.Type.AAG_SC) {
                        Sprite sAag = (Sprite) sourceNode.getUserObject();
                        Sprite tAag = (Sprite) newParentNode.getUserObject();
                        if ( !isAagMovable( sAag, tAag ) && !copy )
                            return null;
                        newObject = copyAagNodeToAagScNode( sourceNode, newParentNode );
                } else if ( tgtType == Gx2dJTreeNode.Type.AAG_SC_SET ) {
                    Gx2dJTreeNode tAagNode = getSpriteJNode(newParentNode);
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
                    Sprite sAag = (Sprite) sourceNode.getUserObject();
                    Sprite tAag = (Sprite) newParentNode.getUserObject();
                    if (!isAagMovable(sAag, tAag) && !copy)
                        return null;
                    newObject = copyAagScNodeToAagNode(sourceNode, newParentNode);
                } else if ( tgtType == Gx2dJTreeNode.Type.AAG_SC ) {
                    return moveNode(sourceNode, (Gx2dJTreeNode) newParentNode.getParent(), copy);
                } else if ( tgtType == Gx2dJTreeNode.Type.AAG_SC_SET ) {
                    Gx2dJTreeNode aagNode = getSpriteJNode(newParentNode);
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
                    Sprite srcAag = (Sprite) handler.get();
                    Sprite tgtAag = (Sprite) newParentNode.getUserObject();
                    if ( srcAag.isParentOf( tgtAag ) && !copy ) {
                        return null;
                    }
                    newObject = copyAagScSetNodeToAagNode( sourceNode, newParentNode );
                } else if ( tgtType == Gx2dJTreeNode.Type.AAG_SC ) {
                    return moveNode( sourceNode, (Gx2dJTreeNode) newParentNode.getParent(), copy );
                } else if ( tgtType == Gx2dJTreeNode.Type.AAG_SC_SET ) {
                    Gx2dJTreeNode aagNode = getSpriteJNode(newParentNode);
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
                    BodyHandler bh = (BodyHandler) sourceNode.getUserObject();
                    ScContainer.Handler handler = (ScContainer.Handler) newParentNode.getUserObject();
                    BiScSet bset = (BiScSet) handler.get();
                    if ( bset.getBodyItems().contains( bh, true)  && !copy )
                        return false;
                    if ( !copy )
                        editorScreen.setModelObject( null, EditorScreen.ModelObjectType.OT_None );
                    newObject = copyBodyItemNode( sourceNode, newParentNode );

                } else if ( tgtType == Gx2dJTreeNode.Type.BODY_ITEM_GROUP ) {
                    BodyHandler bh = (BodyHandler) sourceNode.getUserObject();
                    BiScSet bset = (BiScSet) newParentNode.getUserObject();
                    if ( bset.getBodyItems().contains( bh, true)  && !copy )
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
                    BodyHandler bh = (BodyHandler) newParentNode.getUserObject();
                    FixtureSet fs = (FixtureSet) sourceNode.getUserObject();
                    if ( bh.getFixtureSets().contains( fs, true ) && !copy )
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
                    BodyHandler sBi = (BodyHandler) sourceNode.getUserObject();
                    BodyHandler tBi = (BodyHandler) newParentNode.getUserObject();
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
        BodyHandler bh = (BodyHandler) sNode.getUserObject();
        ScContainer.Handler handler = (ScContainer.Handler) newPNode.getUserObject();
        BiScSet bset = (BiScSet) handler.get();
        Object ret = bset.copyBodyItem( bh, null );
        newPNode.removeAllChildren();
        loadPhysSetNode(newPNode);
        return ret;
    }

    public Object copyFixtureSetGroupNode( Gx2dJTreeNode sNode, Gx2dJTreeNode newPNode ) {
        if ( sNode.getType() != Gx2dJTreeNode.Type.FIXTURE_SET_GROUP )
            return null;
        if ( newPNode.getType() != Gx2dJTreeNode.Type.BODY_HANDLER)
            return null;
        BodyHandler bh = (BodyHandler) newPNode.getUserObject();
        BodyHandler sBi = (BodyHandler) sNode.getUserObject();

        bh.copyFixtureSetArray(sBi.getFixtureSets());

        Gx2dJTreeNode fsgNode = getFsGroupJNode(newPNode);
        if ( fsgNode == null )
            return null;

        fsgNode.removeAllChildren();
        loadFixtureSetsNodes( fsgNode );
        return bh;
    }

    public Object copyFixtureSetNode( Gx2dJTreeNode sNode, Gx2dJTreeNode newPNode ) {
        if ( sNode.getType() != Gx2dJTreeNode.Type.FIXTURE_SET )
            return null;
        if ( newPNode.getType() != Gx2dJTreeNode.Type.BODY_HANDLER)
            return null;
        FixtureSet fs = (FixtureSet) sNode.getUserObject();
        BodyHandler bh = (BodyHandler) newPNode.getUserObject();
        FixtureSet newFs = bh.copyFixtureSet( fs );

        Gx2dJTreeNode fsgNode = getFsGroupJNode(newPNode);
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
        Sprite sAag = (Sprite) sNode.getUserObject();
        AagScContainer sCont = sAag.getScChildren();
        Sprite tAag = (Sprite) newPNode.getUserObject();
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
        Sprite tAag = (Sprite) newPNode.getUserObject();
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
        Sprite sAag = (Sprite) sNode.getUserObject();
        Sprite tAag = (Sprite) newPNode.getUserObject();
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
        Sprite sAag = (Sprite) sNode.getUserObject();
        Sprite tAag = (Sprite) newPNode.getUserObject();
        Sprite aag = Sprite.createFromDescription( sAag.getAagDescription(), sAag.getAtlas() );
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
                Sprite parentAag = (Sprite) parentNode.getUserObject();
                Sprite aag = (Sprite) node.getUserObject();
                AagDescription aagDesc = aag.getAagDescription();

                for ( int i = 0; i < number; i++ ) {
                    Sprite newAag = Sprite.createFromDescription( aagDesc, aag.getAtlas() );
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
                    BodyHandler bh = (BodyHandler) node.getUserObject();
                    BiScSet bset = (BiScSet) parentNode.getUserObject();
                    BodyHandler newBi = bset.copyBodyItem( bh, null );
                    Body body = newBi.getBody();
                    body.setTransform(PhysWorld.get().toPhys(xOffset * (i + 1) )  + body.getPosition().x,
                            PhysWorld.get().toPhys(yOffset * (i + 1) )  + body.getPosition().y,
                            MathUtils.degreesToRadians * rotation * (i + 1) + body.getAngle());
                    newBi.setName( newBi.getName() + "_c" + (i+1) );
                }
                parentNode.removeAllChildren();
                loadBodyHandlerJNode(parentNode);
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


    public void reloadJointHandlerNodes() {
        Gx2dJTreeNode node = (Gx2dJTreeNode) jTreeModel.getLastSelectedPathComponent();
        if ( node == null )
            return;
        reloadJointHandlerNodes(node);
    }

    public void reloadJointHandlerNodes( Gx2dJTreeNode jointGroupNode ) {
        if ( jointGroupNode.getType() != Gx2dJTreeNode.Type.JOINT_ITEM_GROUP )
            return;
        jointGroupNode.removeAllChildren();
        loadJointHandlerJNode(jointGroupNode);
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
                    Sprite aag = (Sprite) node.getUserObject();
                    if ( aag instanceof BodyHandler )
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
        Sprite aag = (Sprite) aagNode.getUserObject();
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
        BodyHandler bh = (BodyHandler) node.getUserObject();
        BodyItemDescription desc = bh.getBodyItemDescription();
        PhysModelProcessing.mirrorBodyItemDescription(desc, dir);
        bh.destroyPhysics();
        bh.loadFromDescription( desc, null );
        reloadJointHandlerNodes();
        return true;
    }

    protected boolean mirrorFixtureSet( Gx2dJTreeNode node, PhysModelProcessing.MirrorDirection dir ) {
        if ( node.getType() != Gx2dJTreeNode.Type.FIXTURE_SET )
            return false;
        FixtureSet fs = (FixtureSet) node.getUserObject();
        FixtureSetDescription fsDesc = fs.getDescription();
        PhysModelProcessing.mirrorFixtureSetDescription( fsDesc, dir );

        BodyHandler bh = fs.getBodyItem();
        bh.removeFixtureSet( fs );
        fs = bh.addNewFixtureSet( fsDesc );

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
            setJointHandlerNodesToModelDesc(desc);
        return desc;
    }

    protected void setBiScSetNodeToModelDescritpion( Gx2dJTreeNode bhScSetNode, PhysModelDescription desc ) {
        if ( bhScSetNode.getType() != Gx2dJTreeNode.Type.BiScSET )
            return;
        ScContainer.Handler handler = (ScContainer.Handler) bhScSetNode.getUserObject();
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

    protected void setBodyItemGroupNodeToModelDesc(Gx2dJTreeNode bhgNode, PhysModelDescription desc) {
        if ( bhgNode.getType() != Gx2dJTreeNode.Type.BODY_ITEM_GROUP )
            return;
        Gx2dJTreeNode bhScNode = (Gx2dJTreeNode) bhgNode.getParent();
        setBiScSetNodeToModelDescritpion( bhScNode, desc );
    }


    protected void setBodyItemNodeToModelDesc(Gx2dJTreeNode bhNode, PhysModelDescription desc) {
        if ( bhNode.getType() != Gx2dJTreeNode.Type.BODY_HANDLER)
            return;
        Gx2dJTreeNode bhGroupNode = (Gx2dJTreeNode) bhNode.getParent();
        Gx2dJTreeNode bhScSetNode = (Gx2dJTreeNode) bhGroupNode.getParent();

        ScContainer.Handler handler = (ScContainer.Handler) bhScSetNode.getUserObject();
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

        BiScSetDescription bhScSetDesc = contDesc.getContentMap().get( idStr );
        BodyHandler bh = (BodyHandler) bhNode.getUserObject();
        BodyItemDescription bhDesc = bh.getBodyItemDescription();
        bhScSetDesc.getBodyItemDescriptions().add( bhDesc );
    }

    protected void setJointHandlerNodeToModelDesc( Gx2dJTreeNode jhNode, PhysModelDescription desc ) {
        if ( jhNode.getType() != Gx2dJTreeNode.Type.JOINT_HANDLER)
            return;
        Gx2dJTreeNode jhGroupNode = (Gx2dJTreeNode) jhNode.getParent();
        Gx2dJTreeNode bhScSetNode = (Gx2dJTreeNode) jhGroupNode.getParent();
        ScContainer.Handler handler = (ScContainer.Handler) bhScSetNode.getUserObject();
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

        BiScSetDescription bhScSetDesc = contDesc.getContentMap().get( idStr );
        JointHandler jh = (JointHandler) jhNode.getUserObject();
        JointHandlerDescription jhDesc = jh.getJointHandlerDescription();
        bhScSetDesc.getJointHandlerDescriptions().add( jhDesc );
    }

    protected BodyItemDescription findBodyItemDescription( long id, PhysModelDescription desc ) {
        for ( String idStr : desc.getBiScContainerDescription().getContentMap().keySet() ) {
            BiScSetDescription bhScSetDesc = desc.getBiScContainerDescription().getContentMap().get( idStr );
            for ( BodyItemDescription bhDesc : bhScSetDesc.getBodyItemDescriptions() ) {
                if ( bhDesc.getId() == id )
                    return bhDesc;
            }
        }
        return null;
    }

    protected void setJointHandlerNodesToModelDesc( PhysModelDescription desc ) {
        for ( int i = 0; i < rootNode.getChildCount(); i++ ) {
            Gx2dJTreeNode bhScSetNode = (Gx2dJTreeNode) rootNode.getChildAt( i );
            Gx2dJTreeNode jhGroupNode = getJhGroupJNode(bhScSetNode);
            if ( jhGroupNode == null )
                continue;
            for ( int j = 0; j < jhGroupNode.getChildCount(); j++ ) {
                Gx2dJTreeNode jhNode = (Gx2dJTreeNode) jhGroupNode.getChildAt( j );
                JointHandler jh = (JointHandler) jhNode.getUserObject();
                long bId = jh.getBodyAId();
                if ( bId != -1 && ( findBodyItemDescription( bId, desc ) == null) )
                    continue;
                bId = jh.getBodyBId();
                if ( bId != -1 && ( findBodyItemDescription( bId, desc ) == null) )
                    continue;
                setJointHandlerNodeToModelDesc(jhNode, desc);
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
                Sprite aag = (Sprite) node.getUserObject();
                propCpyDescRef = aag.getAagDescription();
                break;
            case BODY_HANDLER:
                BodyHandler bh = (BodyHandler) node.getUserObject();
                propCpyDescRef = bh.getBodyItemDescription();
                break;
            case FIXTURE_SET:
                FixtureSet fs = (FixtureSet) node.getUserObject();
                propCpyDescRef = fs.getDescription();
                break;
            case JOINT_HANDLER:
                JointHandler jh = (JointHandler) node.getUserObject();
                propCpyDescRef = jh.getJointHandlerDescription();
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
                    pasteJointHandlerNodeProperties(node);
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
        final Sprite aag = (Sprite) node.getUserObject();
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
        final BodyHandler bh = (BodyHandler) node.getUserObject();

        final Body body = bh.getBody();
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

    protected void pasteJointHandlerNodeProperties( Gx2dJTreeNode node ) {
        final JointHandlerDescription desc = (JointHandlerDescription) propCpyDescRef;
        final JointHandler jh = (JointHandler) node.getUserObject();
        if ( jh.getJoint() == null )
            return;

        Gdx.app.postRunnable( new Runnable() {
            @Override
            public void run() {
                Joint joint = jh.getJoint();

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
            Gx2dJTreeNode bhNode = findParentNode( node, type );
            if ( bhNode != null ) {
                newSelections.add( new TreePath( bhNode.getPath() ) );
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

    public void convertSelectionToJointHandlerSelection() {

        TreePath [] selectionPaths = jTreeModel.getSelectionPaths();
        if ( selectionPaths == null )
            return;
        if ( selectionPaths.length == 0 )
            return;
        Gx2dJTreeNode node = (Gx2dJTreeNode) selectionPaths[0].getLastPathComponent();
        if ( node.type == Gx2dJTreeNode.Type.JOINT_HANDLER)
            return;

        Array<JointHandler> jhArray = new Array<JointHandler>();
        Array< BodyHandler > bodyItems = new Array<BodyHandler>();


        // find all bodyItems
        for ( TreePath tp : selectionPaths ) {
            node = (Gx2dJTreeNode) tp.getLastPathComponent();

            if ( node.type == Gx2dJTreeNode.Type.BODY_HANDLER) {
                bodyItems.add( (BodyHandler) node.getUserObject() );
                continue;
            }

            Gx2dJTreeNode bhNode = findParentNode( node, Gx2dJTreeNode.Type.BODY_HANDLER);
            if ( bhNode != null ) {
                bodyItems.add( (BodyHandler) bhNode.getUserObject() );
            } else {
                Array<Gx2dJTreeNode> bhNodes = findAllChildNodes( node, Gx2dJTreeNode.Type.BODY_HANDLER, 10 );
                for ( Gx2dJTreeNode fBiNode : bhNodes ) {
                    BodyHandler bh = (BodyHandler) fBiNode.getUserObject();
                    if ( bodyItems.contains( bh, true ) )
                        continue;
                    bodyItems.add(bh);
                }
            }
        }

        // find all JointHandlers
        for ( BodyHandler bh : bodyItems ) {
            BiScSet bset = bh.getBiScSet();
            Array< JointHandler > fJiArray = bset.findJointHandlers(bh);
            for ( JointHandler jh : fJiArray ) {
                if ( jhArray.contains( jh, true ) ) {
                    continue;
                }
                jhArray.add( jh );
            }
        }

        clearSelection();

        for ( JointHandler jh : jhArray ) {
            Gx2dJTreeNode jhNode = findNode( jh, Gx2dJTreeNode.Type.JOINT_HANDLER);
            if ( jhNode == null ) {
                continue;
            }
            jTreeModel.addSelectionPath(new TreePath(jhNode.getPath()));
        }


        expandToNode((Gx2dJTreeNode) jTreeModel.getLastSelectedPathComponent());
    }

}
