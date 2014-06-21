package org.skr.PhysModelEditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by rat on 17.06.14.
 */
public class PolygonRefinement {

    public enum EdgeType {
        BORDER, INTERNAL
    }

    // ===========  Edge Class =============================

    public class Edge {
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
        }

        public Edge(Vertex vertexA, Vertex vertexB ) {
            this.vertexA = vertexA;
            this.vertexB = vertexB;
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
        private Array< Edge > outputEdge = new Array<Edge>();


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

            for ( Edge e: outputEdge ) {
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

        public Array<Edge> getOutputEdge() {
            return outputEdge;
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
            inputBorder.setVertexB( null );
            inputBorder = null;
        }

        public void removeOutputBorder() {
            if ( outputBorder == null )
                return;
            outputBorder.setVertexA( null );
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

        @Override
        public String toString() {
            return "<" + vertices + ">";
        }
    }


    //  ===========  ======== =============================

    private final static Vector2 tmpVector = new Vector2();
    public static float  crossDot( Vector2 vectorA, Vector2 vectorB ) {
        tmpVector.set( vectorA );
        return tmpVector.crs( vectorB );
    }

    public static boolean isConvex( Vector2 vectorA, Vector2 vectorB ) {
        if ( crossDot( vectorA, vectorB ) > 0f )
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


    Vertices borderVertices = new Vertices();
    Array< Edge > borderEdges = new Array<Edge>();


    public PolygonRefinement() {
    }


    public void reset() {
        borderVertices.clear();
        borderEdges.clear();
    }


    public void setBorderVertices(Array<Vector2> points) {
        borderVertices.set(points);
        if ( !isCcwDirection( borderVertices) )
            borderVertices.get().reverse();
    }

    public Vertices getBorderVertices() {
        return borderVertices;
    }


    private void createBorderEdges() {
        if ( borderVertices == null)
            return;
        for ( int i = 0; i < borderVertices.getCount(); i++) {

            Edge edge = new Edge(borderVertices.get(i), borderVertices.get(i+1) );
            borderVertices.get( i ).setOutputBorder( edge );
            borderVertices.get( i + 1 ).setInputBorder( edge );
            borderEdges.add(edge);
        }

    }

    public Array<Edge> getBorderEdges() {
        return borderEdges;
    }

    public void refine() {
        borderEdges.clear();
        createBorderEdges();
    }


}
