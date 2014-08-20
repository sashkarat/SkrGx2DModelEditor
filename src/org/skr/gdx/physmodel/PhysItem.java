package org.skr.gdx.physmodel;

import com.badlogic.gdx.scenes.scene2d.Group;
import org.skr.gdx.physmodel.animatedactorgroup.AnimatedActorGroup;

/**
 * Created by rat on 05.07.14.
 */
public class PhysItem extends Group {

    protected String name = "";
    protected AnimatedActorGroup aagBackground;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AnimatedActorGroup getAagBackground() {
        return aagBackground;
    }

    public void removeAagBackground() {
        if ( this.aagBackground != null )
            removeActor( this.aagBackground);
        this.aagBackground = null;
    }

    public void setAagBackground(AnimatedActorGroup aagBackground) {

        if ( this.aagBackground != null  )
            removeActor( this.aagBackground );
        this.aagBackground = aagBackground;
        addActor( aagBackground );
    }

    @Override
    public String toString() {
        return name;
    }
}
