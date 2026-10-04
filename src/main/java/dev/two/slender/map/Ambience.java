// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map;

import java.util.function.Function;
import java.util.Map;
import java.util.Collection;
import java.util.List;

record Ambience(List<SoundZone> sounds, List<MapLight> lights, List<String> phrases) {
    public static final Ambience NONE;
    
    public Ambience(final List<SoundZone> sounds, final List<MapLight> lights, final List<String> phrases) {
        final List<Object> copy = (List<Object>)List.copyOf((Collection<? extends SoundZone>)sounds);
        final List<Object> copy2 = (List<Object>)List.copyOf((Collection<? extends MapLight>)lights);
        final List<Object> copy3 = (List<Object>)List.copyOf((Collection<? extends String>)phrases);
        this.sounds = (List<SoundZone>)copy;
        this.lights = (List<MapLight>)copy2;
        this.phrases = (List<String>)copy3;
    }
    
    public Ambience offset(final int n, final int n2, final int n3) {
        return new Ambience(this.sounds.stream().map(soundZone -> soundZone.offset(n4, n5, n6)).toList(), this.lights.stream().map(mapLight -> mapLight.offset(n7, n8, n9)).toList(), this.phrases);
    }
    
    public List<String> phrasesOr(final List<String> list) {
        return this.phrases.isEmpty() ? list : this.phrases;
    }
    
    public void writeTo(final Map<String, Object> map) {
        if (!this.sounds.isEmpty()) {
            map.put("sounds", this.sounds.stream().map((Function<? super Object, ?>)SoundZone::serialize).toList());
        }
        if (!this.lights.isEmpty()) {
            map.put("lights", this.lights.stream().map((Function<? super Object, ?>)MapLight::serialize).toList());
        }
        if (!this.phrases.isEmpty()) {
            map.put("phrases", this.phrases);
        }
    }
    
    public static Ambience readFrom(final Map<String, ?> map) {
        final List<String> strings = strings(map.get("sounds"));
        final List<String> strings2 = strings(map.get("lights"));
        final List<String> strings3 = strings(map.get("phrases"));
        if (strings.isEmpty() && strings2.isEmpty() && strings3.isEmpty()) {
            return Ambience.NONE;
        }
        return new Ambience(strings.stream().map(SoundZone::parse).toList(), strings2.stream().map(MapLight::parse).toList(), strings3);
    }
    
    private static List<String> strings(final Object o) {
        return (o instanceof List) ? ((List)o).stream().map(Object::toString).toList() : List.of();
    }
    
    static {
        NONE = new Ambience(List.of(), List.of(), List.of());
    }
}
