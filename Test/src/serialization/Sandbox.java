package serialization;

import java.util.ArrayList;
import java.util.List;

import serialization.containers.MSDatabase;
import serialization.containers.MSField;
import serialization.containers.MSObject;
import serialization.containers.MSString;

public class Sandbox {

	static class Level {
		
		private String name;
		private String path;
		private int width, height;
		private List<Entity> entities = new ArrayList<Entity>();
		
		public Level(String path){
			this.name = "level1";
			this.path = path;
			width = 64;
			height = 128;
		}
		
		private Level(){
		}
		
		public void add(Entity e){
			entities.add(e);
			e.init(this);
		}
		
		public void update(){
			
		}
		
		public void render(){
			
		}
		
		public void save(String path){
			MSDatabase database = new MSDatabase(name);
			
			MSObject object = new MSObject("LevelData");
			object.addString(MSString.Create("name", name));
			object.addString(MSString.Create("path", this.path));
			object.addField(MSField.Integer("width", width));
			object.addField(MSField.Integer("height", height));
			object.addField(MSField.Integer("entityCount", entities.size()));
			database.addObject(object);
			
			for(int i = 0; i < entities.size(); i++){
				Entity e = entities.get(i);
				
				MSObject entity = new MSObject("E" + i);
				byte type = 0;
				if(e instanceof Player)
					type = 1;
				entity.addField(MSField.Byte("type", type));
				entity.addField(MSField.Integer("arrayIndex", i));
				e.serialize(entity);	
				database.addObject(entity);
			}
			
			database.serializeToFile(path);
		}
		
		public static Level load(String path){
			MSDatabase database = MSDatabase.deserializeFromFile(path);
			MSObject levelData = database.findObject("LevelData");
			
			Level result = new Level();
			result.name = levelData.findString("name").getString();
			result.path = levelData.findString("path").getString();
			result.width = levelData.findField("width").getInt();
			result.height = levelData.findField("height").getInt();
			
			int entityCount = levelData.findField("entityCount").getInt();
			
			for(int i = 0; i < entityCount; i++){
				MSObject entity = database.findObject("E" + i);
				Entity e;
				if(entity.findField("type").getByte() == 1)
					e = Player.deserialize(entity);
				else
					e = Entity.deserialize(entity);
				result.add(e);
			}
			return result;
		}
	}
	
	static class Entity {
				
		protected int x, y;
		protected boolean removed = false;
		private Level level;
		
		public Entity(){
			x = 0;
			y = 0;
		}
		
		public void init(Level level){
			this.level = level;
		}
		
		public void serialize(MSObject object){
			object.addField(MSField.Integer("x", x));
			object.addField(MSField.Integer("y", y));
			object.addField(MSField.Boolean("removed", removed));
		}
		
		public static Entity deserialize(MSObject object){
			Entity result = new Entity();
			result.x = object.findField("x").getInt();
			result.y = object.findField("y").getInt();
			result.removed = object.findField("removed").getBoolean();
			return result;
		}		
	}
	
	static class Player extends Entity{
		
		private String name;
		private String avatarPath;
		
		public Player(String name, int x, int y){
			this.x = x;
			this.y = y;
			
			this.name = name;
			avatarPath = "res/parsemedaddy.png";
		}
		
		private Player(){			
		}
		
		public void serialize(MSObject object){
			super.serialize(object);
			
			object.addString(MSString.Create("name", name));
			object.addString(MSString.Create("avatarPath", avatarPath));
		}
		
		public static Player deserialize(MSObject object){
			Entity e = Entity.deserialize(object);
			Player result = new Player();
			result.x = e.x;
			result.y = e.y;
			result.removed = e.removed;
			
			result.name = object.findString("name").getString();
			result.avatarPath = object.findString("avatarPath").getString();
			
			return result;
		}
				
	}
	
	public void play(){
		{
		Entity mob = new Entity();
		Player player = new Player("Dat Boi", 40, 28);
		
		Level level = new Level("level.lvl");
		level.add(mob);
		level.add(player);
		
		level.save("level.pcdb");
		}
		{
		Level level = Level.load("level.pcdb");
		System.out.println();
		}
	}
}
