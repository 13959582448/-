package sparkTest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.io.OutputStreamWriter;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.evaluation.MulticlassMetrics;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.tree.RandomForest;
import org.apache.spark.mllib.tree.model.RandomForestModel;
import org.apache.spark.mllib.util.MLUtils;

import scala.Tuple2;

public class RF {		//用于生成判断手写字的随机森林
	public static void main(String[] args) {
	    
	    String path="/home/hadoop-hch/Model/RF";
	    String file="/home/hadoop-hch/Model/1.txt";
	    try {
	    	
	    	String str="";
		    int i=0;
		    for(int numtrees=5;numtrees<=13;numtrees++)		//确定参数范围来进行建模
		    {
		    	for(int maxDepth=5;maxDepth<=16;maxDepth++)
		    		for(int maxBins=50;maxBins<=200;maxBins+=100) {
		    		    
		    		    	SparkConf sparkConf = new SparkConf().setAppName("RF").setMaster("local[2]");
			    		    JavaSparkContext jsc = new JavaSparkContext(sparkConf);
			    			str=function(jsc, path+i, numtrees, maxDepth, maxBins);		//将模型的具体信息赋给一个字符串
			    			i++;

			    			method2(file, str);		    		    //将模型的信息存储在固定的文件中 方便之后的筛选工作
		    		}
		    }
	    }catch(Exception e) {
	    	e.printStackTrace();
	    }
	    
	  }
	
	public static void method2(String file, String conent) {	//生成模型的具体信息的存储方法
		BufferedWriter out = null;
		try {
		out = new BufferedWriter(new OutputStreamWriter(
		new FileOutputStream(file, true)));
		out.write(conent);
		} catch (Exception e) {
		e.printStackTrace();
		} finally {
		try {
		out.close();
		} catch (Exception e) {
		e.printStackTrace();
		}
		}
	}
	
	public static String function(JavaSparkContext jsc,String savePath,int numtrees,int maxDepth,int maxBins) {		//建模的具体方法


		DecimalFormat df = new DecimalFormat("#0.000000");


	    String datapath = "file:///home/hadoop-hch/Downloads/train_data2.txt";
	    String datapath2="file:///home/hadoop-hch/Downloads/test_data2.txt";
	    //String datapath2="file:///home/hadoop-hch/Downloads/pic.txt";
	    JavaRDD<LabeledPoint> train_data = MLUtils.loadLibSVMFile(jsc.sc(), datapath).toJavaRDD();
	    JavaRDD<LabeledPoint> test_data = MLUtils.loadLibSVMFile(jsc.sc(), datapath2).toJavaRDD();		//导入训练数据和测试数据
	    
	    // Split the data into training and test sets (30% held out for testing)
	    //JavaRDD<LabeledPoint>[] splits = data.randomSplit(new double[]{0.7, 0.3});
	    //JavaRDD<LabeledPoint> trainingData = splits[0];
	    //JavaRDD<LabeledPoint> testData = splits[1];


	    int numClasses = 10;
	    Map<Integer, Integer> categoricalFeaturesInfo = new HashMap<>();			//确定参数
        String featureSubsetStrategy = "auto";
	    String impurity = "gini";
	    RandomForestModel model = RandomForest.trainClassifier(train_data,
	            numClasses,
	            categoricalFeaturesInfo,
	            numtrees,
	            featureSubsetStrategy,
	            impurity,
	            maxDepth,
	            maxBins,
	            1050);		
		/*
		 * JavaPairRDD<Double, Double> predictionAndLabel = test_data.mapToPair(p -> new
		 * Tuple2<>(model.predict(p.features()), p.label()));
		 */
	    JavaRDD<Tuple2<Object, Object>> predictionAndLabels = test_data.map(
	    	      new Function<LabeledPoint, Tuple2<Object, Object>>() {
	    	        public Tuple2<Object, Object> call(LabeledPoint p) {
	    	          Double prediction = model.predict(p.features());
	    	          return new Tuple2<Object, Object>(prediction, p.label());
	    	        }
	    	      }
	    	    );
	    
	    MulticlassMetrics metric = new MulticlassMetrics(predictionAndLabels.rdd());
	    double F1=metric.fMeasure();					//调用自带的方法算出Fmeasure的值
	    double accuracy=metric.accuracy();				//调用自带的方法算出准确率的值

	    model.save(jsc.sc(), "file://"+savePath);		//模型的存储地址
	    System.out.println("created successfully!");

	    return "savePath:"+savePath+"\n"+"F1:"+df.format(F1)+"\naccuracy:" + df.format(accuracy)+"\n\nnumtrees:"+numtrees+"\nmaxDepth:"+maxDepth+"\nmaxBins:"+maxBins+"\n\n\n\n";
		//返回该模型的具体参数、存储路径、Fmeasure、准确率。
	}
}
