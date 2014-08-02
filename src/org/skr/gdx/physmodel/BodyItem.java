package org.skr.gdx.physmodel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import org.skr.gdx.PhysWorld;

/**
 * Created by rat on 11.06.14.
 */
public class BodyItem extends PhysItem {

    private static int g_id = -1;

    private int id = -1;

    public BodyItem( int id ) {
        if ( id < 0 ) {
            this.id = ++g_id;
        } else {
            this.id = id;
            if ( g_id < id )
                g_id = id;
        }
    }

    public int getId() {
        return id;
    }

    Body body = null;

    boolean overrideMassData = false;

    Array< FixtureSet > fixtureSets = new Array<FixtureSet>();

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public boolean isOverrideMassData() {
        return overrideMassData;
    }

    public void setOverrideMassData(boolean overrideMassData) {
        this.overrideMassData = overrideMassData;
    }

    public Array<FixtureSet> getFixtureSets() {
        return fixtureSets;
    }

    public void setFixtureSets(Array<FixtureSet> fixtureSets) {
        this.fixtureSets = fixtureSets;
    }

    public FixtureSet addNewFixtureSet( String name ) {
        FixtureSet fs = new FixtureSet( this );
        fs.setName( name );
        fixtureSets.add( fs );
        return fs;
    }

    public void addFixtureSet( FixtureSet fs ) {
        fixtureSets.add( fs );
    }

    public FixtureSet addNewFixtureSet( FixtureSetDescription fsd ) {
        FixtureSet fs = new FixtureSet( fsd, this );
        fixtureSets.add( fs );
        return fs;
    }

    public void removeFixtureSet( FixtureSet fixtureSet ) {
        int indexOf = fixtureSets.indexOf( fixtureSet, true );
        if ( indexOf < 0 )
            return;
        fixtureSets.removeIndex( indexOf );
        fixtureSet.removeAllFixtures();
    }

    public void updateTransform() {
        if ( body == null )
            return;
        Vector2 pos = PhysWorld.get().physToView( body.getPosition() );
        float angle = body.getAngle();
        setPosition( pos.x, pos.y );
        setRotation(MathUtils.radiansToDegrees * angle );

    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        updateTransform();
        super.draw(batch, parentAlpha);
    }

    static final Matrix3 mtx = new Matrix3();

    public static Vector2 stageToBodyItemLocal( BodyItem bodyItem, Vector2 coord ) {
        mtx.idt();

        mtx.translate( bodyItem.getX(), bodyItem.getY() );
        mtx.rotate( bodyItem.getRotation() );

        coord.mul( mtx.inv() );
        return coord;
    }


    public BodyItemDescription createBodyItemDescription() {

        BodyItemDescription bd = new BodyItemDescription();
        bd.setName( getName() );
        BodyDef bodyDef = getBodyDefFromBody( body );
        bd.setBodyDef( bodyDef );
        bd.setId( getId() );

        for ( FixtureSet fs : fixtureSets ) {
            bd.getFixtureSetDescriptions().add( fs.getDescription() );
        }

        if ( getAagBackground() != null ) {
            bd.setAagDescription( getAagBackground().getDescription() );
        }

        if ( overrideMassData ) {
            bd.setOverrideMassData( true );
            bd.setMassData( body.getMassData() );
        }

        return bd;
    }


    // ============= STATIC ================================

    public static BodyDef getBodyDefFromBody( Body body ) {

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

}
