package me.lynes.kese.cmds;

import me.lynes.kese.Kese;
import me.lynes.kese.utils.Utils;
import me.lynes.kese.vault.KeseVaultEconomy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class KeseCmd implements CommandExecutor, TabCompleter {
    private final Kese plugin = Kese.getInstance();
    private final KeseVaultEconomy economy = plugin.getEconomy();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Bukkit.getLogger().log(Level.INFO, "Bu komudu sadece oyuncular kullanabilir.");
            return true;
        }


        Player player = (Player) sender;

        if (args.length > 0 && args[0].equalsIgnoreCase("koy")) {
            if (args.length == 2) {
                double amount;
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    //player.sendMessage("§cMiktar sayı olmalıdır.");
                    Utils.msgPlayer(player,Utils.getConf("messages.must-be-number"));
                    return true;
                }

                if (amount < 0) {
                    //player.sendMessage("§cMiktar sayı olmalıdır.");
                    Utils.msgPlayer(player,Utils.getConf("messages.must-be-number"));
                    return true;
                }

                String formatted = economy.format(amount);
                int z = 0;
                HashMap<Integer, ItemStack> hm = player.getInventory().removeItem(new ItemStack(Material.GOLD_INGOT, (int) amount));
                if (hm.isEmpty()) {
                    if (!economy.depositPlayer(player, amount).transactionSuccess()) {
                        //player.sendMessage("§cBir hata oluştu, işlem gerçekleştirilemiyor.");
                        Utils.msgPlayer(player,Utils.getConf("messages.an-error-occured"));
                        return true;
                    }

                    //player.sendMessage("§6§lKese §fenvanterinizdeki " + formatted + " altın keseye koyuldu.");
                    Utils.msgPlayer(player,Utils.getConf("messages.put-to-pouch").replace("%formatted%",formatted));
                    //player.sendMessage("Yeni altın miktarı §6" + economy.format(economy.getBalance(player)) + " §6Altın");
                    Utils.msgPlayer(player,Utils.getConf("messages.new-balance").replace("%balance%",economy.format(economy.getBalance(player))));
                    //player.sendMessage("§a§l+ §a" + formatted);
                    Utils.msgPlayer(player,Utils.getConf("messages.increase").replace("%formatted%",formatted));


                    return true;
                } else {
                    for (Map.Entry<Integer, ItemStack> entry : hm.entrySet()) {
                        ItemStack value = entry.getValue();
                        z += value.getAmount();
                    }

                    if (!economy.depositPlayer(player, amount - z).transactionSuccess()) {
                        //player.sendMessage("§cBir hata oluştu, işlem gerçekleştirilemiyor.");
                        Utils.msgPlayer(player,Utils.getConf("messages.an-error-occured"));
                        return true;
                    }

                    formatted = economy.format(amount - z);
                    //player.sendMessage("§6§lKese §fenvanterinizdeki " + formatted + " altın keseye koyuldu.");
                    Utils.msgPlayer(player,Utils.getConf("messages.put-to-pouch").replace("%formatted%",formatted));
                    //player.sendMessage("Yeni altın miktarı §6" + economy.format(economy.getBalance(player)) + " §6Altın");
                    Utils.msgPlayer(player,Utils.getConf("messages.new-balance").replace("%balance%",economy.format(economy.getBalance(player))));
                    //player.sendMessage("§a§l+ §a" + formatted);
                    Utils.msgPlayer(player,Utils.getConf("messages.increase").replace("%formatted%",formatted));

                    return true;
                }

            } else {
                //player.sendMessage("§cMiktar sayı olmalıdır.");
                Utils.msgPlayer(player,Utils.getConf("messages.must-be-number"));
                return true;
            }
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("al")) {
            if (args.length == 2) {
                double amount;
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    //player.sendMessage("§cMiktar sayı olmalıdır.");
                    Utils.msgPlayer(player,Utils.getConf("messages.must-be-number"));
                    return true;
                }

                if (amount < 0) {
                    //player.sendMessage("§cMiktar sayı olmalıdır.");
                    Utils.msgPlayer(player,Utils.getConf("messages.must-be-number"));
                    return true;
                }

                String formatted = economy.format(amount);

                if (economy.has(player, amount)) {
                    double bal = economy.getBalance(player);
                    if (!economy.withdrawPlayer(player, amount).transactionSuccess()) {
                        //player.sendMessage("§cBir hata oluştu, işlem gerçekleştirilemiyor.");
                        Utils.msgPlayer(player,Utils.getConf("messages.an-error-occured"));
                        return true;
                    }
                    //player.sendMessage("§6§lKese §f" + economy.format(bal) + " altın aldın.");
                    Utils.msgPlayer(player,Utils.getConf("messages.got-gold").replace("%gold%",economy.format(bal)));
                    //player.sendMessage("Yeni altın miktarı §6" + economy.format(economy.getBalance(player)) + " §6Altın");
                    Utils.msgPlayer(player,Utils.getConf("messages.new-balance").replace("%balance%",economy.format(economy.getBalance(player))));
                    //player.sendMessage("§c§l- §c" + formatted);
                    Utils.msgPlayer(player,Utils.getConf("messages.decrease").replace("%formatted%",formatted));
                    HashMap<Integer, ItemStack> map = player.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, (int) amount));
                    if (!map.isEmpty() && map.get(0).getAmount() != 0) {
                        //player.sendMessage("§cEnvanterinde yer kalmadığı için altınlar yere düştü!");
                        Utils.msgPlayer(player,Utils.getConf("messages.inventory-space"));

                        if (map.get(0).getAmount() <= 64) {
                            player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.GOLD_INGOT, map.get(0).getAmount()));
                        } else {
                            for (int i = map.get(0).getAmount(); i >= 64; i = i - 64) {
                                player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.GOLD_INGOT, 64));
                            }

                            if (map.get(0).getAmount() % 64 != 0) {
                                player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.GOLD_INGOT, map.get(0).getAmount() % 64));
                            }
                        }
                    }
                    return true;
                } else {
                    //player.sendMessage("§cKesenizde yeterli miktarda altın yok.");
                    Utils.msgPlayer(player,Utils.getConf("messages.not-enough-gold"));
                    return true;
                }
            } else {
               //player.sendMessage("§cMiktar sayı olmalıdır.");
                Utils.msgPlayer(player,Utils.getConf("messages.must-be-number"));
                return true;
            }
        }

        if (args.length > 0) {
            sendHelpMessage(player);
        }

        if (args.length == 0) {
            //player.sendMessage("\n§6Kese §8§l≫ " + economy.format(economy.getBalance(player)) + " Altın\n§r ");
            Utils.msgPlayer(player,Utils.getConf("messages.pouch-balance").replace("%balance%",economy.format(economy.getBalance(player))));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("al");
            completions.add("koy");
            completions.add("yardım");
            return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
        }
        return null;
    }


    private void sendHelpMessage(Player player) {
        /*player.sendMessage("§6.oOo.__________________.[ §e/kese §6].__________________.oOo.");
        player.sendMessage("§3/kese §7Kesenizde bulunan altını gösterir.");
        player.sendMessage("§3/kese §bkoy (miktar) §7Keseye altın koyar.");
        player.sendMessage("§3/kese §bal (miktar) §7Keseden altın alır.");
        player.sendMessage("§3/altin §bgonder (oyuncu) (miktar) §7Hedef oyuncuya altın gönderir.");*/

        String sendMsg = String.join("\n",Utils.getList("messages.help-messages"));
        Utils.msgPlayer(player,sendMsg);

    }

}
