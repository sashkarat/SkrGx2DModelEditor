package org.skr.gdx.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by rat on 07.08.14.
 */
public class Utils {

    private static final RectangleExt rectTmp = new RectangleExt();
    private static float xmin = 99999999;
    private static float xmax = -99999999;
    private static float ymin = 9999999;
    private static float ymax = -9999999;

    public static RectangleExt getBBox( Rectangle rect, float originX, float originY, float rotation ) {
        float r = (float)Math.toRadians(rotation);
        float c = (float)Math.cos(r);
        float s = (float)Math.sin(r);

        float x = rect.getX();
        float y = rect.getY();
        float width = rect.getWidth();
        float height = rect.getHeight();

        bBoxProcessingBegin();

        bBoxProcessingAddPoint( x + c * (0 - originX) + -s * (0 - originY) + originX,
                y + s * (0 - originX) + c * (0 - originY) + originY );

        bBoxProcessingAddPoint( x + c * (width - originX) + -s * (0 - originY) + originX,
                y + s * (width - originX) + c * (0 - originY) + originY );

        bBoxProcessingAddPoint( x + c * (width - originX) + -s * (height - originY) + originX,
                y + s * (width - originX) + c * (height - originY) + originY );

        bBoxProcessingAddPoint( x + c * (0 - originX) + -s * (height - originY) + originX,
                y + s * (0 - originX) + c * (height - originY) + originY );

        return bBoxProcessingEnd();

    }


    public static RectangleExt getBBox( RectangleExt boundingBoxA, RectangleExt boundingBoxB ) {

        bBoxProcessingBegin();

        bBoxProcessingAddPoint( boundingBoxA.getLeft(), boundingBoxA.getBottom() );
        bBoxProcessingAddPoint( boundingBoxB.getLeft(), boundingBoxB.getBottom() );
        bBoxProcessingAddPoint( boundingBoxA.getRight(), boundingBoxA.getTop() );
        bBoxProcessingAddPoint( boundingBoxB.getRight(), boundingBoxB.getTop() );

        return bBoxProcessingEnd();
    }

    public static RectangleExt getBBox( Vector2 ... points ) {
        bBoxProcessingBegin();
        for ( Vector2 point : points )
            bBoxProcessingAddPoint( point );
        return bBoxProcessingEnd();
    }


    public static void bBoxProcessingBegin() {
        xmin = 99999999;
        xmax = -99999999;
        ymin = 9999999;
        ymax = -9999999;
    }


    public static void bBoxProcessingAddPoint( float x, float y ) {
        xmin = Math.min( xmin, x );
        xmax = Math.max(xmax, x );
        ymin = Math.min( ymin, y );
        ymax = Math.max( ymax, y );
    }

    public static void bBoxProcessingAddPoint( Vector2 point ) {
        bBoxProcessingAddPoint( point.x, point.y );
    }

    public static RectangleExt bBoxProcessingEnd() {
        rectTmp.set( xmin, ymin, xmax - xmin, ymax - ymin );
        return rectTmp;
    }

}
