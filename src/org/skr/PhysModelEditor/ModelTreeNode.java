package org.skr.PhysModelEditor;

import org.skr.gdx.physmodel.BodyItem;
import org.skr.gdx.physmodel.FixtureSet;
import org.skr.gdx.physmodel.JointItem;
import org.skr.gdx.physmodel.PhysModel;
import org.skr.gdx.physmodel.animatedactorgroup.AnimatedActorGroup;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by rat on 25.08.14.
 */
public class ModelTreeNode extends DefaultMutableTreeNode {
    public enum Type {
        ROOT, AAG, BODY_ITEM, BODIES_GROUP, FIXTURE_SET, JOINT_ITEM, JOINTS_GROUP;
    }

    Type type;

    public ModelTreeNode(Type type, Object userObject) {
        super(userObject);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {

        Object object = getUserObject();

        switch (this.type) {
            case ROOT:
                if ( object != null ) {
                    PhysModel model = (PhysModel) object;
                    return "Model: " + model.getName();
                } else {
                    return "Model: ";
                }
            case AAG:
                if ( object != null ) {
                    AnimatedActorGroup ag = (AnimatedActorGroup) object;
                    return "Actor: " + ag.getName();
                }
            case BODY_ITEM:
                if ( object != null ) {
                    BodyItem bi = ( BodyItem ) object;
                    return "Body: " + bi.getName();
                }
            case BODIES_GROUP:
                return " : BODIES";
            case FIXTURE_SET:
                FixtureSet fs = ( FixtureSet ) object;
                return "FixtureSet: " + fs.getName();
            case JOINT_ITEM:
                JointItem ji = (JointItem) object;
                return "Joint: " + ji.getName();
            case JOINTS_GROUP:
                return " : JOINTS";
        }

        return "";
    }
}
