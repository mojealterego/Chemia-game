(() => {
  const root = document.getElementById('app');
  const CARDS = window.CHEMIA_CARDS || [];
  const KEY = 'chemia_state_v1';
  const categories = [
    ['connection','❤️','Connection'],['tease','😏','Tease'],['heat','🔥','Heat'],['roleplay','🎭','Roleplay'],
    ['desire','💎','Desire'],['control','👑','Control'],['chaos','🎲','Chaos'],['afterglow','🌙','Afterglow']
  ];
  const intensities = [['soft','Soft'],['spicy','Spicy'],['hot','Hot'],['extreme','Extreme']];
  const defaultState = () => ({screen:'home',p1:'Partner 1',p2:'Partner 2',intensity:'spicy',duration:30,heat:0,chain:0,round:0,history:[],skipped:[],favorites:[],secretA:null,secretB:null,secretDone:[],current:null,afterglow:false,seed:Date.now()});
  let state = load();

  function load(){try{return {...defaultState(),...(JSON.parse(localStorage.getItem(KEY)||'{}'))}}catch{return defaultState()}}
  function save(){localStorage.setItem(KEY,JSON.stringify(state))}
  function esc(s){return String(s).replace(/[&<>\"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]))}
  function icon(cat){return categories.find(x=>x[0]===cat)?.[1]||'✦'}
  function titleCat(cat){return categories.find(x=>x[0]===cat)?.[2]||cat}
  function idxSeed(){state.seed=(state.seed*9301+49297)%233280;return state.seed/233280}
  function heatBand(){return state.heat<25?'Warm-up':state.heat<50?'Spicy':state.heat<75?'Hot':state.heat<100?'Extreme':'Afterglow'}
  function secretFor(player){const pool=[
    'Doprowadź do dwóch zmian kategorii.',
    'Spraw, aby partner wybrał „Jeszcze jedna”.',
    'Zdobądź trzy szczere odpowiedzi.',
    'Wybierz trzy różne typy kart.',
    'Uruchom przynajmniej jeden łańcuch ×3.'
  ];return pool[Math.floor(idxSeed()*pool.length)]}
  function render(){
    if(state.screen==='home') return home();
    if(state.screen==='setup') return setup();
    if(state.screen==='game') return game();
    if(state.screen==='after') return after();
    if(state.screen==='history') return history();
    root.innerHTML='';
  }
  function shell(content){root.innerHTML=`<main class="app"><header class="topbar"><div class="brand">CHEMIA<small>PRIVATE COUPLES GAME</small></div><button class="iconbtn" data-action="home" aria-label="Start">⌂</button></header>${content}</main>`;bind()}
  function home(){shell(`<section class="hero"><span class="pill">18+ • TWO PLAYER • OFFLINE</span><h1>CHEMIA</h1><p>Buduj napięcie, odkrywaj preferencje i pozwól, żeby gra dopasowywała kolejne rundy do Waszego rytmu.</p><button class="cta" data-action="setup">NOWA GRA</button><button class="secondary" data-action="history">HISTORIA SESJI</button><div class="notice">Treści są przeznaczone dla dorosłych. Każde z Was może pominąć kartę lub zakończyć sesję w dowolnym momencie.</div></section>`)}
  function setup(){shell(`<section><div class="label">Gracze</div><div class="grid"><label class="option">Imię 1<input id="p1" value="${esc(state.p1)}" style="width:100%;margin-top:8px;background:#0f0b13;border:1px solid #302436;color:#fff;padding:10px;border-radius:10px"></label><label class="option">Imię 2<input id="p2" value="${esc(state.p2)}" style="width:100%;margin-top:8px;background:#0f0b13;border:1px solid #302436;color:#fff;padding:10px;border-radius:10px"></label></div><div class="label">Intensywność</div><div class="grid">${intensities.map(([k,l])=>`<button class="option ${state.intensity===k?'selected':''}" data-intensity="${k}"><strong>${l}</strong><span>${k==='soft'?'lekko':k==='spicy'?'napięcie':k==='hot'?'wysokie':'najodważniej'}</span></button>`).join('')}</div><div class="label">Długość</div><div class="grid">${[15,30,60,90].map(x=>`<button class="option ${state.duration===x?'selected':''}" data-duration="${x}"><strong>${x} min</strong><span>${x<=15?'Quick':x<=30?'Spicy':x<=60?'Hot':'Night'}</span></button>`).join('')}</div><button class="cta" data-action="start">ROZPOCZNIJ</button></section>`);}
  function pickCard(){
    const minI=['soft','spicy','hot','extreme'].indexOf(state.intensity);
    const allowed=CARDS.filter(c=>['soft','spicy','hot','extreme'].indexOf(c.intensity)<=Math.max(3,minI) && !state.history.includes(c.id) && !state.skipped.includes(c.id) && (state.afterglow ? c.afterglow : !c.afterglow));
    const pool=allowed.length?allowed:CARDS.filter(c=>!state.history.slice(-8).includes(c.id) && (state.afterglow?c.afterglow:!c.afterglow));
    const scored=pool.map(c=>{
      let s=1;
      if(['hot','extreme'].includes(c.intensity)&&state.heat>=50)s+=4;
      if(c.category==='afterglow'&&state.heat>=100)s+=12;
      if(c.chain)s+=state.chain*2;
      if(state.favorites.includes(c.category))s+=2;
      return {c,s};
    });
    const total=scored.reduce((a,x)=>a+x.s,0);let r=idxSeed()*total;
    for(const x of scored){r-=x.s;if(r<=0)return x.c}return scored[0]?.c||CARDS[0];
  }
  function game(){const c=state.current;const h=Math.min(100,state.heat);shell(`<section><div class="stats"><span>HEAT ${h}</span><span>${heatBand()}</span><span>ROUND ${state.round}</span></div><div class="meter"><div style="width:${h}%"></div></div><div style="display:flex;justify-content:space-between;gap:8px;margin:15px 0"><span class="pill">${icon(c?.category)} ${c?titleCat(c.category):'—'}</span><span class="pill">${state.chain?`CHAIN ×${state.chain}`:'NO CHAIN'}</span></div>${c?`<article class="card"><div class="eyebrow">${esc(c.intensity)} ${c.chain?'• CHAIN':''}</div><h2>${esc(c.title)}</h2><p>${esc(c.text)}</p><div class="heatplus">+${c.heat} HEAT</div></article><div class="actions"><button class="secondary" data-action="skip">POMIŃ</button><button class="secondary" data-action="favor">ULUBIONA</button><button class="cta wide" data-action="done">WYKONANE — LOSUJ DALEJ</button><button class="secondary" data-action="again">🔥 JESZCZE JEDNA</button><button class="secondary" data-action="partner">❤️ PARTNER WYBIERA</button></div>`:`<div class="card"><div class="eyebrow">READY</div><h2>Rozpocznij pierwszą kartę.</h2><p>Gra dobiera poziom i kategorię dynamicznie.</p></div><button class="cta" data-action="draw">LOSuj</button>`}<div class="notice"><b>Bezpieczeństwo:</b> „POMIŃ” działa zawsze i nie obniża wyniku. Gra nigdy nie wymaga wykonania karty.</div></section>`)}
  function after(){shell(`<section><div class="hero"><span class="pill">♾️ AFTERGLOW</span><h1>Jeszcze nie koniec.</h1><p>Heat osiągnął maksimum. Przełączcie klimat albo kontynuujcie w trybie Endless.</p><div class="grid"><button class="option" data-action="slow"><strong>🌙 SLOW</strong><span>spokojniejsza runda</span></button><button class="option" data-action="endless"><strong>🎲 ENDLESS</strong><span>losuj bez limitu</span></button></div><button class="secondary" data-action="end">ZAKOŃCZ SESJĘ</button></div></section>`)}
  function history(){const rows=state.history.slice(-20).reverse().map(id=>CARDS.find(c=>c.id===id)).filter(Boolean);shell(`<section><div class="label">Ostatnie karty</div>${rows.length?rows.map(c=>`<div class="notice"><b>${icon(c.category)} ${esc(c.title)}</b><br><span>${esc(c.text)}</span></div>`).join(''):`<div class="notice">Brak zapisanych kart. Historia pojawi się po rozpoczęciu gry.</div>`}<button class="secondary" data-action="home">WRÓĆ</button></section>`)}
  function start(){state.screen='game';state.heat=0;state.chain=0;state.round=0;state.history=[];state.skipped=[];state.favorites=[];state.afterglow=false;state.secretA=secretFor('A');state.secretB=secretFor('B');state.secretDone=[];draw();save()}
  function draw(){state.current=pickCard();state.round++;save();render()}
  function complete(){if(!state.current)return;state.history.push(state.current.id);state.heat=Math.min(100,state.heat+state.current.heat);state.chain=state.current.chain?Math.min(4,state.chain+1):0;if(state.heat>=100){state.afterglow=true;state.screen='after';state.current=null;save();render();return} draw()}
  function skip(){if(state.current){state.skipped.push(state.current.id);state.chain=0;draw()}}
  function bind(){
    root.querySelectorAll('[data-action]').forEach(b=>b.onclick=()=>{
      const a=b.dataset.action;
      if(a==='setup'){state.screen='setup';render()}
      else if(a==='home'){state.screen='home';save();render()}
      else if(a==='history'){state.screen='history';render()}
      else if(a==='start'){state.p1=document.getElementById('p1').value||'Partner 1';state.p2=document.getElementById('p2').value||'Partner 2';start()}
      else if(a==='draw'){draw()}
      else if(a==='done'){complete()}
      else if(a==='skip'){skip()}
      else if(a==='favor'){if(state.current&&!state.favorites.includes(state.current.category))state.favorites.push(state.current.category);save();render()}
      else if(a==='again'){state.heat=Math.min(100,state.heat+5);state.chain=Math.min(4,state.chain+1);draw()}
      else if(a==='partner'){state.chain=Math.min(4,state.chain+1);state.heat=Math.min(100,state.heat+3);draw()}
      else if(a==='slow'){state.afterglow=true;state.intensity='soft';state.screen='game';draw()}
      else if(a==='endless'){state.afterglow=false;state.intensity='extreme';state.screen='game';state.heat=75;draw()}
      else if(a==='end'){state.screen='home';save();render()}
    });
    root.querySelectorAll('[data-intensity]').forEach(b=>b.onclick=()=>{state.intensity=b.dataset.intensity;render()});
    root.querySelectorAll('[data-duration]').forEach(b=>b.onclick=()=>{state.duration=Number(b.dataset.duration);render()});
  }
  if('serviceWorker' in navigator) navigator.serviceWorker.register('service-worker.js').catch(()=>{});
  render();
})();
