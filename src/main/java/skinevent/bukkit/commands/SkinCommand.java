package skinevent.bukkit.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import skinevent.bukkit.SkinEvent;
import skinevent.shared.storage.Config;
import skinevent.shared.storage.CooldownStorage;
import skinevent.shared.storage.Locale;
import skinevent.shared.storage.SkinStorage;
import skinevent.shared.utils.MojangAPI;
import skinevent.shared.utils.MojangAPI.SkinRequestException;

import java.util.concurrent.TimeUnit;

public class SkinCommand implements CommandExecutor {

    //Method called for the commands help.
    public void help(Player p) {
        if (!Locale.SR_LINE.isEmpty())
            p.sendMessage(Locale.SR_LINE);
        p.sendMessage(Locale.HELP_PLAYER.replace("%ver%", SkinEvent.getInstance().getVersion()));
        if (p.hasPermission("skinevent.cmds") || p.isOp())
            p.sendMessage(Locale.HELP_SR);
        if (!Locale.SR_LINE.isEmpty())
            p.sendMessage(Locale.SR_LINE);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(Locale.NOT_PLAYER);
            return true;
        }

        final Player p = (Player) sender;

        // Skin Help
        if (args.length == 0 || args.length > 2) {
            if (!Config.SKINWITHOUTPERM) {
                if (p.hasPermission("skinevent.playercmds")) {
                    help(p);
                } else {
                    p.sendMessage(Locale.PLAYER_HAS_NO_PERMISSION);
                    return true;
                }
            }
            return true;
        }

        // Skin Clear and Skin (name)
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("clear")) {
                Object props = null;

                SkinStorage.removePlayerSkin(p.getName());
                props = SkinStorage.createProperty("textures", "", "");
                SkinEvent.getInstance().getFactory().applySkin(p, props);
                SkinEvent.getInstance().getFactory().updateSkin(p);
                p.sendMessage(Locale.SKIN_CLEAR_SUCCESS);

                return true;
            } else {

                StringBuilder sb = new StringBuilder();
                sb.append(args[0]);

                final String skin = sb.toString();

                if (Config.DISABLED_SKINS_ENABLED)
                    if (!p.hasPermission("skinevent.bypassdisabled") && !p.isOp()) {
                        for (String dskin : Config.DISABLED_SKINS)
                            if (skin.equalsIgnoreCase(dskin)) {
                                p.sendMessage(Locale.SKIN_DISABLED);
                                return true;
                            }
                    }

                if (p.hasPermission("skinevent.bypasscooldown")) {

                } else {
                    if (CooldownStorage.hasCooldown(p.getName())) {
                        p.sendMessage(Locale.SKIN_COOLDOWN_NEW.replace("%s", "" + CooldownStorage.getCooldown(p.getName())));
                        return true;
                    }
                }

                CooldownStorage.resetCooldown(p.getName());
                CooldownStorage.setCooldown(p.getName(), Config.SKIN_CHANGE_COOLDOWN, TimeUnit.SECONDS);

                Bukkit.getScheduler().runTaskAsynchronously(SkinEvent.getInstance(), () -> {
                    try {
                        MojangAPI.getUUID(skin);

                        SkinStorage.setPlayerSkin(p.getName(), skin);
                        SkinEvent.getInstance().getFactory().applySkin(p,
                                SkinStorage.getOrCreateSkinForPlayer(p.getName()));
                        p.sendMessage(Locale.SKIN_CHANGE_SUCCESS);
                        return;
                    } catch (SkinRequestException e) {
                        p.sendMessage(e.getReason());
                        return;
                    }
                });
                return true;
            }
        }

        // Skin Set
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("set")) {

                StringBuilder sb = new StringBuilder();
                sb.append(args[1]);

                final String skin = sb.toString();

                if (Config.DISABLED_SKINS_ENABLED)
                    if (!p.hasPermission("skinevent.bypassdisabled") && !p.isOp()) {
                        for (String dskin : Config.DISABLED_SKINS)
                            if (skin.equalsIgnoreCase(dskin)) {
                                p.sendMessage(Locale.SKIN_DISABLED);
                                return true;
                            }
                    }

                if (p.hasPermission("skinevent.bypasscooldown")) {

                } else {
                    if (CooldownStorage.hasCooldown(p.getName())) {
                        p.sendMessage(Locale.SKIN_COOLDOWN_NEW.replace("%s", "" + CooldownStorage.getCooldown(p.getName())));
                        return true;
                    }
                }

                CooldownStorage.resetCooldown(p.getName());
                CooldownStorage.setCooldown(p.getName(), Config.SKIN_CHANGE_COOLDOWN, TimeUnit.SECONDS);

                Bukkit.getScheduler().runTaskAsynchronously(SkinEvent.getInstance(), () -> {
                    try {
                        MojangAPI.getUUID(skin);

                        SkinStorage.setPlayerSkin(p.getName(), skin);
                        SkinEvent.getInstance().getFactory().applySkin(p, SkinStorage.getOrCreateSkinForPlayer(p.getName()));
                        p.sendMessage(Locale.SKIN_CHANGE_SUCCESS);
                        return;
                    } catch (SkinRequestException e) {
                        p.sendMessage(e.getReason());
                        return;
                    }
                });
                return true;
            } else {
                help(p);
                return true;
            }
        }
        return true;
    }
}
