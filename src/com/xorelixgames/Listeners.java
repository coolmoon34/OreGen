package com.xorelixgames;

import java.util.List;
import java.util.Random;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

public class Listeners implements Listener {
	private OreGen _og;

	public Listeners(OreGen og) {
		_og = og;
		_og.getServer().getPluginManager().registerEvents(this, og);
	}

	@EventHandler
	public void onFromTo(BlockFromToEvent event) {
		Material id = event.getBlock().getType();
		if (id == Material.WATER || id == Material.STATIONARY_WATER || id == Material.LAVA || id == Material.STATIONARY_LAVA) {
			Block b = event.getToBlock();
			Material toid = b.getType();
			if (toid == Material.AIR) {
				if (generatesCobble(id, b)) {
					List<String> worlds = _og.getConfig().getStringList("Worlds");
					if (worlds.contains(event.getBlock().getLocation().getWorld().getName())) {
        					Random pick = new Random();
                				b.setType(_og.getBlock(pick.nextDouble()));
					}
				}
			}
		}
	}

	private final BlockFace[] faces = new BlockFace[] { BlockFace.SELF, BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };

	public boolean generatesCobble(Material id, Block b) {
                Material mirrorID1 = (id == Material.WATER || id == Material.STATIONARY_WATER ? Material.LAVA : Material.WATER);
		Material mirrorID2 = (id == Material.WATER || id == Material.STATIONARY_WATER ? Material.STATIONARY_LAVA : Material.STATIONARY_WATER);
		for (BlockFace face : faces) {
			Block r = b.getRelative(face, 1);
			if (r.getType() == mirrorID1 || r.getType() == mirrorID2) {
				return true;
			}
		}
		return false;
	}
}