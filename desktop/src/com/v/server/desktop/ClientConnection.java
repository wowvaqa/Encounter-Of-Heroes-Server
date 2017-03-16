package com.v.server.desktop;

import com.esotericsoftware.kryonet.Connection;
import com.v.server.desktop.Network.CountOfPlayers;
import com.v.server.desktop.Network.GameTypes;

public class ClientConnection extends Connection{
	public String login = null;
	public GameTypes gameTypes;
	public CountOfPlayers countOfPlayers;
	public int enemyID;
	public int playerMobClass;
		
}
