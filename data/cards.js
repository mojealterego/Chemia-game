window.CHEMIA_CARDS = [
  {id:'con-001',category:'connection',intensity:'soft',title:'Pierwsze wrażenie',text:'Powiedz partnerowi jedną rzecz, którą zauważyłeś w nim jako pierwszą.',heat:4,tags:['conversation','compliment']},
  {id:'con-002',category:'connection',intensity:'spicy',title:'Sekretne pragnienie',text:'Dokończ zdanie: „Najbardziej podoba mi się, kiedy Ty…”.',heat:6,tags:['desire','conversation']},
  {id:'con-003',category:'connection',intensity:'hot',title:'Bez uciekania wzrokiem',text:'Patrzcie na siebie przez 20 sekund. Potem każde mówi jedno szczere zdanie.',heat:8,tags:['eye-contact','intimacy']},
  {id:'con-004',category:'connection',intensity:'extreme',title:'Prawda bez filtra',text:'Powiedz coś, czego zwykle nie mówisz podczas codziennego dnia, ale chcesz, żeby partner to wiedział.',heat:10,tags:['honesty','intimacy']},
  {id:'tea-001',category:'tease',intensity:'soft',title:'Komplement',text:'Wybierz jedną cechę partnera i opisz ją tak, jakbyś próbował go uwieść jednym zdaniem.',heat:5,tags:['compliment','flirt']},
  {id:'tea-002',category:'tease',intensity:'spicy',title:'Trzy słowa',text:'Powiedz trzy słowa opisujące atmosferę, której chcesz dziś między Wami.',heat:7,tags:['flirt','mood']},
  {id:'tea-003',category:'tease',intensity:'hot',title:'Wybór partnera',text:'Partner wybiera: spojrzenie, szept albo komplement. Wykonaj wybraną opcję z pełnym zaangażowaniem.',heat:9,tags:['choice','flirt']},
  {id:'tea-004',category:'tease',intensity:'extreme',title:'Napięcie',text:'Przez 30 sekund prowadź rozmowę wyłącznie za pomocą spojrzeń i gestów, a potem nazwij jedno odczucie, które się pojawiło.',heat:12,tags:['eye-contact','tension']},
  {id:'hea-001',category:'heat',intensity:'spicy',title:'Jeszcze jedna',text:'Oboje wybieracie: kontynuujecie tę kategorię albo oddajecie następny wybór partnerowi.',heat:7,tags:['choice','continuation'],chain:true},
  {id:'hea-002',category:'heat',intensity:'hot',title:'Podkręć',text:'Wybierzcie wspólnie jedną rzecz, która ma stać się bardziej intensywna: tempo, długość, rozmowa albo element niespodzianki.',heat:10,tags:['choice','tension'],chain:true},
  {id:'hea-003',category:'heat',intensity:'extreme',title:'Zakaz przewidywania',text:'Partner wybiera kategorię następnej karty. Nie możesz negocjować wyboru.',heat:13,tags:['control','choice'],chain:true},
  {id:'role-001',category:'roleplay',intensity:'spicy',title:'Nieznajomi',text:'Przez dwie minuty udawajcie, że spotykacie się po raz pierwszy. Zacznijcie od krótkiego przedstawienia się.',heat:7,tags:['roleplay','story']},
  {id:'role-002',category:'roleplay',intensity:'hot',title:'Tajemniczy list',text:'Jedna osoba wymyśla tajemniczą wiadomość, którą druga ma odczytać na głos z wybraną emocją.',heat:9,tags:['roleplay','voice']},
  {id:'role-003',category:'roleplay',intensity:'extreme',title:'Scenariusz',text:'Wymyślcie wspólnie scenę romantycznego napięcia z jednym zakazem i jednym sekretem. Zagrajcie ją przez 90 sekund.',heat:12,tags:['roleplay','story','choice']},
  {id:'des-001',category:'desire',intensity:'soft',title:'Ulubiony klimat',text:'Wybierz światło, muzykę albo porę dnia, które najbardziej budują Twoją atmosferę bliskości.',heat:4,tags:['mood']},
  {id:'des-002',category:'desire',intensity:'spicy',title:'Wolałbyś?',text:'Wybierz: spontaniczność czy plan? Potem powiedz dlaczego.',heat:6,tags:['preference','conversation']},
  {id:'des-003',category:'desire',intensity:'hot',title:'Lista marzeń',text:'Każde z Was podaje jedną fantazję dotyczącą randki, nastroju albo scenariusza. Partner wybiera, która zostaje rozwinięta.',heat:9,tags:['fantasy','choice']},
  {id:'des-004',category:'desire',intensity:'extreme',title:'Granica i ciekawość',text:'Każde mówi jedną rzecz, której chętnie spróbowałoby kiedyś w bezpiecznym, uzgodnionym kontekście, oraz jedną rzecz, której zdecydowanie nie chce.',heat:11,tags:['boundaries','fantasy']},
  {id:'ctl-001',category:'control',intensity:'spicy',title:'Ty decydujesz',text:'Partner ma trzy opcje: pytanie, wyzwanie albo zmiana kategorii. Wybiera jedną.',heat:6,tags:['choice','control']},
  {id:'ctl-002',category:'control',intensity:'hot',title:'Ster',text:'Jedna osoba prowadzi rundę przez 60 sekund, druga tylko reaguje na kolejne wybory z aplikacji.',heat:9,tags:['control','timer']},
  {id:'cha-001',category:'chaos',intensity:'spicy',title:'Dwa rzuty',text:'Wylosuj kolejną kartę i natychmiast po niej jeszcze jedną. Para decyduje, która obowiązuje.',heat:6,tags:['random','choice'],chain:true},
  {id:'cha-002',category:'chaos',intensity:'hot',title:'Zmiana klimatu',text:'Natychmiast przełączcie kategorię na przeciwny klimat: z rozmowy na zabawę, z zabawy na role-play lub odwrotnie.',heat:8,tags:['random','switch']},
  {id:'aft-001',category:'afterglow',intensity:'soft',title:'Spokojny finał',text:'Powiedz partnerowi jedną rzecz, którą chcesz zapamiętać z tej sesji.',heat:5,tags:['afterglow','connection'],afterglow:true},
  {id:'aft-002',category:'afterglow',intensity:'spicy',title:'Jeszcze nie koniec',text:'Wybierzcie jedną z trzech dróg: spokojna rozmowa, nowy scenariusz albo losowa karta.',heat:7,tags:['afterglow','choice'],afterglow:true,chain:true},
  {id:'aft-003',category:'afterglow',intensity:'hot',title:'Druga fala',text:'Ustalcie nowy klimat sesji jednym słowem. Aplikacja przełączy następne losowanie na najlepiej pasującą kategorię.',heat:9,tags:['afterglow','mood'],afterglow:true}
];

// Content schema is intentionally extensible to 500+ cards. The engine accepts any card matching this shape.
