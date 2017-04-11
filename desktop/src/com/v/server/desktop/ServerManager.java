package com.v.server.desktop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.v.server.desktop.Network.AttackPlayerMob;
import com.v.server.desktop.Network.BuyPlayerMob;
import com.v.server.desktop.Network.ChangePlayerStatistic;
import com.v.server.desktop.Network.ClientReadyToStartBattle;
import com.v.server.desktop.Network.CountOfPlayers;
import com.v.server.desktop.Network.CreateEquip;
import com.v.server.desktop.Network.DisconnonectedFromBattle;
import com.v.server.desktop.Network.EquipAssume;
import com.v.server.desktop.Network.EquipAssumeCancel;
import com.v.server.desktop.Network.EquipRemove;
import com.v.server.desktop.Network.GameTypes;
import com.v.server.desktop.Network.InstantEffectNet;
import com.v.server.desktop.Network.LoginSuccessAnswer;
import com.v.server.desktop.Network.MovePlayerMob;
import com.v.server.desktop.Network.PlayerStatsRequest;
import com.v.server.desktop.Network.PlayersOnlineAnswer;
import com.v.server.desktop.Network.RegisterUserAnswer;
import com.v.server.desktop.Network.SpellCastNet;
import com.v.server.desktop.Network.StartBattle;
import com.v.server.desktop.Network.Unlog;
import com.v.server.desktop.Network.Victory;


public class ServerManager {
	public Server server;
	public Database database;
	// Array list with all connections
	private ArrayList<ClientConnection> cC = new ArrayList<ClientConnection>();
	// Array list with legged client connections
	private ArrayList<ClientConnection> cCLogged = new ArrayList<ClientConnection>();
	// Array list with disconnected connections
	private ArrayList<ClientConnection> cCdisconnected = new ArrayList<ClientConnection>();
	// Array list with connections ready to 1v1 battle
	private ArrayList<ClientConnection> cCreadyToBattle = new ArrayList<ClientConnection>();
	private ArrayList<ClientConnection> cCreadyFfaFourPlayers = new ArrayList<ClientConnection>();
	private ArrayList<ClientConnection> cCreadyTwoTeamsFourPlayers = new ArrayList<ClientConnection>();
	//private ArrayList<ClientConnection> cCreadyTtFourPlayers = new ArrayList<ClientConnection>();
	// Array List with busy connections - when connection is in battle etc.
	private ArrayList<ClientConnection> cCbusy = new ArrayList<ClientConnection>();
	private Scanner scn = new Scanner(System.in);
	private String cmd;
	private MapManager mapManager;
	private BattleManager battleManager;

	public ServerManager() {
		battleManager = new BattleManager(this);
		autoRun();
		showMenu();
	}
	
	public void autoRun(){
		runServer();
		connectDatabase();
	}

	public void showMenu() {
		System.out.println("1. Start Server");
		System.out.println("2. Stop Server");
		System.out.println("3. Connect database");
		System.out.println("4. Show connections");
		System.out.println("5. Load map");
		System.out.println("0. Exit");
		loop();
	}

	public void loop() {
		cmd = scn.nextLine();

		if (!cmd.equals("0")) {
			if (cmd.equals("1")) {
				runServer();
				showMenu();
			}
			if (cmd.equals("2")) {
				server.stop();
				showMenu();
			}
			if (cmd.equals("3")) {
				connectDatabase();
				showMenu();
			}
			if (cmd.equals("4")) {
				showConnections();
				showMenu();
			}
			if (cmd.equals("5")) {
				mapManager = new MapManager();
				mapManager.loadMap();
				server.sendToAllTCP(mapManager.getMapFile());
				showMenu();
			}
		}
	}

	public void runServer() {

		server = new Server(1024 * 10, 1024 * 10) {
			protected Connection newConnection() {
				return new ClientConnection();
			}
		};

		Network.register(server);
		server.addListener(new Listener() {
						
			/*****************************************************************************************
			 * DISCONNECION
			 *****************************************************************************************/
			@Override
			public void disconnected(Connection connection) {				
				super.disconnected(connection);
				ClientConnection disconnectedClientConnection = (ClientConnection) connection;
				
				cCLogged.remove(disconnectedClientConnection);						
				cCreadyToBattle.remove(disconnectedClientConnection);
				System.out.println("Legged Players Size: " + cCLogged.size());
				
				PlayersOnlineAnswer playersOnlineAnswer = new PlayersOnlineAnswer();
				playersOnlineAnswer.amountOfOnlinePlayers = cCLogged.size();
				server.sendToAllTCP(playersOnlineAnswer);
				
				if (cCbusy.contains(disconnectedClientConnection)){
					DisconnonectedFromBattle disconnonectedFromBattle = new DisconnonectedFromBattle();
					server.sendToTCP(disconnectedClientConnection.enemyID, disconnonectedFromBattle);					
					System.out.println("Usuni�cie po��czenia z listy cCbusy");										
				}
				cCbusy.remove(disconnectedClientConnection);
			}

			@Override
			public void received(Connection connection, Object object) {
				super.received(connection, object);

				ClientConnection clientConnection = (ClientConnection) connection;

				/*****************************************************************************************
				 * REGISTER USER
				 *****************************************************************************************/
				if (object instanceof Network.RegisterUser) {
					System.out.println("Recived 'RegisterUser'");
					Network.RegisterUser registerUser = (Network.RegisterUser) object;
					System.out.println("Login: " + registerUser.login);
					System.out.println("Password: " + registerUser.password);

					String login = registerUser.login;
					String password = registerUser.password;

					if (((Database) database).registerUser(login, password)) {
						RegisterUserAnswer registerUserAnswer = new RegisterUserAnswer();
						registerUserAnswer.registerSucces = true;
						server.sendToTCP(clientConnection.getID(), registerUserAnswer);
					} else {
						RegisterUserAnswer registerUserAnswer = new RegisterUserAnswer();
						registerUserAnswer.registerSucces = false;
						server.sendToTCP(clientConnection.getID(), registerUserAnswer);
					}

					return;
				}
				
				/*****************************************************************************************
				 * LOGIN USER
				 *****************************************************************************************/
				if (object instanceof Network.LoginUser) {
					System.out.println("Recived User Login");
					Network.LoginUser loginUser = (Network.LoginUser) object;
					System.out.println("Login: " + loginUser.login);
					System.out.println("Password: " + loginUser.password);

					String login = loginUser.login;
					String password = loginUser.password;

					if (((Database) database).loginUser(login, password)) {
						
						// Remove old login of client from logged players.
						if (cCLogged.contains(clientConnection)){
							cCLogged.remove(clientConnection);
						}
						
						// Remove same logged users from different machine.
						for (ClientConnection cCtmp: cCLogged){
							if (cCtmp.login.equals(loginUser.login)){
								
								Unlog unlog = new Unlog();
								server.sendToTCP(cCtmp.getID(), unlog);
								
								cCLogged.remove(cCtmp);																
								break;
							}
						}
												
						// Add client to logged list.
						cCLogged.add(clientConnection);
						
						clientConnection.login = login;
						
						// Send info to client about successes login.
						LoginSuccessAnswer loginSuccessAnswer = new LoginSuccessAnswer();
						loginSuccessAnswer.loginSucces = true;
						loginSuccessAnswer.login = login;
						server.sendToTCP(clientConnection.getID(), loginSuccessAnswer);
						
						// Create map to send
						mapManager = new MapManager();
						mapManager.loadMap();
						server.sendToTCP(clientConnection.getID(), mapManager.getMapFile());
						
						// Inform new logged client about how many players are logged.
						PlayersOnlineAnswer playersOnlineAnswer = new PlayersOnlineAnswer();
						playersOnlineAnswer.amountOfOnlinePlayers = cCLogged.size();
						server.sendToAllTCP(playersOnlineAnswer);				
					} else {
						// Inform client about failed login attempt
						LoginSuccessAnswer loginSuccessAnswer = new LoginSuccessAnswer();
						loginSuccessAnswer.loginSucces = false;
						server.sendToTCP(clientConnection.getID(), loginSuccessAnswer);							
					}

					return;
				}

				/*****************************************************************************************
				 * START BATTLE
				 *****************************************************************************************/
				if (object instanceof Network.StartBattle) {
					System.out.println("Player: " + clientConnection.getID() + " sending Start Battle");
										
					if (((StartBattle) object).gameTypes.equals(GameTypes.freeForAll)){
						System.out.println("Game type: Free for All");						
					} else if (((StartBattle) object).gameTypes.equals(GameTypes.twoTeams)){
						System.out.println("Game type: Two teams");
					} 
					
					if (((StartBattle) object).countOfPlayers.equals(CountOfPlayers.two)){
						System.out.println("Players: 2");
					} else if (((StartBattle) object).countOfPlayers.equals(CountOfPlayers.four)){
						System.out.println("Players: 4");
						if (((StartBattle) object).gameTypes.equals(GameTypes.freeForAll)){
							cCreadyFfaFourPlayers.add(clientConnection);
							if (cCreadyFfaFourPlayers.size() == 4){
								battleManager.createBattleFourPlayersFfa(cCreadyFfaFourPlayers);
								return;
							}
							return;
						} else if (((StartBattle) object).gameTypes.equals(GameTypes.twoTeams)){
							cCreadyTwoTeamsFourPlayers.add(clientConnection);
							if (cCreadyTwoTeamsFourPlayers.size() == 4){
								
							}
							return;
						}
					}
					
					clientConnection.countOfPlayers = ((StartBattle) object).countOfPlayers;
					clientConnection.gameTypes = ((StartBattle) object).gameTypes;
					clientConnection.playerMobClass = ((StartBattle) object).playerMobClass;
					
					cCreadyToBattle.add(clientConnection);
					battleManager.createBattle(clientConnection);
					return;
				}
				
				/*****************************************************************************************
				 * CANCEL BATTLE
				 *****************************************************************************************/
				if (object instanceof Network.CancleBattle) {
					System.out.println("Player: " + clientConnection.getID() + " sending Cancel Battle");
					battleManager.cancelBattle(clientConnection);
					return;
				}
				
				/*****************************************************************************************
				 * MOVE PLAYER
				 *****************************************************************************************/
				if (object instanceof Network.MovePlayerMob) {
					System.out.println("Enemy ID: " + ((MovePlayerMob)object).enemyId );
					System.out.println("X: " + ((MovePlayerMob)object).amountXmove );
					System.out.println("Y: " + ((MovePlayerMob)object).amountYmove );
					System.out.println("Index of PlayerMob: " + ((MovePlayerMob)object).indexInArray );
					System.out.println("Index of Player: " + ((MovePlayerMob)object).inedxPlayerOwner );
					server.sendToTCP(((MovePlayerMob)object).enemyId, object);
					return;
				}
				
				/*****************************************************************************************
				 * ATTACK OF PLAYER
				 *****************************************************************************************/
				if (object instanceof Network.AttackPlayerMob) {
					System.out.println("Enemy ID: " + ((AttackPlayerMob)object).enemyId );
					System.out.println("Location X of Enemy: " + ((AttackPlayerMob)object).locationXofEnemy );
					System.out.println("Location Y of Enemy: " + ((AttackPlayerMob)object).locationYofEnemy );
					System.out.println("Index of PlayerMob: " + ((AttackPlayerMob)object).indexInArray );
					System.out.println("Index of Player: " + ((AttackPlayerMob)object).indexPlayerOwner );
					System.out.println("Damage: " + ((AttackPlayerMob)object).damage );
					System.out.println("Hp Left: " + ((AttackPlayerMob)object).hpLeft );
					server.sendToTCP(((AttackPlayerMob)object).enemyId, object);
					return;
				}
				
				/*****************************************************************************************
				 * VICTORY
				 *****************************************************************************************/
				if (object instanceof Network.Victory){
					System.out.println("Odebrano zg�oszenie przgranej gracza");
					System.out.println("Enemy ID: " + ((Victory)object).enemyID);
					server.sendToTCP(((Victory)object).enemyID, object);
					return;
				}
				
				/*****************************************************************************************
				 * REQUEST FOR AMOUNT OF LOGGED PLAYERS
				 *****************************************************************************************/
				if (object instanceof Network.PlayersOnlineAnswer){
					System.out.println("Otrzymano zapytanie o ilo�� zalogowanych graczy");
					PlayersOnlineAnswer playersOnlineAnswer = new PlayersOnlineAnswer();
					playersOnlineAnswer.amountOfOnlinePlayers = cCLogged.size();
					server.sendToTCP(clientConnection.getID(), playersOnlineAnswer);
					return;
				}
				
				/*****************************************************************************************
				 * REQUEST FOR STATISTIC OF LOGGED PLAYER
				 *****************************************************************************************/
				if (object instanceof Network.PlayerStatsRequest){
					System.out.println("Otrzymano pro�b� o statystyki gracza " + 
							((PlayerStatsRequest) object).Login);				
					database.statisticOfPlayer(((PlayerStatsRequest) object).Login, clientConnection);
					return;
				}
				
				/*****************************************************************************************
				 * REQUEST FOR CHANGE OF PLAYER STATISTIC
				 *****************************************************************************************/
				if (object instanceof Network.ChangePlayerStatistic){
					System.out.println("Otrzymano pro�b� o zmian� statystyk gracza " + 
							((ChangePlayerStatistic) object).Login);
					System.out.println("Wygrane: " + ((ChangePlayerStatistic)object).gamesWon);
					System.out.println("Przegrane: " + ((ChangePlayerStatistic)object).gamesLost);
					System.out.println("Rozegrane: " + ((ChangePlayerStatistic)object).gamesPlayed);
										
					database.changeStatisticOfPlayer(
							((ChangePlayerStatistic) object).Login,
							clientConnection,
							((ChangePlayerStatistic)object).gamesPlayed,
							((ChangePlayerStatistic)object).gamesWon,
							((ChangePlayerStatistic)object).gamesLost);
					return;
				}
				
				/*****************************************************************************************
				 * REQUEST FOR CHANGE OF PLAYER STATISTIC
				 *****************************************************************************************/
				if (object instanceof Network.BuyPlayerMob){
					System.out.println("Otrzymano info o tworzeniu nowego bohatera");
					System.out.println("Lokacja X zamku na mapie: " + ((BuyPlayerMob)object).locXofCastleOnMap);
					System.out.println("Lokacja Y zamku na mapie: " + ((BuyPlayerMob)object).locYofCastleOnMap);
					System.out.println("EnemyID: " + ((BuyPlayerMob)object).enemyID);
					System.out.println("Enemy class: " + ((BuyPlayerMob)object).enemyClass);
					
					server.sendToTCP(((BuyPlayerMob)object).enemyID, object);
					
					return;
				}
				
				/*****************************************************************************************
				 * INSTANT EFFECT
				 *****************************************************************************************/
				if (object instanceof Network.InstantEffectNet){
					System.out.println("Otrzymano natychmiastowy efekt od klienta.");
					System.out.println("Lokacja X obiektu dzia�ania efektu: " + ((InstantEffectNet)object).locationXofDefender);
					System.out.println("Lokacja Y obiektu dzia�ania efektu: " + ((InstantEffectNet)object).locationYofDefender);
					System.out.println("Lokacja X obiektu rzucajacego czar: " + ((InstantEffectNet)object).locationXofCaster);
					System.out.println("Lokacja Y obiektu rzucajacego czar: " + ((InstantEffectNet)object).locationYofCaster);
					System.out.println("Obra�enia dla obiektu atakowanego: " + ((InstantEffectNet)object).damage);
					System.out.println("Numer efektu natychmiastowego: " + ((InstantEffectNet)object).instantEffectNumber);
					server.sendToTCP(((InstantEffectNet)object).enemyId, object);
				}
				
				/*****************************************************************************************
				 * SPELL CAST
				 *****************************************************************************************/
				if (object instanceof Network.SpellCastNet){
					System.out.println("Otrzymano rzucenie czaru.");
					System.out.println("Lokacja X rzucaj�cego czar: " + ((SpellCastNet)object).locationXofCaster);
					System.out.println("Lokacja Y rzucaj�cego czar: " + ((SpellCastNet)object).locationYofCaster);
					System.out.println("Koszt czaru: " + ((SpellCastNet)object).spellManaCost);
					System.out.println("EnemyID: " + ((SpellCastNet)object).enemyId);
					server.sendToTCP(((SpellCastNet)object).enemyId, object);
				}
				
				/*****************************************************************************************
				 * CLIENT READY TO START BATTLE
				 *****************************************************************************************/
				if (object instanceof Network.ClientReadyToStartBattle){
					System.out.println("Otrzymano zg�oszenie od klienta o gotowo�ci do rozpocz�cia potyczki.");
					System.out.println("EnemyID: " + ((ClientReadyToStartBattle)object).enemyId);
					server.sendToTCP(((ClientReadyToStartBattle)object).enemyId, object);
				}
				/*****************************************************************************************
				 * EQUIP REMOVE
				 *****************************************************************************************/
				if (object instanceof Network.EquipRemove){
					System.out.println("Otrzymano EquipRemove");
					System.out.println("Enemy ID: " + ((EquipRemove)object).enemyId);
					System.out.println("Equip Index: " + ((EquipRemove)object).equipIndex);
					System.out.println("Loc X of plyerMob: " + ((EquipRemove)object).locationXofPlayerMob);
					System.out.println("Loc Y of plyerMob: " + ((EquipRemove)object).locationYofPlayerMob);
					server.sendToTCP(((EquipRemove)object).enemyId, object);
				}
				/*****************************************************************************************
				 * EQUIP ASSUME CANCEL
				 *****************************************************************************************/
				if (object instanceof Network.EquipAssumeCancel){
					System.out.println("Otrzymano EquipAssumeCancel");
					System.out.println("Enemy ID: " + ((EquipAssumeCancel)object).enemyId);
					System.out.println("Loc X of plyerMob: " + ((EquipAssumeCancel)object).locationXofPlayerMob);
					System.out.println("Loc Y of plyerMob: " + ((EquipAssumeCancel)object).locationYofPlayerMob);
					server.sendToTCP(((EquipAssumeCancel)object).enemyId, object);
				}
				/*****************************************************************************************
				 * EQUIP ASSUME 
				 * *****************************************************************************************/
				if (object instanceof Network.EquipAssume){
					System.out.println("Otrzymano EquipAssume");
					System.out.println("Enemy ID: " + ((EquipAssume)object).enemyId);
					System.out.println("Loc X of plyerMob: " + ((EquipAssume)object).locationXofPlayerMob);
					System.out.println("Loc Y of plyerMob: " + ((EquipAssume)object).locationYofPlayerMob);
					server.sendToTCP(((EquipAssume)object).enemyId, object);
				}
				/*****************************************************************************************
				 * EQUIP CREATE 
				 * *****************************************************************************************/
				if (object instanceof CreateEquip){
					System.out.println("Otrzymano EquipCreate");
					System.out.println("Enemy ID: " + ((Network.CreateEquip)object).enemyId);
					System.out.println("Loc X of plyerMob: " + ((Network.CreateEquip)object).locXofPlayerMob);
					System.out.println("Loc Y of plyerMob: " + ((Network.CreateEquip)object).locYofPlayerMob);
					System.out.println("Equip Kind: " + ((Network.CreateEquip)object).equipKind);
					server.sendToTCP(((Network.CreateEquip)object).enemyId, object);
				}
			}
		});

		server.start();
		try {
			server.bind(54555, 54777);
			System.out.println("Server started!");
		} catch (IOException e) {
			System.out.println("Error starting server");
			e.printStackTrace();
		}
	}

	public void connectDatabase() {
		database = new Database(this);
		((Database) database).connect();
	}

	/**
	 * Shows all players connected to server
	 */
	public void showConnections() {
		if (server != null) {
			Connection[] connections = server.getConnections();

			System.out.println("Connected users:");

			if (connections.length == 0) {
				System.out.println("No one is connected");
			} else {
				for (int i = connections.length - 1; i >= 0; i--) {
					ClientConnection connection = (ClientConnection) connections[i];
					System.out.print(connections[i].getID() + " ");
					if (connection.login != null) {
						System.out.print(connection.login);
					}
					System.out.println("");
				}
				System.out.println("");
			}
		}
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public ArrayList<ClientConnection> getcC() {
		return cC;
	}

	public void setcC(ArrayList<ClientConnection> cC) {
		this.cC = cC;
	}

	public ArrayList<ClientConnection> getcCreadyToBattle() {
		return cCreadyToBattle;
	}

	public void setcCreadyToBattle(ArrayList<ClientConnection> cCreadyToBattle) {
		this.cCreadyToBattle = cCreadyToBattle;
	}

	public ArrayList<ClientConnection> getcCbusy() {
		return cCbusy;
	}

	public void setcCbusy(ArrayList<ClientConnection> cCbusy) {
		this.cCbusy = cCbusy;
	}
	
	
}
