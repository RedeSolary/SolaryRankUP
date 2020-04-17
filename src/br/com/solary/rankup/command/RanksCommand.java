package br.com.solary.rankup.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import br.com.solary.core.nms.SkullTexture;
import br.com.solary.core.utils.ItemBuilder;
import br.com.solary.rankup.RankUP;
import br.com.solary.rankup.models.Rank;

public class RanksCommand implements CommandExecutor {

	private RankUP plugin;
	private List<Integer> slots;
	private Inventory inventory;
	public RanksCommand(RankUP plugin) {
		this.plugin = plugin;
		plugin.getCommand("ranks").setExecutor(this);
		slots = new ArrayList<>(Arrays.asList(11, 12, 13, 14, 15, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 38, 39, 40, 41, 42));
	}

	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
		if(cmd.getName().equalsIgnoreCase("ranks")){
			if(!(sender instanceof Player)) return true;
			Player player = (Player) sender;
			if(inventory == null) {	
				Inventory menu = Bukkit.createInventory(null, 6*9, "§8Lista de ranks");
				int slotInt = 0;
				for (Rank rank : plugin.getRankManager().ranks) {
					menu.setItem(slots.get(slotInt), new ItemBuilder(SkullTexture.createHead(rank.getGameProfileHead(), "§fRank " + rank.getRankName()))
							.setLore(Arrays.asList("",
									"§eRank§8: " + rank.getRankName(),
									"§ePreço§8: §2R$ §f" + RankUP.numberFormat.format(rank.getCost()).replace("R$ ", ""),
									"")).toItemStack());
					slotInt++;
				}
				player.openInventory(menu);
				
				inventory = menu;
			}else {
				player.openInventory(inventory);
			}
		}
		return false;
	}

}
