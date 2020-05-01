package it.niko.Translator;

import org.bukkit.configuration.file.FileConfiguration;

import it.niko.Main;
import it.niko.database.Language;

public class Messager {
	
	public static String getMessage(String key, Language lang) {
		FileConfiguration file = null;
		switch(lang) {
		case EN:
			file = Main.instance.getEnglishConfig();
			break;
		case IT:
			file = Main.instance.getItalianConfig();
			break;
		}
		return file.getString(key);
	}
}
