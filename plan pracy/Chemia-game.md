# Plan pracy — Chemia-game

## Status audytu
AUDYT ZAKOŃCZONY — 2026-09-12.

## Stan faktyczny
Samodzielna gra dla dwóch dorosłych osób, działająca w przeglądarce i jako PWA. README opisuje `index.html`, `styles.css`, `app.js`, manifest, service worker, talię kart i testy silnika. Projekt deklaruje tryb offline, localStorage i brak kont.

## Ryzyka
- prywatność stanu gry w localStorage;
- brak kont nie oznacza automatycznie braku danych w przeglądarce;
- service worker wymaga testów aktualizacji cache;
- treści muszą pozostać zgodne z deklarowaną polityką dla dorosłych i bez graficznych instrukcji seksualnych.

## Priorytet
ŚREDNI.

## Kolejność prac
1. Zweryfikować silnik gry i testy.
2. Przetestować losowanie, anty-repeat, persistence i reset.
3. Przetestować PWA/offline/cache.
4. Dodać privacy-by-design i bezpieczne czyszczenie lokalnego stanu.
5. Dodać dostępność i responsywność.
6. Spolonizować interfejs i dokumentację.

## Kryterium zakończenia
Silnik ma pełne testy, PWA działa offline bez uszkodzenia stanu, dane lokalne można wyczyścić, a polityka treści jest egzekwowana.
