package org.skr.gdx.physmodel;

import org.skr.gdx.physmodel.animatedactorgroup.AagDescription;

/**
 * Created by rat on 05.07.14.
 */
public class PhysItemDescription {
    protected AagDescription aagDescription = null;
    protected String name = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AagDescription getAagDescription() {
        return aagDescription;
    }

    public void setAagDescription(AagDescription aagDescription) {
        this.aagDescription = aagDescription;
    }
}
