package skinevent.bungee;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
//import org.bstats.bungeecord.MetricsLite;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import skinevent.bungee.commands.AdminCommands;
import skinevent.bungee.commands.PlayerCommands;
import skinevent.bungee.listeners.BukkitListener;
import skinevent.bungee.listeners.LoginListener;
import skinevent.shared.storage.Config;
import skinevent.shared.storage.Locale;
import skinevent.shared.storage.SkinStorage;
import skinevent.shared.utils.MojangAPI;
import skinevent.shared.utils.MojangAPI.SkinRequestException;
import skinevent.shared.utils.MySQL;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SkinEvent extends Plugin {

    private static SkinEvent instance;
    private MySQL mysql;
    private boolean multibungee;
    private ExecutorService exe;
    private boolean outdated;

    public static SkinEvent getInstance() {
        return instance;
    }

    public String checkVersion(CommandSender console) {
        try {
            HttpsURLConnection con = (HttpsURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=2124")
                    .openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("GET");
            String version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
            if (version.length() <= 13)
                return version;
        } catch (Exception ex) {
            ex.printStackTrace();
            console.sendMessage(new TextComponent("§e[§2SkinEvent§e] §cFailed to check for an update on Spigot."));
        }
        return getVersion();
    }

    public ExecutorService getExecutor() {
        return exe;
    }

    public MySQL getMySQL() {
        return mysql;
    }

    public String getVersion() {
        return getDescription().getVersion();
    }

    public boolean isMultiBungee() {
        return multibungee;
    }

    public boolean isOutdated() {
        return outdated;
    }

    public void sendToServer(String channel, String message, ServerInfo server) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(stream);

        try {
            output.writeUTF(channel);
            output.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.sendData(channel, stream.toByteArray());
    }

    public void sendToServer(String channel, String message) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(stream);

        try {
            output.writeUTF(channel);
            output.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        getProxy().getServers().values().stream().forEach((server) -> {
            server.sendData(channel, stream.toByteArray());
        });
    }

    public void sendToConsole(String command) {
        getProxy().getPluginManager().dispatchCommand(getProxy().getConsole(), command);
    }

    @Override
    public void onEnable() {

        //@SuppressWarnings("unused")
        //MetricsLite metrics = new MetricsLite(this);

        instance = this;
        Config.load(getResourceAsStream("config.yml"));
        Locale.load();
        exe = Executors.newCachedThreadPool();

        if (Config.USE_MYSQL)
            SkinStorage.init(mysql = new MySQL(Config.MYSQL_HOST, Config.MYSQL_PORT, Config.MYSQL_DATABASE,
                    Config.MYSQL_USERNAME, Config.MYSQL_PASSWORD));
        else
            SkinStorage.init(getDataFolder());

        getProxy().getPluginManager().registerListener(this, new LoginListener());
        getProxy().getPluginManager().registerCommand(this, new AdminCommands());
        getProxy().getPluginManager().registerCommand(this, new PlayerCommands(this));
        getProxy().registerChannel("SkinEvent");
        getProxy().registerChannel("startSkinEvent");
        getProxy().registerChannel("skinEventVoteYes");
        getProxy().registerChannel("skinEventVoteNo");
        getProxy().registerChannel("setPlayerSkin");

        getProxy().getPluginManager().registerListener(this, new BukkitListener(this));

        multibungee = Config.MULTIBUNGEE_ENABLED
                || ProxyServer.getInstance().getPluginManager().getPlugin("RedisBungee") != null;


        CommandSender console = getProxy().getConsole();
        console.sendMessage(new TextComponent(""));
        console.sendMessage(new TextComponent("§e[§2SkinEvent§e] §a   +==================+"));
        console.sendMessage(new TextComponent("§e[§2SkinEvent§e] §a   |  SkinEvent v"+getVersion()+"  |"));
        console.sendMessage(new TextComponent("§e[§2SkinEvent§e] §a   +==================+"));
        console.sendMessage(new TextComponent(""));

        /*exe.submit(new Runnable() {

            @Override
            public void run() {

                CommandSender console = getProxy().getConsole();

                if (Config.UPDATER_ENABLED) {
                    if (checkVersion(console).equals(getVersion())) {
                        outdated = false;
                        console.sendMessage(new TextComponent("§e[§2SkinEvent§e] §a----------------------------------------------"));
                        console.sendMessage(new TextComponent("§e[§2SkinEvent§e] §a    +================+"));
                        console.sendMessage(new TextComponent("§e[§2SkinEvent§e] §a    |   SkinEvent   |"));
                        console.sendMessage(new TextComponent("§e[§2SkinEvent§e] §a    +================+"));
                        console.sendMessage(new TextComponent("§e[§2SkinEvent§e] §a----------------------------------------------"));
                        console.sendMessage(new TextComponent("§e[§2SkinEvent§e] §b    Current version: §a" + getVersion()));
                        console.sendMessage(new TextComponent("§e[§2SkinEvent§e] §a    This is the latest version!"));
                        console.sendMessage(new TextComponent("§e[§2SkinEvent§e] §a----------------------------------------------"));
                    } else {
                        outdated = true;
                        console.sendMessage(new TextComponent("§e[§2SkinEvent§e] §a----------------------------------------------"));
                        console.sendMessage(new TextComponent("§e[§2SkinEvent§e] §a    +================+"));
                        console.sendMessage(new TextComponent("§e[§2SkinEvent§e] §a    |   SkinEvent   |"));
                        console.sendMessage(new TextComponent("§e[§2SkinEvent§e] §a    +================+"));
                        console.sendMessage(new TextComponent("§e[§2SkinEvent§e] §a----------------------------------------------"));
                        console.sendMessage(new TextComponent("§e[§2SkinEvent§e] §b    Current version: §c" + getVersion()));
                        console.sendMessage(new TextComponent("§e[§2SkinEvent§e] §e    A new version is available! Download it at:"));
                        console.sendMessage(new TextComponent("§e[§2SkinEvent§e] §e    https://www.spigotmc.org/resources/skinevent.2124"));
                        console.sendMessage(new TextComponent("§e[§2SkinEvent§e] §a----------------------------------------------"));
                    }
                }

                if (Config.DEFAULT_SKINS_ENABLED)
                    for (String skin : Config.DEFAULT_SKINS)
                        try {
                            SkinStorage.setSkinData(skin, MojangAPI.getSkinProperty(MojangAPI.getUUID(skin)));
                        } catch (SkinRequestException e) {
                            if (SkinStorage.getSkinData(skin) == null)
                                console.sendMessage(new TextComponent("§e[§2SkinEvent§e] §cDefault Skin '" + skin + "' request error:" + e.getReason()));
                        }
            }

        });*/
    }
}
