
public class Wheel {

	public static final double radius = 10;
	public static final double thickness = 4;
	private Car car;
	private TirePosn tirePosn;
	 Vector posn;
	public Vector omega;
	double moi = 10;
	double mu = 0.5;
	double maxBrakeTorqueMag = 5000*radius;
	double g = 9.8;
	
	public Wheel(Car car, TirePosn tirePosn) {
		this.car = car;
		this.tirePosn = tirePosn;
		posn = getRelPosn(car, tirePosn).add(car.posn);
		omega = new Vector();
	}

	
	public Vector tick(Vector torque, double dt, boolean applyBrake, double tireAngle) {
		posn = getRelPosn(car, tirePosn).add(car.posn);
		
		// make omega same as steering
		// if omega pointing in direction of axle, i.e. outwards for left and inwards for right tires
		// set omega to vector with mag same as omega, direction of axle and rotated by tireAngle
		// else set omega to vector with mag same as omega, direction opposite of axle and rotated by tireAngle
		double dot = omega.dot(car.axle);
		if(dot > 0) {
			omega = Quaternion.rotateAxisAngle(car.axle, car.up, tireAngle).multiply(omega.mag()); 
		}
		else {
			omega = Quaternion.rotateAxisAngle(car.axle, car.up, tireAngle).multiply(omega.mag()).multiply(-1); 
		}
		
		Vector radiusVec = car.up.invert().multiply(radius);
		
		if(applyBrake) {
			double brakeTorqueMag = Math.min(maxBrakeTorqueMag, omega.multiply(moi).divide(dt).mag());
			Vector brakeTorque = omega.normalise().invert().multiply(brakeTorqueMag);
			torque = brakeTorque;
		}
		
		Vector deltaOmegaAppliedTorque = torque.divide(moi).multiply(dt);
		omega = omega.add(deltaOmegaAppliedTorque);
				
		Vector relVel = car.vel.add(omega.cross(radiusVec)).add(car.omega.cross(getRelPosn(car, tirePosn)));
		Vector frictionDir = relVel.invert().normalise();
		
		double frictionMag = mu*car.mass*g/4; // static kinetic rolling all the same to me
		Vector deltaOmegaTireFriction = radiusVec.cross(frictionDir.multiply(frictionMag)).multiply(dt).divide(moi);
		omega = omega.add(deltaOmegaTireFriction);
		
		//if(tirePosn == TirePosn.FL) System.out.println("Friction: " + frictionDir.multiply(frictionMag) + "\tcar vel: " + car.vel + "\tangle: " + tireAngle);
		
		return frictionDir.multiply(frictionMag);
	}
	
	public static Vector getRelPosn(Car car, TirePosn tirePosn) {
		switch(tirePosn) {
		case BL:
			return car.axle.multiply(car.getCarBreadth()/2).subtract(car.heading.multiply(car.getCarLength()/2));
		case BR:
			return car.axle.invert().multiply(car.getCarBreadth()/2).subtract(car.heading.multiply(car.getCarLength()/2));
		case FR:
			return car.axle.invert().multiply(car.getCarBreadth()/2).add(car.heading.multiply(car.getCarLength()/2));
		case FL:
			return car.axle.multiply(car.getCarBreadth()/2).add(car.heading.multiply(car.getCarLength()/2));
		}
		return new Vector();
	}

	public TirePosn getTirePosn() {
		return tirePosn;
	}
	
}
