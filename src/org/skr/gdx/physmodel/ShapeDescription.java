package org.skr.gdx.physmodel;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by rat on 12.06.14.
 */

public class ShapeDescription {
    Array<Vector2> vertices  = new Array<Vector2>();
    float radius = 0.1f;
    Vector2 position = new Vector2();
    boolean isLooped = false;

    public Array<Vector2> getVertices() {
        return vertices;
    }

    public void setVertices(Array<Vector2> vertices) {
        this.vertices = vertices;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position.set( position );
    }

    public boolean isLooped() {
        return isLooped;
    }

    public void setLooped(boolean isLooped) {
        this.isLooped = isLooped;
    }

}
