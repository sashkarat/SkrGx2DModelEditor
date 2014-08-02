package org.skr.gdx.physmodel.jointitems;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.joints.WheelJoint;
import com.badlogic.gdx.physics.box2d.joints.WheelJointDef;
import org.skr.gdx.physmodel.BodyItem;
import org.skr.gdx.physmodel.JointItem;
import org.skr.gdx.physmodel.JointItemDescription;
import org.skr.gdx.physmodel.PhysModel;

/**
 * Created by rat on 19.07.14.
 */
public class WheelJointItem extends JointItem {

    WheelJoint joint;

    public WheelJointItem(int id, PhysModel model) {
        super(id, model);
    }

    @Override
    protected void onJointSet() {
        joint = (WheelJoint) getJoint();
    }

    @Override
    protected JointDef createJointDef(JointItemDescription desc) {

        WheelJointDef jd = new WheelJointDef();

        BodyItem bi = getModel().findBodyItem(desc.getBodyAId());

        if ( bi == null )
            return null;

        Body bodyA = bi.getBody();

        bi = getModel().findBodyItem(desc.getBodyBId());

        if ( bi == null )
            return null;

        Body bodyB = bi.getBody();

        jd.initialize( bodyA, bodyB, desc.getAnchorA(), desc.getAxis());
        jd.dampingRatio = desc.getDampingRatio();
        jd.enableMotor = desc.isEnableMotor();
        jd.frequencyHz = desc.getFrequencyHz();
        jd.maxMotorTorque = desc.getMaxMotorTorque();
        jd.motorSpeed = desc.getMotorSpeed();

        return jd;
    }

    @Override
    public void fillUpJointItemDescription(JointItemDescription desc) {

        Body b = joint.getBodyA();
        desc.setAxis( b.getWorldVector(joint.getLocalAxisA()) );
        desc.setAnchorA( joint.getAnchorA() );
        desc.setDampingRatio( joint.getSpringDampingRatio() );
        desc.setEnableMotor( joint.isMotorEnabled() );
        desc.setFrequencyHz( joint.getSpringFrequencyHz() );
        desc.setMaxMotorTorque(joint.getMaxMotorTorque());
        desc.setMotorSpeed( joint.getMotorSpeed() );
    }

    @Override
    public Vector2 getAxis() {
        Body b = joint.getBodyA();
        return b.getWorldVector(joint.getLocalAxisA());
    }

    @Override
    public void setDampingRatio(float dampingRatio) {
        joint.setSpringDampingRatio( dampingRatio );
    }

    @Override
    public float getDampingRatio() {
        return joint.getSpringDampingRatio();
    }

    @Override
    public void setMotorSpeed(float motorSpeed) {
        joint.setMotorSpeed( motorSpeed );
    }

    @Override
    public float getMotorSpeed() {
        return joint.getMotorSpeed();
    }

    @Override
    public void setEnableMotor(boolean enableMotor) {
        joint.enableMotor( enableMotor );
    }

    @Override
    public boolean isEnableMotor() {
        return joint.isMotorEnabled();
    }

    @Override
    public void setFrequencyHz(float frequencyHz) {
        joint.setSpringFrequencyHz( frequencyHz );
    }

    @Override
    public float getFrequencyHz() {
        return joint.getSpringFrequencyHz();
    }

    @Override
    public float getMaxMotorTorque() {
        return joint.getMaxMotorTorque();
    }

    @Override
    public void setMaxMotorTorque(float maxMotorTorque) {
        joint.setMaxMotorTorque( maxMotorTorque );
    }
}
