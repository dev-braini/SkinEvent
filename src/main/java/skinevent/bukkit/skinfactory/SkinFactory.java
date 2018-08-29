package skinevent.bukkit.skinfactory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import skinevent.bukkit.SkinEvent;
import skinevent.shared.utils.ReflectionUtil;

public abstract class SkinFactory {

    /**
     * Applies the skin In other words, sets the skin data, but no changes will
     * be visible until you reconnect or force update with
     *
     * @param p     - Player
     * @param props - Property Object
     * @see updateSkin
     */
    public void applySkin(final Player p, Object props) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(SkinEvent.getInstance(), () -> {
            try {
                if (props == null)
                    return;

                Object ep = ReflectionUtil.invokeMethod(p.getClass(), p, "getHandle");
                Object profile = ReflectionUtil.invokeMethod(ep.getClass(), ep, "getProfile");
                Object propmap = ReflectionUtil.invokeMethod(profile.getClass(), profile, "getProperties");
                ReflectionUtil.invokeMethod(propmap, "clear");
                ReflectionUtil.invokeMethod(propmap.getClass(), propmap, "put", new Class[]{Object.class, Object.class},
                        new Object[]{"textures", props});
                updateSkin(p);
            } catch (Exception e) {
            }
        });
    }

    /**
     * Instantly updates player's skin
     *
     * @param p - Player
     */
    public abstract void updateSkin(Player p);

}
