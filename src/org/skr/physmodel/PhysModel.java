package org.skr.physmodel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;
import org.skr.physmodel.animatedactorgroup.AagDescription;
import org.skr.physmodel.animatedactorgroup.AnimatedActorGroup;
import org.skr.PhysModelEditor.PhysWorld;

import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Created by rat on 31.05.14.
 */

public class PhysModel {

    //That is test section. It does nothing.

    static {
        FixtureDef fd;

        CircleShape cs = new CircleShape();
        PolygonShape ps = new PolygonShape();
        EdgeShape es = new EdgeShape();
        ChainShape chs = new ChainShape();
    }


    static  public class Description {

        String name = "";
        AagDescription backgroundAagDesc = null;
        Array<BodyItemDescription> bodiesDesc = null;
        Array<JointItemDescription> jointsDesc = null;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public AagDescription getBackgroundAagDesc() {
            return backgroundAagDesc;
        }

        public void setBackgroundAagDesc(AagDescription backgroundAagDesc) {
            this.backgroundAagDesc = backgroundAagDesc;
        }

        public Array<BodyItemDescription> getBodyDescriptions() {
            return bodiesDesc;
        }

        public void setBodiesDesc( Array<BodyItemDescription> bodiesDesc ) {
            this.bodiesDesc = bodiesDesc;
        }

        public Array<JointItemDescription> getJointsDesc() {
            return jointsDesc;
        }

        public void setJointsDesc(Array<JointItemDescription> jointsDesc) {
            this.jointsDesc = jointsDesc;
        }
    }

    private String name = "";
    private AnimatedActorGroup backgroundActor;
    private Array<BodyItem> bodyItems = new Array<BodyItem>();
    private Array<JointItem> jointItems = new Array<JointItem>();

    public PhysModel() {
    }

    public PhysModel( Description description ) {
        uploadFromDescription( description );
    }


    public void uploadFromDescription( Description desc) {
        setName( desc.getName() );

        if ( desc.getBackgroundAagDesc() != null) {
            AnimatedActorGroup aag = new AnimatedActorGroup( desc.getBackgroundAagDesc() );
            setBackgroundActor( aag );
        }

        for ( BodyItem bi : bodyItems) {
            PhysWorld.getWorld().destroyBody( bi.body );
        }
        bodyItems.clear();

        Array<BodyItemDescription> bodiesDesc = desc.getBodyDescriptions();

        if ( bodiesDesc != null ) {

            for (BodyItemDescription bd : bodiesDesc) {
                BodyItem bi = addBodyItem( bd.getName(), bd.bodyDef, bd.getId() );

                for ( FixtureSetDescription fsd : bd.getFixtureSetDescriptions() ) {
                    FixtureSet fs = new FixtureSet( bi );
                    bi.getFixtureSets().add( fs.loadFromDescription( fsd) );
                }

                if (bd.aagDescription != null) {
                    bi.setAagBackground(new AnimatedActorGroup(bd.getAagDescription()));
                }

            }
        }

        Array<JointItemDescription> jointsDesc = desc.getJointsDesc();

        if ( jointsDesc != null ) {
            for ( JointItemDescription jd : jointsDesc ) {
                JointItem ji = JointItemFactory.createFromDescription( jd, this );
                if ( ji != null ) {
                    jointItems.add(ji);
                } else {
                    Gdx.app.log("PhysModel.uploadFromDescription", "unable to create the jointItem:  " + jd.getName() );
                }
            }
        }

    }

    public void save(FileHandle fileHandle) {
        PhysModel.saveToFile( this, fileHandle );
    }

    public Description getDescription() {
        Description desc = new Description();
        desc.setName( getName() );
        if ( backgroundActor != null ) {
            desc.setBackgroundAagDesc( backgroundActor.getDescription() );
        }

        if ( bodyItems.size != 0 ) {
            Array<BodyItemDescription> bdesc = new Array<BodyItemDescription>();
            fillUpBodyDescriptions(bdesc);
            desc.setBodiesDesc(bdesc);
        }

        if ( jointItems.size != 0 ) {
            Array<JointItemDescription> jdesc = new Array<JointItemDescription>();
            fillUpJointDescriptions( jdesc );
            desc.setJointsDesc( jdesc );
        }

        return desc;
    }


    void fillUpBodyDescriptions(Array<BodyItemDescription> bodyDescriptions) {

        for(BodyItem bi : bodyItems) {
            BodyItemDescription bd = new BodyItemDescription();
            bd.setName( bi.getName() );
            BodyDef bdef = getBodyDefFromBody( bi.body );
            bd.setBodyDef( bdef );
            bd.setId( bi.getId() );

            for ( FixtureSet fs : bi.getFixtureSets() ) {
                bd.getFixtureSetDescriptions().add( fs.getDescription() );
            }

            if ( bi.aagBackground != null ) {
                bd.setAagDescription( bi.aagBackground.getDescription() );
            }

            bodyDescriptions.add( bd );
        }
    }

    void fillUpJointDescriptions( Array<JointItemDescription> jointItemDescriptions) {
        for ( JointItem ji : jointItems ) {
            JointItemDescription jd = ji.createJointItemDescription();
            if ( jd != null )
                jointItemDescriptions.add( jd );
        }
    }

    public BodyDef getBodyDefFromBody( Body body ) {

        BodyDef bd = new BodyDef();

        bd.type = body.getType();
        bd.position.set( body.getPosition() );
        bd.angle = body.getAngle();
        bd.linearVelocity.set( body.getLinearVelocity() );
        bd.angularVelocity = body.getAngularVelocity();
        bd.linearDamping = body.getLinearDamping();
        bd.angularDamping = body.getAngularDamping();
        bd.allowSleep = body.isSleepingAllowed();
        bd.awake = body.isAwake();
        bd.fixedRotation = body.isFixedRotation();
        bd.bullet = body.isBullet();
        bd.active = body.isActive();
        bd.gravityScale = body.getGravityScale();

        return bd;
    }


    // ================ getters and setters ==================


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public AnimatedActorGroup getBackgroundActor() {
        return backgroundActor;
    }

    public void setBackgroundActor(AnimatedActorGroup backgroundActor) {
        this.backgroundActor = backgroundActor;
    }

    public Array<BodyItem> getBodyItems() {
        return bodyItems;
    }

    public Array<JointItem> getJointItems() {
        return jointItems;
    }

    // =======================================================


    public BodyItem addNewBodyItem(String name) {
        BodyDef bd = new BodyDef();
        return addBodyItem( name, bd, -1 );
    }

    public BodyItem addBodyItem( String name, BodyDef bodyDef, int id ) {
        BodyItem bi = new BodyItem( id );
        bi.setName( name );
        Body body = PhysWorld.getWorld().createBody( bodyDef );
        bi.setBody( body );
        bodyItems.add(bi);
        return bi;
    }

    public void uploadAtlas() {
        if ( backgroundActor != null )
            backgroundActor.updateTextures();
    }

    @Override
    public String toString() {
        return "Model: " + name;
    }

    public void removeBody( BodyItem bodyItem ) {

        Array<JointItem> jointItemsTmp = new Array<JointItem>();

        for ( JointItem ji : jointItems) {
            if ( ji.getBodyAId() == bodyItem.getId() ||
                    ji.getBodyBId() == bodyItem.getId() ) {
                jointItemsTmp.add( ji );
                continue;
            }
        }

        for ( JointItem ji : jointItemsTmp )
            removeJointItem( ji );

        bodyItems.removeValue( bodyItem, true );
        PhysWorld.getWorld().destroyBody( bodyItem.body );
    }


    public BodyItem findBodyItem( int id ) {
        for ( BodyItem bi : bodyItems) {
            if ( bi.getId() == id )
                return bi;
        }
        return null;
    }

    public BodyItem findBodyItem( Body body ) {
        for ( BodyItem bi : bodyItems) {
            if ( bi.getBody() == body )
                return bi;
        }
        return null;
    }

    public JointItem findJointItem( int id ) {
        for ( JointItem ji : jointItems ) {
            if ( ji.getId() == id)
                return ji;
        }
        return null;
    }

    public JointItem findJointItem( Joint joint) {
        for ( JointItem ji : jointItems ) {
            if ( ji.getJoint() == joint )
                return ji;
        }
        return null;
    }


    public JointItem addNewJointItem( JointItemDescription jiDesc) {
        JointItem ji = JointItemFactory.createFromDescription(jiDesc, this);
        if ( ji == null )
            return null;
        jointItems.add( ji );
        return  ji;
    }


    public void removeJointItem ( JointItem ji ) {
        PhysWorld.getWorld().destroyJoint( ji.getJoint() );
        jointItems.removeValue( ji, true );
    }
    //================ Static ================================

    public static PhysModel loadFromFile(FileHandle fileHandle) {

        Json js = new Json();
        PhysModel physModel = null;

        try {

            Description description = js.fromJson(Description.class, fileHandle);
            physModel = new PhysModel( description );
        } catch ( SerializationException e) {
            Gdx.app.error("PhysModel.loadFromFile", e.getMessage() );
            e.printStackTrace();
        }

        if ( physModel!= null ) {
            Gdx.app.log("PhysModel.loadFromFile", "Model \"" + physModel.getName() + "\" OK");
        }

        physModel.uploadAtlas();

        return physModel;
    }

    public static void saveToFile(PhysModel physModel, FileHandle fileHandle) {

        Json js = new Json();
        boolean ok = true;
        try {

            Description description = physModel.getDescription();
            js.toJson(description, Description.class, fileHandle );
        } catch ( SerializationException e) {
            Gdx.app.error("PhysModel.saveToFile", e.getMessage() );
            ok = false;
            e.printStackTrace();
        }

        if ( ok ) {
            Gdx.app.log("PhysModel.saveToFile", "Model \"" + physModel.getName() +
                    "\"; File: \"" + fileHandle + "\" OK");
        }
    }

    public static FileNameExtensionFilter getFileFilter() {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PhysModel files:", "physmodel");
        return  filter;
    }

    // =======================================================

}
