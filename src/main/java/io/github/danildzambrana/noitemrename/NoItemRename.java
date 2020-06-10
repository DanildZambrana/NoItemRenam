package io.github.danildzambrana.noitemrename;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class NoItemRename extends JavaPlugin implements Listener {

    private List<Material> blockedMaterials = new ArrayList<>();

    @Override
    public void onEnable() {
        loadFiles();
        // Plugin startup logic

        if (!getConfig().contains("blocked-items") || getConfig().getStringList("blocked-items") == null) {
            getLogger().warning("No se han encontrado items para bloquear, desactivando plugin!");
            getPluginLoader().disablePlugin(this);
            return;
        }

        blockedMaterials = getConfig().getStringList("blocked-items").stream().map(s -> {
            Material material = null;
            try {
                material = Material.valueOf(s);
            } catch (IllegalArgumentException e) {
                getLogger().warning("No existe un item con el nombre '" + s + "', esquivando");
            }

            return material;
        }).collect(Collectors.toList());

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler(ignoreCancelled = true)
    public void anvilRenameEvent(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }

        if (event.getInventory().getType() == InventoryType.ANVIL) {
            if (blockedMaterials.contains(event.getCurrentItem().getType())) {
                if (event.getSlotType() == InventoryType.SlotType.RESULT) {
                    event.setCancelled(true);
                }
            }
        }
    }

    public void loadFiles() {
        File config = new File(this.getDataFolder(), "config.yml");
        if (!config.exists()) {
            this.getConfig().options().copyDefaults(true);
            this.getLogger().info(ChatColor.RED + "config.yml" + ChatColor.GRAY + "is not found, creating file...");
            saveConfig();
        }
        reloadConfig();
    }


}
