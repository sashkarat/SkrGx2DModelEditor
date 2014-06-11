package org.skr.PhysModelEditor;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;

import java.util.Stack;

/**
 * Created by rat on 31.05.14.
 */
public class AnimatedActorGroup extends Group {

    public interface RenderableUserObject {
        public void render( AnimatedActorGroup aag, Batch batch );
    }


    public interface GlobalSizeChangedListener {
        public void sizeChanged(AnimatedActorGroup aag);
    };

    private static GlobalSizeChangedListener globalSizeChangedListener;

    public static void setGlobalSizeChangedListener( GlobalSizeChangedListener listener ) {
        globalSizeChangedListener = listener;
    }

    public static class Description {

        String name = "";
        String textureName = "";
        float frameDuration = 0.02f;
        Animation.PlayMode playMode = Animation.PlayMode.LOOP;
        float width = 0;
        float height = 0;
        float x = 0;
        float y = 0;
        float rotation = 0;
        boolean drawable = true;
        boolean keepAspectRatio = false;

        Array<Description> children = new Array<Description>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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

        public Animation.PlayMode getPlayMode() {
            return playMode;
        }

        public void setPlayMode(Animation.PlayMode playMode) {
            this.playMode = playMode;
        }

        public float getWidth() {
            return width;
        }

        public void setWidth(float width) {
            this.width = width;
        }

        public float getHeight() {
            return height;
        }

        public void setHeight(float height) {
            this.height = height;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public float getRotation() {
            return rotation;
        }

        public void setRotation(float rotation) {
            this.rotation = rotation;
        }

        public boolean isDrawable() {
            return drawable;
        }

        public void setDrawable(boolean drawable) {
            this.drawable = drawable;
        }

        public boolean isKeepAspectRatio() {
            return keepAspectRatio;
        }

        public void setKeepAspectRatio(boolean keepAspectRatio) {
            this.keepAspectRatio = keepAspectRatio;
        }

        public Array<Description> getChildren() {
            return children;
        }

        public void setChildren(Array<Description> children) {
            this.children = children;
        }
    };


    String textureName = "";
    float frameDuration = 0.02f;
    Animation.PlayMode playMode = Animation.PlayMode.LOOP;
    boolean keepAspectRatio = false;
    boolean drawable = true;


    private TextureRegion currentRegion;
    private Animation animation;
    private float stateTime = 0;
    private int count;

    public AnimatedActorGroup() {
        updateTextures();
    }

    public AnimatedActorGroup( Description desc ) {
        uploadFromDescription( desc );
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


    public Description getDescription() {

        Description desc = new Description();

        Array<Description> children = desc.getChildren();

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

    public void uploadFromDescription( Description desc ) {

        Actor[] old_children = getChildren().toArray();
        for ( Actor a : old_children)
            removeActor( a );

        for ( Description d : desc.getChildren() )
            addChild( new AnimatedActorGroup( d ) );

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

        updateTextures();


    }

    public void updateTextures() {

        if ( animation != null )
            animation = null;
        if ( currentRegion != null )
            currentRegion = null;

        if ( textureName.isEmpty() ) {
            return;
        }

        stateTime = 0;

        TextureAtlas atlas = GdxApplication.get().getAtlas();

        if ( atlas != null ) {

            Array<TextureAtlas.AtlasRegion> tex = atlas.findRegions(textureName);
            animation = new Animation(frameDuration, tex, playMode);
            stateTime = 0;
            currentRegion = animation.getKeyFrame(stateTime);

        }

        checkAspectRatio();

        SnapshotArray<Actor> children =  getChildren();

        for ( Actor a: children ) {
            if ( a instanceof AnimatedActorGroup ) {
                ((AnimatedActorGroup) a).updateTextures();
            }
        }
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

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {


        if ( isDrawable() && currentRegion != null ) {

            batch.setColor(getColor());

            float hW = getWidth() / 2;
            float hH = getHeight() / 2;

            batch.draw(currentRegion, getX() - hW, getY() - hH, hW, hH, getWidth(), getHeight(), 1, 1, getRotation());
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

    public static Vector2 stageToActorLocal(Actor actor, Vector2 stageSpaceCoordinates) {

        Matrix3 mtx = new Matrix3();

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
