package org.skr.PhysModelEditor.gdx.editor.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import org.skr.gdx.PhysWorld;
import org.skr.gdx.editor.Controller;
import org.skr.gdx.physmodel.PhysModel;
import org.skr.gdx.physmodel.bodyitem.BiScSet;
import org.skr.gdx.physmodel.bodyitem.BodyItem;
import org.skr.gdx.physmodel.jointitem.JointItem;
import org.skr.gdx.physmodel.jointitem.JointItemDescription;
import org.skr.gdx.physmodel.jointitem.JointItemFactory;
import org.skr.gdx.utils.RectangleExt;

/**
 * Created by rat on 19.10.14.
 */
public class JointEditorController extends Controller {
    public static class AnchorControlPoint extends ControlPoint {

        public enum AcpType {
            typeA,
            typeB,
            typeAxis,
            typeGrndA,
            typeGrndB
        }

        AcpType type;

        public AnchorControlPoint(Object object, AcpType type ) {
            super(object);
            this.type = type;
        }
    }

    public interface BodyItemSelectionListener {
        public void bodyItemSelected( BodyItem bodyItem, boolean isA );
    }

    JointItemDescription jiDesc;
    PhysModel model;
    BodyItem biA = null;
    BodyItem biB = null;
    JointItem jiA = null;
    JointItem jiB = null;

    BodyItemSelectionListener bodyItemSelectionListener;


    AnchorControlPoint cpAnchorA;
    AnchorControlPoint cpAnchorB;
    AnchorControlPoint cpGAnchorA;
    AnchorControlPoint cpGAnchorB;
    AnchorControlPoint cpAxis;

    boolean bodyItemSelectionEnabled = false;
    boolean selectBodyItemA = true;

    public JointEditorController(Stage stage) {
        super(stage);
        createControlPoints();
        setPosControlPoint( null );
        setEnableBbControl( false );
    }

    public void setBodyItemSelectionListener(BodyItemSelectionListener bodyItemSelectionListener) {
        this.bodyItemSelectionListener = bodyItemSelectionListener;
    }

    public void setBodyItemSelectionEnabled(boolean enabled, boolean isA) {
        bodyItemSelectionEnabled = enabled;
        selectBodyItemA = isA;
    }

    public void setBodyItem( BodyItem bodyItem, boolean isA ) {
        if ( jiDesc == null )
            return;
        if ( isA ) {
            biA = bodyItem;
            if ( biA != null )
                jiDesc.setBodyAId( biA.getId() );
            else
                jiDesc.setBodyAId( -1 );
        } else {
            biB = bodyItem;
            if ( biB != null )
                jiDesc.setBodyBId( biB.getId() );
            else
                jiDesc.setBodyBId( -1 );
        }


    }

    void createControlPoints() {

        getControlPoints().clear();

        cpAnchorA = new AnchorControlPoint( null, AnchorControlPoint.AcpType.typeA );
        getControlPoints().add(cpAnchorA);
        cpAnchorA.setColor(new Color(1, 0, 0, 1));

        cpAnchorB = new AnchorControlPoint( null, AnchorControlPoint.AcpType.typeB );
        getControlPoints().add(cpAnchorB);
        cpAnchorB.setColor(new Color(0, 1, 0, 1));

        cpAxis = new AnchorControlPoint( null, AnchorControlPoint.AcpType.typeAxis);
        getControlPoints().add(cpAxis);
        cpAxis.setColor(new Color(0, 1, 1, 1));

        cpGAnchorA = new AnchorControlPoint( null, AnchorControlPoint.AcpType.typeGrndA );
        getControlPoints().add(cpGAnchorA);
        cpGAnchorA.setColor(new Color(1f, 0.2f, 0.7f, 1));

        cpGAnchorB = new AnchorControlPoint( null, AnchorControlPoint.AcpType.typeGrndB );
        getControlPoints().add(cpGAnchorB);
        cpGAnchorB.setColor(new Color(0.2f, 1f, 0.7f, 1));
    }

    public JointItemDescription getJiDesc() {
        return jiDesc;
    }

    public PhysModel getModel() {
        return model;
    }

    public void setJointItem( JointItem jointItem) {
        bodyItemSelectionEnabled = false;
        biA = null;
        biB = null;
        this.model = jointItem.getBiScSet().getModel();
        jiDesc = jointItem.getJointItemDescription();
        if ( jiDesc == null ) {
            jiDesc = new JointItemDescription();
            jiDesc.setType( JointItemFactory.getJointType( jointItem ) );
        }

        cpAnchorA.setVisible( true );
        cpAnchorB.setVisible( false );
        cpAxis.setVisible(false);
        cpGAnchorA.setVisible( false );
        cpGAnchorB.setVisible( false );

        switch ( jiDesc.getType() ) {
            case Unknown:
                break;
            case RevoluteJoint:
                break;
            case PrismaticJoint:
                cpAxis.setVisible(true);
                break;
            case DistanceJoint:
                cpAnchorB.setVisible( true );
                break;
            case PulleyJoint:
                cpAnchorB.setVisible( true );
                cpGAnchorA.setVisible( true );
                cpGAnchorB.setVisible( true );
                break;
            case MouseJoint:
                break;
            case GearJoint:
                cpAnchorA.setVisible( false );
                break;
            case WheelJoint:
                break;
            case WeldJoint:
                break;
            case FrictionJoint:
                break;
            case RopeJoint:
                cpAnchorB.setVisible( true );
                break;
            case MotorJoint:
                cpAnchorA.setVisible( false );
                break;
        }
        biA = model.findBodyItem( jiDesc.getBodyAId() );
        biB = model.findBodyItem( jiDesc.getBodyBId() );
        jiA = model.findJointItem( jiDesc.getJointAId() );
        jiB = model.findJointItem( jiDesc.getJointBId() );
    }

    @Override
    protected boolean onMouseClicked(Vector2 localCoord, Vector2 stageCoord, int button) {
        if ( !bodyItemSelectionEnabled )
            return super.onMouseClicked(localCoord, stageCoord, button);
        if ( button == Input.Buttons.LEFT && getSelectedControlPoint() == null ) {
            return processBodyItemSelection( stageCoord );
        }

        return false;
    }

    private boolean processBodyItemSelection( Vector2 stageCoord ) {
        if ( model == null )
            return false;
        BodyItem selection = null;
        BiScSet currentSet = model.getScBodyItems().getCurrentSet();
        if ( currentSet == null )
            return false;
        for ( BodyItem bi : currentSet.getBodyItems() ) {
            if ( bi.getBodyItemBoundingBox().contains( stageCoord ) ) {
                selection = bi;
                break;
            }
        }

        if ( selection == null )
            return false;

        bodyItemSelectionEnabled = false;


        setBodyItem( selection, selectBodyItemA );

        if ( bodyItemSelectionListener != null ) {
            bodyItemSelectionListener.bodyItemSelected( selection, selectBodyItemA );
        }

        return true;
    }

    @Override
    protected void translateRendererToObject() {

    }


    protected void drawBodyItemA() {
        if ( biA == null )
            return;
        getShapeRenderer().setColor( 1, 0, 0, 1 );
        RectangleExt bb = biA.getBodyItemBoundingBox();
        getShapeRenderer().rect( bb.getLeft(), bb.getBottom(),
                bb.getWidth(), bb.getHeight() );
    }

    protected void drawBodyItemB() {
        if ( biA == null )
            return;
        getShapeRenderer().setColor( 0, 1, 0, 1 );
        RectangleExt bb = biB.getBodyItemBoundingBox();
        getShapeRenderer().rect( bb.getLeft(), bb.getBottom(),
                bb.getWidth(), bb.getHeight() );
    }


    protected void drawBiAToAnchALine() {
        if ( biA == null )
            return;
        getShapeRenderer().setColor( 1, 0.2f, 0, 1 );
        Vector2 c = biA.getBody().getWorldCenter();
        PhysWorld.get().toView( c );
        getShapeRenderer().line( c.x, c.y, cpAnchorA.getX(), cpAnchorA.getY() );
    }

    protected void drawBiBToAnchBLine() {
        if ( biB == null )
            return;
        getShapeRenderer().setColor(0.2f, 1, 0, 1);
        Vector2 c = biB.getBody().getWorldCenter();
        PhysWorld.get().toView( c );
        getShapeRenderer().line( c.x, c.y, cpAnchorB.getX(), cpAnchorB.getY() );
    }

    protected void drawBiBToAnchALine() {
        if ( biB == null )
            return;
        getShapeRenderer().setColor(0.2f, 1, 0, 1);
        Vector2 c = biB.getBody().getWorldCenter();
        PhysWorld.get().toView( c );
        getShapeRenderer().line( c.x, c.y, cpAnchorA.getX(), cpAnchorA.getY() );
    }

    protected void drawAnchABLine() {
        getShapeRenderer().setColor(0.8f, 0.8f, 0, 1);
        getShapeRenderer().line( cpAnchorA.getX(), cpAnchorA.getY(),
                cpAnchorB.getX(), cpAnchorB.getY() );
    }

    protected void drawRevoluteJoint() {
        drawBiAToAnchALine();
        drawBiBToAnchALine();
    }

    protected void drawDistanceJoint() {
        drawBiAToAnchALine();
        drawBiBToAnchBLine();
        drawAnchABLine();
    }

    @Override
    protected void drawLocal() {
        drawControlPoints();

        getShapeRenderer().begin(ShapeRenderer.ShapeType.Line );

        switch ( jiDesc.getType() ) {
           case Unknown:
               break;
           case RevoluteJoint:
               drawRevoluteJoint();
               break;
           case PrismaticJoint:
               break;
           case DistanceJoint:
               drawDistanceJoint();
               break;
           case PulleyJoint:
               break;
           case MouseJoint:
               break;
           case GearJoint:
               break;
           case WheelJoint:
               break;
           case WeldJoint:
               break;
           case FrictionJoint:
               break;
           case RopeJoint:
               break;
           case MotorJoint:
               break;
        }

        getShapeRenderer().end();

    }

    @Override
    protected Vector2 stageToObject(Vector2 stageCoord) {
        return stageCoord;
    }

    @Override
    protected void updateControlPointFromObject(ControlPoint cp) {
        AnchorControlPoint acp = (AnchorControlPoint) cp;
        switch ( acp.type ) {
            case typeA:
                acp.setPos( PhysWorld.get().toView( jiDesc.getAnchorA().x ), PhysWorld.get().toView( jiDesc.getAnchorA().y) );
                break;
            case typeB:
                acp.setPos( PhysWorld.get().toView( jiDesc.getAnchorB().x ), PhysWorld.get().toView( jiDesc.getAnchorB().y) );
                break;
            case typeAxis:
                if ( biA != null ) {
                    float x = biA.getBody().getWorldCenter().x + jiDesc.getAxis().x;
                    float y = biA.getBody().getWorldCenter().y + jiDesc.getAxis().y;
                    acp.setPos( PhysWorld.get().toView( x ), PhysWorld.get().toView( y ) );
                } else {
                    acp.setPos( PhysWorld.get().toView( jiDesc.getAxis().x ), PhysWorld.get().toView( jiDesc.getAxis().y ) );
                }
                break;
            case typeGrndA:
                acp.setPos( PhysWorld.get().toView( jiDesc.getGroundAnchorA().x ), PhysWorld.get().toView( jiDesc.getGroundAnchorA().y) );
                break;
            case typeGrndB:
                acp.setPos( PhysWorld.get().toView( jiDesc.getGroundAnchorB().x ), PhysWorld.get().toView( jiDesc.getGroundAnchorB().y) );
                break;
        }
    }

    protected void updateDescriptionFromControlPoint( AnchorControlPoint acp ) {
        switch ( acp.type ) {
            case typeA:
                jiDesc.getAnchorA().set( PhysWorld.get().toPhys( acp.getX() ), PhysWorld.get().toPhys( acp.getY() ) );
                break;
            case typeB:
                jiDesc.getAnchorB().set( PhysWorld.get().toPhys( acp.getX() ), PhysWorld.get().toPhys( acp.getY() ) );
                break;
            case typeAxis:
                if ( biA != null ) {
                    float x = PhysWorld.get().toPhys( acp.getX() );
                    float y = PhysWorld.get().toPhys( acp.getY() );
                    jiDesc.getAxis().set( x, y ).sub( biA.getBody().getWorldCenter() );
                } else {
                    jiDesc.getAxis().set( PhysWorld.get().toPhys( acp.getX() ), PhysWorld.get().toPhys( acp.getY() ) );
                }
                break;
            case typeGrndA:
                jiDesc.getGroundAnchorA().set( PhysWorld.get().toPhys( acp.getX() ), PhysWorld.get().toPhys( acp.getY() ) );
                break;
            case typeGrndB:
                jiDesc.getGroundAnchorB().set( PhysWorld.get().toPhys( acp.getX() ), PhysWorld.get().toPhys( acp.getY() ) );
                break;
        }
    }

    @Override
    protected void moveControlPoint(ControlPoint cp, Vector2 offsetLocal, Vector2 offsetStage) {
        cp.offsetPos( offsetStage.x, offsetStage.y );
        updateDescriptionFromControlPoint((AnchorControlPoint) cp);
    }

    @Override
    protected void movePosControlPoint(ControlPoint cp, Vector2 offsetLocal, Vector2 offsetStage) {

    }

    @Override
    protected void rotateAtControlPoint(ControlPoint cp, float angle) {

    }

    @Override
    protected Object getControlledObject() {
        return jiDesc;
    }

    @Override
    protected void updatePosControlPointFromObject(ControlPoint cp) {

    }
}
