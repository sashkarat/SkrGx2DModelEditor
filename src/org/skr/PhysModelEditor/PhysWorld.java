package org.skr.PhysModelEditor;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by rat on 08.06.14.
 */
public class PhysWorld {

    World world;
    Vector2 gravity = new Vector2( 0, -9.8f );

    static PhysWorld instance;

    private PhysWorld() {
        world = new World( gravity, true);
        instance = this;
    }

    public static PhysWorld create() {
        new PhysWorld();
        return get();
    }

    public static PhysWorld get() {
        if ( instance == null ) {
            create();
        }
        return instance;
    }

    public static World getWorld() {
        return get().world;
    }

}
