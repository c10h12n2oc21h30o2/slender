package dev.two.slender.logic;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public final class TargetSelector {
    private TargetSelector() {
    }

    public static double weight(Candidate var0) {
        double var1 = 1.0 + 0.2 * (double)var0.pages() + (var0.flashlightOn() ? 0.5 : 0.0);
        return var0.isolated() ? var1 * 5.0 : var1;
    }

    public static Optional<UUID> pick(List<Candidate> var0, Random var1) {
        if (var0.isEmpty()) {
            return Optional.empty();
        } else {
            double var2 = 0.0;

            Candidate var5;
            for(Iterator var4 = var0.iterator(); var4.hasNext(); var2 += weight(var5)) {
                var5 = (Candidate)var4.next();
            }

            double var8 = var1.nextDouble() * var2;
            Iterator var6 = var0.iterator();

            Candidate var7;
            do {
                if (!var6.hasNext()) {
                    return Optional.of(((Candidate)var0.get(var0.size() - 1)).id());
                }

                var7 = (Candidate)var6.next();
                var8 -= weight(var7);
            } while(!(var8 < 0.0));

            return Optional.of(var7.id());
        }
    }

    public static boolean shouldRetarget(UUID var0, boolean var1, double var2, Optional<UUID> var4) {
        if (var0 != null && var1) {
            if (var4.isPresent() && !((UUID)var4.get()).equals(var0)) {
                return true;
            } else {
                return var2 >= 20.0;
            }
        } else {
            return true;
        }
    }

    public static record Candidate(UUID id, int pages, boolean flashlightOn, boolean isolated) {
        public Candidate(UUID id, int pages, boolean flashlightOn, boolean isolated) {
            this.id = id;
            this.pages = pages;
            this.flashlightOn = flashlightOn;
            this.isolated = isolated;
        }

        public UUID id() {
            return this.id;
        }

        public int pages() {
            return this.pages;
        }

        public boolean flashlightOn() {
            return this.flashlightOn;
        }

        public boolean isolated() {
            return this.isolated;
        }
    }
}
