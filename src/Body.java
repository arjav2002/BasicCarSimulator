import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public abstract class Body {

	Vector size, posn, vel, force, theta, omega, torque;
	double mass;
	Vector momentOfInertia;
	Queue<Force> forces;
	
	public Body(Vector posn, Vector size)
	{
		this.posn = posn;
		this.size = size;
		vel = new Vector();
		force = new Vector();
		theta = new Vector();
		omega = new Vector();
		torque = new Vector();
		forces = new LinkedList<Force>();
		mass = 100;
		momentOfInertia = new Vector(10000, 10000, 10000);
	}
	
	protected void processForces() {
		Iterator<Force> iterator = forces.iterator();
		force = new Vector();
		torque = new Vector();
		while(iterator.hasNext()) {
			Force f = iterator.next();
			force = force.add(f.amount);
			torque = torque.add(f.posn.subtract(posn).cross(f.amount));
		}
		forces.clear();
	}
	
	public void tick() {
		processForces();
		vel = vel.add(force.divide(mass));
		omega = omega.add(new Vector(torque.x/momentOfInertia.x, torque.y/momentOfInertia.y, torque.z/momentOfInertia.z));
		posn = posn.add(vel);
		theta = theta.add(omega);
		//System.out.println(vel.y + " " + posn.y);
	}
	
}
