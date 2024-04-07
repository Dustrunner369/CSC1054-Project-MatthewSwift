import java.net.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.*;
import javafx.scene.paint.*;
import javafx.geometry.*;
import javafx.scene.image.*;

import java.io.*;

import java.util.*;
import java.text.*;
import java.io.*;
import java.lang.*;
import javafx.application.*;
import javafx.event.*;
import javafx.stage.*;
import javafx.scene.canvas.*;
import javafx.scene.paint.*;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.animation.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import java.net.*;
import javafx.geometry.*;

public class Main extends Application
{
   //Initialize Objects
   FlowPane fp;
   StackPane sp;
   Canvas theCanvas = new Canvas(600,600);
   AnimationTimer ta;
   Label playerScoreLabel;
   Label highScoreLabel;
   GraphicsContext gc;
   Player thePlayer = new Player(300,300);
   Mine collidedMine = null;
   ArrayList<Mine> mineList = new ArrayList<Mine>();
   PrintWriter pw = null;
   FileWriter myWriter = null;
   File myFile = null;
   Scanner scan = null;
   
   
   //Initialize Variables
   boolean keyUp, keyDown, keyLeft, keyRight;
   float forceX, forceY, playerSpeed = .5f;
   int score = 0;
   int highScoreCounter = 0;
   boolean collisionOccured = false;
   
   int oldPlayerPositionX = ((int)thePlayer.getX())/100;
   int oldPlayerPositionY = ((int)thePlayer.getY())/100;
   
   
   public void start(Stage stage)
   {   
      fp = new FlowPane();
      sp = new StackPane();
      sp.getChildren().add(theCanvas);
      fp.setOnKeyPressed(new KeyListenerDown());
      fp.setOnKeyReleased(new KeyListenerUp());
      
      gc = theCanvas.getGraphicsContext2D();
      
      drawBackground(300,300,gc);
      
      //Add score and highscore to flowpane.
      playerScoreLabel = new Label("Score: " + score);
      playerScoreLabel.setTextFill(Color.WHITE);
      playerScoreLabel.setTranslateX(-270);
      playerScoreLabel.setTranslateY(-290);
      
      highScoreLabel = new Label("High Score: " + score);
      highScoreLabel.setTextFill(Color.WHITE);
      highScoreLabel.setTranslateX(-255);
      highScoreLabel.setTranslateY(-275);
      
      sp.getChildren().add(playerScoreLabel);
      sp.getChildren().add(highScoreLabel);
      
      fp.getChildren().add(sp);
      
      //Open file and read score from it.
      try{
         myFile = new File("highscore.txt");
         
         if (myFile.exists() && !myFile.isDirectory()){            
            scan = new Scanner(myFile);
            if (scan.hasNextInt()) {               
               highScoreCounter = scan.nextInt();
            }   
            else {
               highScoreCounter = 0;
            }
         }
         else{
            myFile.createNewFile();
         }         
         myWriter = new FileWriter(myFile);         
      } 
      catch (IOException e) {
         System.out.println("An error occurred.");
         e.printStackTrace();
      }
                 
      Scene scene = new Scene(fp, 600, 600);
      stage.setScene(scene);
      stage.setTitle("Project :)");
      fp.requestFocus();
      stage.show();
      
      ta = new AnimationHandler();
      ta.start();
   }
   
   Image background = new Image("stars.png");
   Image overlay = new Image("starsoverlay.png");
   Random backgroundRand = new Random();
   //this piece of code doesn't need to be modified
   public void drawBackground(float playerx, float playery, GraphicsContext gc)
   {
	  //re-scale player position to make the background move slower. 
      playerx*=.1;
      playery*=.1;
   
	//figuring out the tile's position.
      float x = (playerx) / 400;
      float y = (playery) / 400;
      
      int xi = (int) x;
      int yi = (int) y;
      
	  //draw a certain amount of the tiled images
      for(int i=xi-3;i<xi+3;i++)
      {
         for(int j=yi-3;j<yi+3;j++)
         {
            gc.drawImage(background,-playerx+i*400,-playery+j*400);
         }
      }
      
	  //below repeats with an overlay image
      playerx*=2f;
      playery*=2f;
   
      x = (playerx) / 400;
      y = (playery) / 400;
      
      xi = (int) x;
      yi = (int) y;
      
      for(int i=xi-3;i<xi+3;i++)
      {
         for(int j=yi-3;j<yi+3;j++)
         {
            gc.drawImage(overlay,-playerx+i*400,-playery+j*400);
         }
      }
   }         
   
   public class AnimationHandler extends AnimationTimer
   {
      public void handle(long currentTimeInNanoSeconds) 
      {
         gc.clearRect(0,0,600,600);
         
         //USE THIS CALL ONCE YOU HAVE A PLAYER
         drawBackground(thePlayer.getX(),thePlayer.getY(),gc); 
         //System.out.println(thePlayer.getX()+ " "+thePlayer.getY());

	      //example calls of draw - this should be the player's call for draw
         if(!collisionOccured)
            thePlayer.draw(300,300,gc,true); //all other objects will use false in the parameter.

         //example call of a draw where m is a non-player object. Note that you are passing the player's position in and not m's position.
         
         
         //MY OWN CODE///
         
         //Player movement code. Uses force to move the player
         if(keyUp){
            forceY -= .1;
            if(forceY <= -5)
               forceY = -5;
         }
         if(keyDown){
            forceY += .1;
            if(forceY >= 5)
               forceY = 5;
         }
         if(keyRight){
            forceX += .1;
            if(forceX >= 5)
               forceX = 5;
         }
         if(keyLeft){
            forceX -= .1;
            if(forceX <= -5)
               forceX = -5;
         }
         
         if(!keyUp && !keyDown){
            if(forceY < 0)
               forceY += .025;
            if(forceY > 0)
               forceY -= .025;
            if(forceY >= -.25 && forceY <= .25)
                  forceY = 0;
         }
         if(!keyRight && !keyLeft){
            if(forceX < 0)
               forceX += .025;
            if(forceX > 0)
               forceX -= .025;
            if(forceX >= -.25 && forceX <= .25)
                  forceX = 0;
         }
         
         thePlayer.setY(thePlayer.getY() + (forceY * playerSpeed));
         thePlayer.setX(thePlayer.getX() + (forceX * playerSpeed));
         
         //Update score
         score = (int)(Math.sqrt((thePlayer.getX()-300)*(thePlayer.getX()-300) + (thePlayer.getY()-300)*(thePlayer.getY()-300)));
         
         
         if(score > highScoreCounter){
            highScoreCounter = score;
         }
         
         playerScoreLabel.setText("Score: " + score);
         highScoreLabel.setText("High score: " + highScoreCounter);
         
         //Mine Logic
         boolean playerGridChanged = false; 
         int cgridx = ((int)thePlayer.getX())/100;
         int cgridy = ((int)thePlayer.getY())/100;
         
         if(oldPlayerPositionX != cgridx){
            oldPlayerPositionX = cgridx;
            playerGridChanged = true;
         }
         if(oldPlayerPositionY != cgridy){
            oldPlayerPositionY = cgridy;
            playerGridChanged = true;
         }
         
         int n = (int)score/1000;
         
         if(playerGridChanged){
            
            int x;
            int y;
               
            for(int i = 0; i < n; i++){ 
               y = ((cgridy - 5) * 100) + (int)(Math.random() * 99);
               for(int j = -5; j < 5; j++){
                  int percentChance = (int)(Math.random() * 100) + 1;
                  if(percentChance < 30){
                     int temp = cgridx + j;   
                     x = (temp * 100) + (int)(Math.random() * 99);
                     mineList.add(new Mine(x, y));
                  }
               }
               y = ((cgridy + 4) * 100) + (int)(Math.random() * 99);
               for(int j = -5; j < 5; j++){
                  int percentChance = (int)(Math.random() * 100) + 1;
                  if(percentChance < 30){
                     int temp = cgridx + j;   
                     x = (temp * 100) + (int)(Math.random() * 99);
                     mineList.add(new Mine(x, y));
                  }   
               }
               
               x = ((cgridx - 5) * 100) + (int)(Math.random() * 99);
               for(int j = -5; j < 5; j++){
                  int percentChance = (int)(Math.random() * 100) + 1;
                  if(percentChance < 30){
                     int temp = cgridy + j;   
                     y = (temp * 100) + (int)(Math.random() * 99);
                     mineList.add(new Mine(x, y));
                  }   
               }
               x = ((cgridx + 4) * 100) + (int)(Math.random() * 99);
               for(int j = -5; j < 5; j++){
                  int percentChance = (int)(Math.random() * 100) + 1;
                  if(percentChance < 30){
                     int temp = cgridy + j;   
                     y = (temp * 100) + (int)(Math.random() * 99);
                     mineList.add(new Mine(x, y));  
                  } 
               }
            } 
         }
         
         if(collisionOccured)
            ta.stop();
         
         //Loops through all the mines
         for(int i = 0; i < mineList.size(); i++){
            if(mineList.get(i) != collidedMine){
               mineList.get(i).draw(thePlayer.getX(),thePlayer.getY(),gc,false);
               mineList.get(i).advanceColor();
            }
                              
            //End the game when a mine and player are within 20 pixels of each other
            if(thePlayer.distance(mineList.get(i)) < 20){
               collisionOccured = true;
               collidedMine = mineList.get(i);
               try{
                  myWriter.write("" + highScoreCounter);
                  myWriter.close();
               }
               catch(Exception e){
                  System.out.println(e);
               }
            }
            //Removes mine if distance is greater than 800
            if(thePlayer.distance(mineList.get(i)) > 800){
               mineList.remove(i);
               i--;
            }
         }
      }
   }
   
   public class KeyListenerDown implements EventHandler<KeyEvent>  
   {
      public void handle(KeyEvent event) 
      { 
         if (event.getCode() == KeyCode.W)
            keyUp = true;
         if (event.getCode() == KeyCode.A)
            keyLeft = true;
         if (event.getCode() == KeyCode.S)
            keyDown = true; 
         if (event.getCode() == KeyCode.D)
            keyRight = true;
      }
   }
   
   public class KeyListenerUp implements EventHandler<KeyEvent>  
   {
      public void handle(KeyEvent event) 
      { 
         if (event.getCode() == KeyCode.W)
            keyUp = false;
         if (event.getCode() == KeyCode.A) 
            keyLeft = false;
         if (event.getCode() == KeyCode.S) 
            keyDown = false;
         if (event.getCode() == KeyCode.D)
            keyRight = false;
      }
   }
   
   public static void main(String[] args)
   {
      launch(args);
   }
}