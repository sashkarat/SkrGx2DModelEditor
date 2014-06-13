package org.skr.PhysModelEditor;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Created by rat on 08.06.14.
 */
public class PhysWorld {

    private class WorldDebugRenderer extends Box2DDebugRenderer {
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

        }
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
        debugRenderer = new WorldDebugRenderer(true, true, false, false, false, true);
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
