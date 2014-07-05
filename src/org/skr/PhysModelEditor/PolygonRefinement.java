package org.skr.PhysModelEditor;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by rat on 17.06.14.
 */
public class PolygonRefinement {

    private final static float PRECISION =1e-5f;

    public enum EdgeType {
        BORDER, INTERNAL
    }

    // ===========  Edge Class =============================

    public static class Edge {
        private Vertex vertexA = null;
        private Vertex vertexB = null;
        private Vector2 vector = null;
        private EdgeType type = EdgeType.BORDER;

        private void updateVector() {
            if ( vertexA == null ) {
                vector = null;
                return;
            }
            if ( vertexB == null ) {
                vector = null;
                return;
            }
            vector = new Vector2( vertexB.getPoint() );
            vector.sub(vertexA.getPoint());
        }



        public Edge(Vertex vertexA, Vertex vertexB, EdgeType type ) {
            this.vertexA = vertexA;
            this.vertexB = vertexB;
            this.type = type;
            updateVector();
        }

        public Edge(Vertex vertexA, Vertex vertexB ) {
            this.vertexA = vertexA;
            this.vertexB = vertexB;
        }

        public EdgeType getType() {
            return type;
        }

        public boolean isConnected() {
            if ( vertexA == null )
                return false;
            if ( vertexB == null )
                return false;
            return true;
        }

        public Vertex getVertexA() {
            return vertexA;
        }

        public void setVertexA(Vertex vertexA) {
            this.vertexA = vertexA;
            updateVector();
        }

        public Vertex getVertexB() {
            return vertexB;
        }

        public void setVertexB(Vertex vertexB) {
            this.vertexB = vertexB;
            updateVector();
        }

        public Vector2 getVector() {
            return vector;
        }

        public boolean isVertexA( Vertex vertex ) {
            if ( vertex == vertexA )
                return true;
            return false;
        }

        public boolean isVertexB( Vertex vertex ) {
            if ( vertex == vertexB )
                return true;
            return false;
        }

        @Override
        public boolean equals(Object obj) {
            if (  !(obj instanceof Edge) )
                return super.equals(obj);
            Edge e = (Edge) obj;
            if ( e.getVertexA() == vertexA)
                if ( e.getVertexB() == vertexB )
                    return true;
            return false;
        }

        @Override
        public String toString() {
            return "[ A: " + vertexA + " B: " + vertexB + " V: " + vector + " T: " + type + " ]";
        }
    }


    // ===========  Vertex Class =============================

    public class Vertex {
        private Vector2 point = new Vector2();

        private Edge inputBorder;
        private Edge outputBorder;
        private Array< Edge > inputEdges = new Array<Edge>();
        private Array< Edge > outputEdges = new Array<Edge>();


        private void updateEdge( Edge edge ) {
            if ( edge.isVertexA(this) ) {
                edge.setVertexA(this);
            } else if ( edge.isVertexB( this ) ) {
                edge.setVertexB( this );
            }
        }

        private void updateEdges() {
            for ( Edge e : inputEdges ) {
                updateEdge( e );
            }

            for ( Edge e: outputEdges ) {
                updateEdge( e );
            }

            if ( inputBorder != null )
                updateEdge( inputBorder );
            if ( outputBorder != null )
                updateEdge( outputBorder );
        }

        public Vertex(Vector2 point) {
            this.point.set( point );
        }

        public Vector2 getPoint() {
            return point;
        }

        public void setPoint(Vector2 point) {
            this.point.set( point );
            updateEdges();
        }

        public Edge getInputBorder() {
            return inputBorder;
        }

        public Edge getOutputBorder() {
            return outputBorder;
        }

        public Array<Edge> getInputEdges() {
            return inputEdges;
        }

        public Array<Edge> getOutputEdges() {
            return outputEdges;
        }

        public boolean isEdgeConnected( Edge edge ) {
            if ( edge.isVertexA( this ) )
                return true;
            if ( edge.isVertexB( this ) )
                return true;
            return false;
        }


        public void removeInputBorder() {
            if ( inputBorder == null )
                return;
            inputBorder = null;
        }

        public void removeOutputBorder() {
            if ( outputBorder == null )
                return;
            outputBorder = null;
        }


        public void setInputBorder(Edge inputBorder) {
            removeInputBorder();
            this.inputBorder = inputBorder;
            this.inputBorder.setVertexB( this );
        }

        public void setOutputBorder(Edge outputBorder) {
            removeOutputBorder();
            this.outputBorder = outputBorder;
            this.outputBorder.setVertexB( this );
        }

        public boolean addInputEdge( Edge edge ) {
            if ( edge.getType() == EdgeType.BORDER )
                return false;

            if ( inputEdges.contains( edge, true ) )
                return false;

            if ( !edge.isVertexB( this) )
                edge.setVertexB( this );
            inputEdges.add( edge );

            return true;
        }

        public boolean addOutputEdge ( Edge edge ) {
            if ( edge.getType() == EdgeType.BORDER )
                return false;

            if ( outputEdges.contains( edge, true ) )
                return false;

            if ( !edge.isVertexA(this) )
                edge.setVertexA(this);
            outputEdges.add( edge );

            return true;
        }


        public void removeEdge( Edge edge ) {
            if ( edge == inputBorder ) {
                inputBorder = null;
                return;
            }
            if ( edge == outputBorder ) {
                outputBorder = null;
                return;
            }

            if ( inputEdges.removeValue( edge, true ) )
                return;
            outputEdges.removeValue( edge, true);
        }


        public Edge routeNextEdge(Edge inputEdge) {

            final Vector2 tV = new Vector2();
            float angle = -180.001f;
            float offsetAngle = 0;
            try {
                offsetAngle = inputEdge.getVector().angle();
            } catch ( StackOverflowError error ) {
                System.err.println("PolygonRefinement.routeNextEdge. ERROR: " + error.getMessage() );
                return null;
            }

            Edge nextEdge = null;

            if ( outputBorder != null ) {
                tV.set(outputBorder.getVector());
                tV.rotate(-offsetAngle);
                angle = tV.angle();
                if (angle > 179.99)
                    angle -= 360;
                nextEdge = outputBorder;
//            System.out.println("PolygonRefinement.Vertex.routeNextEdge: nextEdge: " + nextEdge + " angle: " + angle);
            }

            for ( Edge e : outputEdges ) {

                tV.set( e.getVector() );
                tV.rotate( -offsetAngle );

                float a = tV.angle();

                if ( a > 179.99 )
                    a -= 360;

//                System.out.println("PolygonRefinement.Vertex.routeNextEdge: a: " + a);

                if ( a > angle ) {
                    angle = a;
                    nextEdge = e;
//                    System.out.println("PolygonRefinement.Vertex.routeNextEdge: nextEdge: " + nextEdge + " angle: " + angle);
                }
            }
            return nextEdge;
        }

        @Override
        public String toString() {
            return point.toString();
        }
    }

    // ============ Vertices class ========================

    public class Vertices {
        Array< Vertex > vertices = new Array<Vertex>();

        public Vertices() {

        }

        public Vertices( Array < Vector2 > points ) {
            set(points);
        }

        public void set(Array<Vector2> points) {
            vertices.clear();
            for ( Vector2 point : points )
                vertices.add( new Vertex( point ) );

        }

        public Array<Vertex> get() {
            return vertices;
        }

        public Array< Vector2 > getPoints() {
            Array< Vector2 > out = new Array<Vector2>();
            for( Vertex v : vertices )
                out.add( v.getPoint() );
            return out;
        }

        public void clear() {
            vertices.clear();
        }

        public int getCount() {
            if ( vertices == null )
                return -1;
            return vertices.size;
        }

        public Vertex get( int index ) {
            return vertices.get( index % vertices.size );
        }

        public Vertex insertAfter( int index, Vertex newValue ) {

            index %= vertices.size;

            if ( index == (vertices.size - 1) ) {
                vertices.add( newValue );
            } else {
                vertices.insert( index + 1, newValue );
            }

            return vertices.get( index + 1);
        }

        public Vertex insertAfter( Vertex v, Vertex newValue ) {

            int indexOf  = vertices.indexOf( v, true );

            return insertAfter( indexOf, newValue );
        }

        @Override
        public String toString() {
            return "<" + vertices + ">";
        }
    }


    //  ===========  ======== =============================

    static PolygonRefinement instance  = null;
    private final static Vector2 tmpVector = new Vector2();


    public static float crossProduct(Vector2 vectorA, Vector2 vectorB) {
        tmpVector.set( vectorA );
        return tmpVector.crs( vectorB );
    }

    public static boolean isConvex( Vector2 vectorA, Vector2 vectorB ) {
        if ( crossProduct(vectorA, vectorB) > 0f )
            return true;
        return false;
    }

    public static boolean isCcwDirection( Vertices vertices ) {

        int convex = 0;
        int concave = 0;

        for ( int i = 0; i < vertices.getCount(); i++) {
            Vector2 p1 = vertices.get(i).getPoint();
            Vector2 p2 = vertices.get(i + 1).getPoint();
            Vector2 p3 = vertices.get(i + 2).getPoint();

            Vector2 vA = p2.cpy();
            vA.sub( p1 );
            Vector2 vB = p3.cpy();
            vB.sub( p2 );

            if ( isConvex( vA, vB) ) {
                convex++;
            } else {
                concave++;
            }
        }

        if ( convex >= concave )
            return true;

        return false;
    }

    public static boolean isConvex( Vertices vertices ) {

        for ( int i = 0; i < vertices.getCount(); i++) {
            Vector2 p1 = vertices.get(i).getPoint();
            Vector2 p2 = vertices.get(i + 1).getPoint();
            Vector2 p3 = vertices.get(i + 2).getPoint();

            Vector2 vA = p2.cpy();
            vA.sub( p1 );
            Vector2 vB = p3.cpy();
            vB.sub( p2 );

            if ( isConvex( vA, vB) ) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    public static boolean isComplex( Array< Edge> borderEdges ) {

        for ( int i = 0; i < borderEdges.size; i++ ) {

            Edge e = borderEdges.get( i );

            for ( Edge e2 : borderEdges ) {
                if ( e2 == e)
                    continue;
                if ( e.getVertexB() == e2.getVertexA() )
                    continue;
                if ( e.getVertexA() == e2.getVertexB() )
                    continue;
                if ( e.getVertexA() == e2.getVertexA() )
                    continue;
                if ( e.getVertexB() == e2.getVertexB() )
                    continue;

                if ( Intersector.intersectSegments(e.getVertexA().getPoint(), e.getVertexB().getPoint(),
                        e2.getVertexA().getPoint(), e2.getVertexB().getPoint(), null ) )
                    return true;
            }
        }

        return false;
    }

    public static boolean areVerticesEqual( Vertex a, Vertex b, float treshold ) {
        float dist = a.getPoint().dst( b.getPoint() );
        if ( dist < treshold )
            return true;
        return false;
    }

    public static Array< Edge > createBorderEdges( Vertices borderVertices ) {
        if ( borderVertices == null)
            return null;

        Array< Edge > outputEdges = new Array<Edge>();

        for ( int i = 0; i < borderVertices.getCount(); i++) {

            Edge edge = new Edge( borderVertices.get(i), borderVertices.get(i+1) );
            borderVertices.get( i ).setOutputBorder( edge );
            borderVertices.get( i + 1 ).setInputBorder( edge );
            outputEdges.add(edge);
        }

        return outputEdges;
    }


    public static Array< Array<Vector2> > dividePolygon( Array< Vector2 > points, int maxVerticesCount ) {
        Array< Array<Vector2> > output = new Array<Array<Vector2>>();

        if ( points.size < 4 )
            return null;

        if ( instance == null )
            instance = new PolygonRefinement();

        instance.reset();
        instance.setBorderVertices( points );
        instance.createInitialTopology();

        Array< Vertices > polygons = instance.divide();

        for ( Vertices polygon : polygons ) {

            if ( polygon.getCount() > maxVerticesCount ) {
                Array<Array<Vector2> > subPolygon  = dividePolygon( polygon.getPoints(), maxVerticesCount );
                if ( subPolygon == null )
                    return null;
                output.addAll( subPolygon );
            } else {
                output.add(polygon.getPoints());
            }
        }

        return output;
    };


    public static Array< Array<Vector2> > cutPolygon( Array< Vector2 > points ) {
        Array< Array<Vector2> > output = new Array<Array<Vector2>>();

        if ( points.size < 3 )
            return output;

        if ( instance == null )
            instance = new PolygonRefinement();

        instance.reset();
        instance.setBorderVertices( points );

//        System.out.println("\nPolygonRefinement.cutPolygon. Border: " + instance.borderVertices );
        Array<Vertices> polygons = null;

        try {
            instance.refine();
            polygons = instance.cut();

            if ( polygons == null )
                return null;

        } catch ( StackOverflowError err ) {
            return null;
        }

//        System.out.println("PolygonRefinement.cutPolygon. Polygons: " + polygons );

        for ( Vertices poly : polygons) {

            if ( isConvex( poly ) ) {
                if ( poly.getCount() > 8 ) {
                    Array< Array<Vector2> > subPoly  = dividePolygon( poly.getPoints(), 8 );
                    if ( subPoly == null )
                        return null;
                    output.addAll( subPoly );
                } else {
                    output.add(poly.getPoints());
                }
//                System.out.println("PolygonRefinement.cutPolygon. Res: " + poly );
            } else {

                Array< Array<Vector2> > subPoly = cutPolygon( poly.getPoints() );
                if ( subPoly == null )
                    return null;
                output.addAll(  subPoly );
            }

        }
//        System.out.println(" Out size: " + output.size );
        return output;
    }


    public static Array<Vector2> mergePolygons( Array< Array<Vector2> > polygons) {
        Array< Array<Vector2> > polygonsArray = new Array<Array<Vector2>>( polygons );
        return mergeAll( polygonsArray );
    }

    public static Array<Vector2> mergePolygons( Array<Vector2> ... polygons ) {
        Array< Array<Vector2> > polygonsArray = new Array<Array<Vector2>>( polygons );
        return mergeAll( polygonsArray );
    }

    private static Array<Vector2> mergeAll( Array< Array<Vector2> > polygons ) {

        if ( polygons.size == 0 )
            return null;

        Array<Vector2> merged = polygons.get(0);
        polygons.removeIndex(0);


        int index = 0;
        int cntr = polygons.size * 4;

        while ( polygons.size > 0 ) {
            if ( cntr-- <= 0 )
                return null;

            if ( index >= polygons.size)
                index = 0;
            Array< Vector2 > merged2 = PolygonRefinement.mergeTwoPolygons( merged, polygons.get(index) );
            if ( merged2 == null ) {
                index++;
                continue;
            }
            merged = merged2;
            polygons.removeIndex( index );
        }
        return merged;
    }


    public static Array< Vector2 > mergeTwoPolygons(Array<Vector2> primary, Array<Vector2> secondary) {

//        System.out.println("\nPolygonRefinement.mergeTwoPolygons:\n    Primary: " + primary +
//        "\n    Secondary: " + secondary);


        if ( instance == null )
            instance = new PolygonRefinement();

        PolygonRefinement instSecondary = new PolygonRefinement();

        instance.reset();
        instance.setBorderVertices( primary );
        instance.createInitialTopology();

        instSecondary.setBorderVertices( secondary );
        instSecondary.createInitialTopology();

        Edge primaryEdge = null;
        Edge secondaryEdge = null;

        for ( Edge e : instance.getBorderEdges() ) {
            secondaryEdge = instance.getSecondaryEqualBorderEdge( e, instSecondary, PRECISION  );
            if ( secondaryEdge == null )
                continue;
            primaryEdge = e;
            break;
        }

        if ( primaryEdge == null )
            return null;

//        System.out.println("PolygonRefinement.mergeTwoPolygons:\n    PrimEdge: " + primaryEdge +
//                "\n    SecEdge: " + secondaryEdge );

        Vertex primVa = primaryEdge.getVertexA();
        Vertex finVertex = secondaryEdge.getVertexA();

        while ( true ) {

            secondaryEdge = secondaryEdge.getVertexB().getOutputBorder();
            if ( secondaryEdge == null )
                return null;

            Vertex secV = secondaryEdge.getVertexB();

            if ( secV == finVertex )
                break;

            primVa = instance.getBorderVertices().insertAfter( primVa, secV );

        }

        instance.createInitialTopology();
        instance.removeConfluentBorderEdges();

        return instance.getBorderVertices().getPoints();
    };




    // ======================= NON-STATIC METHODS ==============================


    Vertices borderVertices = new Vertices();
    Array< Edge > borderEdges = new Array<Edge>();
    Array< Edge > internalEdges = new Array<Edge>();


    private PolygonRefinement() {
    }



    public void reset() {
        borderVertices.clear();
        borderEdges.clear();
        internalEdges.clear();
    }


    private void setBorderVertices(Array<Vector2> points) {
        borderVertices.set(points);
        if ( !isCcwDirection( borderVertices) )
            borderVertices.get().reverse();
    }

    private Vertices getBorderVertices() {
        return borderVertices;
    }


    private Array<Edge> getBorderEdges() {
        return borderEdges;
    }

    private Array<Edge> getInternalEdges() {
        return internalEdges;
    }

    private class TraceInfo {
        public Edge nearestEdge = null;
        public float distance;
    }


    private boolean findAndRemoveConfluentBorderEdge() {
        Vertex v_ = null;

        for ( Vertex v : borderVertices.get() ) {

            Vertex v1 = v.getInputBorder().getVertexA();
            Vertex v2 = v.getOutputBorder().getVertexB();

            if ( areVerticesEqual( v1, v2, PRECISION ) ) {
                v_ = v;
                break;
            }
        }

        if ( v_ == null )
            return false;

        Vertex v1 = v_.getInputBorder().getVertexA();
        Vertex v2 = v_.getOutputBorder().getVertexB();

        removeEdge( v_.getInputBorder() );
        removeEdge( v_.getOutputBorder() );

        v1.setInputBorder( v2.getInputBorder() );

        borderVertices.get().removeValue( v2, true);
        return true;
    }


    private void removeConfluentBorderEdges() {
        while ( findAndRemoveConfluentBorderEdge() ) {};
    }

    private TraceInfo traceEdge(Edge edge, Vector2 rayPoint, Array<Edge> edges, TraceInfo traceInfo) {
        final Vector2 iPoint = new Vector2();
        if ( traceInfo == null ) {
            traceInfo = new TraceInfo();
            traceInfo.distance = 1e20f;
        }
        for ( Edge e : edges ) {
            if ( e == edge )
                continue;
            if ( e.getVertexB() == edge.getVertexA() )
                continue;
            if ( e.getVertexA() == edge.getVertexB() )
                continue;
            if ( e.getVertexA() == edge.getVertexA() )
                continue;
            if ( e.getVertexB() == edge.getVertexB() )
                continue;

            boolean res = Intersector.intersectSegments( edge.getVertexA().getPoint(), rayPoint,
                    e.getVertexA().getPoint(), e.getVertexB().getPoint(), iPoint );
            if ( !res )
                continue;
            float d = edge.getVertexB().getPoint().dst( iPoint );
            if ( d >= traceInfo.distance )
                continue;
            traceInfo.distance = d;
            traceInfo.nearestEdge = e;
        }
        return traceInfo;
    }

    private Edge traceInputBorder( Edge border ) {

        final Vector2 rayPoint = new Vector2();

        TraceInfo finalBorderTrace = null;
        TraceInfo trInfo;

        for ( float ang = 0; ang < 180; ang += 20) {

            rayPoint.set(border.getVector());
            rayPoint.rotate( ang );
            rayPoint.scl(1e4f);
            rayPoint.add(border.getVertexA().getPoint());
            trInfo = traceEdge(border, rayPoint, borderEdges, null);
            if (trInfo.nearestEdge == null) {
                continue;
            }

            if ( finalBorderTrace == null ) {
                finalBorderTrace = trInfo;
                continue;
            }

            if ( finalBorderTrace.distance > trInfo.distance )
                finalBorderTrace = trInfo;

        }

        if ( finalBorderTrace == null ) {
//            System.out.println("PolygonRefinement.traceInputBorder. WARNING. traceEdge failed (trace ray): " + border );
            return null;
        }

//        System.out.println("PolygonRefinement.traceInputBorder. intersectedEdge(Border): " + trInfo.nearestEdge );

        Edge intersectedEdge = finalBorderTrace.nearestEdge;
        Edge edge = new Edge( border.getVertexB(), intersectedEdge.getVertexB(), EdgeType.INTERNAL );

        trInfo = traceEdge( edge, edge.getVertexB().getPoint(), borderEdges, null );
        trInfo = traceEdge( edge, edge.getVertexB().getPoint(), internalEdges, trInfo );

        if ( trInfo.nearestEdge == null ) {

            return edge;
        }

//        System.out.println("PolygonRefinement.traceInputBorder. intersectedEdge(All): " + trInfo.nearestEdge );

        if ( trInfo.nearestEdge.equals( intersectedEdge ) )
            return edge;

        Vertex vertexB = trInfo.nearestEdge.getVertexA();

        Edge outputBorder = border.getVertexB().getOutputBorder();

        if ( vertexB == outputBorder.getVertexB() )
            vertexB = trInfo.nearestEdge.getVertexA();

        edge = new Edge( border.getVertexB(), vertexB, EdgeType.INTERNAL);
        return edge;
    }


    private void trace() {

        for ( int i = 0; i < borderVertices.getCount(); i++) {
            Vertex v = borderVertices.get(i);

            Edge inputBorder = v.getInputBorder();
            Edge outputBorder = v.getOutputBorder();
            if ( inputBorder == null || outputBorder == null ) {
                System.err.println("PolygonRefinement.trace ERROR: vertex without border");
                return;
            }

            if ( isConvex( inputBorder.getVector(), outputBorder.getVector() ) )
                continue;
//            System.out.println("PolygonRefinement.trace: concave vertex: " + v + " InputBorder: " + inputBorder);
            Edge newEdge = traceInputBorder( inputBorder );

            if ( newEdge == null ) {
//                System.out.println("PolygonRefinement.trace: WARNING. Tracing concave vertex failed. ");
                continue;
            }

//            System.out.println("PolygonRefinement.trace: new Edge: " + newEdge );

            newEdge.getVertexA().addOutputEdge( newEdge );
            newEdge.getVertexB().addInputEdge( newEdge );

            internalEdges.add( newEdge );
        }
    }

    private void createOppositeInternalEdges() {
        Array< Edge > revEdges = new Array<Edge>();

        try {
            for (Edge e : internalEdges) {

                Edge ne = new Edge(e.getVertexB(), e.getVertexA(), EdgeType.INTERNAL);
                ne.getVertexB().addInputEdge(ne);
                ne.getVertexA().addOutputEdge(ne);
                revEdges.add(ne);
            }
        } catch ( StackOverflowError error ) {
            System.err.println("PolygonRefinement.createOppositeInternalEdges ERROR: " + error.getMessage() );
            return;
        }

        internalEdges.addAll( revEdges );
    }

    private void removeEdge( Edge edge ) {
        if ( borderEdges.removeValue( edge, true ) )
            return;
        internalEdges.removeValue( edge, true);
    }

    private Vertices extractPolygon( Edge edge ) {
        Vertices poly = new Vertices();


//        System.out.println("PolygonRefinement.extractPolygon: start edge: " + edge);

        poly.get().add( edge.getVertexA() );

        while ( true ) {

            Edge prevEdge = edge;

            edge = edge.getVertexB().routeNextEdge( edge );

            prevEdge.getVertexA().removeEdge( prevEdge );
            prevEdge.getVertexB().removeEdge( prevEdge );
            removeEdge( prevEdge );

//            System.out.println("PolygonRefinement.extractPolygon: edge: " + edge );

            if ( edge == null ) {
                System.out.println("PolygonRefinement.extractPolygon. WARNING. Cannot route the path ");
                return null;
            }



            poly.get().add( edge.getVertexA() );
            if ( edge.getVertexB() == poly.get(0) ) {
                edge.getVertexA().removeEdge( edge );
                edge.getVertexB().removeEdge( edge );
                removeEdge( edge );
                break;
            }
        }
        return poly;
    }


    private void createInitialTopology() {

        clearTopology();

        Array< Edge > newBorderEdges = createBorderEdges( borderVertices );
        if ( newBorderEdges == null )
            return;
        borderEdges.addAll( newBorderEdges );

        if ( isComplex( borderEdges ) ) {
            borderEdges.clear();
        }
    }

    private void clearTopology() {
        borderEdges.clear();
        internalEdges.clear();
    }

    private void refine() {

        createInitialTopology();

        trace();
        createOppositeInternalEdges();
    }

    private Array<Vertices> divide () {
        if ( borderEdges.size == 0)
            return null;
        Array< Vertices > out = new Array<Vertices>();

        Vertex vA = borderVertices.get( borderVertices.getCount() / 2 );
        Vertex vB = borderVertices.get( 0 );

        Edge e = new Edge( vA, vB, EdgeType.INTERNAL );

        vA.addOutputEdge( e );
        vB.addInputEdge( e );

        internalEdges.add( e );

        createOppositeInternalEdges();

        return cut();
    }

    private Array< Vertices > cut() {

        if ( borderEdges.size == 0)
            return null;

        Array< Vertices > out = new Array<Vertices>();

        while ( borderEdges.size > 0 ) {
            Vertices poly = extractPolygon( borderEdges.get(0) );
            if ( poly == null )
                break;
            out.add( poly );
        }
        return out;
    }

    private Vertex getSecondaryEqualVertex( Vertex vertex, PolygonRefinement secondary, float threshold ) {

        for ( Vertex b : secondary.getBorderVertices().get() ) {

            if ( areVerticesEqual( vertex, b, threshold ))
                return b;
        }
        return null;
    }

    private Edge getSecondaryEqualBorderEdge( Edge edge, PolygonRefinement secondary, float threshold ) {

        Vertex secB = getSecondaryEqualVertex( edge.getVertexA(), secondary, threshold );

//        System.out.println("PolygonRefinement.getSecondaryEqualBorderEdge: secB: " + secB);

        if ( secB == null )
            return null;

        Vertex secA = getSecondaryEqualVertex( edge.getVertexB(), secondary, threshold );

//        System.out.println("PolygonRefinement.getSecondaryEqualBorderEdge: secA: " + secA);

        if ( secA == null ) {
            return null;
        }

        if ( secB.getInputBorder().getVertexA() == secA )
            return secB.getInputBorder();
        return  null;
    }

}
