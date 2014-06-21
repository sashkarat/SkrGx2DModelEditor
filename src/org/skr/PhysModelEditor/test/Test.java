package org.skr.PhysModelEditor.test;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import org.skr.PhysModelEditor.PolygonRefinement;

/**
 * Created by rat on 17.06.14.
 */
public class Test {

    public static void main(String[] args ) {

        PolygonRefinement pr = new PolygonRefinement();


        Vector2 [] border = {
                new Vector2(0, -4),
                new Vector2(4, -1),
                new Vector2(4, -2),
                new Vector2(2, 4),
                new Vector2(-1, 3),
                new Vector2(-2, 5),
                new Vector2(-4, 4),
                new Vector2(-5, -1),
                new Vector2(-1, -1)
        };

        Array< Vector2 > borderArray = new Array<Vector2>( border );

        pr.reset();
        pr.setBorderVertices(borderArray);

        System.out.println("Border: " + pr.getBorderVertices() );


        boolean isCcw = PolygonRefinement.isCcwDirection( pr.getBorderVertices() );
        System.out.println(" Is ccw: " + isCcw );

        pr.refine();

        System.out.println(" Edges: " + pr.getBorderEdges() );
    }

}
