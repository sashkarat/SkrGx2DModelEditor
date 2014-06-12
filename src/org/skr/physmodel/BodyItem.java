package org.skr.physmodel;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import org.skr.physmodel.animatedactorgroup.AnimatedActorGroup;
import org.skr.physmodel.animatedactorgroup.FixtureSet;

/**
 * Created by rat on 11.06.14.
 */
public class BodyItem {
    String name = "";
    Body body = null;
    AnimatedActorGroup aagBackground;
    Array< FixtureSet > fixtureSets = new Array<FixtureSet>();

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AnimatedActorGroup getAagBackground() {
        return aagBackground;
    }

    public void setAagBackground(AnimatedActorGroup aagBackground) {
        this.aagBackground = aagBackground;
    }

    public FixtureSet addNewFixtureSet( String name ) {
        FixtureSet fs = new FixtureSet( body );
        fs.setName( name );
        fixtureSets.add( fs );
        return fs;
    }

    public void removeFixtureSet( FixtureSet fixtureSet ) {
        int indexOf = fixtureSets.indexOf( fixtureSet, true );
        if ( indexOf < 0 )
            return;
        fixtureSets.removeIndex( indexOf );
        fixtureSet.removeAllFixtures();
    }

}
