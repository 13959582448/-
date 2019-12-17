package sparkTest;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class DrawMain extends JPanel implements MouseListener,MouseMotionListener {
	private int width = 300;
	private int height = 300;
	private int x = 0;
	private int y = 0;
	public ArrayList<Integer[]> myList = new ArrayList<Integer[]>();
    public DrawMain() {					//初始化设置界面的各项属性
		// TODO Auto-generated constructor stub
    	super();
		this.setSize(width, height);
		this.setBounds(new Rectangle(0,0,450,300));
		this.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		this.setBackground(Color.WHITE);
		this.setVisible(true);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);		
	}
    public void paint(Graphics graphics){		//画板的设置
        super.paint(graphics);
        Graphics2D g2d = (Graphics2D)graphics;
        g2d.setBackground(Color.WHITE);			//背景颜色
 
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(30));			//画笔的粗细
        for(int i=0;i<myList.size();i++){
            g2d.drawLine(myList.get(i)[0],myList.get(i)[1],
                    myList.get(i)[2],myList.get(i)[3]);		//存放画笔的轨迹
        }
        g2d.dispose();
    }
    public void clear() {		//清空画板
    	this.myList.clear();
    	this.repaint();
    	
    }
    
    public void setImage(String path) throws Exception {		
    	File file = new File(path);
    	Image im = ImageIO.read(file);
    	Image newImage = im.getScaledInstance(300,300, Image.SCALE_SMOOTH);
    	Graphics g = this.getGraphics();
    	
    	g.drawImage(newImage, 50, 30, null);  	
    }
	@Override
	public void mouseDragged(MouseEvent e) {		//拖动生成图像
		// TODO Auto-generated method stub
		myList.add(new Integer[]{this.x,this.y,e.getX(),e.getY()});
	    this.x = e.getX();
            this.y = e.getY();
            this.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {			//根据鼠标的点击来确定点的坐标
		// TODO Auto-generated method stub
		this.x = e.getX();
        this.y = e.getY(); 
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

		 //首次的点击采集点

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
