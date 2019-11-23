//Author: Danyang Wang
//Class: ICS3U
//Date: January 19th, 2019
//Instructor: Mr Radulovic
//Assignment name: Culminating Project
/* This class contains constructor and information about the health points of the house  */
public class House {
	private int health;
	
	public House(int health) {
		this.health=health;
	}
	// this method is called when an enemy reaches the house
	public void deductHealth() {
		health-=1;
	}
	
	public int getHealth() {
		return health;
	}
	
	public void setHealth(int health) {
		this.health = health;
	}
	
}
