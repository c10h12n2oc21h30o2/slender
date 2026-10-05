package dev.two.slender.night;

/** Habilidades nuevas de Slender en LA NOCHE ENTERA y la fase (t) en que se estrenan. */
public enum Ability {
    LIGHTS(2), SPRINT(3), HEADS(4), FLASH(5), MIMIC(6), CHASE(7);

    private final int unlock;

    Ability(int unlock) {
        this.unlock = unlock;
    }

    public int unlock() {
        return this.unlock;
    }
}
