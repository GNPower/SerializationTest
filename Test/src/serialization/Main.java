package serialization;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import serialization.containers.MSArray;
import serialization.containers.MSDatabase;
import serialization.containers.MSField;
import serialization.containers.MSObject;
import serialization.containers.MSString;

public class Main {
	
	static Random random = new Random();

	public static void main(String[] args) {
//		serializationTest();
//		deserializationTest();
		
		Sandbox sandbox = new Sandbox();
		sandbox.play();
	}
	
	public static void deserializationTest(){
		MSDatabase database = MSDatabase.deserializeFromFile("test.msdb");
		System.out.println("Database: " + database.getName());
		for(MSObject object : database.objects){
			System.out.println("\t" + object.getName());
			for(MSField field : object.fields){
				System.out.println("\t\t" + field.getName());
			}
			for(MSString string : object.strings){
				System.out.println("\t\t" + string.getName() + " = " + string.getString());
			}
			for(MSArray array : object.arrays){
				System.out.println("\t\t" + array.getName());
			}
		}
	}
	
	public static void serializationTest(){
		int[] data = new int[50000];
		for(int i = 0; i < data.length; i++){
			data[i] = random.nextInt();
		}
		
		MSDatabase database = new MSDatabase("database");
		
		int[] data1 = new int[] {1, 2, 3, 4, 5};
		MSArray array = MSArray.Integer("Random Numbers", data);
		MSField field = MSField.Integer("Integer", 8);
		MSField field1 = MSField.Short("xpos", (short) 2);
		MSField field2 = MSField.Short("ypos", (short) 43);
		
		
		MSObject object = new MSObject("Entity");
		object.addArray(array);
		object.addArray(MSArray.Char("String", "Hello World!".toCharArray()));
		object.addField(field);
		object.addField(field1);
		object.addField(field2);
		object.addString(MSString.Create("Example String", "Testing our MSString class!"));
		
		database.addObject(object);
		database.addObject(new MSObject("Dat Big Boi"));
		database.addObject(new MSObject("Dat Big Boi!!"));
		database.addObject(new MSObject("Dat Big Boi@@"));
		database.addObject(new MSObject("Dat Big Boi##"));
			
		byte[] stream = new byte[database.getSize()];
		database.getBytes(stream, 0);
		//printBytes(stream);
		saveToFile("test.msdb", stream);
	}
	
	static void printBytes(byte[] data){
		for(int i = 0; i < data.length; i++){
			System.out.printf("0x%x ", data[i]);
		}
	}
	
	static void saveToFile(String path, byte[] data){
		try {
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(path));
			stream.write(data);
			stream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
