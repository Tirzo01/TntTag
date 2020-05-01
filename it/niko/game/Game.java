package it.niko.game;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import it.niko.Connector;
import it.niko.Main;
import it.niko.Util;
import it.niko.database.Database;
import it.niko.database.HidePlayer;

public class Game implements Listener {
	
	private static Game instance;
	
	/* System variable*/
	int roundTaskID;
	private FileConfiguration config; //= Main.instance.getConfig();
	private boolean matchStarted = false;
	private BossBar bossbar = null;
	
	/* Game variables */
	double explosionCountdown = 60;
	private ArrayList<Player> playersInGame;
	private boolean canGiveExp = false;
	private ArrayList<Player> playersGivenExp;
	private Player TntPlayer;
	short round;
	/* INFO */
	//L'amplifier della pozione della velocità e buggato, se si imposta il valore 2 diventa 3, 3 diventa 4 etc
	// per intenderci n + 1
	
	private Game() {
		config = Main.instance.getConfig();
		playersInGame = new ArrayList<Player>();
		playersGivenExp = new ArrayList<Player>();
	}
	
	public static Game getInstance() {
		if(instance == null)
			instance = new Game();
		return instance;
	}
	
	public void start() {
		matchStarted = true;
		Bukkit.getOnlinePlayers().forEach(all->playersInGame.add(all));
		if(playersInGame.size() == 0) { //Potrebbe succedere
			//Server in Riavvio
		}
		else {
		newRound();
		}
	}
	
	public boolean isStarted() {
		return matchStarted;
	}
	
	private void newRound() {
		//players in game > di 0?
		this.roundTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.instance, new Runnable() {
			@Override
			public void run() {
				if(explosionCountdown > 60 && explosionCountdown <= 65) {
					//explosionCountdown--;
				}
				if(explosionCountdown == 60) {
					teleportAll();
					playersInGame.forEach(all->all.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2-(1))));
					setTntPlayer(randomPlayer());
				}
				if(explosionCountdown > 10 && explosionCountdown <= 60) {
					setTimeBar("§bBOOM tra: ", BarColor.GREEN, explosionCountdown);
					//explosionCountdown--;
				}
				if(explosionCountdown > 5 && explosionCountdown <= 10) {
					setTimeBar("§bBOOM tra: ", BarColor.YELLOW, explosionCountdown);
					//explosionCountdown--;
				}
				if(explosionCountdown >= 1 && explosionCountdown <= 5) {
					setTimeBar("§bBOOM tra: ", BarColor.RED, explosionCountdown);
					playersInGame.forEach(all->all.playSound(TntPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100, 1));
					//explosionCountdown--;
				}
				if(explosionCountdown == 0) {
					setTimeBar("§bBOOM tra: ", BarColor.PURPLE, explosionCountdown);
					playersInGame.forEach(all->all.playSound(TntPlayer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 1));
				}
				explosionCountdown--;
				if(explosionCountdown < -1) {
					explodeTnt();
					if(playersInGame.size() == 1) {
						setWinner();
						end();
					}
					else {
						TntPlayer = null;
						//explosionCountdown = 60;
						resetRoundCountdown();
					}
				}
			}
			
		},0,20);
		
	}
	
	private void stopRoundTask() {
		Bukkit.getScheduler().cancelTask(roundTaskID);
	}
	
	private void explodeTnt() {
		//Tnttag potrebbe essere null
		Bukkit.getOnlinePlayers().forEach(all->all.playSound(TntPlayer.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 100, 1));
		playersInGame.remove(TntPlayer);
		TntPlayer.setGameMode(GameMode.SPECTATOR);
		TntPlayer.sendMessage("§b§lTntTag §7» §cSei esploso.");
		playersInGame.forEach(all->all.sendMessage("§c" + TntPlayer.getName() + " §7è esploso!"));
		HidePlayer hpwinner = Database.getInstance().getHidePlayer(TntPlayer.getName());
		int exp = Util.randomNumber(20, 30);
		int coins = Util.randomNumber(7, 15);
		hpwinner.setExp(hpwinner.getExp() + exp);
		hpwinner.setCoins(hpwinner.getCoins() + coins);
		TntPlayer.sendMessage("§d+"+exp+" EXP");
		TntPlayer.sendMessage("§6+"+coins+" coins");
	}
	
	public void resetRoundCountdown() {
		this.explosionCountdown = 65;
	}
	
	private void setTimeBar(String text, BarColor color ,double secondsRemain) {
		if(this.bossbar == null) {
			this.bossbar = Bukkit.createBossBar("", color, BarStyle.SOLID);
			for(Player all : Bukkit.getOnlinePlayers()) {
				this.bossbar.addPlayer(all);
			}
		}
		this.bossbar.setColor(color);
		this.bossbar.setTitle(text+(int)secondsRemain);
		this.bossbar.setProgress(0.01666 * secondsRemain); //0.166 è il risultato di 1 (valore massimo che può assumere la bossbar) fratto 60 (secondi che servono per riempirla)
	}
	
	private Player randomPlayer() {
		Random random = new Random();
		Player randomPlayer = playersInGame.get(random.nextInt(playersInGame.size()));
		return randomPlayer;
	}
	private void setTntPlayer(Player player) {
		TntPlayer = player;
		TntPlayer.removePotionEffect(PotionEffectType.SPEED);
		TntPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 4-(1)));
		TntPlayer.getInventory().setHelmet(new ItemStack(Material.TNT));
		TntPlayer.playSound(TntPlayer.getLocation(), Sound.ENTITY_TNT_PRIMED, 1, 1);
		for(int i = 0; i < 9; i++)
			TntPlayer.getInventory().setItem(i, new ItemStack(Material.TNT));
		spawnEffect(TntPlayer.getLocation());
		sendGotTntMessage();
		playersInGame.forEach(all->all.sendMessage("§e"+TntPlayer.getName()+" §7ha la §aTNT"));
	}
	
	public void unsetTntPlayer() {
		TntPlayer.removePotionEffect(PotionEffectType.SPEED);
		TntPlayer.getInventory().setHelmet(new ItemStack(Material.AIR));
		TntPlayer.getInventory().clear();
		TntPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2-(1)));
	}
	
	private void sendGotTntMessage() {
		try {
			TntPlayer.sendTitle("", "§4Hai la §4TNT",10,1,10);
		} catch (Exception ex) {

		}
	}

	private void spawnEffect(Location loc) {
		Firework f = (Firework) loc.getWorld().spawn(loc, Firework.class);
		FireworkMeta fm = f.getFireworkMeta();
		fm.addEffect(FireworkEffect.builder().flicker(false).trail(true).with(Type.STAR).withColor(Color.YELLOW).withFade(Color.ORANGE).build());
		fm.setPower(1);
		f.detonate();
	}
	
	private void teleportAll() {
		playersInGame.forEach(all->all.teleport(getSpawnLocation()));
	}
	
	public void setWinner() {
		Player winner = playersInGame.get(0);
		HidePlayer hpwinner = Database.getInstance().getHidePlayer(winner.getName());
		int exp = Util.randomNumber(100, 150);
		int coins = Util.randomNumber(15, 25);
		hpwinner.setExp(hpwinner.getExp() + exp);
		hpwinner.setCoins(hpwinner.getCoins() + coins);
		winner.sendMessage("§d+"+exp+" EXP");
		winner.sendMessage("§6+"+coins+" coins");
		Bukkit.getOnlinePlayers().forEach(all->{
			all.playSound(TntPlayer.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
			all.playSound(all.getLocation(), Sound.MUSIC_DISC_CAT, 100, 1);
			all.sendMessage("§b§lTntTag §7» §a"+winner.getName() + "§7 ha vinto!");
	});
	}
	
	public Location getSpawnLocation() {
		World world;
		double x, y, z;
		float yaw,pitch;
		world = Bukkit.getWorld(config.getString("gamespawn.world"));
		x = config.getDouble("gamespawn.x");
		y = config.getDouble("gamespawn.y");
		z = config.getDouble("gamespawn.z");
		yaw = Float.parseFloat(config.getString("gamespawn.yaw"));
		pitch = Float.parseFloat(config.getString("gamespawn.pitch"));
		return new Location(world, x, y, z, yaw, pitch);
	}
	
	public void end() {
		stopRoundTask();
		this.playersInGame.clear();
		this.bossbar.removeAll();
		this.bossbar = null;
		this.TntPlayer = null;
		this.round = 0;
		this.explosionCountdown = 60;
		this.canGiveExp = true;
		new BukkitRunnable() {
		     @Override
			public void run() {
				this.matchStarted = false;
				canGiveExp = false;
				playersGivenExp.clear();
				Connector conn = new Connector();
				Bukkit.getOnlinePlayers().forEach(all->conn.connectTo(all, "hub"));
				this.cancel();
			}
		}.runTaskLater(Main.instance, 10*(20));
		//System.out.println("[Debug] end() ended!");
	}
	
	/* Eventi */
	@EventHandler
	public void onHit(EntityDamageByEntityEvent event) {
		if(!this.isStarted()) {
			event.setCancelled(true);
			return;
		}
		//System.out.println(this.playersInGame);
		if(event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
			event.setDamage(0);
			Player damager = (Player) event.getDamager();
			Player target = (Player) event.getEntity();
			if(damager.getName().equals(TntPlayer.getName())) {
				unsetTntPlayer();
				setTntPlayer(target);
			}
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		if(!playersInGame.contains(event.getPlayer()))
			return;
		playersInGame.remove(event.getPlayer());
		if(playersInGame.size() == 0 && matchStarted == true) {
			end();
		}
		if(playersInGame.size() == 1 && matchStarted == true) {
			if(playersInGame.get(0) == TntPlayer) {
				unsetTntPlayer();
			}
			setWinner();
			end();
		}
		if(playersInGame.size() >= 2 && matchStarted == true) {
			explodeTnt();
			//this.explosionCountdown = 65;
			resetRoundCountdown();
		}
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		if(event.getMessage().equalsIgnoreCase("gg") && this.canGiveExp && !this.playersGivenExp.contains(event.getPlayer())) {
			int exp = 15;
			Bukkit.getScheduler().runTaskLater(Main.instance, new Runnable() {
			    @Override
			    public void run() {
			    	event.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE+"+"+exp+" EXP");
			    }
			},4);
			this.playersGivenExp.add(event.getPlayer());
			HidePlayer hp = Database.getInstance().getHidePlayer(event.getPlayer().getName());
			hp.setExp(hp.getExp() + exp);
		}
	}
}
