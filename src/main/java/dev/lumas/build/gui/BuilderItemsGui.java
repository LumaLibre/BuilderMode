package dev.lumas.build.gui;

import dev.lumas.build.model.SuspendedPlayerRegistry;
import dev.lumas.lumacore.manager.guis.AbstractGui;
import dev.lumas.lumacore.manager.guis.items.AbstractGuiItem;
import dev.lumas.lumacore.manager.guis.items.IndexedGuiItem;
import dev.lumas.lumacore.utility.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.type.Light;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class BuilderItemsGui extends AbstractGui {

    private static final int SIZE = 27;
    private static final int MAX_LIGHT_LEVEL = 15;

    private final Inventory inventory;

    public BuilderItemsGui() {
        this.inventory = Bukkit.createInventory(this, SIZE, Text.mm("<dark_gray>Builder Items"));

        addItem(IndexedGuiItem.of(0, new ItemStack(Material.BARRIER), this::give));
        addItem(IndexedGuiItem.of(1, new ItemStack(Material.STRUCTURE_VOID), this::give));
        addItem(IndexedGuiItem.of(2, new ItemStack(Material.DEBUG_STICK), this::give));

        for (int level = 0; level <= MAX_LIGHT_LEVEL; level++) {
            int index = (level < 8 ? 9 : 10) + level;
            addItem(IndexedGuiItem.of(index, lightBlock(level), this::give));
        }
    }

    private ItemStack lightBlock(int level) {
        ItemStack itemStack = new ItemStack(Material.LIGHT);
        ItemMeta meta = itemStack.getItemMeta();

        Light light = (Light) Material.LIGHT.createBlockData();
        light.setLevel(level);
        ((BlockDataMeta) meta).setBlockData(light);
        meta.displayName(Text.mmNoItalic("<white>Light <gray>(Level " + level + ")"));

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private void give(InventoryClickEvent event, AbstractGuiItem guiItem) {
        if (event.getClickedInventory() != this.inventory) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (!SuspendedPlayerRegistry.INSTANCE.isSuspended(player.getUniqueId())) {
            Text.msg(player, "You can only take builder items while suspended.");
            player.closeInventory();
            return;
        }

        Map<Integer, ItemStack> leftover = player.getInventory().addItem(guiItem.getItemStack().clone());
        if (!leftover.isEmpty()) {
            Text.msg(player, "Your inventory is full.");
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onInventoryClose(InventoryCloseEvent event) {
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
