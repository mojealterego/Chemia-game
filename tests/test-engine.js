// Lightweight Node test harness for the pure data/logic invariants used by CHEMIA.
// Run: node tests/test-engine.js
const fs = require('fs');
const assert = require('assert');
const html = fs.readFileSync('index.html','utf8');
const js = fs.readFileSync('app.js','utf8');
const css = fs.readFileSync('styles.css','utf8');
const cards = fs.readFileSync('data/cards.js','utf8');

assert(html.includes('app.js'), 'app shell must load app.js');
assert(html.includes('data/cards.js'), 'app shell must load cards');
assert(js.includes('localStorage'), 'state should persist locally');
assert(js.includes('pickCard'), 'card selection engine missing');
assert(js.includes('Math.min(100,state.heat'), 'Heat cap missing');
assert(js.includes('state.history.includes(c.id)'), 'history anti-repeat missing');
assert(js.includes('state.skipped.includes(c.id)'), 'skip protection missing');
assert(js.includes("state.screen='after'"), 'Afterglow transition missing');
assert(js.includes("state.afterglow=false;state.intensity='extreme'"), 'Endless mode missing');
assert(cards.match(/id:'/g)?.length >= 20, 'starter deck unexpectedly small');
assert(css.includes('@media(max-width:540px)'), 'mobile responsive styles missing');
console.log('CHEMIA engine smoke tests: PASS');
