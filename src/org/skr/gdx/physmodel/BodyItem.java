package org.skr.gdx.physmodel;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import org.skr.gdx.PhysWorld;
import org.skr.gdx.utils.ModShapeRenderer;
import org.skr.gdx.utils.RectangleExt;
import org.skr.gdx.utils.Utils;

/**
 * Created by rat on 11.06.14.
 */
public class BodyItem extends PhysItem {

    private static int g_id = -1;

    private int id = -1;

    public BodyItem( int id ) {
        this.id = genNextId(id);
    }

    public int getId() {
        return id;
    }

    public static int genNextId(int id) {
        int res = -1;
        if ( id < 0 ) {
            res = ++g_id;
        } else {
            res = id;
            if ( g_id < id )
                g_id = id;
        }

        return res;
    }

    Body body = null;

    boolean overrideMassData = false;

    Array< FixtureSet > fixtureSets = new Array<FixtureSet>();

    RectangleExt boundingBox = new RectangleExt();

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

    public RectangleExt getBoundingBox() {
        updateBoundingBox();
        return boundingBox;
    }


    private final static RectangleExt chBBox = new RectangleExt();

    public FixtureSet getFixtureSet( Vector2 viewLocalPoint ) {

        for ( FixtureSet fs : fixtureSets ) {
            for ( Fixture fx : fs.getFixtures() ) {
                chBBox.set(getX(), getY(), 0, 0 );
                switch ( fx.getType() ) {
                    case Circle:
                        getBoundingBoxForCircleShape( chBBox, (CircleShape) fx.getShape() );
                        break;
                    case Edge:
                        getBoundingBoxForEdgeShape( chBBox, (EdgeShape) fx.getShape() );
                        break;
                    case Polygon:
                        getBoundingBoxForPolygonShape( chBBox, (PolygonShape) fx.getShape());
                        break;
                    case Chain:
                        getBoundingBoxForChainShape( chBBox, (ChainShape) fx.getShape() );
                        break;
                }

                if ( chBBox.contains( viewLocalPoint ) )
                    return fs;
            }
        }
        return null;
    };

    private void updateTransform() {
        if ( body == null )
            return;
        Vector2 pos = PhysWorld.get().physToView( body.getPosition() );
        float angle = body.getAngle();
        setPosition( pos.x, pos.y );
        setRotation(MathUtils.radiansToDegrees * angle );
    }






    private void updateBoundingBox() {

        boundingBox.set(getX(), getY(), 0, 0 );
        chBBox.set(getX(), getY(), 0, 0 );

        for ( Fixture fs : body.getFixtureList() ) {
            switch ( fs.getType() ) {
                case Circle:
                    getBoundingBoxForCircleShape( chBBox, (CircleShape) fs.getShape() );
                    break;
                case Edge:
                    getBoundingBoxForEdgeShape( chBBox, (EdgeShape) fs.getShape() );
                    break;
                case Polygon:
                    getBoundingBoxForPolygonShape( chBBox, (PolygonShape) fs.getShape());
                    break;
                case Chain:
                    getBoundingBoxForChainShape( chBBox, (ChainShape) fs.getShape() );
                    break;
            }
            chBBox.setX( chBBox.getX() + getX() );
            chBBox.setY( chBBox.getY() + getY() );

            chBBox.set(Utils.getBBox(chBBox, getX() - chBBox.getX(),
                    getY() - chBBox.getY(), getRotation()));
            if ( boundingBox.getWidth() == 0 ) {
                boundingBox.set( chBBox );
            } else {
                boundingBox.set(Utils.getBBox(boundingBox, chBBox));
            }
        }

        if ( aagBackground != null ) {
            chBBox.set(aagBackground.getBoundingBox());
            chBBox.setX( chBBox.getX() + getX() );
            chBBox.setY( chBBox.getY() + getY() );

            chBBox.set(Utils.getBBox(chBBox, getX() - chBBox.getX(),
                    getY() - chBBox.getY(), getRotation()));
            if ( boundingBox.getWidth() == 0 ) {
                boundingBox.set( chBBox );
            } else {
                boundingBox.set(Utils.getBBox(boundingBox, chBBox));
            }
        }

    }

    private void getBoundingBoxForCircleShape(RectangleExt bbox, CircleShape circleShape ) {
        Vector2 pos = circleShape.getPosition();
        float r = circleShape.getRadius();
        PhysWorld.get().toView( pos );
        r = PhysWorld.get().toView( r );
        bbox.set( pos.x - r, pos.y - r, r + r, r + r );
    }

    private static final Vector2 vA = new Vector2();
    private static final Vector2 vB = new Vector2();

    private void getBoundingBoxForEdgeShape(RectangleExt bbox, EdgeShape edgeShape ) {
        edgeShape.getVertex1( vA );
        edgeShape.getVertex2( vB );

        PhysWorld.get().toView( vA );
        PhysWorld.get().toView( vB );

        bbox.set( Utils.getBBox( vA, vB ) );
    }

    private void getBoundingBoxForChainShape( RectangleExt bbox, ChainShape chainShape ) {
        Utils.bBoxProcessingBegin();
        int c = chainShape.getVertexCount();
        for ( int i = 0; i < c; i++) {
            chainShape.getVertex( i, vA);
            PhysWorld.get().toView( vA );
            Utils.bBoxProcessingAddPoint( vA );
        }
        bbox.set( Utils.bBoxProcessingEnd() );
    }

    private void getBoundingBoxForPolygonShape( RectangleExt bbox, PolygonShape polygonShape ) {
        Utils.bBoxProcessingBegin();
        int c = polygonShape.getVertexCount();
        for ( int i = 0; i < c; i++) {
            polygonShape.getVertex(i, vA);
            PhysWorld.get().toView( vA );
            Utils.bBoxProcessingAddPoint( vA );
        }
        bbox.set( Utils.bBoxProcessingEnd() );
    }

    @Override
    public void act(float delta) {
        updateTransform();
        super.act(delta);
//        updateBoundingBox();
    }


    private static ModShapeRenderer modShapeRenderer = null;

    private void drawBoundingBox(Batch batch, float parentAlpha ) {
        if ( modShapeRenderer == null ) {
            modShapeRenderer = new ModShapeRenderer();
        }

        modShapeRenderer.setProjectionMatrix( batch.getProjectionMatrix() );
        modShapeRenderer.setTransformMatrix( batch.getTransformMatrix() );

        batch.end();

        modShapeRenderer.setColor( 1, 1, 1, 1);
        modShapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        modShapeRenderer.rect( boundingBox.getLeft(), boundingBox.getBottom(),
                boundingBox.getWidth(), boundingBox.getHeight() );

        modShapeRenderer.end();

        batch.begin();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

//        drawBoundingBox( batch, parentAlpha );
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
