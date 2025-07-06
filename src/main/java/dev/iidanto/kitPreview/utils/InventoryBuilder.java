package dev.iidanto.kitPreview.utils;

import dev.iidanto.kitPreview.KitPreview;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class InventoryBuilder implements InventoryHolder {

    private final Map<Integer, Consumer<InventoryClickEvent>> itemHandlers = new HashMap<>();
    private final List<Consumer<InventoryOpenEvent>> openHandlers = new ArrayList<>();
    private final List<Consumer<InventoryCloseEvent>> closeHandlers = new ArrayList<>();
    private final List<Consumer<InventoryClickEvent>> clickHandlers = new ArrayList<>();
    private final Map<Integer, Integer> multiItemIndices = new HashMap<>();
    private boolean viewOnly = false;

    private final org.bukkit.inventory.Inventory inventory;
    private boolean isSafe = false;
    private boolean isEditor = false;

    private Predicate<Player> closeFilter;

    public void setSafe(boolean safe) {
        isSafe = safe;
    }

    public void setEditor(boolean editor) {
        isEditor = editor;
    }


    public void setViewOnly(boolean viewOnly) {
        this.viewOnly = viewOnly;
    }

    public void setCloseFilter(Predicate<Player> closeFilter) {
        this.closeFilter = closeFilter;
    }

    public InventoryBuilder(int size) {
        this(owner -> Bukkit.createInventory(owner, size));
    }

    public InventoryBuilder(int size, Component title, boolean safe) {
        this(owner -> Bukkit.createInventory(owner, size, title));
        isSafe = safe;
    }

    public InventoryBuilder(int size, String title, boolean safe) {
        this(owner -> Bukkit.createInventory(owner, size, ColorUtils.parse(title)));
        isSafe = safe;
    }

    public InventoryBuilder(int size, Component title) {
        this(owner -> Bukkit.createInventory(owner, size, title));
    }

    public InventoryBuilder(int size, String title) {
        this(owner -> Bukkit.createInventory(owner, size, ColorUtils.parse(title)));
    }

    public InventoryBuilder(String title, InventoryType type){
        this(owner -> Bukkit.createInventory(owner, type));
    }

    public InventoryBuilder(InventoryType type) {
        this(owner -> Bukkit.createInventory(owner, type));
    }

    public InventoryBuilder(InventoryType type, Component title) {
        this(owner -> Bukkit.createInventory(owner, type, title));
    }

    public InventoryBuilder(Function<InventoryHolder, org.bukkit.inventory.Inventory> inventoryFunction) {
        Objects.requireNonNull(inventoryFunction, "inventoryFunction");
        org.bukkit.inventory.Inventory inv = inventoryFunction.apply(this);

        if (inv.getHolder() != this) {
            throw new IllegalStateException("Inventory holder is not FastInv, found: " + inv.getHolder());
        }

        this.inventory = inv;
    }

    protected void onOpen(InventoryOpenEvent event) {
    }

    protected void onClick(InventoryClickEvent event) {
    }

    protected void onClose(InventoryCloseEvent event) {
    }

    public void addItem(ItemStack item) {
        addItem(item, null);
    }

    public void addItem(ItemStack item, Consumer<InventoryClickEvent> handler) {
        int slot = this.inventory.firstEmpty();
        if (slot >= 0) {
            setItem(slot, item, handler);
        }
    }

    public void setAnimatedItem(int slot, ItemStack[] stacks, int delay, Consumer<InventoryClickEvent> handler){
        new BukkitRunnable(){
            int frame = 0;
            @Override
            public void run() {
                setItem(slot, stacks[frame % stacks.length], handler);
                frame++;
            }
        }.runTaskTimerAsynchronously(KitPreview.getInstance(), 0, delay);
    }

    public void setItem(int slot, ItemStack item) {
        setItem(slot, item, null);
    }

    public void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
        this.inventory.setItem(slot, item);

        if (handler != null) {
            this.itemHandlers.put(slot, handler);
        } else {
            this.itemHandlers.remove(slot);
        }
    }

    public void setItems(int slotFrom, int slotTo, ItemStack item) {
        setItems(slotFrom, slotTo, item, null);
    }

    public void setItems(int slotFrom, int slotTo, ItemStack item, Consumer<InventoryClickEvent> handler) {
        for (int i = slotFrom; i <= slotTo; i++) {
            setItem(i, item, handler);
        }
    }

    public void setItems(int[] slots, ItemStack item) {
        setItems(slots, item, null);
    }

    public void setItems(int[] slots, ItemStack item, Consumer<InventoryClickEvent> handler) {
        for (int slot : slots) {
            setItem(slot, item, handler);
        }
    }

    public void setMultiItem(int slot, List<ItemStack> stacks, Consumer<InventoryClickEvent> handler) {
        if (stacks == null || stacks.isEmpty()) {
            throw new IllegalArgumentException("Die Liste der Items darf nicht null oder leer sein.");
        }
        multiItemIndices.putIfAbsent(slot, 0);
        int currentIndex = multiItemIndices.get(slot);
        setItem(slot, stacks.get(currentIndex), event -> {
            if(event.getClick().isRightClick()){
                int idx = multiItemIndices.get(slot);
                idx = (idx - 1 + stacks.size()) % stacks.size();
                multiItemIndices.put(slot, idx);
                setItem(slot, stacks.get(idx), this.itemHandlers.get(slot));
                if (handler != null) {
                    handler.accept(event);
                }
                return;
            }
            int idx = multiItemIndices.get(slot);
            idx = (idx + 1) % stacks.size();
            multiItemIndices.put(slot, idx);
            setItem(slot, stacks.get(idx), this.itemHandlers.get(slot));
            if (handler != null) {
                handler.accept(event);
            }
        });
    }

    /**
     *
     * @param slot Inventory Slot
     * @param trueItem Item that will display when swapItemBool is true
     * @param falseItem Item that will display when swapItemBool is false
     * @param swapItemBool If true, trueItem will be displayed, otherwise falseItem will be displayed
     * @param action Action to be performed when item is clicked (only if swapItemBool is false)
     */

    public void setSwapDisplay(int slot, ItemStack trueItem, ItemStack falseItem, boolean swapItemBool, Consumer<InventoryClickEvent> action) {
        Consumer<InventoryClickEvent> handler = event -> {
            event.setCancelled(true);
            action.accept(event);
        };
        if (swapItemBool) {
            setItem(slot, trueItem, event -> {
                event.setCancelled(true);
            });
        } else {
            setItem(slot, falseItem, event -> {
                setItem(slot, trueItem, handler);
                handler.accept(event);
            });
        }
    }


    public void setSwapItem(int slot, ItemStack trueItem, ItemStack falseItem, boolean swapItemBool, Consumer<InventoryClickEvent> trueHandler, Consumer<InventoryClickEvent> falseHandler) {
        Consumer<InventoryClickEvent> handler = event -> {
            event.setCancelled(true);
            setSwapItem(slot, trueItem, falseItem, !swapItemBool, trueHandler, falseHandler);
        };

        if (swapItemBool) {
            setItem(slot, trueItem, event -> {
                handler.accept(event);
                falseHandler.accept(event);
            });
        } else {
            setItem(slot, falseItem, event -> {
                handler.accept(event);
                trueHandler.accept(event);
            });
        }
    }

    public void setPlaceholders(int... slot){
        for(int x : slot){
            setItem(x, ItemBuilder.item(Material.GRAY_STAINED_GLASS_PANE).tooltip(false).build(),
                    event -> event.setCancelled(true));
        }
    }

    public void setPlaceholder(int slot, Material item){
        setItem(slot, ItemBuilder.item(item).tooltip(false).build(),
                event -> event.setCancelled(true));
    }

    public void setPlaceholder(int slot){
        setItem(slot, ItemBuilder.item(Material.GRAY_STAINED_GLASS_PANE).tooltip(false).build(),
                event -> event.setCancelled(true));
    }

    public void removeItem(int slot) {
        this.inventory.clear(slot);
        this.itemHandlers.remove(slot);
    }

    public void removeItems(int... slots) {
        for (int slot : slots) {
            removeItem(slot);
        }
    }

    public InventoryBuilder fill(@NotNull ItemStack filler, @NotNull String... pattern) {
        int row = 0;
        int size = inventory.getSize();

        for (String rowPattern : pattern) {
            if (row < size / 9) {
                String[] rowCharacters = rowPattern.split(" ");

                for (int col = 0; col < rowCharacters.length && col < 9; col++) {
                    String character = rowCharacters[col];
                    int slot = col + row * 9;

                    if (character.equals("X")) {
                        setItem(slot, filler, event -> event.setCancelled(true));
                    }
                }

                row++;
            }
        }
        return this;
    }

    public void fill(List<AbstractItem> stacks, String... pattern) {
        int row = 0;
        int size = inventory.getSize();
        int itemIndex = 0;

        for (String rowPattern : pattern) {
            if (row < size / 9) {
                String[] rowCharacters = rowPattern.split(" ");

                for (int col = 0; col < rowCharacters.length && col < 9; col++) {
                    String character = rowCharacters[col];
                    int slot = col + row * 9;

                    if (character.equals("X") && itemIndex < stacks.size()) {
                        AbstractItem item = stacks.get(itemIndex);
                        if (item == null) continue;
                        setItem(slot, item.getStack(), item.getHandler());
                        itemIndex++;
                    }
                }
                row++;
            }
        }
    }




    public void setSafety(boolean b){
        isSafe = b;
    }

    public void addOpenHandler(Consumer<InventoryOpenEvent> openHandler) {
        this.openHandlers.add(openHandler);
    }

    public void addCloseHandler(Consumer<InventoryCloseEvent> closeHandler) {
        this.closeHandlers.add(closeHandler);
    }

    public void addClickHandler(Consumer<InventoryClickEvent> clickHandler) {
        this.clickHandlers.add(clickHandler);
    }

    public void open(Player player) {
        Bukkit.getScheduler().runTask(KitPreview.getInstance(), () -> {
            player.openInventory(this.inventory);
            player.playSound(player, Sound.ENTITY_CHICKEN_EGG, 1.0f, 5.0f);
        });
    }

    public int[] getBorders() {
        int size = this.inventory.getSize();
        return IntStream.range(0, size).filter(i -> size < 27 || i < 9
                || i % 9 == 0 || (i - 8) % 9 == 0 || i > size - 9).toArray();
    }

    public int[] getCorners() {
        int size = this.inventory.getSize();
        return IntStream.range(0, size).filter(i -> i < 2 || (i > 6 && i < 10)
                || i == 17 || i == size - 18
                || (i > size - 11 && i < size - 7) || i > size - 3).toArray();
    }

    @Override
    public @NotNull org.bukkit.inventory.Inventory getInventory() {
        return this.inventory;
    }

    public ItemStack getItem(int x){
        if(getInventory().getItem(x) == null){
            return new ItemStack(Material.AIR);
        }
        return getInventory().getItem(x);
    }
    void handleOpen(InventoryOpenEvent e) {
        onOpen(e);

        this.openHandlers.forEach(c -> c.accept(e));
    }

    boolean handleClose(InventoryCloseEvent e) {
        onClose(e);

        this.closeHandlers.forEach(c -> c.accept(e));

        return this.closeFilter != null && this.closeFilter.test((Player) e.getPlayer());
    }

    boolean isSafe(){
        return isSafe;
    }
    boolean isEditor() {
        return isEditor;
    }

    void handleClick(InventoryClickEvent e) {
        if (viewOnly) {
            e.setCancelled(true);
            return;
        }

        onClick(e);
        this.clickHandlers.forEach(c -> c.accept(e));

        Consumer<InventoryClickEvent> clickConsumer = this.itemHandlers.get(e.getRawSlot());

        if (clickConsumer != null) {
            clickConsumer.accept(e);
        }
    }
}