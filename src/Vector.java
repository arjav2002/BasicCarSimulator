
public class Vector {
	
	public double x, y, z;
	
	public Vector(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector()
	{
		x = y = z = 0;
	}
	
	public Vector add(Vector v2)
	{
		return new Vector(x+v2.x, y+v2.y, z+v2.z);
	}
	
	public Vector multiply(double multiplier) {
		return new Vector(x*multiplier, y*multiplier, z*multiplier);
	}
	
	public Vector divide(double divisor) {
		return new Vector(x/divisor, y/divisor, z/divisor);
	}
	
	public Vector subtract(Vector v2) {
		return new Vector(x-v2.x, y-v2.y, z-v2.z);
	}
	
	public Vector cross(Vector v2) {
		return new Vector(y*v2.z-z*v2.y, z*v2.x-x*v2.z, x*v2.y-y*v2.x);
	}
	
	public Vector invert() {
		return new Vector(-x, -y, -z);
	}
	
	public double mag() {
		return Math.sqrt(x*x+y*y+z*z);
	}
	
	public boolean equals(Vector v) {
		return v.x==x && v.y==y && v.z == z;
	}
	
	public String toString() {
		return x + ", " + y + ", " + z;
	}

	public Vector normalise() {
		double mag = mag();
		if(mag != 0) return new Vector(x/mag, y/mag, z/mag);
		return new Vector();
	}
	
	public static double angle(Vector v1, Vector v2) {
		return Math.toDegrees(Math.acos(v1.dot(v2)/v1.mag()/v2.mag()));
	}
	
	public double dot(Vector v) {
		return x*v.x + y*v.y + z*v.z;
	}
	
	public Vector clone() {
		return new Vector(x, y, z);
	}

}
