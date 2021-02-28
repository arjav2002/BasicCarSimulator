import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

public class Window extends JFrame implements KeyListener {
	
	private int width, height;
	private BufferStrategy bs;
	private Canvas canvas;
	private Car car;
	private boolean keys[];
	
	public Window(String title, int width, int height) {
		this.width = width;
		this.height = height;
		setSize(width, height);
		setTitle(title);
		canvas = new Canvas();
		canvas.setMinimumSize(new Dimension(width, height));
		canvas.setPreferredSize(new Dimension(width, height));
		canvas.setMaximumSize(new Dimension(width, height));
		canvas.setFocusable(false);
		add(canvas);
		addKeyListener(this);
		pack();
		setVisible(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		car = new Car(this, new Vector(200, 200, 0));
		bs = null;
		keys = new boolean[256];
	}
	
	public void render() {
		if(bs == null)
		{
			canvas.createBufferStrategy(2);
		}
		bs = canvas.getBufferStrategy();
		Graphics g = bs.getDrawGraphics();
		g.clearRect(0, 0, width, height);
		
		car.render(g);
		
		bs.show();
		g.dispose();
	}
	
	public void tick() {
		car.tick();
	}
	
	public static void main(String[] args) {
		Window w = new Window("Gea", 1280, 1080);
		FrameTimer ft = new FrameTimer(60);
		while(true) {
			if(ft.shouldRenderNext()) {
				ft.mark();
				w.tick();
				w.render();
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;	
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false; 
	}
	
	public boolean getKey(int keycode) {
		return keys[keycode];
	}

}
