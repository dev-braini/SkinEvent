package skinevent.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.LoginResult.Property;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import skinevent.bungee.SkinApplier;
import skinevent.bungee.SkinEvent;
import skinevent.shared.storage.Config;
import skinevent.shared.storage.Locale;
import skinevent.shared.storage.SkinStorage;
import skinevent.shared.utils.C;
import skinevent.shared.utils.MojangAPI;
import skinevent.shared.utils.MojangAPI.SkinRequestException;

import java.util.List;

public class AdminCommands extends Command {

    public AdminCommands() {
        super("skinevent", null, new String[]{"sr"});
    }

    @SuppressWarnings("deprecation")
    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (sender.hasPermission("skinevent.admin")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                Locale.load();
                Config.load(SkinEvent.getInstance().getResourceAsStream("config.yml"));
                sender.sendMessage(Locale.RELOAD);

            } else if (args.length == 2 && args[0].equalsIgnoreCase("drop")) {
                StringBuilder sb = new StringBuilder();
                for (int i = 2; i < args.length; i++)
                    if (args.length == 3)
                        sb.append(args[i]);
                    else if (args.length > 3)
                        if (i + 1 == args.length)
                            sb.append(args[i]);
                        else
                            sb.append(args[i] + " ");

                SkinStorage.removeSkinData(sb.toString());
                sender.sendMessage(Locale.SKIN_DATA_DROPPED.replace("%player", sb.toString()));

            } else if (args.length > 2 && args[0].equalsIgnoreCase("set")) {
                StringBuilder sb = new StringBuilder();
                sb.append(args[2]);

                final String skin = sb.toString();
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[1]);

                if (player == null)
                    for (ProxiedPlayer pl : ProxyServer.getInstance().getPlayers())
                        if (pl.getName().startsWith(args[1])) {
                            player = pl;
                            break;
                        }

                if (player == null) {
                    sender.sendMessage(Locale.NOT_ONLINE);
                    return;
                }

                final ProxiedPlayer p = player;

                SkinEvent.getInstance().getExecutor().submit(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            MojangAPI.getUUID(skin);
                            SkinStorage.setPlayerSkin(p.getName(), skin);
                            SkinApplier.applySkin(p);

                            if ((sender instanceof ProxiedPlayer)) {
                                sender.sendMessage("§e[§2SkinEvent§e] §fDer Skin von §e"+p.getName()+" §fwurde erfolgreich geaendert");
                                p.sendMessage("§e[§2SkinEvent§e] §fDein Skin wurde geaendert von §e" + sender.getName());
                            } else {
                                p.sendMessage("§e[§2SkinEvent§e] §fDein Skin wurde geaendert.");
                            }
                            p.sendMessage("§e[§2SkinEvent§e] §fDu kannst ihn mit §e/skin clear §fzurücksetzen.");

                            return;

                        } catch (SkinRequestException e) {
                            sender.sendMessage(e.getReason());
                            return;
                        }
                    }

                });

            } else if (args.length == 1 && args[0].equalsIgnoreCase("config")) {
                sender.sendMessage(Locale.HELP_CONFIG);
                return;

                // DefaultSkins
            } else if (args.length >= 2 && args[0].equalsIgnoreCase("defaultSkins")) {
                if (args[1].equalsIgnoreCase("true")) {
                    Config.DEFAULT_SKINS_ENABLED = true;
                    Config.set("DefaultSkins.Enabled", String.valueOf(args[1]));
                    reloadConfig(sender, "&2Default skins has been enabled.");

                } else if (args[1].equalsIgnoreCase("false")) {
                    Config.DEFAULT_SKINS_ENABLED = false;
                    Config.set("DefaultSkins.Enabled", String.valueOf(args[1]));
                    reloadConfig(sender, "&4Default skins has been disabled.");
                } else if (args[1].equalsIgnoreCase("add")) {
                    String skin = args[2];
                    List<String> skins = Config.DEFAULT_SKINS;
                    skins.add(skin);
                    Config.set("DefaultSkins.Names", skins);
                    reloadConfig(sender, "&2Added &f" + skin + " &2to the default skins list");
                }
                return;
            } else if (args.length >= 2 && args[0].equalsIgnoreCase("disabledSkins")) {
                if (args[1].equalsIgnoreCase("true")) {
                    Config.DISABLED_SKINS_ENABLED = true;
                    Config.set("DisabledSkins.Enabled", String.valueOf(args[1]));
                    reloadConfig(sender, "&2Disabled skins has been enabled.");
                } else if (args[1].equalsIgnoreCase("false")) {
                    Config.DISABLED_SKINS_ENABLED = false;
                    Config.set("DisabledSkins.Enabled", String.valueOf(args[1]));
                    reloadConfig(sender, "&4Disabled skins has been disabled.");
                } else if (args[1].equalsIgnoreCase("add")) {
                    String skin = args[2];
                    List<String> skins = Config.DISABLED_SKINS;
                    skins.add(skin);
                    Config.set("DisabledSkins.Names", skins);
                    reloadConfig(sender, "&2Added &f" + skin + " &2to the disabled skins list");
                }
                return;

            } else if (args.length == 2 && args[0].equalsIgnoreCase("joinSkins")) {
                if (args[1].equalsIgnoreCase("true")) {
                    Config.DISABLE_ONJOIN_SKINS = true;
                    Config.set("DisableOnJoinSkins", String.valueOf(args[1]));
                    reloadConfig(sender, "&4Players will have skins on join.");
                } else if (args[1].equalsIgnoreCase("false")) {
                    Config.DISABLE_ONJOIN_SKINS = false;
                    Config.set("DisableOnJoinSkins", String.valueOf(args[1]));
                    ;
                    reloadConfig(sender, "&2Players will not have skins on join.");
                }
                return;
            } else if (args.length == 2 && args[0].equalsIgnoreCase("updater")) {
                if (args[1].equalsIgnoreCase("true")) {
                    Config.UPDATER_ENABLED = true;
                    Config.set("Updater.Enabled", String.valueOf(args[1]));
                    reloadConfig(sender, "&2The updater has been enabled.");
                } else if (args[1].equalsIgnoreCase("false")) {
                    Config.UPDATER_ENABLED = false;
                    Config.set("Updater.Enabled", String.valueOf(args[1]));
                    reloadConfig(sender, "&4The updater has been disabled.");
                }
                return;
            } else if (args.length == 2 && args[0].equalsIgnoreCase("skinwithoutperm")) {
                if (args[1].equalsIgnoreCase("true")) {
                    Config.SKINWITHOUTPERM = true;
                    Config.set("SkinWithoutPerm", String.valueOf(args[1]));
                    reloadConfig(sender, "&2Skins will not require permissions.");
                } else if (args[1].equalsIgnoreCase("false")) {
                    Config.SKINWITHOUTPERM = false;
                    Config.set("SkinWithoutPerm", String.valueOf(args[1]));
                    reloadConfig(sender, "&2Skins will require permissions.");
                }
                return;
            } else if (args.length == 2 && args[0].equalsIgnoreCase("skinCooldown")) {
                if (isStringInt(args[1])) {
                    Config.SKIN_CHANGE_COOLDOWN = Integer.valueOf(args[1]);
                    Config.set("SkinChangeCooldown", Integer.valueOf(args[1]));
                    reloadConfig(sender, "&2The skin change cooldown has been set to &f" + Integer.valueOf(args[1]) + "&seconds(s)");
                }
                return;
            } else if (args.length == 2 && args[0].equalsIgnoreCase("SkinExpiresAfter")) {
                if (isStringInt(args[1])) {
                    Config.SKIN_EXPIRES_AFTER = Integer.valueOf(args[1]);
                    Config.set("SkinExpiresAfter", Integer.valueOf(args[1]));
                    reloadConfig(sender, "&2The skin cache time is now &f" + Integer.valueOf(args[1]) + "&2minute(s)");
                }
                return;

            } else if (args.length > 0 && args[0].equalsIgnoreCase("props")) {

                ProxiedPlayer p = null;

                if (args.length == 1) {
                    if (!(sender instanceof ProxiedPlayer)) {
                        sender.sendMessage(Locale.NOT_PLAYER);
                        return;
                    }
                    p = (ProxiedPlayer) sender;
                } else if (args.length > 1) {
                    String name = "";
                    for (int i = 1; i < args.length; i++)
                        if (args.length == 2)
                            name += args[i];
                        else if (args.length > 2)
                            if (i + 1 == args.length)
                                name += args[i];
                            else
                                name += args[i] + " ";

                    p = ProxyServer.getInstance().getPlayer(name);

                    if (p == null) {
                        sender.sendMessage(Locale.NOT_ONLINE);
                        return;
                    }
                }

                InitialHandler h = (InitialHandler) p.getPendingConnection();
                Property prop = h.getLoginProfile().getProperties()[0];

                if (prop == null) {
                    sender.sendMessage(Locale.NO_SKIN_DATA);
                    return;
                }

                CommandSender cons = ProxyServer.getInstance().getConsole();

                cons.sendMessage("\n§aName: §8" + prop.getName());
                cons.sendMessage("\n§aValue : §8" + prop.getValue());
                cons.sendMessage("\n§aSignature : §8" + prop.getSignature());

                String decoded = Base64Coder.decodeString(prop.getValue());
                cons.sendMessage("\n§aValue Decoded: §e" + decoded);

                sender.sendMessage("\n§e" + decoded);

                sender.sendMessage("§cMore info in console!");
            } else {
                if (!Locale.SR_LINE.isEmpty())
                    sender.sendMessage(Locale.SR_LINE);
                sender.sendMessage(Locale.HELP_ADMIN.replace("%ver%", SkinEvent.getInstance().getVersion()));
                if (!Locale.SR_LINE.isEmpty())
                    sender.sendMessage(Locale.SR_LINE);
            }
        } else {
            sender.sendMessage(Locale.PLAYER_HAS_NO_PERMISSION);
            return;

        }
    }

    public boolean isStringInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public void reloadConfig(CommandSender sender, String msg) {
        Locale.load();
        Config.load(SkinEvent.getInstance().getResourceAsStream("config.yml"));
        sender.sendMessage(new TextComponent(C.c(msg)));
    }
}
