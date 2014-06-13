package org.skr.physmodel;

import com.badlogic.gdx.math.Vector2;
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

    private ShapeDescription getShapeDescription(Fixture fixture) {

        ShapeDescription shd = new ShapeDescription();
        Shape shape = fixture.getShape();
        switch ( shape.getType() ) {

            case Circle:
                CircleShape csh = ( CircleShape ) shape;
                shd.setPosition(csh.getPosition());
                shd.setRadius(csh.getRadius());
                break;

            case Edge:

                EdgeShape esh = ( EdgeShape ) shape;

                shd.getVertices().add( new Vector2() );
                shd.getVertices().add(new Vector2());

                esh.getVertex1( shd.getVertices().get(0)  );
                esh.getVertex2( shd.getVertices().get(1) );
                   break;

            case Polygon:

                PolygonShape psh = ( PolygonShape ) shape;
                for ( int i = 0; i < psh.getVertexCount(); i++) {
                    Vector2 v = new Vector2();
                    psh.getVertex( i, v);
                    shd.getVertices().add( v );
                }

                break;
            case Chain:
                ChainShape chsh = ( ChainShape ) shape;
                shd.setLooped( chsh.isLooped() );
                for ( int i = 0; i < chsh.getVertexCount(); i++) {
                    Vector2 v = new Vector2();
                    chsh.getVertex( i, v);
                    shd.getVertices().add( v );
                }
                break;
        }
        return shd;
    }

    public FixtureSetDescription getDescription() {

        FixtureSetDescription desc = new FixtureSetDescription();
        desc.setName( name );
        desc.setDensity( density );
        desc.setFriction( friction );
        desc.setShapeType( shapeType );

        int c = fixtures.size;

        for ( int i = 0; i < c; i++) {
            ShapeDescription shd = getShapeDescription( fixtures.get(i) );
            desc.getShapeDescriptions().add( shd );
        }
        return desc;
    }

    public FixtureSet loadFromDescription( FixtureSetDescription desc ) {

        setName( desc.getName() );
        setDensity( desc.getDensity() );
        setRestitution( desc.getRestitution() );
        setFriction( desc.getFriction() );
        setShapeType( desc.getShapeType() );

        for ( ShapeDescription shd : desc.getShapeDescriptions() ) {
            fixtures.add( createFixture( shd ) );
        }

        return this;
    }

    public Fixture updateFixture(Fixture oldFixture, ShapeDescription shd ) {
        int indexOf = fixtures.indexOf( oldFixture, true );
        if ( indexOf < 0)
            return oldFixture;
        body.destroyFixture( oldFixture );

        Fixture fixture = createFixture ( shd );
        fixtures.set(indexOf, fixture);

        return fixture;
    }

    public Fixture createFixture ( ShapeDescription shd ) {
        FixtureDef fixtureDef = new FixtureDef();

        Shape shape = new CircleShape();

        switch ( shapeType ) {
            case Circle:
                shape = createCircleShape( shd );
                break;
            case Edge:
                shape  = createEdgeShape( shd );
                break;
            case Polygon:
                shape = createPolygonShape( shd );
                break;
            case Chain:
                shape = createChainShape( shd );
                break;
        }

        fixtureDef.density = density;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;
        fixtureDef.shape = shape;

        return body.createFixture( fixtureDef );

    }

    private Shape createCircleShape( ShapeDescription shd ) {

        CircleShape sh = new CircleShape();
        sh.setPosition( shd.getPosition() );
        sh.setRadius( shd.getRadius() );

        return sh;
    }

    private Shape createEdgeShape( ShapeDescription shd ) {

        EdgeShape sh = new EdgeShape();
        sh.set( shd.getVertices().get(0),  shd.getVertices().get(1) );
        return sh;
    }

    private Shape createPolygonShape( ShapeDescription shd ) {
        PolygonShape sh = new PolygonShape();
        sh.set( shd.getVertices().toArray() );
        return sh;
    }

    private Shape createChainShape( ShapeDescription shd ) {
        ChainShape sh = new ChainShape();
        if ( shd.isLooped() ) {
            sh.createLoop( shd.getVertices().toArray() );
        } else {
            sh.createChain( shd.getVertices().toArray() );
        }
        return sh;
    }
}
