//Author: Danyang Wang
//Class: ICS3U
//Date: January 19th, 2019
//Instructor: Mr Radulovic
//Assignment name: Culminating Project
/* This class contains the private variables,constructor, getter and setters, and other 
  methods for the slime  */

import java.net.MalformedURLException;
import java.nio.file.Paths;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Slime {
	
	private int health;	//represent the health of the slime
	private final int movementSpeed=10;	//movementSpeed has to be a multiple of the tile size
	private int cashReceived; 	//cash received for defeating the slime 
	
	private final int max_cashReceived=30;	//the range of how much cash you will receive
	private final int min_cashReceived=20;
	
	private int location_x,location_y;	//stores the location of the slime
	private ImageView slimePicture;		//displays the image(png) of the slime
	private int path;	//shows which path the slime is going
	
	//constructs a slime object
	public Slime(int location_x, int location_y, int health, int path) {
		this.location_x=location_x;
		this.location_y=location_y;
		this.health=health;
		this.path=path;
	}
	
	public int getHealth() {
		return health;
	}
	
	public void setHealth(int health) {
		this.health = health;
	}
	
	public int getLocation_x() {
		return location_x;
	}
	public void setLocation_x(int location_x) {
		this.location_x = location_x;
	}
	
	public int getLocation_y() {
		return location_y;
	}
	public void setLocation_y(int location_y) {
		this.location_y = location_y;
	}
	
	public int getMovementSpeed() {
		return movementSpeed;
	}
	public ImageView getSlimePicture() {
		return slimePicture;
	}
	
	public void setSlimePicture(ImageView slimePicture) {
		this.slimePicture= slimePicture;
	}
	
	public int getPath() {
		return path;
	}

	public void setPath(int path) {
		this.path = path;
	}
	
	
	//This method loads the image of the slime
	//This method takes the image location, the desired width and height of the image as parameter
	public ImageView loadSlime(String FileLocation,int width,int height){
		String url="";
		try {
			url = Paths.get(FileLocation).toUri().toURL().toString();
		} catch (MalformedURLException e) {
			System.out.println("Invalid File");
			e.printStackTrace();
		}
		Image image=new Image(url,width,height,true,false);	//image of slime
		ImageView slimeImage = new ImageView(image);
		return slimeImage;
		
	}
	
	public int deductHealth(int health,int damage) {
		health-=damage;
		return health;
	}

	
	//This method generates the amount of cash dropped upon defeating a slime
	//The amount of cash is randomly selected between 20 to 30
	public int dropLoot() {
		int range=max_cashReceived-min_cashReceived+1;
		cashReceived=(int)(Math.random()*range)+min_cashReceived;
		return cashReceived;
	}
}
