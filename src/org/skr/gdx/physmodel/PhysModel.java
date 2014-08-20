package org.skr.gdx.physmodel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;
import org.skr.gdx.physmodel.animatedactorgroup.AagDescription;
import org.skr.gdx.physmodel.animatedactorgroup.AnimatedActorGroup;
import org.skr.gdx.PhysWorld;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.UUID;

/**
 * Created by rat on 31.05.14.
 */

public class PhysModel {

    static  public class Description {

        Object userObject;

        String uuid = "";

        String name = "";
        AagDescription backgroundAagDesc = null;
        Array<BodyItemDescription> bodiesDesc = null;
        Array<JointItemDescription> jointsDesc = null;

        @SuppressWarnings("UnusedDeclaration")
        public Object getUserObject() {
            return userObject;
        }

        @SuppressWarnings("UnusedDeclaration")
        public void setUserObject(Object userObject) {
            this.userObject = userObject;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(UUID uuid) {
            this.uuid = uuid.toString();
        }

        public void setUuid( String uuid ) {
            this.uuid = uuid;
        }

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

    private final World world;
    protected UUID uuid;
    protected String name = "";
    private AnimatedActorGroup backgroundActor;
    private Array<BodyItem> bodyItems = new Array<BodyItem>();
    private Array<JointItem> jointItems = new Array<JointItem>();

    TextureAtlas atlas;



    public PhysModel( World world, TextureAtlas atlas ) {
        this.world = world;
        this.atlas = atlas;
        this.uuid = UUID.randomUUID();
    }

    public PhysModel( Description description, World world, TextureAtlas atlas ) {
        this.world = world;
        this.atlas = atlas;
        uploadFromDescription( description );
    }

    public World getWorld() {
        return this.world;
    }

    @SuppressWarnings("UnusedDeclaration")
    public TextureAtlas getAtlas() {
        return atlas;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setAtlas(TextureAtlas atlas) {
        this.atlas = atlas;
    }

    public void changeUuid() {
        this.uuid = UUID.randomUUID();
    }

    public void uploadFromDescription( Description desc ) {
        this.name = desc.getName();
        String strUuid = desc.getUuid();
        if ( strUuid.isEmpty() )
            this.uuid = UUID.randomUUID();
        else
            this.uuid = UUID.fromString( strUuid );

        if ( desc.getBackgroundAagDesc() != null) {
            AnimatedActorGroup aag = new AnimatedActorGroup( desc.getBackgroundAagDesc(), atlas );
            setBackgroundActor( aag );
        }

        for ( BodyItem bi : bodyItems) {
            world.destroyBody( bi.body );
        }
        bodyItems.clear();

        Array<BodyItemDescription> bodiesDesc = desc.getBodyDescriptions();

        if ( bodiesDesc != null ) {

            for (BodyItemDescription bd : bodiesDesc) {
                addBodyItem( bd );
            }
        }

        Array<JointItemDescription> jointsDesc = desc.getJointsDesc();

        if ( jointsDesc != null ) {
            for ( JointItemDescription jd : jointsDesc ) {
                JointItem ji = JointItemFactory.createFromDescription( jd, this, world );
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
        desc.setUuid( this.uuid );
        desc.setName(getName());
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
            BodyItemDescription bd = bi.createBodyItemDescription();
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

    // ================ getters and setters ==================


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
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


    public BodyItem addBodyItem(String name) {
        BodyDef bd = new BodyDef();
        return addBodyItem( name, bd, -1 );
    }

    public BodyItem addBodyItem( String name, BodyDef bodyDef, int id ) {
        BodyItem bi = new BodyItem( id );
        bi.setName( name );
        Body body = world.createBody( bodyDef );
        bi.setBody( body );
        body.setUserData( bi );
        bodyItems.add(bi);
        return bi;
    }

    public BodyItem addBodyItem( BodyItemDescription bd ) {

        BodyItem bi = addBodyItem( bd.getName(), bd.bodyDef, bd.getId() );

        for ( FixtureSetDescription fsd : bd.getFixtureSetDescriptions() ) {
            FixtureSet fs = new FixtureSet( bi );
            bi.getFixtureSets().add( fs.loadFromDescription( fsd) );
        }

        if (bd.aagDescription != null) {
            bi.setAagBackground(new AnimatedActorGroup(bd.getAagDescription(), atlas ));
        }

        if ( bd.isOverrideMassData() ) {
            MassData md = bd.getMassData();
            if ( md != null ) {
                bi.getBody().setMassData( md );
                bi.setOverrideMassData( true );
            }
        }

        return bi;
    }


    public void uploadAtlas() {
        if ( backgroundActor != null )
            backgroundActor.updateTextures( atlas );
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
            }
        }

        for ( JointItem ji : jointItemsTmp )
            removeJointItem( ji );

        bodyItems.removeValue( bodyItem, true );
        world.destroyBody( bodyItem.body );
    }


    public BodyItem findBodyItem( int id ) {
        for ( BodyItem bi : bodyItems) {
            if ( bi.getId() == id )
                return bi;
        }
        return null;
    }

    public BodyItem findBodyItem( Body body ) {

        if ( body.getUserData() == null )
            return null;
        if ( !(body.getUserData() instanceof BodyItem) )
            return  null;

        return (BodyItem) body.getUserData();
    }

    public JointItem findJointItem( int id ) {
        for ( JointItem ji : jointItems ) {
            if ( ji.getId() == id)
                return ji;
        }
        return null;
    }

    public JointItem findJointItem( Joint joint) {
        if ( joint.getUserData() == null )
            return null;
        Object o = joint.getUserData();
        if ( !(o instanceof JointItem ))
            return null;
        return (JointItem) o;
    }


    public JointItem addNewJointItem( JointItemDescription jiDesc) {
        JointItem ji = JointItemFactory.createFromDescription(jiDesc, this, world);
        if ( ji == null )
            return null;
        jointItems.add( ji );
        return  ji;
    }


    public void removeJointItem ( JointItem ji ) {
        world.destroyJoint( ji.getJoint() );
        jointItems.removeValue( ji, true );
    }
    //================ Static ================================

    public static Description loadModelDescription( FileHandle fileHandle ) {
        Json js = new Json();
        Description desc = null;
        try {
            desc = js.fromJson(Description.class, fileHandle);
        } catch ( SerializationException e) {
            Gdx.app.error("PhysModel.loadModelDescription", e.getMessage() );
            e.printStackTrace();
        }

        return desc;
    }


    public static PhysModel loadFromFile( FileHandle fileHandle, TextureAtlas atlas ) {

        PhysModel physModel;

        Description desc = loadModelDescription( fileHandle );

        if ( desc == null ) {
            return null;
        }

        physModel = new PhysModel( desc, PhysWorld.getPrimaryWorld(), atlas );

        Gdx.app.log("PhysModel.loadFromFile", "Model \"" + physModel.getName() + "\" OK");

        physModel.uploadAtlas();

        return physModel;
    }

    public static boolean saveModelDescription( Description desc, FileHandle fileHandle ) {
        Json js = new Json();
        boolean ok = true;
        try {
            js.toJson( desc, Description.class, fileHandle );
        } catch ( SerializationException e) {
            Gdx.app.error("PhysModel.saveDescription", e.getMessage() );
            ok = false;
            e.printStackTrace();
        }

        return ok;
    }

    public static void saveToFile(PhysModel physModel, FileHandle fileHandle) {

        Description desc = physModel.getDescription();

        if ( saveModelDescription( desc, fileHandle ) ) {
            Gdx.app.log("PhysModel.saveToFile", "Model \"" + physModel.getName() +
                    "\"; File: \"" + fileHandle + "\" OK");
        }
    }

    public static FileNameExtensionFilter getFileFilter() {
        return new FileNameExtensionFilter("PhysModel files:", "physmodel");
    }

    public static void destroyPhysics( PhysModel model ) {
        for ( JointItem ji : model.jointItems ) {
            model.getWorld().destroyJoint( ji.getJoint() );
        }

        for ( BodyItem bi : model.bodyItems ) {
            model.getWorld().destroyBody( bi.getBody() );
        }

        model.jointItems.clear();
        model.bodyItems.clear();
    }

    // =======================================================

}
