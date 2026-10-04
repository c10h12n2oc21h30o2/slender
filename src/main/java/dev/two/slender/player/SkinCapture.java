// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.player;

import java.util.Optional;
import com.destroystokyo.paper.profile.ProfileProperty;
import java.util.Collection;

public final class SkinCapture
{
    private SkinCapture() {
    }
    
    public static Optional<ProfileProperty> signedTextures(final Collection<ProfileProperty> collection) {
        return collection.stream().filter(profileProperty -> "textures".equals(profileProperty.getName()) && profileProperty.isSigned()).findFirst();
    }
}
