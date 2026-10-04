"""Planos por bandas de altura de una sección: región (gris), alcanzado desde el spawn (verde),
derrumbes (rojo), salida/gate (azul), páginas (amarillo)."""
import numpy as np
from PIL import Image, ImageDraw, ImageFont

try:
    FONT = ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf', 13)
except OSError:
    FONT = ImageFont.load_default()
OX, OZ = 383, -326   # original → schem


def section_png(res: dict, path: str, title: str, marks=None, band: int = 12, scale: int = 2, crop=None):
    reg, reached = res['region'], res['reached_mask']
    st = res['mask']
    H, L, W = reg.shape
    ys = np.nonzero(st.any(axis=(1, 2)))[0]
    if len(ys) == 0:
        return
    if crop is None:
        zs, xs = np.nonzero(st.any(0))
        crop = (max(0, xs.min() - 5), min(W, xs.max() + 6), max(0, zs.min() - 5), min(L, zs.max() + 6))
    x0, x1, z0, z1 = crop
    seal = np.zeros_like(reg)
    for x, y, z in res['seal']:
        seal[y, z, x] = True
    tiles = []
    for a in range(int(ys.min()), int(ys.max()) + 1, band):
        b = min(a + band, H)
        img = np.zeros((z1 - z0, x1 - x0, 3), np.uint8)
        sl = (slice(a, b), slice(z0, z1), slice(x0, x1))
        img[st[sl].any(0)] = (70, 70, 80)
        img[reached[sl].any(0)] = (60, 170, 60)
        img[seal[sl].any(0)] = (220, 40, 40)
        for (mx, my, mz, col) in (marks or []):
            if a <= my < b and x0 <= mx < x1 and z0 <= mz < z1:
                img[mz - z0, mx - x0] = col
        im = Image.fromarray(img).resize(((x1 - x0) * scale, (z1 - z0) * scale), Image.NEAREST)
        d = ImageDraw.Draw(im)
        for gx in range((x0 - OX) // 20 * 20 + 20 + OX, x1, 20):
            d.line([((gx - x0) * scale, 0), ((gx - x0) * scale, im.height)], fill=(45, 45, 45))
            d.text(((gx - x0) * scale + 2, 2), str(gx - OX), fill=(200, 200, 0), font=FONT)
        for gz in range((z0 - OZ) // 20 * 20 + 20 + OZ, z1, 20):
            d.line([(0, (gz - z0) * scale), (im.width, (gz - z0) * scale)], fill=(45, 45, 45))
            d.text((2, (gz - z0) * scale + 2), str(gz - OZ), fill=(200, 200, 0), font=FONT)
        d.text((6, im.height - 18), f'y {a}-{b - 1}', fill=(255, 120, 120), font=FONT)
        tiles.append(im)
    cols = 3
    tw, th = tiles[0].size
    rows = (len(tiles) + cols - 1) // cols
    sheet = Image.new('RGB', (cols * (tw + 8), rows * (th + 8) + 26), (15, 15, 15))
    ImageDraw.Draw(sheet).text((6, 4), f'{title}: verde = se llega desde el spawn, gris = sección sin alcanzar, '
                                       f'rojo = derrumbe (coordenadas originales)', fill=(255, 255, 255), font=FONT)
    for i, t in enumerate(tiles):
        sheet.paste(t, ((i % cols) * (tw + 8), 26 + (i // cols) * (th + 8)))
    sheet.save(path)
