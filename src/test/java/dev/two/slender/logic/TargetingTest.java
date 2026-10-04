package dev.two.slender.logic;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class TargetingTest {
    private final UUID a = new UUID(0, 1), b = new UUID(0, 2), c = new UUID(0, 3), d = new UUID(0, 4), e = new UUID(0, 5);

    private Map<UUID, Vec3> group(Vec3 loner) {
        Map<UUID, Vec3> m = new LinkedHashMap<>();
        m.put(a, new Vec3(0, 65, 0)); m.put(b, new Vec3(2, 65, 0)); m.put(c, new Vec3(0, 65, 3));
        m.put(d, new Vec3(3, 65, 3)); m.put(e, loner);
        return m;
    }

    @Test
    void lonerWithFivePlayersBecomesPriorityAfterFortySeconds() {
        IsolationTracker t = new IsolationTracker(4, 25, 40);
        for (int i = 0; i < 39; i++) t.update(group(new Vec3(50, 65, 0)), 1);
        assertFalse(t.isIsolated(e));
        t.update(group(new Vec3(50, 65, 0)), 1);
        assertTrue(t.isIsolated(e));
        assertEquals(Optional.of(e), t.priorityTarget());
        assertFalse(t.isIsolated(a));
    }

    @Test
    void fourPlayersNeverTriggerIsolation() {
        IsolationTracker t = new IsolationTracker(4, 25, 40);
        Map<UUID, Vec3> four = group(new Vec3(80, 65, 0));
        four.remove(d);
        for (int i = 0; i < 100; i++) t.update(four, 1);
        assertEquals(0, t.seconds(e), 1e-9);
        assertTrue(t.priorityTarget().isEmpty());
    }

    @Test
    void rejoiningTheGroupDecaysTwiceAsFast() {
        IsolationTracker t = new IsolationTracker(4, 25, 40);
        for (int i = 0; i < 40; i++) t.update(group(new Vec3(50, 65, 0)), 1);
        for (int i = 0; i < 10; i++) t.update(group(new Vec3(5, 65, 5)), 1);
        assertEquals(20, t.seconds(e), 1e-9);
    }

    @Test
    void playersThatLeaveAreForgotten() {
        IsolationTracker t = new IsolationTracker(4, 25, 40);
        for (int i = 0; i < 45; i++) t.update(group(new Vec3(50, 65, 0)), 1);
        Map<UUID, Vec3> without = group(new Vec3(50, 65, 0));
        without.remove(e);
        t.update(without, 1);
        assertEquals(0, t.seconds(e), 1e-9);
    }

    @Test
    void weightsFollowSpec() {
        assertEquals(1.0, TargetSelector.weight(new TargetSelector.Candidate(a, 0, false, false)), 1e-9);
        assertEquals(1.9, TargetSelector.weight(new TargetSelector.Candidate(a, 2, true, false)), 1e-9);
        assertEquals(9.5, TargetSelector.weight(new TargetSelector.Candidate(a, 2, true, true)), 1e-9);
    }

    @Test
    void pickFavoursIsolatedPlayer() {
        List<TargetSelector.Candidate> cs = List.of(
            new TargetSelector.Candidate(a, 0, false, false),
            new TargetSelector.Candidate(b, 0, false, true));
        Random r = new Random(42);
        int bCount = 0;
        for (int i = 0; i < 6000; i++) if (TargetSelector.pick(cs, r).orElseThrow().equals(b)) bCount++;
        assertEquals(5000, bCount, 200);
        assertTrue(TargetSelector.pick(List.of(), r).isEmpty());
    }

    @Test
    void retargetRules() {
        assertTrue(TargetSelector.shouldRetarget(null, false, 0, Optional.empty()));
        assertTrue(TargetSelector.shouldRetarget(a, false, 1, Optional.empty()));
        assertFalse(TargetSelector.shouldRetarget(a, true, 19, Optional.empty()));
        assertTrue(TargetSelector.shouldRetarget(a, true, 20, Optional.empty()));
        assertTrue(TargetSelector.shouldRetarget(a, true, 1, Optional.of(b)));
        assertFalse(TargetSelector.shouldRetarget(a, true, 1, Optional.of(a)));
    }
}
