package org.skr.PhysModelEditor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Transform;
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

    final Matrix4 tmpMtx = new Matrix4();
    final Vector2 tmpVec = new Vector2();

    private void drawBodyItem(Batch batch, float parentAlpha, BodyItem bodyItem) {
        tmpVec.set( bodyItem.getBody().getPosition() );
        float angle = bodyItem.getBody().getAngle();

        tmpMtx.set( batch.getTransformMatrix() );
        PhysWorld.get().toView(tmpVec);
        batch.getTransformMatrix().translate( tmpVec.x, tmpVec.y, 0 );
        batch.getTransformMatrix().rotate( 0,0,1, angle );


        AnimatedActorGroup aag = bodyItem.getAagBackground();
        if ( aag != null ) {
            aag.draw( batch, parentAlpha );
        }

        batch.setTransformMatrix( tmpMtx );
    }

    @Override
    public void act(float delta) {
        if ( model == null )
            return;
        if ( model.getBackgroundActor() != null )
            model.getBackgroundActor().act( delta );
    }


}
