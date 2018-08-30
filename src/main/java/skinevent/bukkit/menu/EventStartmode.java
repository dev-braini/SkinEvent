package skinevent.bukkit.menu;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import skinevent.bukkit.SkinEvent;
import skinevent.shared.storage.SkinEventCooldownStorage;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

public class EventStartmode implements Listener {

    private SkinEvent plugin;
    private Inventory inv;
    private Player sender;
    private Integer noVotes = 0, yesVotes = 0, numPlayers;
    private boolean isRunning = false;
    private EventSkinChoose eventSkinChoose;
    private int eventDuration;
    ArrayList<Player> playerVoted = new ArrayList<>();
    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

    public EventStartmode(SkinEvent p) {
        this.plugin = p;
        eventSkinChoose = new EventSkinChoose(plugin, this);
        inv = Bukkit.getServer().createInventory(null, 9, "SkinEvent: Abstimmung starten?");

        ItemStack y = createItem(DyeColor.LIME, ChatColor.GREEN + "Ja, Abstimmung starten.");
        ItemStack n = createItem(DyeColor.RED, ChatColor.RED + "Nein, Skin direkt verteilen.");
        ItemStack c = createItem(DyeColor.GRAY, ChatColor.GRAY + "Abbrechen");

        inv.clear();
        inv.setItem(0, y);
        inv.setItem(2, n);
        inv.setItem(8, c);
        Bukkit.getServer().getPluginManager().registerEvents(this, p);
    }

    public ArrayList<Player> getPlayerVoted() {
        return playerVoted;
    }

    private ItemStack createItem(DyeColor dc, String text) {
        ItemStack i= new Wool(dc).toItemStack(1);
        ItemMeta im = i.getItemMeta();
        im.setDisplayName(text);
        i.setItemMeta(im);
        return i;
    }

    public void incYesVotes() {
        yesVotes++;
    }

    public void incNoVotes() {
        noVotes++;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void showInventory(Player p) {
        if(isRunning) {
            plugin.sendMessageToPlayer(p, "§4Es laeuft bereits ein SkinEvent.");
            return;
        }
        this.sender = p;

        if(p.hasPermission("skinevent.admin")){
            p.openInventory(inv);
        } else eventSkinChoose.showInventory(p, false);
    }

    public void doASurvey(Player player, String skin) {
        numPlayers = Bukkit.getServer().getOnlinePlayers().size();
        yesVotes = 0; noVotes = 0;
        playerVoted.clear();
        isRunning = true;
        eventDuration = 60;

        plugin.sendBroadcastMessage(sender.getDisplayName() + " hat eine Umfrage gestartet.");
        plugin.sendBroadcastMessage("§e/skin vote yes §f fuer §aJA");
        plugin.sendBroadcastMessage("§e/skin vote no  §f fuer §4NEIN");

        // check survey for one minute
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        final Runnable task = new Runnable() {
            public void run() {
                String message = "Abstimmung dauert noch " + eventDuration + " Sekunden.";

                if(eventDuration > 0) {
                    if(eventDuration == 20) plugin.sendBroadcastMessage(message);
                    else plugin.sendMessageToPlayer(player, message);

                    eventDuration -= 20;
                }
            }
        };
        final ScheduledFuture<?> handle = scheduler.scheduleAtFixedRate(task, 1, 20, SECONDS);
        scheduler.schedule(new Runnable() {
            public void run() {
                handle.cancel(true);

                isRunning = false;
                double percent = (yesVotes.doubleValue()/numPlayers.doubleValue())*100;

                if(percent >= 50) {
                    plugin.sendBroadcastMessage("§aAbstimmung erfolgreich §f - §e" + Math.round(percent) + "%§f JA-Stimmen");
                    plugin.sendToBungeeCord(sender, "setPlayerSkin", skin);
                } else {
                    plugin.sendBroadcastMessage("§4Abstimmung gescheitert §f - §e" + Math.round(percent) + "%§f JA-Stimmen");
                }

            }
        }, 60, SECONDS);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        Inventory inventory = event.getInventory();

        if (inventory.getName().equals(inv.getName()) && clicked.getType() == Material.WOOL) {
            String clickedDisplayName = clicked.getItemMeta().getDisplayName();
            event.setCancelled(true);
            player.closeInventory();
            boolean forceChange = false;

            if (clickedDisplayName.contains("Nein")) forceChange = true;
            if(!clickedDisplayName.contains("Abbrechen")) eventSkinChoose.showInventory(player, forceChange);
            else SkinEventCooldownStorage.resetCooldown(player.getName());
        }
    }
}
