package org.skr.PhysModelEditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import org.skr.physmodel.BodyItem;
import org.skr.physmodel.animatedactorgroup.AnimatedActorGroup;
import org.skr.physmodel.PhysModel;

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
        if ( model == null )
            return;

        if ( model.getWorld() != world )
            return;

        for (BodyItem bi :  model.getBodyItems() ) {
            bi.draw( batch, parentAlpha );
        }
    }

    @Override
    public void act(float delta) {
        if ( model == null )
            return;
        for ( BodyItem bi : model.getBodyItems() )
            bi.act( delta );

    }


}
