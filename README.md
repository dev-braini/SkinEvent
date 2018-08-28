Unterstützte Versionen: 1.7.10 - 1.12.x

# SkinPlugin
## Beschreibung
Entwickler-Prüfungsaufgabe für die Bewerbung bei GrieferGames.net im Bereich _Spigot & Bungeecord Entwicklung_.

## Features


* Ändere deinen Skin - Jeder Skin der je auf Mojang geladen wurde ist erlaubt
* Starte einen SkinEvent, in welchem alle Spieler auf dem Server, nach einer Abstimmung, denselben Skin erhalten
* Adminrechte: Ändere den Skin eines anderen Spielers / Starte den SkinEvent ohne Abstimmung
* BungeeCord support - Die Skins bleiben auch bei einem Serverwechsel erhalten
* Keine Client-Modifikationen nötig
* MySQL-Datenbank

## Kommandos

    /skin set <username>                       Ändere deinen Skin in den angegebenen Spieler (alle 10 Minuten)
    /skin <username>                           Kurzversion von /skin set
    /skin clear                                Setze deinen Skin zurück
    /skin event                                Starte einen SkinEvent (alle 60 Minuten)
    /skin vote <yes/no>                        Stimme ab, ob ein SkinEvent durchgeführt werden soll
                
    /sr set <player> <username>                Ändert den Skin von einem Spieler...

_Die Skins sind 10 Minuten gültig._ Danach werden Sie beim nächsten Reload zurückgesetzt.

## Rechte

* changeskin.command.skinupdate - Command to refresh a player's own skin
* changeskin.command.skinupdate.other.uuid - Allows to update the skin of that specific user
* changeskin.command.skinupdate.other.* - Allowed to update the skins of all players
* changeskin.command.setskin.* - Includes all the commands below
* changeskin.command.setskin - Set your own skin
* changeskin.command.setskin.other - Set the skin of other players
* changeskin.command.skinselect - Select a skin from the database
* changeskin.command.skinupload - Upload a skin to one of the configured accounts
* changeskin.command.skinskull - Use the skull command
* changeskin.command.skininfo - Use the info command

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