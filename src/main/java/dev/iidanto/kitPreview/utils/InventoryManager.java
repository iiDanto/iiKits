package dev.iidanto.kitPreview.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public final class InventoryManager {

    private static final AtomicBoolean REGISTERED = new AtomicBoolean(false);

    private InventoryManager() {
        throw new UnsupportedOperationException();
    }

    public static void register(Plugin plugin) {
        Objects.requireNonNull(plugin, "plugin");

        if (REGISTERED.getAndSet(true)) {
            throw new IllegalStateException("FastInv is already registered");
        }

        Bukkit.getPluginManager().registerEvents(new InventoryListener(plugin), plugin);
    }

    public static void closeAll() {
        Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.getOpenInventory().getTopInventory().getHolder() instanceof InventoryBuilder)
                .forEach(Player::closeInventory);
    }

    public static final class InventoryListener implements Listener {

        private final Plugin plugin;

        public InventoryListener(Plugin plugin) {
            this.plugin = plugin;
        }

        @EventHandler
        public void onInventoryDrag(InventoryDragEvent e) {
            if (e.getInventory().getHolder() instanceof InventoryBuilder inv) {
                if (inv.isEditor()) {
                    e.setCancelled(true);
                }
            }
        }

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e) {
            if (e.getInventory().getHolder() instanceof InventoryBuilder inv) {
                if(e.getClickedInventory() == null) {
                    if (inv.isEditor()) e.setCancelled(true);
                }
                if (e.getClickedInventory() != null) {
                    if (inv.isEditor()) {
                        if (e.getClickedInventory() != inv.getInventory()) {
                            e.setCancelled(true);
                            return;
                        }
                        if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY ||
                                e.getAction() == InventoryAction.HOTBAR_SWAP ||
                                e.getAction() == InventoryAction.DROP_ALL_CURSOR ||
                                e.getAction() == InventoryAction.DROP_ALL_SLOT ||
                                e.getAction() == InventoryAction.DROP_ONE_SLOT ||
                                e.getAction() == InventoryAction.DROP_ONE_CURSOR ||
                                e.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
                            e.setCancelled(true);
                        }
                    }
                    if (inv.isSafe()) {
                        e.setCancelled(true);
                    }
                    if (e.getInventory().contains(e.getCurrentItem())) {
                        inv.handleClick(e);
                    }
                }
            }
        }

        @EventHandler
        public void onInventoryOpen(InventoryOpenEvent e) {
            if (e.getInventory().getHolder() instanceof InventoryBuilder inv) {
                inv.handleOpen(e);
            }
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e) {
            if (e.getInventory().getHolder() instanceof InventoryBuilder inv) {
                if(!e.getPlayer().getItemOnCursor().isEmpty()){
                    inv.addItem(e.getPlayer().getItemOnCursor());
                    e.getPlayer().getItemOnCursor().setAmount(0);
                }
                if (inv.handleClose(e)) {
                    Bukkit.getScheduler().runTask(this.plugin, () -> inv.open((Player) e.getPlayer()));
                }
            }
        }

        @EventHandler
        public void onPluginDisable(PluginDisableEvent e) {
            if (e.getPlugin() == this.plugin) {
                closeAll();

                REGISTERED.set(false);
            }
        }
    }
}