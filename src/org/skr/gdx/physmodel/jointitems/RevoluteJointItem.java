package org.skr.gdx.physmodel.jointitems;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import org.skr.gdx.physmodel.BodyItem;
import org.skr.gdx.physmodel.JointItem;
import org.skr.gdx.physmodel.JointItemDescription;
import org.skr.gdx.physmodel.PhysModel;

/**
 * Created by rat on 13.07.14.
 */
public class RevoluteJointItem extends JointItem {

    RevoluteJoint joint;

    public RevoluteJointItem(int id, PhysModel model) {
        super(id, model);
    }

    @Override
    protected void onJointSet() {
        joint = (RevoluteJoint) getJoint();
    }

    @Override
    protected JointDef createJointDef(JointItemDescription desc) {

        RevoluteJointDef jd = new RevoluteJointDef();

        BodyItem bi = getModel().findBodyItem(desc.getBodyAId());

        if ( bi == null )
            return null;

        Body bodyA = bi.getBody();

        bi = getModel().findBodyItem(desc.getBodyBId());

        if ( bi == null )
            return null;

        Body bodyB = bi.getBody();

        Vector2 anchor = desc.getAnchorA();

        jd.initialize( bodyA, bodyB, anchor );
        jd.enableLimit = desc.isEnableLimit();
        jd.enableMotor = desc.isEnableMotor();
        jd.lowerAngle = desc.getLowerAngle();
        jd.upperAngle = desc.getUpperAngle();
        jd.maxMotorTorque = desc.getMaxMotorTorque();
        jd.motorSpeed = desc.getMotorSpeed();
        return jd;
    }

    @Override
    public void fillUpJointItemDescription(JointItemDescription desc) {
        desc.setEnableLimit( joint.isLimitEnabled() );
        desc.setEnableMotor( joint.isMotorEnabled() );
        desc.setLowerAngle( joint.getLowerLimit() );
        desc.setUpperAngle( joint.getUpperLimit() );
        desc.setMaxMotorTorque( joint.getMaxMotorTorque() );
        desc.setMotorSpeed( joint.getMotorSpeed() );
    }

    @Override
    public boolean isEnableLimit() {
        return joint.isLimitEnabled();
    }

    @Override
    public void setEnableLimit(boolean enableLimit) {
        joint.enableLimit( enableLimit );
    }

    @Override
    public boolean isEnableMotor() {
        return joint.isMotorEnabled();
    }

    @Override
    public void setEnableMotor(boolean enableMotor) {
       joint.enableMotor( enableMotor );
    }

    @Override
    public float getReferenceAngle() {
        return joint.getReferenceAngle();
    }

    @Override
    public float getMaxTorque() {
        return joint.getMaxMotorTorque();
    }

    @Override
    public void setMaxTorque(float maxTorque) {
        joint.setMaxMotorTorque( maxTorque );
    }

    @Override
    public float getLowerAngle() {
        return joint.getLowerLimit();
    }

    @Override
    public void setLowerAngle(float lowerAngle) {
        if ( lowerAngle > joint.getUpperLimit() )
            return;
        joint.setLimits( lowerAngle, joint.getUpperLimit() );
    }

    @Override
    public float getUpperAngle() {
        return joint.getUpperLimit();
    }

    @Override
    public void setUpperAngle(float upperAngle) {
        if ( upperAngle <= joint.getLowerLimit() )
            return;
        joint.setLimits( joint.getLowerLimit(), upperAngle );
    }

    @Override
    public float getMotorSpeed() {
        return joint.getMotorSpeed();
    }

    @Override
    public void setMotorSpeed(float motorSpeed) {
        joint.setMotorSpeed( motorSpeed );
    }
}
