package es.atp;

import robocode.AdvancedRobot;
import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.Condition;
import robocode.CustomEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;
import robocode.StatusEvent;

public class PRObot extends AdvancedRobot {

	double eHeading = 0.0;
	double eDistance = 0.0;
	double eEnergy = 100.0;
	double eVelocity = 0.0;
	double eBearing = 0.0;
	// /**
	// * Track enemy position
	// */
	double eX = 0.0;
	double eY = 0.0;
	double minSize;
	double maxSize;
	double movement = 1.0;
	double safeDistance = 200;
	boolean avoidWall = false;

	double wallMargin;
	double hits = 1;
	double hitsBy = 1;
	double missed = 1;

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
		maxSize = Math.max(getBattleFieldHeight(), getBattleFieldWidth());
		wallMargin = minSize * 0.2;
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

		double deltaEnergy = eEnergy - e.getEnergy();
		updateEnemy(e);
		rotate(e);
		moveGun(e.getBearing());
		move(e, deltaEnergy);
		moveRadar(e.getBearing());
		execute();

	}

	private void rotate(ScannedRobotEvent e) {
		double angle = e.getBearing() + 90;

		if (eDistance > minSize / 3 || avoidWall) {
			angle -= 15 * movement;
			System.out.println(angle);
		} else if (eDistance < safeDistance) {
			System.out.println("seguridad:" + safeDistance);
			angle += 15 * movement;
		}
		System.out.println("gira esto : " + angle);

		setTurnRight(shortestAngle(angle));
	}

	private void move(ScannedRobotEvent e, double deltaEnergy) {

		// dodge if enemy's shooting
		if ((deltaEnergy > 0.1 && deltaEnergy < 3.1) || Math.abs(eHeading) < 5) {

			setAhead(eDistance / 60 * (30 - (20 * eDistance / maxSize)) * movement);
			System.out.println();
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
		eBearing = e.getBearing();
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
		double extra = Math.random() * 1.5 * eVelocity;
		gunTurn += extra;
		setTurnGunRight(shortestAngle(gunTurn));
		if (missed <= hits || hitsBy > hits || eDistance < 100 || eEnergy < 10
				|| Math.random() < hits / (missed + hitsBy))
			if (eDistance > 100)
				setFire(1 + (1 - eDistance / minSize));
			else {
				setFire(3);
			}
	}

	@Override
	public void onHitByBullet(HitByBulletEvent event) {
		hitsBy++;
		safeDistance += event.getPower();
		safeDistance = Math.min(safeDistance, minSize / 4);
		if (!(getX() <= wallMargin || getX() >= getBattleFieldWidth() - wallMargin || getY() <= wallMargin
				|| getY() >= getBattleFieldHeight() - wallMargin)) {
			movement = -movement;
		}
	}

	@Override
	public void onBulletMissed(BulletMissedEvent event) {
		missed++;

	}

	@Override
	public void onHitWall(HitWallEvent event) {
		avoidWall = true;
		movement = -movement;
		setAhead(movement * 200);
	}

	public void onCustomEvent(CustomEvent e) {
		if (e.getCondition().getName().equals("avoid_wall"))
			if (!avoidWall) {
				avoidWall = true;
				movement = -movement;
			} else if (Math.abs(getDistanceRemaining()) < 5) {
				avoidWall = false;
			}
	}

	@Override
	public void onBulletHit(BulletHitEvent e) {
		hits++;
		eEnergy = e.getEnergy();
		eX = e.getBullet().getX();
		eY = e.getBullet().getY();

		if (eEnergy < getEnergy()) {
			safeDistance -= e.getBullet().getPower();
		}
	}

	@Override
	public void onHitRobot(HitRobotEvent e) {
		setFire(3);
		setTurnRight(shortestAngle(e.getBearing() + 90));
		setAhead(safeDistance * movement);
		execute();

	}
}
