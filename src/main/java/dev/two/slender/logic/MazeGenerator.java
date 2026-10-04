// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.logic;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Random;

public final class MazeGenerator
{
    private static final int[][] DIRS;
    
    private MazeGenerator() {
    }
    
    public static boolean[][] generate(final int n, final int n2, final double n3, final Random random) {
        final boolean[][] array = new boolean[2 * n + 1][2 * n2 + 1];
        final boolean[][] array2 = new boolean[n][n2];
        final ArrayDeque arrayDeque = new ArrayDeque();
        arrayDeque.push(new int[] { 0, 0 });
        array2[0][0] = true;
        array[1][1] = true;
        while (!arrayDeque.isEmpty()) {
            final int[] array3 = (int[])arrayDeque.peek();
            final ArrayList list = new ArrayList();
            for (final int[] array4 : MazeGenerator.DIRS) {
                final int n4 = array3[0] + array4[0];
                final int n5 = array3[1] + array4[1];
                if (n4 >= 0 && n5 >= 0 && n4 < n && n5 < n2 && !array2[n4][n5]) {
                    list.add(new int[] { n4, n5 });
                }
            }
            if (list.isEmpty()) {
                arrayDeque.pop();
            }
            else {
                final int[] array5 = (int[])list.get(random.nextInt(list.size()));
                array2[array5[0]][array5[1]] = true;
                array[array3[0] + array5[0] + 1][array3[1] + array5[1] + 1] = true;
                array[2 * array5[0] + 1][2 * array5[1] + 1] = true;
                arrayDeque.push(array5);
            }
        }
        if (n3 > 0.0) {
            braid(array, n, n2, n3, random);
        }
        return array;
    }
    
    private static void braid(final boolean[][] array, final int n, final int n2, final double n3, final Random random) {
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n2; ++j) {
                final int n4 = 2 * i + 1;
                final int n5 = 2 * j + 1;
                int n6 = 0;
                final ArrayList list = new ArrayList();
                for (final int[] array2 : MazeGenerator.DIRS) {
                    final int n7 = n4 + array2[0];
                    final int n8 = n5 + array2[1];
                    final int n9 = i + array2[0];
                    final int n10 = j + array2[1];
                    if (array[n7][n8]) {
                        ++n6;
                    }
                    else if (n9 >= 0 && n10 >= 0 && n9 < n && n10 < n2) {
                        list.add(new int[] { n7, n8 });
                    }
                }
                if (n6 == 1 && !list.isEmpty() && random.nextDouble() < n3) {
                    final int[] array3 = (int[])list.get(random.nextInt(list.size()));
                    array[array3[0]][array3[1]] = true;
                }
            }
        }
    }
    
    public static void carve(final boolean[][] array, final int n, final int n2, final int n3, final int n4) {
        final int max = Math.max(1, Math.min(n, n3));
        final int min = Math.min(array.length - 2, Math.max(n, n3));
        final int max2 = Math.max(1, Math.min(n2, n4));
        final int min2 = Math.min(array[0].length - 2, Math.max(n2, n4));
        for (int i = max; i <= min; ++i) {
            for (int j = max2; j <= min2; ++j) {
                array[i][j] = true;
            }
        }
    }
    
    public static int openCount(final boolean[][] array) {
        int n = 0;
        for (final boolean[] array2 : array) {
            for (int length2 = array2.length, j = 0; j < length2; ++j) {
                if (array2[j]) {
                    ++n;
                }
            }
        }
        return n;
    }
    
    public static boolean isConnected(final boolean[][] array) {
        final int openCount = openCount(array);
        if (openCount == 0) {
            return true;
        }
        int n = -1;
        int n2 = -1;
    Label_0068:
        for (int i = 0; i < array.length; ++i) {
            for (int j = 0; j < array[0].length; ++j) {
                if (array[i][j]) {
                    n = i;
                    n2 = j;
                    break Label_0068;
                }
            }
        }
        final boolean[][] array2 = new boolean[array.length][array[0].length];
        final ArrayDeque arrayDeque = new ArrayDeque();
        arrayDeque.add(new int[] { n, n2 });
        array2[n][n2] = true;
        int n3 = 0;
        while (!arrayDeque.isEmpty()) {
            final int[] array3 = (int[])arrayDeque.poll();
            ++n3;
            for (final int[] array4 : MazeGenerator.DIRS) {
                final int n4 = array3[0] + array4[0];
                final int n5 = array3[1] + array4[1];
                if (n4 >= 0 && n5 >= 0 && n4 < array.length && n5 < array[0].length && array[n4][n5] && !array2[n4][n5]) {
                    array2[n4][n5] = true;
                    arrayDeque.add(new int[] { n4, n5 });
                }
            }
        }
        return n3 == openCount;
    }
    
    static {
        DIRS = new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
    }
}
