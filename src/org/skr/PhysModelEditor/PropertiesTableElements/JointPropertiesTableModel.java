package org.skr.PhysModelEditor.PropertiesTableElements;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.utils.Array;
import org.skr.physmodel.BodyItem;
import org.skr.physmodel.JointItem;
import org.skr.physmodel.PhysModel;

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
                Property_.BodyA, Property_.BodyB,
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
                //TODO: implement this
            case BodyB:
                //TODO: implement this
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

    @Override
    public Object getPropertyValue(int rowIndex) {
        return null;
    }

    @Override
    public void setProperty(Object aValue, int rowIndex) {

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
