package org.skr.PhysModelEditor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

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

        for (PhysModel.BodyItem bi :  model.getBodyItems() ) {
            drawBodyItem(batch, parentAlpha, bi);
        }
    }


    private void drawBodyItem(Batch batch, float parentAlpha, PhysModel.BodyItem bodyItem) {

    }


    @Override
    public void act(float delta) {
        if ( model == null )
            return;
        if ( model.getBackgroundActor() != null )
            model.getBackgroundActor().act( delta );
    }


}
