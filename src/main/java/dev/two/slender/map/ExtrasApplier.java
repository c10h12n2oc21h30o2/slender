package dev.two.slender.map;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import dev.two.slender.map.schem.SchematicExtras;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.Art;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.block.Banner;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Painting;

/** Pone en el mundo los extras de un schematic (necesita el servidor: no tiene tests unitarios). */
final class ExtrasApplier {
    private ExtrasApplier() {
    }

    static void sign(World w, int bx, int by, int bz, SchematicExtras.SignText s) {
        BlockState st = w.getBlockAt(bx + s.pos().x(), by + s.pos().y(), bz + s.pos().z()).getState();
        if (!(st instanceof Sign sign)) {
            return;
        }
        SignSide side = sign.getSide(Side.FRONT);
        for (int i = 0; i < 4 && i < s.lines().size(); i++) {
            side.line(i, Component.text(s.lines().get(i)));
        }
        side.setColor(dye(s.color(), DyeColor.BLACK));
        side.setGlowingText(s.glow());
        sign.update(true, false);
    }

    static void banner(World w, int bx, int by, int bz, SchematicExtras.BannerData b) {
        BlockState st = w.getBlockAt(bx + b.pos().x(), by + b.pos().y(), bz + b.pos().z()).getState();
        if (!(st instanceof Banner banner)) {
            return;
        }
        List<Pattern> pats = new ArrayList<>();
        for (String[] p : b.patterns()) {
            PatternType type = Registry.BANNER_PATTERN.get(NamespacedKey.fromString(p[0]));
            if (type != null) {
                pats.add(new Pattern(dye(p[1], DyeColor.WHITE), type));
            }
        }
        banner.setPatterns(pats);
        banner.update(true, false);
    }

    static void head(World w, int bx, int by, int bz, SchematicExtras.HeadData h) {
        BlockState st = w.getBlockAt(bx + h.pos().x(), by + h.pos().y(), bz + h.pos().z()).getState();
        if (!(st instanceof Skull skull)) {
            return;
        }
        PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes(h.texture().getBytes()), "");
        profile.setProperty(new ProfileProperty("textures", h.texture()));
        skull.setPlayerProfile(profile);
        skull.update(true, false);
    }

    static void painting(World w, int bx, int by, int bz, SchematicExtras.PaintingData p) {
        Art art = Registry.ART.get(NamespacedKey.fromString(p.variant()));
        if (art == null) {
            return;
        }
        BlockFace face = BlockFace.valueOf(p.facing().toUpperCase(Locale.ROOT));
        Location at = new Location(w, bx + p.pos().x(), by + p.pos().y(), bz + p.pos().z());
        w.spawn(at, Painting.class, pt -> {
            pt.setFacingDirection(face, true);
            pt.setArt(art, true);
        });
    }

    private static DyeColor dye(String name, DyeColor dflt) {
        try {
            return DyeColor.valueOf(name.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return dflt;
        }
    }
}
