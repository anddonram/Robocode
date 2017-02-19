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

	// /**
	// * Track enemy position
	// */
	// double eX = 0.0;
	// double eY = 0.0;

	double minSize = 0.0;
	/**
	 * is the enemy shooting strong bullets?
	 */
	boolean hittingHard = false;

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
		}
	}

	/**
	 * 
	 * @param bearing
	 * @param flee
	 *            -1.0 if fleeing, 1.0 if getting closer
	 */
	private void moveAround(double bearing, double flee) {
		turnRight(bearing + 80);
		ahead(flee * eDistance * 0.6);
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		double deltaEnergy = eEnergy - e.getEnergy();
		updateEnemy(e);
		// dodge if enemy's shooting
		if (deltaEnergy > 0.0 && deltaEnergy < 4.0 && getEnergy() <= eEnergy) {
			moveAround(e.getBearing(), 1.0);
		} else if (deltaEnergy < 0.0 && hittingHard) {

			if (getEnergy() >= eEnergy) {
				// attack if we are stronger
				moveRadar(e.getBearing());

				if (eDistance <= minSize / 1.5)
					moveGun(e.getBearing());
				else
					moveAround(e.getBearing(), 1.0);
			} else {
				// flee otherwise
				moveAround(e.getBearing(), -1.0);
			}
		} else {
			// attack
			moveRadar(e.getBearing());
			moveGun(e.getBearing());

		}
	}

	private void moveRadar(double bearing) {
		double eRadar = bearing + getHeading() - getRadarHeading();
		if (eRadar > 180) {
			turnRadarLeft(360 - eRadar);
		} else if (eRadar < -180) {
			turnRadarRight(360 + eRadar);
		} else {
			turnRadarRight(eRadar);
		}

	}

	private void moveGun(double bearing) {
		double absBearing = bearing + getHeading();
		double gunTurn = absBearing - getGunHeading();
		double extra = eVelocity * 4 * (eDistance / minSize) * Math.sin(Math.toRadians(eHeading - absBearing));
		System.out.println("hay que girar: " + gunTurn);
		System.out.println("y de propina: " + extra);
		gunTurn += extra;
		if (gunTurn > 180) {
			turnGunLeft(360 - gunTurn);
		} else if (gunTurn < -180) {
			turnGunRight(360 + gunTurn);
		} else {
			turnGunRight(gunTurn);
		}

		fire(3 / (1 + eDistance / minSize));
	}

	private void updateEnemy(ScannedRobotEvent e) {
		eVelocity = e.getVelocity();
		eHeading = e.getHeading();
		eEnergy = e.getEnergy();
		eDistance = e.getDistance();
		// eX = getX() + e.getDistance() * Math.cos(getRadarHeading());
		// eY = getY() + e.getDistance() * Math.sin(getRadarHeading());
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {

		if (eEnergy > getEnergy())
			moveAround(e.getBearing(), -1.5);
		hittingHard = e.getBullet().getPower() > 2;

	}

	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		turnRight(e.getBearing() + 60);
		back(minSize / 3);
	}

	public void onHitRobot(HitRobotEvent e) {
		// moveGun(e.getBearing());
		if (e.getEnergy() > getEnergy()) {
			turnRight(e.getBearing() + 70);
			back(minSize / 3);
		}

	}

	public void onBulletHit(BulletHitEvent event) {
		eEnergy = event.getEnergy();
	}
}
