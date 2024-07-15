package controller.constants;

import java.util.concurrent.TimeUnit;

public enum AbilityConstants {
    WRIT_OF_ACESO_HEALING_FREQUENCY, WRIT_OF_ACESO_HEALING_AMOUNT, WRIT_OF_ARES_BUFF_AMOUNT, ATHENA_ABILITY_SHOOTING_RAPIDITY,
    ATHENA_ABILITY_TIME, APOLLO_ABILITY_HEALING_AMOUNT, HEPHAESTUS_ABILITY_WAVE_POWER;

    public float getValue() {
        return switch (this) {

            case WRIT_OF_ACESO_HEALING_FREQUENCY -> TimeUnit.SECONDS.toNanos(1);
            case WRIT_OF_ACESO_HEALING_AMOUNT -> 1;
            case WRIT_OF_ARES_BUFF_AMOUNT -> 2;
            case ATHENA_ABILITY_SHOOTING_RAPIDITY -> 3;
            case ATHENA_ABILITY_TIME -> TimeUnit.SECONDS.toMillis(10);
            case APOLLO_ABILITY_HEALING_AMOUNT -> 10;
            case HEPHAESTUS_ABILITY_WAVE_POWER -> 1.5f;
        };
    }
}
