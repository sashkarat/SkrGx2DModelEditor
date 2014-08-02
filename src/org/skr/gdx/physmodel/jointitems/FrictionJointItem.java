package org.skr.gdx.physmodel.jointitems;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.joints.FrictionJoint;
import com.badlogic.gdx.physics.box2d.joints.FrictionJointDef;
import org.skr.gdx.physmodel.BodyItem;
import org.skr.gdx.physmodel.JointItem;
import org.skr.gdx.physmodel.JointItemDescription;
import org.skr.gdx.physmodel.PhysModel;

/**
 * Created by rat on 19.07.14.
 */
public class FrictionJointItem extends JointItem  {
    FrictionJoint joint;

    public FrictionJointItem(int id, PhysModel model) {
        super(id, model);
    }

    @Override
    protected void onJointSet() {
        joint = (FrictionJoint) getJoint();
    }

    @Override
    protected JointDef createJointDef(JointItemDescription desc) {
        FrictionJointDef jd = new FrictionJointDef();

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
        desc.setMaxForce( joint.getMaxForce() );
        desc.setMaxTorque( joint.getMaxTorque() );
    }

    @Override
    public float getMaxForce() {
        return joint.getMaxForce();
    }

    @Override
    public void setMaxForce(float maxForce) {
        joint.setMaxForce( maxForce );
    }

    @Override
    public float getMaxTorque() {
        return joint.getMaxTorque();
    }

    @Override
    public void setMaxTorque(float maxTorque) {
        joint.setMaxTorque( maxTorque );
    }
}
