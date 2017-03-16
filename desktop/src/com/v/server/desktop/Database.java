package com.v.server.desktop;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mysql.jdbc.PreparedStatement;
import com.v.server.desktop.Network.PlayerStatsRequest;

public class Database {
	
	private ServerManager sM;

	boolean isConnected = false;
	Connection con = null;
	Statement st = null;
	ResultSet rs = null;
	PreparedStatement pst = null;

	String url = "jdbc:mysql://85.255.9.69:3306/eoh";
	String user = "client";
	String passwordToDatabase = "rasengan";
	
	public Database(ServerManager serverManager){
		this.sM = serverManager;
	}

	public void connect() {
		try {
			con = DriverManager.getConnection(url, user, passwordToDatabase);
			st = con.createStatement();
			rs = st.executeQuery("SELECT VERSION()");
			isConnected = true;

			if (rs.next()) {
				//System.out.println(rs.getString(1));
			}

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
			ex.printStackTrace();
		} finally {
			try {

				if (rs != null) {
					rs.close();
				}

				if (st != null) {
					st.close();
				}

				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {

				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}
	
	/**
	 * 
	 * @param login
	 * @param password
	 * @return
	 */
	public boolean loginUser(String login, String password){
		boolean succesLogin = false;
		try {
			con = DriverManager.getConnection(url, user, passwordToDatabase);			
			st = con.createStatement();
			rs = st.executeQuery("SELECT * FROM users WHERE name='"+login+"' AND password='"+password+"'");
			
			rs.beforeFirst();
			if (rs.next()) {
				System.out.println(rs.getString(1));
				succesLogin = true;
			} else {				
				System.out.println("No data");
				succesLogin = false;
			}
			
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
			ex.printStackTrace();
			succesLogin = false;
		} finally {
			try {

				if (rs != null) {
					rs.close();
				}

				if (st != null) {
					st.close();
				}

				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {

				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
		return succesLogin;
	}
	
	public boolean registerUser(String login, String password){
		boolean successRegister = false;
		try {
			con = DriverManager.getConnection(url, user, passwordToDatabase);			
			st = con.createStatement();
			rs = st.executeQuery("SELECT COUNT(idusers) FROM users");
			
			rs.beforeFirst();
			if (rs.next()) {
				System.out.println(rs.getString(1));
			}
			
			int idUser = rs.getInt(1);
			
			pst = (PreparedStatement) con.prepareStatement(
					//"INSERT INTO users (idusers, name, password, gamesPlayed, gamesWon, gamesLost, rank) VALUES ('2', '"+login+"', '"+password+"', '0', '0', '0', '0')");
					"INSERT INTO users (idusers, name, password, gamesPlayed, gamesWon, gamesLost, rank) VALUES ("+idUser+", '"+login+"', '"+password+"', '0', '0', '0', '0')");
			
			pst.execute();
			
			successRegister = true;
			
			//rs = st.executeQuery(
			//		"INSERT INTO 'eoh'.'users' (`name`, `password`, `gamesPlayed`, `gamesWon`, `gamesLost`, `rank`) VALUES ('"+login+"', '"+password+"', '0', '0', '0', '0')");
					//"SELECT * FROM eoh.users");
//			if (rs.next()) {
//				System.out.println(rs.getString(1));
//			}
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
			ex.printStackTrace();
		} finally {
			try {

				if (rs != null) {
					rs.close();
				}

				if (st != null) {
					st.close();
				}

				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {

				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
		return successRegister;
	}

	/**
	 * 
	 * @param login
	 * @param password
	 * @return
	 */
	public void statisticOfPlayer(String login, ClientConnection cC){
		
		try {
			con = DriverManager.getConnection(url, user, passwordToDatabase);			
			st = con.createStatement();
			rs = st.executeQuery("SELECT * FROM users WHERE name='" + login + "'");
			
			rs.beforeFirst();
			if (rs.next()) {
				//System.out.println(rs.getString(1));
				//System.out.println(rs.getString(2));
				//System.out.println(rs.getString(3));
				//System.out.println(rs.getString(4));
				//System.out.println(rs.getString(5));
				//System.out.println(rs.getString(6));		
				
				PlayerStatsRequest playerStatsRequest = new PlayerStatsRequest();
				playerStatsRequest.gamesPlayed = rs.getInt(4);
				playerStatsRequest.gamesWon = rs.getInt(5);
				playerStatsRequest.gamesLost = rs.getInt(6);
				playerStatsRequest.rank = rs.getInt(7);
				playerStatsRequest.Login = login;
				
				sM.server.sendToTCP(cC.getID(), playerStatsRequest);				
			} else {				
				System.out.println("No data");				
			}
			
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
			ex.printStackTrace();			
		} finally {
			try {

				if (rs != null) {
					rs.close();
				}

				if (st != null) {
					st.close();
				}

				if (con != null) {
					con.close();
				}

			} catch (SQLException ex) {

				Logger lgr = Logger.getLogger(Database.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}
	
	/**
	 * Changing statistic of player
	 * @param login Player to change statistic 
	 */
	public void changeStatisticOfPlayer(String login, ClientConnection cC, int gamesPlayed, int gamesWon, int gamesLost){
		try {
			con = DriverManager.getConnection(url, user, passwordToDatabase);			
			st = con.createStatement();
			
			con.setAutoCommit(false);
			
			st.executeUpdate("UPDATE users SET gamesPlayed = gamesPlayed +'" + gamesPlayed + 
					"' WHERE name = '" + login +"'");
			st.executeUpdate("UPDATE users SET gamesWon = gamesWon +'" + gamesWon + 
					"' WHERE name = '" + login +"'");
			st.executeUpdate("UPDATE users SET gamesLost = gamesLost +'" + gamesLost + 
					"' WHERE name = '" + login +"'");
			
			con.commit();
		} catch (SQLException ex) {
			if (con != null){
				try {
					con.rollback();
				} catch (SQLException ex1) {
					Logger lgr = Logger.getLogger(Database.class.getName());
                    lgr.log(Level.WARNING, ex1.getMessage(), ex1);
				}
			}
			
			Logger lgr = Logger.getLogger(Database.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
			ex.printStackTrace();
			
		} finally {
			
			try {
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(Database.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
                ex.printStackTrace();
            }
		}
	}
}
