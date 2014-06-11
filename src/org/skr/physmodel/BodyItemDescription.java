package org.skr.physmodel;

import com.badlogic.gdx.physics.box2d.BodyDef;
import org.skr.physmodel.animatedactorgroup.AagDescription;

/**
 * Created by rat on 11.06.14.
 */
public class BodyItemDescription {
    BodyDef bodyDef = new BodyDef();
    String name = "";
    AagDescription aagDescription = null;

    //TODO: add fixtures

    public BodyDef getBodyDef() {
        return bodyDef;
    }

    public void setBodyDef(BodyDef bodyDef) {
        this.bodyDef = bodyDef;
    }

    public AagDescription getAagDescription() {
        return aagDescription;
    }

    public void setAagDescription(AagDescription aagDescription) {
        this.aagDescription = aagDescription;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
