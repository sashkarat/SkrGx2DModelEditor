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

    }

    private String name = "";
    private AnimatedActorGroup backgroundActor;
    private Array<BodyItem> bodyItems = new Array<BodyItem>();

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
                BodyItem bi = addBodyItem( bd.getName(), bd.bodyDef );

                for ( FixtureSetDescription fsd : bd.getFixtureSetDescriptions() ) {
                    FixtureSet fs = new FixtureSet( bi );
                    bi.getFixtureSets().add( fs.loadFromDescription( fsd) );
                }



                if (bd.aagDescription != null) {
                    bi.setAagBackground(new AnimatedActorGroup(bd.getAagDescription()));
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

        return desc;
    }


    void fillUpBodyDescriptions(Array<BodyItemDescription> bodyDescriptions) {

        for(BodyItem bi : bodyItems) {
            BodyItemDescription bd = new BodyItemDescription();
            bd.setName( bi.getName() );
            BodyDef bdef = getBodyDefFromBody( bi.body );
            bd.setBodyDef( bdef );

            for ( FixtureSet fs : bi.getFixtureSets() ) {
                bd.getFixtureSetDescriptions().add( fs.getDescription() );
            }


            if ( bi.aagBackground != null ) {
                bd.setAagDescription( bi.aagBackground.getDescription() );
            }

            bodyDescriptions.add( bd );
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

    // =======================================================


    public BodyItem addNewBodyItem(String name) {
        BodyDef bd = new BodyDef();
        return addBodyItem( name, bd );
    }

    public BodyItem addBodyItem( String name, BodyDef bodyDef ) {
        BodyItem bi = new BodyItem();
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

        int indexOf = bodyItems.indexOf( bodyItem, true );
        if ( indexOf < 0 )
            return;
        bodyItems.removeIndex( indexOf );

        PhysWorld.getWorld().destroyBody( bodyItem.body );
        //TODO: check joint list

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

    // =======================================================

    public static FileNameExtensionFilter getFileFilter() {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PhysModel files", "physmodel");
        return  filter;
    }

}
