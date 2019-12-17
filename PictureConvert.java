package sparkTest;
import java.awt.Image;
import java.awt.List;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import sparkTest.TestModel;
import spire.optional.intervalGeometricPartialOrder;				//以下代码与ConvertMachine的代码类似可以跳过

public class PictureConvert {
	public static void resizeImage(String srcPath, String desPath,  
        int width, int height,int ts) throws IOException {  
        File srcFile = new File(srcPath);  
        Image srcImg = ImageIO.read(srcFile);  
        BufferedImage buffImg = null;  
        buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        buffImg.getGraphics().drawImage(  
                srcImg.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0,  
                0, null);  
        //ImageIO.write(buffImg, "PNG", new File(desPath));  
        int[] rgb = new int[3];
        int[] list=new int[28*28];
        for (int i = (28-height)/2; i < 28-(28-height)/2; i++) {
            for (int j = (28-width)/2; j < 28 -(28-width)/2; j++) {
                int pixel = buffImg.getRGB(j-(28-width)/2, i-(28-height)/2);//获得像素值
                rgb[0] = (pixel >> 16)&0xff;
                rgb[1] = (pixel >> 8)&0xff;
                rgb[2] = (pixel & 0xff);
                int grey=(int)(rgb[0]*0.3+rgb[1]*0.59+rgb[2]*0.11);
                grey=255-grey;
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
        for(int i=0;i<list.length-1;i++) {
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
        for(int i:list) {
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
	
	public static void resizeImage_machine(String srcPath, String desPath,  
	        int width, int height,int ts) throws IOException {  
	        File srcFile = new File(srcPath);  
	        Image srcImg = ImageIO.read(srcFile);  
	        BufferedImage buffImg = null;  
	        buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	        buffImg.getGraphics().drawImage(  
	                srcImg.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0,  
	                0, null);  
	        //ImageIO.write(buffImg, "PNG", new File(desPath));  
	        int[] rgb = new int[3];
	        int[] list=new int[28*28];
	        for (int i = (28-height)/2; i < 28-(28-height)/2; i++) {
	            for (int j = (28-width)/2; j < 28 -(28-width)/2; j++) {
	                int pixel = buffImg.getRGB(j-(28-width)/2, i-(28-height)/2);//获得像素值
	                rgb[0] = (pixel >> 16)&0xff;
	                rgb[1] = (pixel >> 8)&0xff;
	                rgb[2] = (pixel & 0xff);
	                int grey=(int)(rgb[0]*0.3+rgb[1]*0.59+rgb[2]*0.11);
	                grey=255-grey;
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
	        for(int i=0;i<list.length-1;i++) {
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
	        for(int i:list) {
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
	        int width, int height,int ts) throws IOException {  
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
	                if(grey<ts)grey=0;
	                else grey=1;
	                list_o[i*width+j] = grey;
	            }  	            
	        }
	        int[] list=new int[width*height];
	        // 6 7 9
	        
	        
	        if(label.equals(6+"")) {
	        	for(int i=2;i<26;i++) {
	        		int i2=i-2;
	        		for(int j=0;j<28;j++) {
	        			list[i*28+j]=list_o[i2*28+j];
	        		}
	        	}
	        }
	        
	        else if(label.equals(7+"")) {
	        	for(int i=3;i<25;i++) {
	        		int i2=i+3;
	        		for(int j=0;j<28;j++) {
	        			list[i*28+j]=list_o[i2*28+j];
	        		}
	        	}
	        }
	        
	        else if(label.equals(9+"")) {
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
	        
	        //6 7 9
	        String str=label;
	        for(int i=0;i<list.length-1;i++) {
	        	if(list[i]!=0) {
	        		str=str+" "+(i+1)+":"+list[i];
	        	}
	        }
	        if(list[list.length-1]==0) {
	        	str=str+" 784:0";
	        }
	        else
	        	str=str+" 784:"+list[list.length-1];
	        str=str+"\n";
	        return str;
		}

	
	public static void convertAll(int ts) throws Exception{
		File file=new File("/home/hadoop-hch/test_images");
		File[] list=file.listFiles();
		File f2=new File("/home/hadoop-hch/Downloads/test_data2.txt");
		FileWriter fw=new FileWriter(f2);
		int i=1;
		for(File f:list) {
			String label=f.getAbsolutePath().split("_")[3].substring(0, 1);
			System.out.println(i+"   "+label);
			String str=train(f.getAbsolutePath(), label, 28, 28,ts);
			fw.write(str);
			i++;
		}
		fw.close();

		File file2=new File("/home/hadoop-hch/train_images");
		File[] list2=file2.listFiles();
		File f3=new File("/home/hadoop-hch/Downloads/train_data2.txt");
		FileWriter fw2=new FileWriter(f3);
		int i2=1;
		for(File f:list2) {
			String label=f.getAbsolutePath().split("_")[3].substring(0, 1);
			System.out.println(i2+"   "+label);
			String str=train(f.getAbsolutePath(), label, 28, 28,ts);
			fw2.write(str);
			i2++;
		}
		fw2.close();	
	}
	
	
	
	//main
	public static void main(String []args) {
		try {
			convertAll(130);
		}
		catch(Exception e) {
			e.printStackTrace();
		}

	}
}