package org.skr.gx2d.ModelEditor.PropertiesTableElements;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.utils.Array;
import org.skr.gx2d.physnodes.BodyHandler;
import org.skr.gx2d.physnodes.JointHandler;

import javax.swing.*;
import java.util.HashMap;

/**
 * Created by rat on 06.07.14.
 */
public class JointPropertiesTableModel extends PropertiesBaseTableModel {

    private static HashMap<JointDef.JointType, Array<Property_> > propMap = new HashMap<JointDef.JointType, Array<Property_>>();

    public enum Property_ {

        Name(PropertyType.STRING),
        Type(PropertyType.STRING ),
        BodyA(PropertyType.STRING),
        BodyB(PropertyType.STRING),
        CollideConnected(PropertyType.BOOLEAN),
        Length(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        AnchorA_X(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        AnchorA_Y(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        AnchorB_X(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        AnchorB_Y(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        FrequencyHz(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        DampingRatio(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        Ratio(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        MaxLength(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        MaxForce(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        MaxMotorForce(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        MotorSpeed(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        MaxMotorTorque(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        MaxTorque(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        AngularOffset(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        LowerAngle(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        UpperAngle(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        CorrectionFactor(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        ReferenceAngle(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        LowerTranslation(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        UpperTranslation(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        EnableLimit(PropertyType.BOOLEAN),
        EnableMotor(PropertyType.BOOLEAN),
        JointA(PropertyType.STRING),
        JointB(PropertyType.STRING),
        LinearOffset_X(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        LinearOffset_Y(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        Axis_X(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        Axis_Y(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        GroundAnchorA_X(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        GroundAnchorA_Y(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        GroundAnchorB_X(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        GroundAnchorB_Y(PropertyType.NUMBER, DataRole.PHYS_COORDINATES);

        private PropertyType propertyType;
        private DataRole dataRole = DataRole.DEFAULT;

        Property_(PropertyType propertyType) {
            this.propertyType = propertyType;
        }

        Property_(PropertyType propertyType, DataRole dataRole) {
            this.propertyType = propertyType;
            this.dataRole = dataRole;
        }

        static Property_[] values = Property_.values();
    }

    static { // fill propMap

        Array<Property_> propList = new Array<Property_>();
        propList.addAll(Property_.Name, Property_.Type );
        propMap.put(JointDef.JointType.Unknown, propList);


        // Distance Joint
        propList = new Array<Property_>();
        propList.addAll(Property_.Name, Property_.Type,
                Property_.BodyA, Property_.BodyB, Property_.CollideConnected,
                Property_.AnchorA_X, Property_.AnchorA_Y,
                Property_.AnchorB_X, Property_.AnchorB_Y,
                Property_.DampingRatio, Property_.FrequencyHz,
                Property_.Length);
        propMap.put(JointDef.JointType.DistanceJoint, propList );

        // Revolute Joint

        propList = new Array<Property_>();
        propList.addAll(Property_.Name, Property_.Type,
                Property_.BodyA, Property_.BodyB, Property_.CollideConnected,
                Property_.AnchorA_X, Property_.AnchorA_Y,
                Property_.EnableLimit, Property_.UpperAngle,
                Property_.LowerAngle, Property_.EnableMotor, Property_.MotorSpeed,
                Property_.MaxMotorTorque);
        propMap.put(JointDef.JointType.RevoluteJoint, propList );

        // Prismatic Joint

        propList = new Array<Property_>();
        propList.addAll(Property_.Name, Property_.Type,
                Property_.BodyA, Property_.BodyB, Property_.CollideConnected,
                Property_.AnchorA_X, Property_.AnchorA_Y,
                Property_.Axis_X, Property_.Axis_Y,
                Property_.EnableLimit, Property_.UpperTranslation,
                Property_.LowerTranslation, Property_.EnableMotor, Property_.MotorSpeed,
                Property_.MaxMotorForce);
        propMap.put(JointDef.JointType.PrismaticJoint, propList );

        // Pulley Joint

        propList = new Array<Property_>();
        propList.addAll(Property_.Name, Property_.Type,
                Property_.BodyA, Property_.BodyB, Property_.CollideConnected,
                Property_.AnchorA_X, Property_.AnchorA_Y,
                Property_.AnchorB_X, Property_.AnchorB_Y,
                Property_.GroundAnchorA_X, Property_.GroundAnchorA_Y,
                Property_.GroundAnchorB_X, Property_.GroundAnchorB_Y,
                Property_.Ratio);
        propMap.put(JointDef.JointType.PulleyJoint, propList );

        // Gear Joint

        propList = new Array<Property_>();
        propList.addAll(Property_.Name, Property_.Type,
                Property_.BodyA, Property_.BodyB, Property_.CollideConnected,
                Property_.JointA, Property_.JointB,
                Property_.Ratio);
        propMap.put(JointDef.JointType.GearJoint, propList );

        // Wheel Joint

        propList = new Array<Property_>();
        propList.addAll( Property_.Name, Property_.Type,
                Property_.BodyA, Property_.BodyB, Property_.CollideConnected,
                Property_.AnchorA_X, Property_.AnchorA_Y,
                Property_.Axis_X, Property_.Axis_Y,
                Property_.DampingRatio, Property_.FrequencyHz,
                Property_.EnableMotor, Property_.MaxMotorTorque, Property_.MotorSpeed );
        propMap.put(JointDef.JointType.WheelJoint, propList );

        // Rope Joint

        propList = new Array<Property_>();
        propList.addAll( Property_.Name, Property_.Type,
                Property_.BodyA, Property_.BodyB, Property_.CollideConnected,
                Property_.AnchorA_X, Property_.AnchorA_Y,
                Property_.AnchorB_X, Property_.AnchorB_Y,
                Property_.MaxLength );
        propMap.put(JointDef.JointType.RopeJoint, propList );

        // Friction Joint

        propList = new Array<Property_>();
        propList.addAll(Property_.Name, Property_.Type,
                Property_.BodyA, Property_.BodyB, Property_.CollideConnected,
                Property_.AnchorA_X, Property_.AnchorA_Y,
                Property_.MaxForce, Property_.MaxTorque );
        propMap.put(JointDef.JointType.FrictionJoint, propList );

        // Motor Joint

        propList = new Array<Property_>();
        propList.addAll(Property_.Name, Property_.Type,
                Property_.BodyA, Property_.BodyB, Property_.CollideConnected,
                Property_.LinearOffset_X, Property_.LinearOffset_Y,
                Property_.AngularOffset, Property_.MaxForce, Property_.MaxTorque,
                Property_.CorrectionFactor);
        propMap.put(JointDef.JointType.MotorJoint, propList );

        // Weld Joint

        propList = new Array<Property_>();
        propList.addAll(Property_.Name, Property_.Type,
                Property_.BodyA, Property_.BodyB, Property_.CollideConnected,
                Property_.AnchorA_X, Property_.AnchorA_Y,
                Property_.ReferenceAngle, Property_.DampingRatio, Property_.FrequencyHz );
        propMap.put(JointDef.JointType.WeldJoint, propList );
    }


    JointHandler jointHandler = null;

    public JointPropertiesTableModel(JTree modelJTree) {
        super(modelJTree);
    }

    public void setJointHandler(JointHandler jointHandler) {
        this.jointHandler = jointHandler;
    }

    @Override
    public int getCurrentSelectorIndex(int rowIndex) {
        return -1;
    }

    @Override
    public Array<String> getSelectorArray(int rowIndex) {
        return null;
    }

    @Override
    public PropertyType getPropertyType(int rowIndex) {
        if ( jointHandler == null || jointHandler.getJoint() == null )
            return null;
        Property_ prop = getProperty(jointHandler.getJoint().getType(), rowIndex);
        if ( prop == null )
            return null;
        return prop.propertyType;
    }

    @Override
    public DataRole getDataRole(int rowIndex) {
        if ( jointHandler == null || jointHandler.getJoint() == null )
            return DataRole.DEFAULT;
        Property_ prop = getProperty(jointHandler.getJoint().getType(), rowIndex);
        if ( prop == null )
            return DataRole.DEFAULT;
        return prop.dataRole;
    }

    @Override
    public int getPropertiesCount() {
        if ( jointHandler == null || jointHandler.getJoint() == null  )
            return 0;
        return getPropertiesCount(jointHandler.getJoint().getType());
    }

    @Override
    public boolean isPropertyEditable(int rowIndex) {
        Property_ prop = getProperty(jointHandler.getJoint().getType(), rowIndex);
        switch ( prop ) {
            case Name:
                break;
            case Type:
                return false;
            case BodyA:
                return false;
            case BodyB:
                return false;
            case CollideConnected:
                return false;
            case Length:
                break;
            case AnchorA_X:
                return false;
            case AnchorA_Y:
                return false;
            case AnchorB_X:
                return false;
            case AnchorB_Y:
                return false;
            case FrequencyHz:
                break;
            case DampingRatio:
                break;
            case Ratio:
                return false;
            case MaxLength:
                break;
            case MaxForce:
                break;
            case MaxMotorForce:
                break;
            case MotorSpeed:
                break;
            case MaxMotorTorque:
                break;
            case MaxTorque:
                break;
            case AngularOffset:
                break;
            case LowerAngle:
                break;
            case UpperAngle:
                break;
            case CorrectionFactor:
                break;
            case ReferenceAngle:
                return false;
            case LowerTranslation:
                break;
            case UpperTranslation:
                break;
            case EnableLimit:
                break;
            case EnableMotor:
                break;
            case JointA:
                return false;
            case JointB:
                return false;
            case LinearOffset_X:
                break;
            case LinearOffset_Y:
                break;
            case Axis_X:
                return false;
            case Axis_Y:
                return false;
            case GroundAnchorA_X:
                return false;
            case GroundAnchorA_Y:
                return false;
            case GroundAnchorB_X:
                return false;
            case GroundAnchorB_Y:
                return false;
        }
        return true;
    }

    @Override
    public Object getPropertyName(int rowIndex) {
        if ( jointHandler == null || jointHandler.getJoint() == null )
            return null;
        Property_ prop = getProperty(jointHandler.getJoint().getType(), rowIndex);
        if ( prop == null )
            return null;
        return prop.name();
    }

    String getBodyName( long id ) {
        BodyHandler bh = jointHandler.getPhysSet().getBodyHandler( id );
        if ( bh == null )
            return "not found";
        return bh.getName();
    }

    String getJointName(long id)  {
        JointHandler jh = jointHandler.getPhysSet().getJointHandler( id );
        if ( jh == null )
            return "not found";
        return jh.getName();
    }

    @Override
    public Object getPropertyValue(int rowIndex) {
        Property_ prop = getProperty(jointHandler.getJoint().getType(), rowIndex);

        switch ( prop ) {
            case Name:
                return jointHandler.getName();
            case Type:
                return jointHandler.getJoint().getType().name();
            case BodyA:
                return getBodyName( jointHandler.getBodyAId() );
            case BodyB:
                return getBodyName( jointHandler.getBodyBId() );
            case CollideConnected:
                return jointHandler.isCollideConnected();
            case Length:
                return jointHandler.getLength();
            case AnchorA_X:
                return jointHandler.getAnchorA().x;
            case AnchorA_Y:
                return jointHandler.getAnchorA().y;
            case AnchorB_X:
                return jointHandler.getAnchorB().x;
            case AnchorB_Y:
                return jointHandler.getAnchorB().y;
            case FrequencyHz:
                return jointHandler.getFrequencyHz();
            case DampingRatio:
                return jointHandler.getDampingRatio();
            case Ratio:
                return jointHandler.getRatio();
            case MaxLength:
                return jointHandler.getMaxLength();
            case MaxForce:
                return jointHandler.getMaxForce();
            case MaxMotorForce:
                return jointHandler.getMaxMotorForce();
            case MotorSpeed:
                return jointHandler.getMotorSpeed();
            case MaxMotorTorque:
                return jointHandler.getMaxMotorTorque();
            case MaxTorque:
                return jointHandler.getMaxTorque();
            case AngularOffset:
                return jointHandler.getAngularOffset() * MathUtils.radiansToDegrees;
            case LowerAngle:
                return jointHandler.getLowerAngle() * MathUtils.radiansToDegrees;
            case UpperAngle:
                return jointHandler.getUpperAngle() * MathUtils.radiansToDegrees;
            case CorrectionFactor:
                return jointHandler.getCorrectionFactor();
            case ReferenceAngle:
                return jointHandler.getReferenceAngle() * MathUtils.radiansToDegrees;
            case LowerTranslation:
                return jointHandler.getLowerTranslation();
            case UpperTranslation:
                return jointHandler.getUpperTranslation();
            case EnableLimit:
                return jointHandler.isEnableLimit();
            case EnableMotor:
                return jointHandler.isEnableMotor();
            case JointA:
                return getJointName(jointHandler.getJointAId());
            case JointB:
                return getJointName(jointHandler.getJointBId());
            case LinearOffset_X:
                return jointHandler.getLinearOffset().x;
            case LinearOffset_Y:
                return jointHandler.getLinearOffset().y;
            case Axis_X:
                return jointHandler.getAxis().x;
            case Axis_Y:
                return jointHandler.getAxis().y;
            case GroundAnchorA_X:
                return jointHandler.getGroundAnchorA().x;
            case GroundAnchorA_Y:
                return jointHandler.getGroundAnchorA().y;
            case GroundAnchorB_X:
                return jointHandler.getGroundAnchorB().x;
            case GroundAnchorB_Y:
                return jointHandler.getGroundAnchorB().y;
        }

        return null;
    }

    @Override
    public void setProperty(Object aValue, int rowIndex) {
        Property_ prop = getProperty(jointHandler.getJoint().getType(), rowIndex);

        switch( prop ) {
            case Name:
                jointHandler.setName((String) aValue);
                break;
            case Type:
                break;
            case BodyA:
                break;
            case BodyB:
                break;
            case CollideConnected:
                break;
            case Length:
                jointHandler.setLength((Float) aValue);
                break;
            case AnchorA_X:
                break;
            case AnchorA_Y:
                break;
            case AnchorB_X:
                break;
            case AnchorB_Y:
                break;
            case FrequencyHz:
                jointHandler.setFrequencyHz((Float) aValue);
                break;
            case DampingRatio:
                jointHandler.setDampingRatio((Float) aValue);
                break;
            case Ratio:
                jointHandler.setRatio((Float) aValue);
                break;
            case MaxLength:
                jointHandler.setMaxLength((Float) aValue);
                break;
            case MaxForce:
                jointHandler.setMaxForce((Float) aValue);
                break;
            case MaxMotorForce:
                jointHandler.setMaxMotorForce((Float) aValue);
                break;
            case MotorSpeed:
                jointHandler.setMotorSpeed((Float) aValue);
                break;
            case MaxMotorTorque:
                jointHandler.setMaxMotorTorque((Float) aValue);
                break;
            case MaxTorque:
                jointHandler.setMaxTorque((Float) aValue);
                break;
            case AngularOffset:
                jointHandler.setAngularOffset((Float) aValue * MathUtils.degreesToRadians);
                break;
            case LowerAngle:
                jointHandler.setLowerAngle((Float) aValue * MathUtils.degreesToRadians);
                break;
            case UpperAngle:
                jointHandler.setUpperAngle((Float) aValue * MathUtils.degreesToRadians);
                break;
            case CorrectionFactor:
                jointHandler.setCorrectionFactor((Float) aValue);
                break;
            case ReferenceAngle:
                break;
            case LowerTranslation:
                jointHandler.setLowerTranslation((Float) aValue);
                break;
            case UpperTranslation:
                jointHandler.setUpperTranslation((Float) aValue);
                break;
            case EnableLimit:
                jointHandler.setEnableLimit((Boolean) aValue);
                break;
            case EnableMotor:
                jointHandler.setEnableMotor((Boolean) aValue);
                break;
            case JointA:
                break;
            case JointB:
                break;
            case LinearOffset_X:
                break;
            case LinearOffset_Y:
                break;
            case Axis_X:
                break;
            case Axis_Y:
                break;
            case GroundAnchorA_X:
                break;
            case GroundAnchorA_Y:
                break;
            case GroundAnchorB_X:
                break;
            case GroundAnchorB_Y:
                break;
        }
    }

    protected static Property_ getProperty(JointDef.JointType type, int index) {
        if ( !propMap.containsKey( type ) )
            return null;
        Array<Property_> properties = propMap.get( type );
        if ( index < 0 || index >= properties.size )
            return null;
        return properties.get( index );
    }

    protected static int getPropertiesCount(JointDef.JointType type) {
        if ( !propMap.containsKey( type ) )
            return 0;
        return propMap.get( type ).size;
    }
}
