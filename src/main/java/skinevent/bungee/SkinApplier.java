package skinevent.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.connection.LoginResult.Property;

import skinevent.shared.storage.SkinStorage;
import skinevent.shared.utils.ReflectionUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class SkinApplier {

    private static Class<?> LoginResult;

    public static void applySkin(final ProxiedPlayer p) {
        SkinEvent.getInstance().getExecutor().submit(new Runnable() {

            @Override
            public void run() {
                try {
                    Property textures = (Property) SkinStorage.getOrCreateSkinForPlayer(p.getName());

                    InitialHandler handler = (InitialHandler) p.getPendingConnection();

                    if (handler.isOnlineMode()) {
                        sendUpdateRequest(p, textures);
                        return;
                    }
                    LoginResult profile = null;

                    try {
                        // NEW BUNGEECORD
                        profile = (net.md_5.bungee.connection.LoginResult) ReflectionUtil.invokeConstructor(LoginResult,
                                new Class<?>[]{String.class, String.class, Property[].class},
                                p.getUniqueId().toString(), p.getName(), new Property[]{textures});
                    } catch (Exception e) {
                        // FALL BACK TO OLD
                        profile = (net.md_5.bungee.connection.LoginResult) ReflectionUtil.invokeConstructor(LoginResult,
                                new Class<?>[]{String.class, Property[].class}, p.getUniqueId().toString(),
                                new Property[]{textures});
                    }
                    Property[] present = profile.getProperties();
                    Property[] newprops = new Property[present.length + 1];
                    System.arraycopy(present, 0, newprops, 0, present.length);
                    newprops[present.length] = textures;
                    profile.getProperties()[0].setName(newprops[0].getName());
                    profile.getProperties()[0].setValue(newprops[0].getValue());
                    profile.getProperties()[0].setSignature(newprops[0].getSignature());
                    ReflectionUtil.setObject(InitialHandler.class, handler, "loginProfile", profile);

                    if (SkinEvent.getInstance().isMultiBungee())
                        sendUpdateRequest(p, textures);
                    else
                        sendUpdateRequest(p, null);
                } catch (Exception e) {

                }
            }
        });

    }

    public static void applySkin(final String pname) {
        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(pname);
        if (p != null)
            applySkin(p);
    }

    public static void init() {
        try {
            LoginResult = ReflectionUtil.getBungeeClass("connection", "LoginResult");
        } catch (Exception e) {

        }
    }

    private static void sendUpdateRequest(ProxiedPlayer p, Property textures) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("SkinUpdate");

            if (textures != null) {
                out.writeUTF(textures.getName());
                out.writeUTF(textures.getValue());
                out.writeUTF(textures.getSignature());
            }

            p.getServer().sendData("SkinEvent", b.toByteArray());
        } catch (Exception e) {
        }
    }

}