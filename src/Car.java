import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class Car extends Body {

	private Window window;
	private double tireAngle;
	private Tire tires[];
	Vector axle;
	Vector up;
	Vector heading;
	
	public Car(Window window, Vector posn) {
		super(posn, new Vector(50, 150, 50));
		this.window = window;
		tireAngle = 0;
		tires = new Tire[4];
		axle = new Vector(-1, 0, 0);
		up = new Vector(0, 0, 1);
		heading = new Vector(0, 1, 0);
		tires[0] = new Tire(this, TirePosn.FL);
		tires[1] = new Tire(this, TirePosn.FR);
		tires[2] = new Tire(this, TirePosn.BR);
		tires[3] = new Tire(this, TirePosn.BL);
	}

	public void render(Graphics g)
	{
		g.setColor(Color.GREEN);
		g.fillRect((int)posn.x-(int)size.x/2, (int)posn.y+(int)size.y/2, (int)size.x, (int)size.y);
	}
	
	public void accelerate(double accel) {
		for(Tire t : tires) {
			forces.add(new Force(t.tick(axle.multiply(accel)), t.posn));
		}
	}

	@Override
	public void tick() {
		if(window.getKey(KeyEvent.VK_UP)) {
			if(vel.y < 0) accelerate(50);
			else accelerate(0.05);
		}
		else if(window.getKey(KeyEvent.VK_DOWN)) {
			if(vel.y > 0) accelerate(-50000);
			else accelerate(-0.05);
		}
		else {
			for(Tire t : tires) {
				forces.add(new Force(t.tick(new Vector()), t.posn));
			}
		}
		
		if(window.getKey(KeyEvent.VK_RIGHT)) {
			if(tireAngle > -50) {
				tireAngle -= 0.2;
			}
		}
		else if(window.getKey(KeyEvent.VK_LEFT)) {
			if(tireAngle < 50) {
				tireAngle += 0.2;
			}
		}
		else {
			if(tireAngle > 0) {
				tireAngle -= 0.2;
			}
			else if(tireAngle < 0) {
				tireAngle += 0.2;
			}
		}
		
		super.tick();
	}
	
}
