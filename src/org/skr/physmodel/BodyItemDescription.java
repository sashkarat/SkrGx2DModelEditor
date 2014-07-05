package org.skr.physmodel;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import org.skr.physmodel.animatedactorgroup.AagDescription;

/**
 * Created by rat on 11.06.14.
 */
public class BodyItemDescription extends PhysItemDescription {

    BodyDef bodyDef = new BodyDef();
    int id = -1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    Array< FixtureSetDescription > fixtureSetDescriptions = new Array<FixtureSetDescription>();

    public BodyDef getBodyDef() {
        return bodyDef;
    }

    public void setBodyDef(BodyDef bodyDef) {
        this.bodyDef = bodyDef;
    }

    public Array<FixtureSetDescription> getFixtureSetDescriptions() {
        return fixtureSetDescriptions;
    }

    public void setFixtureSetDescriptions(Array<FixtureSetDescription> fixtureSetDescriptions) {
        this.fixtureSetDescriptions = fixtureSetDescriptions;
    }
}
