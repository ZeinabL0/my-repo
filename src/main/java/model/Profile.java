package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import model.entities.Skill;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings({"FieldCanBeLocal","FieldMayBeFinal"})
public class Profile {
    private static Profile current=new Profile("");
    public static final int UP_KEYCODE = KeyEvent.VK_W;
    public static final int DOWN_KEYCODE = KeyEvent.VK_S;
    public static final int LEFT_KEYCODE = KeyEvent.VK_A;
    public static final int RIGHT_KEYCODE = KeyEvent.VK_D;
    public static final int SKILL_KEYCODE = KeyEvent.VK_SHIFT;
    public static final int PAUSE_KEYCODE = KeyEvent.VK_ESCAPE;
    private String profileId;
    private List<Integer> waveEnemyCount = new CopyOnWriteArrayList<>(List.of(3,6,10,14,16));
    private int ups = 800;
    private int fps = 80;
    private int epsilonMeleeDamage = 10;
    private int epsilonRangedDamage = 5;
    private float soundScale = 6;
    private float sizeScale = 0.75f;
    private float gameSpeed = 1.8f;
    private int epsilonShootingRapidity = 1;
    private String activeSkillSaveName = "";
    private List<String> acquiredSkillsNames = new CopyOnWriteArrayList<>();
    private int totalXP = 600;
    private int currentGameXP = 300;

    @JsonCreator
    public Profile(@JsonProperty("profileId") String profileId, @JsonProperty("waveEnemyCount") List<Integer> waveEnemyCount, @JsonProperty("ups") int ups,
                   @JsonProperty("fps") int fps, @JsonProperty("epsilonMeleeDamage") int epsilonMeleeDamage, @JsonProperty("epsilonRangedDamage") int epsilonRangedDamage,
                   @JsonProperty("soundScale") float soundScale, @JsonProperty("sizeScale") float sizeScale, @JsonProperty("gameSpeed") float gameSpeed,
                   @JsonProperty("epsilonShootingRapidity") int epsilonShootingRapidity, @JsonProperty("activeSkillSaveName") String activeSkillSaveName,
                   @JsonProperty("acquiredSkillsNames") List<String> acquiredSkillsNames, @JsonProperty("totalXP") int totalXP, @JsonProperty("currentGameXP") int currentGameXP) {
        this.profileId = profileId;
        this.waveEnemyCount = waveEnemyCount;
        this.ups = ups;
        this.fps = fps;
        this.epsilonMeleeDamage = epsilonMeleeDamage;
        this.epsilonRangedDamage = epsilonRangedDamage;
        this.soundScale = soundScale;
        this.sizeScale = sizeScale;
        this.gameSpeed = gameSpeed;
        this.epsilonShootingRapidity = epsilonShootingRapidity;
        this.activeSkillSaveName = activeSkillSaveName;
        this.acquiredSkillsNames = acquiredSkillsNames;
        this.totalXP = totalXP;
        this.currentGameXP = currentGameXP;
    }

    public Profile(String profileId) {
        this.profileId = String.valueOf(profileId.hashCode());
    }

    public static Profile getCurrent() {
        return current;
    }

    public static void setCurrent(Profile current) {
        Profile.current = current;
    }

    public void updateINSTANCE() {
        this.setActiveSkillSaveName(Skill.getActiveSkill() != null ? Skill.getActiveSkill().getName() : "");
        this.getAcquiredSkillsNames().clear();
    }

    public void saveXP() {
        setTotalXP(getTotalXP() + Profile.getCurrent().getCurrentGameXP());
        setCurrentGameXP(0);
    }

    public String getProfileId() {
        return String.valueOf(profileId);
    }

    public List<Integer> getWaveEnemyCount() {
        return waveEnemyCount;
    }

    public int getUps() {
        return ups;
    }

    public int getFps() {
        return fps;
    }

    public int getEpsilonMeleeDamage() {
        return epsilonMeleeDamage;
    }

    public void setEpsilonMeleeDamage(int epsilonMeleeDamage) {
        this.epsilonMeleeDamage = epsilonMeleeDamage;
    }

    public int getEpsilonRangedDamage() {
        return epsilonRangedDamage;
    }

    public void setEpsilonRangedDamage(int epsilonRangedDamage) {
        this.epsilonRangedDamage = epsilonRangedDamage;
    }

    public float getSoundScale() {
        return soundScale;
    }

    public void setSoundScale(float soundScale) {
        this.soundScale = soundScale;
    }

    public float getSizeScale() {
        return sizeScale;
    }

    public float getGameSpeed() {
        return gameSpeed;
    }

    public void setGameSpeed(float gameSpeed) {
        this.gameSpeed = gameSpeed;
    }

    public int getEpsilonShootingRapidity() {
        return epsilonShootingRapidity;
    }

    public String getActiveSkillSaveName() {
        return activeSkillSaveName;
    }

    public void setActiveSkillSaveName(String activeSkillSaveName) {
        this.activeSkillSaveName = activeSkillSaveName;
    }

    public List<String> getAcquiredSkillsNames() {
        return acquiredSkillsNames;
    }

    public int getTotalXP() {
        return totalXP;
    }

    public void setTotalXP(int totalXP) {
        this.totalXP = totalXP;
    }

    public int getCurrentGameXP() {
        return currentGameXP;
    }

    public void setCurrentGameXP(int currentGameXP) {
        this.currentGameXP = currentGameXP;
    }
}
