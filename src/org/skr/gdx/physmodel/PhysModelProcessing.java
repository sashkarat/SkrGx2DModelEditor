package org.skr.gdx.physmodel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.MassData;
import org.skr.gdx.physmodel.animatedactorgroup.AagDescription;

/**
 * Created by rat on 26.08.14.
 */
public class PhysModelProcessing {

    public static enum MirrorDirection {
        Vertical,
        Horizontal
    }

    public static PhysModel.Description mirrorModelDescription(PhysModel.Description desc, MirrorDirection dir ) {

        if ( desc.getBackgroundAagDesc() != null ) {
            mirrorAagDescription( desc.getBackgroundAagDesc(), dir );
        }

        for ( BodyItemDescription bdesc : desc.getBodyDescriptions() ) {
            mirrorBodyItemDescription( bdesc, dir );
        }

        for ( JointItemDescription jdesc : desc.getJointsDesc() ) {
            mirrorJointItemDescription( jdesc, dir );
        }

        return desc;
    }

    public static AagDescription mirrorAagDescription( AagDescription desc,MirrorDirection dir  ) {

        desc.setKeepAspectRatio( false );

        switch (dir) {
            case Vertical:
                desc.setX(  - desc.getX() );
                desc.setWidth( - desc.getWidth() );
                desc.setRotation( 360 - desc.getRotation() );
                break;
            case Horizontal:
                desc.setY( - desc.getY() );
                desc.setHeight( - desc.getHeight() );
                desc.setRotation( 360 - desc.getRotation() );
                break;
        }

        for ( AagDescription chDesc : desc.getChildren() ) {
            mirrorAagDescription( chDesc, dir );
        }
        return desc;
    }

    public static BodyItemDescription mirrorBodyItemDescription( BodyItemDescription desc, MirrorDirection dir  ) {

        BodyDef bdef = desc.getBodyDef();
        switch (dir) {
            case Vertical:
                bdef.position.x =  - bdef.position.x;
                break;
            case Horizontal:
                bdef.position.y = - bdef.position.y;
                break;
        }
        bdef.angle = 2 * (float) Math.PI - bdef.angle;

        if ( desc.getAagDescription() != null  )
            mirrorAagDescription( desc.getAagDescription(), dir );

        for ( FixtureSetDescription fsdesc : desc.getFixtureSetDescriptions() ) {
            mirrorFixtureSetDescription( fsdesc, dir );
        }

        if ( desc.isOverrideMassData() ) {
            MassData md =  desc.getMassData();
            switch (dir) {
                case Vertical:
                    md.center.x = - md.center.x;
                    break;
                case Horizontal:
                    md.center.y = - md.center.y;
                    break;
            }
        }

        return desc;
    }

    public static FixtureSetDescription mirrorFixtureSetDescription( FixtureSetDescription fsDesc, MirrorDirection dir ) {
        for ( ShapeDescription shdesc : fsDesc.getShapeDescriptions() ) {
            switch (dir) {
                case Vertical:
                    shdesc.getPosition().x =  - shdesc.getPosition().x;
                    break;
                case Horizontal:
                    shdesc.getPosition().y =  - shdesc.getPosition().y;
                    break;
            }

            for ( Vector2 point : shdesc.getVertices() ) {
                switch (dir) {
                    case Vertical:
                        point.x = - point.x;
                        break;
                    case Horizontal:
                        point.y = - point.y;
                        break;
                }
            }
        }
        return fsDesc;
    }

    public static JointItemDescription mirrorJointItemDescription( JointItemDescription desc, MirrorDirection dir  ) {

        Vector2 anchorA = desc.getAnchorA();
        Vector2 anchorB = desc.getAnchorB();
        Vector2 groundAnchorA = desc.getGroundAnchorA();
        Vector2 groundAnchorB = desc.getGroundAnchorB();
        Vector2 axis = desc.getAxis();
        Vector2 target = desc.getTarget();

        switch (dir) {
            case Vertical:
                anchorA.x = - anchorA.x;
                anchorB.x = - anchorB.x;
                groundAnchorA.x = - groundAnchorA.x;
                groundAnchorB.x = - groundAnchorB.x;
                axis.x = - axis.x;
                target.x = - target.x;
                break;
            case Horizontal:
                anchorA.y = - anchorA.y;
                anchorB.y = - anchorB.y;
                groundAnchorA.y = - groundAnchorA.y;
                groundAnchorB.y = - groundAnchorB.y;
                axis.y = - axis.y;
                target.y = - target.y;
                break;
        }

        return desc;
    }
}
