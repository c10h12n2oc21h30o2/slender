package dev.two.slender.logic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SightMathTest {
    private static final double EPS = 1e-6;

    @Test
    void yawPitchFollowsMinecraftConvention() {
        Vec3 south = Vec3.fromYawPitch(0f, 0f);
        assertEquals(0, south.x(), EPS); assertEquals(0, south.y(), EPS); assertEquals(1, south.z(), EPS);
        Vec3 west = Vec3.fromYawPitch(90f, 0f);
        assertEquals(-1, west.x(), EPS); assertEquals(0, west.z(), EPS);
        Vec3 down = Vec3.fromYawPitch(0f, 90f);
        assertEquals(-1, down.y(), EPS);
        assertEquals(90f, Vec3.yawOf(new Vec3(-1, 0, 0)), 1e-3);
        assertEquals(0f, Vec3.yawOf(new Vec3(0, 0, 1)), 1e-3);
    }

    @Test
    void rotateYawMatchesYawOffset() {
        Vec3 r = new Vec3(0, 0, 1).rotateYaw(90);
        assertEquals(-1, r.x(), EPS); assertEquals(0, r.z(), EPS);
    }

    @Test
    void targetStraightAheadIsInCone() {
        Vec3 eye = new Vec3(0, 65, 0);
        Vec3 dir = new Vec3(0, 0, 1);
        assertTrue(SightMath.inCone(eye, dir, new Vec3(1, 65, 20), 30, 48));
    }

    @Test
    void targetBehindOrTooFarIsNotInCone() {
        Vec3 eye = new Vec3(0, 65, 0);
        Vec3 dir = new Vec3(0, 0, 1);
        assertFalse(SightMath.inCone(eye, dir, new Vec3(0, 65, -10), 30, 48));
        assertFalse(SightMath.inCone(eye, dir, new Vec3(0, 65, 60), 30, 48));
        assertFalse(SightMath.inCone(eye, dir, new Vec3(20, 65, 10), 30, 48));
    }

    @Test
    void horizontalAngleIgnoresHeight() {
        Vec3 eye = new Vec3(0, 65, 0);
        Vec3 dir = new Vec3(0, -0.7, 0.7);
        assertEquals(0, SightMath.horizontalAngleDeg(eye, dir, new Vec3(0, 90, 10)), 1e-3);
        assertEquals(180, SightMath.horizontalAngleDeg(eye, dir, new Vec3(0, 65, -10)), 1e-3);
    }

    @Test
    void degenerateVectorsAreSafe() {
        assertEquals(180, SightMath.angleBetweenDeg(Vec3.ZERO, new Vec3(1, 0, 0)), EPS);
        assertTrue(SightMath.inCone(Vec3.ZERO, new Vec3(0, 0, 1), Vec3.ZERO, 10, 5));
    }
}
