//Author: Danyang Wang
//Class: ICS3U
//Date: January 19th, 2019
//Instructor: Mr Radulovic
//Assignment name: Culminating Project
/* This class contains the private variables,constructor, getter and setters, and other 
  methods for the cannon */
import java.net.MalformedURLException;
import java.nio.file.Paths;
import javafx.scene.shape.Line;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
public class Cannon {
	private final int cost=200;				//the cost of the defense
	private final int attackRange=100;		//the range is a circle of 100 pixel
	private final int attackDamage=5;		//every attack deals 5 damage
	private final double attackSpeed=0.5;	//attacks every 0.5 seconds 
	private int location_x,location_y;
	
	//constructs a cannon object
	public Cannon(int location_x,int location_y) {
		this.location_x=location_x;
		this.location_y=location_y;
	}
	
	//This method loads the image of the cannon
	//This method takes the image location, the desired width and height of the image as parameter
	public ImageView loadCannon(String FileLocation,int width,int height){
		String url="";
		try {
			url = Paths.get(FileLocation).toUri().toURL().toString();
		} catch (MalformedURLException e) {
			System.out.println("Invalid File");
			e.printStackTrace();
		}
		Image cannonImage=new Image(url,width,height,true,false);
		ImageView cannon = new ImageView(cannonImage);
		return cannon;
	}
	/*This method draws a line from the defense to the enemy to display the attack, the parameter
	 takes x and y coordinates of the defense, and the x and y coordinates of the enemy*/
	public Line attack(int x1,int y1, int x2, int y2) {
		Line attackLine=new Line(x1,y1,x2,y2);
		attackLine.setStroke(Color.RED);
		return attackLine;
	}
	
	public int getCost() {
		return cost;
	}
	
	public int getAttackRange() {
		return attackRange;
	}
	
	public int getAttackDamage() {
		return attackDamage;
	}
	
	public double getAttackSpeed() {
		return attackSpeed;
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
}
