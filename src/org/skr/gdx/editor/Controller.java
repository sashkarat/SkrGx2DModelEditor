package org.skr.gdx.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import org.skr.gdx.utils.ModShapeRenderer;
import org.skr.gdx.utils.RectangleExt;

/**
 * Created by rat on 03.06.14.
 */
public abstract  class Controller  {


    public static class ControlPoint {

        private static float size = 10;
        private static int idc = 0;
        private float x = 0;
        private float y = 0;
        private boolean selected = false;
        private Object object = null;
        private Color color;
        private int id = -1;
        private boolean visible = true;

        public enum ShapeType {
            _Rect,
            _Circle
        }

        public ControlPoint( Object object) {
            this.object = object;
            this.id = idc++;
        }


        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
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

        public void setPos(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public Object getObject() {
            return object;
        }

        public void setObject(Object object) {
            this.object = object;
        }

        public int getId() {
            return id;
        }

        public void offsetPos(float xOffset, float yOffset) {
            this.x += xOffset;
            this.y += yOffset;
        }

        private static final Color colorBackup = new Color();

        public void draw( ModShapeRenderer shr ) {
            draw( shr, ShapeType._Rect );
        }

        public void draw( ModShapeRenderer shr, ShapeType type ) {


            if ( color != null ) {
                colorBackup.set( shr.getColor() );
                shr.setColor(color);
            }

            if ( selected ) {
                shr.begin(ShapeRenderer.ShapeType.Filled);
            } else {
                shr.begin(ShapeRenderer.ShapeType.Line);
            }

            if ( type == ShapeType._Rect ) {
                shr.rect(x - size / 2, y - size / 2, size, size);
            } else if ( type == ShapeType._Circle ) {
                shr.solidCircle( x, y, size / 2);
            }

            shr.end();
            if ( color != null ) {
                shr.setColor( colorBackup );
            }
        }



        public static float getSize() {
            return size;
        }

        public static void setSize(float size) {
            ControlPoint.size = size;
        }

        private static final RectangleExt tmpRect = new RectangleExt();

        public boolean contains( Vector2 localCoord ) {
            tmpRect.set(x - size / 2, y - size / 2, size, size);
            return tmpRect.contains( localCoord );
        }

    }


    public interface controlPointListener {
        public void changed ( Object controlledObject, ControlPoint controlPoint);
    }


    protected enum ControlPointSelectionMode {
        SELECT_BY_CLICK,
        PRESSED_ONLY
    }

    public static class BoundingBox {
        public float minX;
        public float maxX;
        public float minY;
        public float maxY;
    }

    ModShapeRenderer shapeRenderer = new ModShapeRenderer();
    Stage stage;
    Array<ControlPoint> controlPoints = new Array<ControlPoint>();
    ControlPoint posControlPoint = null;
    ControlPoint selectedControlPoint = null;
    Array<ControlPoint> boundingBoxControlPoints = new Array<ControlPoint>();
    boolean enableBbControl = true;
    controlPointListener controlPointListener;
    ControlPointSelectionMode selectionMode = ControlPointSelectionMode.PRESSED_ONLY;

    protected float controlPointBaseSize = 10;
    protected float cameraZoom = 1;
    private float cpSize = 10;

    public Controller(Stage stage) {
        this.stage = stage;

        setPosControlPoint( new ControlPoint( null ) );

        for ( int i = 0; i < 4; i++ ) {
            ControlPoint cp = new ControlPoint( null );
            cp.setColor( new Color(0.3f, 0.6f, 0.3f, 1) );
            boundingBoxControlPoints.add( cp );
        }
    }

    public ModShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    protected void setPosControlPoint( ControlPoint cp ) {
        if ( posControlPoint != null ) {
            if ( selectedControlPoint == posControlPoint ) {
                selectedControlPoint = cp;
                cp.setSelected( true );
            }
        }
        posControlPoint = cp;
    }

    public Array<ControlPoint> getControlPoints() {
        return controlPoints;
    }

    public void setControlPoints(Array<ControlPoint> controlPoints) {
        this.controlPoints = controlPoints;
    }

    public ControlPoint getSelectedControlPoint() {
        return selectedControlPoint;
    }

    public void setSelectedControlPoint(ControlPoint selectedControlPoint) {
        this.selectedControlPoint = selectedControlPoint;
    }

    public void setBoundingBoxControlPoints(Array<ControlPoint> boundingBoxControlPoints) {
        this.boundingBoxControlPoints = boundingBoxControlPoints;
    }

    public Array<ControlPoint> getBoundingBoxControlPoints() {
        return boundingBoxControlPoints;
    }

    public boolean isEnableBbControl() {
        return enableBbControl;
    }

    public void setEnableBbControl(boolean enableBbControl) {
        this.enableBbControl = enableBbControl;
    }

    public ControlPoint getBbDownLeftControlPoint() {
        return boundingBoxControlPoints.get(0);
    }

    public ControlPoint getBbDownRightControlPoint() {
        return boundingBoxControlPoints.get(1);
    }

    public ControlPoint getBbTopRightControlPoint() {
        return boundingBoxControlPoints.get(2);
    }

    public ControlPoint getBbTopLeftControlPoint() {
        return boundingBoxControlPoints.get(3);
    }

    protected ControlPoint getPosControlPoint() {
        return posControlPoint;
    }

    protected ControlPoint getControlPoint( int index ) {
        if ( controlPoints.size == 0 )
            return null;
        return controlPoints.get( index % controlPoints.size);
    }

    public Controller.controlPointListener getControlPointListener() {
        return controlPointListener;
    }

    protected void setSelectionMode(ControlPointSelectionMode selectionMode) {
        this.selectionMode = selectionMode;
    }

    public void setControlPointListener(Controller.controlPointListener controlPointListener) {
        this.controlPointListener = controlPointListener;
    }

    public float getControlPointBaseSize() {
        return controlPointBaseSize;
    }

    public void setControlPointBaseSize(float controlPointBaseSize) {
        this.controlPointBaseSize = controlPointBaseSize;
        ControlPoint.setSize( controlPointBaseSize * cameraZoom );
    }

    public void setCameraZoom(float cameraZoom) {
        this.cameraZoom = cameraZoom;
        ControlPoint.setSize( controlPointBaseSize * cameraZoom );
    }

    protected abstract void translateRendererToObject();
    protected abstract void draw();
    protected abstract Vector2 stageToObject( Vector2 stageCoord );
    protected abstract void updateControlPointFromObject(ControlPoint cp);
    protected abstract void moveControlPoint( ControlPoint cp, Vector2 offsetLocal, Vector2 offsetStage );
    protected abstract void movePosControlPoint( ControlPoint cp, Vector2 offsetLocal, Vector2 offsetStage );
    protected abstract void rotateAtControlPoint(ControlPoint cp, float angle);
    protected abstract Object getControlledObject();
    protected abstract void updatePosControlPointFromObject(ControlPoint cp);


    protected BoundingBox getBoundingBox() {

        final BoundingBox bb = new BoundingBox();

        if ( controlPoints.size == 0 )
            return bb;

        bb.minX = 999999;
        bb.minY = 999999;
        bb.maxX = -999999;
        bb.maxY = -999999;


        for ( ControlPoint cp : controlPoints) {
            if ( cp.getX() < bb.minX )
                bb.minX = cp.getX();
            if ( cp.getX() > bb.maxX )
                bb.maxX = cp.getX();
            if ( cp.getY() < bb.minY )
                bb.minY = cp.getY();
            if ( cp.getY() > bb.maxY )
                bb.maxY = cp.getY();
        }

        return bb;
    }

    protected void updateBoundingBox() {

        BoundingBox b = getBoundingBox( );

        if ( !getBbDownLeftControlPoint().isSelected() )
            getBbDownLeftControlPoint().setPos( b.minX - ControlPoint.getSize(),
                b.minY - ControlPoint.getSize());
        if ( !getBbDownRightControlPoint().isSelected() )
            getBbDownRightControlPoint().setPos( b.maxX + ControlPoint.getSize(),
                b.minY - ControlPoint.getSize() );
        if ( !getBbTopRightControlPoint().isSelected() )
            getBbTopRightControlPoint().setPos( b.maxX + ControlPoint.getSize(),
                b.maxY + ControlPoint.getSize() );
        if ( !getBbTopLeftControlPoint().isSelected() )
            getBbTopLeftControlPoint().setPos( b.minX - ControlPoint.getSize(),
                b.maxY + ControlPoint.getSize() );
    }

    protected void moveBbControlPoint(ControlPoint movingPoint, ControlPoint basePoint,
                                      Vector2 localOffset, Vector2 stageOffset ) {
        movingPoint.offsetPos( localOffset.x, localOffset.y );

        float xdist = movingPoint.getX() - basePoint.getX();
        float ydist = movingPoint.getY() - basePoint.getY();

        if ( xdist > 0 ) {
            xdist -= ControlPoint.getSize();
        } else {
            xdist += ControlPoint.getSize();
        }

        if ( ydist > 0) {
            ydist -= ControlPoint.getSize();
        } else {
            ydist += ControlPoint.getSize();
        }

        for ( ControlPoint cp : controlPoints ) {
            float xd = cp.getX() - basePoint.getX();
            float yd = cp.getY() - basePoint.getY();
            cp.offsetPos( offsetLocal.x * xd / xdist, offsetLocal.y * yd / ydist);
        }


        updateBoundingBox();

    }

    protected void drawControlPoints() {



        for( ControlPoint cp : controlPoints ) {
            if ( !cp.isSelected() )
                updateControlPointFromObject(cp);
            if ( !cp.isVisible() )
                continue;
            cp.draw( shapeRenderer );
        }

        if ( posControlPoint != null ) {
            if ( !posControlPoint .isSelected() ) {
                updatePosControlPointFromObject(posControlPoint);
            }
            if ( !posControlPoint.isVisible() )
                return;
            posControlPoint.draw( shapeRenderer, ControlPoint.ShapeType._Circle );
        }

        drawBbControlPoints();
    }

    protected void drawBbControlPoints() {
        if ( !enableBbControl )
            return;

        updateBoundingBox();

        for( ControlPoint cp : boundingBoxControlPoints ) {
            if ( !cp.isVisible() )
                continue;
            cp.draw( shapeRenderer );
        }

        shapeRenderer.setColor( boundingBoxControlPoints.get(0).getColor() );
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line );
        for ( int i = 0; i < 4; i++ ) {
            shapeRenderer.line( boundingBoxControlPoints.get(i).getX(),
                                boundingBoxControlPoints.get(i).getY(),
                                boundingBoxControlPoints.get( (i+1) % 4).getX(),
                                boundingBoxControlPoints.get( (i+1) % 4).getY()
                    );
        }
        shapeRenderer.end();
    }

    protected boolean removeControlPoint( ControlPoint cp ) {
        return controlPoints.removeValue( cp, true );
    }


    public void render() {
        shapeRenderer.setProjectionMatrix( stage.getBatch().getProjectionMatrix() );
        shapeRenderer.setTransformMatrix( stage.getBatch().getTransformMatrix() );
        translateRendererToObject();
        draw();
    }


    protected boolean updateSelection(Vector2 coords) {

        if ( selectedControlPoint != null ) {
            selectedControlPoint.setSelected(false);
            selectedControlPoint = null;
        }

        for ( ControlPoint cp : controlPoints) {

            if ( cp.contains( coords ) && cp.isVisible() ) {
                cp.setSelected( true );
                selectedControlPoint = cp;
                return true;
            }
        }

        if ( posControlPoint != null ) {
            if ( posControlPoint.contains( coords ) && posControlPoint.isVisible() ) {
                selectedControlPoint = posControlPoint;
                return true;
            }
        }

        if ( enableBbControl ) {
            for (ControlPoint cp : boundingBoxControlPoints) {

                if (cp.contains(coords) && cp.isVisible()) {
                    cp.setSelected(true);
                    selectedControlPoint = cp;
                    return true;
                }
            }
        }

        return false;
    }

    protected boolean onMouseClicked( Vector2 localCoord, Vector2 stageCoord, int button ) {
        //dumb
        return false;
    }

    protected boolean onMouseDoubleClicked( Vector2 localCoord, Vector2 stageCoord, int button ) {
        return false;
    }


    private final Vector2 downLocalPos = new Vector2();
    private final Vector2 downStagePos = new Vector2();

    private final Vector2 offsetLocal = new Vector2();
    private final Vector2 offsetStage = new Vector2();

    private final Vector2 localCoord = new Vector2();


    private boolean controlPointMovingEnabled = false;

    public boolean touchDown( Vector2 stageCoord ) {
        localCoord.set(stageCoord);
        stageToObject(localCoord);

        controlPointMovingEnabled = false;

        switch ( selectionMode ) {

            case SELECT_BY_CLICK:

                if ( Gdx.input.isKeyPressed( Input.Keys.CONTROL_LEFT ) )
                    break;

                if ( Gdx.input.isKeyPressed( Input.Keys.SHIFT_LEFT ) )
                    break;

                if ( updateSelection(localCoord) )
                    controlPointMovingEnabled = true;
                break;
            case PRESSED_ONLY:
                if ( updateSelection(localCoord) )
                    controlPointMovingEnabled = true;
                break;
        }

        downStagePos.set( stageCoord );
        downLocalPos.set( localCoord );

        if ( selectedControlPoint != null ) {
            return true;
        }

        return false;
    }

    private enum MoveDir {
        Free,
        Vertical,
        Horizontal
    }

    private MoveDir moveDir = MoveDir.Free;

    private static final Vector2 tVec1 = new Vector2();
    private static final Vector2 tVec2 = new Vector2();

    public boolean touchDragged( Vector2 stageCoord ) {

        localCoord.set(stageCoord);
        stageToObject(localCoord);

        if ( selectedControlPoint == null )
            return false;

        offsetLocal.set( localCoord ).sub( downLocalPos );
        offsetStage.set( stageCoord ).sub( downStagePos );

        if ( !controlPointMovingEnabled ) {

            downStagePos.set( stageCoord );
            downLocalPos.set( localCoord );
            return true;
        }

        if ( Gdx.input.isKeyPressed( Input.Keys.SHIFT_LEFT ) && (moveDir == MoveDir.Free) ) {
            if ( Math.abs(offsetStage .x) < Math.abs( offsetStage.y) ) {
                moveDir = MoveDir.Vertical;
            } else  {
                moveDir = MoveDir.Horizontal;
            }
        } else if ( !Gdx.input.isKeyPressed( Input.Keys.SHIFT_LEFT ) ) {
            moveDir = MoveDir.Free;
        }

        switch ( moveDir ) {
            case Free:
                break;
            case Vertical:
                offsetStage.set(0, offsetStage.y);
                break;
            case Horizontal:
                offsetStage.set(offsetStage.x, 0);
                break;
        }

        if ( moveDir != MoveDir.Free ) {
            offsetLocal.set( offsetStage );
            stageToObject( offsetLocal );
        }


        if ( Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) ) {

            float ang = stageCoord.angle() - downStagePos.angle();
            rotateAtControlPoint(selectedControlPoint, ang);

        } else {

            if ( selectedControlPoint == posControlPoint ) {
                movePosControlPoint( selectedControlPoint, offsetLocal, offsetStage );
            } else if ( selectedControlPoint == getBbDownLeftControlPoint() ) {
                moveBbControlPoint( selectedControlPoint, getBbTopRightControlPoint(),
                        offsetLocal, offsetStage);
            } else if ( selectedControlPoint == getBbDownRightControlPoint() ) {
                moveBbControlPoint( selectedControlPoint, getBbTopLeftControlPoint(),
                        offsetLocal, offsetStage);
            } else if ( selectedControlPoint == getBbTopLeftControlPoint() ) {
                moveBbControlPoint( selectedControlPoint, getBbDownRightControlPoint(),
                        offsetLocal, offsetStage);
            } else if ( selectedControlPoint == getBbTopRightControlPoint() ) {
                moveBbControlPoint( selectedControlPoint, getBbDownLeftControlPoint(),
                        offsetLocal, offsetStage);
            } else {
                moveControlPoint( selectedControlPoint, offsetLocal, offsetStage );
            }
        }


        if ( controlPointListener != null ) {
            controlPointListener.changed( getControlledObject(), selectedControlPoint );
        }

        downStagePos.set( stageCoord );
        downLocalPos.set( localCoord );
        return true;

    }

    public boolean touchUp( Vector2 stageCoord, int button ) {

        localCoord.set(stageCoord);
        stageToObject(localCoord);

        boolean res = false;

        switch ( selectionMode ) {
            case SELECT_BY_CLICK:
                if ( selectedControlPoint != null )
                    res = true;
                break;
            case PRESSED_ONLY:
                if ( selectedControlPoint != null ) {
                    selectedControlPoint.setSelected(false);
                    res = true;
                }
                selectedControlPoint = null;
                break;
        }
        moveDir = MoveDir.Free;

        return res;
    }

    public boolean mouseClicked( Vector2 stageCoord, int button ) {
        localCoord.set(stageCoord);
        stageToObject(localCoord);

        if (onMouseClicked(localCoord, stageCoord, button))
            return true;
        return false;
    }

    public boolean mouseDoubleClicked( Vector2 stageCoord, int button ) {
        localCoord.set( stageCoord );
        stageToObject( localCoord );
        if ( onMouseDoubleClicked( localCoord, stageCoord, button ) )
            return true;
        return false;
    }

    protected void setControlPointVisible( ControlPoint cp, boolean state ) {
        cp.setVisible( state );
        if ( cp.isSelected() ) {
            selectedControlPoint = null;
            cp.setSelected( false );
        }
    }
}
