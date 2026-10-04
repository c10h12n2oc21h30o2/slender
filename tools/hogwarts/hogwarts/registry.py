"""Lista oficial de bloques y sonidos de 26.3, sacada del servidor vanilla con --reports."""
import json, os, subprocess, sys, tempfile

JAVA = os.environ.get('JAVA', '/usr/lib/jvm/java-25-openjdk-amd64/bin/java')


def generate(mojang_jar: str, out_dir: str) -> None:
    with tempfile.TemporaryDirectory() as tmp:
        subprocess.run([JAVA, '-DbundlerMainClass=net.minecraft.data.Main', '-jar', os.path.abspath(mojang_jar),
                        '--reports', '--output', os.path.join(tmp, 'out')], cwd=tmp, check=True,
                       stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
        rep = os.path.join(tmp, 'out', 'reports')
        blocks = json.load(open(os.path.join(rep, 'blocks.json')))
        reduced = {name: b.get('properties', {}) for name, b in sorted(blocks.items())}
        regs = json.load(open(os.path.join(rep, 'registries.json')))
        sounds = sorted(k.removeprefix('minecraft:') for k in regs['minecraft:sound_event']['entries'])
    os.makedirs(out_dir, exist_ok=True)
    with open(os.path.join(out_dir, 'blocks_26.3.json'), 'w') as f:
        json.dump(reduced, f, indent=0, sort_keys=True)
    with open(os.path.join(out_dir, 'sounds_26.3.json'), 'w') as f:
        json.dump(sounds, f, indent=0)


def load(path: str) -> dict[str, dict[str, list[str]]]:
    with open(path) as f:
        return json.load(f)


def load_sounds(path: str) -> set[str]:
    with open(path) as f:
        return set(json.load(f))


if __name__ == '__main__':
    generate(sys.argv[1], sys.argv[2])
