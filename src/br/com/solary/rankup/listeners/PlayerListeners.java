package br.com.solary.rankup.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.solary.rankup.RankUP;
import br.com.solary.rankup.models.PlayerRank;
import br.net.fabiozumbi12.UltimateChat.Bukkit.API.SendChannelMessageEvent;

public class PlayerListeners implements Listener {

	private RankUP plugin;
	public PlayerListeners(RankUP plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	private void onJoin(PlayerJoinEvent e) {
		plugin.getSql().loadPlayer(e.getPlayer().getName());
	}

	@EventHandler
	private void onQuit(PlayerQuitEvent e) {
		plugin.getRankManager().playerRanks.removeIf(playerRank -> playerRank.getName().equalsIgnoreCase(e.getPlayer().getName()));
	}

	@EventHandler
	public void onChat(SendChannelMessageEvent e){
		if(!(e.getSender() instanceof Player)) return;
		Player player = (Player) e.getSender();
		PlayerRank playerRank = plugin.getRankManager().getPlayerRank(e.getSender().getName());
		if(playerRank == null) return;
		if(player.hasPermission("solary.staff")) return;
		e.addTag("{rankup}", playerRank.getRank().getRankName().replace("&", "§"));
	}

	@EventHandler
	private void onInvClick(InventoryClickEvent e) {
		if(e.getInventory().getTitle().equalsIgnoreCase("§8Lista de ranks")) {
			e.setCancelled(true);
		}
	}
}
