"""Volumen de bloques: array (Y, Z, X) de índices de paleta + paleta de estados."""
from typing import Callable
import numpy as np

AIR = 'minecraft:air'


class Volume:
    def __init__(self, data: np.ndarray, palette: list[str]):
        self.data = data
        self.palette = list(palette)
        self._index = {s: i for i, s in enumerate(self.palette)}

    @classmethod
    def empty(cls, width: int, height: int, length: int) -> 'Volume':
        return cls(np.zeros((height, length, width), np.uint16), [AIR])

    @property
    def shape(self):
        return self.data.shape

    def idx(self, state: str) -> int:
        i = self._index.get(state)
        if i is None:
            i = len(self.palette)
            self.palette.append(state)
            self._index[state] = i
        return i

    def get(self, x: int, y: int, z: int) -> str:
        return self.palette[self.data[y, z, x]]

    def set(self, x: int, y: int, z: int, state: str) -> None:
        self.data[y, z, x] = self.idx(state)

    def names(self) -> np.ndarray:
        return np.array([s.split('[')[0] for s in self.palette], dtype=object)

    def mask(self, pred: Callable[[str], bool]) -> np.ndarray:
        lut = np.array([bool(pred(s)) for s in self.palette])
        return lut[self.data]

    def replace_palette(self, new_palette: list[str]) -> None:
        """Sustituye la paleta (misma longitud) y fusiona estados repetidos."""
        uniq = {}
        remap = np.zeros(len(new_palette), np.uint16)
        for i, s in enumerate(new_palette):
            remap[i] = uniq.setdefault(s, len(uniq))
        self.data = remap[self.data]
        self.palette = list(uniq.keys())
        self._index = {s: i for i, s in enumerate(self.palette)}
