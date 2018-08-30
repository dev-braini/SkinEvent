package skinevent.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import skinevent.bungee.SkinApplier;
import skinevent.bungee.SkinEvent;
import skinevent.shared.storage.Config;
import skinevent.shared.storage.CooldownStorage;
import skinevent.shared.storage.Locale;
import skinevent.shared.storage.SkinStorage;
import skinevent.shared.utils.MojangAPI;
import skinevent.shared.utils.MojangAPI.SkinRequestException;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.concurrent.TimeUnit;

public class PlayerCommands extends Command {

    private final SkinEvent plugin;

    public PlayerCommands(SkinEvent plugin) {
        super("skin", null);
        this.plugin = plugin;
        this.plugin.getProxy().getPluginManager().registerCommand(plugin, this);
    }

    //Method called for the commands help.
    public void help(ProxiedPlayer p) {
        if (!Locale.SR_LINE.isEmpty())
            p.sendMessage(new TextComponent(Locale.SR_LINE));
        p.sendMessage(new TextComponent(Locale.HELP_PLAYER.replace("%ver%", SkinEvent.getInstance().getVersion())));
        if (p.hasPermission("skinevent.skinupdate"))
            p.sendMessage(new TextComponent(Locale.HELP_SR));
        if (!Locale.SR_LINE.isEmpty())
            p.sendMessage(new TextComponent(Locale.SR_LINE));
    }

    public void execute(CommandSender sender, final String[] args) {


        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent(Locale.NOT_PLAYER));
            return;
        }

        final ProxiedPlayer p = (ProxiedPlayer) sender;

        // Skin Help
        if (args.length == 0 || args.length > 2) {
            if (!Config.SKINWITHOUTPERM) {
                if (p.hasPermission("skinevent.skinupdate")) {
                    help(p);
                } else {
                    p.sendMessage(Locale.PLAYER_HAS_NO_PERMISSION);
                }
            } else {
                help(p);
            }
        }

        //Skin Clear and Skin (name)
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("clear")) {
                if (p.hasPermission("skinevent.skinupdate")) {
                    CooldownStorage.resetCooldown(p.getName());
                    CooldownStorage.setCooldown(p.getName(), Config.SKIN_CHANGE_COOLDOWN, TimeUnit.SECONDS);

                    ProxyServer.getInstance().getScheduler().runAsync(SkinEvent.getInstance(), () -> {
                        try {
                            SkinStorage.removePlayerSkin(p.getName());
                            SkinStorage.setPlayerSkin(p.getName(), p.getName());
                            SkinApplier.applySkin(p);
                            p.sendMessage(new TextComponent(Locale.SKIN_CLEAR_SUCCESS));
                            return;
                        } catch (Exception e) {
                            return;
                        }
                    });
                } else {
                    p.sendMessage(Locale.PLAYER_HAS_NO_PERMISSION);
                }
            } else if (args[0].equalsIgnoreCase("event")) {
                if (p.hasPermission("skinevent.skinupdate")) {
                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(b);
                    try {
                        out.writeUTF(sender.getName());

                        p.getServer().sendData("startSkinEvent", b.toByteArray());
                    } catch (Exception e) { }
                } else {
                    p.sendMessage(new TextComponent(Locale.PLAYER_HAS_NO_PERMISSION));
                    return;
                }
            } else {
                if (p.hasPermission("skinevent.skinupdate")) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(args[0]);

                    //skin <skin>
                    final String skin = sb.toString();

                    if (Config.DISABLED_SKINS_ENABLED)
                        if (!p.hasPermission("skinevent.bypassdisabled")) {
                            for (String dskin : Config.DISABLED_SKINS)
                                if (skin.equalsIgnoreCase(dskin)) {
                                    p.sendMessage(new TextComponent(Locale.SKIN_DISABLED));
                                    return;
                                }
                        }

                    if (!p.hasPermission("skinevent.bypasscooldown") && CooldownStorage.hasCooldown(p.getName())) {
                        p.sendMessage(new TextComponent(Locale.SKIN_COOLDOWN_NEW.replace("%s", "" + CooldownStorage.getCooldown(p.getName()))));
                        return;
                    }
                    CooldownStorage.resetCooldown(p.getName());
                    CooldownStorage.setCooldown(p.getName(), Config.SKIN_CHANGE_COOLDOWN, TimeUnit.SECONDS);

                    ProxyServer.getInstance().getScheduler().runAsync(SkinEvent.getInstance(), () -> {
                        try {
                            MojangAPI.getUUID(skin);
                            SkinStorage.setPlayerSkin(p.getName(), skin);
                            SkinApplier.applySkin(p);
                            p.sendMessage(new TextComponent(Locale.SKIN_CHANGE_SUCCESS));
                            return;
                        } catch (SkinRequestException e) {
                            p.sendMessage(new TextComponent(e.getReason()));
                            return;
                        }
                    });
                    return;
                } else {
                    p.sendMessage(new TextComponent(Locale.PLAYER_HAS_NO_PERMISSION));
                    return;
                }
            }
        }

        //skin set
        if (args.length == 2) {
            if (p.hasPermission("skinevent.skinupdate")) {
                if (args[0].equalsIgnoreCase("set")) {

                    StringBuilder sb = new StringBuilder();
                    sb.append(args[1]);

                    final String skin = sb.toString();

                    if (Config.DISABLED_SKINS_ENABLED)
                        if (!p.hasPermission("skinevent.bypassdisabled")) {
                            for (String dskin : Config.DISABLED_SKINS)
                                if (skin.equalsIgnoreCase(dskin)) {
                                    p.sendMessage(new TextComponent(Locale.SKIN_DISABLED));
                                    return;
                                }
                        }

                    if (!p.hasPermission("skinevent.bypasscooldown") && CooldownStorage.hasCooldown(p.getName())) {
                        p.sendMessage(new TextComponent(Locale.SKIN_COOLDOWN_NEW.replace("%s", "" + CooldownStorage.getCooldown(p.getName()))));
                        return;
                    }
                    CooldownStorage.resetCooldown(p.getName());
                    CooldownStorage.setCooldown(p.getName(), Config.SKIN_CHANGE_COOLDOWN, TimeUnit.SECONDS);

                    ProxyServer.getInstance().getScheduler().runAsync(SkinEvent.getInstance(), () -> {
                        try {
                            MojangAPI.getUUID(skin);
                            SkinStorage.setPlayerSkin(p.getName(), skin);
                            SkinApplier.applySkin(p);
                            p.sendMessage(new TextComponent(Locale.SKIN_CHANGE_SUCCESS));
                            return;
                        } catch (SkinRequestException e) {
                            p.sendMessage(new TextComponent(e.getReason()));
                            return;
                        }
                    });
                    return;
                } else if(args[0].equalsIgnoreCase("vote")) {
                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(b);
                    try {
                        out.writeUTF(sender.getName());

                        if(args[1].equalsIgnoreCase("yes")) {
                            p.getServer().sendData("skinEventVoteYes", b.toByteArray());
                        } else if (args[1].equalsIgnoreCase("no")){
                            p.getServer().sendData("skinEventVoteNo", b.toByteArray());
                        }
                    } catch (Exception e) { }

                    return;
                } else {
                    help(p);
                    return;
                }
            } else {
                p.sendMessage(Locale.PLAYER_HAS_NO_PERMISSION);
            }
        }
    }
}