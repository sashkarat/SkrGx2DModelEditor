package org.skr.PhysModelEditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.joints.GearJoint;
import com.badlogic.gdx.physics.box2d.joints.PulleyJoint;
import org.skr.PhysModelEditor.gdx.editor.SkrGdxAppPhysModelEditor;
import org.skr.PhysModelEditor.gdx.editor.controllers.JointEditorController;
import org.skr.gdx.PhysWorld;
import org.skr.gdx.editor.Controller;
import org.skr.gdx.physmodel.PhysModel;
import org.skr.gdx.physmodel.bisc.BiScSet;
import org.skr.gdx.physmodel.bodyitem.BodyItem;
import org.skr.gdx.physmodel.jointitem.JointItem;
import org.skr.gdx.physmodel.jointitem.JointItemDescription;
import org.skr.gdx.physmodel.jointitem.JointItemFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by rat on 19.10.14.
 */
public class JointEditorFrom {
    private JPanel panelRootJointEditorForm;
    private JToggleButton btnBodyItemA;
    private JComboBox comboBodyItemA;
    private JToggleButton btnBodyItemB;
    private JComboBox comboBodyItemB;
    private JCheckBox chbCollideConnected;
    private JTabbedPane tpaneJointOptions;
    private JPanel panelGearJointOptions;
    private JComboBox comboJointItemA;
    private JComboBox comboJointItemB;
    private JTextField tfGearJointRatio;
    private JTextField tfAnchAX;
    private JTextField tfAnchAY;
    private JButton btnSetAnchA;
    private JButton btnSetAnchB;
    private JTextField tfAnchBY;
    private JTextField tfAnchBX;
    private JTextField tfAxisX;
    private JTextField tfAxisY;
    private JButton btnAxisSet;
    private JButton btnSetGrndAnchA;
    private JTextField tfGrndAX;
    private JTextField tfGrndAY;
    private JTextField tfGrndBX;
    private JTextField tfGrndBY;
    private JButton btnSetGrndAnchB;
    private JPanel panelAxisCtrl;
    private JPanel panelGrndACtrl;
    private JPanel panelGrndBCtrl;
    private JPanel panelAnchorPoints;
    private JRadioButton rbViewCoordinates;
    private JRadioButton rbPhysCoordinates;
    private JPanel panelAnchACtrl;
    private JPanel panelAnchBCtrl;
    private JTextField tfPulleyJointRatio;
    private JPanel panelPulleyJointOptions;

    JointEditorController jeController;
    JointItem jointItem;
    boolean useViewCoordinates = true;


    public JointEditorFrom() {

        btnBodyItemA.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setBodyItemSelectionMode( true, btnBodyItemA.isSelected() );
            }
        });

        btnBodyItemB.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setBodyItemSelectionMode( false, btnBodyItemB.isSelected() );
            }
        });

        comboBodyItemA.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jeController.setBodyItem((BodyItem) comboBodyItemA.getSelectedItem(), true );
            }
        });

        comboBodyItemB.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jeController.setBodyItem((BodyItem) comboBodyItemB.getSelectedItem(), false );
            }
        });
        rbViewCoordinates.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeCoordinatesMode( rbViewCoordinates.isSelected() );
            }
        });
        rbPhysCoordinates.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeCoordinatesMode( rbViewCoordinates.isSelected() );
            }
        });
        btnSetAnchA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAnchorPointA();
            }

        });
        btnSetAnchB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAnchorPointB();
            }
        });
        btnAxisSet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAxisPoint();
            }
        });
        btnSetGrndAnchA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setGroundAnchPointA();
            }
        });
        btnSetGrndAnchB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setGroundAnchPointB();
            }
        });
    }

    public void setupGdxApp() {
        jeController = SkrGdxAppPhysModelEditor.get().getEditorScreen().getJointEditorController();
        jeController.setBodyItemSelectionListener( new JointEditorController.BodyItemSelectionListener() {
            @Override
            public void bodyItemSelected(BodyItem bodyItem, boolean isA) {
                if ( isA ) {
                    btnBodyItemA.setSelected(false );
                    comboBodyItemA.setSelectedItem( bodyItem );
                } else {
                    btnBodyItemB.setSelected( false );
                    comboBodyItemB.setSelectedItem( bodyItem );
                }
            }
        });

        jeController.setControlPointListener( new Controller.controlPointListener() {
            @Override
            public void changed(Object controlledObject, Controller.ControlPoint controlPoint) {
                if ( !( controlPoint instanceof JointEditorController.AnchorControlPoint) )
                    return;
                updateAnchorPointsGui((JointEditorController.AnchorControlPoint) controlPoint);
            }
        });


    }

    protected void transformCoordinates(JTextField tfX, JTextField tfY) {
        try {
            float x = ( tfX.getText().isEmpty() ) ? 0 : Float.valueOf( tfX.getText() );
            float y = ( tfY.getText().isEmpty() ) ? 0 : Float.valueOf( tfY.getText() );
            if ( useViewCoordinates ) {
                x = PhysWorld.get().toView( x );
                y = PhysWorld.get().toView( y );
            } else {
                x = PhysWorld.get().toPhys( x );
                y = PhysWorld.get().toPhys( y );
            }
            tfX.setText( String.valueOf( x ) );
            tfY.setText( String.valueOf( y ) );
        } catch ( NumberFormatException e ) {
            Gdx.app.error("JointEditorFrom.transformCoordinates", "WARNING: " + e.getMessage() );
        }
    }

    private static Vector2 tmpV = new Vector2();

    protected Vector2 readAnchorPointFromTextFields_viewCoord( JTextField tfX, JTextField tfY ) {
        try {
            float x = tfX.getText().isEmpty() ? 0 : Float.valueOf( tfX.getText() );
            float y = tfY.getText().isEmpty() ? 0 : Float.valueOf( tfY.getText() );
            tmpV.set(x, y);
            if ( !useViewCoordinates )
                PhysWorld.get().toView( tmpV );
        } catch ( NumberFormatException e ) {
            Gdx.app.error("JointEditorFrom.getAnchorPoint", "WARNING: " + e.getMessage() );
            return null;
        }
        return tmpV;
    }


    protected void setAnchorPointA() {
        Vector2 v = readAnchorPointFromTextFields_viewCoord( tfAnchAX, tfAnchAY );
        if ( v == null )
            return;
        jeController.setAnchorControlPoint(JointEditorController.AnchorControlPoint.AcpType.typeA, v);
    }

    protected void setAnchorPointB() {
        Vector2 v = readAnchorPointFromTextFields_viewCoord( tfAnchBX, tfAnchBY );
        if ( v == null )
            return;
        jeController.setAnchorControlPoint(JointEditorController.AnchorControlPoint.AcpType.typeB, v);
    }

    protected void setAxisPoint() {
        Vector2 v = readAnchorPointFromTextFields_viewCoord( tfAxisX, tfAxisY );
        if ( v == null )
            return;
        jeController.setAnchorControlPoint(JointEditorController.AnchorControlPoint.AcpType.typeAxis, v);
    }

    protected void setGroundAnchPointA() {
        Vector2 v = readAnchorPointFromTextFields_viewCoord( tfGrndAX, tfGrndAY );
        if ( v == null )
            return;
        jeController.setAnchorControlPoint(JointEditorController.AnchorControlPoint.AcpType.typeGrndA, v);
    }

    protected void setGroundAnchPointB() {
        Vector2 v = readAnchorPointFromTextFields_viewCoord( tfGrndBX, tfGrndBY );
        if ( v == null )
            return;
        jeController.setAnchorControlPoint(JointEditorController.AnchorControlPoint.AcpType.typeGrndB, v);
    }

    protected void changeCoordinatesMode( boolean useViewCoord ) {
        this.useViewCoordinates = useViewCoord;
        transformCoordinates(tfAnchAX, tfAnchAY);
        transformCoordinates(tfAnchBX, tfAnchBY);
        transformCoordinates(tfAxisX, tfAxisY);
        transformCoordinates(tfGrndAX, tfGrndAY);
        transformCoordinates(tfGrndBX, tfGrndBY);
    }

    protected void updateAnchorPointsGui( JointEditorController.AnchorControlPoint acp ) {
        float x = acp.getX();
        float y = acp.getY();

        if ( !useViewCoordinates ) {
            x = PhysWorld.get().toPhys( x );
            y = PhysWorld.get().toPhys(y);
        }

        switch ( acp.type ) {
            case typeA:
                tfAnchAX.setText( String.valueOf( x ));
                tfAnchAY.setText( String.valueOf( y ));
                break;
            case typeB:
                tfAnchBX.setText( String.valueOf( x ));
                tfAnchBY.setText( String.valueOf( y ));
                break;
            case typeAxis:
                tfAxisX.setText(String.valueOf(x));
                tfAxisY.setText( String.valueOf( y ));
                break;
            case typeGrndA:
                tfGrndAX.setText( String.valueOf( x ));
                tfGrndAY.setText( String.valueOf( y ));
                break;
            case typeGrndB:
                tfGrndBX.setText( String.valueOf( x ));
                tfGrndBY.setText( String.valueOf( y ));
                break;
        }
    }

    public JointItem getJointItem() {
        return jointItem;
    }

    public void setJointItem(JointItem jointItem) {

        comboBodyItemA.removeAllItems();
        comboBodyItemB.removeAllItems();

        this.jointItem = jointItem;
        PhysModel model = jointItem.getBiScSet().getModel();
        BiScSet biScSet = model.getScBodyItems().getCurrentSet();
        if ( biScSet == null )
            return;

        comboBodyItemA.addItem( null );
        comboBodyItemB.addItem( null );

        for (BodyItem bi : biScSet.getBodyItems() ) {
            comboBodyItemA.addItem( bi);
            comboBodyItemB.addItem( bi );
        }

        if ( jointItem.getJoint() != null ) {
            comboBodyItemA.setSelectedItem( jointItem.getBodyItemA() );
            comboBodyItemB.setSelectedItem( jointItem.getBodyItemB() );
            chbCollideConnected.setSelected(jointItem.getJoint().getCollideConnected());
        }
        
        setupTPaneJointOptions();

        Gdx.app.postRunnable( new Runnable() {
            @Override
            public void run() {
                updateAnchorPointsGui( jeController.getCpAnchorA() );
                updateAnchorPointsGui( jeController.getCpAnchorB() );
                updateAnchorPointsGui( jeController.getCpAxis() );
                updateAnchorPointsGui( jeController.getCpGAnchorA() );
                updateAnchorPointsGui( jeController.getCpGAnchorB() );
            }
        });
    }

    protected void setupTPaneJointOptions() {
        tpaneJointOptions.removeAll();
        panelAnchACtrl.setVisible(true);
        panelAnchBCtrl.setVisible( false );
        panelAxisCtrl.setVisible( false );
        panelGrndACtrl.setVisible( false );
        panelGrndBCtrl.setVisible( false );

        tpaneJointOptions.addTab( "Anchor Points ", panelAnchorPoints );

        switch ( JointItemFactory.getJointType( jointItem ) ) {
            case Unknown:
                break;
            case RevoluteJoint:
                break;
            case PrismaticJoint:
                panelAxisCtrl.setVisible( true );
                break;
            case DistanceJoint:
                panelAnchBCtrl.setVisible( true );
                break;
            case PulleyJoint:
                panelAnchBCtrl.setVisible( true );
                panelGrndACtrl.setVisible( true );
                panelGrndBCtrl.setVisible( true );
                setupPulleyJointOptions();
                break;
            case MouseJoint:
                break;
            case GearJoint:
                panelAnchACtrl.setVisible( false );
                setupGJointOptions();
                break;
            case WheelJoint:
                panelAxisCtrl.setVisible( true );
                break;
            case WeldJoint:
                break;
            case FrictionJoint:
                break;
            case RopeJoint:
                panelAnchBCtrl.setVisible( true );
                break;
            case MotorJoint:
                break;
        }

    }

    protected void setupGJointOptions() {
        tpaneJointOptions.addTab("Gear Joint Options ", panelGearJointOptions );
        comboJointItemA.removeAllItems();
        comboJointItemB.removeAllItems();
        comboJointItemA.addItem( null );
        comboJointItemB.addItem( null );
        BiScSet bset = jointItem.getBiScSet();
        for ( JointItem ji : bset.getJointItems() ) {
            if ( ji.getJoint() == null )
                continue;
            if ( ji.getJoint().getType() == JointDef.JointType.RevoluteJoint ) {
                comboJointItemA.addItem(ji);
                comboJointItemB.addItem(ji);
                continue;
            }

            if ( ji.getJoint().getType() == JointDef.JointType.PrismaticJoint )
                comboJointItemB.addItem( ji );
        }
        if ( jointItem.getJoint() != null ) {
            GearJoint gearJoint = (GearJoint) jointItem.getJoint();
            JointItem ji = bset.findJointItem( gearJoint.getJoint1() );
            comboJointItemA.setSelectedItem( ji );
            ji = bset.findJointItem( gearJoint.getJoint2() );
            comboJointItemB.setSelectedItem( ji );
            tfGearJointRatio.setText(String.valueOf(gearJoint.getRatio()));
        } else {
            tfGearJointRatio.setText( " 1.0 ");
        }
    }

    protected void setupPulleyJointOptions() {
        tpaneJointOptions.addTab("Pulley Joint Options", panelPulleyJointOptions );
        if ( jointItem.getJoint() != null ) {
            PulleyJoint pulleyJoint = (PulleyJoint) jointItem.getJoint();
            tfPulleyJointRatio.setText(String.valueOf(pulleyJoint.getRatio()));
        } else {
            tfPulleyJointRatio.setText( " 1.0 ");
        }
    }

    public void setBodyItemSelectionMode( boolean isA, boolean select ) {
        if ( select )
            jeController.setBodyItemSelectionEnabled(select, isA);
    }


    protected void guiToJiDescription( JointItemDescription jiDesc ) {
        jiDesc.setCollideConnected( chbCollideConnected.isSelected() );
        switch ( jiDesc.getType() ) {
            case Unknown:
                break;
            case RevoluteJoint:
                break;
            case PrismaticJoint:
                break;
            case DistanceJoint:
                break;
            case PulleyJoint:
                guiToPulleyJiDescription( jiDesc );
                break;
            case MouseJoint:
                break;
            case GearJoint:
                guiToGearJiDescription( jiDesc );
                break;
            case WheelJoint:
                break;
            case WeldJoint:
                break;
            case FrictionJoint:
                break;
            case RopeJoint:
                break;
            case MotorJoint:
                break;
        }
    }

    protected void guiToGearJiDescription( JointItemDescription jiDesc ) {
        if ( comboJointItemA.getSelectedItem() == null )
            return;
        if ( comboJointItemB.getSelectedItem() == null )
            return;

        JointItem ji = (JointItem) comboJointItemA.getSelectedItem();
        jiDesc.setJointAId( ji.getId() );
        ji = (JointItem) comboJointItemB.getSelectedItem();
        jiDesc.setJointBId( ji.getId() );
        try {
            float ratio = Float.valueOf(tfGearJointRatio.getText());
            jiDesc.setRatio( ratio );
        } catch ( NumberFormatException e ) {
            Gdx.app.error("JointEditorFrom.guiToGearJiDescription", "WARNING: " + e.getMessage());
            jiDesc.setRatio( 1.0f );
        }
    }

    protected void guiToPulleyJiDescription( JointItemDescription jiDesc ) {
        try {
            float ratio = Float.valueOf(tfPulleyJointRatio.getText());
            jiDesc.setRatio( ratio );
        } catch ( NumberFormatException e ) {
            Gdx.app.error("JointEditorFrom.guiToPulleyJiDescription", "WARNING: " + e.getMessage());
            jiDesc.setRatio( 1.0f );
        }
    }

    public JointItemDescription getJointItemDescription() {
        JointItemDescription jiDesc = jeController.getJiDesc();

        guiToJiDescription( jiDesc );

        return jiDesc;
    }
}
