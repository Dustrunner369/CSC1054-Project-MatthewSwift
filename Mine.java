import javafx.scene.paint.*;
import javafx.scene.canvas.*;
import java.lang.*;

//this is an example object
public class Mine extends DrawableObject
{
   static double colorValue = Math.random();
   static int way = 1;
   int randomColor = (int)(Math.random() * 2) + 1;
	//takes in its position
   public Mine(float x, float y)
   {
      super(x,y);
   }
   public static void advanceColor()
   {
      colorValue += 0.01f * way;
      
      if(colorValue > 1)
      {
         colorValue = 1;
         way = - 1;
      }
      if(colorValue < 0)
      {
         colorValue = 0;
         way = 1;
      }
   }
   //draws itself at the passed in x and y.
   public void drawMe(float x, float y, GraphicsContext gc)
   {
      if(randomColor == 1)
         gc.setFill(Color.WHITE.interpolate(Color.RED,colorValue));
      else
         gc.setFill(Color.RED.interpolate(Color.WHITE,colorValue));
     
      gc.fillOval(x-13,y-13,12,12);
   }
}
