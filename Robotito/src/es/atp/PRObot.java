package es.atp;

import robocode.AdvancedRobot;
import robocode.BulletHitEvent;
import robocode.Condition;
import robocode.CustomEvent;
import robocode.HitByBulletEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;
import robocode.StatusEvent;
import robocode.WinEvent;

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
	double safeDistance = 200;
	boolean avoidWall = false;

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
		double wallMargin = minSize * 0.2;
		addCustomEvent(new Condition("avoid_wall") {
			public boolean test() {
				return getX() <= wallMargin || getX() >= getBattleFieldWidth() - wallMargin || getY() <= wallMargin
						|| getY() >= getBattleFieldHeight() - wallMargin;
			}
		});

	}

	@Override
	public void onStatus(StatusEvent e) {

		setTurnRadarRight(180);
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent e) {
		if (!avoidWall) {
			double deltaEnergy = eEnergy - e.getEnergy();
			updateEnemy(e);
			rotate(e);
			moveGun(e.getBearing());
			move(e, deltaEnergy);
			moveRadar(e.getBearing());
			execute();
		} else if (getDistanceRemaining() < 20) {
			avoidWall = false;
		}
	}

	private void rotate(ScannedRobotEvent e) {
		double angle = e.getBearing() + 90;

		 double d = Math.signum(shortestAngle(angle));
		if (eDistance > minSize / 2) {
			angle += 10 * movement*d;
			System.out.println(angle);
		} else if (eDistance < safeDistance) {
			System.out.println("seguridad:" + safeDistance);
			angle -= 10 * movement*d;
		}
		System.out.println("gira esto : " + angle);

		setTurnRight(shortestAngle(angle));
	}

	private void move(ScannedRobotEvent e, double deltaEnergy) {

		// dodge if enemy's shooting
		if (deltaEnergy > 0.1 && deltaEnergy < 3.1) {

			setAhead(e.getDistance() / 3 * movement);

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
		setTurnRadarRight(shortestAngle(eRadar + Math.signum(eRadar) * 5));
	}

	private void moveGun(double bearing) {
		double absBearing = bearing + getHeading();
		double gunTurn = absBearing - getGunHeading();
		double extra = Math.random() * 5 * (eVelocity > 0 ? 1 : -1);
		gunTurn += extra;
		setTurnGunRight(shortestAngle(gunTurn));
		if (eDistance > safeDistance)
			setFire(1 + (1 - eDistance / minSize));
		else {
			setFire(3);
		}
	}

	@Override
	public void onHitByBullet(HitByBulletEvent event) {
		safeDistance += event.getPower() * 5;
	}

	@Override
	public void onHitWall(HitWallEvent event) {
		// if (getVelocity() == 0) {
		// avoidWall = true;
		// movement = -movement;
		// wallBearing = event.getBearing();
		// System.out.println("auch" + movement);
		// }
		// setTurnRight((90 + event.getBearing()));
		// setAhead(200);
	}

	public void onCustomEvent(CustomEvent e) {
		if (e.getCondition().getName().equals("avoid_wall"))
			if (!avoidWall) {
				// switch directions and move away
				avoidWall = true;
				System.out.println("x: " + ((getBattleFieldWidth() / 2) - getX()) + ", y: "
						+ ((getBattleFieldHeight() / 2) - getY()));
				double angle = (Math.atan2((getBattleFieldHeight() / 2) - getY(), (getBattleFieldWidth() / 2) - getX())
						/ Math.PI + 1) * 180;
				System.out.println("angulo: " + angle);
				movement = -movement;

				setTurnRight(270 - angle - getHeading());
				setAhead(minSize / 3);
				execute();

				System.out.println("meh: " + movement);
			} else if (getDistanceRemaining() < 20) {
				avoidWall = false;
			}
	}

	@Override
	public void onBulletHit(BulletHitEvent e) {
		eEnergy = e.getEnergy();
		eX = e.getBullet().getX();
		eY = e.getBullet().getY();
		if (eEnergy < getEnergy()) {
			safeDistance -= e.getBullet().getPower() * 4;
		}
	}

	@Override
	public void onWin(WinEvent event) {
		setStop();
		execute();
	}
}
