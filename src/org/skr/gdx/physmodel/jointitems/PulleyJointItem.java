package org.skr.gdx.physmodel.jointitems;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.joints.PulleyJoint;
import com.badlogic.gdx.physics.box2d.joints.PulleyJointDef;
import org.skr.gdx.physmodel.BodyItem;
import org.skr.gdx.physmodel.JointItem;
import org.skr.gdx.physmodel.JointItemDescription;
import org.skr.gdx.physmodel.PhysModel;

/**
 * Created by rat on 13.07.14.
 */
public class PulleyJointItem extends JointItem {

    PulleyJoint joint;

    public PulleyJointItem(int id, PhysModel model) {
        super(id, model);
    }

    @Override
    protected void onJointSet() {
        joint = (PulleyJoint) getJoint();
    }

    @Override
    protected JointDef createJointDef(JointItemDescription desc) {

        PulleyJointDef jd = new PulleyJointDef();

        BodyItem bi = getModel().findBodyItem(desc.getBodyAId());

        if ( bi == null )
            return null;

        Body bodyA = bi.getBody();

        bi = getModel().findBodyItem(desc.getBodyBId());

        if ( bi == null )
            return null;

        Body bodyB = bi.getBody();

        jd.initialize(bodyA, bodyB, desc.getGroundAnchorA(), desc.getGroundAnchorB(),
                desc.getAnchorA(), desc.getAnchorB(), desc.getRatio() );

        return jd;
    }

    @Override
    public void fillUpJointItemDescription(JointItemDescription desc) {
        desc.setGroundAnchorA( joint.getGroundAnchorA() );
        desc.setGroundAnchorB( joint.getGroundAnchorB() );
        desc.setRatio( joint.getRatio() );
    }

    @Override
    public float getRatio() {
        return joint.getRatio();
    }

    @Override
    public Vector2 getGroundAnchorA() {
        return joint.getGroundAnchorA();
    }

    @Override
    public Vector2 getGroundAnchorB() {
        return joint.getGroundAnchorB();
    }
}
