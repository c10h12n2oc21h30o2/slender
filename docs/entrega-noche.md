# LA NOCHE ENTERA: entrega (Slender 1.9.0)

## Qué hay

El modo historia de Hogwarts. Un grupo de 1 a 8 jugadores recorre las 7 secciones del castillo una detrás de otra:

1. Terrenos
2. Gran Comedor
3. Mazmorras
4. Ala Este
5. Patio
6. Aulas altas
7. Torres

Entre fase y fase espera en la Sala de los Menesteres.

- **Entrada:** la 8.ª puerta del Expreso, que abre un menú con tres opciones: nueva noche, continuar o unirse. Solo puede haber una noche en marcha a la vez.
- **Sala de los Menesteres:**
  - tablón con la fase, las velas, la misión y el diario de Filch;
  - círculo de listos: 10 s cuando están todos dentro, o 30 s con que haya uno;
  - altar de la vela: gasta una vela para recuperar a un perdido;
  - puerta de salida.
- **Dificultad que sube en cada fase:**
  - páginas según el número de jugadores;
  - tiempo, agresividad y estática;
  - 6 poderes nuevos de Slender: apagar luces y linternas, oír a quien corre mucho rato, cazar cabezas, fogonazo, suplantar a un jugador y perseguir.
- **Cabezas y caldero:**
  - quien muere suelta su cabeza;
  - un compañero vivo la lleva al caldero de la fase (8 s de ritual) o la saca por la salida;
  - quien queda perdido ve la fase siguiente como espectador.
- **Velas:**
  - se empieza con 2 y el máximo es 4;
  - si no escapa nadie en una fase, se gasta una vela y se repite la fase;
  - se gana +1 con la misión de la noche (al azar: Norris, cromos o braseros);
  - se gana +1 con la Snitch Dorada.
- **Guardado y admin:**
  - se guarda en cada fase, en `plugins/Slender/noches/`;
  - comandos `/slender noche estado | terminar | fase N`;
  - los ajustes están en el apartado `noche:` del `config.yml`.
- **Arreglo de 1.8:** las velas de Hogwarts ya parpadean.

## Instalar

1. Cambia `Slender.jar` y reinicia el servidor. `/version Slender` debe decir **1.9.0**.
2. `/slender build vestibulo` sin nadie en la estación. Así aparecen la Sala y la 8.ª puerta.
3. Hogwarts no hace falta reconstruirlo. Tienen que estar construidas las 7 secciones (`/slender build hogwarts`).

Los ficheros están en `C:\Users\kivi2\IdeaProjects\slender\`: `Slender.jar`, `INSTALACION.md` (con el apartado "Novedades 1.9") y `codigo-1.8.zip`.

## Desviaciones respecto a la spec

- **La Sala va dentro de la caja de la estación**, detrás del muro sur (x −7…7, y 0…7, z 24…38 respecto a la estación), y no en otra caja aparte. Se construye con `/slender build vestibulo`.
- **No se sube la versión del config (`ConfigMigration`):** las claves nuevas de `noche:` y los mensajes `noche-*` se añaden solos al arrancar.
- **No hay `ChaseModel` aparte:** perseguir está dentro de `NightPowers`.
- **Los perdidos empiezan la fase como participantes muertos** (en modo espectador), no como un tipo de jugador nuevo.

## Arreglado en la revisión final

- **Los braseros ya no rompen el castillo.**
  - Antes, al quitarlos, dejaban aire donde había escaleras, puertas o escalas.
  - Ahora, al acabar la fase, cada bloque vuelve a quedar como estaba.
  - Los objetos de la misión solo se ponen en celdas libres.
- **Cazar cabezas respeta los tiempos:**
  - la cabeza tiene que llevar 20 s sin vigilar;
  - después Slender aparece a 1,5–3 bloques;
  - y tarda 2 s en llevársela.
- **Los espectadores (muertos, perdidos o escapados) ya no cuentan para la misión.**
- **Si un admin para la partida, la fase no cuenta:** `/slender stop`, `/slender build` o guardar en el editor durante una fase ya no gastan vela ni borran la partida.
- **Salir del mundo a mitad de fase:**
  - las cabezas se quedan junto al caldero;
  - el contador de la fase ya no se para.
- **El falso compañero ya no se delata:** sin la etiqueta "NPC" y sin nombre a la vista.

## Pendientes menores (no arreglados)

- Unirse durante una fase guarda lo que va pasando en ella.
- El aviso de "misión fallida" no le llega a nadie (el tablón sí lo pone).
- Alguien que no está en la partida (un admin en creativo) puede coger cabezas y cromos.
- Si el servidor se cae a mitad de fase, se quedan en el mundo el caldero y las cabezas.
- No hay un tope de 8 miembros.
- Un `factor-jugadores` mal escrito en el config rompe el arranque.
- El ritual durante los 60 s de espera del caldero falla sin avisar.
- Si el dueño de la cabeza se ha desconectado, el ritual gasta la cabeza sin revivir a nadie.
- Una cabeza que se sale del mapa por un lado no vuelve sola al caldero.

## Lista de comprobación en el servidor

- [ ] Una noche entera con 2 jugadores.
- [ ] Morir, coger la cabeza, hacer el ritual y revivir.
- [ ] Revivir justo al morir (antes de 1,5 s): no te tiene que dejar en espectador.
- [ ] Escapar con una cabeza: el rescatado juega la fase siguiente.
- [ ] Quedar perdido, ver la fase como espectador sin volver a la estación, y gastar una vela en el altar.
- [ ] Derrota y repetición. Repetir una fase dos veces y que no quede nada de la anterior: caldero, cabezas, gata, braseros, Snitch y luces apagadas.
- [ ] Misión BRASEROS: encender los 4. Después, en las secciones, las escaleras, puertas y escalas tienen que estar intactas.
- [ ] Un espectador hace clic en la gata o en un brasero: no cuenta.
- [ ] Snitch: atraparla da +1 vela.
- [ ] Guardar, salir por la puerta de la sala y continuar desde el menú.
- [ ] `/slender stop hw_…` durante una fase: no se gasta vela.
- [ ] `/spawn` o `/home` durante una fase en la fase 4 o después: las cabezas van al caldero y el tiempo sigue.
- [ ] Cada habilidad en su fase:
  - luces (fase 2);
  - oír correr (3);
  - cazar cabezas (4);
  - fogonazo (5);
  - suplantar (6), y el falso compañero no tiene que llevar etiqueta;
  - perseguir (7).
- [ ] Las velas de Hogwarts parpadean.
- [ ] Bedrock: cabezas, fogonazo, partículas de la Snitch y el falso compañero.
