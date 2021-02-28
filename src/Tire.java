
public class Tire {

	private static final double radius = 10;
	private Car car;
	private TirePosn tirePosn;
	 Vector posn;
	private Vector omega;
	double moi = 10;
	
	public Tire(Car car, TirePosn tirePosn) {
		this.car = car;
		this.tirePosn = tirePosn;
		posn = getRelPosn(car, tirePosn).add(car.posn);
		omega = new Vector();
	}

	
	public Vector tick(Vector torque) {
		posn = getRelPosn(car, tirePosn).add(car.posn);
		omega = omega.add(torque).divide(moi);
		
		System.out.println("New omega: " + omega);
		System.out.println("Car vel: " + car.vel);
		
		Vector friction = omega.cross(car.up.invert().multiply(radius)).subtract(car.vel);
		if(friction.mag() != 0) friction = friction.divide(friction.mag()).multiply(car.mass*0.0005);
		System.out.println("Friction: " + friction);
		
		omega = omega.add(car.up.invert().multiply(radius).cross(friction).divide(moi));
		System.out.println("Omega after friction: " + omega);
		
		long last = System.currentTimeMillis();
		//while(System.currentTimeMillis() - last < 1000L);
		return friction;
	}
	
	private static Vector getRelPosn(Car car, TirePosn tirePosn) {
		switch(tirePosn) {
		case BL:
			return car.axle.subtract(car.heading);
		case BR:
			return car.axle.invert().subtract(car.heading);
		case FR:
			return car.axle.invert().add(car.heading);
		case FL:
			return car.axle.add(car.heading);
		}
		return new Vector();
	}

}
