package dev.two.slender.player;

import com.destroystokyo.paper.profile.ProfileProperty;
import java.util.Collection;
import java.util.Optional;

public final class SkinCapture {
    private SkinCapture() {
    }

    public static Optional<ProfileProperty> signedTextures(Collection<ProfileProperty> var0) {
        return var0.stream().filter((var0x) -> {
            return "textures".equals(var0x.getName()) && var0x.isSigned();
        }).findFirst();
    }
}
