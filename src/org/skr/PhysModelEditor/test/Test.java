package org.skr.PhysModelEditor.test;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import org.skr.PhysModelEditor.PolygonRefinement;

/**
 * Created by rat on 17.06.14.
 */
public class Test {

    public static void test1() {

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

        System.out.println("Border: " + borderArray );

        Array< Array<Vector2> > polygons;
        polygons = PolygonRefinement.cutPolygon( borderArray );

        System.out.println("Number: " + polygons.size );

        for ( Array<Vector2> polygon : polygons) {
            System.out.println("  Polygon: " + polygon );
        }

    }

    public static void test2() {

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

        System.out.println(" \n **** Border: " + borderArray );

        Array< Array<Vector2> > polygons;

        polygons = PolygonRefinement.cutPolygon( borderArray );

        System.out.println("Number: " + polygons.size );

        for ( Array<Vector2> polygon : polygons) {
            System.out.println("  Polygon: " + polygon );
        }


        Array<Vector2> mergedPolygon = PolygonRefinement.mergePolygons( polygons );

        System.out.println("Merged polygon: " + mergedPolygon );


    }

    public static void test3() {

        Vector2 [] border = {
                new Vector2( -1,  -1),
                new Vector2(  1,  -1),
                new Vector2(  2,   1),
                new Vector2(  1, 0.5f),
                new Vector2(  1,   2),
                new Vector2(-0.5f, 2),
                new Vector2(-0.5f, 0.5f),
                new Vector2( -2,   1)
        };

        Array< Vector2 > borderArray = new Array<Vector2>( border );

        System.out.println(" \n **** Border: " + borderArray );

        Array< Array<Vector2> > polygons;

        polygons = PolygonRefinement.cutPolygon( borderArray );

        System.out.println("Number: " + polygons.size );

        for ( Array<Vector2> polygon : polygons) {
            System.out.println("  Polygon: " + polygon );
        }


        Array<Vector2> mergedPolygon = PolygonRefinement.mergePolygons( polygons );

        System.out.println("Merged polygon: " + mergedPolygon );


    }

    public static void main(String[] args ) {

//        Vector2 vA = new Vector2( 2, -5 );
//        Vector2 vB = new Vector2( -4, 6);
////        Vector2 vB = new Vector2( vA ).rotate(90);
//
//        float d = vB.rotate(  - vA.angle() ).angle();
//
//        System.out.println( "a: " + d);

//        test1();
//        test2();
        test3();
    }

}
