package sparkTest;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.calcite.util.Static;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.classification.SVMModel;
import org.apache.spark.mllib.classification.SVMWithSGD;
import org.apache.spark.mllib.evaluation.MulticlassMetrics;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.tree.RandomForest;
import org.apache.spark.mllib.tree.model.RandomForestModel;
import org.apache.spark.mllib.util.MLUtils;

import scala.Tuple2;

public class RF2 {		//以下代码与RF类似，用于建立剩余的两个模型，不再进行注释
	public static void main(String[] args) {
    	SparkConf sparkConf = new SparkConf().setAppName("RF").setMaster("local[2]");
	    JavaSparkContext jsc = new JavaSparkContext(sparkConf);
	    String path="/home/hadoop-hch/Modeltmp/RF";
	    String file="/home/hadoop-hch/Modeltmp/1.txt";
	    try {
	    	
	    	String str="";
		    int i=0;
		    for(int numtrees=3;numtrees<=10;numtrees++)
		    {
		    	for(int maxDepth=4;maxDepth<=13;maxDepth++)
		    		for(int maxBins=100;maxBins<=100;maxBins+=10) {
		    		    
		    			if((maxDepth>=(numtrees-2))) {

			    			str=function(jsc, path+i, numtrees, maxDepth, maxBins);
			    			i++;
			    			//System.out.println(str);
			    			method2(file, str);		  
		    			}
		    		}
		    }
	    }catch(Exception e) {
	    	e.printStackTrace();
	    }
	    
	  }
	
	public static void method2(String file, String conent) {
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
	
	public static String function(JavaSparkContext jsc,String savePath,int numtrees,int maxDepth,int maxBins) {


		DecimalFormat df = new DecimalFormat("#0.000000");


	    String datapath = "file:///home/hadoop-hch/Downloads/test/train.txt";
	    //String datapath2="file:///home/hadoop-hch/Downloads/binary/test.txt";
	    JavaRDD<LabeledPoint> data = MLUtils.loadLibSVMFile(jsc.sc(), datapath).toJavaRDD();
	    //JavaRDD<LabeledPoint> test_data = MLUtils.loadLibSVMFile(jsc.sc(), datapath2).toJavaRDD();
	    //JavaRDD<LabeledPoint> test_data = MLUtils.loadLibSVMFile(jsc.sc(), datapath2).toJavaRDD();
	    JavaRDD<LabeledPoint>[] splits=data.randomSplit(new double[] {0.7,0.3});    
	    JavaRDD<LabeledPoint> train_data = splits[0];
	    JavaRDD<LabeledPoint> test_data = splits[1];



	    int numClasses = 10;
	    Map<Integer, Integer> categoricalFeaturesInfo = new HashMap<>();
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
	            1);
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
	    predictionAndLabels.foreach(f->{System.out.println("label:"+f._2()+" predict:"+f._1());});
	    
	    MulticlassMetrics metric = new MulticlassMetrics(predictionAndLabels.rdd());
	    double F1=metric.fMeasure();
	    double accuracy=metric.accuracy();

	    model.save(jsc.sc(), "file://"+savePath);
	    System.out.println("created successfully!");

	    return "savePath:"+savePath+"\n"+"F1:"+df.format(F1)+"\naccuracy:" + df.format(accuracy)+"\n\nnumtrees:"+numtrees+"\nmaxDepth:"+maxDepth+"\nmaxBins:"+maxBins+"\n\n\n\n";
	}
}
