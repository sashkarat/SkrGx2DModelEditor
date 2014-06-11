package org.skr.physmodel;

import com.badlogic.gdx.physics.box2d.Body;
import org.skr.physmodel.animatedactorgroup.AnimatedActorGroup;

/**
 * Created by rat on 11.06.14.
 */
public class BodyItem {
    String name = "";
    Body body = null;

    AnimatedActorGroup aagBackground;

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
}
