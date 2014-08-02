package org.skr.gdx.physmodel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.World;
import org.skr.gdx.SkrGdxApplication;
import org.skr.gdx.physmodel.animatedactorgroup.AnimatedActorGroup;
import org.skr.gdx.physmodel.jointitems.*;

/**
 * Created by rat on 06.07.14.
 */
public class JointItemFactory {


    public static JointItem createFromDescription( JointItemDescription desc, PhysModel model, World world) {

        JointItem ji = create( desc.getType(),
                desc.getId(), desc.getName(), model );

        if ( ji == null) {
            return null;
        }

        if ( desc.getAagDescription() != null ) {
            ji.setAagBackground( new AnimatedActorGroup( desc.getAagDescription() , SkrGdxApplication.get().getAtlas() ) );
        }

        JointDef jd = ji.createJointDef(desc);


        if ( jd == null ) {
            Gdx.app.log("JointItemFactory.createFromDescription",
                    "Unable to create a JointDef: " + desc.getName() );
            return null;
        }

        jd.collideConnected = desc.isCollideConnected();

        Joint joint = null;
        try {
            joint = world.createJoint(jd);
        } catch (NullPointerException e ) {
            joint = null;
        }

        if ( joint == null ) {
            Gdx.app.log("JointItemFactory.createFromDescription",
                    "Unable to create a joint: " + desc.getName() );
            return null;
        }
        ji.setJoint( joint );
        joint.setUserData( ji );
        return ji;
    }

    public static JointItem create(JointDef.JointType type,  int id, String name, PhysModel model ) {

        JointItem jointItem = null;

        switch ( type ) {
            case Unknown:
                break;
            case RevoluteJoint:
                jointItem = new RevoluteJointItem( id, model );
                break;
            case PrismaticJoint:
                jointItem = new PrismaticJointItem( id, model);
                break;
            case DistanceJoint:
                jointItem = new DistanceJointItem( id, model);
                break;
            case PulleyJoint:
                jointItem = new PulleyJointItem( id, model );
                break;
            case MouseJoint:
                break;
            case GearJoint:
                jointItem = new GearJointItem(id, model );
                break;
            case WheelJoint:
                jointItem = new WheelJointItem( id, model );
                break;
            case WeldJoint:
                jointItem = new WeldJointItem( id, model );
                break;
            case FrictionJoint:
                jointItem = new FrictionJointItem( id, model );
                break;
            case RopeJoint:
                jointItem = new RopeJointItem( id, model );
                break;
            case MotorJoint:
                jointItem = new MotorJointItem( id, model );
                break;
        }

        if ( jointItem == null )
            return null;

        jointItem.setName( name );
        return jointItem;
    }
}
