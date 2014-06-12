package org.skr.physmodel.animatedactorgroup;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

/**
 * Created by rat on 11.06.14.
 */
public class FixtureSet {

    String name = "";
    Shape.Type shapeType = Shape.Type.Polygon;
    public float friction = 0.2f;
    public float restitution = 0;
    public float density = 0.1f;


    Array< Fixture> fixtures = new Array<Fixture>();
    Body body;

    public FixtureSet( Body body) {
        this.body = body;
    }

    public Shape.Type getShapeType() {
        return shapeType;
    }

    public void setShapeType(Shape.Type shapeType) {
        this.shapeType = shapeType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getFriction() {
        return friction;
    }

    public void setFriction(float friction) {
        this.friction = friction;
        updateFriction();
    }

    public float getRestitution() {
        return restitution;
    }

    public void setRestitution(float restitution) {
        this.restitution = restitution;
        updateRestitution();
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
        updateDensity();
    }

    public Array<Fixture> getFixtures() {
        return fixtures;
    }

    public void setFixtures(Array<Fixture> fixtures) {
        this.fixtures = fixtures;
    }

    public Body getBody() {
        return body;
    }

    public void removeAllFixtures() {
        for ( Fixture f : fixtures ) {
            body.destroyFixture( f );
        }
        fixtures.clear();
    }

    private void updateFriction() {
        for ( Fixture f : fixtures)
            f.setFriction( friction );
    }

    private void updateRestitution() {
        for ( Fixture f : fixtures)
            f.setRestitution( restitution );
    }

    private void updateDensity() {
        for ( Fixture f : fixtures)
            f.setDensity( density );
    }


}
