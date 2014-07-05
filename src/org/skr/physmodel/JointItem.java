package org.skr.physmodel;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import org.skr.PhysModelEditor.PhysWorld;
import org.skr.physmodel.animatedactorgroup.AnimatedActorGroup;

/**
 * Created by rat on 05.07.14.
 */
public class JointItem extends PhysItem {

    private static int g_id = -1;
    int id = -1;

    Joint joint;

    public JointItem( int id) {
        if ( id < 0 ) {
            this.id = ++g_id;
        } else {
            this.id = id;
            if ( id > g_id )
                g_id = this.id;
        }
    }

    public int getId() {
        return id;
    }

    public Joint getJoint() {
        return joint;
    }

    public void setJoint(Joint joint) {
        this.joint = joint;
    }

    // ====================== create Joints ==================================

    public static JointItem createFromDescription( JointItemDescription desc, PhysModel model ) {
        JointItem ji = new JointItem( desc.getId() );

        if ( desc.getAagDescription() != null ) {
            ji.setAagBackground( new AnimatedActorGroup( desc.getAagDescription() ) );
        }

        JointDef jd = createJointDef( desc, model );

        if ( jd == null )
            return null;

        Joint joint = PhysWorld.getWorld().createJoint( jd );

        ji.setJoint( joint );

        return ji;
    }

    public static JointDef createJointDef( JointItemDescription desc, PhysModel model ) {


        switch ( desc.getType() ) {

            case Unknown:
                return null;
            case RevoluteJoint:
                break;
            case PrismaticJoint:
                break;
            case DistanceJoint:
                return createDistanceJointDef( desc, model);
            case PulleyJoint:
                break;
            case MouseJoint:
                break;
            case GearJoint:
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

        return null;
    }

    public static JointDef createDistanceJointDef( JointItemDescription desc, PhysModel model )  {
        DistanceJointDef jd = new DistanceJointDef();

        BodyItem bi = model.findBodyItem( desc.getBodyAId() );

        if ( bi == null )
            return null;
        Body bodyA = bi.getBody();

        bi = model.findBodyItem( desc.getBodyBId() );

        if ( bi == null )
            return null;

        Body bodyB = bi.getBody();

        jd.initialize( bodyA, bodyB, desc.getAnchorA(), desc.getAnchorB() );
        jd.dampingRatio = desc.getDumpingRatio();
        jd.frequencyHz = desc.getFrequencyHz();

        return jd;
    }

    // ====================== create description ==================================


    public static JointItemDescription createJointItemDescription ( JointItem ji, PhysModel model ) {
        JointItemDescription jd = new JointItemDescription();
        jd.setId( ji.getId() );

        if ( ji.getJoint() == null )
            return jd;
        fillUpJointItemDescription(jd, ji.getJoint(), model);

        if ( ji.getAagBackground() != null ) {
            jd.setAagDescription( ji.getAagBackground().getDescription() );
        }

        return jd;
    }

    public static void fillUpJointItemDescription(JointItemDescription desc, Joint joint, PhysModel model) {
        switch ( joint.getType() ) {

            case Unknown:
                break;
            case RevoluteJoint:
                break;
            case PrismaticJoint:
                break;
            case DistanceJoint:
                fillDistanceJointItemDescription( desc, (DistanceJoint) joint, model );
                break;
            case PulleyJoint:
                break;
            case MouseJoint:
                break;
            case GearJoint:
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

    public static void fillDistanceJointItemDescription( JointItemDescription desc,
                                                         DistanceJoint joint, PhysModel model ) {

        BodyItem bi = model.findBodyItem( joint.getBodyA() );
        if ( bi == null)
            return;

        int bodyAId = bi.getId();

        bi = model.findBodyItem( joint.getBodyB() );
        if ( bi == null )
            return;

        int bodyBId = bi.getId();

        desc.setBodyAId( bodyAId );
        desc.setBodyBId( bodyBId );
        desc.setDumpingRatio( joint.getDampingRatio() );
        desc.setFrequencyHz( joint.getFrequency() );
    }
}
