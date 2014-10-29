package org.skr.PhysModelEditor;

import org.skr.gdx.SelectableContent.ScContainer;
import org.skr.gdx.physmodel.PhysModel;
import org.skr.gdx.physmodel.animatedactorgroup.AnimatedActorGroup;
import org.skr.gdx.physmodel.bodyitem.BodyItem;
import org.skr.gdx.physmodel.bodyitem.fixtureset.FixtureSet;
import org.skr.gdx.physmodel.jointitem.JointItem;


import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by rat on 25.08.14.
 */
public class PhysModelJTreeNode extends DefaultMutableTreeNode {

    public enum Type {
        MODEL,
        BiScSET,
        AAG,
        AAG_SC,
        AAG_SC_SET,
        BODY_ITEM,
        BODY_ITEM_GROUP,
        FIXTURE_SET,
        FIXTURE_SET_GROUP,
        JOINT_ITEM,
        JOINT_ITEM_GROUP
    }

    Type type;

    public PhysModelJTreeNode(Type type, Object userObject) {
        super(userObject);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public static String getHandlerString( ScContainer.Handler handler ) {
        StringBuilder sb = new StringBuilder("Id: " + handler.id );
        String name = handler.container.findNameById(handler.id);
        if (name != null && !name.isEmpty())
            sb.append(" <" + name + "> ");
        if (handler.container.isContentSelected( handler.id ) )
            sb.append(" *");
        return sb.toString();
    }

    @Override
    public String toString() {

        Object object = getUserObject();

        if ( object == null )
            return this.type.name() + " with NULL object";

        switch (this.type) {
            case MODEL:
                return "Model: " + ((PhysModel) object).getName();
            case BiScSET:
                return getHandlerString( (ScContainer.Handler) object);
            case AAG:
                return "Aag: " + ((AnimatedActorGroup) object).getName();
            case AAG_SC:
                return ": AAG Selectable Content";
            case AAG_SC_SET:
                return getHandlerString( (ScContainer.Handler) object);
            case BODY_ITEM:
                return "Body: " + object.toString();
            case BODY_ITEM_GROUP:
                return ": Bodies ";
            case FIXTURE_SET:
                return "FixtureSet: " + (( FixtureSet ) object).getName();
            case FIXTURE_SET_GROUP:
                return ": Fixtures";
            case JOINT_ITEM:
                return "Joint: " + object.toString();
            case JOINT_ITEM_GROUP:
                return ": Joints ";
        }

        return "";
    }
}
