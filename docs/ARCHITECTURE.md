# Architektur: Pendulum et Falcatis

Stand: Entwurf v1. Grundlage für die Meilensteine unten.

## Spielmechanik

Zwei Werkzeuge, beide nach demselben Prinzip aufgebaut: ein **Core**, ein **aktiver Slot** und
1–4 **passive Slots**, bestückt mit **Tarotkarten**.

| | Sense | Pendel |
|---|---|---|
| Basis | wie ein Eisenschwert | wie ein Schild (blockt mit Rechtsklick) |
| GUI | Shift+Rechtsklick | Shift+Rechtsklick (statt Blocken) |
| Aktive Fähigkeit | eigene Taste | eigene Taste |
| Passive Effekte | nur in der Haupthand | in Haupt- oder Nebenhand |
| Kosten der aktiven Fähigkeit | Haltbarkeit (Betrag je Karte) | Haltbarkeit (Betrag je Karte) |

Beide Tasten sind standardmäßig **nicht belegt**. Der Spieler legt sie selbst in den
Minecraft-Steuerungsoptionen fest.

### Haltbarkeit

Die Werkzeuge zerbrechen nicht. Sie bleiben bei 1 Restpunkt stehen und sind dann **inaktiv**:
keine Angriffs-Boni, kein Blocken, keine Fähigkeiten. Core und Karten bleiben erhalten.
Nach einer Reparatur ist das Werkzeug wieder voll nutzbar.

### Cores

| Stufe | Passive Slots |
|---|---|
| 1 – Basic | 1 |
| 2 | 2 |
| 3 | 3 |
| 4 | 4 |

- Höhere Stufen verbessern zusätzlich Schaden bzw. Blockwerte und Haltbarkeit.
- Ein Basic-Core ist Teil des Rezepts von Sense und Pendel, das fertige Werkzeug hat ihn bereits eingesetzt.
- Cores sind eigene Items und passen in Sense und Pendel. Ein Core lässt sich herausnehmen,
  z. B. um ihn zur nächsten Stufe weiterzucraften, und wieder einsetzen.
- Ohne Core ist das Werkzeug inaktiv (wie bei aufgebrauchter Haltbarkeit).
- Slots, die der aktuelle Core nicht freischaltet (ohne Core: alle Karten-Slots), sind gesperrt.
  Karten darin bleiben gespeichert, wirken aber nicht. Man kann sie herausnehmen, aber keine hineinlegen.
- Beim Core-Wechsel bleibt der Abnutzungsanteil erhalten (aufgerundet), damit das Tauschen keine
  Haltbarkeit zurückbringt.

Aktuelle Werte (Arbeitsstand, leicht anpassbar in `CoreTier`):

| Stufe | Item | Rezept-Zutaten | Schaden Sense | Haltbarkeit | Pendel: Sperrzeit nach Axttreffer |
|---|---|---|---|---|---|
| 1 | Basic-Core | Eisen, Amethyst | +0 | ×1 | 100 % |
| 2 | Adept-Core | Basic-Core, Gold, Amethyst | +1 | ×2 | 75 % |
| 3 | Arkaner Core | Adept-Core, Diamant, Amethyst | +2 | ×3 | 50 % |
| 4 | Aufgestiegener Core | Arkaner Core, Netherit, Echoscherben | +3 | ×4 | 25 % |

### Tarotkarten

- Jede Karte hat vier Effekte: Sense-aktiv, Sense-passiv, Pendel-aktiv, Pendel-passiv.
- Karten lassen sich jederzeit ohne Verlust entnehmen.
- Jede Karte hat ein eigenes Motiv. Der Kartenrahmen ist bei allen gleich (einheitlicher Look).

### Vorhandene Karten

Passive Werte gelten für eine Karte; weitere Kopien wirken abgeschwächt (Stärkefaktor).
Kosten = Haltbarkeit pro Einsatz.

| Karte | Sense aktiv (Kosten) | Sense passiv | Pendel aktiv (Kosten) | Pendel passiv |
|---|---|---|---|---|
| I – Der Magier | Arkaner Stoß: 6 Schaden am anvisierten Gegner, 12 Blöcke (5) | +2 Angriffsschaden | Magische Barriere: Absorption II, 5 s (8) | +2 Rüstung |
| IV – Der Herrscher | Kriegsschrei: Stärke I, 8 s (10) | +15 % Angriffstempo | Schockwelle: Rückstoß im Umkreis von 4 Blöcken (6) | +2 Rüstungshärte, +20 % Rückstoßresistenz |
| XIII – Der Tod | Schnitter-Schwung: 5 Schaden + 3 s Verdorrung vor dir (8) | Nach einem Kill 10 s Regeneration I | Todeshauch: Schwäche + Langsamkeit, 5 Blöcke, 5 s (8) | Erlittene Treffer −1 Schaden |
| XVI – Der Turm | Zerschmettern: nächster Treffer in 5 s doppelt (6) | Treffer setzen 3 s in Brand | Blitzschlag auf anvisierten Gegner, 16 Blöcke (15) | Dornen: Blocken wirft 30 % zurück (min. 1) |
| XVII – Der Stern | Heilt 3 Herzen (10) | Lebensraub: 10 % des Schadens | Reinigung: negative Effekte weg, 3 s Resistenz I (8) | Alle 4 s ein halbes Herz |

Keine Karte erhöht die maximalen Lebenspunkte: Beim Ablegen des Werkzeugs würden die Herzen
verschwinden. Aktive Fähigkeiten ohne Ziel (z. B. kein Gegner in Reichweite) lösen nicht aus
und kosten keine Haltbarkeit.

### Eine neue Karte anlegen

1. In `ModCards` registrieren: `register("name", scytheEffects, pendulumEffects)`.
   Das legt das Item `pendulumetfalcatis:<name>_card` an.
2. Motiv als `textures/item/card/<name>.png` (16×16). Sichtbar ist das Feld x 5–10, y 3–12;
   der gemeinsame Rahmen `textures/item/card_frame.png` liegt darüber.
3. Item-Modell `models/item/<name>_card.json` mit `layer0` = Motiv, `layer1` = `pendulumetfalcatis:item/card_frame`,
   dazu `items/<name>_card.json`.
4. Übersetzungen: Item-Name sowie `tarot_card.pendulumetfalcatis.<name>.<scythe|pendulum>.<active|passive>`.
- Dieselbe Karte darf mehrfach in die passiven Slots, jede weitere wirkt schwächer:
  100 % → 50 % → 25 % → 12,5 %.
- Beschaffung: Crafting, Truhen-Loot, Mob-Drops, Dorfbewohner-Handel (Wahrsager).

### Spam-Schutz

Zusätzlich zur Haltbarkeit hat jede aktive Fähigkeit eine kurze Abklingzeit (ca. 0,5 s).
Sonst würde gedrückt gehaltenes Spammen jeden Tick auslösen und die Haltbarkeit sofort verbrauchen.

## Technischer Aufbau

```
de.gilfort.pendulumetfalcatis
├── PendulumEtFalcatis / …Client      Einstieg, Registrierung
├── registry/                          ModItems, ModDataComponents, ModCreativeTabs, (später ModMenus, ModCards)
├── item/
│   ├── ArcaneToolItem                 gemeinsame Basis: Core, Slots, Haltbarkeit, GUI öffnen
│   ├── ScytheItem / PendulumItem      Sense bzw. Pendel
│   ├── CoreTier                       Stufen 1–4 mit ihren Werten
│   ├── CoreItem                       Core als Item
│   └── TarotCardItem                  ein Item pro Karte
├── card/
│   ├── TarotCard                      Definition: je Werkzeug ein aktiver und ein passiver Effekt
│   ├── ActiveEffect                   activate(Kontext) → Erfolg/Fehlschlag, Haltbarkeitskosten
│   ├── PassiveEffect                  Hooks: Attribute, ausgeteilter/erlittener Schaden, Blocken, jede Sekunde (mit Stärkefaktor)
│   └── ToolPassives                   ermittelt, welche Karteneffekte gerade wirken
├── menu/  ToolMenu (Server) + client/ToolScreen
├── network/  UseAbilityPayload        Client → Server: „Fähigkeit von Werkzeug X auslösen“
│             AbilityHandler            prüft Werkzeug, Karte, Abklingzeit; zieht Haltbarkeit ab
├── event/                             Passiv-Auswertung, Treffer, Blocken
└── client/                            Tastenbelegung, Tooltips
```

### Leitlinien

1. **Alles steckt im ItemStack.** Core-Stufe (`core_tier`) und Karten (`cards`: Slot 0 aktiv, 1–4 passiv) werden als Data Components direkt am
   Item gespeichert. Weitere Speicherung am Spieler ist nicht nötig. Damit funktioniert alles in
   Truhen, beim Ablegen und im Mehrspieler.
2. **Der Server entscheidet.** Die Taste schickt nur „Hand X“ an den Server. Der Server prüft
   Item, Karte und Haltbarkeit und führt den Effekt aus.
3. **Pendel als echter Schild.** Das Pendel nutzt die Vanilla-Komponente `blocks_attacks` und
   damit das normale Blocken.
4. **Neue Karte = eine neue Definition.** Eine Karte besteht aus einer `TarotCard` mit ihren vier
   Effekten. Karten-Item, Tooltip und Creative-Tab-Eintrag entstehen automatisch.
5. **GUI-Sicherheit.** Solange die GUI offen ist, ist das Item in der Hand gesperrt. Ein Werkzeug
   kann nicht in sich selbst gesteckt werden.

## Meilensteine

1. ✅ **Werkzeuge:** Sense und Pendel mit Basiswerten, gespeicherter Core-Stufe und Rezept; Basic-Core ist automatisch drin.
2. ✅ **Cores und GUI:** 4 Core-Stufen als Items, GUI mit Slot-Logik.
3. ✅ **Karten-System:** Karten-Grundgerüst, Tasten, Netzwerk, passive Auswertung.
4. ✅ **Testkarten:** 5 Karten der Großen Arkana zum Durchspielen (vorerst nur im Creative-Tab).
5. **Beschaffung:** Crafting, Truhen-Loot, Mob-Drops.
6. **Wahrsager:** Dorfbewohner-Beruf mit eigenem Arbeitsblock.

## Offene Punkte

- Namen und Materialien der Core-Stufen (Arbeitsnamen: Basic, Adept, Arcane, Ascended).
- Haltbarkeitswerte je Core-Stufe und Kosten pro Fähigkeit.
