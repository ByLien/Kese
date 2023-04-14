package me.lynes.kese;

import me.lynes.kese.cmds.AltinCmd;
import me.lynes.kese.cmds.KeseAdminCmd;
import me.lynes.kese.cmds.KeseCmd;
import me.lynes.kese.listeners.PlayerListener;
import me.lynes.kese.vault.KeseVaultEconomy;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class Kese extends JavaPlugin {

    private static Kese instance;
    private KeseVaultEconomy economy;
    private Database db;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        this.db = new Database();

        try {
            db.connect();
            db.setup();
        } catch (SQLException exception) {
            db.report(exception);
        }

        // bStats
        Metrics metrics = new Metrics(this, 13183);

        // Update checker

        // Vault integration
        economy = new KeseVaultEconomy();
        Bukkit.getServicesManager().register(Economy.class, economy, instance, ServicePriority.Normal);
        getCommand("kese").setExecutor(new KeseCmd());
        getCommand("altin").setExecutor(new AltinCmd());
        getCommand("keseadmin").setExecutor(new KeseAdminCmd());
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            db.getConnection().close();
        } catch (SQLException exception) {
            db.report(exception);
        }
    }

    public static Kese getInstance() {
        return instance;
    }
    public Database getDatabase() { return db;}
    public KeseVaultEconomy getEconomy() { return economy;}

}
