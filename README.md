Unterstützte Versionen: 1.7.10 - 1.12.x

# SkinPlugin
## Beschreibung
Entwickler-Prüfungsaufgabe für die Bewerbung bei GrieferGames.net im Bereich _Spigot & Bungeecord Entwicklung_.

## Features


* Ändere deinen Skin - Jeder Skin der je auf Mojang geladen wurde ist erlaubt
* Starte einen Skin-Event, in welchem alle Spieler auf dem Server, nach einer Abstimmung, denselben Skin erhalten
* Adminrechte: Ändere den Skin eines anderen Spielers / Starte den Skin-Event ohne Abstimmung
* BungeeCord support - Die Skins bleiben auch bei einem Serverwechsel erhalten
* Keine Client-Modifikationen nötig
* MySQL-Datenbank

## Kommandos

    /skin set <username>                  Ändere deinen Skin in den angegebenen Spieler (alle 10 Minuten)
    /skin <username>                      Kurzversion von /skin set
    /skin clear                           Setze deinen Skin zurück
    /skin event                           Starte einen Skin-Event (alle 60 Minuten)
    /skin vote <yes/no>                   Stimme ab, ob ein Skin-Event durchgeführt werden soll
                
    /sr set <player> <username>           Admin: Ändert den Skin von einem Spieler...

_Die Skins sind 10 Minuten gültig._ Danach werden Sie beim nächsten Reload zurückgesetzt.

## Rechte

    skinevent.skinupdate                  Eigenen Skin ändern und zurücksetzen
    skinevent.startevent                  Skin-Event starten
    skinevent.admin.startevent            Skin-Event (ohne Abstimmung) starten
    skinevent.admin.skinupdate            Skin von anderen Spielern ändern

## Requirements

* Java 8+
* Minecraft server software
    * Spigot 1.8.8+ or any fork of it
    * BungeeCord 1.12+ (Rückwärtskompatibel)

## Installation

1. Installiere das Plugin auf dem BungeeCord **und** dem Bukkit-Server
2. Aktiviere BungeeCord support in der Spigot Konfiguration (`bungeecord: true` in spigot.yml)

## Lizenz
SkinPlugin ist lizenziert unter  GNU General Public License v3.0. Bitte lies [`LICENSE.txt`](hhttps://github.com/dev-braini/SkinEvent/blob/master/LICENSE) für mehr Informationen.