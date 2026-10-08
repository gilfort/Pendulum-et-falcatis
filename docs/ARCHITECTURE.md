# Architektur: Pendulum et Falcatis

Stand: Entwurf v1. Grundlage für die Meilensteine unten.

## Spielmechanik

Zwei Werkzeuge, beide nach demselben Prinzip aufgebaut: ein **Core**, ein **aktiver Slot** und
1–4 **passive Slots**, bestückt mit **Tarotkarten**.

| | Sense | Pendel |
|---|---|---|
| Basis | wie ein Eisenschwert | wie ein Schild (blockt mit Rechtsklick) |
| GUI | Shift+Rechtsklick | Shift+Rechtsklick (statt Blocken) |
| Aktive Fähigkeit | Taste A | Taste B |
| Passive Effekte | nur in der Haupthand | in Haupt- oder Nebenhand |
| Kosten der aktiven Fähigkeit | Haltbarkeit (Betrag je Karte) | Haltbarkeit (Betrag je Karte) |

Beide Tasten sind in den Minecraft-Steuerungsoptionen frei belegbar.

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
- Ein frisch gecraftetes Werkzeug hat den Basic-Core bereits eingesetzt.
- Cores sind eigene Items und passen in Sense und Pendel. Beim Tausch kommt der alte Core zurück ins Inventar.
- Ein kleinerer Core lässt sich nur einsetzen, wenn die wegfallenden Slots leer sind.

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
│   ├── CoreItem                       Core als Item (Meilenstein 2)
│   └── TarotCardItem                  ein Item pro Karte (Meilenstein 3)
├── card/
│   ├── TarotCard                      Definition: 4 Effekte, Haltbarkeitskosten
│   ├── ActiveEffect                   activate(Kontext) → Erfolg/Fehlschlag
│   └── PassiveEffect                  Hooks: Werte, beim Treffen, beim Blocken, pro Tick (mit Stärkefaktor)
├── component/
│   └── ToolLoadout                    gespeicherter Inhalt am Item: aktive Karte, passive Karten
├── menu/  ToolMenu (Server) + client/ToolScreen
├── network/  UseAbilityPayload        Client → Server: „Fähigkeit von Hand X auslösen“
├── event/                             Passiv-Auswertung, Treffer, Blocken
└── client/                            Tastenbelegung, Tooltips
```

### Leitlinien

1. **Alles steckt im ItemStack.** Core-Stufe und Karten werden als Data Components direkt am
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
3. **Karten-System:** Karten-Grundgerüst, Tasten, Netzwerk, passive Auswertung.
4. **Testkarten:** 3–5 Karten der Großen Arkana zum Durchspielen.
5. **Beschaffung:** Crafting, Truhen-Loot, Mob-Drops.
6. **Wahrsager:** Dorfbewohner-Beruf mit eigenem Arbeitsblock.

## Offene Punkte

- Namen und Materialien der Core-Stufen (Arbeitsnamen: Basic, Adept, Arcane, Ascended).
- Welche 3–5 Karten zuerst, mit welchen Effekten.
- Haltbarkeitswerte je Core-Stufe und Kosten pro Fähigkeit.
