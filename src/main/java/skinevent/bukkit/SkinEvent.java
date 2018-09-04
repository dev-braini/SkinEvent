package skinevent.bukkit;

import org.inventivetalent.update.spiget.SpigetUpdate;
import org.inventivetalent.update.spiget.comparator.VersionComparator;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import skinevent.bukkit.commands.GUICommand;
import skinevent.bukkit.commands.SkinCommand;
import skinevent.bukkit.commands.SrCommand;
import skinevent.bukkit.menu.EventStartmode;
import skinevent.bukkit.menu.SkinsGUI;
import skinevent.bukkit.skinfactory.SkinFactory;
import skinevent.bukkit.skinfactory.UniversalSkinFactory;
import skinevent.shared.storage.*;
import skinevent.shared.utils.MySQL;
import skinevent.shared.utils.ReflectionUtil;

import java.io.*;
import java.util.List;

public class SkinEvent extends JavaPlugin {

    private static SkinEvent instance;
    private SkinFactory factory;
    private MySQL mysql;
    private boolean bungeeEnabled;
    public static EventStartmode skinEventInventory;

    public static SkinEvent getInstance() {
        return instance;
    }

    public SkinFactory getFactory() {
        return factory;
    }

    public MySQL getMySQL() {
        return mysql;
    }

    public String getVersion() {
        return getDescription().getVersion();
    }

    public void sendToBungeeCord(Player player, String channel, String information) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(stream);

        try {
            output.writeUTF(channel);
            output.writeUTF(information);
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendPluginMessage(this, channel, stream.toByteArray());
    }

    public void sendBroadcastMessage(String message) {
        getServer().broadcastMessage("§e[§2SkinEvent§e] §f" + message);
    }

    public void sendMessageToPlayer(Player player, String message) {
        player.sendMessage("§e[§2SkinEvent§e] §f" + message);
    }

    public void sendMessageToConsole(String message) {

    }

    public void onEnable() {

        ConsoleCommandSender console = getServer().getConsoleSender();

        SpigetUpdate updater = new SpigetUpdate(this, 2124);
        updater.setVersionComparator(VersionComparator.EQUAL);
        updater.setVersionComparator(VersionComparator.SEM_VER);

        instance = this;
        skinEventInventory = new EventStartmode(this);

        try {
            // Doesn't support Cauldron and stuff..
            Class.forName("net.minecraftforge.cauldron.CauldronHooks");
            console.sendMessage("§e[§2SkinEvent§e] §cSkinEvent doesn't support Cauldron, Thermos or KCauldron, Sorry :(");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        } catch (Exception e) {
            try {
                // Checking for old versions
                factory = (SkinFactory) Class
                        .forName("skinevent.bukkit.skinfactory.SkinFactory_" + ReflectionUtil.serverVersion)
                        .newInstance();
            } catch (Exception ex) {
                // 1.8+++
                factory = new UniversalSkinFactory();
            }
        }
        console.sendMessage("§e[§2SkinEvent§e] §aDetected Minecraft §e" + ReflectionUtil.serverVersion + "§a, using §e" + factory.getClass().getSimpleName() + "§a.");

        // Multiverse Core support.
        MCoreAPI.init();
        if (MCoreAPI.check())
            console.sendMessage("§e[§2SkinEvent§e] §aDetected §eMultiverse-Core§a! Using it for dimensions.");

        // Detect ChangeSkin
        if(getServer().getPluginManager().getPlugin("ChangeSkin") != null) {
            console.sendMessage("§e[§2SkinEvent§e] §cWe have detected ChangeSkin on your server, disabling SkinEvent.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Bungeecord stuff
        try {
            bungeeEnabled = YamlConfiguration.loadConfiguration(new File("spigot.yml")).getBoolean("settings.bungeecord");
        } catch (Exception e) {
            bungeeEnabled = false;
        }

        if (bungeeEnabled) {
            Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            Bukkit.getMessenger().registerOutgoingPluginChannel(this, "setPlayerSkin");
            Bukkit.getMessenger().registerIncomingPluginChannel(this, "startSkinEvent", new PluginMessageListener() {
                @Override
                public void onPluginMessageReceived(String channel, final Player player, final byte[] message) {
                    if (!channel.equals("startSkinEvent"))
                        return;

                    Bukkit.getScheduler().runTaskAsynchronously(getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            try {
                                skinEventInventory.showInventory(player);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });

            Bukkit.getMessenger().registerIncomingPluginChannel(this, "skinEventVoteYes", new PluginMessageListener() {
                @Override
                public void onPluginMessageReceived(String channel, final Player player, final byte[] message) {
                    if (!channel.equals("skinEventVoteYes"))
                        return;

                    Bukkit.getScheduler().runTaskAsynchronously(getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
                            try {
                                if(skinEventInventory.isRunning()) {
                                    if (!skinEventInventory.getPlayerVoted().contains(player)) {
                                        skinEventInventory.getPlayerVoted().add(player);
                                        skinEventInventory.incYesVotes();
                                        sendMessageToPlayer(player, "Danke fuer deine Stimme - §aJA");
                                    } else sendMessageToPlayer(player, "§4Du hast bereits abgestimmt!");
                                } else sendMessageToPlayer(player, "§4Momentan laeuft kein SkinEvent!");

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            });

            Bukkit.getMessenger().registerIncomingPluginChannel(this, "skinEventVoteNo", new PluginMessageListener() {
                @Override
                public void onPluginMessageReceived(String channel, final Player player, final byte[] message) {
                    if (!channel.equals("skinEventVoteNo"))
                        return;

                    Bukkit.getScheduler().runTaskAsynchronously(getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
                            try {
                                if(skinEventInventory.isRunning()) {
                                    if(!skinEventInventory.getPlayerVoted().contains(player)) {
                                        skinEventInventory.getPlayerVoted().add(player);
                                        skinEventInventory.incNoVotes();
                                        sendMessageToPlayer(player, "Danke fuer deine Stimme - §4NEIN");
                                    } else sendMessageToPlayer(player, "§4Du hast bereits abgestimmt!");
                                } else sendMessageToPlayer(player, "§4Momentan laeuft kein SkinEvent!");

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            });

            Bukkit.getMessenger().registerIncomingPluginChannel(this, "SkinEvent", new PluginMessageListener() {
                @Override
                public void onPluginMessageReceived(String channel, final Player player, final byte[] message) {
                    if (!channel.equals("SkinEvent"))
                        return;

                    Bukkit.getScheduler().runTaskAsynchronously(getInstance(), new Runnable() {

                        @Override
                        public void run() {
                            DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));

                            try {
                                String subchannel = in.readUTF();

                                if (subchannel.equalsIgnoreCase("SkinUpdate")) {
                                    try {
                                        factory.applySkin(player,
                                                SkinStorage.createProperty(in.readUTF(), in.readUTF(), in.readUTF()));
                                    } catch (Exception e) {
                                    }
                                    factory.updateSkin(player);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });

            console.sendMessage("");
            console.sendMessage("§e[§2SkinEvent§e] §a   +==================+");
            console.sendMessage("§e[§2SkinEvent§e] §a   |  SkinEvent v"+getVersion()+"  |");
            console.sendMessage("§e[§2SkinEvent§e] §a   +------------------+");
            console.sendMessage("§e[§2SkinEvent§e] §a   |  §eBungee Mode§a     |");
            console.sendMessage("§e[§2SkinEvent§e] §a   +==================+");
            console.sendMessage("");

            return;
        }

        // Config stuff
        Config.load(getResource("config.yml"));
        Locale.load();

        if (Config.USE_MYSQL)
            SkinStorage.init(mysql = new MySQL(Config.MYSQL_HOST, Config.MYSQL_PORT, Config.MYSQL_DATABASE,
                    Config.MYSQL_USERNAME, Config.MYSQL_PASSWORD));
        else
            SkinStorage.init(getDataFolder());

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new SkinChangeCooldownStorage(), 0, 1 * 20);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new SkinEventCooldownStorage(), 0, 1 * 20);

        // Commands
        getCommand("skinevent").setExecutor(new SrCommand());
        getCommand("skin").setExecutor(new SkinCommand());
        getCommand("skins").setExecutor(new GUICommand());
        getCommand("skinver").setExecutor(new CommandExecutor() {

            public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] arg3) {
                sender.sendMessage("§8This server is running §aSkinEvent §e"
                        + SkinEvent.getInstance().getVersion() + "§8, made with love by §c"
                        + SkinEvent.getInstance().getDescription().getAuthors().get(0)
                        + "§8, utilizing Minecraft §a" + ReflectionUtil.serverVersion + "§8.");
                return false;
            }

        });

        Bukkit.getPluginManager().registerEvents(new SkinsGUI(), this);
        Bukkit.getPluginManager().registerEvents(new Listener() {

            // LoginEvent happens on attemptLogin so its the best place to set the skin
            @EventHandler
            public void onLogin(PlayerJoinEvent e) {
                Bukkit.getScheduler().runTaskAsynchronously(SkinEvent.getInstance(), () -> {
                    try {
                        if (Config.DISABLE_ONJOIN_SKINS) {
                            factory.applySkin(e.getPlayer(),
                                    SkinStorage.getSkinData(SkinStorage.getPlayerSkin(e.getPlayer().getName())));
                            return;
                        }
                        if (Config.DEFAULT_SKINS_ENABLED)
                            if (SkinStorage.getPlayerSkin(e.getPlayer().getName()) == null) {
                                List<String> skins = Config.DEFAULT_SKINS;
                                int randomNum = 0 + (int) (Math.random() * skins.size());
                                factory.applySkin(e.getPlayer(),
                                        SkinStorage.getOrCreateSkinForPlayer(skins.get(randomNum)));
                                return;
                            }
                        factory.applySkin(e.getPlayer(), SkinStorage.getOrCreateSkinForPlayer(e.getPlayer().getName()));
                    } catch (Exception ex) {
                    }
                });
            }
        }, this);
    }
}
