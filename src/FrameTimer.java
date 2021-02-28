
public class FrameTimer {

	private long lastTime;
	private final int FPS;
	
	public FrameTimer(int FPS) {
		lastTime = System.currentTimeMillis();
		this.FPS = FPS;
	}
	
	public boolean shouldRenderNext() {
		return System.currentTimeMillis() - lastTime >= 1.0/FPS;
	}
	
	public void mark() {
		lastTime = System.currentTimeMillis();
	}
	
}
