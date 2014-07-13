package org.skr.PhysModelEditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

/**
 * Created by rat on 08.06.14.
 */
public class PhysWorld {

    static private class WorldDebugRenderer extends Box2DDebugRenderer {
        Matrix4 box2dProjection = new Matrix4();


        public WorldDebugRenderer(boolean drawBodies, boolean drawJoints, boolean drawAABBs, boolean drawInactiveBodies, boolean drawVelocities, boolean drawContacts) {
            super(drawBodies, drawJoints, drawAABBs, drawInactiveBodies, drawVelocities, drawContacts);
        }

        public void render( World world, Stage stage, float scaleFactor) {
            box2dProjection.set( stage.getBatch().getProjectionMatrix() );
            box2dProjection.scl( scaleFactor );
            render(world, box2dProjection );
        }


        @Override
        protected void renderBody(Body body) {
            super.renderBody(body);
            renderer.setColor(1, 1, 0, 1);
            float x = body.getWorldCenter().x;
            float y = body.getWorldCenter().y;
            renderer.rect( x - 0.004f, y - 0.004f, 0.008f, 0.008f);

//            Array<Fixture> fixtureList = body.getFixtureList();
//
//            for ( Fixture fixture : fixtureList ) {
//                if ( fixture.getType() == Shape.Type.Polygon ) {
//                    drawPolygonShape( (PolygonShape) fixture.getShape() );
//                }
//            }
        }

//        private static Vector2 aV = new Vector2();
//        private static Vector2 bV = new Vector2();
//
//        protected void drawPolygonShape( PolygonShape psh ) {
//            int c = psh.getVertexCount();
//            for ( int i = 0; i < c; i++ ) {
//                psh.getVertex( i,  aV );
//                psh.getVertex( (i+1) % c, bV);
//
//                renderer.line(aV, bV);
//            }
//        }
    }

    World world;
    Vector2 gravity = new Vector2( 0, -9.8f );
    float scale = 10;
    float timing = 1f/120f;
    WorldDebugRenderer debugRenderer;
    final Vector2 tmpVect = new Vector2();


    static PhysWorld instance;

    private PhysWorld(float scale) {
        world = new World( gravity, true);
        instance = this;
        this.scale = scale;
        debugRenderer = new WorldDebugRenderer(true, true, false, true, true, true);
    }


    public static PhysWorld create( float scale ) {
        new PhysWorld( scale );
        return get();
    }

    public static PhysWorld get() {
        return instance;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public static World getWorld() {
        return get().world;
    }

    public void debugRender( Stage stage) {
        debugRenderer.render( world, stage, scale);
    }

    public void step() {
       world.step( timing, 8, 8);
    }

    public float toView(float v) {
        return v * scale;
    }

    public Vector2 toView(Vector2 v) {
        return v.scl( scale );
    }

    public Vector2 physToView(Vector2 v) {
        return tmpVect.set(v).scl( scale );
    };

    public float toPhys(float v) {
        return v / scale;
    }

    public Vector2 toPhys( Vector2 v) {
        return v.scl( 1f / scale );
    }

    public Vector2 viewToPhys(Vector2 v) {
        return tmpVect.set(v).scl( 1f / scale );
    }

    public float unTime( float value ) {
        return value / timing;
    }

    public void act() {
        world.step( timing, 10, 8);
    }

}
