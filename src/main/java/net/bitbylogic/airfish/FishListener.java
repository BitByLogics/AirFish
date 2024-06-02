package net.bitbylogic.airfish;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Fish;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Squid;
import org.bukkit.entity.WaterMob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class FishListener implements Listener {

    private final AirFish plugin;

    public FishListener(AirFish plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFishDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Fish) && !(event.getEntity() instanceof Squid)) {
            return;
        }

        if (event.getCause() != EntityDamageEvent.DamageCause.DROWNING) {
            return;
        }

        if (!((WaterMob) event.getEntity()).hasPotionEffect(PotionEffectType.WATER_BREATHING)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onLeash(PlayerInteractAtEntityEvent event) {
        if (!plugin.getConfig().getBoolean("Settings.Fish-Walking", true)) {
            return;
        }

        if (!(event.getRightClicked() instanceof Fish)) {
            return;
        }

        PlayerInventory inventory = event.getPlayer().getInventory();
        ItemStack heldItem = inventory.getItem(event.getHand());
        LivingEntity entity = (LivingEntity) event.getRightClicked();

        event.setCancelled(true);

        if (entity.isLeashed()) {
            return;
        }

        if (heldItem == null || heldItem.getType() != Material.LEAD) {
            return;
        }

        heldItem.setAmount(heldItem.getAmount() - 1);
        Bukkit.getScheduler().runTask(plugin, () -> entity.setLeashHolder(event.getPlayer()));
    }

    @EventHandler
    public void onUnleash(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof Fish)) {
            return;
        }

        LivingEntity entity = (LivingEntity) event.getRightClicked();

        if (!entity.isLeashed()) {
            return;
        }

        event.setCancelled(true);

        if (!entity.getLeashHolder().getUniqueId().equals(event.getPlayer().getUniqueId())) {
            return;
        }

        entity.getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(Material.LEAD));
        entity.setLeashHolder(null);
    }

    @EventHandler
    public void onPotionFeed(PlayerInteractAtEntityEvent event) {
        if (!plugin.getConfig().getBoolean("Settings.Edible-Infinite-Breathing", true)) {
            return;
        }

        if (!(event.getRightClicked() instanceof Fish) && !(event.getRightClicked() instanceof Squid)) {
            return;
        }

        PlayerInventory inventory = event.getPlayer().getInventory();
        ItemStack heldItem = inventory.getItem(event.getHand());
        LivingEntity entity = (LivingEntity) event.getRightClicked();

        if (heldItem == null || (heldItem.getType() != Material.POTION
                && heldItem.getType() != Material.SPLASH_POTION
                && heldItem.getType() != Material.LINGERING_POTION)) {
            return;
        }

        PotionMeta potionMeta = (PotionMeta) heldItem.getItemMeta();

        if (potionMeta == null || potionMeta.getBasePotionType() != PotionType.WATER_BREATHING
                && potionMeta.getBasePotionType() != PotionType.LONG_WATER_BREATHING) {
            return;
        }

        event.setCancelled(true);

        heldItem.setType(Material.GLASS_BOTTLE);
        event.getPlayer().getWorld().playSound(event.getRightClicked().getLocation(), Sound.ENTITY_GENERIC_DRINK, 1f, 1f);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, -1, 1));

        if (!plugin.getConfig().getBoolean("Settings.Fish-Glow", true)) {
            return;
        }

        entity.setGlowing(true);
    }

    @EventHandler
    public void onMilkCleanse(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof Fish) && !(event.getRightClicked() instanceof Squid)) {
            return;
        }

        PlayerInventory inventory = event.getPlayer().getInventory();
        ItemStack heldItem = inventory.getItem(event.getHand());
        LivingEntity entity = (LivingEntity) event.getRightClicked();

        if (heldItem == null || heldItem.getType() != Material.MILK_BUCKET) {
            return;
        }

        if (!entity.hasPotionEffect(PotionEffectType.WATER_BREATHING)) {
            return;
        }

        event.setCancelled(true);

        heldItem.setType(Material.BUCKET);
        entity.removePotionEffect(PotionEffectType.WATER_BREATHING);
        event.getPlayer().getWorld().playSound(event.getRightClicked().getLocation(), Sound.ENTITY_GENERIC_DRINK, 1f, 1f);
    }

    @EventHandler
    public void onPotionApplied(EntityPotionEffectEvent event) {
        if (!plugin.getConfig().getBoolean("Settings.Fish-Glow", true)) {
            return;
        }

        if (!(event.getEntity() instanceof Fish) && !(event.getEntity() instanceof Squid)) {
            return;
        }

        if (event.getModifiedType() != PotionEffectType.WATER_BREATHING) {
            return;
        }

        switch (event.getAction()) {
            case ADDED:
                if (event.getEntity().isGlowing()) {
                    break;
                }

                event.getEntity().setGlowing(true);
                break;
            case CLEARED:
            case REMOVED:
                if (!event.getEntity().isGlowing()) {
                    break;
                }

                event.getEntity().setGlowing(false);
                break;
            default:
                break;
        }
    }

}
