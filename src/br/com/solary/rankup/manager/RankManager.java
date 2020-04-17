package br.com.solary.rankup.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import br.com.solary.core.SolaryPlugin;
import br.com.solary.rankup.RankUP;
import br.com.solary.rankup.models.PlayerRank;
import br.com.solary.rankup.models.Rank;

public class RankManager {

	public List<Rank> ranks;
	public Set<PlayerRank> playerRanks;
	
	private RankUP plugin;
	public RankManager(RankUP plugin) {
		this.plugin = plugin;
		ranks = new ArrayList<>();
		playerRanks = Sets.newConcurrentHashSet();
		loadRanks();
	}
	
	private void loadRanks() {
		plugin.getConfig().getConfigurationSection("Ranks").getKeys(false).forEach(key -> {
			String rankName = plugin.getConfig().getString("Ranks." + key + ".name").replace("&", "§");
			double rankCost = plugin.getConfig().getDouble("Ranks." + key + ".cost");
			int rankOrder = plugin.getConfig().getInt("Ranks." + key + ".order");
			String headName = plugin.getConfig().getString("Ranks." + key + ".headName");
			List<String> commands = plugin.getConfig().getStringList("Ranks." + key + ".commands");
			ranks.add(new Rank(rankName, rankCost, rankOrder, headName, commands));
			SolaryPlugin.fancyLog("Rank " + rankName + " carregado.");
		});
	}
	
	public PlayerRank getPlayerRank(String name) {
		for(PlayerRank ePlayer : playerRanks) {
			if(ePlayer.getName().equalsIgnoreCase(name)) return ePlayer;
		}
		return null;
	}

	public Rank getRankByName(String name) {
		for(Rank rank : ranks) {
			if(rank.getRankName().equalsIgnoreCase(name)) return rank;
		}
		return null;
	}

	public Rank getRankByOrder(int order) {
		for(Rank rank : ranks) {
			if(rank.getOrder() == order) return rank;
		}
		return null;
	}
	
	public Rank getNextRank(int order) {
		return getRankByOrder(order + 1);
	}

	public Rank getNextRank(Rank rank) {
		return getRankByOrder(rank.getOrder() + 1);
	}
}
