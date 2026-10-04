import pickle, numpy as np, sys, json
sys.path.insert(0,'.')
from hogwarts.volume import Volume
from hogwarts import sections, content
data,pal=pickle.load(open('build/vol.pkl','rb')); vol=Volume(data,pal)
ctx=sections.Context.__new__(sections.Context); ctx.vol=vol
ctx.st=np.load('build/st.npy'); ctx.roof=np.load('build/roof.npy'); ctx.pas=np.load('build/pas.npy')
defs=sections.load_defs(); regs={d.id: sections.region(d, ctx.roof, ctx.pas) for d in defs if not d.rest}
others=ctx.indoor_union(defs)
for d in defs:
    if sys.argv[1:] and d.id not in sys.argv[1:]: continue
    res=ctx.check(d, others=others if d.rest else None)
    reached=res['reached_mask']
    sx,sy,sz=res['starts'][0]
    cl=content.seal_clusters(res)
    rows=[]
    for c in cl:
        if not (4<=len(c)<=24): continue
        a=np.array(c)
        # ¿toca la zona alcanzada?
        touch=any(reached[y+dy,z+dz,x+dx] for x,y,z in c for dx,dy,dz in ((1,0,0),(-1,0,0),(0,0,1),(0,0,-1),(0,-1,0)) if 0<=y+dy<255)
        if not touch: continue
        cx,cy,cz=a.mean(0)
        # qué hay detrás: celdas pasables vecinas fuera de la región
        beh=set()
        for x,y,z in c:
            for dx,dz in ((1,0),(-1,0),(0,1),(0,-1)):
                X,Z=x+dx,z+dz
                if ctx.pas[y,Z,X] and not res['region'][y,Z,X] and (X,y,Z) not in set(c):
                    lab='fuera' if not ctx.roof[y,Z,X] else next((k for k,r in regs.items() if r[y,Z,X]),'castillo?')
                    beh.add(lab)
        dist=abs(cx-sx)+abs(cz-sz)+abs(cy-sy)
        rows.append((len(c), int(cx)-383, int(a[:,1].min()), int(a[:,1].max()), int(cz)+326, ','.join(sorted(beh)), int(dist)))
    rows.sort(key=lambda r:-r[6])
    print(d.id, 'inicio', (sx-383,sy,sz+326), len(rows),'huecos')
    for r in rows[:40]: print('   n=%2d x=%4d y=%3d-%3d z=%4d detrás=%s dist=%d'%r)
