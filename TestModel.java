package sparkTest;

import java.util.List;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.tree.model.RandomForestModel;
import org.apache.spark.mllib.util.MLUtils;
import scala.Tuple2;

public class TestModel {
	public static String test(JavaSparkContext jsc,String path,RandomForestModel model) {	//用于预测的函数
   	    
	    JavaRDD<LabeledPoint> data = MLUtils.loadLibSVMFile(jsc.sc(), path).toJavaRDD();	    //导入数据
	    JavaPairRDD<Double, Double> predictionAndLabel =
	  	      data.mapToPair(p -> new Tuple2<>(model.predict(p.features()), p.label()));
	    data.foreach(f->{System.out.println("features:"+f.features().toString()+" Labels:"+f.label());});
	    List<Tuple2<Double,Double>> arr=predictionAndLabel.collect();
	    int result=(new Double(arr.get(0)._1())).intValue();		
	    String result2=result+"";
	    return result2;					//返回预测结果
		
	}
	
}
