package com.mygdx.eoh.mapEditor;

import java.io.Serializable;

/**
 *
 * Created by v on 2016-11-21.
 */
public class MapFile implements Serializable{

    private static final long serialVersionUID = 1L;
    public String nameOfMap;
    public int mapColumns = 0;
    public int mapRows = 0;
    public Field[][] fields;

    private boolean isMapLoaded;

    public MapFile (int columns, int rows){
        fields = new Field[columns][rows];
        for (int i = 0; i < columns; i ++){
            for (int j = 0; j < rows; j ++) {
                fields[i][j] = new Field();
            }
        }
    }

    public MapFile(){

    }

    public static class Field implements Serializable{
        private static final long serialVersionUID = 1L;
        public boolean player1StartLocation = false;
        public boolean player2StartLocation = false;        
        public boolean player3StartLocation = false;
        public boolean player4StartLocation = false;
        public boolean mobSkeletonLocation = false;
        public boolean mobWolfLocation = false;
        public boolean mobZombieLocation = false;
        public boolean mobSpiderLocation = false;
        public boolean mobRandomLevel1 = false;
        public boolean mobRandomLevel2 = false;
        public boolean terrainForest = false;
        public boolean terrainMountain = false;
        public boolean terrainRiver = false;
        public boolean terrainGrass = false;
        public boolean tresureBoxLvl1 = false;
        public boolean tresureBoxLvl2 = false;
        public boolean towerMagic = false;
        public boolean towerWisdom = false;
        public boolean towerDefence = false;
        public boolean towerSpeed = false;
        public boolean towerAttack = false;
        public boolean towerHp = false;
        public boolean towerWell = false;
        public boolean towerHospital = false;
        public boolean itemGold = false;
        public boolean manaPotion = false;
        public boolean healthPotion = false;
        
        public Terrains terrains;
        public int coordinateXonMap;
        public int coordinateYonMap;
    }

    public enum Terrains implements Serializable{
        Grass, Mountain, Forest, River
    }

//    public boolean isMapLoaded() {
//        return isMapLoaded;
//    }

    public void setMapLoaded(boolean mapLoaded) {
        isMapLoaded = mapLoaded;
    }
}
