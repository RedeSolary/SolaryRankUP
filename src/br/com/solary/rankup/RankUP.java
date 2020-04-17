package br.com.solary.rankup;

import java.text.NumberFormat;
import java.util.Locale;

import br.com.solary.core.SolaryPlugin;
import br.com.solary.rankup.command.RankUPCommand;
import br.com.solary.rankup.command.RanksCommand;
import br.com.solary.rankup.database.SQL;
import br.com.solary.rankup.database.SQL.SQLType;
import br.com.solary.rankup.listeners.PlayerListeners;
import br.com.solary.rankup.manager.RankManager;

public class RankUP extends SolaryPlugin{

	private RankManager rankManager;
	private SQL sql;
	public static RankUP instance;
	public static NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));

	@Override
	public void onEnableInner() {
		instance = this;
		saveDefaultConfig();
		
		if (getConfig().getBoolean("MySQL.Ativar")) {
			String user, password, host, database;
			user = getConfig().getString("MySQL.User");
			password = getConfig().getString("MySQL.Password");
			host = getConfig().getString("MySQL.Host");
			database = getConfig().getString("MySQL.Database");
			sql = new SQL(user, password, host, database, SQLType.MySQL, this);
			sql.startConnection();
		} else {
			if(!this.getDataFolder().exists()) this.getDataFolder().mkdir();
			sql = new SQL("SolaryRankUP", getDataFolder(), SQLType.SQLite, this);
			sql.startConnection();
		}	
		
		rankManager = new RankManager(this);
		new RanksCommand(this);
		new RankUPCommand(this);
		new PlayerListeners(this);
		
		sql.loadCache();
	}

	@Override
	public void onDisableInner() {
		// TODO Auto-generated method stub

	}

	public RankManager getRankManager() {
		return rankManager;
	}
	
	public SQL getSql() {
		return sql;
	}
}
