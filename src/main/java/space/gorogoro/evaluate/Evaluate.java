package space.gorogoro.evaluate;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import space.gorogoro.evaluate.BukkitUtil;

public class Evaluate extends JavaPlugin implements Listener{
  private Connection con;
  
  @Override
  public void onEnable(){
    getServer().getPluginManager().registerEvents(this, this);
    BukkitUtil.logInfo("The Plugin Has Been Enabled!");
    try{
      // 設定ファイルが無ければ作成
      File configFile = new File(this.getDataFolder() + "/config.yml");
      if(!configFile.exists()){
        this.saveDefaultConfig();
      }
      
      // JDBCドライバーの指定
      Class.forName("org.sqlite.JDBC");
      // データベースに接続する なければ作成される
      con = DriverManager.getConnection("jdbc:sqlite:" + this.getDataFolder() + "/status.db");
      con.setAutoCommit(false);      // auto commit無効
      
      // ResultSet及び、Statementオブジェクト作成
      ResultSet rs;
      Statement stmt = con.createStatement();
      stmt.setQueryTimeout(30);    // タイムアウト設定
      
      // テーブルの実在チェック
      Boolean existsUserTable = false;
      rs = stmt.executeQuery("SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='users'");
      while (rs.next()) {
        if(rs.getString(1).equals("1")){
          existsUserTable = true;
        }
      }
      Boolean existsHistoryTable = false;
      rs = stmt.executeQuery("SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='last_executed_history'");
      while (rs.next()) {
        if(rs.getString(1).equals("1")){
          existsHistoryTable = true;
        }
      }
      
      // 理由の項目の実在チェック
      Boolean existsHistoryReasonField = false;
      rs = stmt.executeQuery("SELECT count(*) from sqlite_master  where name = 'last_executed_history' and sql like '%reason%'");
      while (rs.next()) {
        if(rs.getString(1).equals("1")){
          existsHistoryReasonField = true;
        }
      }
      
      // テーブルが無かった場合
      if(!existsUserTable){
        // テーブル作成
        stmt.executeUpdate("CREATE TABLE users ("
          + " id INTEGER PRIMARY KEY AUTOINCREMENT"
          + ",uuid STRING NOT NULL"
          + ",playername STRING NOT NULL"
          + ",reputation REAL NOT NULL DEFAULT 1.000"
          + ",reason STRING NOT NULL DEFAULT 'Empty for old data.'"
          + ",last_executed_at DATETIME NOT NULL DEFAULT (datetime('now','localtime')) CHECK(last_executed_at LIKE '____-__-__ __:__:__')"
          + ",created_at DATETIME NOT NULL DEFAULT (datetime('now','localtime')) CHECK(created_at LIKE '____-__-__ __:__:__')"
          + ",updated_at DATETIME NOT NULL DEFAULT (datetime('now','localtime')) CHECK(updated_at LIKE '____-__-__ __:__:__')"
          + ");"
        );
        //インデックス作成
        stmt.executeUpdate("CREATE INDEX uuid ON users (uuid);");
        stmt.executeUpdate("CREATE INDEX playername ON users (playername);");
      }
      // テーブルが無かった場合
      if(!existsHistoryTable){
        // テーブル作成
        stmt.executeUpdate("CREATE TABLE last_executed_history ("
          + " id INTEGER PRIMARY KEY AUTOINCREMENT"
          + ",sender_uuid STRING NOT NULL"
          + ",sender_playername STRING NOT NULL"
          + ",target_uuid STRING NOT NULL"
          + ",target_playername STRING NOT NULL"
          + ",last_executed_type INTEGER NOT NULL"
          + ",reason STRING NOT NULL DEFAULT 'Empty for old data'"
          + ",created_at DATETIME NOT NULL DEFAULT (datetime('now','localtime')) CHECK(created_at LIKE '____-__-__ __:__:__')"
          + ",updated_at DATETIME NOT NULL DEFAULT (datetime('now','localtime')) CHECK(updated_at LIKE '____-__-__ __:__:__')"
          + ",unique(sender_uuid, target_uuid, last_executed_type)"
          + ");"
        );
        //インデックス作成
        stmt.executeUpdate("CREATE INDEX last_uuid_type ON last_executed_history (sernder_uuid, target_uuid, last_executed_type);");
        stmt.executeUpdate("CREATE INDEX sender_playername ON last_executed_history (sender_playername);");
        stmt.executeUpdate("CREATE INDEX target_playername ON last_executed_history (target_playername);");
      }
      
      // 理由の項目が無かった場合
      if(!existsHistoryReasonField){
        // 理由の追加
        stmt.executeUpdate("ALTER TABLE last_executed_history ADD COLUMN reason STRING NOT NULL DEFAULT 'Empty for old data';");
      }
      stmt.close();
    } catch (Exception e){
      BukkitUtil.logStackMessage(e);
    }
    
  }
  
  @EventHandler
  public void onLogin(PlayerJoinEvent event){
    PreparedStatement prepStmt;
    ResultSet rs;
    
    try {
      // 既存ユーザーか確認
      Player tp = event.getPlayer();
      Boolean existsUser = false;
      prepStmt = con.prepareStatement("SELECT COUNT(id) FROM users WHERE uuid=?");
      prepStmt.setString(1,tp.getUniqueId().toString());
      rs = prepStmt.executeQuery();
      while(rs.next()){
        if(rs.getString(1).equals("1")){
          existsUser = true;
        }
      }
      
      // 既存ユーザーで無ければユーザー登録
      if(!existsUser){
        prepStmt = con.prepareStatement(
          "INSERT INTO users("
          + " uuid"
          + ",playername"
          + ") VALUES ("
          + " ?"
          + ",?"
          + ");"
        );
        prepStmt.setString(1, tp.getUniqueId().toString());
        prepStmt.setString(2, tp.getName());
        prepStmt.addBatch();
        prepStmt.executeBatch();
        con.commit();
      }
      
      // 現在の情報を表示
      tp.sendMessage("ようこそ " + tp.getName() + " さん♪");
      MainProcess.status(con, tp);
    } catch (SQLException e) {
      BukkitUtil.logStackMessage(e);
    }
  }
  
  /**
   * コマンド実行時に呼び出されるメソッド
   * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender,
   *      org.bukkit.command.Command, java.lang.String, java.lang.String[])
   */
  public boolean onCommand( CommandSender sender, Command command, String label, String[] args) {
    try{
      if((sender instanceof Player)) {
        Player sp = (Player)sender;
        if(sp.hasPermission("space.gorogoro.evaluate.*")){
          FileConfiguration conf = getConfig();
          if( command.getName().equals("bad") ) { 
            return MainProcess.bad(conf, con, sp, args);
          }else if( command.getName().equals("good") ) {
            return MainProcess.good(conf, con, sp, args);
          }else if( command.getName().equals("status") ) {
            boolean ret;
            if(sp.isOp()){
              ret = MainProcess.statusOp(con, sp, args);
              if(sp.getName().equals(conf.getString("owner_name"))){
                MainProcess.reasonOp(con, sp, args);
              }
            }else{
              ret = MainProcess.status(con, sp);
            }
            return ret;
          }else if( sp.isOp() && command.getName().equals("update") ) { 
            return MainProcess.upDate(con, sp, args);
          }else if( sp.isOp() && command.getName().equals("upvalue") ) { 
            return MainProcess.upValue(con, sp, args);
          }
        }
      }
    }catch(Exception e){
      BukkitUtil.logStackMessage(e);
    }
    return true;
  }
  
  @Override
  public void onDisable(){
    try{
      if (con != null) {
        con.close();      // DB切断
      }
    } catch (Exception e){
      BukkitUtil.logStackMessage(e);
    }
    BukkitUtil.logInfo("The Plugin Has Been Disabled!");
  }
}
