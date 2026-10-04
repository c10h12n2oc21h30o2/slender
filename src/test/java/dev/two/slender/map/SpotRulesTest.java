package dev.two.slender.map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.two.slender.map.builders.hogwarts.HogwartsBuilder;
import dev.two.slender.map.builders.hogwarts.HogwartsMaps;
import dev.two.slender.map.builders.hogwarts.HogwartsSections;
import org.junit.jupiter.api.Test;

class SpotRulesTest {
    @Test
    void unknownMapsAllowEverything() {
        assertTrue(SpotRules.allows("bosque", 1, 2, 3));
    }

    /** En Hogwarts Slender solo aparece en suelo al que llegan los jugadores: ni tras un derrumbe ni en roca. */
    @Test
    void hogwartsOnlyOnReachableFloor() {
        HogwartsMaps.all();   // registra las reglas, como al arrancar
        for (HogwartsSections.Section s : HogwartsSections.resource()) {
            HogwartsBuilder b = new HogwartsBuilder(s);
            BlockPos o = b.origin(0);
            int ox = o.x() + HogwartsBuilder.DX;
            int oy = o.y() + HogwartsBuilder.DY;
            int oz = o.z() + HogwartsBuilder.DZ;
            for (Loc l : s.spawns()) {
                assertTrue(SpotRules.allows(s.id(), ox + (int) Math.floor(l.x()), oy + (int) l.y(), oz + (int) Math.floor(l.z())),
                        s.id() + " spawn " + l);
            }
            for (HogwartsSections.Seal c : s.seal()) {
                assertFalse(SpotRules.allows(s.id(), ox + c.pos().x(), oy + c.pos().y(), oz + c.pos().z()), s.id() + " derrumbe " + c);
            }
            assertFalse(SpotRules.allows(s.id(), ox, oy + 254, oz), s.id());
        }
    }
}
