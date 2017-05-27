package com.v.server.desktop;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import com.badlogic.gdx.files.FileHandle;
import com.mygdx.eoh.mapEditor.MapFile;

public class MapManager {
	
	public MapFile mapFile;
	
	public MapManager(){
	}
	
	public void loadMap(){
				
		//Comment
		//FileHandle file = new FileHandle("D:/s01.map");
		FileHandle file = new FileHandle("/home/eohServer/s01.map");
		//FileHandle file = new FileHandle("/home/wowvaqa/java/eohserver/s01.map");
		
		try {
			mapFile = (MapFile) deserialize(file.readBytes());
			System.out.println("Map successfully loaded.");
		} catch (ClassNotFoundException e) {
			System.out.println("Map load filed");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Map load filed");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public MapFile getMapFile() {
		return mapFile;
	}

	public void setMapFile(MapFile mapFile) {
		this.mapFile = mapFile;
	}
	
	public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        ObjectInputStream o = new ObjectInputStream(b);
        return o.readObject();
    }

}
