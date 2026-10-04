package dev.two.slender.map.schem;

import java.io.UncheckedIOException;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public final class SchematicReader {
    private SchematicReader() {
    }

    public static Schematic read(InputStream var0) throws IOException {
        DataInputStream var1 = new DataInputStream(new BufferedInputStream(new GZIPInputStream(var0)));
        int var2 = var1.readUnsignedByte();
        if (var2 != 10) {
            throw new IOException("No es NBT: la raíz no es un compuesto");
        } else {
            var1.readUTF();
            Map var3 = compound(var1);
            Object var4 = var3.get("Schematic");
            if (!(var4 instanceof Map)) {
                throw new IOException("Falta el compuesto 'Schematic' (solo se admite Sponge v3)");
            } else {
                Map var5 = (Map)var4;
                if (num(var5, "Version") != 3) {
                    throw new IOException("Versión de schematic no admitida: " + String.valueOf(var5.get("Version")));
                } else {
                    int var7 = num(var5, "Width") & '\uffff';
                    int var8 = num(var5, "Height") & '\uffff';
                    int var9 = num(var5, "Length") & '\uffff';
                    Object var11 = var5.get("Blocks");
                    if (!(var11 instanceof Map)) {
                        throw new IOException("Faltan los bloques");
                    } else {
                        Map var10 = (Map)var11;
                        Object var12 = var10.get("Palette");
                        if (!(var12 instanceof Map)) {
                            throw new IOException("Falta la paleta");
                        } else {
                            Map var17 = (Map)var12;
                            Object var13 = var10.get("Data");
                            if (!(var13 instanceof byte[])) {
                                throw new IOException("Faltan los datos de bloques");
                            } else {
                                byte[] var18 = (byte[])var13;
                                String[] var19 = new String[var17.size()];

                                Map.Entry var15;
                                int var16;
                                for(Iterator var14 = var17.entrySet().iterator(); var14.hasNext(); var19[var16] = (String)var15.getKey()) {
                                    var15 = (Map.Entry)var14.next();
                                    var16 = ((Number)var15.getValue()).intValue();
                                    if (var16 < 0 || var16 >= var19.length || var19[var16] != null) {
                                        throw new IOException("Paleta con índices raros: " + var16);
                                    }
                                }

                                return new Schematic(var7, var8, var9, Arrays.asList(var19), varints(var18, var7 * var8 * var9));
                            }
                        }
                    }
                }
            }
        }
    }

    static int[] varints(byte[] var0, int var1) throws IOException {
        int[] var2 = new int[var1];
        int var3 = 0;

        for(int var4 = 0; var4 < var1; ++var4) {
            int var5 = 0;
            int var6 = 0;

            byte var7;
            do {
                if (var3 >= var0.length) {
                    throw new IOException("Datos de bloques cortados en la celda " + var4);
                }

                var7 = var0[var3++];
                var5 |= (var7 & 127) << var6;
                var6 += 7;
                if (var6 > 28) {
                    throw new IOException("Varint demasiado largo");
                }
            } while((var7 & 128) != 0);

            var2[var4] = var5;
        }

        if (var3 != var0.length) {
            throw new IOException("Sobran datos de bloques");
        } else {
            return var2;
        }
    }

    private static int num(Map<String, Object> var0, String var1) throws IOException {
        Object var3 = var0.get(var1);
        if (var3 instanceof Number var2) {
            return var2.intValue();
        } else {
            throw new IOException("Falta " + var1);
        }
    }

    private static Map<String, Object> compound(DataInputStream var0) throws IOException {
        LinkedHashMap var1 = new LinkedHashMap();

        while(true) {
            int var2 = var0.readUnsignedByte();
            if (var2 == 0) {
                return var1;
            }

            String var3 = var0.readUTF();
            var1.put(var3, payload(var0, var2));
        }
    }

    private static Object payload(DataInputStream var0, int var1) throws IOException {
        Object var10000;
        int var3;
        switch (var1) {
            case 1:
                var10000 = var0.readByte();
                break;
            case 2:
                var10000 = var0.readShort();
                break;
            case 3:
                var10000 = var0.readInt();
                break;
            case 4:
                var10000 = var0.readLong();
                break;
            case 5:
                var10000 = var0.readFloat();
                break;
            case 6:
                var10000 = var0.readDouble();
                break;
            case 7:
                byte[] var8 = new byte[length(var0)];
                var0.readFully(var8);
                var10000 = var8;
                break;
            case 8:
                var10000 = var0.readUTF();
                break;
            case 9:
                int var7 = var0.readUnsignedByte();
                var3 = length(var0);
                ArrayList var4 = new ArrayList(var3);

                for(int var5 = 0; var5 < var3; ++var5) {
                    var4.add(payload(var0, var7));
                }

                var10000 = var4;
                break;
            case 10:
                var10000 = compound(var0);
                break;
            case 11:
                int[] var6 = new int[length(var0)];

                for(var3 = 0; var3 < var6.length; ++var3) {
                    var6[var3] = var0.readInt();
                }

                var10000 = var6;
                break;
            case 12:
                long[] var2 = new long[length(var0)];

                for(var3 = 0; var3 < var2.length; ++var3) {
                    var2[var3] = var0.readLong();
                }

                var10000 = var2;
                break;
            default:
                throw new IOException("Tipo NBT desconocido: " + var1);
        }

        return var10000;
    }

    private static int length(DataInputStream var0) throws IOException {
        int var1 = var0.readInt();
        if (var1 < 0) {
            throw new IOException("Longitud negativa en el NBT");
        } else {
            return var1;
        }
    }

    public static Schematic resource(String name) {
        try (InputStream in = SchematicReader.class.getClassLoader().getResourceAsStream(name)) {
            if (in == null) {
                throw new IllegalStateException("No está en el jar: " + name);
            }
            return read(in);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer " + name, e);
        }
    }
}
