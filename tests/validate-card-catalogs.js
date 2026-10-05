const fs = require('fs');
const path = require('path');

const root = path.resolve(__dirname, '..');
const read = (relative) => JSON.parse(fs.readFileSync(path.join(root, relative), 'utf8'));

const core = read('app/src/main/res/raw/cards_core_pl.json');
const adult = read('app/src/adult/res/raw/cards_mode_pl.json');
const play = read('app/src/play/res/raw/cards_mode_pl.json');

function validate(name, cards, { maxIntensity }) {
  const errors = [];
  const ids = new Set();

  for (const card of cards) {
    if (!card.id || typeof card.id !== 'string') errors.push(`${name}: missing id`);
    if (ids.has(card.id)) errors.push(`${name}: duplicate id ${card.id}`);
    ids.add(card.id);
    if (!card.title || !card.title.trim()) errors.push(`${name}/${card.id}: blank title`);
    if (!card.text || !card.text.trim()) errors.push(`${name}/${card.id}: blank text`);
    if (!Number.isInteger(card.intensity) || card.intensity < 1 || card.intensity > maxIntensity) {
      errors.push(`${name}/${card.id}: intensity ${card.intensity} exceeds ${maxIntensity}`);
    }
    if (!Number.isInteger(card.heat) || card.heat < 0 || card.heat > 20) {
      errors.push(`${name}/${card.id}: heat must be 0..20`);
    }
    if (card.intensity >= 3 && card.requiresMutualYes !== true) {
      errors.push(`${name}/${card.id}: HOT/EXTREME requires mutual YES`);
    }
    const afterglow = card.afterglow === true || card.category === 'AFTERGLOW';
    if (afterglow) {
      if (card.afterglow !== true || card.category !== 'AFTERGLOW') errors.push(`${name}/${card.id}: invalid afterglow marker`);
      if (card.intensity !== 1) errors.push(`${name}/${card.id}: afterglow must be SOFT`);
      if (card.heat !== 0) errors.push(`${name}/${card.id}: afterglow heat must be zero`);
    }
  }

  if (errors.length) throw new Error(errors.join('\n'));
}

validate('adult', [...core, ...adult], { maxIntensity: 4 });
validate('play', [...core, ...play], { maxIntensity: 2 });

console.log(`Card catalogs valid: adult=${core.length + adult.length}, play=${core.length + play.length}`);
