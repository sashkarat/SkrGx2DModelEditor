package org.skr.gdx.physmodel.jointitems;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJoint;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import org.skr.gdx.physmodel.BodyItem;
import org.skr.gdx.physmodel.JointItem;
import org.skr.gdx.physmodel.JointItemDescription;
import org.skr.gdx.physmodel.PhysModel;

/**
 * Created by rat on 20.07.14.
 */
public class WeldJointItem extends JointItem {

    WeldJoint joint;

    public WeldJointItem(int id, PhysModel model) {
        super(id, model);
    }

    @Override
    protected void onJointSet() {
        joint = (WeldJoint) getJoint();
    }

    @Override
    protected JointDef createJointDef(JointItemDescription desc) {

        WeldJointDef jd = new WeldJointDef();


        BodyItem bi = getModel().findBodyItem(desc.getBodyAId());

        if ( bi == null )
            return null;

        Body bodyA = bi.getBody();

        bi = getModel().findBodyItem(desc.getBodyBId());

        if ( bi == null )
            return null;

        Body bodyB = bi.getBody();

        jd.initialize(bodyA, bodyB, desc.getAnchorA() );

        return jd;
    }

    @Override
    public void fillUpJointItemDescription(JointItemDescription desc) {
        desc.setAnchorA( joint.getAnchorA() );
    }


    @Override
    public float getReferenceAngle() {
        return joint.getReferenceAngle();
    }
}
