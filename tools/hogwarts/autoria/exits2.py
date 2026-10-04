import pickle, numpy as np, sys, json
sys.path.insert(0,'.')
from hogwarts.volume import Volume
from hogwarts import sections, content
data,pal=pickle.load(open('build/vol.pkl','rb')); vol=Volume(data,pal)
ctx=sections.Context.__new__(sections.Context); ctx.vol=vol
ctx.st=np.load('build/st.npy'); ctx.roof=np.load('build/roof.npy'); ctx.pas=np.load('build/pas.npy')
defs={d.id:d for d in sections.load_defs()}; others=ctx.indoor_union(defs.values())
pick={'hw_mazmorras':(-126,26,31,503),'hw_comedor':(-210,71,73,478),'hw_ala_este':(-106,84,86,494),
      'hw_patio':(-133,84,86,555),'hw_aulas':(-242,103,109,584),'hw_torres':(-172,144,147,570),'hw_terrenos':(-104,28,31,504)}
out={}
for sid,(x,y1,y2,z) in pick.items():
    d=defs[sid]; o=others if d.rest else None
    res=ctx.check(d, others=o)
    cl=content.seal_clusters(res)
    best=min(cl, key=lambda c: min(abs(cx-(x+383))+abs(cz-(z-326))+abs(cy-(y1+y2)/2) for cx,cy,cz in c))
    prop=json.load(open(f'build/propuesta_{sid}.json'))
    spawns=[content.conv(s) for s in prop['spawns']]
    ez=content.exit_zone(ctx, d, best, spawns, o)
    reached=res['reached_mask']&res['region']
    b=content.find_board(ctx, reached, res['starts'][0])
    out[sid]={'gate':[[a-383,b_,c+326] for a,b_,c in best], 'exit': None if ez is None else [ez[0]-383,ez[1],ez[2]+326,ez[3]-383,ez[4],ez[5]+326],
              'board': None if b is None else [b[0]-383,b[1],b[2]+326]}
    print(sid, 'gate n=',len(best), 'exit', out[sid]['exit'], 'board', out[sid]['board'])
json.dump(out, open('build/exits.json','w'))
