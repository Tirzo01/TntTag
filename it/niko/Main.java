package it.niko;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

import it.niko.game.Game;
import it.niko.game.LobbyManager;

public class Main extends JavaPlugin implements Listener {
	
	public static Main instance = null;
	
	private File italianFile;
	private FileConfiguration italianMessages;
	private File englishFile;
	private FileConfiguration englishMessages;
	
	public void onEnable() {
		instance = this;
		loadLanguages();
		saveDefaultConfig();
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		loadEvents();
	}
	
	public void onDisable() {}
	
	private void loadEvents() {
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		Bukkit.getServer().getPluginManager().registerEvents(new LobbyManager(), this);
		Bukkit.getServer().getPluginManager().registerEvents(Game.getInstance(), this);
	}
	
	private void loadLanguages() {
		italianFile = new File(instance.getDataFolder(), "italian.yml");
		if (!italianFile.exists()) {
			try {
				italianFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		italianMessages = YamlConfiguration.loadConfiguration(italianFile);
		
		englishFile = new File(instance.getDataFolder(), "english.yml");
		if (!englishFile.exists()) {
			try {
				englishFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		englishMessages = YamlConfiguration.loadConfiguration(englishFile);
		
		instance.saveResource("italian.yml", true);
		instance.saveResource("english.yml", true);
	}
	
	public FileConfiguration getItalianConfig() {
		return this.italianMessages;
	}
	
	public FileConfiguration getEnglishConfig() {
		return this.englishMessages;
	}
	
	@EventHandler
	public void onClickInventory(InventoryClickEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onFoodChange(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onFall(EntityDamageEvent event) {
		event.setDamage(0);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockDestroy(BlockBreakEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onJoinRemoveMessage(PlayerJoinEvent event) {
		event.setJoinMessage(null);
	}
	
	@EventHandler
	public void onQuitRemoveMessage(PlayerQuitEvent event) {
		event.setQuitMessage(null);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getAction().equals(Action.PHYSICAL) && event.getClickedBlock().getType() == Material.FARMLAND) {
		        event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPing(ServerListPingEvent event) {
		if(!Game.getInstance().isStarted()) {
			event.setMotd("Attesa");
		}
		else {
			event.setMotd("Occupato");
		}
	}
}