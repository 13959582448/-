package sparkTest;

import java.awt.TextField;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.tree.model.RandomForestModel;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JFrame;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.swing.JButton;
import javax.swing.JTextField;
import sparkTest.PictureConvert;
import sparkTest.TestModel;

public class UI {
	static String PicPath="/home/hadoop-hch/1.jpg"; 
	static String DataPath="/home/hadoop-hch/1.txt";
	
	static int times=1;
	private JFrame frame;
	private JTextField textField;
	private JTextField textField_1;
	DrawMain dw = new DrawMain();
	public static String predict_number="";
	public static String predict_type="";
	
	SparkConf sparkConf = new SparkConf().setAppName("UI").setMaster("local[2]");
	JavaSparkContext jsc = new JavaSparkContext(sparkConf);
	RandomForestModel model_binary=RandomForestModel.load(jsc.sc(), "file:///home/hadoop-hch/Model/RF41");
	RandomForestModel model_hand=RandomForestModel.load(jsc.sc(), "file:///home/hadoop-hch/Model/RF0");
	RandomForestModel model_machine=RandomForestModel.load(jsc.sc(), "file:///home/hadoop-hch/Model/RF54");   //提前导入模型以提高减少后期预测的时间
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UI window = new UI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	public UI() {
		initialize();
	}

	private void initialize() {		//初始化设置
		frame = new JFrame();
		frame.setBounds(100, 100, 486, 618);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setTitle("数字识别");			//标题
		dw.setBounds(54, 10, 376,376);
		frame.getContentPane().add(dw);		//导入DrawMain的设置
		
		textField = new JTextField();
		textField.setBounds(93, 528, 66, 21);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		textField.setEditable(false);
		textField_1 = new JTextField();
		textField_1.setBounds(312, 528, 66, 21);
		frame.getContentPane().add(textField_1);
		textField_1.setColumns(10);
		textField_1.setEditable(false);
		JButton btnSave = new JButton("save");
		btnSave.setBounds(26, 411, 93, 23);
		frame.getContentPane().add(btnSave);		//画板、文本框、按钮的设置
		btnSave.addActionListener(new ActionListener() {		//保存画板的图像
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					savePic("/home/hadoop-hch/1.jpg");
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		JButton btnClear = new JButton("clear");		//清空功能的按钮
		btnClear.setBounds(184, 411, 93, 23);
		frame.getContentPane().add(btnClear);
		btnClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {		//清空功能的具体实现步骤
				// TODO Auto-generated method stub
				try {
					dw.clear();
					textField_1.setText("");
					textField.setText("");
				}
				catch(Exception f) {
					f.printStackTrace();
				}
			}
 
		});
		
		JButton btnUpload = new JButton("upload");		//上传功能
		btnUpload.setBounds(330, 411, 93, 23);
		frame.getContentPane().add(btnUpload);
		btnUpload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					dw.setImage(upload());				//将选择的图片展示在画板上
				}
				catch(Exception f) {
					
				}
				
			}
 
		});
		
		JButton btnConfig = new JButton("predict");			//预测功能
		btnConfig.setBounds(184, 476, 93, 23);
		frame.getContentPane().add(btnConfig);
		btnConfig.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					textField.setText("");
					textField_1.setText("");
					PictureConvert.resizeImage(PicPath, DataPath, 28, 28,20);
					predict_number=TestModel.test(jsc,"file://"+DataPath,model_binary);		//判断是机器字还是手写字
					if(predict_number.equals("0")) {					//手写字 调用第一个模型
						textField.setText("手");
						PictureConvert.resizeImage(PicPath, DataPath, 24, 24,20);		//24 24 代表图像保存的宽度和高度 20 代表二值化的阈值
						predict_number=TestModel.test(jsc,"file://"+DataPath,model_hand);
						textField_1.setText(predict_number);
					}
					else {												//机器字 调用另一个模型 此时不需要对图像进行处理 直接预测
						textField.setText("机器");
						predict_number=TestModel.test(jsc,"file://"+DataPath,model_machine);
						textField_1.setText(predict_number);
					}
					
				}
				catch(Exception f) {
					f.printStackTrace();
				}
			}
 
		});
		
		
		
	}
	public  String savePic(String path) throws IOException{			//保存图片的函数
		PicPath="/home/hadoop-hch/1.jpg";
		DataPath="/home/hadoop-hch/1.txt";
		Dimension imagesize = this.dw.getSize();
		BufferedImage image = new BufferedImage(imagesize.width,imagesize.height,BufferedImage.TYPE_INT_RGB);
		
		Graphics2D graphics = image.createGraphics();
        this.dw.paint(graphics);
        graphics.dispose();
        Image newImage = image.getScaledInstance(24, 24, Image.SCALE_SMOOTH);//
        BufferedImage myImage = new BufferedImage(24, 24,BufferedImage.TYPE_INT_RGB);//
        Graphics graphics1 = myImage.getGraphics();
        graphics1.drawImage(newImage, 0, 0, null);
        graphics1.dispose();			//对图片调整大小
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
		
		public String upload() {						//上传图片功能的具体实现步骤
		textField.setText("");
		textField_1.setText("");						
		JFileChooser chooser = new JFileChooser();		
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
	        "JPG & PNG Images", "jpg", "png");
	    File f =null;
	    chooser.setFileFilter(filter);			
	    int returnVal = chooser.showOpenDialog( new  TextField()); //选择图片
	    String flujin=null;
	    if(returnVal == JFileChooser.APPROVE_OPTION) { 
	      f= chooser.getSelectedFile(); 
	      
	    flujin =f.getParent()+"/"+f.getName();		//获得路径
	    }
	    PicPath=flujin;
		DataPath="/home/hadoop-hch/1.txt";
	    return  flujin;									//返回路径
	}
	
}