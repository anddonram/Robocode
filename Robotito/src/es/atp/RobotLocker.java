package es.atp;

import robocode.*;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * Robotito - a robot by ATP
 */
public class RobotLocker extends Robot {

	
	double eHeading = 0.0;
	double eDistance = 0.0;
	double eEnergy = 100.0;
	double eVelocity = 0.0;

	/**
	 * Track enemy position
	 */
	double eX = 0.0;
	double eY = 0.0;

	double minSize = 0.0;
	
	/**
	 * run: Robotito's default behavior
	 */
	public void run() {
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn(true);
		minSize = Math.min(getBattleFieldHeight(), getBattleFieldWidth());

		eEnergy = getEnergy();

		while (true) {
			turnRadarRight(360);
			double angle=(Math.atan2(getY()-eY,getX()- eX)/Math.PI+1)*180;
			//Detect if enemy is aiming at you
			if(Math.abs(eHeading-angle)<=20){
				turnRight(eHeading+70);
				ahead(minSize/2);
			}
		}
	}

	private void moveAround() {
		turnLeft(180 * Math.random());
		ahead(minSize * Math.random() * 0.25);
		
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		updateEnemy(e);
		moveRadar(e);
		if (getEnergy() > eEnergy * 0.5f || eDistance <= minSize / 1.5)
			moveGun(e.getBearing());
		else
			moveAround();
	}

	private void moveRadar(ScannedRobotEvent e) {
		double eRadar = e.getBearing() + getHeading() - getRadarHeading();
		if (eRadar > 180) {
			turnRadarLeft(360 - eRadar);
		} else if (eRadar < -180) {
			turnRadarRight(360 + eRadar);
		} else {
			turnRadarRight(eRadar);
		}

	}

	private void moveGun(double bearing) {
		double eGun = bearing + getHeading() - getGunHeading();
		if (eGun > 180) {
			turnGunLeft(360 - eGun);
		} else if (eGun < -180) {
			turnGunRight(360 + eGun);
		} else {
			turnGunRight(eGun);
		}

		fire(3 / 1 + eDistance / minSize);
	}

	private void updateEnemy(ScannedRobotEvent e) {
		eVelocity = e.getVelocity();
		eHeading = e.getHeading();

		eDistance = e.getDistance();
		eX = getX() + e.getDistance() * Math.cos(getRadarHeading());
		eY = getY() + e.getDistance() * Math.sin(getRadarHeading());
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		if (eVelocity == 0) {
			moveGun(e.getBearing());
			fire(3);
		} else {
			turnRight(e.getBearing() + 80);
			back(40);

			// if (getX() < getBattleFieldWidth() * 0.15) {
			//
			// } else if (getX() > getBattleFieldWidth() * 0.85) {
			// }
			// if (getY() < getBattleFieldHeight() * 0.15) {
			// } else if (getY() > getBattleFieldHeight() * 0.85) {
			// }
		}
	}

	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		turnRight(e.getBearing() + 60);
		back(minSize / 4);
	}

	public void onHitRobot(HitRobotEvent e) {
		//moveGun(e.getBearing());
		if (e.getEnergy() > getEnergy()) {
			turnRight(e.getBearing() + 70);
			back(minSize / 4);
		}

	}

	public void onBulletHit(BulletHitEvent event) {
		eEnergy = event.getEnergy();
	}
}
