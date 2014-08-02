package org.skr.gdx.physmodel;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.utils.Array;

/**
 * Created by rat on 11.06.14.
 */
public class BodyItemDescription extends PhysItemDescription {

    BodyDef bodyDef = new BodyDef();
    int id = -1;
    boolean overrideMassData = false;
    Array< FixtureSetDescription > fixtureSetDescriptions = new Array<FixtureSetDescription>();
    MassData massData;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BodyDef getBodyDef() {
        return bodyDef;
    }

    public void setBodyDef(BodyDef bodyDef) {
        this.bodyDef = bodyDef;
    }

    public boolean isOverrideMassData() {
        return overrideMassData;
    }

    public void setOverrideMassData(boolean overrideMassData) {
        this.overrideMassData = overrideMassData;
    }

    public MassData getMassData() {
        return massData;
    }

    public void setMassData(MassData massData) {
        this.massData = massData;
    }

    public Array<FixtureSetDescription> getFixtureSetDescriptions() {
        return fixtureSetDescriptions;
    }

    public void setFixtureSetDescriptions(Array<FixtureSetDescription> fixtureSetDescriptions) {
        this.fixtureSetDescriptions = fixtureSetDescriptions;
    }
}
