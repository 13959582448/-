package sparkTest;

import java.awt.BasicStroke;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import sparkTest.PictureConvert;
import sparkTest.TestModel;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
public class SaveModel extends JFrame {				//旧UI，可以忽略
	private int width = 400;
	private int height = 680;
	private DrawMain DM = null;
	JButton save = null;
	JButton Config = null;
	JButton clear = null;
	JTextField TextResult = null;
	String s = "";
	public static void main(String args[]) {
		String PicPath="/home/hadoop-hch/1.jpg"; 
		String DataPath="/home/hadoop-hch/1.txt";
		new SaveModel(PicPath,DataPath);
	}
	public SaveModel(String PicPath,String DataPath) {
		super();
		this.setTitle("Num config");
		this.setSize(width, height);
		this.setResizable(false);
		this.setLocation(400, 180);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new GridLayout(0,1));
		
		JPanel jp1=new JPanel(new GridLayout(2,3,5,5));
		jp1.setPreferredSize(new Dimension(200, 100));
		this.DM = new DrawMain();//create Panel
		this.add(DM);
		
		
		this.Config = new JButton();//create config button
		this.Config.setPreferredSize(new Dimension(15, 15));
		this.Config.setText("Config");
		this.Config.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//try {
					//PictureDeal  pd = new PictureDeal();
					//s = pd.getString();
					TextResult.setText(s);
				//} catch (IOException e1) {
					//e1.printStackTrace();
				//}
			}
 
		});
		this.clear = new JButton();
		this.clear.setText("clear");
		this.clear.setPreferredSize(new Dimension(15, 15));
		this.clear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//try {
					//clear();
				//} catch (IOException e1) {
				//	e1.printStackTrace();
				//}
			}
 
		});
		
		this.save = new JButton();//create save button
		this.save.setText("save");
		this.save.setPreferredSize(new Dimension(15, 15));
		this.save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					savePic(PicPath);
					PictureConvert.resizeImage(PicPath, DataPath, 28, 28,20);
				    //TestModel.test("file://"+DataPath);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
 
		});
		
		this.TextResult = new JTextField();
		this.TextResult.setPreferredSize(new Dimension(15, 15));
		this.TextResult.setEditable(false);
		
		
		
		
		jp1.add(save);
		jp1.add(Config);
		jp1.add(clear);
		jp1.add(TextResult);
		this.add(jp1);
		this.setVisible(true);

	}
	
	public String savePic(String path) throws IOException{
		Dimension imagesize = this.DM.getSize();
		BufferedImage image = new BufferedImage(imagesize.width,imagesize.height,BufferedImage.TYPE_INT_RGB);
		
		Graphics2D graphics = image.createGraphics();
        this.DM.paint(graphics);
        graphics.dispose();
        Image newImage = image.getScaledInstance(28, 28, Image.SCALE_SMOOTH);
        BufferedImage myImage = new BufferedImage(28,28,BufferedImage.TYPE_INT_RGB);
        Graphics graphics1 = myImage.getGraphics();
        graphics1.drawImage(newImage, 0, 0, null);
        graphics1.dispose();//对图片调整大小
        File f=new File(path);
		if( !f.exists() )
		{
			f.createNewFile();
		}	
		FileImageOutputStream fos = new FileImageOutputStream(f);
		ImageIO.write(myImage, "JPG",fos);
		fos.flush();
		fos.close();
		return null;
	}	
}