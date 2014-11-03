package org.skr.PhysModelEditor.PropertiesTableElements;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import org.skr.gdx.physmodel.bodyitem.BodyItem;
import org.skr.gdx.policy.CollisionSolver;

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
        LinearDumping(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        AngularDumping(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        AllowSleep(PropertyType.BOOLEAN),
        Awake(PropertyType.BOOLEAN),
        FixedRot(PropertyType.BOOLEAN),
        Bullet(PropertyType.BOOLEAN),
        Active(PropertyType.BOOLEAN),
        GravityScale(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        Mass(PropertyType.NUMBER, DataRole.PHYS_COORDINATES, false),
        MassCenterX(PropertyType.NUMBER, DataRole.PHYS_COORDINATES, false),
        MassCenterY(PropertyType.NUMBER, DataRole.PHYS_COORDINATES, false),
        ImpulseFunction(PropertyType.SELECTOR ),
        N(PropertyType.NUMBER),
        X(PropertyType.NUMBER, DataRole.PHYS_COORDINATES ),
        C(PropertyType.NUMBER, DataRole.PHYS_COORDINATES ),
        A1(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        A2(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        A3(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        A4(PropertyType.NUMBER, DataRole.PHYS_COORDINATES);


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
        static Array<String> ifuncNames = new Array<String>();
        static {
            for ( int i = 0; i < BodyDef.BodyType.values().length; i++)
                typeNames.add( BodyDef.BodyType.values()[i].name() );
            for ( int i = 0; i < CollisionSolver.ImpulseFunction.values().length; i++)
                ifuncNames.add(CollisionSolver.ImpulseFunction.values()[i].name() );
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

    protected static  Property_ getProperty( int rowIndex ) {
        return Property_.values[rowIndex];
    }

    @Override
    public int getCurrentSelectorIndex(int rowIndex) {
        if ( bodyItem == null )
            return -1;
        Property_ property = getProperty( rowIndex );
        if ( property == Property_.Type )
            return bodyItem.getBody().getType().ordinal();
        else if ( property == Property_.ImpulseFunction )
            return bodyItem.getCollisionProperties().getImpulseFunction().ordinal();
        return -1;
    }

    @Override
    public Array<String> getSelectorArray(int rowIndex) {
        Property_ property = Property_.values[rowIndex];

        if ( property == Property_.Type )
            return Property_.typeNames;
        else if ( property == Property_.ImpulseFunction )
            return Property_.ifuncNames;

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
        if ( bodyItem == null)
            return null;
        Body body = bodyItem.getBody();
        Property_ property = getProperty( rowIndex );
        switch (property) {
            case Name:
                return bodyItem.getName();
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
            case ImpulseFunction:
                return bodyItem.getCollisionProperties().getImpulseFunction().name();
            case N:
                return bodyItem.getCollisionProperties().getN();
            case X:
                return bodyItem.getCollisionProperties().getX();
            case C:
                return bodyItem.getCollisionProperties().getC();
            case A1:
                return bodyItem.getCollisionProperties().getA()[0];
            case A2:
                return bodyItem.getCollisionProperties().getA()[1];
            case A3:
                return bodyItem.getCollisionProperties().getA()[2];
            case A4:
                return bodyItem.getCollisionProperties().getA()[3];
        }

        return null;
    }



    @Override
    public void setProperty(Object aValue, int rowIndex) {

        Vector2 tmp;

        if ( bodyItem == null )
            return;
        Body body = bodyItem.getBody();
        Property_ property = Property_.values[rowIndex];
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
            case Mass:
                break;
            case MassCenterX:
                break;
            case MassCenterY:
                break;
            case ImpulseFunction:
                bodyItem.getCollisionProperties().setImpulseFunction(
                        CollisionSolver.ImpulseFunction.values()[ (Integer) aValue ]);
                break;
            case N:
                bodyItem.getCollisionProperties().setN(MathUtils.round((Float) aValue));
                break;
            case X:
                bodyItem.getCollisionProperties().setX((Float) aValue);
                break;
            case C:
                bodyItem.getCollisionProperties().setC((Float) aValue);
                break;
            case A1:
                bodyItem.getCollisionProperties().getA()[0] = (Float) aValue;
                break;
            case A2:
                bodyItem.getCollisionProperties().getA()[1] = (Float) aValue;
                break;
            case A3:
                bodyItem.getCollisionProperties().getA()[2] = (Float) aValue;
                break;
            case A4:
                bodyItem.getCollisionProperties().getA()[3] = (Float) aValue;
                break;
        }
        fireTableDataChanged();
    }

}
