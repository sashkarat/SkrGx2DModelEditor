package org.skr.physmodel.jointitems;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import org.skr.physmodel.BodyItem;
import org.skr.physmodel.JointItem;
import org.skr.physmodel.JointItemDescription;
import org.skr.physmodel.PhysModel;

/**
 * Created by rat on 06.07.14.
 */
public class DistanceJointItem extends JointItem {

    private DistanceJoint joint = null;

    public DistanceJointItem(int id, PhysModel model) {
        super(id, model);
    }

    @Override
    protected JointDef createJointDef(JointItemDescription desc) {
        DistanceJointDef jd = new DistanceJointDef();

        BodyItem bi = getModel().findBodyItem(desc.getBodyAId());

        if ( bi == null )
            return null;
        Body bodyA = bi.getBody();

        bi = getModel().findBodyItem(desc.getBodyBId());

        if ( bi == null )
            return null;

        Body bodyB = bi.getBody();
        jd.initialize( bodyA, bodyB, desc.getAnchorA(), desc.getAnchorB() );
        jd.collideConnected = desc.isCollideConnected();
        jd.dampingRatio = desc.getDampingRatio();
        jd.frequencyHz = desc.getFrequencyHz();
        jd.length = desc.getLength();

        return jd;
    }

    @Override
    public void fillUpJointItemDescription(JointItemDescription desc) {
        BodyItem bi = getModel().findBodyItem( joint.getBodyA() );
        if ( bi == null)
            return;

        int bodyAId = bi.getId();

        bi = getModel().findBodyItem( joint.getBodyB() );
        if ( bi == null )
            return;

        int bodyBId = bi.getId();
        desc.setCollideConnected( joint.getCollideConnected() );
        desc.setBodyAId( bodyAId );
        desc.setBodyBId( bodyBId );
        desc.setDampingRatio(joint.getDampingRatio());
        desc.setFrequencyHz( joint.getFrequency() );
        desc.setLength( joint.getLength() );

    }

    @Override
    public float getLength() {
        return joint.getLength();
    }

    @Override
    public void setLength(float length) {
        joint.setLength( length );
    }

    @Override
    public float getFrequencyHz() {
        return joint.getFrequency();
    }

    @Override
    public void setFrequencyHz(float frequencyHz) {
        joint.setFrequency( frequencyHz );
    }

    @Override
    public float getDampingRatio() {
        return joint.getDampingRatio();
    }

    @Override
    public void setDampingRatio(float dumpingRatio) {
        joint.setDampingRatio( dumpingRatio );
    }

    @Override
    protected void onJointSet() {
        joint = ( DistanceJoint ) getJoint();
    }
}
