package org.skr.PhysModelEditor.PropertiesTableElements;

import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.utils.Array;
import org.skr.physmodel.BodyItem;
import org.skr.physmodel.JointItem;

import javax.swing.*;
import java.util.HashMap;

/**
 * Created by rat on 06.07.14.
 */
public class JointPropertiesTableModel extends PropertiesBaseTableModel {

    private static HashMap<JointDef.JointType, Array<Property_> > propMap = new HashMap<JointDef.JointType, Array<Property_>>();

    private enum Property_ {

        Name(PropertyType.STRING),
        Type(PropertyType.SELECTOR ),
        BodyA(PropertyType.SELECTOR),
        BodyB(PropertyType.SELECTOR),
        CollideConnected(PropertyType.BOOLEAN),
        Length(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        AnchorA_X(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        AnchorA_Y(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        AnchorB_X(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        AnchorB_Y(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        FrequencyHz(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        DumpingRatio(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
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
        JointA(PropertyType.SELECTOR),
        JointB(PropertyType.SELECTOR),
        LinearOffset_X(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        LinearOffset_Y(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        Target_X(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
        Target_Y(PropertyType.NUMBER, DataRole.PHYS_COORDINATES),
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
        static Array<String> jointTypeNames = new Array<String>();
        static {
            for ( int i = 0; i < JointDef.JointType.values().length; i++)
                jointTypeNames.add( JointDef.JointType.values()[i].toString() );
        }
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
                Property_.DumpingRatio, Property_.FrequencyHz,
                Property_.Length);
        propMap.put(JointDef.JointType.DistanceJoint, propList );
    }


    JointItem jointItem = null;

    Array< Integer > bodyIdList = new Array< Integer >();
    Array< String > bodyNameList = new Array<String>();

    Array< Integer > jointIdList = new Array< Integer >();
    Array< String > jointNameList = new Array<String>();



    public JointPropertiesTableModel(JTree modelJTree) {
        super(modelJTree);
    }

    public void setJointItem( JointItem jointItem ) {

        this.jointItem = jointItem;

        bodyIdList.clear();
        bodyNameList.clear();

        jointIdList.clear();
        jointNameList.clear();

        for ( BodyItem bi : jointItem.getModel().getBodyItems() ) {
            bodyIdList.add( bi.getId() );
            bodyNameList.add( bi.getName() );
        }

        for (JointItem ji : jointItem.getModel().getJointItems() ) {
            jointIdList.add( ji.getId() );
            jointNameList.add( ji.getName() );
        }
    }

    int getBodyNameIndex( int id ) {
        int index = bodyIdList.indexOf( id, true );
        return index;
    }

    @Override
    public int getCurrentSelectorIndex(int rowIndex) {
        if ( jointItem == null )
            return -1;

        Property_ prop = getProperty(jointItem.getJoint().getType(), rowIndex);

        switch ( prop ) {
            case Type:
                return Property_.jointTypeNames.indexOf(
                        jointItem.getJoint().getType().name(), false);
            case BodyA:
                return getBodyNameIndex( jointItem.getBodyAId() );
            case BodyB:
                return getBodyNameIndex( jointItem.getBodyBId() );
            case JointA:
                //TODO: implement this
            case JointB:
                //TODO: implement this
        }

        return -1;
    }

    @Override
    public Array<String> getSelectorArray(int rowIndex) {
        if ( jointItem == null)
            return null;
        Property_ prop = getProperty( jointItem.getJoint().getType(), rowIndex );
        if ( prop == null )
            return null;
        switch ( prop ) {
            case Type:
                return Property_.jointTypeNames;
            case BodyA:
            case BodyB:
                return bodyNameList;
            case JointA:
            case JointB:
                return jointNameList;
        }

        return null;
    }

    @Override
    public PropertyType getPropertyType(int rowIndex) {
        if ( jointItem == null)
            return null;
        Property_ prop = getProperty(jointItem.getJoint().getType(), rowIndex);
        if ( prop == null )
            return null;
        return prop.propertyType;
    }

    @Override
    public DataRole getDataRole(int rowIndex) {
        if ( jointItem == null)
            return DataRole.DEFAULT;
        Property_ prop = getProperty(jointItem.getJoint().getType(), rowIndex);
        if ( prop == null )
            return DataRole.DEFAULT;
        return prop.dataRole;
    }

    @Override
    public int getPropertiesCount() {
        if ( jointItem == null )
            return 0;
        return getPropertiesCount(jointItem.getJoint().getType());
    }

    @Override
    public boolean isPropertyEditable(int rowIndex) {
        Property_ prop = getProperty(jointItem.getJoint().getType(), rowIndex);
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
                break;
            case AnchorA_Y:
                break;
            case AnchorB_X:
                break;
            case AnchorB_Y:
                break;
            case FrequencyHz:
                break;
            case DumpingRatio:
                break;
            case Ratio:
                break;
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
                break;
            case LowerTranslation:
                break;
            case UpperTranslation:
                break;
            case EnableLimit:
                break;
            case EnableMotor:
                break;
            case JointA:
                break;
            case JointB:
                break;
            case LinearOffset_X:
                break;
            case LinearOffset_Y:
                break;
            case Target_X:
                break;
            case Target_Y:
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
        return true;
    }

    @Override
    public Object getPropertyName(int rowIndex) {
        if ( jointItem == null)
            return null;
        Property_ prop = getProperty(jointItem.getJoint().getType(), rowIndex);
        if ( prop == null )
            return null;
        return prop.name();
    }

    String getBodyName( int id ) {
        BodyItem bi = jointItem.getModel().findBodyItem( id );
        if ( bi == null )
            return "";
        return bi.getName();
    }

    @Override
    public Object getPropertyValue(int rowIndex) {
        Property_ prop = getProperty(jointItem.getJoint().getType(), rowIndex);

        switch ( prop ) {
            case Name:
                return jointItem.getName();
            case Type:
                return jointItem.getJoint().getType().name();
            case BodyA:
                return getBodyName( jointItem.getBodyAId() );
            case BodyB:
                return getBodyName( jointItem.getBodyBId() );
            case CollideConnected:
                return jointItem.isCollideConnected();
            case Length:
                return jointItem.getLength();
            case AnchorA_X:
                return jointItem.getAnchorA().x;
            case AnchorA_Y:
                return jointItem.getAnchorA().y;
            case AnchorB_X:
                return jointItem.getAnchorB().x;
            case AnchorB_Y:
                return jointItem.getAnchorB().y;
            case FrequencyHz:
                return jointItem.getFrequencyHz();
            case DumpingRatio:
                return jointItem.getDampingRatio();
            case Ratio:
                return jointItem.getRatio();
            case MaxLength:
                return jointItem.getMaxLength();
            case MaxForce:
                return jointItem.getMaxForce();
            case MaxMotorForce:
                return jointItem.getMaxMotorForce();
            case MotorSpeed:
                return jointItem.getMotorSpeed();
            case MaxMotorTorque:
                return jointItem.getMaxMotorTorque();
            case MaxTorque:
                return jointItem.getMaxTorque();
            case AngularOffset:
                return jointItem.getAngularOffset();
            case LowerAngle:
                return jointItem.getLowerAngle();
            case UpperAngle:
                return jointItem.getUpperAngle();
            case CorrectionFactor:
                return jointItem.getCorrectionFactor();
            case ReferenceAngle:
                return jointItem.getReferenceAngle();
            case LowerTranslation:
                return jointItem.getLowerTranslation();
            case UpperTranslation:
                return jointItem.getUpperTranslation();
            case EnableLimit:
                return jointItem.isEnableLimit();
            case EnableMotor:
                return jointItem.isEnableMotor();
            case JointA:
                //TODO: implement that
                break;
            case JointB:
                //TODO: implement that
                break;
            case LinearOffset_X:
                return jointItem.getLinearOffset().x;
            case LinearOffset_Y:
                return jointItem.getLinearOffset().y;
            case Target_X:
                return jointItem.getTarget().x;
            case Target_Y:
                return jointItem.getTarget().y;
            case Axis_X:
                return jointItem.getAxis().x;
            case Axis_Y:
                return jointItem.getAxis().y;
            case GroundAnchorA_X:
                return jointItem.getGroundAnchorA().x;
            case GroundAnchorA_Y:
                return jointItem.getGroundAnchorA().y;
            case GroundAnchorB_X:
                return jointItem.getGroundAnchorB().x;
            case GroundAnchorB_Y:
                return jointItem.getGroundAnchorB().y;
        }

        return null;
    }

    @Override
    public void setProperty(Object aValue, int rowIndex) {
        Property_ prop = getProperty(jointItem.getJoint().getType(), rowIndex);

        switch( prop ) {
            case Name:
                jointItem.setName( (String) aValue);
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
                jointItem.setLength((Float) aValue);
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
                jointItem.setFrequencyHz((Float) aValue );
                break;
            case DumpingRatio:
                jointItem.setDampingRatio((Float) aValue);
                break;
            case Ratio:
                jointItem.setRatio((Float) aValue);
                break;
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
                break;
            case LowerTranslation:
                break;
            case UpperTranslation:
                break;
            case EnableLimit:
                break;
            case EnableMotor:
                break;
            case JointA:
                break;
            case JointB:
                break;
            case LinearOffset_X:
                break;
            case LinearOffset_Y:
                break;
            case Target_X:
                break;
            case Target_Y:
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