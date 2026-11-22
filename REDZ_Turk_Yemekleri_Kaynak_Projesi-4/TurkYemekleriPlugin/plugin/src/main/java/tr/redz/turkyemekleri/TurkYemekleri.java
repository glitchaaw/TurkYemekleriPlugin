package tr.redz.turkyemekleri;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.FoodProperties;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class TurkYemekleri extends JavaPlugin implements Listener, TabExecutor {
    private NamespacedKey foodKey;
    private final Map<String, Food> foods = new LinkedHashMap<>();

    private record Food(String id, String name, int nutrition, float saturation) {}

    @Override
    public void onEnable() {
        foodKey = new NamespacedKey(this, "food_id");
        registerFoods();
        Bukkit.getPluginManager().registerEvents(this, this);
        Objects.requireNonNull(getCommand("yemek")).setExecutor(this);
        Objects.requireNonNull(getCommand("yemek")).setTabCompleter(this);
        getLogger().info("TurkYemekleri etkin! " + foods.size() + " yemek yüklendi.");
    }

    private void registerFoods() {
        add("doner","Döner",6,0.8f); add("cig_kofte","Çiğ Köfte",5,0.7f);
        add("lahmacun","Lahmacun",7,0.8f); add("baklava","Baklava",5,0.9f);
        add("iskender","İskender",8,1.0f); add("manti","Mantı",6,0.8f);
        add("pide","Pide",7,0.8f); add("kunefe","Künefe",6,0.9f);
        add("gozleme","Gözleme",6,0.7f); add("menemen","Menemen",5,0.6f);
        add("simit","Simit",4,0.5f); add("sucuklu_yumurta","Sucuklu Yumurta",7,0.8f);
        add("kokorec","Kokoreç",7,0.8f); add("tantuni","Tantuni",7,0.8f);
        add("kofte","Köfte",6,0.7f); add("adana_kebap","Adana Kebap",8,0.9f);
        add("yaprak_sarma","Yaprak Sarma",5,0.7f); add("dolma","Biber Dolması",5,0.7f);
        add("borek","Börek",6,0.7f); add("ayran","Ayran",3,0.4f);
        add("turk_kahvesi","Türk Kahvesi",2,0.2f); add("sutlac","Sütlaç",4,0.6f);
        add("lokum","Lokum",3,0.4f); add("mercimek_corbasi","Mercimek Çorbası",5,0.6f);
        add("kumpir","Kumpir",8,0.9f);
    }

    private void add(String id, String name, int nutrition, float saturation) {
        foods.put(id, new Food(id, name, nutrition, saturation));
    }

    private ItemStack createFood(Food food) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(food.name(), NamedTextColor.GOLD));
        meta.lore(List.of(Component.text("Türk Mutfağı", NamedTextColor.GRAY),
                Component.text("Yemek için sağ tıkla", NamedTextColor.YELLOW)));
        meta.setItemModel(new NamespacedKey("turkfood", food.id()));
        meta.getPersistentDataContainer().set(foodKey, PersistentDataType.STRING, food.id());
        item.setItemMeta(meta);
        item.setData(DataComponentTypes.FOOD, FoodProperties.food()
                .nutrition(food.nutrition()).saturation(food.saturation()).build());
        item.setData(DataComponentTypes.CONSUMABLE,
                io.papermc.paper.datacomponent.item.Consumable.consumable().build());
        return item;
    }

    private void openMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, Component.text("🇹🇷 Türk Mutfağı", NamedTextColor.GOLD));
        int slot = 0;
        for (Food food : foods.values()) {
            if (slot >= inv.getSize()) break;
            inv.setItem(slot++, createFood(food));
        }
        player.openInventory(inv);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Konsoldan kullanım: /yemek ver <oyuncu> <yemek> [adet]");
                return true;
            }
            openMenu(player);
            return true;
        }
        if (args[0].equalsIgnoreCase("ver")) {
            if (!sender.hasPermission("turkyemek.admin")) {
                sender.sendMessage("§cBu komut için yetkin yok.");
                return true;
            }
            if (args.length < 3) {
                sender.sendMessage("§eKullanım: /yemek ver <oyuncu> <yemek> [adet]");
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[1]);
            Food food = foods.get(args[2].toLowerCase(Locale.ROOT));
            if (target == null || food == null) {
                sender.sendMessage("§cOyuncu bulunamadı veya yemek ID'si hatalı. /yemek liste");
                return true;
            }
            int amount = 1;
            if (args.length >= 4) {
                try { amount = Math.max(1, Math.min(64, Integer.parseInt(args[3]))); }
                catch (NumberFormatException ex) { sender.sendMessage("§cAdet sayı olmalı."); return true; }
            }
            ItemStack stack = createFood(food);
            stack.setAmount(amount);
            Map<Integer, ItemStack> leftovers = target.getInventory().addItem(stack);
            leftovers.values().forEach(item -> target.getWorld().dropItemNaturally(target.getLocation(), item));
            sender.sendMessage("§a" + target.getName() + " oyuncusuna " + amount + "x " + food.name() + " verildi.");
            return true;
        }
        if (args[0].equalsIgnoreCase("liste")) {
            sender.sendMessage("§6Yemek ID'leri: §e" + String.join(", ", foods.keySet()));
            return true;
        }
        sender.sendMessage("§eKullanım: /yemek | /yemek liste | /yemek ver <oyuncu> <id> [adet]");
        return true;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getView().title().equals(Component.text("🇹🇷 Türk Mutfağı", NamedTextColor.GOLD))) {
            event.setCancelled(true);
            if (event.getRawSlot() < 0 || event.getRawSlot() >= event.getView().getTopInventory().getSize()) return;
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta()) return;
            String id = clicked.getItemMeta().getPersistentDataContainer().get(foodKey, PersistentDataType.STRING);
            Food food = id == null ? null : foods.get(id);
            if (food != null) {
                Map<Integer, ItemStack> leftovers = player.getInventory().addItem(createFood(food));
                leftovers.values().forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
                player.sendMessage(Component.text(food.name() + " envanterine eklendi!", NamedTextColor.GREEN));
            }
        }
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return List.of("liste", "ver");
        if (args.length == 2 && args[0].equalsIgnoreCase("ver"))
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        if (args.length == 3 && args[0].equalsIgnoreCase("ver"))
            return foods.keySet().stream().toList();
        return Collections.emptyList();
    }
}
