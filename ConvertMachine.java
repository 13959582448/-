package sparkTest;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ConvertMachine {			//单个图片的转换方法
	
	static int hand=1;			//判断手写还是机器
	static String Label="0";	//生成LabelPoint格式的标签
	static int ts=0;  //设置分类的阈值
	static int move=0;//用于判断数字6 7 9的位置是否需要调整
	
	public static void resizeImage_machine(String srcPath, String desPath,  
	        int width, int height,int ts) throws IOException {  
	        File srcFile = new File(srcPath);  
	        Image srcImg = ImageIO.read(srcFile);  
	        BufferedImage buffImg = null;  
	        buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	        buffImg.getGraphics().drawImage(  
	                srcImg.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0,  
	                0, null);  
	        //
	        int[] rgb = new int[3];	
	        int[] list=new int[28*28];	//用于存放灰度值
	        for (int i = (28-height)/2; i < 28-(28-height)/2; i++) {		 //以下两行的代码是用于对画板的图像进行处理 在四周增加2个像素宽度的黑底 可以大幅度提高预测的准确率
	            for (int j = (28-width)/2; j < 28 -(28-width)/2; j++) {		//
	                int pixel = buffImg.getRGB(j-(28-width)/2, i-(28-height)/2);
	                rgb[0] = (pixel >> 16)&0xff;
	                rgb[1] = (pixel >> 8)&0xff;
	                rgb[2] = (pixel & 0xff);
	                int grey=(int)(rgb[0]*0.3+rgb[1]*0.59+rgb[2]*0.11);
	                if(hand==0) {			//如果不是手写的图片则转换灰度值，手写图为黑底白字，其余的图为白底黑字
	                	grey=255-grey;			//
	                }
	                																											
	                System.out.print(grey+" ");
	                if(grey<ts)grey=0;
	                else grey=1;
	                list[i*28+j] = grey;        
	            }  
	            System.out.println();
	        }
	        System.out.println("\n\n");
	        
	        File file=new File(desPath);
	        String str="0";
	        for(int i=0;i<list.length-1;i++) {//将转换完的数据写入文件
	        	if(list[i]!=0) {
	        		str=str+" "+(i+1)+":"+list[i];
	        	}
	        }	
	        if(list[list.length-1]==0) {
	        	str=str+" 784:0";
	        }
	        else
	        	str=str+" 784:"+list[list.length-1];
	        
	        int w=1;
	        for(int i:list) {//输出转换完成的图像方便检查
	        	System.out.print(i+" ");
	        	if(w%28==0)
	        		System.out.println();
	        	w++;
	        }	
	        System.out.println();

	        FileWriter fw=new FileWriter(file);
	        fw.write(str);
	        fw.close();
		}
	
	public static String train(String srcPath, String label,  
	        int width, int height,int ts) throws IOException {  	//训练集和测试集的转换
	        File srcFile = new File(srcPath);  
	        Image srcImg = ImageIO.read(srcFile);  
	        BufferedImage buffImg = null;  
	        buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	        buffImg.getGraphics().drawImage(  
	                srcImg.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0,  
	                0, null);  
	        int[] rgb = new int[3];
	        int[] list_o=new int[width*height];
	        for (int i = 0; i < height; i++) {
	            for (int j = 0; j < width; j++) {
	                int pixel = buffImg.getRGB(j, i);//获得像素值

	                rgb[0] = (pixel >> 16)&0xff;
	                rgb[1] = (pixel >> 8)&0xff;
	                rgb[2] = (pixel & 0xff);	                
	                int grey=(int)(rgb[0]*0.3+rgb[1]*0.59+rgb[2]*0.11);
	                if(hand==0)grey=255-grey;																											/////////////////grey=255-grey
	                if(grey<ts)grey=0;
	                else grey=1;
	                list_o[i*width+j] = grey;
	            }  	            
	        }
	        int[] list=new int[width*height];
	        for(int i=0;i<28;i++) {
        		for(int j=0;j<28;j++) {
        			list[i*28+j]=0;
        		}
        	}
	        // 6 7 9

	        if(move==1) {		//move用于判断是否移动训练集中数字6 7 9的位置，使其居中
		        if(label.equals(6+"")) {		//标签为6，则向下移动两行
		        	for(int i=2;i<26;i++) {
		        		int i2=i-2;
		        		for(int j=0;j<28;j++) {
		        			list[i*28+j]=list_o[i2*28+j];
		        		}
		        	}
		        }
		        
		        else if(label.equals(7+"")) {		//标签为7，则向上移动3行
		        	for(int i=3;i<25;i++) {
		        		int i2=i+3;
		        		for(int j=0;j<28;j++) {
		        			list[i*28+j]=list_o[i2*28+j];
		        		}
		        	}
		        }
		        
		        else if(label.equals(9+"")) {		//标签为9，则上移动2行
		        	for(int i=2;i<26;i++) {
		        		int i2=i+2;
		        		for(int j=0;j<28;j++) {
		        			list[i*28+j]=list_o[i2*28+j];
		        		}
		        	}
		        }	
		        else {							
		        	for(int i=0;i<28;i++) {
		        		for(int j=0;j<28;j++) {
		        			list[i*28+j]=list_o[i*28+j];
		        		}
		        	}
				}
	        }
	        
	        else {					//move==0则不移动
	        	for(int i=0;i<28;i++) {
	        		for(int j=0;j<28;j++) {
	        			list[i*28+j]=list_o[i*28+j];
	        		}
	        	}
	        }
			
			
	        
	        //6 7 9
	        String str=label;
	        for(int i=0;i<list.length-1;i++) {
	        	if(list[i]!=0) {
	        		str=str+" "+(i+1)+":"+list[i];
	        	}
	        }
	        if(list[list.length-1]==0) {		//为解决数组越界的问题，手动为最后一位加上灰度值
	        	str=str+" 784:0";
	        }
	        else
	        	str=str+" 784:1";
	        str=str+"\n";
	        
	        for(int i=0;i<28;i++)
	        {
	        	for(int j=0;j<28;j++)
	        	{
	        		System.out.print(list[i*28+j]+" ");
	        	}
	        	System.out.println();
	        }
	        
	        return str;
		}

	
	public static void convertAll(int ts) throws Exception{		//用于转换所有数据
		File file=new File("/home/hadoop-hch/Downloads/test");
		File[] list=file.listFiles();

		File f2=new File("/home/hadoop-hch/Downloads/test/.txt");
		FileWriter fw=new FileWriter(f2);
		int i=1;
		for(File f:list) {
			int length=f.getAbsolutePath().split("/").length;
			String label=f.getAbsolutePath().split("/")[length-1].substring(0, 1);
			label=Label;
			
			String str=train(f.getAbsolutePath(), label, 28, 28,ts);
			System.out.println();
			fw.write(str);
			i++;
		}
		fw.close();
	}
	

	public static void main(String []args) {
		String type="machine";		//用于指定转换的数据集是机器字数据集还是手写的数据集
		
		if(type.equals("hand")) {
			hand=1;
			ts=130;
			Label="0";
			move=1;
		}
		if(type.equals("machine")) {
			hand=0;
			ts=20;
			Label="1";
			move=0;
		}
		try {
			convertAll(ts);																
		}
		catch(Exception e) {
			e.printStackTrace();
		}

	}
}
