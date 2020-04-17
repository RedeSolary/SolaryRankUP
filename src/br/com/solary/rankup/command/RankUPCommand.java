package br.com.solary.rankup.command;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.solary.core.integrations.VaultEconomy;
import br.com.solary.core.nms.ActionBar;
import br.com.solary.rankup.RankUP;
import br.com.solary.rankup.events.RankUPEvent;
import br.com.solary.rankup.models.PlayerRank;
import br.com.solary.rankup.models.Rank;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class RankUPCommand implements CommandExecutor {

	public RankUP plugin;
	private HashMap<String, Long> confirmRankUP;
	public RankUPCommand(RankUP plugin) {
		this.plugin = plugin;
		confirmRankUP = new HashMap<>();
		plugin.getCommand("rankup").setExecutor(this);
		new BukkitRunnable() {	
			@Override
			public void run() {
				new HashMap<>(confirmRankUP).forEach((player, millis) -> {
					if(System.currentTimeMillis() >= millis) confirmRankUP.remove(player);
				});
			}
		}.runTaskTimerAsynchronously(plugin, 20L, 2 * 20L);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
		if(cmd.getName().equalsIgnoreCase("rankup")){
			if(!(sender instanceof Player)) return true;
			Player player = (Player) sender;
			PlayerRank playerRank = plugin.getRankManager().getPlayerRank(player.getName());
			Rank nextRank = plugin.getRankManager().getNextRank(playerRank.getRank());
			if(nextRank == null) {
				player.sendMessage("§cVocê já está no ultimo rank, por enquanto não existem mais ranks para evoluir");
				return true;
			}
			if(confirmRankUP.containsKey(player.getName())) {
				if(!(VaultEconomy.getEconomy().getBalance(player) >= nextRank.getCost())) {
					player.sendMessage("§cVocê não possui dinheiro suficiente para evoluir, você precisa de §2R$ §f" + RankUP.numberFormat.format(nextRank.getCost()).replace("R$ ", ""));
					return true;
				}else {
					VaultEconomy.getEconomy().withdrawPlayer(player, nextRank.getCost());
					plugin.getSql().setRank(playerRank, nextRank);
					player.sendMessage("§aVocê evoluiu para o rank " + nextRank.getRankName() + " §acom sucesso, boa sorte em sua jornada.");
					new ActionBar("§f" + player.getName() + " §aEvoluiu para o rank " + nextRank.getRankName()).sendToAll();
					nextRank.getCommands().forEach(commands -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commands.replace("%player%", player.getName())));
					Bukkit.getPluginManager().callEvent(new RankUPEvent(player, nextRank, playerRank.getRank()));
				}
			}else {
				confirmRankUP.put(player.getName(), System.currentTimeMillis() + 10000L);
				String hasMoney = VaultEconomy.getEconomy().getBalance(player) >= nextRank.getCost() ? "§8§l(§a✔§8§l)" : "§8§l(§c✘§8§l)"; 
				BaseComponent[] baseComponentText = TextComponent.fromLegacyText("§cSe você realmente quiser evoluir clique ");
				BaseComponent[] baseComponentClick = TextComponent.fromLegacyText("§a§lAQUI");
				TextComponent textComponentText = new TextComponent(baseComponentText);
				TextComponent textComponentClick = new TextComponent(baseComponentClick);
				textComponentClick.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rankup"));
				textComponentClick.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
						"§7Clique aqui para confiar sua evolução\n" +
								"§7\n" +
								"§eRank§8: §f" + nextRank.getRankName() + "\n" + 
								"§ePreço§8: §2R$ §f" + RankUP.numberFormat.format(nextRank.getCost()).replace("R$ ", "") + " " + hasMoney).create()));
				player.sendMessage("");
				player.sendMessage("§cVocê tem certeza que deseja evoluir para o rank " + nextRank.getRankName() + "§c?");
				player.spigot().sendMessage(textComponentText, textComponentClick);
				player.sendMessage("");
			}
		}
		return false;
	}

}
