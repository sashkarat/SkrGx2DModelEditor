package org.skr.gdx.utils;

import com.badlogic.gdx.math.Rectangle;

/**
 * Created by rat on 23.03.14.
 */
public class RectangleExt extends Rectangle {


    public RectangleExt() {
        super();
    }

    public RectangleExt(float x, float y, float width, float height) {
        super(x,y, width, height);
    }

    public RectangleExt(Rectangle r) {
        super(r);
    }

    public float getLeft() {
        return this.x;
    }

    public float getRight() {
        return this.x + this.width;
    }

    public float getTop() {
        return this.y + this.height;
    }

    public float getBottom() {
        return this.y;
    }

    public RectangleExt setRight( float x ) {
        this.x = x - this.width;
        return this;
    }

    public RectangleExt setLeft( float x ) {
        this.x = x;
        return this;
    }

    public RectangleExt setBottom( float y ) {
        this.y = y;
        return this;
    }

    public RectangleExt setTop( float y ) {
        this.y = y - this.height;
        return this;
    }

    public float getAspectRatio() {
        return getHeight() / getWidth();
    }

    public float getCenterX() {
        return this.x + this.width / 2;
    }

    public float getCenterY() {
        return this.y + this.height / 2;
    }

    public static String getRecStr( RectangleExt r ) {
        return " l: " + r.getLeft() + " r: " + r.getRight() +
                " b: " + r.getBottom() + " t: " + r.getTop();
    }
}
