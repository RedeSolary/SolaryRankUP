
package br.com.solary.rankup.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.solary.rankup.RankUP;
import br.com.solary.rankup.events.RankLoadEvent;
import br.com.solary.rankup.models.PlayerRank;
import br.com.solary.rankup.models.Rank;

public class SQL {

	private String user, host, database, password;
	private Connection connection;	
	private Statement statement;
	private SQLType sqlType;
	private File db;

	public RankUP plugin;

	public SQL(String user, String password, String host, String database, SQLType sqlType, RankUP plugin) {
		this.plugin = plugin;
		this.user = user;
		this.password = password;
		this.host = host;
		this.database = database;
		this.sqlType = sqlType;
	}

	public SQL(String database, File folder, SQLType sqlType, RankUP plugin){
		this.plugin = plugin;
		this.db = folder;
		this.database = database;
		this.sqlType = sqlType;
	}

	public SQL(RankUP plugin) {
		this.plugin = plugin;
	}

	public void startConnection(){
		if(getType().equals(SQLType.MySQL)){
			try {
				Class.forName("com.mysql.jdbc.Driver");
				connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database + "", user, password);
				statement = connection.createStatement();
				statement.execute("CREATE TABLE IF NOT EXISTS RankUP "
						+ "("
						+ "player VARCHAR(16) NOT NULL, "
						+ "rank VARCHAR(64) NOT NULL"
						+ ")");
			} catch (SQLException | ClassNotFoundException e) {
				Bukkit.getConsoleSender().sendMessage(e.getMessage());
			}
		}else if(getType().equals(SQLType.SQLite)){
			try {
				Class.forName("org.sqlite.JDBC");
				connection = DriverManager.getConnection("jdbc:sqlite:" + db.getAbsolutePath() + File.separator + database+".db");
				statement = connection.createStatement();
				statement.execute("CREATE TABLE IF NOT EXISTS RankUP "
						+ "("
						+ "player VARCHAR(16) NOT NULL, "
						+ "rank VARCHAR(64) NOT NULL"
						+ ")");
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void setRank(PlayerRank playerRank, Rank rank){
		checkConnection();
		new BukkitRunnable() {
			@Override
			public void run() {
				try{
					playerRank.setRank(rank);
					PreparedStatement ps = connection.prepareStatement("UPDATE `RankUP` SET rank=? WHERE player=?");
					ps.setString(1, rank.getRankName());
					ps.setString(2, playerRank.getName());
					ps.execute();
				}catch(SQLException e){
					Bukkit.getConsoleSender().sendMessage(e.getMessage());
				}
			}
		}.runTaskAsynchronously(plugin);
	}
	
	public void loadPlayer(String playerName){
		checkConnection();
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					if(plugin.getRankManager().getPlayerRank(playerName) != null) return;
					PreparedStatement ps = connection.prepareStatement("SELECT * FROM RankUP WHERE player = ?");
					ps.setString(1, playerName);
					final ResultSet rs = ps.executeQuery();
					if (rs.next()) {
						String player = rs.getString("player");
						String rankName = rs.getString("rank");
						if(Bukkit.getPlayer(player) == null) return;
						Rank rank = plugin.getRankManager().getRankByName(rankName);
						if(rank == null) rank = plugin.getRankManager().getRankByOrder(0);
						plugin.getRankManager().playerRanks.add(new PlayerRank(player, rank));
						Bukkit.getPluginManager().callEvent(new RankLoadEvent(Bukkit.getPlayer(player), rank));
					}else {
						PreparedStatement ps1 = connection.prepareStatement("INSERT INTO RankUP (player, rank) VALUES (?,?)");
						Rank defaultRank = plugin.getRankManager().getRankByOrder(0);
						ps1.setString(1, playerName);
						ps1.setString(2, defaultRank.getRankName());
						ps1.execute();
						plugin.getRankManager().playerRanks.add(new PlayerRank(playerName, defaultRank));
						if(Bukkit.getPlayer(playerName) == null) return;
						Bukkit.getPluginManager().callEvent(new RankLoadEvent(Bukkit.getPlayer(playerName), defaultRank));
					}
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(plugin);
	}
	
	public void loadCache(){
		checkConnection();
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					String query = "SELECT * FROM RankUP";
					final ResultSet rs = statement.executeQuery(query);
					PlayerRank playerRank;
					while (rs.next()) {
						String player = rs.getString("player");
						if(Bukkit.getPlayer(player) == null) continue;
						String rankName = rs.getString("rank");
						playerRank = new PlayerRank(player, plugin.getRankManager().getRankByName(rankName));
						if(playerRank != null){
							plugin.getRankManager().playerRanks.add(playerRank);
						}
					}
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(plugin);
	}

	public void closeConnection(){
		try {
			this.statement.close();
			this.connection.close();
		} catch (SQLException e) {}
	}

	public SQLType getType(){
		return sqlType;
	}

	public enum SQLType{
		MySQL, SQLite;
	}

	public void checkConnection(){
		try {
			if(this.connection.isClosed() || this.connection == null){
				startConnection();
				Bukkit.getConsoleSender().sendMessage("§a[RankUP] A conexão com o MySQL foi restabelecida");
			}
		} catch (SQLException e) {
			Bukkit.getConsoleSender().sendMessage("§4[RankUP] Erro ao checar a conexão: ");
			e.printStackTrace();
		}
	}
	
	public Connection getConnection() {
		return connection;
	}
}
