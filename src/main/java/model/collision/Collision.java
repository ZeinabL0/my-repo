package model.collision;

import model.MotionPanelModel;
import model.Profile;
import model.characters.CollectibleModel;
import model.characters.EpsilonModel;
import model.characters.GeoShapeModel;
import model.entities.AttackTypes;
import model.entities.Entity;
import model.movement.Direction;
import model.projectiles.BulletModel;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static controller.constants.ImpactConstants.IMPACT_RADIUS;
import static controller.constants.ImpactConstants.IMPACT_SCALE;
import static model.MotionPanelModel.getMainMotionPanelModel;
import static model.Utils.*;
import static model.characters.GeoShapeModel.allShapeModelsList;

public final class Collision implements Runnable {
    private static Collision INSTANCE = null;

    public static Collision getINSTANCE() {
        if (INSTANCE == null) INSTANCE = new Collision();
        return INSTANCE;
    }

    /**
     * @return a thread-safe hashmap of post-collision data of all GeoShapes (direction,scale,rotation)
     */
    public static List<MovementState.ShapeMovementState> evaluateMovementEffects(MovementState.CollisionState state, boolean artificialImpact) {
        CopyOnWriteArrayList<MovementState.ShapeMovementState> out = new CopyOnWriteArrayList<>();
        for (GeoShapeModel shapeModel : allShapeModelsList) {
            if (shapeModel instanceof BulletModel || (shapeModel instanceof CollectibleModel collectibleModel &&
                    state.stateOf1.collidable != collectibleModel.ancestor && state.stateOf2.collidable != collectibleModel.ancestor)) continue;

            float distance = (float) shapeModel.getMovement().getAnchor().distance(state.collisionPoint);
            float scale = (distance > IMPACT_RADIUS.getValue()) ? 0 : (IMPACT_SCALE.getValue() * IMPACT_RADIUS.getValue() - distance) / IMPACT_RADIUS.getValue();
            Direction direction = new Direction(relativeLocation(shapeModel.getMovement().getAnchor(), state.collisionPoint));
            float torque = 0;
            if (!artificialImpact) {
                if (shapeModel == state.stateOf1.collidable) {
                    direction = state.stateOf1.direction;
                    scale *= state.stateOf1.scale;
                    torque = scale * state.stateOf1.torque;
                }
                if (shapeModel == state.stateOf2.collidable) {
                    direction = state.stateOf2.direction;
                    scale *= state.stateOf2.scale;
                    torque = scale * state.stateOf2.torque;
                }
            }
            out.add(new MovementState.ShapeMovementState(shapeModel,direction,torque,scale));
        }
        return out;
    }

    public static void emitImpactWave(MovementState.CollisionState state, boolean artificialImpact, float wavePower) {
        List<MovementState.ShapeMovementState> collisionData = evaluateMovementEffects(state, artificialImpact);
        for (MovementState.ShapeMovementState movementState : collisionData) {
            movementState.geoShapeModel.getMovement().impact(movementState.direction, wavePower * movementState.scale);
            if (movementState.torque != 0) movementState.geoShapeModel.getMovement().setAngularSpeed(movementState.torque);
        }
    }

    public static void emitImpactWave(Point2D collisionPoint, float wavePower) {
        emitImpactWave(new MovementState.CollisionState(collisionPoint), true, wavePower);
    }

    public static void emitImpactWave(MovementState.CollisionState state) {
        emitImpactWave(state, false, 1);
    }

    public static void evaluatePhysicalEffects(MovementState.CollisionState state) {
        if (state.stateOf1.collidable instanceof Entity entity1 && state.stateOf2.collidable instanceof Entity entity2 && state.collisionPoint != null) {
            Pair<Boolean,Boolean> meleePair=checkMelee(state);
            if (meleePair.getLeft() && meleePair.getRight()) return;
            if (entity1.isVulnerable() && (state.stateOf2.collidable instanceof BulletModel || state.stateOf1.collidable instanceof CollectibleModel || meleePair.getRight())) {
                entity2.damage(entity1, AttackTypes.MELEE);
            }
            if (entity2.isVulnerable() && (state.stateOf1.collidable instanceof BulletModel || state.stateOf2.collidable instanceof CollectibleModel || meleePair.getLeft())) {
                entity1.damage(entity2, AttackTypes.MELEE);
            }
        }
    }
    public static Pair<Boolean,Boolean> checkMelee(MovementState.CollisionState state){
        boolean melee1to2 = state.stateOf1.collidable.isGeometryVertex(toCoordinate(state.collisionPoint)) != null &&
                (state.stateOf1.collidable instanceof EpsilonModel || state.stateOf2.collidable instanceof EpsilonModel);
        boolean melee2to1 = state.stateOf2.collidable.isGeometryVertex(toCoordinate(state.collisionPoint)) != null &&
                (state.stateOf1.collidable instanceof EpsilonModel || state.stateOf2.collidable instanceof EpsilonModel);
        return new MutablePair<>(melee1to2,melee2to1);
    }
    public static void resolveCollectiblePickup(MovementState.CollisionState state){
        if (state.stateOf1.collidable instanceof EpsilonModel && state.stateOf2.collidable instanceof CollectibleModel) {
            Profile.getCurrent().setCurrentGameXP(Profile.getCurrent().getCurrentGameXP() + ((CollectibleModel) state.stateOf2.collidable).getValue());
        }
        if (state.stateOf2.collidable instanceof EpsilonModel && state.stateOf1.collidable instanceof CollectibleModel) {
            Profile.getCurrent().setCurrentGameXP(Profile.getCurrent().getCurrentGameXP() + ((CollectibleModel) state.stateOf1.collidable).getValue());
        }
    }

    @Override
    public void run() {
        Collidable.CreateAllGeometries();
        List<MovementState.CollisionState> collisionStates = getAllMomentaryCollisions();
        for (MovementState.CollisionState state : collisionStates) {
            boolean notNull=state.stateOf1!=null && state.stateOf2!=null;
            if (!notNull) continue;
            if (state.stateOf1.collidable instanceof BulletModel bulletModel) bulletModel.eliminate();
            if (state.stateOf2.collidable instanceof BulletModel bulletModel) bulletModel.eliminate();
            evaluatePhysicalEffects(state);
            resolveCollectiblePickup(state);
            boolean areBulletMotionPanel =  areInstancesOf(state.stateOf1.collidable, state.stateOf2.collidable, MotionPanelModel.class, BulletModel.class);
            boolean areEpsilonCollectible = areInstancesOf(state.stateOf1.collidable, state.stateOf2.collidable, EpsilonModel.class, CollectibleModel.class);
            if (areBulletMotionPanel) getMainMotionPanelModel().extend(roundPoint(state.collisionPoint));
            else if (!areEpsilonCollectible) emitImpactWave(state);
        }
    }
    public List<MovementState.CollisionState> getAllMomentaryCollisions(){
        CopyOnWriteArrayList<MovementState.CollisionState> collisionStates = new CopyOnWriteArrayList<>();
        for (int i = 0; i < Collidable.collidables.size(); i++) {
            for (int j = i + 1; j < Collidable.collidables.size(); j++) {
                MovementState.CollisionState state;
                state = Collidable.collidables.get(i).checkCollision(Collidable.collidables.get(j));
                if (state != null) collisionStates.add(state);
            }
        }
        return collisionStates;
    }
}