package org.skr.gx2d.ModelEditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.joints.GearJoint;
import com.badlogic.gdx.physics.box2d.joints.PulleyJoint;
import org.skr.gx2d.ModelEditor.gdx.SkrGx2DModelEditorGdxApp;
import org.skr.gx2d.ModelEditor.gdx.controllers.JointEditorController;
import org.skr.gx2d.common.Env;
import org.skr.gx2d.editor.Controller;
import org.skr.gx2d.model.Model;
import org.skr.gx2d.node.Node;
import org.skr.gx2d.physnodes.BodyHandler;
import org.skr.gx2d.physnodes.JointHandler;
import org.skr.gx2d.physnodes.PhysSet;
import org.skr.gx2d.physnodes.physdef.JointDefinition;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by rat on 19.10.14.
 */
public class JointEditorFrom {
    private JPanel panelRootJointEditorForm;
    private JToggleButton btnBodyHandlerA;
    private JComboBox comboBodyHandlerA;
    private JToggleButton btnBodyHandlerB;
    private JComboBox comboBodyHandlerB;
    private JCheckBox chbCollideConnected;
    private JTabbedPane tpaneJointOptions;
    private JPanel panelGearJointOptions;
    private JComboBox comboJointHandlerA;
    private JComboBox comboJointHandlerB;
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
    JointHandler jointHandler;
    boolean useViewCoordinates = true;


    public JointEditorFrom() {

        btnBodyHandlerA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setBodyItemSelectionMode(true, btnBodyHandlerA.isSelected());
            }
        });

        btnBodyHandlerB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setBodyItemSelectionMode(false, btnBodyHandlerB.isSelected());
            }
        });

        comboBodyHandlerA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jeController.setBodyHandlerA( (BodyHandler) comboBodyHandlerA.getSelectedItem() );
            }
        });

        comboBodyHandlerB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jeController.setBodyHandlerB((BodyHandler) comboBodyHandlerB.getSelectedItem());
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
        jeController = SkrGx2DModelEditorGdxApp.get().getEditorScreen().getJointEditorController();
        jeController.setBodyHandlerSelectionListener(new JointEditorController.BodyHandlerSelectionListener() {
            @Override
            public void bodyHandlerSelected(BodyHandler bodyItem, boolean isA) {
                if (isA) {
                    btnBodyHandlerA.setSelected(false);
                    comboBodyHandlerA.setSelectedItem(bodyItem);
                } else {
                    btnBodyHandlerB.setSelected(false);
                    comboBodyHandlerB.setSelectedItem(bodyItem);
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
                x = Env.get().world.toView( x );
                y = Env.get().world.toView( y );
            } else {
                x = Env.get().world.toPhys( x );
                y = Env.get().world.toPhys( y );
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
                Env.get().world.toView( tmpV );
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
            x = Env.get().world.toPhys( x );
            y = Env.get().world.toPhys(y);
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

    public JointHandler getJointHandler() {
        return jointHandler;
    }

    public void setJointHandler(JointHandler jointHandler) {

        comboBodyHandlerA.removeAllItems();
        comboBodyHandlerB.removeAllItems();

        this.jointHandler = jointHandler;

        PhysSet physSet = jointHandler.getPhysSet();
        Model model = physSet.getModel();

        comboBodyHandlerA.addItem(null);
        comboBodyHandlerB.addItem(null);

        for ( Node n : physSet.getBodyHandler() ) {
            BodyHandler bh = (BodyHandler) n;
            comboBodyHandlerA.addItem(bh);
            comboBodyHandlerB.addItem(bh);
        }

        if ( jointHandler.getJoint() != null ) {
            comboBodyHandlerA.setSelectedItem(jointHandler.getBodyHandlerA());
            comboBodyHandlerB.setSelectedItem(jointHandler.getBodyHandlerB());
            chbCollideConnected.setSelected(jointHandler.getJoint().getCollideConnected());
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

        switch ( jointHandler.getJointType() ) {
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
        comboJointHandlerA.removeAllItems();
        comboJointHandlerB.removeAllItems();
        comboJointHandlerA.addItem(null);
        comboJointHandlerB.addItem(null);

        PhysSet physSet = jointHandler.getPhysSet();

        for ( Node n : physSet.getJointHandler() ) {

            JointHandler jh = (JointHandler) n;

            if ( jh.getJoint() == null )
                continue;
            if ( jh.getJoint().getType() == JointDef.JointType.RevoluteJoint ) {
                comboJointHandlerA.addItem(jh);
                comboJointHandlerB.addItem(jh);
                continue;
            }

            if ( jh.getJoint().getType() == JointDef.JointType.PrismaticJoint )
                comboJointHandlerB.addItem( jh );
        }
        if ( jointHandler.getJoint() != null && jointHandler.getJointType() == JointDef.JointType.GearJoint ) {
            GearJoint gearJoint = (GearJoint) jointHandler.getJoint();
            JointHandler jh = jointHandler.getJointHandler( gearJoint.getJoint1() );
            comboJointHandlerA.setSelectedItem(jh);
            jh = jointHandler.getJointHandler( gearJoint.getJoint2() );
            comboJointHandlerB.setSelectedItem(jh);
            tfGearJointRatio.setText(String.valueOf(gearJoint.getRatio()));
        } else {
            tfGearJointRatio.setText( " 1.0 ");
        }
    }

    protected void setupPulleyJointOptions() {
        tpaneJointOptions.addTab("Pulley Joint Options", panelPulleyJointOptions );
        if ( jointHandler.getJoint() != null ) {
            PulleyJoint pulleyJoint = (PulleyJoint) jointHandler.getJoint();
            tfPulleyJointRatio.setText(String.valueOf(pulleyJoint.getRatio()));
        } else {
            tfPulleyJointRatio.setText( " 1.0 ");
        }
    }

    public void setBodyItemSelectionMode( boolean isA, boolean select ) {
        if ( select )
            jeController.setBodyHandlerSelectionEnabled(select, isA);
    }


    protected void guiToJhDefinition(JointDefinition jhDef) {
        jhDef.setCollideConnected(chbCollideConnected.isSelected());
        switch ( jhDef.getType() ) {
            case Unknown:
                break;
            case RevoluteJoint:
                break;
            case PrismaticJoint:
                break;
            case DistanceJoint:
                break;
            case PulleyJoint:
                guiToPulleyJhDef(jhDef);
                break;
            case MouseJoint:
                break;
            case GearJoint:
                guiToGearJhDef(jhDef);
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

    protected void guiToGearJhDef(JointDefinition jhDef) {
        if ( comboJointHandlerA.getSelectedItem() == null )
            return;
        if ( comboJointHandlerB.getSelectedItem() == null )
            return;

        JointHandler jh = (JointHandler) comboJointHandlerA.getSelectedItem();
        jhDef.setJointAId(jh.getId());
        jh = (JointHandler) comboJointHandlerB.getSelectedItem();
        jhDef.setJointBId(jh.getId());
        try {
            float ratio = Float.valueOf(tfGearJointRatio.getText());
            jhDef.setRatio(ratio);
        } catch ( NumberFormatException e ) {
            Gdx.app.error("JointEditorFrom.guiToGearJhDef", "WARNING: " + e.getMessage());
            jhDef.setRatio(1.0f);
        }
    }

    protected void guiToPulleyJhDef(JointDefinition jhDef) {
        try {
            float ratio = Float.valueOf(tfPulleyJointRatio.getText());
            jhDef.setRatio(ratio);
        } catch ( NumberFormatException e ) {
            Gdx.app.error("JointEditorFrom.guiToPulleyJhDef", "WARNING: " + e.getMessage());
            jhDef.setRatio(1.0f);
        }
    }

    public JointDefinition getJointHandlerDefinition() {
        JointDefinition jhDef = jeController.getJhDef();

        guiToJhDefinition(jhDef);

        return jhDef;
    }
}
