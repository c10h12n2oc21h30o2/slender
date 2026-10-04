import pickle, numpy as np, sys, json
sys.path.insert(0,'.')
from hogwarts.volume import Volume
from hogwarts import sections, content
data,pal=pickle.load(open('build/vol.pkl','rb')); vol=Volume(data,pal)
ctx=sections.Context.__new__(sections.Context); ctx.vol=vol
ctx.st=np.load('build/st.npy'); ctx.roof=np.load('build/roof.npy'); ctx.pas=np.load('build/pas.npy')
for d in sections.load_defs():
    if sys.argv[1:] and d.id not in sys.argv[1:]: continue
    p=content.propose(d.id, vol, ctx)
    json.dump(p, open(f'build/propuesta_{d.id}.json','w'), ensure_ascii=False, indent=1)
    print(d.id, 'paginas', len(p['pages']), [x[4][:22] for x in p['pages']])
