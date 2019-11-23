//Author: Danyang Wang
//Class: ICS3U
//Date: January 19th, 2019
//Instructor: Mr Radulovic
//Assignment name: Culminating Project
/* This class runs the tower defense game, where the player places defenses to survive 
  10 waves of enemies*/
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TextField;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.scene.shape.Line;
import javafx.scene.layout.VBox;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Button;
public class towerDefense extends Application {
	
	private int scene_x,scene_y;	//the size of the window
	private int cash;	//the amount of cash you have 
	private int wave,maxWave;	//the current wave number and the total wave numbers respectively
	
	private int tenth_second; //one tenth of a second represented in nanoseconds	
	
	private int map_width,map_height;	//the size of the map
	private int row,column;	//represent how many tiles are in each row and column
	private int defense_height,defense_width;	//represent the size(pixels) of each defense
	private int enemy_height,enemy_width;	//represent # of pixels the enemy image file occupies
	private boolean Tiles[][];	
	private int tile_xsize,tile_ysize;	//size of each tile
	private int centerTileX,centerTileY;	//center of the tile
	private int house_x,house_y;	//house location on the map
	
	private ArrayList<Cannon> cannons;	//stores all the cannons placed
	private ArrayList<Slime> slimes;	//stores all the slimes spawned
	
	//Layout Managers
	private HBox root;
	private VBox gameData,guiObjects;
	private Group mapGroup,enemyGroup,attackGroup;
	
	private int nodeWithin;	//keeps track of how many children the enemyGroup has at the beginning
	
	//GUI (labels,border,font size, spacing)
	private Label cashAmount,waveNumber,defenseList,cannonLabel,cannonPrice,houseHealthLabel;
	private Border border;
	private int headingFontSize,textFontSize,maxWidth,spacing;
	
	private boolean waveStarted;	//starts animating when this variable is true
	private int start_x,start_y; //the starting position of where the enemies will be spawned
	private int beginEnemy; //number of enemies at wave 1
	
	//the number of enemies in each wave, and how many enemies are still alive respectively
	private int enemy,enemyAlive;	
	
	private int slimeHealth;	//the health of each slime 
	private int houseHealth;
	
	//the different direction that the enemies can travel
	private final int pathNum1=1,pathNum2=2,pathNum3=3; 
	private int turningPoint1,turningPoint2,turningPoint3;
	
	//the spawn rate of enemies and the attack speed of defenses
	private double spawnTime,attackTimer,time,spawnSpeed;
	
	private String playerName;	//stores player username if the player win the game
								//does not ask for player username if the player loses
	private Cannon c;
	private Slime s;
	private House h;
	
	/*This method takes the coordinates of the defense and the coordinates of the enemy and returns
	 the distance between them. The distance formula was used.*/ 
	public double getDistance(int x1,int y1, int x2,int y2) {
		double distance=Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1, 2));
		return distance;
	}
	
	/*This method reads a premade file that contains the coordinates of enemy spawn point, 
	house location and turning points for this specific map*/ 
	private void readLocation(String fileLocation) {
		File file= new File(fileLocation);
		Scanner scan;
		try {
			scan = new Scanner(file);
			start_x=scan.nextInt(); //first line stores the enemy spawn point
			start_y=scan.nextInt();
			house_x=scan.nextInt();	//second line stores the location of the house
			house_y=scan.nextInt();
			turningPoint1=scan.nextInt();	//stores the coordinate in which a turn happens
			turningPoint2=scan.nextInt();
			turningPoint3=scan.nextInt();
			scan.close();
		} catch (FileNotFoundException e) {
			System.out.println("Invalid File");
			e.printStackTrace();
		}
	}
	
	//This method writes the player's name to the file listed in the parameter 
	private void recordUser(String fileName) throws IOException	{
		RandomAccessFile fileAF = new RandomAccessFile(fileName,"rw");
		fileAF.seek(fileAF.length());
		fileAF.readLine();
		playerName+="\n";
		fileAF.write(playerName.getBytes());
		fileAF.close();
	}
	
	//This method checks if you have won, and then displays the end screen after the user wins
	/*The end screen allows you to enter a username in the textfield so that it can be recorded to 
	Winners.txt file */
	private void winScreen() {
		if (wave>maxWave) {
			waveStarted=false;
			root.getChildren().clear();
			Label win=new Label("You Win! ");
			win.setFont(new Font("Calibri",headingFontSize));
			TextField userName=new TextField("Enter Username");
			Button saveScore= new Button("Save");
			root.getChildren().addAll(win,userName,saveScore);
			userName.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent arg0) {
					playerName=userName.getText();
				}
			});
			saveScore.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent arg0) {
					try {
						recordUser("Winners.txt");
					} catch (IOException e) {
						System.out.println("Invalid File");
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	//This method updates the coordinates and animates the movement of the slime 
	private void updateSlimeLocation() {
		for (int i=0;i<slimes.size();i++) {
			Slime currentSlime= slimes.get(i);
			int current_x=currentSlime.getLocation_x();	//get current slime coordinates
			int current_y=currentSlime.getLocation_y();	
			int speed=currentSlime.getMovementSpeed();	
			int path=currentSlime.getPath();
			
			//update slime coordinates based on movement speed and current path
			if (current_y>=turningPoint1 && path==pathNum1) {
				path++;
				currentSlime.setPath(path);
			}
			else if (current_x>=turningPoint2 && path==pathNum2) {
				path++;	
				currentSlime.setPath(path);
			}
			else if (current_y<=turningPoint3 && path==pathNum3) {
				path++;
				currentSlime.setPath(path);	
			}	
			if (currentSlime.getPath()==pathNum1) {
				current_y+=speed;	
			}
			else if (currentSlime.getPath()==pathNum2) {
				current_x+=speed;
			}
			else if (currentSlime.getPath()==pathNum3) {
				current_y-=speed;
			}
			else {
				current_x-=speed;
			}
			
			//store the location of slime and move the slime image
			currentSlime.setLocation_x(current_x);	
			currentSlime.setLocation_y(current_y);
			currentSlime.getSlimePicture().setTranslateX(current_x);
			currentSlime.getSlimePicture().setTranslateY(current_y);
		
		}
	}
	
	/*This method deducts the house's health if the enemies reach the house and displays the 
	  gameover screen when health falls below or equal to 0*/
	private void checkHouse() {
		for (int i=0;i<slimes.size();i++) {
			Slime currentSlime= slimes.get(i);
			if (currentSlime.getLocation_x()==house_x && currentSlime.getLocation_y()==house_y) {
				h.deductHealth();
				if (h.getHealth()<=0) {
					waveStarted=false;
					root.getChildren().clear();
					Label gameOver= new Label("Game Over");
					gameOver.setFont(new Font("Calibri",headingFontSize));
					root.getChildren().addAll(gameOver);
				}
				else {
					updateHouseHealth(h.getHealth());	//update health
					enemyAlive-=1;	//remove that enemy
					enemyGroup.getChildren().remove(nodeWithin+i);	
					slimes.remove(i);
				}
			}
		}
	}
	
	private void updateAttack() {
		if (attackTimer==c.getAttackSpeed()) {
			attackTimer=0;
			//checks if there are slimes within attack range for every defense
			for (int i=0;i<cannons.size();i++) {	
				Cannon currentCannon= cannons.get(i);
				for (int j=0;j<slimes.size();j++) {
					Slime slimeSelected=slimes.get(j);
					
					//use distance formula to calculate distance between two points
					double distance= getDistance(currentCannon.getLocation_x(),
						currentCannon.getLocation_y(),slimeSelected.getLocation_x(),
						slimeSelected.getLocation_y());
					
					//check if the distance falls within the defense's attack range
					if (distance<=currentCannon.getAttackRange()) {
						//draw the line to show which enemy the defense is attacking
						Line displayAttack=currentCannon.attack(currentCannon.getLocation_x()+
							centerTileX,currentCannon.getLocation_y()+centerTileY,
							slimeSelected.getLocation_x()+centerTileX,
							slimeSelected.getLocation_y()+centerTileY);
						attackGroup.getChildren().addAll(displayAttack);
						
						//update the health of the enemy that got attacked
						int newEnemyHealth=slimeSelected.deductHealth(slimeSelected.getHealth(),
								currentCannon.getAttackDamage());
						
						//enemies drop loot(in this case cash) when they lose all their health 
						if (newEnemyHealth<=0) {
							cash+=slimeSelected.dropLoot();
							updateCash(cash);
							enemyAlive-=1;
							enemyGroup.getChildren().remove(nodeWithin+j);	
							slimes.remove(j);
						}
						
						//otherwise, update their new health after the attack
						else {
							slimeSelected.setHealth(newEnemyHealth);
						}
						break;	//stops when it attacks one enemy
					
					}
				}
			}
		}
	}
	
	//This method basically keeps track of the time and update all the animations 
	private void updateAll() {
		winScreen();
		spawnTime+=time;
		if (spawnTime==spawnSpeed) {
			spawnEnemy(enemy);
			spawnTime=0;
		}
		updateSlimeLocation();
		attackGroup.getChildren().clear();
		attackTimer+=time;
		updateAttack();
		checkHouse();
		if (enemyAlive==0 && slimes.size()==0) {
			attackGroup.getChildren().clear();
			waveStarted=false;
		}
	}
	
	//This method takes the number of enemies that needs to be spawned as parameter
	//This method spawns the enemy on the map, in other words construct a lot of slime objects
	private void spawnEnemy(int enemyCount) {
		
		if (enemyCount!=0) {
			//create slime objects
			ImageView slimeImage=s.loadSlime("Resources/slime.png",enemy_width,enemy_height);
			Slime sl=new Slime(start_x,start_y,slimeHealth,pathNum1);
			sl.setSlimePicture(slimeImage);
			enemyGroup.getChildren().addAll(sl.getSlimePicture());
			slimes.add(sl);
			enemy-=1;
		}
		
	}
	
	//This method updates the health of the house
	private void updateHouseHealth(int health){
		houseHealthLabel.setText("Health Remaining: "+health+" hp");
	}
	//This method updates the current wave number
	private void updateWaveNumber(int wave) {
		waveNumber.setText(" Wave Number: "+Integer.toString(wave));
	}
	//This method updates the amount of cash you have
	private void updateCash(int cash) {
		cashAmount.setText(" Cash: "+Integer.toString(cash));
	}
	/*This method takes the current wave number as parameter, and spawns
	  one more enemy than the previous wave as the wave number increases */
	private int getEnemy(int waveNumber) {
		enemy=beginEnemy+(waveNumber-1);
		enemyAlive=enemy;
		return enemy;
	}
	
	public static void main(String args[]) {
		launch(args);
	}
	public void start(Stage primaryStage) throws Exception {
		//Initializing variables 
		scene_x=1200;
		scene_y=600;
		map_width=960;
		map_height=600;
		cash=500;
		wave=0;
		maxWave=10;
		c=new Cannon(0,0);	//used to get the value of private variables of the cannon class
		s=new Slime(0,0,0,0);	//used to get the value of private variables of the slime class
		slimeHealth=10;
		houseHealth=10;
		h=new House(houseHealth);

		cannons= new ArrayList<Cannon>();
		slimes= new ArrayList<Slime>();
		beginEnemy=5;
		getEnemy(wave);
		defense_width=60;
		defense_height=60;
		enemy_width=60;
		enemy_height=60;
		time=0.1;
		spawnSpeed=0.6;	//time it takes to spawn a slime
		readLocation("importantLocation.txt");
		playerName="Default_Player";
		maxWidth=scene_x-map_width;
		headingFontSize=20;
		textFontSize=18;
		spacing=10;
		tenth_second = 100000000;
		
		//The animation starts when the next wave is started
		AnimationTimer timer = new AnimationTimer() {
            long oldTime = 0;	//counter

            public void handle(long time) {
                oldTime += 1;
            //the update method is called once every tenth of a second to animate 
            //only starts checking for one tenth of a second when there's an animation happening
                if (time - oldTime > tenth_second && waveStarted ){
                    updateAll();
                    oldTime = time;
                }
            }
        };
        timer.start();	//starts timer
        
        //Layout Management
	    root= new HBox();
		guiObjects= new VBox(); 
		gameData= new VBox();
		mapGroup = new Group();
		enemyGroup = new Group();
		attackGroup = new Group();
		gameData.setSpacing(spacing);
		root.getChildren().addAll(enemyGroup,guiObjects);
		enemyGroup.getChildren().addAll(mapGroup,attackGroup);
		nodeWithin=enemyGroup.getChildren().size();
		guiObjects.getChildren().addAll(gameData);
		Scene scene=new Scene(root,scene_x,scene_y);
		
		//Loading the map and the state of each tile
		Map m=new Map();
		ImageView background=m.loadMap("Resources/map_creation.png",map_width,map_height);
		m.loadTiles("Tiles.txt");
		background.setX(0);
		background.setY(0);
		mapGroup.getChildren().addAll(background);
		Tiles=m.getTiles();
		row=m.getRow();
		column=m.getColumn();
		tile_xsize=map_width/row;	//calculating the size of each tile
		tile_ysize=map_height/column;
		centerTileX=tile_xsize/2;	
		centerTileY=tile_ysize/2;
		
		//GUI (displayed on the right side when you run the program)
		border= new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, 
				null, null));
		guiObjects.setBorder(border);
		guiObjects.setPrefWidth(maxWidth);
		guiObjects.setSpacing(spacing);
		waveNumber=new Label(" Wave Number: "+Integer.toString(wave));
		waveNumber.setFont(new Font("Calibri",headingFontSize));
		waveNumber.setMaxWidth(maxWidth);
		Separator s0=new Separator();
		cashAmount=new Label(" Cash: "+Integer.toString(cash));
		cashAmount.setFont(new Font("Calibri",headingFontSize));
		Separator s1=new Separator();
		gameData.getChildren().addAll(waveNumber,s0,cashAmount,s1);
		defenseList=new Label(" Defenses: ");
		defenseList.setFont(new Font("Calibri",headingFontSize));
		Separator s2=new Separator();
		cannonLabel=new Label(" Cannon");
		cannonLabel.setFont(new Font("Calibri",textFontSize));
		cannonPrice=new Label(" Price: "+Integer.toString(c.getCost()));
		cannonPrice.setFont(new Font("Calibri",textFontSize));
		Button cannonBuy= new Button("Buy");
		cannonBuy.setFont(new Font("Calibri",textFontSize));
		Separator s3=new Separator();	
		guiObjects.getChildren().addAll(defenseList,s2,cannonLabel,cannonPrice,cannonBuy,s3);
		houseHealthLabel = new Label();
		updateHouseHealth(h.getHealth());
		houseHealthLabel.setFont(new Font("Calibri",headingFontSize));
		Button startWaveButton= new Button("Start Next Wave");
		guiObjects.getChildren().addAll(houseHealthLabel,startWaveButton);
		
		//EventHandler for buttons
		cannonBuy.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				//checks if the user has enough cash
				if (cash<c.getCost()) {
					System.out.println("Not enough cash");
				}
				
				/*otherwise, the program gets the location of the tile that the user clicked on and 
				 checks if that tile is already occupied*/
				else {	
					background.setOnMouseClicked(new EventHandler<MouseEvent>(){
						public void handle(MouseEvent event) {
							
							//get the tile number that the user clicked on 
							int tile_x=(int) Math.floor(event.getX()/(tile_xsize));
							int tile_y=(int) Math.floor(event.getY()/(tile_ysize));
							
							/*multiply the tile number by the size of every tile to get the 
							  coordinate of the that tile*/
							int defense_x=(int) Math.floor(event.getX()/(tile_xsize))*tile_xsize;
							int defense_y=(int) Math.floor(event.getY()/(tile_ysize))*tile_ysize;
							
							if (!m.checkTileOccupied(tile_x, tile_y)) {	//if tile not occupied
								
								//update the amount of cash
								cash-=c.getCost();
								updateCash(cash);
								
								//creates a cannon object
								//adds it to the cannon arraylist and mapGroup node
								Cannon newCannon = new Cannon(defense_x,defense_y);
								m.markTileOccupied(tile_x,tile_y);
								ImageView cannon_image=c.loadCannon("Resources/Cannon.png",
										defense_width,defense_height);
								cannon_image.setX(defense_x);
								cannon_image.setY(defense_y);
								cannons.add(newCannon);
								mapGroup.getChildren().addAll(cannon_image);

								//reset mouse event so that the user only places one defense
								background.setOnMouseClicked(null);	  
							}
							//System.out.println("The tile selected is occupied.");
						}	
					});
				}
			}
		});
		
	//only starts the wave when you have defeated every enemy or when you are just starting the game
		startWaveButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				if (enemyAlive<=0 && slimes.size()==0 || wave==0) {
					waveStarted=true;	//starts animation
					
					//increase wave number
					wave++;	
					updateWaveNumber(wave);
					
					//get the number of enemies for this wave
					getEnemy(wave);
				}
			}
		});
		
		
		primaryStage.setTitle("Culminating Project");		//display title
		primaryStage.setScene(scene);	
		primaryStage.show(); 	
	}
}
