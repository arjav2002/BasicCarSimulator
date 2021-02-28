
public class Force {

	Vector amount;
	Vector posn;
	
	public Force(Vector amount, Vector posn) {
		this.amount = amount;
		this.posn = posn;
	}
	
	public Force getOpp() {
		return new Force(new Vector(-amount.x, -amount.y, -amount.y), posn);
	}
}
