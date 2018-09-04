package skinevent.bungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import skinevent.bungee.commands.AdminCommands;
import skinevent.bungee.commands.PlayerCommands;
import skinevent.bungee.listeners.BukkitListener;
import skinevent.bungee.listeners.LoginListener;
import skinevent.shared.storage.Config;
import skinevent.shared.storage.Locale;
import skinevent.shared.storage.SkinStorage;
import skinevent.shared.utils.MySQL;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MINUTES;

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
        getProxy().getServers().values().stream().forEach((server) -> server.sendData(channel, stream.toByteArray()));
    }

    public void sendToConsole(String command) {
        getProxy().getPluginManager().dispatchCommand(getProxy().getConsole(), command);
    }

    @Override
    public void onEnable() {
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

        // check players for old skin
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        final Runnable task = new Runnable() {
            public void run() {
                for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                    String name = p.getName();


                    if(!SkinStorage.getPlayerSkin(name).equalsIgnoreCase(name)) {
                        if(SkinStorage.checkForOldSkin(p.getName())) {
                            SkinStorage.setPlayerSkin(name, name, false);
                            SkinApplier.applySkin(p);
                            p.sendMessage(new TextComponent(Locale.SKIN_CLEAR_SUCCESS));
                        }
                    }
                }
            }
        };
        scheduler.scheduleAtFixedRate(task, 1, 1, MINUTES);
    }
}
