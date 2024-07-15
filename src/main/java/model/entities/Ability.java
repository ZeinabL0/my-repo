package model.entities;

import model.characters.EpsilonModel;

import javax.swing.*;
import java.awt.event.ActionListener;

import static controller.constants.AbilityConstants.*;
import static controller.constants.EntityConstants.EPSILON_SHOOTING_RAPIDITY;
import static model.collision.Collision.emitImpactWave;

public enum Ability {
    HEPHAESTUS, ATHENA, APOLLO;

    public String getName() {
        return switch (this) {

            case HEPHAESTUS -> "O' HEPHAESTUS, BANISH";
            case ATHENA -> "O' ATHENA, EMPOWER";
            case APOLLO -> "O' APOLLO, HEAL";
        };
    }

    public int getCost() {
        return switch (this) {

            case HEPHAESTUS -> 100;
            case ATHENA -> 75;
            case APOLLO -> 50;
        };
    }

    public ActionListener getAction() {
        return switch (this) {

            case HEPHAESTUS -> e -> emitImpactWave(EpsilonModel.getINSTANCE().getAnchor(), HEPHAESTUS_ABILITY_WAVE_POWER.getValue());
            case ATHENA -> e -> {
                EpsilonModel.getINSTANCE().setShootingRapidity((int) ATHENA_ABILITY_SHOOTING_RAPIDITY.getValue());
                Timer timer=new Timer((int) ATHENA_ABILITY_TIME.getValue(), e1 -> EpsilonModel.getINSTANCE().setShootingRapidity(EPSILON_SHOOTING_RAPIDITY.getValue()));
                timer.setRepeats(false);
                timer.start();
            };
            case APOLLO -> e -> EpsilonModel.getINSTANCE().addHealth((int) APOLLO_ABILITY_HEALING_AMOUNT.getValue());
        };
    }
}
