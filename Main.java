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
   Label playerScore;
   Label highScore;
   GraphicsContext gc;
   Player thePlayer = new Player(300,300);
   Mine mine = new Mine(250,250);
   ArrayList<Mine> mineList = new ArrayList<Mine>();
   
   
   //Initialize Variables
   boolean keyUp, keyDown, keyLeft, keyRight;
   float forceX, forceY, playerSpeed = 1;
   int score = 0;
   int highScoreCounter;
   
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
      playerScore = new Label("Score: " + score);
      playerScore.setTextFill(Color.WHITE);
      playerScore.setTranslateX(-270);
      playerScore.setTranslateY(-290);
      
      highScore = new Label("High Score: " + score);
      highScore.setTextFill(Color.WHITE);
      highScore.setTranslateX(-255);
      highScore.setTranslateY(-275);
      
      sp.getChildren().add(playerScore);
      sp.getChildren().add(highScore);
      
      fp.getChildren().add(sp);
      
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
         playerScore.setText("Score: " + score);
         
         //Mine Logic
         Mine.advanceColor();
         
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
         
         
         
         if(playerGridChanged){
            
               int x;
               int y;
               //Moving Left
               // if(cgridx < 0){
//                   x = ((cgridx - 3) * 100) + (int)(Math.random() * 99);
//                }
//                //Moving Right
//                else{
//                   x = ((cgridx + 3) * 100) + (int)(Math.random() * 99);
//                }
//                //Moving Up
//                if(cgridy < 0){
//                   y = ((cgridy - 3) * 100) + (int)(Math.random() * 99);
//                }
//                //Moving Down
//                else{
//                   y = ((cgridy + 3) * 100) + (int)(Math.random() * 99);
//                }
               
               y = ((cgridy - 4) * 100) + (int)(Math.random() * 99);
               for(int j = -5; j < 5; j++){
               
                  int temp = cgridx + j;   
                  x = (temp * 100) + (int)(Math.random() * 99);
                  mineList.add(new Mine(x, y));   
               }
               y = ((cgridy + 3) * 100) + (int)(Math.random() * 99);
               for(int j = -5; j < 5; j++){
               
                  int temp = cgridx + j;   
                  x = (temp * 100) + (int)(Math.random() * 99);
                  mineList.add(new Mine(x, y));   
               }
               
               x = ((cgridx - 4) * 100) + (int)(Math.random() * 99);
               for(int j = -5; j < 5; j++){
               
                  int temp = cgridy + j;   
                  y = (temp * 100) + (int)(Math.random() * 99);
                  mineList.add(new Mine(x, y));   
               }
               x = ((cgridx + 3) * 100) + (int)(Math.random() * 99);
               for(int j = -5; j < 5; j++){
               
                  int temp = cgridy + j;   
                  y = (temp * 100) + (int)(Math.random() * 99);
                  mineList.add(new Mine(x, y));   
               }
               
               //System.out.println(cgridx +  "  " + cgridy);
               //System.out.println(x +  "  " + y);
               //System.out.println(thePlayer.distance();
               
               
         }
         
         
         for(Mine mine : mineList){
            mine.draw(thePlayer.getX(),thePlayer.getY(),gc,false);
         }
         
      }
   }
   
   public class KeyListenerDown implements EventHandler<KeyEvent>  
   {
      public void handle(KeyEvent event) 
      { 
         if (event.getCode() == KeyCode.W){ 
            keyUp = true;
         }
         if (event.getCode() == KeyCode.A){
            keyLeft = true;
         }
         if (event.getCode() == KeyCode.S){
            keyDown = true;
         } 
         if (event.getCode() == KeyCode.D){
            keyRight = true;
         }
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


