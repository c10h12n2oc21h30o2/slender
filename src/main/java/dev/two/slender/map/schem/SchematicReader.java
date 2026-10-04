// 
// Decompiled by Procyon v0.6.0
// 

package dev.two.slender.map.schem;

import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Map;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.util.zip.GZIPInputStream;
import java.io.InputStream;

public final class SchematicReader
{
    private SchematicReader() {
    }
    
    public static Schematic read(final InputStream in) throws IOException {
        final DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(in)));
        if (dataInputStream.readUnsignedByte() != 10) {
            throw new IOException("No es NBT: la ra\u00edz no es un compuesto");
        }
        dataInputStream.readUTF();
        final Object value = compound(dataInputStream).get("Schematic");
        if (!(value instanceof Map)) {
            throw new IOException("Falta el compuesto 'Schematic' (solo se admite Sponge v3)");
        }
        final Map map = (Map)value;
        if (num(map, "Version") != 3) {
            throw new IOException("Versi\u00f3n de schematic no admitida: " + String.valueOf(map.get("Version")));
        }
        final int n = num(map, "Width") & 0xFFFF;
        final int n2 = num(map, "Height") & 0xFFFF;
        final int n3 = num(map, "Length") & 0xFFFF;
        final Object value2 = map.get("Blocks");
        if (!(value2 instanceof Map)) {
            throw new IOException("Faltan los bloques");
        }
        final Map map2 = (Map)value2;
        final Object value3 = map2.get("Palette");
        if (!(value3 instanceof Map)) {
            throw new IOException("Falta la paleta");
        }
        final Map map3 = (Map)value3;
        final Object value4 = map2.get("Data");
        if (value4 instanceof final byte[]array) {
            final String[] a = new String[map3.size()];
            for (final Map.Entry entry : map3.entrySet()) {
                final int intValue = ((Number)entry.getValue()).intValue();
                if (intValue < 0 || intValue >= a.length || a[intValue] != null) {
                    throw new IOException("Paleta con \u00edndices raros: " + intValue);
                }
                a[intValue] = (String)entry.getKey();
            }
            return new Schematic(n, n2, n3, Arrays.asList(a), varints(array, n * n2 * n3));
        }
        throw new IOException("Faltan los datos de bloques");
    }
    
    static int[] varints(final byte[] array, final int n) throws IOException {
        final int[] array2 = new int[n];
        int i = 0;
        int j = 0;
    Label_0009:
        while (j < n) {
            int n2 = 0;
            int n3 = 0;
            while (i < array.length) {
                final byte b = array[i++];
                n2 |= (b & 0x7F) << n3;
                n3 += 7;
                if (n3 > 28) {
                    throw new IOException("Varint demasiado largo");
                }
                if ((b & 0x80) == 0x0) {
                    array2[j] = n2;
                    ++j;
                    continue Label_0009;
                }
            }
            throw new IOException("Datos de bloques cortados en la celda " + j);
        }
        if (i != array.length) {
            throw new IOException("Sobran datos de bloques");
        }
        return array2;
    }
    
    private static int num(final Map<String, Object> map, final String s) throws IOException {
        final Number value = map.get(s);
        if (value instanceof Number) {
            return value.intValue();
        }
        throw new IOException("Falta " + s);
    }
    
    private static Map<String, Object> compound(final DataInputStream dataInputStream) throws IOException {
        final LinkedHashMap linkedHashMap = new LinkedHashMap();
        while (true) {
            final int unsignedByte = dataInputStream.readUnsignedByte();
            if (unsignedByte == 0) {
                break;
            }
            linkedHashMap.put(dataInputStream.readUTF(), payload(dataInputStream, unsignedByte));
        }
        return linkedHashMap;
    }
    
    private static Object payload(final DataInputStream dataInputStream, final int n) throws IOException {
        Object o = null;
        switch (n) {
            case 1: {
                o = dataInputStream.readByte();
                break;
            }
            case 2: {
                o = dataInputStream.readShort();
                break;
            }
            case 3: {
                o = dataInputStream.readInt();
                break;
            }
            case 4: {
                o = dataInputStream.readLong();
                break;
            }
            case 5: {
                o = dataInputStream.readFloat();
                break;
            }
            case 6: {
                o = dataInputStream.readDouble();
                break;
            }
            case 7: {
                final byte[] b = new byte[length(dataInputStream)];
                dataInputStream.readFully(b);
                o = b;
                break;
            }
            case 8: {
                o = dataInputStream.readUTF();
                break;
            }
            case 9: {
                final int unsignedByte = dataInputStream.readUnsignedByte();
                final int length = length(dataInputStream);
                final ArrayList list = new ArrayList(length);
                for (int i = 0; i < length; ++i) {
                    list.add(payload(dataInputStream, unsignedByte));
                }
                o = list;
                break;
            }
            case 10: {
                o = compound(dataInputStream);
                break;
            }
            case 11: {
                final int[] array = new int[length(dataInputStream)];
                for (int j = 0; j < array.length; ++j) {
                    array[j] = dataInputStream.readInt();
                }
                o = array;
                break;
            }
            case 12: {
                final long[] array2 = new long[length(dataInputStream)];
                for (int k = 0; k < array2.length; ++k) {
                    array2[k] = dataInputStream.readLong();
                }
                o = array2;
                break;
            }
            default: {
                throw new IOException("Tipo NBT desconocido: " + n);
            }
        }
        return o;
    }
    
    private static int length(final DataInputStream dataInputStream) throws IOException {
        final int int1 = dataInputStream.readInt();
        if (int1 < 0) {
            throw new IOException("Longitud negativa en el NBT");
        }
        return int1;
    }
    
    public static Schematic resource(final String name) {
        try (final InputStream resourceAsStream = SchematicReader.class.getClassLoader().getResourceAsStream(name)) {
            if (resourceAsStream == null) {
                throw new IllegalStateException("No est\u00e1 en el jar: " + name);
            }
            return read(resourceAsStream);
        }
        catch (final IOException cause) {
            throw new UncheckedIOException("No se pudo leer " + name, cause);
        }
    }
}
