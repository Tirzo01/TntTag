package it.niko.database;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class HidePlayer {
	String name;
	int exp;
	int coins;
	int crates;
	Language language;
	
	public HidePlayer(String name, int exp, int coins, int crates, Language language) {
		this.name = name;
		this.exp = exp;
		this.coins = coins;
		this.crates = crates;
		this.language = language;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getExp() {
		return this.exp;
	}
	
	public int getCoins() {
		return this.coins;
	}
	
	public int getCrates() {
		return this.crates;
	}
	
	public void setExp(int exp) {
		this.exp = exp;
		Database.getInstance().updateHidePlayer(this);
	}
	
	public void setCoins(int coins) {
		this.coins = coins; 
		Database.getInstance().updateHidePlayer(this);
	}
	
	public void setCrates(int crates) {
		this.crates = crates;
		Database.getInstance().updateHidePlayer(this);
	}
	
	public void setLanguage(Language language) {
		this.language = language;
		Database.getInstance().updateHidePlayer(this);
	}
	
	public Language getLanguage() {
		return this.language;
	}
	
	public void setLevel(int level) {
		int exp = 0;
		int i = 1;
		int k = 1;
		while(i < level) {
			exp = exp + k*50;
			k++;
			i++;
		}
		this.exp = exp;
		Database.getInstance().updateHidePlayer(this);
	}
	
	public Level getCalculatedLevel() {
		int level = 0;
		int j = 1;
		int totalExp = exp;
		while(totalExp >= 0) {
			totalExp = totalExp - 50*j;
			level++;
			j++;
		}
		return new Level(level, totalExp*(-1));
	}
	
	@SuppressWarnings("deprecation")
	public void updateHUD() {
		Player player = Bukkit.getPlayer(name);
		if(player == null)
			return;
		float level = (float) getCalculatedLevel().getLevel();
		float neededExp = (float)getCalculatedLevel().getExpToNextLevel();
		float totalExpPerLevel = 50F*(float)level;
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		Objective objective = board.registerNewObjective("CustomBoard", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName("§e§lHide§6§lWars §c§lNetwork");
		ArrayList<String> rows = new ArrayList<String>();
		rows.add(" ");
		rows.add("§6Rank: §cAdmin");
		rows.add("§6EXP: §c"+this.exp);
		rows.add("§6Livello: §c"+(int)level);
		rows.add("§6Coins: §c"+this.coins);
		rows.add("§6Key: §c"+this.crates);
		rows.add("  ");
		rows.add("§eplay.hidewars.it");
		rows.add("   ");
		rows.add("§7Powered by §bOxStudios");
		int j = rows.size()-1;
		for(int i = 0; i < rows.size(); i++) {
			objective.getScore(rows.get(i)).setScore(j);
			j--;
		}
		player.setScoreboard(board);
		player.setLevel((int)level);
		player.setExp((((totalExpPerLevel-neededExp)*100) / totalExpPerLevel) / 100);
	}
}
