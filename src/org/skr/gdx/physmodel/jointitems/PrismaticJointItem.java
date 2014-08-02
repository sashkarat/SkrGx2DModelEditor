package org.skr.gdx.physmodel.jointitems;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import org.skr.gdx.physmodel.BodyItem;
import org.skr.gdx.physmodel.JointItem;
import org.skr.gdx.physmodel.JointItemDescription;
import org.skr.gdx.physmodel.PhysModel;

/**
 * Created by rat on 13.07.14.
 */
public class PrismaticJointItem extends JointItem {

    PrismaticJoint joint;
    Vector2 axis = new Vector2();

    public PrismaticJointItem(int id, PhysModel model) {
        super(id, model);
    }

    @Override
    protected void onJointSet() {
        joint = (PrismaticJoint) getJoint();
    }

    @Override
    protected JointDef createJointDef(JointItemDescription desc) {
        PrismaticJointDef jd = new PrismaticJointDef();

        BodyItem bi = getModel().findBodyItem(desc.getBodyAId());

        if ( bi == null )
            return null;

        Body bodyA = bi.getBody();

        bi = getModel().findBodyItem(desc.getBodyBId());

        if ( bi == null )
            return null;

        Body bodyB = bi.getBody();

        Vector2 anchor = desc.getAnchorA();
        Vector2 axis = desc.getAxis();
        jd.initialize( bodyA, bodyB, anchor, axis);

        jd.enableLimit = desc.isEnableLimit();
        jd.enableMotor = desc.isEnableMotor();
        jd.maxMotorForce = desc.getMaxMotorForce();
        jd.motorSpeed = desc.getMotorSpeed();
        jd.upperTranslation = desc.getUpperTranslation();
        jd.lowerTranslation = desc.getLowerTranslation();

        this.axis.set( desc.getAxis() );

        return jd;
    }

    @Override
    public void fillUpJointItemDescription(JointItemDescription desc) {
        desc.setAnchorA( joint.getAnchorA() );
        desc.setAxis( this.axis );
        desc.setEnableMotor( joint.isMotorEnabled() );
        desc.setEnableLimit( joint.isLimitEnabled() );
        desc.setMotorSpeed( joint.getMotorSpeed() );
        desc.setMaxMotorForce( joint.getMaxMotorForce() );
        desc.setUpperTranslation( joint.getUpperLimit() );
        desc.setLowerTranslation( joint.getLowerLimit() );
    }


    @Override
    public Vector2 getAxis() {
        return axis;
    }

    @Override
    public void setAxis(Vector2 axis) {
        super.setAxis(axis);
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
    public float getMaxMotorForce() {
        return joint.getMaxMotorForce();
    }

    @Override
    public void setMaxMotorForce(float maxMotorForce) {
        joint.setMaxMotorForce( maxMotorForce );
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
    public float getUpperTranslation() {
        return joint.getUpperLimit();
    }

    @Override
    public void setUpperTranslation(float upperTranslation) {
        if ( upperTranslation <= getLowerTranslation() )
            return;
        joint.setLimits( joint.getLowerLimit(), upperTranslation);
    }

    @Override
    public float getLowerTranslation() {
        return joint.getLowerLimit();
    }

    @Override
    public void setLowerTranslation(float lowerTranslation) {
        if ( lowerTranslation > getUpperTranslation() )
            return;
        joint.setLimits( lowerTranslation, joint.getUpperLimit() );
    }
}
