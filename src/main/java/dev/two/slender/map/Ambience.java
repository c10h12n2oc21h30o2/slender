package dev.two.slender.map;

import java.util.List;
import java.util.Map;

public record Ambience(List<SoundZone> sounds, List<MapLight> lights, List<String> phrases) {
    public static final Ambience NONE = new Ambience(List.of(), List.of(), List.of());

    public Ambience(List<SoundZone> sounds, List<MapLight> lights, List<String> phrases) {
        sounds = List.copyOf(sounds);
        lights = List.copyOf(lights);
        phrases = List.copyOf(phrases);
        this.sounds = sounds;
        this.lights = lights;
        this.phrases = phrases;
    }

    public Ambience offset(int var1, int var2, int var3) {
        return new Ambience(this.sounds.stream().map((var3x) -> {
            return var3x.offset(var1, var2, var3);
        }).toList(), this.lights.stream().map((var3x) -> {
            return var3x.offset(var1, var2, var3);
        }).toList(), this.phrases);
    }

    public List<String> phrasesOr(List<String> var1) {
        return this.phrases.isEmpty() ? var1 : this.phrases;
    }

    public void writeTo(Map<String, Object> var1) {
        if (!this.sounds.isEmpty()) {
            var1.put("sounds", this.sounds.stream().map(SoundZone::serialize).toList());
        }

        if (!this.lights.isEmpty()) {
            var1.put("lights", this.lights.stream().map(MapLight::serialize).toList());
        }

        if (!this.phrases.isEmpty()) {
            var1.put("phrases", this.phrases);
        }

    }

    public static Ambience readFrom(Map<String, ?> var0) {
        List<String> var1 = strings(var0.get("sounds"));
        List<String> var2 = strings(var0.get("lights"));
        List<String> var3 = strings(var0.get("phrases"));
        return var1.isEmpty() && var2.isEmpty() && var3.isEmpty() ? NONE : new Ambience(var1.stream().map(SoundZone::parse).toList(), var2.stream().map(MapLight::parse).toList(), var3);
    }

    private static List<String> strings(Object var0) {
        List var10000;
        if (var0 instanceof List var1) {
            var10000 = var1.stream().map(Object::toString).toList();
        } else {
            var10000 = List.of();
        }

        return var10000;
    }

    public List<SoundZone> sounds() {
        return this.sounds;
    }

    public List<MapLight> lights() {
        return this.lights;
    }

    public List<String> phrases() {
        return this.phrases;
    }
}
