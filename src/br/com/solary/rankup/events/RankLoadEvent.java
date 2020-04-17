package br.com.solary.rankup.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import br.com.solary.rankup.models.Rank;

public class RankLoadEvent extends Event {

	private final Player player;
	private final Rank loadedRank;

	public RankLoadEvent(Player player, Rank loadedRank) {
		this.player = player;
		this.loadedRank = loadedRank;
	}

	private static final HandlerList HANDLERS = new HandlerList();

	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	public Player getPlayer() {
		return this.player;
	}

	public Rank getLoadedRank() {
		return loadedRank;
	}
}
