package com.v.server.desktop;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.mygdx.eoh.mapEditor.MapFile;
import com.mygdx.eoh.mapEditor.MapFile.Field;
import com.mygdx.eoh.mapEditor.MapFile.Terrains;

public class Network {

    static public void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();

        kryo.register(BuyPlayerMob.class);
        kryo.register(ChangePlayerStatistic.class);
        kryo.register(PlayerStatsRequest.class);
        kryo.register(Unlog.class);
        kryo.register(Victory.class);
        kryo.register(DisconnonectedFromBattle.class);
        kryo.register(PlayersOnlineAnswer.class);        
        kryo.register(RegisterUser.class);
        kryo.register(RegisterUserAnswer.class);
        kryo.register(LoginUser.class);
        kryo.register(LoginSuccessAnswer.class);
        kryo.register(MapFile.class);
        kryo.register(Field.class);
        kryo.register(Field[].class);
        kryo.register(Field[][].class);
        kryo.register(Terrains.class);
        kryo.register(StartBattle.class);
        kryo.register(FoundEnemy.class);
        kryo.register(CancleBattle.class);
        kryo.register(MovePlayerMob.class);
        kryo.register(AttackPlayerMob.class);
        kryo.register(NextTurn.class);
        kryo.register(CancleBattle.class);
        kryo.register(MovePlayerMob.class);
        kryo.register(CountOfPlayers.class);
        kryo.register(GameTypes.class);
        kryo.register(InstantEffectNet.class);
        kryo.register(SpellCastNet.class);
        kryo.register(ClientReadyToStartBattle.class);        
        kryo.register(EquipRemove.class);      
        kryo.register(EquipAssumeCancel.class);
        kryo.register(EquipAssume.class);
        kryo.register(CreateEquip.class);
        kryo.register(ChangeAtributes.class);
    }

    /**
     * Buying new player Mob
     */
    static public class BuyPlayerMob{
    	public int enemyID;
    	public int enemyClass;
    	public int locXofCastleOnMap;
        public int locYofCastleOnMap;
    }
    
    static public class ChangePlayerStatistic{
        public String Login;
        public int gamesWon;
        public int gamesLost;
        public int gamesPlayed;
    }
    
    static public class PlayerStatsRequest{
        public String Login;
        public int gamesWon;
        public int gamesLost;
        public int gamesPlayed;
        public int rank;
    }
    
    static public class Unlog{

    }
    
    static public class Victory{
    	public int enemyID;
    }
    
    static public class DisconnonectedFromBattle{

    }
    
    static public class PlayersOnlineAnswer{
        public int amountOfOnlinePlayers;
    }
    
    /**
     * Registering new player.
     */
    static public class RegisterUser {
        public String login;
        public String password;
    }
    
    static public class RegisterUserAnswer {
    	public boolean registerSucces;
    }

    /**
     * Login player
     * @author v
     */
    static public class LoginUser {
        public String login;
        public String password;
    }
    
    static public class LoginSuccessAnswer {
    	public boolean loginSucces;
    	public String login;
    }
    
    
    static public class StartBattle {
    	public GameTypes gameTypes;
        public CountOfPlayers countOfPlayers;
        public int playerMobClass;
    }
    
    static public class FoundEnemy {
    	public String enemyLogin;
    	public int enemyId;
    	public int playerNumber;
    	public int playerMobClass;
    }
    
    static public class CancleBattle {

    }
    
    static public class MovePlayerMob{
    	public int enemyId;
    	public int inedxPlayerOwner;
    	public int indexInArray;
    	public int amountXmove;
    	public int amountYmove;
    }
    
    static public class AttackPlayerMob {
        public int enemyId;
        public int indexPlayerOwner;
        public int indexInArray;
        public int locationXofEnemy;
        public int locationYofEnemy;
        public int locationXofAttacker;
        public int locationYofAttacker;
        public int damage;
        public int hpLeft;
    }
    
    static public class InstantEffectNet{
        public int enemyId;
        public int instantEffectNumber;
        public int damage;
        public int locationXofDefender;
        public int locationYofDefender;
        public int locationXofCaster;
        public int locationYofCaster;
    }
    
    static public class SpellCastNet{
        public int enemyId;
        public int locationXofCaster;
        public int locationYofCaster;
        public int spellManaCost;
    }
    
    static public class NextTurn {
    	public int enemyId;
    	public int playerIndex;
    }
    
    static public class ClientReadyToStartBattle {
        public int enemyId;        
    }
    
    static public class EquipRemove{
    	public int enemyId;    	
    	public int locationXofPlayerMob;
    	public int locationYofPlayerMob;
    	public int equipIndex;    	
    }
    
    static public class EquipAssumeCancel{
    	public int enemyId;
    	public int locationXofPlayerMob;
    	public int locationYofPlayerMob; 
    }
    
    static public class EquipAssume{
    	public int enemyId;
    	public int locationXofPlayerMob;
    	public int locationYofPlayerMob; 
    }
    
    static public class CreateEquip{
    	public int enemyId;
    	public int locXofPlayerMob;
    	public int locYofPlayerMob;
    	public int equipKind;
    }
    
    static public class ChangeAtributes {
    	public int enemyId;
        public int playerIndex;
        public int playerMobIndex;
        public int attack;
        public int actualAttack;
        public int defence;
        public int actualDefence;
        public int speed;
        public int actualSpeed;
        public int power;
        public int actualPower;
        public int wisdom;
        public int actualWisdom;
        public int maxHp;
        public int actualHp;
        public int exp;
        public int expToNextLevel;
        public int level;
    }
    
    static public enum GameTypes{
    	freeForAll, twoTeams;
    }
    
    static public enum CountOfPlayers{
    	one, two, three, four;
    }
}