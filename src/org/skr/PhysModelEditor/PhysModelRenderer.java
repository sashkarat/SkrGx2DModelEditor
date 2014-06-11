package org.skr.PhysModelEditor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import org.skr.physmodel.BodyItem;
import org.skr.physmodel.animatedactorgroup.AnimatedActorGroup;
import org.skr.physmodel.PhysModel;

/**
 * Created by rat on 02.06.14.
 */

public class PhysModelRenderer extends Actor {

    PhysModel model;


    public PhysModel getModel() {
        return model;
    }

    public void setModel(PhysModel model) {
        this.model = model;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if ( model == null )
            return;
        if ( model.getBackgroundActor() != null )
            model.getBackgroundActor().draw( batch, parentAlpha );

        for (BodyItem bi :  model.getBodyItems() ) {
            drawBodyItem(batch, parentAlpha, bi);
        }
    }


    private void drawBodyItem(Batch batch, float parentAlpha, BodyItem bodyItem) {
        AnimatedActorGroup aag = bodyItem.getAagBackground();
        if ( aag != null ) {
            aag.draw( batch, parentAlpha );
        }
    }

    @Override
    public void act(float delta) {
        if ( model == null )
            return;
        if ( model.getBackgroundActor() != null )
            model.getBackgroundActor().act( delta );
    }


}
