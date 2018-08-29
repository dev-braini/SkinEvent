package skinevent.bukkit.menu;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import skinevent.bukkit.SkinEvent;
import skinevent.shared.utils.MojangAPI;

import java.util.Map;
import java.util.UUID;

public class EventSkinChoose implements Listener {

    private SkinEvent plugin;
    private Inventory inv;
    private Player sender;
    private boolean forceChange;
    private EventStartmode esc;

    public EventSkinChoose(SkinEvent p, EventStartmode esc) {
        this.plugin = p;
        this.esc = esc;
        inv = Bukkit.getServer().createInventory(null, 27, "Skin auswaehlen...");
        //{"id":"bd6120e17b8d45d88d3e474a278d162a","name":"braini_","properties":[{"name":"textures","value":"eyJ0aW1lc3RhbXAiOjE1MzU0NzcyNTgyMDgsInByb2ZpbGVJZCI6ImJkNjEyMGUxN2I4ZDQ1ZDg4ZDNlNDc0YTI3OGQxNjJhIiwicHJvZmlsZU5hbWUiOiJicmFpbmlfIiwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzQ5NTc1ODRjNzk3MTM3ZmFlMTI3MjMyZjBhZGIyOTMyZjQ3MjhkZTBiOTczNDYxNzNkYWQzYWQwNjk3YWQwYmMiLCJtZXRhZGF0YSI6eyJtb2RlbCI6InNsaW0ifX19fQ=="}]}

        ItemStack AbgegerieftHD = createSkull("AbgegrieftHD");
        ItemStack LordVarus = createSkull("LordVarus");
        ItemStack Bantor = createSkull("Bantor");
        ItemStack Backz = createSkull("DerBackz");
        ItemStack RypexYT = createSkull("RypexYT");
        ItemStack Zwiebackgesicht = createSkull("Zwiebackgesicht");
        ItemStack HoodedWolfi = createSkull("HoodedWolfi");
        ItemStack ScarWatergren = createSkull("ScarWatergren");
        ItemStack SkillZilla144p = createSkull("SkillZilla144p");

        ItemStack Creeper = createSkull("Creeper");
        ItemStack Zombie = createSkull("Zombie");
        ItemStack Pig = createSkull("Pig");
        ItemStack Sheep = createSkull("Sheep");
        ItemStack Golem = createSkull("Golem");
        ItemStack Villager = createSkull("Villager");
        ItemStack Turtle = createSkull("Turtle");
        ItemStack Guardian = createSkull("Guardian");
        ItemStack Wither = createSkull("Wither");

        inv.clear();
        inv.setItem(0, AbgegerieftHD);
        /*inv.setItem(1, LordVarus);
        inv.setItem(2, Bantor);
        inv.setItem(3, Backz);
        inv.setItem(4, RypexYT);
        inv.setItem(5, Zwiebackgesicht);
        inv.setItem(6, HoodedWolfi);
        inv.setItem(7, ScarWatergren);
        inv.setItem(8, SkillZilla144p);

        inv.setItem(18, Creeper);
        inv.setItem(19, Zombie);
        inv.setItem(20, Pig);
        inv.setItem(21, Sheep);
        inv.setItem(22, Golem);
        inv.setItem(23, Villager);
        inv.setItem(24, Turtle);
        inv.setItem(25, Guardian);
        inv.setItem(26, Wither);*/
        Bukkit.getServer().getPluginManager().registerEvents(this, p);
    }

    private ItemStack createSkull(String playerName) {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
        SkullMeta skMeta = (SkullMeta) skull.getItemMeta();
        skMeta.setDisplayName(playerName);

        try {
            MojangAPI.getUUID(playerName);
            skMeta.setOwner(playerName);
        } catch (MojangAPI.SkinRequestException e) { }

        skull.setItemMeta(skMeta);

        return skull;
    }

    public void showInventory(Player p, boolean forceChange) {
        this.sender = p;
        this.forceChange = forceChange;
        p.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        Inventory inventory = event.getInventory();

        if (inventory.getName().equals(inv.getName()) && clicked.getType() == Material.SKULL_ITEM) {
            String clickedDisplayName = clicked.getItemMeta().getDisplayName();
            event.setCancelled(true);
            player.closeInventory();

            if(forceChange) {
                plugin.sendToBungeeCord(sender, "setPlayerSkin", clickedDisplayName);
            }
            else esc.doASurvey(clickedDisplayName);
        }
    }
}
