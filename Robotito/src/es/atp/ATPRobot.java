package es.atp;
import robocode.*;
import robocode.util.*;
import java.util.*;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * Robotito - a robot by ATP
 */
public class ATPRobot extends Robot
{
	/**
	 * run: Robotito's default behavior
	 */
	Random rnd=Utils.getRandom();
	int i=0;
	public void run() {
		// Initialization of the robot should be put here
		energy=getEnergy();
		
		while(true) {
			ahead(20+5*rnd.nextDouble());
			turnRight(20+5*rnd.nextDouble());
			if(getX()<=getBattleFieldWidth()*0.25){
				turnRight(90);
				if(getHeading()>=90&& getHeading()<=270){
					back(100);
				}else{
					ahead(140);
				}
				
			}else if(getX()>=getBattleFieldWidth()*0.75){
				turnLeft(90);
				if(getHeading()>=90&& getHeading()<=270){
					ahead(100);
				}else{
					back(140);
				}
			}
			if(getY()<=getBattleFieldHeight()*0.25){	
				turnLeft(90);
				if(getHeading()>=0&& getHeading()<=180){
					back(100);
				}else{
					ahead(140);
				}				
			}else if(getY()>=getBattleFieldHeight()*0.75){
				turnRight(90);
				if(getHeading()>=0&& getHeading()<=180){
					ahead(100);
				}else{
					back(140);
				}			
				
			}
			i++;
			if(i>=6){
			turnGunRight(360);
		
			turnLeft(rnd.nextDouble()*40);
			back(50+rnd.nextDouble()*30);
			i=0;
			}
		}
	}

	double energy=100.0;
	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		double current=e.getEnergy();

		double power=3;	
	
	if (e.getVelocity()!=0){
		power=3/(1+0.8*e.getDistance()/getBattleFieldHeight());		
		double rotation=e.getBearing()
			/e.getDistance()*e.getVelocity();
		out.println(rotation*0.8);
		turnRight(rotation*0.9);
		fire(power);
		turnLeft(1.8*rotation);
		fire(power);	
	}	else
	{
		fire(power);	
	}
	
	if(energy-current<=3){
		turnLeft(20);
		ahead(200);
	}
		energy=current;
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent event) {
		turnRight(event.getBearing());
		if (event.getBearing() > -10 && event.getBearing() <= 10) {
			back(80);
    	   }
		else {
			ahead(80);
		}
		
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
	
	}
	public void onHitRobot(HitRobotEvent event) {
       if (event.getBearing() > -90 && event.getBearing() <= 90) {
    	   if (event.getBearing() > -10 && event.getBearing() <= 10) {
    		   fire(3);
    	   }
     	  back(120);
       } else {
           ahead(50);
       }
	   turnLeft(70);
   }	
}
