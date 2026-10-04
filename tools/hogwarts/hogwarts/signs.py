"""Traducción de los carteles de Gabbel al español (tabla data/carteles_es.json)."""
import json, os, sys

TABLE = os.path.join(os.path.dirname(__file__), '..', 'data', 'carteles_es.json')


def key(lines: list[str]) -> str:
    return '\n'.join(l.strip() for l in lines)


def is_empty(lines: list[str]) -> bool:
    return not any(l.strip() for l in lines)


def load_table(path: str = TABLE) -> dict[str, list[str]]:
    with open(path, encoding='utf-8') as f:
        return json.load(f)


def translate_all(signs: list[dict], table: dict[str, list[str]]) -> None:
    for s in signs:
        if is_empty(s['lines']):
            continue
        k = key(s['lines'])
        if k not in table:
            raise KeyError(k)
        s['lines'] = list(table[k])


def dump_missing(signs: list[dict], table: dict[str, list[str]]) -> list[str]:
    seen, out = set(), []
    for s in signs:
        if is_empty(s['lines']):
            continue
        k = key(s['lines'])
        if k not in table and k not in seen:
            seen.add(k)
            out.append(k)
    return out


def merge(pending_path: str, translated_path: str, table_path: str = TABLE) -> int:
    """Junta las traducciones escritas a mano ("N|l1|l2|l3|l4") con las claves pendientes ("N\\t<clave json>")."""
    keys = {}
    with open(pending_path, encoding='utf-8') as f:
        for ln in f:
            n, k = ln.rstrip('\n').split('\t', 1)
            keys[n] = json.loads(k)
    table = load_table(table_path) if os.path.exists(table_path) else {}
    added = 0
    with open(translated_path, encoding='utf-8') as f:
        for ln in f:
            ln = ln.rstrip('\n')
            if not ln.strip() or ln.startswith('#'):
                continue
            parts = ln.split('|')
            n, lines = parts[0].strip(), parts[1:]
            if len(lines) != 4:
                raise ValueError(f'línea {n}: hacen falta 4 líneas: {ln}')
            table[keys[n]] = lines
            added += 1
    with open(table_path, 'w', encoding='utf-8') as f:
        json.dump(dict(sorted(table.items())), f, ensure_ascii=False, indent=1)
    return added


if __name__ == '__main__':
    if sys.argv[1] == '--missing':
        from . import anvil, extras
        _, tes, ents = anvil.load_world(sys.argv[2])
        data = extras.extract(tes, ents, (383, 0, -326))
        table = load_table() if os.path.exists(TABLE) else {}
        for i, k in enumerate(dump_missing(data['signs'], table)):
            print(f'{i}\t{json.dumps(k, ensure_ascii=False)}')
    elif sys.argv[1] == '--merge':
        print(merge(sys.argv[2], sys.argv[3]))
