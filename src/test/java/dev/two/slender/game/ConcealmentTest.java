package dev.two.slender.game;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Proxy;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class ConcealmentTest {
    /** Jugador falso que apunta quién está oculto para él. */
    private static final class Viewer {
        final UUID id = UUID.randomUUID();
        final Set<UUID> hidden = new HashSet<>();
        boolean online = true;
        final Player player = (Player) Proxy.newProxyInstance(Player.class.getClassLoader(), new Class<?>[]{Player.class},
            (proxy, m, args) -> switch (m.getName()) {
                case "getUniqueId" -> id;
                case "isOnline" -> online;
                case "hidePlayer" -> { hidden.add(((Player) args[args.length - 1]).getUniqueId()); yield null; }
                case "showPlayer" -> { hidden.remove(((Player) args[args.length - 1]).getUniqueId()); yield null; }
                case "hashCode" -> id.hashCode();
                case "equals" -> proxy == args[0];
                default -> null;
            });
    }

    @Test
    void revealShowsSlenderToEveryoneItWasHiddenFromEvenAfterTheyLeftTheGame() {
        Viewer slender = new Viewer(), a = new Viewer(), b = new Viewer();
        Concealment c = new Concealment(null);
        c.hide(slender.player, List.of(a.player, b.player));
        assertTrue(a.hidden.contains(slender.id) && b.hidden.contains(slender.id));
        c.reveal(slender.player);
        assertFalse(a.hidden.contains(slender.id), "A ya no estaba en la partida pero debe volver a verle");
        assertFalse(b.hidden.contains(slender.id));
    }

    @Test
    void revealToSingleViewerWhenASurvivorLeavesMidGame() {
        Viewer slender = new Viewer(), a = new Viewer(), b = new Viewer();
        Concealment c = new Concealment(null);
        c.hide(slender.player, List.of(a.player, b.player));
        c.revealTo(a.player, slender.player);
        assertFalse(a.hidden.contains(slender.id));
        assertTrue(b.hidden.contains(slender.id));
    }

    @Test
    void offlineViewersAreSkipped() {
        Viewer slender = new Viewer(), a = new Viewer();
        Concealment c = new Concealment(null);
        c.hide(slender.player, List.of(a.player));
        a.online = false;
        assertDoesNotThrow(() -> c.reveal(slender.player));
        assertTrue(a.hidden.contains(slender.id), "no se toca a un jugador desconectado");
    }
}
