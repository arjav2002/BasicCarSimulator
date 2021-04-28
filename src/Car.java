import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

public class Car extends Body {

	private Window window;
	private Wheel wheels[];
	Vector axle;
	Vector up;
	Vector heading;
	private double radius;
	private double rightTireAngle, leftTireAngle;
	private double extentOfTurning;
	private static final double carLength = 150, carBreadth = 50;
	private static final Vector defaultHeading = new Vector(0, 1, 0);
	private static final Vector defaultUp = new Vector(0, 0, 1);
	private Force frForce, flForce, brForce, blForce;
	private Vector frOmega, flOmega;
	private int gear;
	private static final double defaultAccel = 1000;
	
	public Car(Window window, Vector posn) {
		super(posn, new Vector(50, carLength, carBreadth));
		this.window = window;
		radius = 0;
		wheels = new Wheel[4];
		axle = new Vector();
		up = defaultUp;
		heading = defaultHeading;
		rightTireAngle = leftTireAngle = 0;
		wheels[0] = new Wheel(this, TirePosn.FL);
		wheels[1] = new Wheel(this, TirePosn.FR);
		wheels[2] = new Wheel(this, TirePosn.BR);
		wheels[3] = new Wheel(this, TirePosn.BL);
		extentOfTurning = 0;
		frForce = flForce = brForce = blForce = new Force(posn, posn);
		frOmega = flOmega = new Vector();
	}

	public void render(Graphics g)
	{
		g.setColor(Color.GREEN);
		int x = (int)posn.x-(int)size.x/2;
		int y = -(int)posn.y-(int)size.y/2;
		GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 4);
		polygon.moveTo(Window.WIDTH - wheels[0].posn.x, wheels[0].posn.y);
		for(int i = 1; i < 4; i++) {
			polygon.lineTo(Window.WIDTH - wheels[i].posn.x, wheels[i].posn.y);
		}
		polygon.closePath();
		((Graphics2D)g).draw(polygon);
		
		g.setFont(new Font("TimesRoman", Font.PLAIN, 25));
		g.drawString("Extent of turning: " + extentOfTurning, Window.WIDTH/4, 100);
		g.drawString("Torque: " + torque.z, Window.WIDTH/4, 200);
		g.drawString("FL omega : " + flOmega, Window.WIDTH/4, 400);
		g.drawString("FL tire angle: " + Math.toDegrees(leftTireAngle), Window.WIDTH/4, 500);
		g.drawString("FR omega : " + frOmega, Window.WIDTH*2/3, 400);
		g.drawString("FR tire angle: " + Math.toDegrees(rightTireAngle), Window.WIDTH*2/3, 500);
		g.drawString("Car velocity: " + vel, Window.WIDTH/4, 600);
		
		renderWheels(g);
		renderVector(axle.multiply(50), posn, g, Color.BLUE);
		renderVector(heading.multiply(50), posn, g, Color.GREEN);
	}
	
	public void accelerate(double accel, double dt, boolean applyBrakes) {
		for(Wheel t : wheels) {
			double tireAngle = 0;
			if(t.getTirePosn() == TirePosn.FL) tireAngle = leftTireAngle;
			else if(t.getTirePosn() == TirePosn.FR) tireAngle = rightTireAngle;
			Force f;
			if(accel == 0) {
				f = new Force(t.tick(t.omega.invert().divide(10000*dt)/*retardation torque due to internal parts*/, dt, applyBrakes, tireAngle), t.posn);
				forces.add(f);
			}
			else {
				Vector axleDirection = axle;
				if(t.getTirePosn() == TirePosn.BR || t.getTirePosn() == TirePosn.FR) {
					axleDirection.invert();
				}
				axleDirection = Quaternion.rotateAxisAngle(axleDirection, up, tireAngle);
				f = new Force(t.tick(axle.multiply(accel), dt, applyBrakes, tireAngle), t.posn);
				forces.add(f);
			}
			if(t.getTirePosn() == TirePosn.FL) {
				flForce = f;
				flOmega = t.omega;
			}
			else if(t.getTirePosn() == TirePosn.FR) {
				frForce = f;
				frOmega = t.omega;
			}
			else if(t.getTirePosn() == TirePosn.BR) {
				brForce = f;
			}
			else blForce = f;
		}
	}

	@Override
	public void tick(double dt) {
		double accel = 0;
		boolean applyBrakes = false;
		heading = Quaternion.rotateEuler(defaultHeading, theta);
		up = Quaternion.rotateEuler(defaultUp, theta);
		axle = up.cross(heading);
		
		
		if(window.getKey(KeyEvent.VK_UP)) {
			if(vel.dot(heading)/heading.mag() < -1) applyBrakes = true;
			else accel = defaultAccel;
		}
		else if(window.getKey(KeyEvent.VK_DOWN)) {
			if(vel.dot(heading)/heading.mag() > 1) applyBrakes = true;
			else accel = -defaultAccel;
		}
		
		if(window.getKey(KeyEvent.VK_RIGHT) && rightTireAngle > -Math.PI/6) {
			extentOfTurning += 0.0000002;
		}
		else if(window.getKey(KeyEvent.VK_LEFT) && leftTireAngle < Math.PI/6) {
			extentOfTurning -= 0.0000002;
		}
		else {
			if(extentOfTurning > 0) {
				extentOfTurning -= 0.0000002;
			}
			else if(extentOfTurning < 0) {
				extentOfTurning += 0.0000002;
			}
		}
		
		if(extentOfTurning > 0) {
			radius = 1/(extentOfTurning);
			rightTireAngle = -Math.atan(carLength/(radius - carBreadth));
			leftTireAngle = -Math.atan(carLength/radius);
		}
		else if(extentOfTurning < 0) {
			radius = 1/(extentOfTurning);
			rightTireAngle = -Math.atan(carLength/radius);
			leftTireAngle = -Math.atan(carLength/(radius + carBreadth));
		}
		
		accelerate(accel, dt, applyBrakes);
		
		super.tick(dt);
	}
	
	public double getCarBreadth() { return carBreadth; }
	
	public double getCarLength() { return carLength; }
	
	// TODO draw force vectors on tires
	private void renderWheels(Graphics g) {
		for(Wheel w : wheels) {
			g.setColor(Color.BLACK);
			Vector v = axle.multiply(Wheel.thickness/2);
			if(w.getTirePosn() == TirePosn.BR || w.getTirePosn() == TirePosn.FR) v = v.invert();
			double angle = 0;
			if(w.getTirePosn() == TirePosn.FL) {
				angle = leftTireAngle;
			}
			else if(w.getTirePosn() == TirePosn.FR) {
				angle = rightTireAngle;
			}
			Vector vecs[] = {
					Quaternion.rotateAxisAngle(v.add(heading.multiply(Wheel.radius)), up, angle).add(w.posn),
					Quaternion.rotateAxisAngle(v.invert().add(heading.multiply(Wheel.radius)), up, angle).add(w.posn),
					Quaternion.rotateAxisAngle(v.invert().subtract(heading.multiply(Wheel.radius)), up, angle).add(w.posn),
					Quaternion.rotateAxisAngle(v.subtract(heading.multiply(Wheel.radius)), up, angle).add(w.posn)
					};
			GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 4);
			polygon.moveTo(Window.WIDTH - vecs[0].x, vecs[0].y);
			for(int i = 1; i < 4; i++) {
				polygon.lineTo(Window.WIDTH - vecs[i].x, vecs[i].y);
			}
			polygon.closePath();
			((Graphics2D)g).draw(polygon);
			renderVector(w.omega.multiply(30), w.posn, g, Color.RED);
			Force force;
			switch(w.getTirePosn()) {
			case BL:
				force = blForce;
				break;
			case BR:
				force = brForce;
				break;
			case FR:
				force = frForce;
				break;
			default:
				force = flForce;
			}
			renderVector(force.amount.divide(2), force.posn, g, Color.BLUE);
		}
	}
	
	private void renderVector(Vector vector, Vector posn, Graphics g, Color c) {
		g.setColor(c);
		drawArrow(g, (int)posn.x, (int)posn.y, (int)vector.add(posn).x, (int)vector.add(posn).y);
	}
	
	 private final int ARR_SIZE = 4;

     void drawArrow(Graphics g1, int x1, int y1, int x2, int y2) {
         Graphics2D g = (Graphics2D) g1.create();

         x1 = Window.WIDTH - x1;
         x2 = Window.WIDTH - x2;
         double dx = x2 - x1, dy = y2 - y1;
         double angle = Math.atan2(dy, dx);
         int len = (int) Math.sqrt(dx*dx + dy*dy);
         AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
         at.concatenate(AffineTransform.getRotateInstance(angle));
         g.transform(at);

         // Draw horizontal arrow starting in (0, 0)
         g.drawLine(0, 0, len, 0);
         g.fillPolygon(new int[] {len, len-ARR_SIZE, len-ARR_SIZE, len},
                       new int[] {0, -ARR_SIZE, ARR_SIZE, 0}, 4);
     }
}
