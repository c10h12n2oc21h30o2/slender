package dev.two.slender.game;

import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

/**
 * Enganche de LA NOCHE ENTERA en una partida. Con hooks a null, Game se comporta como siempre. Los métodos con cuerpo
 * por defecto no hacen nada; solo páginas, tiempo y agresividad son obligatorios.
 */
public interface PhaseHooks {
    int pages(int spots);

    int timeSeconds(int pages);

    double aggression(int collected, int total, double secsSincePage, boolean escape);

    default boolean dormantAllowed() {
        return true;
    }

    default double staticFactor() {
        return 1.0;
    }

    /** Juega la fase como espectador desde el principio (perdidos). */
    default boolean startsAsSpectator(UUID id) {
        return false;
    }

    default void onStart(Game g) {
    }

    /** Cada 2 ticks de juego. */
    default void onTick(Game g) {
    }

    default void onSecond(Game g) {
    }

    default void onKill(Game g, Player p) {
    }

    /** Un superviviente activo se va (sale del servidor o cambia de mundo) en mitad de la fase. */
    default void onLeave(Game g, UUID id, Location last) {
    }

    default void onEscape(Game g, Player p) {
    }

    /** true = manejado (Game no sigue con su lógica). */
    default boolean onInteractBlock(Game g, Player p, Block b) {
        return false;
    }

    /** Qué hacer al intentar coger un objeto: IGNORE = no es de la fase; ALLOW = cogerlo; DENY = no cogerlo. */
    enum Pickup { IGNORE, ALLOW, DENY }

    default Pickup onPickup(Game g, Player p, Item item) {
        return Pickup.IGNORE;
    }

    default boolean onInteractEntity(Game g, Player p, Entity e) {
        return false;
    }

    /** La partida ha terminado y se ha limpiado; los jugadores ya no están en ella. */
    default void onFinish(Game g, PhaseResult r) {
    }

    default void onCleanup(Game g) {
    }
}
