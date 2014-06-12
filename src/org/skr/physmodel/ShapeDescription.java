package org.skr.physmodel;

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
    boolean hasVertex0 = false;
    boolean hasVertex3 = false;

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
        this.position = position;
    }

    public boolean isLooped() {
        return isLooped;
    }

    public void setLooped(boolean isLooped) {
        this.isLooped = isLooped;
    }

    public boolean isHasVertex0() {
        return hasVertex0;
    }

    public void setHasVertex0(boolean hasVertex0) {
        this.hasVertex0 = hasVertex0;
    }

    public boolean isHasVertex3() {
        return hasVertex3;
    }

    public void setHasVertex3(boolean hasVertex3) {
        this.hasVertex3 = hasVertex3;
    }
}
