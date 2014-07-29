package org.skr.PhysModelEditor.PropertiesTableElements;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import org.skr.physmodel.BodyItem;
import org.skr.physmodel.PhysModel;

import javax.swing.*;

/**
 * Created by rat on 08.06.14.
 */
public class BodyPropertiesTableModel extends PropertiesBaseTableModel {


    private enum Properties_ {

        Name(PropertyType.STRING),
        Type(PropertyType.SELECTOR),
        PositionX(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        PositionY(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        Angle(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        LinearVelX(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        LinearVelY(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        AngularVel(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        LinearDumping(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        AngularDumping(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        AllowSleep(PropertyType.BOOLEAN),
        Awake(PropertyType.BOOLEAN),
        FixedRot(PropertyType.BOOLEAN),
        Bullet(PropertyType.BOOLEAN),
        Active(PropertyType.BOOLEAN),
        GravityScale(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        Mass(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        MassCenterX(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        MassCenterY(PropertyType.NUMBER, DataRole.PHYS_COORDINATES);

        private PropertyType propertyType;
        private DataRole dataRole = DataRole.DEFAULT;

        Properties_(PropertyType propertyType) {
            this.propertyType = propertyType;
        }

        Properties_(PropertyType propertyType, DataRole dataRole) {
            this.propertyType = propertyType;
            this.dataRole = dataRole;
        }

        static Properties_[] values = Properties_.values();
        static Array<String> typeNames = new Array<String>();
        static {
            for ( int i = 0; i < BodyDef.BodyType.values().length; i++)
                typeNames.add( BodyDef.BodyType.values()[i].toString() );
        }
    }

    BodyItem bodyItem;


    public BodyPropertiesTableModel(JTree modelJTree) {
        super(modelJTree);
    }



    public BodyItem getBodyItem() {
        return bodyItem;
    }

    public void setBodyItem( BodyItem bodyItem) {
        this.bodyItem = bodyItem;
    }


    public void bodyItemChanged( BodyItem bodyItem ) {
        if ( bodyItem == this.bodyItem ) {
            fireTableDataChanged();
        }
    }

    //==========================================================

    @Override
    public int getCurrentSelectorIndex(int rowIndex) {
        if ( bodyItem == null )
            return -1;
        return bodyItem.getBody().getType().ordinal();
    }

    @Override
    public Array<String> getSelectorArray(int rowIndex) {
        Properties_ property = Properties_.values[rowIndex];

        if ( property == Properties_.Type ) {
            return Properties_.typeNames;
        }

        return null;
    }

    @Override
    public PropertyType getPropertyType(int rowIndex) {
        Properties_ property = Properties_.values[rowIndex];
        return property.propertyType;
    }

    @Override
    public DataRole getDataRole(int rowIndex) {
        Properties_ property = Properties_.values[rowIndex];
        return property.dataRole;
    }


    @Override
    public int getPropertiesCount() {
        return Properties_.values.length;
    }

    @Override
    public boolean isPropertyEditable(int rowIndex) {
        Properties_ property = Properties_.values[rowIndex];
        switch ( property ) {
            case Name:
                break;
            case Type:
                break;
            case PositionX:
                break;
            case PositionY:
                break;
            case Angle:
                break;
            case LinearVelX:
                break;
            case LinearVelY:
                break;
            case AngularVel:
                break;
            case LinearDumping:
                break;
            case AngularDumping:
                break;
            case AllowSleep:
                break;
            case Awake:
                break;
            case FixedRot:
                break;
            case Bullet:
                break;
            case Active:
                break;
            case GravityScale:
                break;
            case Mass:
                return false;
            case MassCenterX:
                return false;
            case MassCenterY:
                return false;
        }
        return true;
    }

    @Override
    public Object getPropertyName(int rowIndex) {
        if ( rowIndex >= getPropertiesCount() )
            return null;
        return Properties_.values[rowIndex].toString();
    }

    @Override
    public Object getPropertyValue(int rowIndex) {
        if ( bodyItem == null)
            return null;
        Body body = bodyItem.getBody();
        Properties_ property = Properties_.values[rowIndex];
        switch (property) {
            case Name:
                return bodyItem.getName();
            case Type:
                return body.getType().toString();
            case PositionX:
                return body.getPosition().x;
            case PositionY:
                return body.getPosition().y;
            case Angle:
                return MathUtils.radiansToDegrees * body.getAngle();
            case LinearVelX:
                return body.getLinearVelocity().x;
            case LinearVelY:
                return body.getLinearVelocity().y;
            case AngularVel:
                return MathUtils.radiansToDegrees * body.getAngularVelocity();
            case LinearDumping:
                return body.getLinearDamping();
            case AngularDumping:
                return MathUtils.radiansToDegrees * body.getAngularDamping();
            case AllowSleep:
                return body.isSleepingAllowed();
            case Awake:
                return body.isAwake();
            case FixedRot:
                return body.isFixedRotation();
            case Bullet:
                return body.isBullet();
            case Active:
                return body.isActive();
            case GravityScale:
                return body.getGravityScale();
            case Mass:
                return body.getMass();
            case MassCenterX:
                return body.getLocalCenter().x;
            case MassCenterY:
                return body.getLocalCenter().y;
        }

        return null;
    }



    @Override
    public void setProperty(Object aValue, int rowIndex) {

        Vector2 tmp;

        if ( bodyItem == null )
            return;
        Body body = bodyItem.getBody();
        Properties_ property = Properties_.values[rowIndex];
        switch ( property ) {
            case Name:
                bodyItem.setName( (String) aValue );
                break;
            case Type:
                body.setType( BodyDef.BodyType.values()[ (Integer) aValue ] );
                break;
            case PositionX:
                tmp = body.getPosition();
                tmp.x = (Float) aValue;
                body.setTransform( tmp, body.getAngle() );
                break;
            case PositionY:
                tmp = body.getPosition();
                tmp.y = (Float) aValue;
                body.setTransform( tmp, body.getAngle() );
                break;
            case Angle:
                tmp = body.getPosition();
                body.setTransform( tmp, MathUtils.degreesToRadians * (Float) aValue );
                break;
            case LinearVelX:
                tmp = body.getLinearVelocity();
                tmp.x = (Float) aValue;
                body.setLinearVelocity( tmp );
                break;
            case LinearVelY:
                tmp = body.getLinearVelocity();
                tmp.y = (Float) aValue;
                body.setLinearVelocity( tmp );
                break;
            case AngularVel:
                body.setAngularVelocity( MathUtils.degreesToRadians * (Float) aValue );
                break;
            case LinearDumping:
                body.setLinearDamping( (Float) aValue );
                break;
            case AngularDumping:
                body.setAngularDamping( MathUtils.degreesToRadians *  (Float) aValue );
                break;
            case AllowSleep:
                body.setSleepingAllowed( (Boolean) aValue );
                break;
            case Awake:
                body.setAwake( (Boolean) aValue );
                break;
            case FixedRot:
                body.setFixedRotation( (Boolean) aValue );
                break;
            case Bullet:
                body.setBullet( (Boolean) aValue );
                break;
            case Active:
                body.setActive( (Boolean) aValue );
                break;
            case GravityScale:
                body.setGravityScale( (Float) aValue );
                break;
        }
        fireTableDataChanged();
    }

}
