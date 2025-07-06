package dev.iidanto.kitPreview.utils;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class AbstractItem {

    private final ItemStack stack;
    private final Consumer<InventoryClickEvent> handler;

    public AbstractItem(ItemStack stack, Consumer<InventoryClickEvent> handler) {
        this.stack = stack;
        this.handler = handler;
    }

    public ItemStack getStack() {
        return stack;
    }

    public Consumer<InventoryClickEvent> getHandler() {
        return handler;
    }
}