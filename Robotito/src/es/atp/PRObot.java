package es.atp;

import robocode.AdvancedRobot;
import robocode.BulletHitEvent;
import robocode.HitByBulletEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;
import robocode.StatusEvent;

public class PRObot extends AdvancedRobot {

	double eHeading = 0.0;
	double eDistance = 0.0;
	double eEnergy = 100.0;
	double eVelocity = 0.0;

	// /**
	// * Track enemy position
	// */
	double eX = 0.0;
	double eY = 0.0;
	double minSize;
	double movement = 1.0;
	double wallBearing;
	boolean moveMore = false;

	private double shortestAngle(double angle) {
		double res;
		if (angle > 180) {
			res = angle - 360;
		} else if (angle < -180) {
			res = 360 + angle;
		} else
			res = angle;
		return res;
	}

	public void run() {
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn(true);
		minSize = Math.min(getBattleFieldHeight(), getBattleFieldWidth());

	}

	@Override
	public void onStatus(StatusEvent e) {

		setTurnRadarRight(360);
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent e) {
		double deltaEnergy = eEnergy - e.getEnergy();
		updateEnemy(e);
		rotate(e);
		moveGun(e.getBearing());
		moveRadar(e.getBearing());
		move(e, deltaEnergy);

		execute();
	}

	private void rotate(ScannedRobotEvent e) {
		double angle = e.getBearing() + 90;
		if (moveMore) {
			angle += wallBearing - 90;
			angle /= 2;
		} else {
			double d = Math.signum(shortestAngle(angle));
			if (eDistance > minSize / 2) {
				angle += 10 * d;
			} else if (eDistance < 100) {
				angle -= 10 * d;
			}
		}
		System.out.println("gira esto : " + angle);

		setTurnRight(shortestAngle(angle ));
	}

	private void move(ScannedRobotEvent e, double deltaEnergy) {

		// dodge if enemy's shooting
		if (deltaEnergy > 0.1 && deltaEnergy < 3.1) {
			if (moveMore) {

				setAhead(e.getDistance() * 2 / 3 * movement);
				moveMore = false;
			} else {
				setAhead(e.getDistance() * 2 / 3 * movement);
			}

			// if (getX() <= getBattleFieldWidth() * 0.25 || getX() >=
			// getBattleFieldWidth() * 0.75
			// || getY() <= getBattleFieldHeight() * 0.25 || getY() >=
			// getBattleFieldHeight() * 0.75)

		}

	}

	private void updateEnemy(ScannedRobotEvent e) {
		eVelocity = e.getVelocity();
		eHeading = e.getHeading();
		eEnergy = e.getEnergy();
		eDistance = e.getDistance();
		eX = getX() + eDistance * Math.cos(getRadarHeading());
		eY = getY() + eDistance * Math.sin(getRadarHeading());
	}

	private void moveRadar(double bearing) {
		double eRadar = bearing + getHeading() - getRadarHeading();
		setTurnRadarRight(shortestAngle(eRadar));
	}

	private void moveGun(double bearing) {
		double absBearing = bearing + getHeading();
		double gunTurn = absBearing - getGunHeading();
		double extra = Math.random() * 5 * (eVelocity > 0 ? 1 : -1);
		// System.out.println("hay que girar: " + gunTurn);
		// System.out.println("y de propina: " + extra);
		gunTurn += extra;
		setTurnGunRight(shortestAngle(gunTurn));

		fire(1 + (1 - eDistance / minSize));
	}

	@Override
	public void onHitByBullet(HitByBulletEvent event) {

	}

	@Override
	public void onHitWall(HitWallEvent event) {
		moveMore = true;
		movement = -movement;
		wallBearing = event.getBearing();
		// setTurnRight((90 + event.getBearing()));
		// setAhead(200);
	}

	@Override
	public void onBulletHit(BulletHitEvent e) {
		eEnergy = e.getEnergy();
		eX = e.getBullet().getX();
		eY = e.getBullet().getY();
	}

}
