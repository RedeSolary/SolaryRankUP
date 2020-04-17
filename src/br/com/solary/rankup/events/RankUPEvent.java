package br.com.solary.rankup.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import br.com.solary.rankup.models.Rank;

public class RankUPEvent extends Event {

    private final Player player;
    private final Rank afterRank;
    private final Rank beforeRank;
    
	public RankUPEvent(Player player, Rank afterRank, Rank beforeRank) {
		this.player = player;
		this.afterRank = afterRank;
		this.beforeRank = beforeRank;
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
    
    public Rank getAfterRank() {
		return afterRank;
	}

	public Rank getBeforeRank() {
		return beforeRank;
	}


}
