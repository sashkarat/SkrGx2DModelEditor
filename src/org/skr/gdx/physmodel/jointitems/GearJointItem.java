package org.skr.gdx.physmodel.jointitems;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.joints.GearJoint;
import com.badlogic.gdx.physics.box2d.joints.GearJointDef;
import org.skr.gdx.physmodel.BodyItem;
import org.skr.gdx.physmodel.JointItem;
import org.skr.gdx.physmodel.JointItemDescription;
import org.skr.gdx.physmodel.PhysModel;

/**
 * Created by rat on 19.07.14.
 */
public class GearJointItem extends JointItem {
    GearJoint joint;
    public GearJointItem(int id, PhysModel model) {
        super(id, model);
    }

    @Override
    protected void onJointSet() {
        joint = (GearJoint) getJoint();
    }

    @Override
    protected JointDef createJointDef(JointItemDescription desc) {

        GearJointDef jd = new GearJointDef();


        BodyItem bi = getModel().findBodyItem(desc.getBodyAId());

        if ( bi == null )
            return null;

        Body bodyA = bi.getBody();

        bi = getModel().findBodyItem(desc.getBodyBId());

        if ( bi == null )
            return null;

        Body bodyB = bi.getBody();

        Joint jA, jB;

        JointItem ji = getModel().findJointItem( desc.getJointAId() );
        if ( ji == null )
            return null;
        jA = ji.getJoint();

        if ( jA.getType() != JointDef.JointType.RevoluteJoint )
            return null;

        ji = getModel().findJointItem( desc.getJointBId() );
        if ( ji == null )
            return null;
        jB = ji.getJoint();

        if ( !( jB.getType() == JointDef.JointType.RevoluteJoint ||
                jB.getType() == JointDef.JointType.PrismaticJoint ))
            return  null;

        jd.joint1 = jA;
        jd.joint2 = jB;
        jd.ratio = desc.getRatio();
        return jd;
    }

    @Override
    public void fillUpJointItemDescription(JointItemDescription desc) {
        desc.setRatio( joint.getRatio() );
        JointItem ji = getModel().findJointItem( joint.getJoint1() );
        desc.setJointAId( ji.getId() );
        ji = getModel().findJointItem( joint.getJoint1() );
        desc.setJointBId( ji.getId() );
    }

    @Override
    public float getRatio() {
        return joint.getRatio();
    }

    @Override
    public int getJointAId() {
        JointItem ji = getModel().findJointItem( joint.getJoint1() );
        return ji.getId();
    }

    @Override
    public int getJointBId() {
        JointItem ji = getModel().findJointItem( joint.getJoint2() );
        return ji.getId();
    }
}
