package org.skr.gx2d.ModelEditor;


import org.skr.gx2d.model.Model;
import org.skr.gx2d.physnodes.FixtureSet;
import org.skr.gx2d.sprite.Sprite;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by rat on 25.08.14.
 */
public class Gx2dJTreeNode extends DefaultMutableTreeNode {

    public enum Type {
        MODEL,
        PHYS_SET,
        SPRITE,
        BODY_HANDLER,
        FIXTURE_SET,
        JOINT_HANDLER,
    }

    Type type;

    public Gx2dJTreeNode(Type type, Object userObject) {
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

        if ( object == null )
            return this.type.name() + " with NULL object";

        switch (this.type) {
            case MODEL:
                return "Model: " + ((Model) object).getName();
            case SPRITE:
                return "Sprite: " + ((Sprite) object).getName();
            case BODY_HANDLER:
                return "BH: " + object.toString();
            case FIXTURE_SET:
                return "FS: " + ((FixtureSet) object).getName();
            case JOINT_HANDLER:
                return "Joint: " + object.toString();
        }

        return "";
    }
}
