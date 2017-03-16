package com.v.server.desktop;

import java.util.ArrayList;

import com.v.server.desktop.Network.FoundEnemy;

public class BattleManager {

	private ServerManager sM;

	public BattleManager(ServerManager sM) {
		this.sM = sM;
	}

	/**
	 * Creates a battle with specified Client Connection object. Method looking
	 * for free enemy in arrayList cCreadytoBattle.
	 * 
	 * @param cC
	 *            Client Connection object.
	 */
	public void createBattle(ClientConnection cC) {
		outerLoop: if (sM.getcCreadyToBattle().size() > 1) {
			for (ClientConnection cCtmp : sM.getcCreadyToBattle()) {
				if (!cCtmp.equals(cC)) {
					if (cCtmp.countOfPlayers.equals(cC.countOfPlayers) && cCtmp.gameTypes.equals(cC.gameTypes)) {
						System.out.println("I found an enemy! Sending message to ID:" + cC.getID());

						FoundEnemy foundEnemy = new FoundEnemy();
						
						foundEnemy.enemyLogin = cCtmp.login;
						foundEnemy.enemyId = cCtmp.getID();
						foundEnemy.playerNumber = 2;
						foundEnemy.playerMobClass = cCtmp.playerMobClass;
						cC.enemyID = cCtmp.getID(); 
						sM.server.sendToTCP(cC.getID(), foundEnemy);

						foundEnemy.enemyLogin = cC.login;
						foundEnemy.enemyId = cC.getID();
						foundEnemy.playerNumber = 1;
						foundEnemy.playerMobClass = cC.playerMobClass;
						cCtmp.enemyID = cC.getID();
						sM.server.sendToTCP(cCtmp.getID(), foundEnemy);

						sM.getcCbusy().add(cCtmp);
						sM.getcCbusy().add(cC);

						removeFromCcArrayList(cC, sM.getcCreadyToBattle());
						removeFromCcArrayList(cCtmp, sM.getcCreadyToBattle());
						break outerLoop;
					}
				}
			}
		}
	}
	
	public void createBattleFourPlayersFfa(ArrayList<ClientConnection> clientConnections){
		for (int i = 0; i < 4; i ++){
			FoundEnemy foundEnemy1 = new FoundEnemy();
			FoundEnemy foundEnemy2 = new FoundEnemy();
			FoundEnemy foundEnemy3 = new FoundEnemy();
			
			if (i == 0){
				foundEnemy1.enemyId = clientConnections.get(1).getID();
				foundEnemy1.enemyLogin = clientConnections.get(1).login;
				sM.server.sendToTCP(clientConnections.get(0).getID(), foundEnemy1);
				foundEnemy2.enemyId = clientConnections.get(2).getID();
				foundEnemy2.enemyLogin = clientConnections.get(2).login;
				sM.server.sendToTCP(clientConnections.get(0).getID(), foundEnemy2);
				foundEnemy3.enemyId = clientConnections.get(3).getID();
				foundEnemy3.enemyLogin = clientConnections.get(3).login;
				sM.server.sendToTCP(clientConnections.get(0).getID(), foundEnemy3);				
			}
			
			if (i == 1){
				foundEnemy1.enemyId = clientConnections.get(0).getID();
				foundEnemy1.enemyLogin = clientConnections.get(0).login;
				sM.server.sendToTCP(clientConnections.get(1).getID(), foundEnemy1);
				foundEnemy2.enemyId = clientConnections.get(2).getID();
				foundEnemy2.enemyLogin = clientConnections.get(2).login;
				sM.server.sendToTCP(clientConnections.get(1).getID(), foundEnemy2);
				foundEnemy3.enemyId = clientConnections.get(3).getID();
				foundEnemy3.enemyLogin = clientConnections.get(3).login;
				sM.server.sendToTCP(clientConnections.get(1).getID(), foundEnemy3);
			}
			
			if (i == 2){
				foundEnemy1.enemyId = clientConnections.get(0).getID();
				foundEnemy1.enemyLogin = clientConnections.get(0).login;
				sM.server.sendToTCP(clientConnections.get(2).getID(), foundEnemy1);
				foundEnemy2.enemyId = clientConnections.get(1).getID();
				foundEnemy2.enemyLogin = clientConnections.get(1).login;
				sM.server.sendToTCP(clientConnections.get(2).getID(), foundEnemy2);
				foundEnemy3.enemyId = clientConnections.get(3).getID();
				foundEnemy3.enemyLogin = clientConnections.get(3).login;
				sM.server.sendToTCP(clientConnections.get(2).getID(), foundEnemy3);
			}
			
			if (i == 3){
				foundEnemy1.enemyId = clientConnections.get(0).getID();
				foundEnemy1.enemyLogin = clientConnections.get(0).login;
				sM.server.sendToTCP(clientConnections.get(3).getID(), foundEnemy1);
				foundEnemy2.enemyId = clientConnections.get(1).getID();
				foundEnemy2.enemyLogin = clientConnections.get(1).login;
				sM.server.sendToTCP(clientConnections.get(3).getID(), foundEnemy2);
				foundEnemy3.enemyId = clientConnections.get(2).getID();
				foundEnemy3.enemyLogin = clientConnections.get(2).login;
				sM.server.sendToTCP(clientConnections.get(3).getID(), foundEnemy3);
			}
		}
		
		for (int i = 0; i < 4; i ++){
			sM.getcCbusy().add(clientConnections.get(i));
		}		
		clientConnections.clear();		
	}
	
	public void createBattleFourPlayersTwoTeams(ArrayList<ClientConnection> clientConnections){
		
	}

	/**
	 * Removes specified client connection from array.
	 * 
	 * @param cC
	 */
	public void cancelBattle(ClientConnection cC) {
		removeFromCcArrayList(cC, sM.getcCreadyToBattle());
	}

	/**
	 * Removes Client Connection object from specified array list of
	 * ClientConnections
	 * 
	 * @param cC
	 *            object of Client Connection class.
	 * @param array
	 *            Array where client connections may be removed.
	 */
	private void removeFromCcArrayList(ClientConnection cC, ArrayList<ClientConnection> array) {
		int index = 0;
		boolean found = false;

		outerLoop: for (ClientConnection tmpCc : array) {
			if (cC.equals(tmpCc)) {
				found = true;
				break outerLoop;
			}
			index += 1;
		}

		if (found) {
			array.remove(index);
		}
	}
}
