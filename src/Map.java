//Author: Danyang Wang
//Class: ICS3U
//Date: January 19th, 2019
//Instructor: Mr Radulovic
//Assignment name: Culminating Project
/* This class is used to load the map of the tower defense game, and to check the 
 state of each tile during the game.  */
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Scanner;
public class Map {
	
	private boolean[][] tiles;	//stores the state of every tile (occupied or not occupied)
	private int row, column;	
	
	public boolean[][] getTiles() {
		return tiles;
	}
	
	public int getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}
	
	//This method loads the map
	//This method takes the map location, the desired width and height of the map as parameter
	public ImageView loadMap(String FileLocation,int width,int height){
		String url="";
		try {
			url = Paths.get(FileLocation).toUri().toURL().toString();
		} catch (MalformedURLException e) {
			System.out.println("Invalid File");
			e.printStackTrace();
		}
		Image image=new Image(url,width,height,true,false);
		ImageView map = new ImageView(image);
		return map;
	}
	
	//This method reads the state of every tile from the start of the game
	//Tiles that are part of the enemy's path or the house are considered to be occupied tiles
	public void loadTiles(String fileName) throws FileNotFoundException {
		File file= new File(fileName);
		Scanner scan=new Scanner(file);
		row=scan.nextInt();
		column=scan.nextInt();
		tiles=new boolean[column][row];
		for (int i=0;i<column;i++) {
			for (int j=0;j<row;j++) {
				int occupied=scan.nextInt();
				if (occupied==1) {	//occupied tiles are represented as '1' in the file
					tiles[i][j]=true;
				}
				else {				//unoccupied tiles are represented as '0' in the file
					tiles[i][j]=false;
				}
			}
		}
		scan.close();
		
	}
	
	//sets the specified tile as occupied
	public void markTileOccupied(int x_cord,int y_cord) {
		tiles[y_cord][x_cord]=true;
	}
	
	//checks if the specified tile is occupied or not
	public boolean checkTileOccupied(int x_cord,int y_cord) {
		if (tiles[y_cord][x_cord]==true) {
			return true;
		}
		else {
			return false;
		}
	}

}