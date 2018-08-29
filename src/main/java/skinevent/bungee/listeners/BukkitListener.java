package skinevent.bungee.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import skinevent.bungee.SkinEvent;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Collection;

public class BukkitListener implements Listener {
    private static SkinEvent plugin;

    public BukkitListener(SkinEvent plugin) {
        this.plugin = plugin;
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPluginMessage(PluginMessageEvent e) {
        if(!e.getTag().equalsIgnoreCase("setPlayerSkin"))
            return;

        DataInputStream stream = new DataInputStream(new ByteArrayInputStream(e.getData()));

        String channel = null;
        try {
            channel = stream.readUTF();
            if(channel.equalsIgnoreCase("setPlayerSkin")) {
                String senderName = stream.readUTF();
                ProxiedPlayer p = ProxyServer.getInstance().getPlayer(e.getReceiver().toString());
                Collection<ProxiedPlayer> onlinePlayers = ProxyServer.getInstance().getServers().get(p.getServer().getInfo().getName()).getPlayers();

                ProxyServer.getInstance().getScheduler().runAsync(SkinEvent.getInstance(), () -> {
                    for (ProxiedPlayer player : onlinePlayers) {
                        try{ Thread.sleep(20); } catch(InterruptedException ex) { Thread.currentThread().interrupt(); }
                        String command = "sr set " + player.getName() + " " + senderName;

                        plugin.sendToConsole(command);
                    }
                });
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}