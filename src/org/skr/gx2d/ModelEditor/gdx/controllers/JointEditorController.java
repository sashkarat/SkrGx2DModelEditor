package org.skr.gx2d.ModelEditor.gdx.controllers;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import org.skr.gx2d.common.Env;
import org.skr.gx2d.editor.Controller;
import org.skr.gx2d.node.Node;
import org.skr.gx2d.physnodes.BodyHandler;
import org.skr.gx2d.physnodes.JointHandler;
import org.skr.gx2d.physnodes.PhysSet;
import org.skr.gx2d.physnodes.physdef.JointDefinition;
import org.skr.gx2d.utils.RectangleExt;

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

        public AcpType type;

        public AnchorControlPoint(Object object, AcpType type ) {
            super(object);
            this.type = type;
        }
    }

    public interface BodyHandlerSelectionListener {
        public void bodyHandlerSelected(BodyHandler bodyItem, boolean isA);
    }

    JointDefinition jhDef;
    PhysSet physSet;
    BodyHandler bhA = null;
    BodyHandler bhB = null;
    JointHandler jhA = null;
    JointHandler jhB = null;

    BodyHandlerSelectionListener bhSelectionListener;

    AnchorControlPoint cpAnchorA;
    AnchorControlPoint cpAnchorB;
    AnchorControlPoint cpGAnchorA;
    AnchorControlPoint cpGAnchorB;
    AnchorControlPoint cpAxis;

    boolean bhSelectionEnabled = false;
    boolean selectBodyHandlerA = true;

    public JointEditorController(Stage stage) {
        super(stage);
        createControlPoints();
        setPosControlPoint( null );
        setEnableBbControl( false );
    }

    public void setBodyHandlerSelectionListener(BodyHandlerSelectionListener bodyItemSelectionListener) {
        this.bhSelectionListener = bodyItemSelectionListener;
    }

    public void setBodyHandlerSelectionEnabled(boolean enabled, boolean isA) {
        bhSelectionEnabled = enabled;
        selectBodyHandlerA = isA;
    }

    public void setBodyHandlerA(BodyHandler bodyHandler) {
        if ( jhDef == null )
            return;
        bhA = bodyHandler;
        if ( bhA != null )
            jhDef.setBodyAId( bhA.getId() );
        else
            jhDef.setBodyAId( -1 );
    }

    public void setBodyHandlerB(BodyHandler bodyHandler) {
        if ( jhDef == null )
            return;
        bhB = bodyHandler;
        if ( bhB != null )
            jhDef.setBodyBId( bhB.getId() );
        else
            jhDef.setBodyBId( -1 );
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

    public JointDefinition getJhDef() {
        return jhDef;
    }

    public AnchorControlPoint getCpAnchorA() {
        return cpAnchorA;
    }

    public AnchorControlPoint getCpAnchorB() {
        return cpAnchorB;
    }

    public AnchorControlPoint getCpGAnchorA() {
        return cpGAnchorA;
    }

    public AnchorControlPoint getCpGAnchorB() {
        return cpGAnchorB;
    }

    public AnchorControlPoint getCpAxis() {
        return cpAxis;
    }

    public void setJointHandler(JointHandler jointHandler) {
        bhSelectionEnabled = false;
        bhA = null;
        bhB = null;
        this.physSet = jointHandler.getPhysSet();
        jhDef = jointHandler.getJhDef();

        cpAnchorA.setVisible( true );
        cpAnchorB.setVisible( false );
        cpAxis.setVisible(false);
        cpGAnchorA.setVisible( false );
        cpGAnchorB.setVisible( false );

        switch ( jhDef.getType() ) {
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
                cpAxis.setVisible( true );
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
        bhA = physSet.getBodyHandler().getBodyHandler(jhDef.getBodyAId());
        bhB = physSet.getBodyHandler().getBodyHandler(jhDef.getBodyBId());
        jhA = physSet.getJointHandler().getJointHandler(jhDef.getJointAId());
        jhB = physSet.getJointHandler().getJointHandler(jhDef.getJointBId());

        updateControlPointFromObject( cpAnchorA );
        updateControlPointFromObject( cpAnchorB );
        updateControlPointFromObject( cpAxis );
        updateControlPointFromObject( cpGAnchorA );
        updateControlPointFromObject( cpGAnchorB );
    }

    public void setAnchorControlPoint( AnchorControlPoint.AcpType type, Vector2 c ) {
        AnchorControlPoint acp = null;
        switch ( type ) {
            case typeA:
                cpAnchorA.setPos( c.x, c.y );
                acp = cpAnchorA;
                break;
            case typeB:
                cpAnchorB.setPos( c.x, c.y );
                acp = cpAnchorB;
                break;
            case typeAxis:
                cpAxis.setPos( c.x, c.y );
                acp = cpAxis;
                break;
            case typeGrndA:
                cpGAnchorA.setPos( c.x, c.y );
                acp = cpGAnchorA;
                break;
            case typeGrndB:
                cpGAnchorB.setPos( c.x, c.y );
                acp = cpGAnchorB;
                break;
        }
        updateDescriptionFromControlPoint( acp );
    }


    @Override
    protected boolean onMouseClicked(Vector2 localCoord, Vector2 stageCoord, int button) {
        if ( !bhSelectionEnabled)
            return super.onMouseClicked(localCoord, stageCoord, button);
        if ( button == Input.Buttons.LEFT && getSelectedControlPoint() == null ) {
            return processBodyHandlerSelection(stageCoord);
        }

        return false;
    }

    private boolean processBodyHandlerSelection( Vector2 stageCoord ) {
        BodyHandler selection = null;

        for ( Node node : physSet.getBodyHandler() ) {
            BodyHandler bh = (BodyHandler) node;
            if ( bh.getBodyBoundingBox().contains( stageCoord ) ) {
                selection = bh;
                break;
            }
        }

        if ( selection == null )
            return false;

        bhSelectionEnabled = false;

        if ( selectBodyHandlerA )
            setBodyHandlerA(selection);
        else
            setBodyHandlerB(selection);

        if ( bhSelectionListener != null ) {
            bhSelectionListener.bodyHandlerSelected(selection, selectBodyHandlerA);
        }

        return true;
    }

    @Override
    protected void translateRendererToObject() {

    }


    protected void drawBodyHandlerA() {
        if ( bhA == null )
            return;
        getShapeRenderer().setColor( 1, 0, 0, 1 );
        RectangleExt bb = bhA.getBodyBoundingBox();
        getShapeRenderer().rect( bb.getLeft(), bb.getBottom(),
                bb.getWidth(), bb.getHeight() );
    }

    protected void drawBodyHandlerB() {
        if ( bhA == null )
            return;
        getShapeRenderer().setColor( 0, 1, 0, 1 );
        RectangleExt bb = bhB.getBodyBoundingBox();
        getShapeRenderer().rect( bb.getLeft(), bb.getBottom(),
                bb.getWidth(), bb.getHeight() );
    }


    protected void drawBiAToAnchALine() {
        if ( bhA == null )
            return;
        getShapeRenderer().setColor( 1, 0.2f, 0, 1 );
        Vector2 c = bhA.getBody().getWorldCenter();
        Env.get().world.toView(c);
        getShapeRenderer().line( c.x, c.y, cpAnchorA.getX(), cpAnchorA.getY() );
    }

    protected void drawBiBToAnchBLine() {
        if ( bhB == null )
            return;
        getShapeRenderer().setColor(0.2f, 1, 0, 1);
        Vector2 c = bhB.getBody().getWorldCenter();
        Env.get().world.toView(c);
        getShapeRenderer().line( c.x, c.y, cpAnchorB.getX(), cpAnchorB.getY() );
    }

    protected void drawBiBToAnchALine() {
        if ( bhB == null )
            return;
        getShapeRenderer().setColor(0.2f, 1, 0, 1);
        Vector2 c = bhB.getBody().getWorldCenter();
        Env.get().world.toView(c);
        getShapeRenderer().line( c.x, c.y, cpAnchorA.getX(), cpAnchorA.getY() );
    }

    protected void drawAnchABLine() {
        getShapeRenderer().setColor(0.8f, 0.8f, 0, 1);
        getShapeRenderer().line( cpAnchorA.getX(), cpAnchorA.getY(),
                cpAnchorB.getX(), cpAnchorB.getY() );
    }


    protected void drawAxis() {

        getShapeRenderer().setColor( 1, 1, 1, 1 );

        if ( bhA == null ) {
            getShapeRenderer().line(0, 0, cpAxis.getX(), cpAxis.getY() );
            return;
        }

        Vector2 c = bhA.getBody().getWorldCenter();
        Env.get().world.toView(c);
        getShapeRenderer().line( c.x, c.y, cpAxis.getX(), cpAxis.getY() );
    }

    protected void drawAnchAGrndALine() {
        getShapeRenderer().setColor( 0.8f, 0.8f, 0, 1 );
        getShapeRenderer().line( cpAnchorA.getX(), cpAnchorA.getY(),
                cpGAnchorA.getX(), cpGAnchorA.getY() );
    }

    protected void drawAnchBGrndBLine() {
        getShapeRenderer().setColor( 0.8f, 0.8f, 0, 1 );
        getShapeRenderer().line( cpAnchorB.getX(), cpAnchorB.getY(),
                cpGAnchorB.getX(), cpGAnchorB.getY() );
    }

    protected void drawGrndABLine() {
        getShapeRenderer().setColor( 0.8f, 0.8f, 0, 1 );
        getShapeRenderer().line( cpGAnchorA.getX(), cpGAnchorA.getY(),
                cpGAnchorB.getX(), cpGAnchorB.getY() );
    }

    protected void drawBiABLine() {
        if ( bhA == null )
            return;
        if ( bhB == null )
            return;
        getShapeRenderer().setColor( 0.8f, 0.8f, 0.4f, 1);
        Vector2 c1 = bhA.getBody().getWorldCenter();
        Vector2 c2 = bhB.getBody().getWorldCenter();
        Env.get().world.toView( c1 );
        Env.get().world.toView(c2);
        getShapeRenderer().line( c1.x, c1.y, c2.x, c2.y );
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

    protected void drawPrismaticJoint() {
        drawAxis();
        drawBiAToAnchALine();
        drawBiBToAnchALine();
    }

    protected void drawPulleyJoint() {
        drawBiAToAnchALine();
        drawBiBToAnchBLine();
        drawAnchAGrndALine();
        drawAnchBGrndBLine();
        drawGrndABLine();
    }

    protected void drawWheelJoint() {
        drawBiAToAnchALine();
        drawBiBToAnchALine();
        drawAxis();
    }

    protected void drawWeldJoint() {
        drawBiAToAnchALine();
        drawBiBToAnchALine();
    }

    protected void drawRopeJoint() {
        drawBiAToAnchALine();
        drawBiBToAnchBLine();
        drawAnchABLine();
    }

    protected void drawFrictionJoint() {
        drawBiAToAnchALine();
        drawBiBToAnchALine();
    }

    protected void drawMotorJoint() {
        drawBiABLine();
    }

    @Override
    protected void drawLocal() {
        drawControlPoints();

        getShapeRenderer().begin(ShapeRenderer.ShapeType.Line );

        switch ( jhDef.getType() ) {
           case Unknown:
               break;
           case RevoluteJoint:
               drawRevoluteJoint();
               break;
           case PrismaticJoint:
               drawPrismaticJoint();
               break;
           case DistanceJoint:
               drawDistanceJoint();
               break;
           case PulleyJoint:
               drawPulleyJoint();
               break;
           case MouseJoint:
               break;
           case GearJoint:

               break;
           case WheelJoint:
               drawWheelJoint();
               break;
           case WeldJoint:
               drawWeldJoint();
               break;
           case FrictionJoint:
               drawFrictionJoint();
               break;
           case RopeJoint:
               drawRopeJoint();
               break;
           case MotorJoint:
               drawMotorJoint();
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
                acp.setPos( Env.get().world.toView( jhDef.getAnchorA().x ), Env.get().world.toView( jhDef.getAnchorA().y) );
                break;
            case typeB:
                acp.setPos( Env.get().world.toView( jhDef.getAnchorB().x ), Env.get().world.toView( jhDef.getAnchorB().y) );
                break;
            case typeAxis:
                if ( bhA != null ) {
                    float x = bhA.getBody().getWorldCenter().x + jhDef.getAxis().x;
                    float y = bhA.getBody().getWorldCenter().y + jhDef.getAxis().y;
                    acp.setPos( Env.get().world.toView( x ), Env.get().world.toView( y ) );
                } else {
                    acp.setPos( Env.get().world.toView( jhDef.getAxis().x ), Env.get().world.toView( jhDef.getAxis().y ) );
                }
                break;
            case typeGrndA:
                acp.setPos( Env.get().world.toView( jhDef.getGroundAnchorA().x ), Env.get().world.toView( jhDef.getGroundAnchorA().y) );
                break;
            case typeGrndB:
                acp.setPos( Env.get().world.toView( jhDef.getGroundAnchorB().x ), Env.get().world.toView( jhDef.getGroundAnchorB().y) );
                break;
        }
    }

    protected void updateDescriptionFromControlPoint( AnchorControlPoint acp ) {
        switch ( acp.type ) {
            case typeA:
                jhDef.getAnchorA().set( Env.get().world.toPhys( acp.getX() ), Env.get().world.toPhys( acp.getY() ) );
                break;
            case typeB:
                jhDef.getAnchorB().set( Env.get().world.toPhys( acp.getX() ), Env.get().world.toPhys( acp.getY() ) );
                break;
            case typeAxis:
                if ( bhA != null ) {
                    float x = Env.get().world.toPhys( acp.getX() );
                    float y = Env.get().world.toPhys( acp.getY() );
                    jhDef.getAxis().set( x, y ).sub( bhA.getBody().getWorldCenter() );
                } else {
                    jhDef.getAxis().set( Env.get().world.toPhys( acp.getX() ), Env.get().world.toPhys( acp.getY() ) );
                }
                break;
            case typeGrndA:
                jhDef.getGroundAnchorA().set( Env.get().world.toPhys( acp.getX() ), Env.get().world.toPhys( acp.getY() ) );
                break;
            case typeGrndB:
                jhDef.getGroundAnchorB().set( Env.get().world.toPhys( acp.getX() ), Env.get().world.toPhys( acp.getY() ) );
                break;
        }
    }

    @Override
    protected void moveControlPoint(ControlPoint cp, Vector2 offsetLocal, Vector2 offsetStage, final Vector2 posLocal, final Vector2 posStage ) {
//        cp.offsetPos( offsetStage.x, offsetStage.y );

        cp.setPos( posStage.x, posStage.y );

        snapToGrid( cp, 5, 5, 2 );

        AnchorControlPoint acp = (AnchorControlPoint) cp;
        if ( acp.type == AnchorControlPoint.AcpType.typeA && bhA != null ) {
            Vector2 c = bhA.getBody().getWorldCenter();
            Env.get().world.toView(c);
            Controller.snapTo( acp, c, 10 );
        } else if ( acp.type == AnchorControlPoint.AcpType.typeB && bhB != null ) {
            Vector2 c = bhB.getBody().getWorldCenter();
            Env.get().world.toView(c);
            Controller.snapTo( acp, c, 10 );
        }

        updateDescriptionFromControlPoint((AnchorControlPoint) cp);
    }

    @Override
    protected void movePosControlPoint(ControlPoint cp, Vector2 offsetLocal, Vector2 offsetStage, final Vector2 posLocal, final Vector2 posStage ) {

    }

    @Override
    protected void rotateAtControlPoint(ControlPoint cp, float angle) {

    }

    @Override
    protected Object getControlledObject() {
        return jhDef;
    }

    @Override
    protected void updatePosControlPointFromObject(ControlPoint cp) {

    }
}
