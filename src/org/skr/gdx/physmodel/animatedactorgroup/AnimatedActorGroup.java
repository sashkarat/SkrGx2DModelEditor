package org.skr.gdx.physmodel.animatedactorgroup;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;
import org.skr.gdx.utils.ModShapeRenderer;
import org.skr.gdx.utils.RectangleExt;
import org.skr.gdx.utils.Utils;

import java.util.Stack;

/**
 * Created by rat on 31.05.14.
 *
 */
public class AnimatedActorGroup extends Group {

    private static ModShapeRenderer modShapeRenderer = null;

    public interface RenderableUserObject {
        public void render( AnimatedActorGroup aag, Batch batch );
    }


    public interface GlobalSizeChangedListener {
        public void sizeChanged(AnimatedActorGroup aag);
    }

    private static GlobalSizeChangedListener globalSizeChangedListener;

    public static void setGlobalSizeChangedListener( GlobalSizeChangedListener listener ) {
        globalSizeChangedListener = listener;
    }


    String textureName = "";
    float frameDuration = 0.02f;
    Animation.PlayMode playMode = Animation.PlayMode.LOOP;
    boolean keepAspectRatio = false;
    boolean drawable = true;

    private TextureRegion currentRegion;
    private Animation animation;
    private float stateTime = 0;
    private int count;

    RectangleExt boundingBox = new RectangleExt();

    public AnimatedActorGroup( TextureAtlas atlas ) {
        updateTextures( atlas );
    }

    public AnimatedActorGroup( AagDescription desc, TextureAtlas atlas  ) {
        uploadFromDescription( desc, atlas );
    }


    // ============ Getters and Setters ===================================

    public Animation.PlayMode getPlayMode() {
        return playMode;
    }

    public void setPlayMode(Animation.PlayMode playMode) {
        this.playMode = playMode;
    }

    public String getTextureName() {
        return textureName;
    }

    public void setTextureName(String textureName) {
        this.textureName = textureName;
    }

    public float getFrameDuration() {
        return frameDuration;
    }

    public void setFrameDuration(float frameDuration) {
        this.frameDuration = frameDuration;
    }

    public boolean isKeepAspectRatio() {
        return keepAspectRatio;
    }

    public void setKeepAspectRatio(boolean keepAspectRatio) {
        this.keepAspectRatio = keepAspectRatio;
        checkAspectRatio();
    }

    public boolean isDrawable() {
        return drawable;
    }

    public void setDrawable(boolean drawable) {
        this.drawable = drawable;
    }

    // ================================================================


    public AagDescription getDescription() {

        AagDescription desc = new AagDescription();

        Array<AagDescription> children = desc.getChildren();

        for ( Actor a : getChildren() ) {
            if ( a instanceof AnimatedActorGroup ) {
                AnimatedActorGroup aag = (AnimatedActorGroup) a;
                children.add( aag.getDescription() );
            }
        }

        desc.setName( getName() );
        desc.setTextureName(getTextureName());
        desc.setPlayMode(getPlayMode());
        desc.setFrameDuration(getFrameDuration());
        desc.setWidth(getWidth());
        desc.setHeight(getHeight());
        desc.setX(getX());
        desc.setY(getY());
        desc.setRotation( getRotation() );
        desc.setDrawable( isDrawable() );
        desc.setKeepAspectRatio( isKeepAspectRatio() );

        return desc;
    }

    public void uploadFromDescription( AagDescription desc, TextureAtlas atlas ) {

        Actor[] old_children = getChildren().toArray();
        for ( Actor a : old_children)
            removeActor( a );

        for ( AagDescription d : desc.getChildren() )
            addChild( new AnimatedActorGroup( d, atlas ) );

        setName( desc.getName() );
        setFrameDuration( desc.getFrameDuration() );
        setPlayMode( desc.getPlayMode() );
        setWidth( desc.getWidth() );
        setHeight( desc.getHeight() );
        setX( desc.getX() );
        setY( desc.getY() );
        setRotation( desc.getRotation() );
        setDrawable(desc.isDrawable());
        setKeepAspectRatio( desc.isKeepAspectRatio() );
        count = getChildren().size;

        setTextureName( desc.getTextureName() );

        updateTextures( atlas );


    }

    public boolean updateTextures( TextureAtlas atlas ) {

        if ( animation != null )
            animation = null;
        if ( currentRegion != null )
            currentRegion = null;

        if ( textureName.isEmpty() ) {
            return false;
        }

        stateTime = 0;

        if ( atlas != null ) {

            Array<TextureAtlas.AtlasRegion> tex = atlas.findRegions(textureName);
            animation = new Animation(frameDuration, tex, playMode);
            stateTime = 0;
            int l = animation.getKeyFrames().length;
            if ( l == 0 ) {
                animation = null;
                currentRegion = null;
                return false;
            }
            currentRegion = animation.getKeyFrame(stateTime);

        }

        checkAspectRatio();

        SnapshotArray<Actor> children =  getChildren();

        for ( Actor a: children ) {
            if ( a instanceof AnimatedActorGroup ) {
                if ( !((AnimatedActorGroup) a).updateTextures( atlas ) )
                    continue;
            }
        }

        return true;
    }

    @Override
    protected void sizeChanged() {
        if ( globalSizeChangedListener != null ) {
            globalSizeChangedListener.sizeChanged( this );
        }
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        checkAspectRatio();
    }

    @Override
    public void setHeight(float height) {
        if ( keepAspectRatio )
            height = getHeightByWidth( height );
        super.setHeight(height);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        checkAspectRatio();
    }

    @Override
    public void sizeBy(float size) {
        super.sizeBy(size);
        checkAspectRatio();
    }

    @Override
    public void sizeBy(float width, float height) {
        super.sizeBy(width, height);
        checkAspectRatio();
    }

    @Override
    public void setBounds(float x, float y, float width, float height) {
        super.setBounds(x, y, width, height);
        checkAspectRatio();
    }

    void checkAspectRatio() {

        if ( !keepAspectRatio )
            return;
        if ( animation == null ) {
            return;
        }

        setHeight( getHeightByWidth( getHeight() ) );

    }

    public float getHeightByWidth( float height) {
        if ( !keepAspectRatio )
            return height;
        if ( animation == null ) {
            return height;
        }

        TextureRegion reg = animation.getKeyFrame(0);

        float aspectRatio = (float) reg.getRegionHeight() / (float)reg.getRegionWidth();

        return getWidth() * aspectRatio ;
    }

    public void addChild( AnimatedActorGroup ag ) {
        addActor( ag );
        count++;
    }

    public void removeChild ( AnimatedActorGroup ag ) {
        removeActor( ag );
        count--;
    }

    public int getChildrenCount() {
        return count;
    }

    public AnimatedActorGroup getChild( int index ) {
        if ( index < 0 || index >= getChildren().size )
            return null;

        Actor a = getChildren().get( index );

        if ( !( a instanceof AnimatedActorGroup) )
            return null;

        return (AnimatedActorGroup) a;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if ( animation == null )
            return;

        stateTime += delta;
        currentRegion = animation.getKeyFrame( stateTime );

//        updateBoundingBox();
    }


    public RectangleExt getBoundingBox() {
        updateBoundingBox();
        return boundingBox;
    }

    public boolean contains( Vector2 localPoint ) {

        float w2 = Math.abs( getWidth() / 2 );

        if( localPoint.x <  - w2 ) {
            return false;
        }
        if ( localPoint.x > w2 ) {
            return false;
        }

        float h2 = Math.abs( getHeight() /2 );

        if ( localPoint.y < -h2 )
            return false;

        if ( localPoint.y > h2 )
            return false;

        return true;
    }


    public AnimatedActorGroup getAag(Vector2 localCoord) {

        AnimatedActorGroup aagRes;

        Vector2 chCoord = new Vector2();

        for ( Actor a : getChildren() ) {
            if (!( a instanceof AnimatedActorGroup) )
                continue;
            AnimatedActorGroup aag = (AnimatedActorGroup) a;
            chCoord.set(localCoord);
            aag.parentToLocalCoordinates(chCoord);
            aagRes = aag.getAag(chCoord);
            if ( aagRes != null )
                return aagRes;
        }
        if ( contains( localCoord ) ) {
            return this;
        }
        return null;
    }

    private final static RectangleExt box = new RectangleExt();

    private final RectangleExt chBBox = new RectangleExt();

    private void updateBoundingBox() {

        box.set( getX() - getWidth()/2, getY() - getHeight()/2, getWidth(), getHeight() );

        boundingBox.set(Utils.getBBox(box, getWidth() / 2, getHeight() / 2, getRotation()));

        for ( int i = 0; i < getChildren().size; i++) {
            AnimatedActorGroup aagCh = (AnimatedActorGroup) getChildren().get( i );

            chBBox.set(aagCh.getBoundingBox());
            chBBox.setX( chBBox.getX() + getX() );
            chBBox.setY( chBBox.getY() + getY() );

            chBBox.set( Utils.getBBox(chBBox, getX() - chBBox.getX(), getY() - chBBox.getY(), getRotation()) );

            boundingBox.set( Utils.getBBox( boundingBox, chBBox ) );
        }

    }


    private void drawBoundingBox( Batch batch, float parentAlpha ) {
        batch.end();
        modShapeRenderer.setProjectionMatrix( batch.getProjectionMatrix() );
        modShapeRenderer.setTransformMatrix( batch.getTransformMatrix() );

        modShapeRenderer.begin(ShapeRenderer.ShapeType.Line );


        if ( boundingBox != null ) {
            modShapeRenderer.setColor( 0.7f, 0.7f, 0.7f, 1);
            modShapeRenderer.rect(boundingBox.getX(), boundingBox.getY(),
                    boundingBox.getWidth(), boundingBox.getHeight());
        }

        modShapeRenderer.end();
        batch.begin();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        if ( modShapeRenderer == null )
            modShapeRenderer = new ModShapeRenderer();


        if ( isDrawable() && currentRegion != null ) {

            batch.setColor(getColor());

            float hW = getWidth() / 2;
            float hH = getHeight() / 2;

            batch.draw(currentRegion, getX() - hW, getY() - hH, hW, hH, getWidth(), getHeight(), 1, 1, getRotation());

//            if ( getParent() == null || !(getParent() instanceof AnimatedActorGroup) ) {
//                    drawBoundingBox(batch, parentAlpha );
//            }

        }

        Object obj = getUserObject();
        if (obj != null) {
            if (obj instanceof RenderableUserObject) {
                ((RenderableUserObject) obj).render(this, batch);
            }
        }



        super.draw( batch, parentAlpha );

    }

    static final Stack<Actor> actorsTmpStack = new Stack<Actor>();
    static final Matrix3 mtx = new Matrix3();


    public static Vector2 stageToActorLocal(Actor actor, Vector2 stageSpaceCoordinates) {

        mtx.idt();
        actorsTmpStack.clear();

        Actor act = actor;
        actorsTmpStack.push( act );

        while ( act.hasParent() ) {
            act = act.getParent();
            actorsTmpStack.push( act );
        }

        while ( !actorsTmpStack.isEmpty() ) {
            Actor a = actorsTmpStack.pop();
            mtx.translate(a.getX(), a.getY());
            mtx.rotate(a.getRotation());
        }


        stageSpaceCoordinates.mul( mtx.inv() );

        return stageSpaceCoordinates;

    };

    public static Vector2 rotateStageToLocal(Actor actor, Vector2 stageVector ) {
        actorsTmpStack.clear();

        Actor act = actor;

        while ( act.hasParent() ) {
            act = act.getParent();
            actorsTmpStack.push( act );
        }

        float rot = 0;

        while ( !actorsTmpStack.isEmpty() ) {
            Actor a = actorsTmpStack.pop();
            rot += a.getRotation();
        }
        stageVector.rotate( -rot );
        return stageVector;
    }
}
