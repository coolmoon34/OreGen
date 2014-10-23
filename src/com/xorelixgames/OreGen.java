package com.xorelixgames;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

//TODO: make per world block config.

public class OreGen extends JavaPlugin {
	public final Logger log = Logger.getLogger("Minecraft");
        private double chances[];
        private Material blocks[];
	/*CHANGELOG :
	 * V1.0 :
	 * First Release - ores had a random chance to spawn (all the same chance)
	 * V2.0 :
	 * Added /og reload command with - og.reload permission
	 * Added World configuration
	 * Added Percentage calculation for each ore (configurable)
	 */

	@Override
	public void onEnable() {
		new Listeners(this);
		getCommand("og").setExecutor(new Commands(this));
		if (!getDataFolder().exists()) {
			getConfig().options().copyDefaults(true);
			getConfig().set("Worlds", addWorlds());
			saveConfig();
		}
                reload();
	}

	public List<String> addWorlds() {
		List<String> worldList = new ArrayList<String>();
		for (World w : getServer().getWorlds()) {
			worldList.add(w.getName());
		}
		return worldList;
	}
        
        public void reload() {
            double ac = 0;
            reloadConfig();
            Map<String, Object> map = getConfig().getConfigurationSection("Chances").getValues(false);
            int count = map.size();
            int i = 0;
            double new_chances[] = new double[count];
            Material new_blocks[] = new Material[count];
            for (Entry<String, Object> entry : map.entrySet()) {
                double ch;
                if (entry.getValue() instanceof String) {
                    ch = Double.parseDouble((String)entry.getValue());
                } else if (entry.getValue() instanceof Integer) {
                    ch = (Integer)entry.getValue();
                } else if (entry.getValue() instanceof Double) {
                    ch = (Double)entry.getValue();
                } else if (entry.getValue() instanceof Float) {
                    ch = (Float)entry.getValue();
                } else {
                    throw new IllegalArgumentException(entry.getKey() + ": " + entry.getValue().toString() + " is not a decimal value");
                }
                if (ch < 0) {
                    throw new IllegalArgumentException(entry.getKey() + "; chances must be positive");
                }
                ac += ch;
                new_chances[i] = ac;
                new_blocks[i] = Material.matchMaterial(entry.getKey());
                if (new_blocks[i] == null) {
                    throw new IllegalArgumentException(entry.getKey() + " not reconized as a material");
                }
                i++;
            }
            if (ac == 0)
                return;
            for (i = 0; i < new_chances.length; i++) {
                new_chances[i] /= ac;
            }
            chances = new_chances;
            blocks = new_blocks;
        }
        
        //chance is bound [0,1]
        public Material getBlock(double chance) {
            if (chances == null)
                return Material.COBBLESTONE;
            int index = Arrays.binarySearch(chances, chance);
            if (index < 0)
                index = -index - 1;
            return blocks[index];
        }

        void printchances(CommandSender player) {
                if (chances == null) {
                    player.sendMessage(ChatColor.GRAY + "AIR         100%");
                    return;
                }
                double last = 0;
                player.sendMessage(String.format("%s--------", ChatColor.GRAY));
                DecimalFormat df = new DecimalFormat("#.#####");
                for (int i = 0; i < chances.length; i++) {
                    double chancepercent = (chances[i] - last) * 100;
                    if (chancepercent > 10) {
                        player.sendMessage(String.format("%1$s%2$-19s %3$s%%", ChatColor.GRAY, blocks[i].toString(), df.format(chancepercent)));
                    } else {
                        player.sendMessage(String.format("%1$s%2$-20s %3$s%%", ChatColor.GRAY, blocks[i].toString(), df.format(chancepercent)));
                    }
                    last = chances[i];
                }
                player.sendMessage(String.format("%s--------", ChatColor.GRAY));
        }
}