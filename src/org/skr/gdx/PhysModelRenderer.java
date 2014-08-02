package org.skr.gdx;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Group;
import org.skr.gdx.physmodel.BodyItem;
import org.skr.gdx.physmodel.PhysModel;

/**
 * Created by rat on 02.06.14.
 */

public class PhysModelRenderer extends Group {

    PhysModel model;
    final World world;

    public PhysModelRenderer(World world) {
        this.world = world;
    }

    public PhysModel getModel() {
        return model;
    }

    public void setModel(PhysModel model) {
        this.model = model;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {


        if ( model == null ) {
            return;
        }

        if ( model.getWorld() == world ) {
            for (BodyItem bi :  model.getBodyItems() )
                bi.draw( batch, parentAlpha );
        }

        if ( model.getBackgroundActor() != null ) {
            model.getBackgroundActor().draw( batch, parentAlpha);
        }


    }

    @Override
    public void act(float delta) {
        if ( model == null )
            return;

        if ( model.getWorld() == world ) {
            for (BodyItem bi : model.getBodyItems())
                bi.act(delta);
        }

        if ( model.getBackgroundActor() != null ) {
            model.getBackgroundActor().act( delta );
        }
    }


}
