import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


class titlePanel extends JPanel implements Runnable{
	JLabel nameLabel= new JLabel("수철이의 블록깨기");
	JLabel space= new JLabel("-Press SpaceBar to Start-");
	BufferedImage image=null;
	titlePanel(){
		URL url = getClass().getResource("back.jpg");
		try {
			image = ImageIO.read(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setLayout(null);
		Thread t = new Thread(this);
		t.start();
		//제목 폰트 설정
		Font nfont = new Font("맑은 고딕", Font.BOLD, 50);
		nameLabel.setFont(nfont);
		nameLabel.setLocation(82, 300);
		nameLabel.setSize(425,50);
		nameLabel.setForeground(Color.CYAN);
		add(nameLabel);
		//스페이스바 폰트 설정
		Font sfont = new Font("맑은 고딕", Font.BOLD, 30);
		space.setFont(sfont);
		space.setLocation(110, 800);
		space.setSize(370,40);
		space.setForeground(Color.red);
		add(space);
	}
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
		//g.drawLine(getWidth()/2, 0, getWidth()/2, getHeight());
	}
	@Override
	public void run() {	
		while(this.isVisible()) {		//보일때만 반복

			try {
				Thread.sleep(100);
				if(space.isVisible()) space.setVisible(false);
				else space.setVisible(true);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}

abstract class gameObj{
	double x, y;
	Color c;
	gameObj(double _x, double _y, Color _c){
		x = _x;
		y = _y;
		c = _c;
	}
	abstract void draw(Graphics2D g); 
	void update(double dt) {};
	abstract void collision(gameObj o);
}

class gWall extends gameObj{
	double w, h;
	gWall(double _x, double _y,double _w, double _h, Color _c) {
		super(_x, _y, _c);
		w = _w;
		h = _h;
	}

	@Override
	void draw(Graphics2D g) {
		g.setColor(c);
		g.fillRect((int)x, (int)y, (int)w, (int)h);
	}

	@Override
	void collision(gameObj o) {
		
	}
}

class pWall extends gWall{
	double vx;
	pWall(double _x, double _y, double _w, double _h, Color _c) {
		super(_x, _y, _w, _h, _c);
		// TODO Auto-generated constructor stub
		vx = 0;
	}
	@Override
	void draw(Graphics2D g) {
		g.setColor(c);
		g.fillRoundRect((int)x, (int)y, (int)w, (int)h, 10, 10);
	}
	@Override
	void update(double dt) {
		if(x + vx*dt > 10 && x + w + vx*dt < 580)
			x += vx * dt;
	}
	
}

class bWall extends gWall{
	BufferedImage image = null;
	bWall(double _x, double _y, double _w, double _h, Color _c) {
		super(_x, _y, _w, _h, _c);
	}
	@Override
	void draw(Graphics2D g) {
		g.setColor(c);
		g.fillRoundRect((int)x, (int)y, (int)w, (int)h, 10, 10);
		
	}
}

class gBall extends gameObj{
	double r;
	double vx, vy;
	float speed;
	double pre_X, pre_Y;
	gBall(double _x, double _y,double _r, Color _c) {
		super(_x, _y, _c);
		r = _r;
		
		pre_X = 0;
		pre_Y = 0;
		
		speed = 500.0f;
		float angle = (float)(Math.random() * -1.0*3.141592);
		vx = (float)(speed * Math.cos(angle));	
		vy = (float)(speed * Math.sin(angle));	
	}

	@Override
	void draw(Graphics2D g) {
		g.setColor(c);
		g.fillOval((int)(x-r), (int)(y-r), (int)(r*2), (int)(r*2));
	}
	
	@Override
	void update(double dt) {
		pre_X = x;
		pre_Y = y;
		
		x += vx * dt;
		y += vy * dt;
		
	}

	@Override
	void collision(gameObj o) {
		if(o instanceof gWall) {
			gWall w = (gWall)o;
			if(pre_X < w.x - r) {
				x = w.x - r;
				vx = -vx;
			}
			if(pre_X > w.x + w.w+r) {
				x = w.x + w.w + r;
				vx = -vx;
			}
			if(pre_Y < w.y - r) {
				y = w.y - r;
				vy = -vy;
			}
			if(pre_Y > w.y + w.h+r) {
				y = w.y + w.h + r;
				vy = -vy;
			}
		}
		if(o  instanceof pWall) {
			float speed = 500.0f;
			float angle = (float)(Math.random() * -1.0*3.141592);
			vx = (float)(speed * Math.cos(angle));	
			vy = (float)(speed * Math.sin(angle));	
			//System.out.println(angle + ","  + vx + ", " + vy);
		}

	}
	
	boolean isCollide(gameObj o) {
		if(o instanceof gWall) {
			gWall w  = (gWall)o;
			if(w.x - r < x && w.x + w.w + r > x
					&& w.y - r < y && w.y + w.h + r > y) {
				return true;
			}
		}
		return false;
	}
	
}

class mainPanel extends JPanel implements KeyListener, Runnable{
	boolean pressKey = false;
	int blocks = 0;
	int stage = 0;
	boolean isOver = false;
	int balls = 0;
	int score=0;
	boolean haveblock = false;
	Clip background;
	Clip breakBlock;
	Clip itemBlock;
	Clip balltouch;
	Clip next;
	Clip fail;
	Clip stageClear;
	LinkedList<gameObj>obj = new LinkedList<>();		//오브젝트 저장 변수
	Iterator<gameObj>it1;
	Iterator<gameObj>it2;
	Thread t = new Thread(this);
	mainPanel(){
		try {
			background = AudioSystem.getClip();
			breakBlock = AudioSystem.getClip();
			itemBlock = AudioSystem.getClip();
			balltouch = AudioSystem.getClip();
			next = AudioSystem.getClip();
			fail = AudioSystem.getClip();
			stageClear = AudioSystem.getClip();
			URL url1 = getClass().getClassLoader().getResource("videoplayback.wav");
			URL url2 = getClass().getClassLoader().getResource("blockBreak.wav");
			URL url3 = getClass().getClassLoader().getResource("itemBlock.wav");
			URL url4 = getClass().getClassLoader().getResource("balltouch.wav");
			URL url5 = getClass().getClassLoader().getResource("next.wav");
			URL url6 = getClass().getClassLoader().getResource("fail.wav");
			URL url7 = getClass().getClassLoader().getResource("stageClear.wav");
			
			AudioInputStream backstream = 
					AudioSystem.getAudioInputStream(url1);
			AudioInputStream breakstream = 
					AudioSystem.getAudioInputStream(url2);
			AudioInputStream itemstream = 
					AudioSystem.getAudioInputStream(url3);
			AudioInputStream touchstream = 
					AudioSystem.getAudioInputStream(url4);
			AudioInputStream nextstream = 
					AudioSystem.getAudioInputStream(url5);
			AudioInputStream failstream = 
					AudioSystem.getAudioInputStream(url6);
			AudioInputStream clearstream = 
					AudioSystem.getAudioInputStream(url7);
			background.open(backstream);
			breakBlock.open(breakstream);
			itemBlock.open(itemstream);
			balltouch.open(touchstream);
			next.open(nextstream);
			fail.open(failstream);
			stageClear.open(clearstream);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
		next.setFramePosition(0);
		next.start();
		background.loop(Clip.LOOP_CONTINUOUSLY);
		this.setBackground(Color.yellow);		//배경색
		obj.add(new pWall(200, 900, 100, 30, Color.blue));
		obj.add(new gWall(0,0,10,1000, Color.orange));
		obj.add(new gWall(575,0,10,1000, Color.orange));
		obj.add(new gWall(10,0,575,10,Color.orange));
		obj.add(new gBall(250, 850, 6, Color.red));
		balls++;
		setFocusable(true);
		addKeyListener(this);
	}
	void restart() {
		blocks = 0;
		stage = 0;
		isOver = false;
		obj.clear();
		obj.add(new pWall(200, 900, 100, 30, Color.blue));
		obj.add(new gWall(0,0,10,1000, Color.orange));
		obj.add(new gWall(575,0,10,1000, Color.orange));
		obj.add(new gWall(10,0,575,10,Color.orange));
		obj.add(new gBall(250, 850, 6, Color.red));
	}
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		for(int i =0; i < obj.size(); i++) {
			obj.get(i).draw(g2);
		}
		repaint();
	}
	public void keyTyped(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {
		if(pressKey) {
			pressKey = false;
			for(int i =0; i < obj.size(); i++) {
				if(obj.get(i) instanceof pWall) {
					pWall a = (pWall) obj.get(i);
					a.vx = 0;
					break;
				}
			}
		}
	}
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_LEFT: 
			pressKey = true;
			for(int i = 0; i < obj.size(); i++) {
				if(obj.get(i) instanceof pWall) {
					pWall a = (pWall) obj.get(i);
					a.vx = -200;
					break;
				}
			}
			break;
		case KeyEvent.VK_RIGHT: 
			pressKey = true;
			for(int i = 0; i < obj.size(); i++) {
				if(obj.get(i) instanceof pWall) {
					pWall a = (pWall) obj.get(i);
					a.vx = 200;
					break;
				}
			}
			break;
		}
	}
	@Override
	public void run() {
		while(this.isVisible()) {
			
			if(!hasFocus())
				requestFocus();
			try {
				haveblock = false;   
				for(int i = 0; i < obj.size(); i++) {
					obj.get(i).update(0.016f);
				}
				
				for (int i = 0; i<obj.size(); i++) {
					
					if(!(obj.get(i) instanceof gBall))continue;	//이게 공이면 충돌
					gBall gball = (gBall)obj.get(i);
					for(int j = 0; j<obj.size(); j++) {
						if(!(obj.get(j) instanceof gWall))continue; {
						gWall gwall = (gWall)obj.get(j);
							if(gwall instanceof bWall)
								haveblock = true;
							if(gball.isCollide(gwall)){
								gball.collision(gwall);
								if(gwall instanceof bWall) {
									obj.remove(j);
									score += 10;
									if(gwall.c.equals(Color.decode("#B5E61D"))) {
										gBall clone = addClone(gball);
										obj.add(clone);
										balls++;
										obj.add(addClone(clone));
										balls++;
										itemBlock.setFramePosition(0);
										itemBlock.start();
										
									}
									breakBlock.setFramePosition(0);
									breakBlock.start();								
									blocks--;
									if(blocks == 0) {
										stage++;
									}
								}
								if(gwall instanceof pWall) {
									balltouch.setFramePosition(0);
									balltouch.start();
								}
							}
						}
					}
					if(gball.y > 1000) {
						obj.remove(i);
						balls--;
						if(balls == 0) {
							background.stop();
							background.setFramePosition(0);
							fail.setFramePosition(0);
							fail.start();
							isOver = true;
							stage = 0;
							obj.clear();
						}
					}
				}
				if(!haveblock) {
					if(stage != 0) {
						balls = 0;
						obj.clear();
						obj.add(new pWall(200, 900, 100, 30, Color.blue));
						obj.add(new gWall(0,0,10,1000, Color.orange));
						obj.add(new gWall(575,0,10,1000, Color.orange));
						obj.add(new gWall(10,0,575,10,Color.orange));
						obj.add(new gBall(250, 850, 6, Color.red));
						balls++;
						stageClear.setFramePosition(0);
						stageClear.start();
					}
					blocks = 0;
					makeBlock();
				}
				
				Thread.sleep(16);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	void makeBlock() {
		int wblock = (stage*2) + 3;
		int hblock = (stage*2) + 3;
		int blocksH = 200 + (stage * 50);
		for(int i= 0; i < hblock; i++) {
			for(int j = 0; j <wblock; j++) {
				int y = (int) (Math.random() * 2);
				if(y == 0) {
					obj.add(new bWall((585 - 20)/wblock * j + 12, ((blocksH-10)/hblock) * i + 11,
							(585 -20)/wblock - 1, (blocksH-10)/hblock - 1, Color.gray));
				}
				else if(y == 1) {
					obj.add(new bWall((585 - 20)/wblock * j + 12, ((blocksH-10)/hblock) * i + 11,
							(585-20)/wblock - 1, (blocksH-10)/hblock - 1, Color.decode("#B5E61D")));
				}
				blocks++;
			}
		}
	}
	gBall addClone(gBall g) {
		gBall r= new gBall(g.x,g.y,g.r,g.c);
		r.vx = g.vx*1.3;
		if(r.vx >= 450) {
			r.vx -= 450;
		}
		if(r.vx < 0) {
			r.vx = 450 + r.vx;
		}
		r.vy = Math.sqrt(Math.pow(g.speed, 2) - Math.pow(r.vx, 2));
		r.pre_X = r.x;
		r.pre_Y = r.y;
		//System.out.println("clone info x:" + r.x + ", y :" + r.y + ", vx :"+ r.vx + ", vy:"+ r.vy);
		return r;
	}
}

class endingPanel extends JPanel implements Runnable{
	JLabel nameLabel= new JLabel("GAME OVER");
	JLabel space= new JLabel("-Press SpaceBar to Start-");
	JLabel bsco;
	JLabel sco;
	Thread t;
	int BestScore = 0;
	int score = 0;
	endingPanel(int b, int s){
		BestScore = b;
		score = s;
		bsco =  new JLabel("HIGH SCORE:" + BestScore);
		sco =  new JLabel("SCORE:" +score);
		setLayout(null);
		setBackground(Color.gray);
		t = new Thread(this);
		t.start();
		//제목 폰트 설정
		Font nfont = new Font("맑은 고딕", Font.BOLD, 50);
		nameLabel.setFont(nfont);
		nameLabel.setLocation(135, 300);
		nameLabel.setSize(425,50);
		nameLabel.setForeground(Color.CYAN);
		add(nameLabel);
		//스페이스바 폰트 설정
		Font sfont = new Font("맑은 고딕", Font.BOLD, 30);
		space.setFont(sfont);
		space.setLocation(110, 800);
		space.setSize(370,40);
		space.setForeground(Color.red);
		add(space);
		
		Font scfont = new Font("맑은 고딕", Font.BOLD, 30);
		sco.setFont(scfont);
		sco.setLocation(110, 500);
		sco.setSize(370,40);
		sco.setForeground(Color.red);
		add(sco);
		
		Font bscfont = new Font("맑은 고딕", Font.BOLD, 30);
		bsco.setFont(bscfont);
		bsco.setLocation(110, 600);
		bsco.setSize(370,40);
		bsco.setForeground(Color.red);
		add(bsco);
	}
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		//g.drawLine(getWidth()/2, 0, getWidth()/2, getHeight());
	}
	@Override
	public void run() {	
		while(this.isVisible()) {		//보일때만 반복
			try {
				Thread.sleep(100);
				if(space.isVisible()) space.setVisible(false);
				else space.setVisible(true);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

public class blockBreak extends JFrame implements Runnable{
	mainPanel mainp;
	titlePanel titlep;
	endingPanel endp;
	int bestScore = 0;
	int Score = 0;
	
	blockBreak(){
		Thread t = new Thread(this);
		setTitle("수철이의 블록깨기");
		setSize(600,1000);
		this.setLocation(660,30);
		this.setResizable(false);

		titlep = new titlePanel();
		add(titlep);
		
		setFocusable(true);
		requestFocus();
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_SPACE) {
					if(titlep.isVisible()) {
						titlep.setVisible(false);
						mainp = new mainPanel();
						if(!mainp.t.isAlive()) {
							mainp.t.start();		
						}
						if(!t.isAlive())
							t.start();
						add(mainp);
					}
					else if(endp.isVisible()){
						endp.setVisible(false);
						titlep = new titlePanel();
						add(titlep);
					}
				}
			}
		});

		setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	public static void main(String[] args) {
		new blockBreak();
	}
	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(mainp.isOver) {
				mainp.setVisible(false);
				Score = mainp.score;
				if(bestScore < Score)
					bestScore = Score;
				endp = new endingPanel(bestScore,Score);
				add(endp);
				if(hasFocus()) {
					requestFocus();
					if(hasFocus())
						mainp.isOver = false;
				}
			}
		}
	}
}
