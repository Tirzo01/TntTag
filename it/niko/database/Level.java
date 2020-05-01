package it.niko.database;

public class Level {
	
	private int level;
	private int expToNextLevel;
	
	public Level(int level, int expToNextLevel) {
		this.level = level;
		this.expToNextLevel = expToNextLevel;
	}
	
	public int getLevel() {
		return level;
	}
	public int getExpToNextLevel() {
		return expToNextLevel;
	}
}
