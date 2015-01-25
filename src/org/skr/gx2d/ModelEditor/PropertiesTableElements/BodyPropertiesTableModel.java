package org.skr.gx2d.ModelEditor.PropertiesTableElements;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import org.skr.gx2d.physnodes.BodyHandler;

import javax.swing.*;

/**
 * Created by rat on 08.06.14.
 */
public class BodyPropertiesTableModel extends PropertiesBaseTableModel {


    private enum Property_ {

        Name(PropertyType.STRING),
        Type(PropertyType.SELECTOR),
        PositionX(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        PositionY(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        Angle(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        LinearVelX(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        LinearVelY(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        AngularVel(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        LinearDamping(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        AngularDamping(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        AllowSleep(PropertyType.BOOLEAN),
        Awake(PropertyType.BOOLEAN),
        FixedRot(PropertyType.BOOLEAN),
        Bullet(PropertyType.BOOLEAN),
        Active(PropertyType.BOOLEAN),
        GravityScale(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        Mass(PropertyType.NUMBER, DataRole.PHYS_COORDINATES, false),
        MassCenterX(PropertyType.NUMBER, DataRole.PHYS_COORDINATES, false),
        MassCenterY(PropertyType.NUMBER, DataRole.PHYS_COORDINATES, false);

        private PropertyType propertyType;
        private DataRole dataRole = DataRole.DEFAULT;
        private boolean editable = true;

        Property_(PropertyType propertyType) {
            this.propertyType = propertyType;
        }

        Property_(PropertyType propertyType, DataRole dataRole) {
            this.propertyType = propertyType;
            this.dataRole = dataRole;
        }

        Property_(PropertyType propertyType, DataRole dataRole, boolean editable) {
            this.propertyType = propertyType;
            this.dataRole = dataRole;
            this.editable = editable;
        }

        Property_(PropertyType propertyType, boolean editable) {
            this.propertyType = propertyType;
            this.editable = editable;
        }

        static Property_[] values = Property_.values();
        static Array<String> typeNames = new Array<String>();

        static {
            for ( int i = 0; i < BodyDef.BodyType.values().length; i++)
                typeNames.add( BodyDef.BodyType.values()[i].name() );
        }
    }

    BodyHandler bodyHandler;


    public BodyPropertiesTableModel(JTree modelJTree) {
        super(modelJTree);
    }

    public BodyHandler getBodyHandler() {
        return bodyHandler;
    }

    public void setBodyHandler(BodyHandler bodyHandler) {
        this.bodyHandler = bodyHandler;
    }


    public void bodyItemChanged( BodyHandler bodyItem ) {
        if ( bodyItem == this.bodyHandler) {
            fireTableDataChanged();
        }
    }

    //==========================================================

    protected static  Property_ getProperty( int rowIndex ) {
        return Property_.values[rowIndex];
    }

    @Override
    public int getCurrentSelectorIndex(int rowIndex) {
        if ( bodyHandler == null )
            return -1;
        Property_ property = getProperty( rowIndex );
        if ( property == Property_.Type )
            return bodyHandler.getBody().getType().ordinal();
        return -1;
    }

    @Override
    public Array<String> getSelectorArray(int rowIndex) {
        Property_ property = Property_.values[rowIndex];

        if ( property == Property_.Type )
            return Property_.typeNames;

        return null;
    }

    @Override
    public PropertyType getPropertyType(int rowIndex) {
        return getProperty( rowIndex ).propertyType;
    }

    @Override
    public DataRole getDataRole(int rowIndex) {
        return getProperty( rowIndex ).dataRole;
    }


    @Override
    public int getPropertiesCount() {
        return Property_.values.length;
    }

    @Override
    public boolean isPropertyEditable(int rowIndex) {
        return getProperty( rowIndex ).editable;
    }

    @Override
    public Object getPropertyName(int rowIndex) {
        return getProperty( rowIndex ).name();
    }

    @Override
    public Object getPropertyValue(int rowIndex) {
        if ( bodyHandler == null)
            return null;
        Body body = bodyHandler.getBody();
        Property_ property = getProperty( rowIndex );
        switch (property) {
            case Name:
                return bodyHandler.getName();
            case Type:
                return body.getType().name();
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
            case LinearDamping:
                return body.getLinearDamping();
            case AngularDamping:
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

        if ( bodyHandler == null )
            return;
        Body body = bodyHandler.getBody();
        Property_ property = Property_.values[rowIndex];
        switch ( property ) {
            case Name:
                bodyHandler.setName((String) aValue);
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
            case LinearDamping:
                body.setLinearDamping( (Float) aValue );
                break;
            case AngularDamping:
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
            case Mass:
                break;
            case MassCenterX:
                break;
            case MassCenterY:
                break;
        }
        fireTableDataChanged();
    }

}
