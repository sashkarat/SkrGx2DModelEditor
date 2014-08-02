package org.skr.gdx.physmodel;

import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Array;

/**
 * Created by rat on 12.06.14.
 */
public class FixtureSetDescription {
    String name = "";
    Shape.Type shapeType = Shape.Type.Polygon;
    public float friction = 0.2f;
    public float restitution = 0;
    public float density = 0.1f;
    Array<ShapeDescription> shapeDescriptions = new Array<ShapeDescription>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Shape.Type getShapeType() {
        return shapeType;
    }

    public void setShapeType(Shape.Type shapeType) {
        this.shapeType = shapeType;
    }

    public float getFriction() {
        return friction;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }

    public float getRestitution() {
        return restitution;
    }

    public void setRestitution(float restitution) {
        this.restitution = restitution;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public Array<ShapeDescription> getShapeDescriptions() {
        return shapeDescriptions;
    }

    public void setShapeDescriptions(Array<ShapeDescription> shapeDescriptions) {
        this.shapeDescriptions = shapeDescriptions;
    }
}
