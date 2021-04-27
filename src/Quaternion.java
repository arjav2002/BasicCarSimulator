/******************************************************************************
 *  Compilation:  javac Quaternion.java
 *  Execution:    java Quaternion
 *
 *  Data type for quaternions.
 *
 *  http://mathworld.wolfram.com/Quaternion.html
 *
 *  The data type is "immutable" so once you create and initialize
 *  a Quaternion, you cannot change it.
 *
 *  % java Quaternion
 *
 ******************************************************************************/

public class Quaternion {
    private final double x0, x1, x2, x3; 
    
    // create a new object with the given components
    public Quaternion(double x0, double x1, double x2, double x3) {
        this.x0 = x0;
        this.x1 = x1;
        this.x2 = x2;
        this.x3 = x3;
    }

    // return a string representation of the invoking object
    public String toString() {
        return x0 + " + " + x1 + "i + " + x2 + "j + " + x3 + "k";
    }

    // return the quaternion norm
    public double norm() {
        return Math.sqrt(x0*x0 + x1*x1 +x2*x2 + x3*x3);
    }

    // return the quaternion conjugate
    public Quaternion conjugate() {
        return new Quaternion(x0, -x1, -x2, -x3);
    }

    // return a new Quaternion whose value is (this + b)
    public Quaternion plus(Quaternion b) {
        Quaternion a = this;
        return new Quaternion(a.x0+b.x0, a.x1+b.x1, a.x2+b.x2, a.x3+b.x3);
    }


    // return a new Quaternion whose value is (this * b)
    public Quaternion times(Quaternion b) {
        Quaternion a = this;
        double y0 = a.x0*b.x0 - a.x1*b.x1 - a.x2*b.x2 - a.x3*b.x3;
        double y1 = a.x0*b.x1 + a.x1*b.x0 + a.x2*b.x3 - a.x3*b.x2;
        double y2 = a.x0*b.x2 - a.x1*b.x3 + a.x2*b.x0 + a.x3*b.x1;
        double y3 = a.x0*b.x3 + a.x1*b.x2 - a.x2*b.x1 + a.x3*b.x0;
        return new Quaternion(y0, y1, y2, y3);
    }

    // return a new Quaternion whose value is the inverse of this
    public Quaternion inverse() {
        double d = x0*x0 + x1*x1 + x2*x2 + x3*x3;
        return new Quaternion(x0/d, -x1/d, -x2/d, -x3/d);
    }


    // return a / b
    // we use the definition a * b^-1 (as opposed to b^-1 a)
    public Quaternion divides(Quaternion b) {
        Quaternion a = this;
        return a.times(b.inverse());
    }

    public Quaternion divideBy(double quotient) {
    	return new Quaternion(x0/quotient, x1/quotient, x2/quotient, x3/quotient);
    }
    

    // sample client for testing
    public static void main(String[] args) {
        Quaternion a = new Quaternion(3.0, 1.0, 0.0, 0.0);
        System.out.println("a = " + a);

        Quaternion b = new Quaternion(0.0, 5.0, 1.0, -2.0);
        System.out.println("b = " + b);

        System.out.println("norm(a)  = " + a.norm());
        System.out.println("conj(a)  = " + a.conjugate());
        System.out.println("a + b    = " + a.plus(b));
        System.out.println("a * b    = " + a.times(b));
        System.out.println("b * a    = " + b.times(a));
        System.out.println("a / b    = " + a.divides(b));
        System.out.println("a^-1     = " + a.inverse());
        System.out.println("a^-1 * a = " + a.inverse().times(a));
        System.out.println("a * a^-1 = " + a.times(a.inverse()));
    }
    
    public static Quaternion fromEuler(Vector theta) {
    	double p = theta.y / 2.0;
    	double y = theta.z / 2.0;
    	double r = theta.x / 2.0;
    	double sinp = Math.sin(p);
    	double siny = Math.sin(y);
    	double sinr = Math.sin(r);
    	double cosp = Math.cos(p);
    	double cosy = Math.cos(y);
    	double cosr = Math.cos(r);
    	Quaternion q = new Quaternion(cosr * cosp * cosy + sinr * sinp * siny, sinr * cosp * cosy - cosr * sinp * siny, cosr * sinp * cosy + sinr * cosp * siny, cosr * cosp * siny - sinr * sinp * cosy);
    	q = q.divideBy(q.norm());
    	return q;
    }
    
    public static Vector rotateEuler(Vector vec, Vector theta) {
    	return rotateFromQuaternion(vec, fromEuler(theta));
    }
    
    public static Vector rotateAxisAngle(Vector vec, Vector axis, double angle) {
    	double sinAngle = Math.sin(angle);
    	Quaternion q = new Quaternion(Math.cos(angle), axis.x * sinAngle, axis.y * sinAngle, axis.z * sinAngle);
    	return rotateFromQuaternion(vec, q);
    }
    
    public static Vector rotateFromQuaternion(Vector vec, Quaternion q) {
    	Quaternion resQ = q.times(new Quaternion(0, vec.x, vec.y, vec.z)).times(q.conjugate());
    	return new Vector(resQ.x1, resQ.x2, resQ.x3);
    }
}