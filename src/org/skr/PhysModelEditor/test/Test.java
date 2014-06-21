package org.skr.PhysModelEditor.test;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import org.skr.PhysModelEditor.PolygonRefinement;

/**
 * Created by rat on 17.06.14.
 */
public class Test {

    public static void test1() {

        PolygonRefinement pr = new PolygonRefinement();
        Vector2 [] border = {
                new Vector2(0, -4),
                new Vector2(4, -1),
                new Vector2(4, 2),
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

        System.out.println("\n\nBorderEdges: " + pr.getBorderEdges() );
        System.out.println("Interanl Edges: " + pr.getInternalEdges() );
    }

    public static void test2() {

        PolygonRefinement pr = new PolygonRefinement();
        Vector2 [] border = {
                new Vector2(-10,  -3),
                new Vector2(  1,   3),
                new Vector2( -5,   7),
                new Vector2( -3,   2),
                new Vector2( -7,   8),
                new Vector2( -6,   5),
                new Vector2( -8,   7),
                new Vector2( -6,   3),
                new Vector2( -9,   2)
        };

        Array< Vector2 > borderArray = new Array<Vector2>( border );

        pr.reset();
        pr.setBorderVertices(borderArray);

        System.out.println(" \n **** Border: " + pr.getBorderVertices() );


        boolean isCcw = PolygonRefinement.isCcwDirection( pr.getBorderVertices() );
        System.out.println(" Is ccw: " + isCcw );

        pr.refine();

        System.out.println("\n\nBorderEdges: " + pr.getBorderEdges() );
        System.out.println("Interanl Edges: " + pr.getInternalEdges() );
    }

    public static void main(String[] args ) {
        test1();
        test2();
    }

}
