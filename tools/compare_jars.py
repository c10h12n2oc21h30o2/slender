"""Compara dos árboles de clases: API pública (javap -public) y llamadas por método (javap -c).
Uso: python3 tools/compare_jars.py <dir_original> <dir_nuevo>"""
import subprocess, sys, os, re, collections
J = os.environ.get('JAVAP', '/usr/lib/jvm/java-25-openjdk-amd64/bin/javap')
def classes(d):
    out=set()
    for r,_,fs in os.walk(d):
        for f in fs:
            if f.endswith('.class'):
                out.add(os.path.relpath(os.path.join(r,f),d)[:-6].replace('/','.'))
    return out
def javap(d, args, cls):
    return subprocess.run([J,*args,'-cp',d,cls],capture_output=True,text=True).stdout
def calls(text):
    per=collections.defaultdict(collections.Counter); cur=None
    for ln in text.split('\n'):
        m=re.match(r'^  (?:[\w<>\[\],.? $]+ )?([\w$<>]+)\(.*\);$', ln)
        if m: cur=m.group(1)
        if 'lambda$' in (cur or ''): cur='<lambdas>'
        m=re.search(r'invoke\w+\s+#\d+(?:,\s+\d+)?\s+// (?:Interface)?Method ([\w/$]+)\.([\w$<>]+)', ln)
        if m and cur and 'lambda$' not in m.group(2):
            owner=m.group(1)
            key=('java/util/*.' if owner.startswith('java/util/') else owner+'.')+m.group(2)
            per[cur][key]+=1
    return per
a,b=sys.argv[1],sys.argv[2]
ca={c for c in classes(a) if not re.search(r'\$\d+$',c)}; cb={c for c in classes(b) if not re.search(r'\$\d+$',c)}
print('solo original:',sorted(ca-cb)); print('solo nuevo:',sorted(cb-ca))
api=0; calld=0
for c in sorted(ca&cb):
    pa=javap(a,['-public'],c).split('\n')[1:]; pb=javap(b,['-public'],c).split('\n')[1:]
    if sorted(pa)!=sorted(pb):
        api+=1; print('API distinta:',c); [print('  -',x) for x in pa if x not in pb]; [print('  +',x) for x in pb if x not in pa]
    ka=calls(javap(a,['-c','-p'],c)); kb=calls(javap(b,['-c','-p'],c))
    for m in set(ka)|set(kb):
        if m=='<lambdas>': continue
        if ka.get(m)!=kb.get(m):
            calld+=1; da=ka.get(m,collections.Counter()); db=kb.get(m,collections.Counter())
            print(f'llamadas distintas: {c}.{m}  -{dict(da-db)} +{dict(db-da)}')
print(f'RESUMEN api_distintas={api} metodos_con_llamadas_distintas={calld}')
