package org.skr.PhysModelEditor.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import org.skr.PhysModelEditor.ModShapeRenderer;
import org.skr.PhysModelEditor.RectangleExt;

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


    ModShapeRenderer shapeRenderer = new ModShapeRenderer();
    Stage stage;
    Array<ControlPoint> controlPoints = new Array<ControlPoint>();
    ControlPoint posControlPoint = null;
    ControlPoint selectedControlPoint = null;
    controlPointListener controlPointListener;
    ControlPointSelectionMode selectionMode = ControlPointSelectionMode.PRESSED_ONLY;

    protected float controlPointBaseSize = 10;
    protected float cameraZoom = 1;
    private float cpSize = 10;

    public Controller(Stage stage) {
        this.stage = stage;

        setPosControlPoint( new ControlPoint( null ) );
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
    }

    protected void removeControlPoint( ControlPoint cp ) {
        controlPoints.removeValue( cp, true );
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

        return false;
    }

    protected void onMouseClicked( Vector2 localCoord, Vector2 stageCoord ) {
        // dumb
    }


    private final Vector2 downLocalPos = new Vector2();
    private final Vector2 downStagePos = new Vector2();

    private final Vector2 offsetLocal = new Vector2();
    private final Vector2 offsetStage = new Vector2();

    private final Vector2 localCoord = new Vector2();


    private boolean mouseMoved = false;
    private boolean controlPointMovingEnabled = false;

    public void touchDown( Vector2 stageCoord ) {
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
    }

    public void touchDragged( Vector2 stageCoord ) {

        mouseMoved = true;

        localCoord.set(stageCoord);
        stageToObject(localCoord);

        if ( selectedControlPoint != null ) {
            offsetLocal.set( localCoord ).sub( downLocalPos );
            offsetStage.set( stageCoord ).sub( downStagePos );

            if ( Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) ) {
                float ang = localCoord.angle() - downLocalPos.angle();
                rotateAtControlPoint(selectedControlPoint, ang);

            } else {
                if ( controlPointMovingEnabled ) {
                    if ( selectedControlPoint == posControlPoint ) {
                        movePosControlPoint( selectedControlPoint, offsetLocal, offsetStage );
                    } else {
                        moveControlPoint( selectedControlPoint, offsetLocal, offsetStage );
                    }

                    if ( controlPointListener != null ) {
                        controlPointListener.changed( getControlledObject(), selectedControlPoint );
                    }
                }
            }
        }

        downStagePos.set( stageCoord );
        downLocalPos.set( localCoord );

    }

    public void touchUp( Vector2 stageCoord ) {
        localCoord.set(stageCoord);
        stageToObject(localCoord);

        switch ( selectionMode ) {

            case SELECT_BY_CLICK:
                break;
            case PRESSED_ONLY:
                if ( selectedControlPoint != null )
                    selectedControlPoint.setSelected( false );
                selectedControlPoint = null;
                break;
        }


        if ( !mouseMoved ) {
            onMouseClicked( localCoord, stageCoord);
        }

        mouseMoved = false;
    }

    protected void setControlPointVisible( ControlPoint cp, boolean state ) {
        cp.setVisible( state );
        if ( cp.isSelected() ) {
            selectedControlPoint = null;
            cp.setSelected( false );
        }
    }
}
