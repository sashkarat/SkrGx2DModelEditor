package org.skr.gdx.physmodel;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;

/**
 * Created by rat on 05.07.14.
 */
public abstract class JointItem extends PhysItem {

    private static int g_id = -1;
    int id = -1;

    private Joint joint;
    private PhysModel model;

    protected JointItem( int id, PhysModel model ) {

        this.id = genNextId( id );
        this.model = model;
    }

    public final int getId() {
        return id;
    }

    public static int genNextId( int id ) {
        int res = -1;
        if ( id < 0 ) {
            res = ++g_id;
        } else {
            res = id;
            if ( id > g_id )
                g_id = id;
        }
        return res;
    }

    public final Joint getJoint() {
        return joint;
    }

    public final void setJoint(Joint joint) {
        this.joint = joint;
        onJointSet();
    }

    public PhysModel getModel() {
        return model;
    }

    public final int getBodyAId() {
        Body body = joint.getBodyA();

        BodyItem bi = model.findBodyItem( body );
        if ( bi == null )
            return -1;
        return bi.getId();
    }

    public final int getBodyBId() {
        Body body = joint.getBodyB();
        BodyItem bi = model.findBodyItem( body );
        if ( bi == null )
            return -1;
        return bi.getId();
    }

    public final boolean isCollideConnected() {
        return joint.getCollideConnected();
    }

    public final Vector2 getAnchorA() {
        return joint.getAnchorA();
    }

    public final Vector2 getAnchorB() {
        return joint.getAnchorB();
    }

    public float getLength() {
        return 0;
    }

    public void setLength(float length) {
    }

    public float getMaxForce() {
        return 0;
    }

    public void setMaxForce(float maxForce) {
    }

    public float getMaxTorque() {
        return 0;
    }

    public void setMaxTorque(float maxTorque) {

    }

    public float getRatio() {
        return 0;
    }

    public void setRatio(float ratio) {

    }

    public int getJointAId() {
        return -1;
    }

    public void setJointAId(int jointAId) {

    }

    public int getJointBId() {
        return -1;
    }

    public void setJointBId(int jointBId) {

    }

    public Vector2 getLinearOffset() {
        return null;
    }

    public void setLinearOffset(Vector2 linearOffset) {

    }

    public float getAngularOffset() {
        return 0;
    }

    public void setAngularOffset(float angularOffset) {

    }

    public float getCorrectionFactor() {
        return 0;
    }

    public void setCorrectionFactor(float correctionFactor) {

    }

    public Vector2 getTarget() {
        return null;
    }

    public void setTarget(Vector2 target) {

    }

    public float getFrequencyHz() {
        return 0;
    }

    public void setFrequencyHz(float frequencyHz) {

    }

    public float getDampingRatio() {
        return 0;
    }

    public void setDampingRatio(float dampingRatio ) {

    }

    public Vector2 getAxis() {
        return null;
    }

    public void setAxis(Vector2 axis) {

    }

    public float getReferenceAngle() {
        return 0;
    }

    public void setReferenceAngle(float referenceAngle) {

    }

    public boolean isEnableLimit() {
        return false;
    }

    public void setEnableLimit(boolean enableLimit) {

    }

    public float getLowerTranslation() {
        return 0;
    }

    public void setLowerTranslation(float lowerTranslation) {

    }

    public float getUpperTranslation() {
        return 0;
    }

    public void setUpperTranslation(float upperTranslation) {

    }

    public boolean isEnableMotor() {
        return false;
    }

    public void setEnableMotor(boolean enableMotor) {

    }

    public float getMaxMotorForce() {
        return 0;
    }

    public void setMaxMotorForce(float maxMotorForce) {

    }

    public float getMotorSpeed() {
        return 0;
    }

    public void setMotorSpeed(float motorSpeed) {

    }

    public Vector2 getGroundAnchorA() {
        return null;
    }

    public void setGroundAnchorA(Vector2 groundAnchorA) {

    }

    public Vector2 getGroundAnchorB() {
        return null;
    }

    public void setGroundAnchorB(Vector2 groundAnchorB) {

    }

    public float getLowerAngle() {
        return 0;
    }

    public void setLowerAngle(float lowerAngle) {

    }

    public float getUpperAngle() {
        return 0;
    }

    public void setUpperAngle(float upperAngle) {

    }

    public float getMaxMotorTorque() {
        return 0;
    }

    public void setMaxMotorTorque(float maxMotorTorque) {

    }

    public float getMaxLength() {
        return 0;
    }

    public void setMaxLength(float maxLength) {

    }

    protected abstract void onJointSet();

    protected abstract JointDef createJointDef( JointItemDescription desc );

    public abstract void fillUpJointItemDescription( JointItemDescription desc );


    public JointItemDescription createJointItemDescription ( ) {

        if ( getJoint() == null )
            return null;

        JointItemDescription jd = new JointItemDescription();
        jd.setId( getId() );
        jd.setName( getName() );
        jd.setAnchorA( getAnchorA() );
        jd.setAnchorB( getAnchorB() );
        jd.setCollideConnected( isCollideConnected() );
        jd.setType( joint.getType() );
        BodyItem bi = getModel().findBodyItem( joint.getBodyA() );
        if ( bi != null) {
            jd.setBodyAId( bi.getId() );
        }
        bi = getModel().findBodyItem( joint.getBodyB() );
        if ( bi != null ) {
            jd.setBodyBId( bi.getId() );
        }

        fillUpJointItemDescription( jd );

        if ( getAagBackground() != null ) {
            jd.setAagDescription( getAagBackground().getDescription() );
        }

        return jd;
    }


/*

    public static JointDef createDistanceJointDef( JointItemDescription desc, PhysModel model )  {
        DistanceJointDef jd = new DistanceJointDef();

        BodyItem bi = model.findBodyItem( desc.getBodyAId() );

        if ( bi == null )
            return null;
        Body bodyA = bi.getBody();

        bi = model.findBodyItem( desc.getBodyBId() );

        if ( bi == null )
            return null;

        Body bodyB = bi.getBody();

        jd.initialize( bodyA, bodyB, desc.getAnchorA(), desc.getAnchorB() );
        jd.dampingRatio = desc.getDampingRatio();
        jd.frequencyHz = desc.getFrequencyHz();
        jd.length = desc.getLength();

        return jd;
    }


    public static void fillDistanceJointItemDescription( JointItemDescription desc,
                                                         DistanceJoint joint, PhysModel model ) {

        BodyItem bi = model.findBodyItem( joint.getBodyA() );
        if ( bi == null)
            return;

        int bodyAId = bi.getId();

        bi = model.findBodyItem( joint.getBodyB() );
        if ( bi == null )
            return;

        int bodyBId = bi.getId();

        desc.setBodyAId( bodyAId );
        desc.setBodyBId( bodyBId );
        desc.setDampingRatio( joint.getDampingRatio() );
        desc.setFrequencyHz( joint.getFrequency() );
        desc.setLength( joint.getLength() );
    }


*/


}
