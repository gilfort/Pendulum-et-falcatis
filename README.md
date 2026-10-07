# Pendulum et Falcatis

Pendel und Sense – ein kampforientierter Magie-Mod für Minecraft.

## Umgebung

- Minecraft `26.3`, NeoForge `26.3.0.52-beta`
- Build-System: Gradle 9.2.1 mit [ModDevGradle](https://github.com/neoforged/ModDevGradle)
- Java 25
- Basis: [NeoForgeMDKs/MDK-26.3-ModDevGradle](https://github.com/NeoForgeMDKs/MDK-26.3-ModDevGradle) (Commit `4dacaff`)

Mod-ID, Paket und Versionen werden in `gradle.properties` gepflegt.
Die Mod-Metadaten liegen in `src/main/templates/META-INF/neoforge.mods.toml`.

## Häufige Befehle

```sh
./gradlew build          # Mod-JAR bauen (build/libs/)
./gradlew runClient      # Client starten
./gradlew runServer      # Dedizierten Server starten
./gradlew runData        # Data-Generatoren ausführen (src/generated/resources)
./gradlew --refresh-dependencies   # Abhängigkeiten neu laden
```

Das Projekt einfach in IntelliJ IDEA oder Eclipse als Gradle-Projekt öffnen.

## Mappings

Das MDK verwendet die offiziellen Mojang-Namen. Lizenzhinweise:
https://github.com/NeoForged/NeoForm/blob/main/Mojang.md

## Ressourcen

- Dokumentation: https://docs.neoforged.net/
- Discord: https://discord.neoforged.net/
