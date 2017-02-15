package ATP;
import robocode.*;
import robocode.util.*;
import java.util.*;
//import java.awt.Color;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * Robotito - a robot by ATP
 */
public class Robotito extends Robot
{
	/**
	 * run: Robotito's default behavior
	 */
	Random rnd=Utils.getRandom();
	public void run() {
		// Initialization of the robot should be put here

		// After trying out your robot, try uncommenting the import at the top,
		// and the next line:

		// setColors(Color.red,Color.blue,Color.green); // body,gun,radar

		while(true) {
			ahead(150);
			turnRight(30);
			turnGunRight(360);
			back(100);
			turnLeft(rnd.nextDouble()*40);
			back(50+rnd.nextDouble()*30);
			turnLeft(50);
			turnGunLeft(360);
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
	double power=3;	
	if (e.getVelocity()!=0){
		power=3/(1+0.8*e.getDistance()/getBattleFieldHeight());		
		double rotation=(e.getBearing()-getGunHeading())
			/e.getDistance()*e.getVelocity();
		out.println(rotation*0.8);
		turnGunRight(rotation);
	}	
	fire(power);
		
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		turnRight(e.getBearing());
		back(60);
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		turnRight(e.getBearing());
		back(80);
	}
	public void onHitRobot(HitRobotEvent event) {
       if (event.getBearing() > -90 && event.getBearing() <= 90) {
     	  back(120);
       } else {
           ahead(50);
		   turnGunRight(270);
       }
	   turnLeft(70);
   }	
}
