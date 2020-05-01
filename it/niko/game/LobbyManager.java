package it.niko.game;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

import it.niko.Main;
import net.md_5.bungee.api.ChatColor;

public class LobbyManager implements Listener {
	
	private ArrayList<Player> queue = new ArrayList<Player>();
	private int player_needed_to_start;
	private Location lobbySpawn;
	int idCountDownTask;
	FileConfiguration config;
	
	public LobbyManager() {
		config = Main.instance.getConfig();
		setLobbySpawn();
		setPlayerNeededToStart();
	}
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		/* Se la partita è già startata questa funzione non dovrà essere richiamata*/
		if(Game.getInstance().isStarted()) {
			if(!event.getPlayer().hasPermission("staff.do")) {
				event.getPlayer().kickPlayer(ChatColor.RED + "Questa partita e' gia' avviata.");
				return;
			}
			else {
				event.getPlayer().teleport(Game.getInstance().getSpawnLocation());
				return;
			}
		}
		queue.add(event.getPlayer());
		clearPlayer(event.getPlayer());
		event.getPlayer().teleport(getLobbySpawn());
		messageAll("§b§lLobby §7» §6"+event.getPlayer().getName()+"§2 è entrato/a in gioco.", Sound.BLOCK_NOTE_BLOCK_PLING);
		if(queue.size() == player_needed_to_start) {
			startCountDown();
			//server diventa occupato
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		/* Se la partita è già startata questa funzione non dovrà essere richiamata*/
		if(Game.getInstance().isStarted())
			return;
		queue.remove(event.getPlayer()); //Se queue contiene il player chiaramente.
		messageAll("§b§lLobby §7» §6"+event.getPlayer().getName()+"§c ha lasciato il gioco.", Sound.ENTITY_CAT_HISS);
	}
	
	private void startCountDown() {
		this.idCountDownTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.instance, new Runnable() {
			int time = 15;
		    public void run() {
				if(queue.size() < player_needed_to_start) {
					messageAll("§b§lLobby §7» §cNon ci sono abbastanza giocatori per far inziare la partita.", null);
					stopCountDown();
				}
				if(time % 5 == 0 || time <= 5) {
					messageAll("§b§lLobby §7» §aLa partita inzierà tra §2"+time+" secondi", Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
				}
				if(time == 0) {
					Game.getInstance().start();
					queue.clear();
					stopCountDown();
				}
				time--;
		    }
		}, 20, 20);
	}
	
	private void stopCountDown() {
		Bukkit.getScheduler().cancelTask(idCountDownTask);
	}
	
	private void clearPlayer(Player player) {
		player.setGameMode(GameMode.SURVIVAL);
		player.getInventory().clear();
		player.setHealth(20);
		player.setFoodLevel(20);
		player.removePotionEffect(PotionEffectType.SPEED);
	}
	
	private void setPlayerNeededToStart() {
		player_needed_to_start = config.getInt("players_needed_to_start");
	}
	
	private void messageAll(String text, Sound sound) {
		for(Player all : queue) {
			if(sound != null)
				all.playSound(all.getLocation(), sound, 1, 1);
			all.sendMessage(text);
		}
	}
	
	/* Inizializzazione e get delle variabili*/
	
	
	public Location getLobbySpawn() {
		return lobbySpawn;
	}
	
	private void setLobbySpawn() {
		//FileConfiguration config= Main.instance.getConfig();
		World world;
		double x,y,z;
		float yaw,pitch;
		world = Bukkit.getWorld(config.getString("lobbyspawn.world"));
		x = config.getDouble("lobbyspawn.x");
		y = config.getDouble("lobbyspawn.y");
		z = config.getDouble("lobbyspawn.z");
		yaw = Float.parseFloat(config.getString("lobbyspawn.yaw"));
		pitch = Float.parseFloat(config.getString("lobbyspawn.pitch"));
		lobbySpawn = new Location(world, x, y, z, yaw, pitch);
	}
}
