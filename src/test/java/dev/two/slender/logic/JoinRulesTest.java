package dev.two.slender.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JoinRulesTest {
    @Test
    void firstPlayerChoosesMode() {
        assertEquals(JoinRules.Result.OK, JoinRules.check(null, "asim", true, 0, 9));
        assertEquals("asim", JoinRules.resolveMode(null, "asim"));
    }

    @Test
    void laterPlayersJoinExistingMode() {
        assertEquals(JoinRules.Result.OK_MODE_FORCED, JoinRules.check("coop", "asim", true, 2, 8));
        assertEquals("coop", JoinRules.resolveMode("coop", "asim"));
        assertEquals(JoinRules.Result.OK, JoinRules.check("coop", "coop", true, 2, 8));
        assertEquals(JoinRules.Result.OK, JoinRules.check("coop", null, true, 2, 8));
    }

    @Test
    void runningOrFullGamesRefuse() {
        assertEquals(JoinRules.Result.RUNNING, JoinRules.check("coop", "coop", false, 2, 8));
        assertEquals(JoinRules.Result.FULL, JoinRules.check("coop", "coop", true, 8, 8));
    }

    @Test
    void defaultModeIsCoopAndAliasesWork() {
        assertEquals("coop", JoinRules.resolveMode(null, null));
        assertEquals("coop", JoinRules.normalizeMode("Cooperativo"));
        assertEquals("asim", JoinRules.normalizeMode("asimetrico"));
        assertEquals("asim", JoinRules.normalizeMode("ASYM"));
        assertNull(JoinRules.normalizeMode("zombies"));
    }
}
