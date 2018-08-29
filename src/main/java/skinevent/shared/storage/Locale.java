package skinevent.shared.storage;

import skinevent.shared.utils.C;
import skinevent.shared.utils.YamlConfig;

import java.io.File;
import java.lang.reflect.Field;

public class Locale {

    public static String SR_LINE = "&7&m----------------------------------------";
    public static String PLAYER_HAS_NO_PERMISSION = "&e[&2SkinEvent&e] &4Fehler&8: &cDu hast keine Berechtigung.";
    public static String HELP_PLAYER = "  &2&lSkinEvent &7- &f&lv%ver%"
            + "\n   &2/skin set <skinname> &7-&f Aendere deinen Skin."
            + "\n    &2/skin <skinname> &7-&f Kurzversion von \"/skin set\"."
            + "\n    &2/skin clear &7-&f Setzt deinen Skin zurueck."
            + "\n    &2/skin event &7-&f Starte einen Skin-Event.";
    public static String HELP_SR = "    &2/sr &7- &fZeige Admin Kommandos.";
    public static String NOT_PREMIUM = "&e[&2SkinEvent&e] &4Fehler&8: &cPremium player mit diesem Namen existiert nicht.";
    public static String SKIN_COOLDOWN_NEW = "&e[&2SkinEvent&e] &4Fehler&8: &cDu kannst deinen Skin nur alle &e%s &csekunden aendern.";
    public static String SKIN_CHANGE_SUCCESS = "&e[&2SkinEvent&e] &2Dein Skin wurde geaendert.";
    public static String SKIN_CLEAR_SUCCESS = "&e[&2SkinEvent&e] &2Dein Skin wurde zurueckgesetzt.";
    public static String HELP_ADMIN = "  &2&lSkinEvent &7- &f&lv%ver% &c&lAdmin"
            + "\n    &2/sr set <player> <skinname> &7- &fAendert den Skin eines Spielers...";
    public static String ADMIN_SET_SKIN = "&e[&2SkinEvent&e] &2You set %player's skin.";
    public static String NOT_ONLINE = "&e[&2SkinEvent&e] &4Fehler&8: &cSpieler ist nicht online!";
    public static String SKIN_DATA_DROPPED = "&e[&2SkinEvent&e] &2Skin data for player %player dropped.";
    public static String RELOAD = "&e[&2SkinEvent&e] &2Config and Locale has been reloaded!";
    public static String HELP_CONFIG = "  &2&lSkinEvent &7- &c&lConfig"
            + "\n\n   &2/sr joinSkins <true/false> &7- &fToggles the skins on join."
            + "\n    &2/sr SkinWithoutPerm <true/false> &7- &fConfigures the DisabledSkins section."
            + "\n    &2/sr SkinExpiresAfter <time> &7- &fHow long the a skin is cached."
            + "\n    &2/sr skinCooldown <time> &7- &f/skin cooldown in minute(s)."
            + "\n    &2/sr defaultSkins <true/false/add [skin]> &7- &fConfigures the DefaultSkins section."
            + "\n    &2/sr updater <true/false> &7- &fToggles the updater";
    public static String SKIN_DISABLED = "&e[&2SkinEvent&e] &4Error&8: &cThis skin is disabled by an administrator.";
    public static String ALT_API_FAILED = "&e[&2SkinEvent&e] &4Error&8: &cSkin Data API ist ueberlastet, bitte versuche es saeäter noch einmal!";
    public static String NO_SKIN_DATA = "&e[&2SkinEvent&e] &4Error&8: &cNo skin data acquired! Does this player have a skin?";
    public static String STATUS_OK = "&e[&2SkinEvent&e] &2Mojang API connection successful!";
    public static String GENERIC_ERROR = "&e[&2SkinEvent&e] &4Error&8: &cAn error occurred while requesting skin data, please try again later!";
    public static String WAIT_A_MINUTE = "&e[&2SkinEvent&e] &4Error&8: &cPlease wait a minute before requesting that skin again. (Rate Limited)";
    public static String NOT_PLAYER = "&e[&2SkinEvent&e] &4Error&8: &cYou need to be a player!";
    public static String OUTDATED = "&e[&2SkinEvent&e] &4You are running an outdated version of SkinEvent!\n&cPlease update to the latest version.";
    public static String MENU_OPEN = "&2Opening the skins menu...";
    public static String PLAYERS_ONLY = "&4These commands are only for players!";
    public static String NEXT_PAGE = "&a&l»&7 Next Page&a&l »";
    public static String PREVIOUS_PAGE = "&e&l»&7 Previous Page&e&l «";
    public static String REMOVE_SKIN = "&c&l»&7 Remove Skin&c&l »";
    public static String SELECT_SKIN = "&2Click to select this skin";

    private static YamlConfig locale = new YamlConfig("plugins" + File.separator + "SkinEvent" + File.separator + "", "messages");

    public static void load() {
        try {
            locale.reload();

            for (Field f : Locale.class.getFields()) {

                if (f.getType() != String.class)
                    continue;

                f.set(null, C.c(locale.getString(f.getName(), f.get(null))));
            }
        } catch (Exception e) {
            System.out.println("§e[§2SkinEvent§e] §cCan't read messages.yml! Try removing it and restart your server.");
        }
    }
}