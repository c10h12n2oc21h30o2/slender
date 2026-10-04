"""Escribe data/contenido/<id>.json a partir de las propuestas + decisiones a mano (etiquetas, textos, sonidos)."""
import json, os
E = json.load(open('build/exits.json'))
LABELS = {
 'hw_mazmorras': ['Túneles del lago','Pasillo sur','Aula de Magia de raíces','Baños','Despacho de Necrófagos','Mazmorra 3',
                  'Escalera de las mazmorras','Pasadizo estrecho','Escalera norte','Almacén de yesqueros','Dormitorio de Slytherin',
                  'Objetos perdidos','Estudios de Necrófagos','Almacén de pociones'],
 'hw_comedor': ['Sala de Aparición','Muro del patio del reloj','Aula de Aritmancia','Cocinas','Baños del ala norte','Pasillo norte',
                'Dormitorios del servicio','Tablón de anuncios','Almacén de yesqueros','Galería de cuadros','Almacén de heno',
                'Sala de reuniones','Pasillo de la lechucería','Cuarto de la limpieza'],
 'hw_ala_este': ['Despensa de tartas','Estudios Muggles','Sala de los retratos','Aula de música','Despacho de Burbage','Aula de castigo',
                 'Museo muggle','Biblioteca de música','Baños del puente','Aseos','Pasillo de los anuncios','Aula 11','Aula de Lockhart',
                 'Baño de chicos'],
 'hw_patio': ['Aula de Filosofía','Sala de los cromos','Baño de Myrtle','Sortilegios Weasley','Baños del sótano','Mirador',
              'Gran Escalera','Archivos','Comedor del servicio','Práctica de duelo','Pasillo del tercer piso','Aula de Transformación',
              'Aula de Estudios Místicos','Club de poesía'],
 'hw_aulas': ['Objetos confiscados','Armario de Quirrell','Sala de duelos','Sala de las almohadas','Enfermería','Aula 16C',
              'Historia de la Magia','Habitación de invitados','Sala de programación','Galería de criaturas','Club de cromos',
              'Almacén de cálices','Aseos','Aula de castigo 5C'],
 'hw_torres': ['Sala común de Gryffindor','El Entremedio','Sala de experimentos','Pasillo del Entremedio','Sala de contemplación',
               'Baños de la torre','Dormitorio de chicas','Dormitorio de chicos','Sala de utilería','Rellano de la torre','Atajo secreto',
               'Pasillo de los retratos','Sala de trofeos','Almacén de macetas'],
 'hw_terrenos': ['Invernadero','Muralla oeste','Pie de la lechucería','Patio norte','Muro del reloj','Torre oeste','Puerta del reloj',
                 'Contrafuerte oeste','Invernadero 2','Balcón oeste','Cobertizo del jardín','Rocas del este','Orilla sur','Playa del sureste'],
}
EXTRA_PAGES = {'hw_terrenos': [[-34, 40, 561, 'EAST'], [-111, 40, 676, 'EAST'], [-75, 40, 607, 'SOUTH']]}
TITLE = {'hw_mazmorras': ['HOGWARTS', 'MAZMORRAS'], 'hw_comedor': ['HOGWARTS', 'GRAN COMEDOR'], 'hw_ala_este': ['HOGWARTS', 'ALA ESTE'],
         'hw_patio': ['PATIO DE', 'TRANSFORMACIÓN'], 'hw_aulas': ['HOGWARTS', 'AULAS ALTAS'], 'hw_torres': ['HOGWARTS', 'TORRES'],
         'hw_terrenos': ['HOGWARTS', 'TERRENOS']}
GATE_BLOCK = {'hw_mazmorras': 'minecraft:iron_bars', 'hw_comedor': 'minecraft:spruce_planks', 'hw_ala_este': 'minecraft:spruce_planks',
              'hw_patio': 'minecraft:iron_bars', 'hw_aulas': 'minecraft:mossy_cobblestone', 'hw_torres': 'minecraft:spruce_planks',
              'hw_terrenos': 'minecraft:iron_bars'}
NOTES = {
 'hw_mazmorras': [['Ronda de las 2.', 'Mazmorras.', 'Ni un alumno', 'en su cama.'],
                  ['Las antorchas', 'se apagan', 'cuando paso.', '¿Señora Norris?'],
                  ['Algo muy alto', 'me mira desde', 'el fondo del', 'pasillo.']],
 'hw_comedor': [['Mesas puestas', 'y nadie cena.', 'Las velas', 'flotan bajas.'],
                ['Los retratos', 'están vacíos.', 'Se han ido', 'todos. ¿Adónde?']],
 'hw_ala_este': [['La sala del', 'ruido está en', 'silencio.', 'Eso es peor.'],
                 ['El gramófono', 'suena solo.', 'No tiene', 'disco.']],
 'hw_patio': [['La Señora', 'Norris no', 'vuelve. Nunca', 'tarda tanto.'],
              ['Pisadas largas', 'en el patio.', 'Peeves dice:', '"no tiene cara"'],
              ['Biblioteca:', 'libros abiertos', 'por la misma', 'página.']],
 'hw_aulas': [['Las campanas', 'suenan a las', 'dos y trece.', 'No hay nadie.'],
              ['Enfermería:', 'camas hechas,', 'sábanas aún', 'calientes.']],
 'hw_torres': [['Subo a la', 'torre. Desde', 'arriba se ve', 'todo. Y él a mí'],
               ['El despacho', 'del director', 'está cerrado.', 'Algo contesta.']],
 'hw_terrenos': [['Si lees esto:', 'no mires al', 'hombre sin', 'cara.'],
                 ['Voy al lago.', 'La barca sigue', 'allí. Ojalá', 'llegue. -A.F.']],
}
PHRASES = {
 'hw_mazmorras': ['¡ALUMNOS FUERA DE LA CAMA!', 'LAS MAZMORRAS NO TIENEN SALIDA', 'HUELE A POCIÓN QUEMADA', 'ALGO GOTEA EN LA OSCURIDAD',
                  'NORRIS, ¿DÓNDE ESTÁS?', 'LA PUERTA DE PIEDRA SE CIERRA', 'LAS CADENAS SE MUEVEN', 'BAJO EL LAGO HAY ALGO',
                  'NADIE BAJA AQUÍ DE NOCHE', 'TE HA VISTO'],
 'hw_comedor': ['LA CENA SE ENFRÍA', 'NADIE SE SIENTA A LA MESA', 'LAS VELAS BAJAN', '¡A LA CAMA, AHORA!', 'LOS RETRATOS SE HAN IDO',
                'EL TECHO NO MUESTRA ESTRELLAS', 'ALGUIEN PASA LISTA', 'HAY UN PLATO DE MÁS EN LA MESA', 'LAS COCINAS ESTÁN FRÍAS', 'TE ESTÁ MIRANDO'],
 'hw_ala_este': ['LA MÚSICA NO PARA', 'EL GRAMÓFONO NO TIENE DISCO', 'SILENCIO EN LA SALA DEL RUIDO', '¡FUERA DE AQUÍ!',
                 'LOS DUENDECILLOS SE ESCONDEN', 'LOCKHART NO SONRÍE', 'EL METRÓNOMO NO SE PARA', 'ALGUIEN AFINA EL PIANO',
                 'EL PUENTE CRUJE', 'TE HA OÍDO'],
 'hw_patio': ['NORRIS NO VUELVE', 'PISADAS EN EL PATIO', 'LA BIBLIOTECA ESTÁ ABIERTA', '¡PROHIBIDO CORRER!',
              'LOS LIBROS SUSURRAN', 'LA SECCIÓN PROHIBIDA ESPERA', 'LA ARMADURA HA GIRADO LA CABEZA', 'LA FUENTE ESTÁ SECA',
              'ALGUIEN SUBE LA ESCALERA', 'TE SIGUE'],
 'hw_aulas': ['LAS CAMPANAS SUENAN SOLAS', 'LAS DOS Y TRECE', 'LA ENFERMERÍA ESTÁ VACÍA', '¡SILENCIO EN CLASE!',
              'LAS CAMAS ESTÁN CALIENTES', 'EL ENCERADO SE BORRA SOLO', 'LA TIZA ESCRIBE TU NOMBRE', 'NADIE PASA LISTA',
              'LA PUERTA SE CIERRA SOLA', 'YA ESTÁ AQUÍ'],
 'hw_torres': ['SUBE Y NO MIRES ABAJO', 'EL VIENTO SILBA EN LA TORRE', 'LAS LECHUZAS NO VUELVEN', '¡NADIE EN LA TORRE!',
               'EL DIRECTOR NO CONTESTA', 'LA ESCALERA NO TERMINA', 'LA ÚLTIMA VELA SE APAGA', 'DESDE ARRIBA SE VE TODO',
               'ÉL TAMBIÉN TE VE', 'NO HAY ADÓNDE IR'],
 'hw_terrenos': ['EL LAGO ESTÁ QUIETO', 'LA BARCA ESPERA', 'NO MIRES AL HOMBRE SIN CARA', '¡VUELVE AL CASTILLO!',
                 'LAS RANAS SE CALLAN', 'EL BOSQUE TE LLAMA', 'LAS HUELLAS VAN HACIA EL AGUA', 'NIEBLA SOBRE EL AGUA',
                 'FILCH NUNCA LLEGÓ', 'TE ESPERA EN LA ORILLA'],
}
SOUNDS = {  # (índice de página para el centro, radio, sonido, volumen, tono, probabilidad)
 'hw_mazmorras': [(0, 40, 'BLOCK_POINTED_DRIPSTONE_DRIP_WATER', 0.6, 0.8, 0.10), (10, 30, 'BLOCK_CHAIN_PLACE', 0.4, 0.6, 0.04),
                  (5, 40, 'AMBIENT_CAVE', 0.5, 0.8, 0.03), (13, 25, 'BLOCK_BREWING_STAND_BREW', 0.4, 0.7, 0.05),
                  (12, 30, 'ENTITY_BAT_AMBIENT', 0.3, 0.7, 0.04)],
 'hw_comedor': [(3, 25, 'BLOCK_FIRE_AMBIENT', 0.4, 0.8, 0.06), (9, 40, 'BLOCK_WOODEN_DOOR_CLOSE', 0.4, 0.7, 0.03),
                (11, 40, 'ITEM_ARMOR_EQUIP_CHAIN', 0.4, 0.6, 0.03), (0, 30, 'BLOCK_CANDLE_EXTINGUISH', 0.5, 0.8, 0.04),
                (1, 40, 'ENTITY_PARROT_FLY', 0.3, 0.6, 0.03)],
 'hw_ala_este': [(3, 25, 'BLOCK_NOTE_BLOCK_HARP', 0.4, 0.5, 0.06), (6, 25, 'BLOCK_NOTE_BLOCK_CHIME', 0.3, 0.6, 0.05),
                 (12, 30, 'ENTITY_VEX_AMBIENT', 0.3, 0.6, 0.04), (8, 30, 'BLOCK_WOOD_STEP', 0.5, 0.6, 0.05)],
 'hw_patio': [(11, 50, 'ITEM_BOOK_PAGE_TURN', 0.4, 0.7, 0.05), (6, 40, 'BLOCK_WOODEN_DOOR_OPEN', 0.4, 0.7, 0.03),
              (7, 30, 'ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM', 0.3, 0.5, 0.03), (5, 40, 'BLOCK_GRASS_STEP', 0.5, 0.6, 0.05),
              (2, 25, 'BLOCK_WATER_AMBIENT', 0.4, 0.7, 0.06)],
 'hw_aulas': [(4, 30, 'BLOCK_BELL_USE', 0.4, 0.5, 0.02), (10, 40, 'BLOCK_BELL_RESONATE', 0.3, 0.6, 0.02),
              (6, 40, 'BLOCK_WOODEN_DOOR_CLOSE', 0.4, 0.7, 0.03), (9, 30, 'ENTITY_PLAYER_BREATH', 0.3, 0.6, 0.04)],
 'hw_torres': [(0, 40, 'ENTITY_PARROT_FLY', 0.4, 0.5, 0.05), (4, 40, 'ENTITY_PHANTOM_FLAP', 0.4, 0.6, 0.04),
               (2, 30, 'ENTITY_BAT_TAKEOFF', 0.4, 0.6, 0.04), (9, 50, 'WEATHER_RAIN', 0.2, 0.6, 0.05)],
 'hw_terrenos': [(12, 60, 'BLOCK_WATER_AMBIENT', 0.5, 0.7, 0.08), (13, 60, 'ENTITY_FROG_AMBIENT', 0.5, 0.6, 0.08),
                 (0, 40, 'BLOCK_GRASS_STEP', 0.5, 0.6, 0.05), (2, 60, 'ENTITY_PARROT_FLY', 0.3, 0.5, 0.03),
                 (5, 60, 'AMBIENT_CAVE', 0.3, 0.7, 0.02)],
}
SHORT = {'Túneles del lago': 'Túneles', 'Aula de Magia de raíces': 'Magia raíces', 'Despacho de Necrófagos': 'Desp. Necróf.',
 'Escalera de las mazmorras': 'Escalera', 'Pasadizo estrecho': 'Pasadizo', 'Escalera norte': 'Escalera N',
 'Almacén de yesqueros': 'Yesqueros', 'Dormitorio de Slytherin': 'Slytherin', 'Objetos perdidos': 'Perdidos',
 'Sala de Aparición': 'Aparición', 'Muro del patio del reloj': 'Patio reloj', 'Aula de Aritmancia': 'Aritmancia',
 'Baños del ala norte': 'Baños norte', 'Dormitorios del servicio': 'Dormitorios', 'Tablón de anuncios': 'Tablón',
 'Galería de cuadros': 'Cuadros', 'Sala de reuniones': 'Reuniones', 'Despensa de tartas': 'Tartas',
 'Estudios Muggles': 'Muggles', 'Despacho de Burbage': 'Burbage', 'Biblioteca de música': 'Partituras',
 'Pasillo de los anuncios': 'Anuncios', 'Aula de Filosofía': 'Filosofía', 'Sortilegios Weasley': 'Weasley',
 'Comedor del servicio': 'Comedor serv.', 'Práctica de duelo': 'Duelo', 'Pasillo del tercer piso': '3.er piso',
 'Aula de Transformación': 'Transform.', 'Objetos confiscados': 'Confiscado', 'Armario de Quirrell': 'Quirrell',
 'Sala de las almohadas': 'Almohadas', 'Historia de la Magia': 'Historia', 'Habitación de invitados': 'Invitados',
 'Sala de programación': 'Programación', 'Galería de criaturas': 'Criaturas', 'Almacén de cálices': 'Cálices',
 'Sala común de Gryffindor': 'Gryffindor', 'Sala de experimentos': 'Experimentos', 'Pasillo del Entremedio': 'Entremedio',
 'Sala de contemplación': 'Contemplar', 'Dormitorio de chicas': 'Chicas', 'Dormitorio de chicos': 'Chicos',
 'Pasillo de los retratos': 'Retratos', 'Pie de la lechucería': 'Lechucería', 'Contrafuerte oeste': 'Contrafuerte',
 'Cobertizo del jardín': 'Cobertizo'}
def short(n):
    if n in SHORT: return SHORT[n]
    w = ' '.join(x for x in n.split() if x.lower() not in ('de', 'del', 'la', 'las', 'los', 'el'))
    return w[:13]
for sid in LABELS:
    p = json.load(open(f'build/propuesta_{sid}.json'))
    pages = [pg[:4] for pg in p['pages']] + EXTRA_PAGES.get(sid, [])
    pages = [pg + [lab] for pg, lab in zip(pages, LABELS[sid])]
    notes = []
    for n, lines in zip(p['notes'], NOTES[sid]):
        notes.append({**n, 'lines': lines})
    snd = []
    for i, r, f, v, pi, c in SOUNDS[sid]:
        x, y, z = pages[i][:3]
        snd.append([x, y, z, r, f, v, pi, c])
    e = E[sid]
    board = None
    if e['board']:
        board = {'pos': e['board'], 'title': TITLE[sid], 'places': [[short(pg[4]), pg[0], pg[2]] for pg in pages[:12]]}
    c = {'spawns': p['spawns'], 'pages': pages, 'batteries': p['batteries'], 'gate': e['gate'], 'gate_block': GATE_BLOCK[sid],
         'exit': e['exit'], 'board': board, 'notes': notes, 'phrases': PHRASES[sid], 'sounds': snd, 'lights': p['lights']}
    os.makedirs('data/contenido', exist_ok=True)
    json.dump(c, open(f'data/contenido/{sid}.json', 'w', encoding='utf-8'), ensure_ascii=False, indent=1)
    print(sid, len(pages), 'páginas', len(notes), 'notas', 'tablón' if board else 'sin tablón')
