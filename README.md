<p align="center">
  <img src="https://raw.githubusercontent.com/dev-braini/SkinEvent/master/src/main/resources/logo_skinevent.png">
</p>

# SkinEvent
Unterstützte Versionen: 1.7.10 - 1.12.x
## Beschreibung
Entwickler-Prüfungsaufgabe für die Bewerbung bei GrieferGames.net im Bereich _Spigot & Bungeecord Entwicklung_.

Demo-Bungee-Server: 80.240.22.129

[`JAR-Download (v0.9)`](https://github.com/dev-braini/SkinEvent/raw/master/JAR/SkinEvent.jar)

## Features


* Ändere deinen Skin - Jeder Skin der je auf Mojang geladen wurde ist erlaubt
* Starte einen Skin-Event, in welchem alle Spieler auf dem Server, nach einer Abstimmung, denselben Skin erhalten
* Adminrechte: Ändere den Skin eines anderen Spielers / Starte den Skin-Event ohne Abstimmung
* BungeeCord support - Die Skins bleiben auch bei einem Serverwechsel erhalten
* Fast keine Majon-API rate limits wegen Skincaching in Datenbank.
* Skins werden nach konfigurierbarer Dauer automatisch zurückgesetzt (ausser beim permanentem Ändern des Skins)
* Konfigurierbare Cooldowns (SkinChange, SkinEvent)
* Keine Client-Modifikationen nötig
* MySQL-Datenbank oder File-Storage

## Kommandos

    /skin set <username>                   Ändere deinen Skin auf den, des angegebenen Spieler 
    /skin setperm <username>               Ändere den Skin permanent
    /skin <username>                       Kurzversion von /skin set
    /skin clear                            Setze deinen Skin zurück
    /skin event                            Starte einen Skin-Event (alle 60 Minuten)
    /skin vote <yes/no>                    Stimme ab, ob ein Skin-Event durchgeführt werden soll
                
    /sr set <player> <username>            Admin: Ändert den Skin von einem Spieler...

_Skins werden nach 10 Minuten automatisch zurückgesetzt._

## Rechte

    skinevent.skinupdate                   Eigenen Skin ändern und zurücksetzen
    skinevent.startevent                   Skin-Event starten
    skinevent.admin                        Skin von anderen Spielern ändern / Event (ohne Abstimmung) starten

## Requirements

* Java 8+
* Minecraft server software
    * Spigot 1.8.8+ or any fork of it
    * BungeeCord 1.12+ (Rückwärtskompatibel)

## Installation

1. Installiere das Plugin auf dem BungeeCord **und** dem Bukkit-Server
2. Aktiviere BungeeCord support in der Spigot Konfiguration (`bungeecord: true` in spigot.yml)

## Lizenz
SkinEvent ist lizenziert unter  GNU General Public License v3.0. Bitte lies [`LICENSE.txt`](https://github.com/dev-braini/SkinEvent/blob/master/LICENSE) für mehr Informationen.