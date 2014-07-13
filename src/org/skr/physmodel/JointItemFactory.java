package org.skr.physmodel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;
import org.skr.PhysModelEditor.PhysWorld;
import org.skr.physmodel.animatedactorgroup.AnimatedActorGroup;
import org.skr.physmodel.jointitems.DistanceJointItem;

/**
 * Created by rat on 06.07.14.
 */
public class JointItemFactory {


    public static JointItem createFromDescription( JointItemDescription desc, PhysModel model ) {

        JointItem ji = create( desc.getType(),
                desc.getId(), desc.getName(), model );

        if ( ji == null) {
            return null;
        }

        if ( desc.getAagDescription() != null ) {
            ji.setAagBackground( new AnimatedActorGroup( desc.getAagDescription() ) );
        }

        JointDef jd = ji.createJointDef(desc);

        if ( jd == null ) {
//            Gdx.app.log("JointItemFactory.createFromDescription",
//                    "Unable to create a JointDef: " + desc.getName() );
            return null;
        }

        Joint joint = PhysWorld.getWorld().createJoint( jd );
        if ( joint == null ) {
//            Gdx.app.log("JointItemFactory.createFromDescription",
//                    "Unable to create a joint: " + desc.getName() );
            return null;
        }
        ji.setJoint( joint );
        return ji;
    }

    public static JointItem create(JointDef.JointType type,  int id, String name, PhysModel model ) {

        JointItem jointItem = null;

        switch ( type ) {
            case Unknown:
                break;
            case RevoluteJoint:
                break;
            case PrismaticJoint:
                break;
            case DistanceJoint:
                jointItem = new DistanceJointItem( id, model);
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

        jointItem.setName( name );
        return jointItem;
    }
}
