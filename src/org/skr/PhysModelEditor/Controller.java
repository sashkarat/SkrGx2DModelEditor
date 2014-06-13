package org.skr.PhysModelEditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

/**
 * Created by rat on 03.06.14.
 */
public abstract  class Controller  {

    public interface controlPointListener {
        public void changed ( Object controlledObject, ControlPoint controlPoint);
    }


    public enum CpType {
        FREE,
        CENTER,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        TOP_RIGHT,
        TOP_LEFT
    }

    public class ControlPoint {
        private CpType type = CpType.FREE;
        private float x = 0;
        private float y = 0;
        private boolean selected = false;
        private Object object = null;

        public ControlPoint(CpType type) {
            this.type = type;
        }

        public ControlPoint(CpType type, Object object) {
            this.type = type;
            this.object = object;
        }

        public CpType getType() {
            return type;
        }

        public void setType(CpType type) {
            this.type = type;
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

        public void offsetPos(float xOffset, float yOffset) {
            this.x += xOffset;
            this.y += yOffset;
        }
    }


    ShapeRenderer shapeRenderer = new ShapeRenderer();
    Stage stage;
    Array<ControlPoint> controlPoints = new Array<ControlPoint>();
    ControlPoint selectedControlPoint = null;
    controlPointListener controlPointListener;

    protected float controlPointSize = 10;
    protected float cameraZoom = 1;
    private float cpSize = 10;

    protected Controller(Stage stage) {
        this.stage = stage;
    }


    public Controller.controlPointListener getControlPointListener() {
        return controlPointListener;
    }

    public void setControlPointListener(Controller.controlPointListener controlPointListener) {
        this.controlPointListener = controlPointListener;
    }

    public float getControlPointSize() {
        return controlPointSize;
    }

    public void setControlPointSize(float controlPointSize) {
        this.controlPointSize = controlPointSize;
        cpSize = controlPointSize * cameraZoom;
    }

    public void setCameraZoom(float cameraZoom) {
        this.cameraZoom = cameraZoom;
        cpSize = controlPointSize * cameraZoom;
    }

    protected abstract void translateRendererToObject();
    protected abstract void draw();
    protected abstract Vector2 stageToObject( Vector2 stageCoord );
    protected abstract void updateControlPoint( ControlPoint cp );
    protected abstract void moveControlPoint( ControlPoint cp, Vector2 offsetLocal, Vector2 offsetStage );
    protected abstract void rotateAtControlPoint(ControlPoint cp, float angle);
    protected abstract Object getControlledObject();

    protected void drawControlPoints() {
        for( ControlPoint cp : controlPoints ) {
            if ( !cp.isSelected() )
                updateControlPoint( cp );

            if ( cp.isSelected() ) {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            } else {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            }
            shapeRenderer.rect( cp.getX() - cpSize/2, cp.getY() - cpSize/2, cpSize, cpSize);
            shapeRenderer.end();
        }
    }

    public void render() {
        shapeRenderer.setProjectionMatrix( stage.getBatch().getProjectionMatrix() );
        shapeRenderer.setTransformMatrix( stage.getBatch().getTransformMatrix() );
        translateRendererToObject();
        draw();
    }

    protected void updateSelection(Vector2 coords) {

        final RectangleExt r = new RectangleExt();

        for ( ControlPoint cp : controlPoints) {
            r.set( cp.getX() - cpSize/2, cp.getY() - cpSize/2, cpSize, cpSize);

            if ( r.contains( coords ) ) {
                cp.setSelected( true );
                selectedControlPoint = cp;
                return;
            }
        }
    }


    private final Vector2 downLocalPos = new Vector2();
    private final Vector2 downStagePos = new Vector2();

    private final Vector2 offsetLocal = new Vector2();
    private final Vector2 offsetStage = new Vector2();

    private final Vector2 localCoord = new Vector2();



    public void touchDragged( Vector2 stageCoord ) {

        localCoord.set(stageCoord);
        stageToObject(localCoord);

        if ( selectedControlPoint != null ) {
            offsetLocal.set( localCoord ).sub( downLocalPos );
            offsetStage.set( stageCoord ).sub( downStagePos );

            if ( Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) ) {
                float ang = localCoord.angle() - downLocalPos.angle();
                rotateAtControlPoint(selectedControlPoint, ang);

            } else {
                moveControlPoint(selectedControlPoint, offsetLocal, offsetStage);
            }

            if ( controlPointListener != null ) {
                controlPointListener.changed( getControlledObject(), selectedControlPoint );
            }
        }

        downStagePos.set( stageCoord );
        downLocalPos.set( localCoord );

    }

    public void touchDown( Vector2 stageCoord ) {
        localCoord.set(stageCoord);
        stageToObject(localCoord);
        updateSelection( localCoord );

        downStagePos.set( stageCoord );
        downLocalPos.set( localCoord );
    }

    public void touchUp( Vector2 stageCoord ) {
        localCoord.set(stageCoord);
        stageToObject(localCoord);

        if ( selectedControlPoint != null ) {
            selectedControlPoint.setSelected( false );
            selectedControlPoint = null;
        }
    }

}
