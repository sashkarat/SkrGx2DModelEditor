package org.skr.gdx.physmodel.jointitems;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.joints.RopeJoint;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import org.skr.gdx.physmodel.BodyItem;
import org.skr.gdx.physmodel.JointItem;
import org.skr.gdx.physmodel.JointItemDescription;
import org.skr.gdx.physmodel.PhysModel;

/**
 * Created by rat on 19.07.14.
 */
public class RopeJointItem extends JointItem {

    RopeJoint joint;

    public RopeJointItem(int id, PhysModel model) {
        super(id, model);
    }

    @Override
    protected void onJointSet() {
        joint = (RopeJoint) getJoint();
    }

    @Override
    protected JointDef createJointDef(JointItemDescription desc) {

        RopeJointDef jd = new RopeJointDef();

        BodyItem bi = getModel().findBodyItem(desc.getBodyAId());

        if ( bi == null )
            return null;
        Body bodyA = bi.getBody();

        bi = getModel().findBodyItem(desc.getBodyBId());

        if ( bi == null )
            return null;

        Body bodyB = bi.getBody();

        jd.bodyA = bodyA;
        jd.bodyB = bodyB;
        jd.localAnchorA.set( bodyA.getLocalPoint( desc.getAnchorA() ) );
        jd.localAnchorB.set( bodyB.getLocalPoint( desc.getAnchorB() ) );
        jd.maxLength = desc.getAnchorA().dst( desc.getAnchorB() );

        return jd;
    }

    @Override
    public void fillUpJointItemDescription(JointItemDescription desc) {
        desc.setAnchorA(joint.getBodyA().getWorldPoint(joint.getLocalAnchorA()));
        desc.setAnchorB(joint.getBodyB().getWorldPoint(joint.getLocalAnchorB()));
        desc.setMaxLength( joint.getMaxLength() );
    }

    @Override
    public float getMaxLength() {
        return joint.getMaxLength();
    }

    @Override
    public void setMaxLength(float maxLength) {
        //Something strange. symbol setMaxLength not found
//        joint.setMaxLength( maxLength );
    }
}
