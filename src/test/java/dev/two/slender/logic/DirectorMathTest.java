package dev.two.slender.logic;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class DirectorMathTest {
    @Test
    void formulasMatchSpec() {
        assertTrue(DirectorMath.dormant(0, 59));
        assertFalse(DirectorMath.dormant(0, 60));
        assertFalse(DirectorMath.dormant(1, 5));
        assertEquals(3, DirectorMath.aggression(2, 95, false), 1e-9);
        assertEquals(8, DirectorMath.aggression(7, 500, false), 1e-9);
        assertEquals(10, DirectorMath.aggression(8, 0, true), 1e-9);
        assertEquals(12, DirectorMath.teleportIntervalSeconds(0), 1e-9);
        assertEquals(4, DirectorMath.teleportIntervalSeconds(10), 1e-9);
        assertEquals(35, DirectorMath.teleportDistance(0), 1e-9);
        assertEquals(7, DirectorMath.teleportDistance(10), 1e-9);
        assertEquals(0.05, DirectorMath.creepSpeedPerTick(0), 1e-9);
        assertEquals(0.21, DirectorMath.creepSpeedPerTick(8), 1e-9);
        assertEquals(0, DirectorMath.frontAppearChance(2), 1e-9);
        assertEquals(0.18, DirectorMath.frontAppearChance(3), 1e-9);
        assertEquals(0.6, DirectorMath.frontAppearChance(10), 1e-9);
    }

    @Test
    void candidatesAreBehindAndInBounds() {
        Vec3 target = new Vec3(0, 65, 0);
        Vec3 view = new Vec3(0, 0, 1);
        DirectorMath.Bounds b = new DirectorMath.Bounds(-30, -30, 30, 30);
        List<Vec3> cs = DirectorMath.teleportCandidates(target, view, 20, 24, 100, new Random(7), b);
        assertFalse(cs.isEmpty());
        for (Vec3 c : cs) {
            assertTrue(SightMath.horizontalAngleDeg(target, view, c) >= 100 - 1e-6, "delante: " + c);
            assertEquals(20, c.sub(target).horizontal().length(), 1e-6);
            assertEquals(65, c.y(), 1e-9);
            assertTrue(b.contains(c));
        }
    }

    @Test
    void candidatesOutsideBoundsAreDropped() {
        DirectorMath.Bounds tiny = new DirectorMath.Bounds(-5, -5, 5, 5);
        assertTrue(DirectorMath.teleportCandidates(new Vec3(0, 65, 0), new Vec3(0, 0, 1), 20, 24, 100, new Random(1), tiny).isEmpty());
    }

    @Test
    void lookingStraightDownStillProducesCandidates() {
        DirectorMath.Bounds b = new DirectorMath.Bounds(-100, -100, 100, 100);
        assertEquals(10, DirectorMath.teleportCandidates(new Vec3(0, 65, 0), new Vec3(0, -1, 0), 10, 10, 100, new Random(3), b).size());
    }

    @Test
    void frontAndStep() {
        Vec3 f = DirectorMath.frontPosition(new Vec3(0, 65, 0), new Vec3(0, -0.5, 1), 5);
        assertEquals(5, f.z(), 1e-9); assertEquals(65, f.y(), 1e-9);
        Vec3 s = DirectorMath.stepTowards(new Vec3(0, 70, 0), new Vec3(10, 65, 0), 2);
        assertEquals(2, s.x(), 1e-9); assertEquals(70, s.y(), 1e-9);
        Vec3 arrive = DirectorMath.stepTowards(new Vec3(0, 70, 0), new Vec3(1, 65, 0), 2);
        assertEquals(1, arrive.x(), 1e-9);
    }

    @Test
    void creepIsSplitIntoShortSubSteps() {
        List<Vec3> pts = DirectorMath.subSteps(new Vec3(0, 70, 0), new Vec3(10, 65, 0), 2.1, 0.5);
        assertEquals(5, pts.size());
        Vec3 prev = new Vec3(0, 70, 0);
        for (Vec3 p : pts) {
            assertTrue(p.sub(prev).horizontal().length() <= 0.5 + 1e-9, "paso demasiado largo: " + p);
            assertEquals(70, p.y(), 1e-9);
            prev = p;
        }
        assertEquals(2.1, pts.get(pts.size() - 1).x(), 1e-9);
    }

    @Test
    void subStepsStopAtTheTarget() {
        List<Vec3> pts = DirectorMath.subSteps(new Vec3(0, 70, 0), new Vec3(1, 70, 0), 2.1, 0.5);
        assertEquals(2, pts.size());
        assertEquals(1, pts.get(1).x(), 1e-9);
        assertTrue(DirectorMath.subSteps(new Vec3(0, 70, 0), new Vec3(0, 70, 0), 2, 0.5).isEmpty());
    }

    @Test
    void pageSelectionIsDistinctAndBounded() {
        List<Integer> spots = new ArrayList<>();
        for (int i = 0; i < 12; i++) spots.add(i);
        List<Integer> chosen = PageSelector.choose(spots, 8, new Random(5));
        assertEquals(8, chosen.size());
        assertEquals(8, new HashSet<>(chosen).size());
        assertEquals(chosen, PageSelector.choose(spots, 8, new Random(5)));
        assertEquals(3, PageSelector.choose(List.of(1, 2, 3), 8, new Random(5)).size());
        assertEquals(12, spots.size(), "no modifica la lista original");
    }
}
