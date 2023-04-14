package me.lynes.kese.cmds;

import me.lynes.kese.Kese;
import me.lynes.kese.utils.Utils;
import me.lynes.kese.vault.KeseVaultEconomy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class AltinCmd implements CommandExecutor, TabCompleter {
    private final Kese plugin = Kese.getInstance();
    private final KeseVaultEconomy economy = plugin.getEconomy();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Bukkit.getLogger().log(Level.INFO, "Bu komut, sadece oyuncular tarafindan kullanilabilir.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length > 0 && args[0].equalsIgnoreCase("gonder")) {
            if (args.length == 3) {
                double amount;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    //player.sendMessage("§cMiktar sayı olmalıdır.");
                    Utils.msgPlayer(player,Utils.getConf("messages.must-be-number"));
                    return true;
                }

                if (amount < 1) {
                    //player.sendMessage("§cMiktar birden küçük olamaz.");
                    Utils.msgPlayer(player,Utils.getConf("messages.must-be-higher"));
                    return true;
                }

                if (economy.has(player, amount)) {
                    Player target = Bukkit.getPlayer(args[1]);

                    if (target == null) {
                        //player.sendMessage("§cBelirtilen oyuncu bulunamadı ya da çevrimiçi değil.");
                        Utils.msgPlayer(player,Utils.getConf("messages.player-not-found"));
                        return true;
                    }


                    if (target == player) {
                        //player.sendMessage("§cKendine altın gönderemezsin!");
                        Utils.msgPlayer(player,Utils.getConf("messages.cant-send-yourself"));
                        return true;
                    }

                    if (!economy.withdrawPlayer(player, amount).transactionSuccess()) {
                        //player.sendMessage("§cBir hata oluştu, işlem gerçekleştirilemiyor.");
                        Utils.msgPlayer(player,Utils.getConf("messages.an-error-occured"));
                        return true;
                    }

                    if (!economy.depositPlayer(target, amount).transactionSuccess()) {
                        //player.sendMessage("§cBir hata oluştu, işlem gerçekleştirilemiyor.");
                        Utils.msgPlayer(player,Utils.getConf("messages.an-error-occured"));
                        return true;
                    }


                    String formatted = economy.format(amount);

                    //player.sendMessage("Yeni altın miktarı §6" + economy.format(economy.getBalance(player)) + " §6Altın");
                    Utils.msgPlayer(player,Utils.getConf("messages.new-balance").replace("%balance%",economy.format(economy.getBalance(player))));
                    //player.sendMessage("§c§l- §c" + formatted);
                    Utils.msgPlayer(player,Utils.getConf("messages.decrease").replace("%formatted%",formatted));

                    //target.sendMessage("Yeni altın miktarı §6" + economy.format(economy.getBalance(target)) + " §6Altın");
                    Utils.msgPlayer(target,Utils.getConf("messages.new-balance").replace("%balance%",economy.format(economy.getBalance(player))));
                    //target.sendMessage("§a§l+ §a" + formatted);
                    Utils.msgPlayer(target,Utils.getConf("messages.increase").replace("%formatted%",formatted));
                    return true;
                } else {
                    //player.sendMessage("§cKesenizde yeterli miktarda altın yok.");
                    Utils.msgPlayer(player,Utils.getConf("messages.not-enough-gold"));
                    return true;
                }

            } else {
                sendHelpMessage(player);
                return true;
            }
        } else {
            sendHelpMessage(player);
        }


        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("gonder");
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

        String sendMsg = String.join("\n", Utils.getList("messages.help-messages"));
        Utils.msgPlayer(player,sendMsg);

    }


}
