import pickle, numpy as np, sys
sys.path.insert(0,'.')
from hogwarts import main, sections, walk
vol,_,_,_=main.build_volume('input/region')
ctx=sections.Context(vol)
pickle.dump((vol.data, vol.palette), open('build/vol.pkl','wb'))
np.save('build/st.npy', ctx.st); np.save('build/roof.npy', ctx.roof); np.save('build/pas.npy', ctx.pas)
