package model.entities;

import model.Profile;
import model.characters.EpsilonModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static controller.UserInterfaceController.*;
import static controller.constants.AbilityConstants.*;
import static controller.constants.EntityConstants.SKILL_COOLDOWN_IN_MINUTES;

public enum Skill {
    ARES, ACESO, PROTEUS;

    private static Skill activeSkill = null;
    private boolean acquired = false;
    private long lastSkillTime = 0;

    public static void initializeSkills() {
        setActiveSkill(findSkill(Profile.getCurrent().getActiveSkillSaveName()));
        CopyOnWriteArrayList<Skill> acquiredSkillSave = new CopyOnWriteArrayList<>();
        for (String skillName : Profile.getCurrent().getAcquiredSkillsNames()) acquiredSkillSave.add(findSkill(skillName));
        for (Skill skill : acquiredSkillSave) skill.setAcquired(true);
    }

    public static Skill getActiveSkill() {
        return activeSkill;
    }

    public static void setActiveSkill(Skill activeSkill) {
        Skill.activeSkill = activeSkill;
    }

    public String getName() {
        return "WRIT OF " + name();
    }

    public int getCost() {
        return switch (this) {

            case ARES -> 750;
            case ACESO -> 500;
            case PROTEUS -> 1000;
        };
    }

    public SkillType getType() {
        return switch (this) {

            case ARES -> SkillType.ATTACK;
            case ACESO -> SkillType.GUARD;
            case PROTEUS -> SkillType.POLYMORPHIA;
        };
    }

    public ActionListener getAction() {
        return switch (this) {

            case ARES -> e -> {
                Profile.getCurrent().setEpsilonMeleeDamage((int) (Profile.getCurrent().getEpsilonMeleeDamage() + WRIT_OF_ARES_BUFF_AMOUNT.getValue()));
                Profile.getCurrent().setEpsilonRangedDamage((int) (Profile.getCurrent().getEpsilonRangedDamage() + WRIT_OF_ARES_BUFF_AMOUNT.getValue()));
            };
            case ACESO -> e -> {
                Timer healthTimer = new Timer((int) WRIT_OF_ACESO_HEALING_FREQUENCY.getValue(), null);
                healthTimer.addActionListener(e1 -> {
                    if (isGameRunning()) EpsilonModel.getINSTANCE().addHealth((int) WRIT_OF_ACESO_HEALING_AMOUNT.getValue());
                    if (!isGameOn()) healthTimer.stop();
                });
                healthTimer.start();
            };
            case PROTEUS -> e -> EpsilonModel.getINSTANCE().addVertex();
        };
    }

    public void fire() {
        long now = System.nanoTime();
        if (now - getLastSkillTime() >= TimeUnit.MINUTES.toNanos(SKILL_COOLDOWN_IN_MINUTES.getValue())) {
            getAction().actionPerformed(new ActionEvent(new Object(), ActionEvent.ACTION_PERFORMED, null));
            setLastSkillTime(now);
        }
    }

    public boolean isAcquired() {
        return acquired;
    }

    public void setAcquired(boolean acquired) {
        this.acquired = acquired;
    }

    public long getLastSkillTime() {
        return lastSkillTime;
    }

    public void setLastSkillTime(long lastSkillTime) {
        this.lastSkillTime = lastSkillTime;
    }

    public enum SkillType {
        ATTACK, GUARD, POLYMORPHIA
    }
}
