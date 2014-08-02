package org.skr.gdx.physmodel.jointitems;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.joints.MotorJoint;
import com.badlogic.gdx.physics.box2d.joints.MotorJointDef;
import org.skr.gdx.physmodel.BodyItem;
import org.skr.gdx.physmodel.JointItem;
import org.skr.gdx.physmodel.JointItemDescription;
import org.skr.gdx.physmodel.PhysModel;

/**
 * Created by rat on 19.07.14.
 */
public class MotorJointItem extends JointItem {

    MotorJoint joint;

    public MotorJointItem(int id, PhysModel model) {
        super(id, model);
    }

    @Override
    protected void onJointSet() {
        joint = (MotorJoint) getJoint();
    }

    @Override
    protected JointDef createJointDef(JointItemDescription desc) {
        MotorJointDef jd = new MotorJointDef();
        BodyItem bi = getModel().findBodyItem(desc.getBodyAId());

        if ( bi == null )
            return null;
        Body bodyA = bi.getBody();

        bi = getModel().findBodyItem(desc.getBodyBId());

        if ( bi == null )
            return null;

        Body bodyB = bi.getBody();

        jd.initialize( bodyA, bodyB );

        jd.maxForce = desc.getMaxForce();
        jd.maxTorque = desc.getMaxTorque();
        jd.correctionFactor = desc.getCorrectionFactor();

        return jd;
    }

    @Override
    public void fillUpJointItemDescription(JointItemDescription desc) {
        desc.setLinearOffset( joint.getLinearOffset() );
        desc.setAngularOffset( joint.getAngularOffset() );
        desc.setMaxForce( joint.getMaxForce() );
        desc.setMaxTorque( joint.getMaxTorque() );
        desc.setCorrectionFactor( joint.getCorrectionFactor() );
    }

    @Override
    public void setLinearOffset(Vector2 linearOffset) {
        joint.setLinearOffset( linearOffset );
    }

    @Override
    public Vector2 getLinearOffset() {
        return joint.getLinearOffset();
    }

    @Override
    public float getAngularOffset() {
        return joint.getAngularOffset();
    }

    @Override
    public void setAngularOffset(float angularOffset) {
        joint.setAngularOffset( angularOffset );
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

    @Override
    public float getCorrectionFactor() {
        return joint.getCorrectionFactor();
    }

    @Override
    public void setCorrectionFactor(float correctionFactor) {
        joint.setCorrectionFactor( correctionFactor );
    }
}
