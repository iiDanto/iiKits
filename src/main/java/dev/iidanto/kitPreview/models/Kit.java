package dev.iidanto.kitPreview.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Getter @Setter
public class Kit {
    private UUID owner;
    private int ID;
    private Map<Integer, ItemStack> content;
}