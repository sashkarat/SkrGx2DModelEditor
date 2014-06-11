package org.skr.PhysModelEditor.PropertiesTableElements;

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
        PositionX(PropertyType.NUMBER),
        PositionY(PropertyType.NUMBER),
        Angle(PropertyType.NUMBER),
        LinearVelX(PropertyType.NUMBER),
        LinearVelY(PropertyType.NUMBER),
        AngularVel(PropertyType.NUMBER),
        LinearDumping(PropertyType.NUMBER),
        AngularDumping(PropertyType.NUMBER),
        AllowSleep(PropertyType.BOOLEAN),
        Awake(PropertyType.BOOLEAN),
        FixedRot(PropertyType.BOOLEAN),
        Bullet(PropertyType.BOOLEAN),
        Active(PropertyType.BOOLEAN),
        GravityScale(PropertyType.NUMBER);

        private PropertyType propertyType;

        Properties_(PropertyType propertyType) {
            this.propertyType = propertyType;
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
    public int getPropertiesCount() {
        return Properties_.values.length;
    }

    @Override
    public boolean isPropertyEditable(int rowIndex) {
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
                return body.getAngle();
            case LinearVelX:
                return body.getLinearVelocity().x;
            case LinearVelY:
                return body.getLinearVelocity().y;
            case AngularVel:
                return body.getAngularVelocity();
            case LinearDumping:
                return body.getLinearDamping();
            case AngularDumping:
                return body.getAngularDamping();
            case AllowSleep:
                return body.isSleepingAllowed();
            case Awake:
                return body.isAwake();
            case FixedRot:
                return body.isFixedRotation();
            case Bullet:
                return body.isFixedRotation();
            case Active:
                return body.isActive();
            case GravityScale:
                return body.getGravityScale();
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
                body.setTransform( tmp, (Float) aValue );
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
                body.setAngularVelocity( (Float) aValue );
                break;
            case LinearDumping:
                body.setLinearDamping( (Float) aValue );
                break;
            case AngularDumping:
                body.setAngularVelocity( (Float) aValue );
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
