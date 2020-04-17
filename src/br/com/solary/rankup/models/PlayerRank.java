package br.com.solary.rankup.models;

import lombok.Data;

@Data
public class PlayerRank {

	private String name;
	private Rank rank;

	public PlayerRank(String name, Rank rank) {
		this.name = name;
		this.rank = rank;
	}
}
