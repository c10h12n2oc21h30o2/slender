package dev.two.slender.listener;

import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Los marcos fijos e invulnerables nunca disparan EntityDamageByEntityEvent, así que golpear una página
 * (lo normal en Bedrock) y el agarre de Slender tienen que ir por PrePlayerAttackEntityEvent.
 */
class GameListenerAttackTest {
    private static Method handlerFor(Class<?> event) {
        return Arrays.stream(GameListener.class.getDeclaredMethods())
            .filter(m -> m.isAnnotationPresent(EventHandler.class))
            .filter(m -> m.getParameterCount() == 1 && m.getParameterTypes()[0] == event)
            .findFirst().orElse(null);
    }

    @Test
    void attacksAreHandledBeforeVanillaDamageChecks() {
        Method m = handlerFor(PrePlayerAttackEntityEvent.class);
        assertNotNull(m, "falta un @EventHandler para PrePlayerAttackEntityEvent");
        assertFalse(m.getAnnotation(EventHandler.class).ignoreCancelled(),
            "debe ejecutarse aunque otro plugin (p. ej. PvP desactivado) cancele el ataque");
    }

    @Test
    void damageHandlerStillExistsAsSafetyNet() {
        assertNotNull(handlerFor(EntityDamageByEntityEvent.class));
    }
}
