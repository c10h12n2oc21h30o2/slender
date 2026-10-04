import pickle, numpy as np, sys, time
sys.path.insert(0,'.')
from hogwarts.volume import Volume
from hogwarts import sections
data,pal=pickle.load(open('build/vol.pkl','rb')); vol=Volume(data,pal)
ctx=sections.Context.__new__(sections.Context); ctx.vol=vol
ctx.st=np.load('build/st.npy'); ctx.roof=np.load('build/roof.npy'); ctx.pas=np.load('build/pas.npy')
only=sys.argv[1:] or None
out={}
defs=sections.load_defs(); others=ctx.indoor_union(defs)
for d in defs:
    if only and d.id not in only: continue
    t=time.time(); r=ctx.check(d, others=others if d.rest else None)
    print(f"{d.id:14s} celdas={r['cells']:6d} alcanzadas={r['reached']:6d} fuga={r['leak']:6d} derrumbe={len(r['seal']):5d} inicio={r['starts']} {time.time()-t:.1f}s")
    out[d.id]=r
pickle.dump({k:{kk:v for kk,v in r.items()} for k,r in out.items()}, open('build/check.pkl','wb'))
from hogwarts import render
import os
for sid,r in out.items():
    render.section_png(r, f'build/plano_{sid}.png', sid)
