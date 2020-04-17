package br.com.solary.rankup.models;

import java.util.List;

import com.mojang.authlib.GameProfile;

import br.com.solary.core.nms.SkullTexture;
import lombok.Data;

@Data
public class Rank {

	private String rankName;
	private double cost;
	private int order;
	private GameProfile gameProfileHead;
	private List<String> commands;
	
	public Rank(String rankName, double cost, int order, String gameProfileHead, List<String> commands) {
		this.rankName = rankName;
		this.cost = cost;
		this.order = order;
		this.gameProfileHead = SkullTexture.getNonPlayerProfile(gameProfileHead);
		this.commands = commands;
	}
}
