/* In this homework we will be working with an advertising dataset
 * indicating whether particular internet user will click on an
 * advertisement based on features of that user
 *
 * this dataset contains the following features:
 * "daily time spent on site" consumer time on site in minutes
 * "age" customer age in years
 * "area income" avg income of geographical area of consumer
 * "daily internet usage" avg minutes a dat consumer is on the internet
 * "ad topic line" headline of the advertisement
 * "city" city of consumer
 * "male" whether or not the consumer was male
 * "country" country of consumer
 * "timestamp" time at which consumer clicked on ad or closed window
 * "clicked on ad" 0 or 1 indicated clicking on ad */
import Utilities.setupLogging
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.sql.SparkSession
//for date time functions
import org.apache.spark.sql.functions._
//import vectorassembler and vectors
import org.apache.spark.ml.feature.{VectorAssembler,StringIndexer,VectorIndexer,OneHotEncoder}
import org.apache.spark.ml.linalg.Vectors
//import pipeline
import org.apache.spark.ml.Pipeline

object LogRegExercise
{
  def main(args: Array[String]): Unit = {
    //build spark session
    val spark = SparkSession.builder.master("local[*]").getOrCreate()

    setupLogging()
    //use spark to read in the advertising csv file
    //everything builds up each other
    //load data source into dataframe
    val data = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .format("csv")
      .load("../Downloads/data_functional/data/realestate.csv")
    //print schema of dataframe
    data.printSchema()

    /* display data */
    val colnames = data.columns
    val firstrow = data.head(1)(0)
    println("\nExample data row")
    for (ind <- Range(0, colnames.length)) {
      println(s"${colnames(ind)}: ${firstrow(ind)}")
    }

    /* Setting up dataframe for machine learning */
    // do the following
    // rename the clicked on ad column to label
    // grab the following columns: daily time spent on site, age, area income, daily internet usage,
    //timestamp(as hour), male
    // create a new column called hour from the timestamp containing the hour of the click
    //do not include timestamp info which is not compatible to logistic regression
    //hour only has digit from 0 - 24

    //create hour column
    val timedata = data.withColumn("Hour", hour(data("Timestamp")))

    //for scala/spark $ syntax
    //this import is needed to use the $ notation
    //abstract class sqlimplicits
    import spark.implicits._

    //select related column for dataframe
    val logregdata = timedata.select(data("Clicked on Ad").as("label"),
    $"Daily Time Spent on Site",
    $"Age",$"Area Income",
    $"Daily Internet Usage", $"Hour", $"Male")

    //create a new vectorassembler object called assembler
    //for the feature
    //columns as the input set the output column to be called features
    val assembler = new VectorAssembler()
      .setInputCols(Array("Daily Time Spent on Site", "Age",
      "Area Income", "Daily Internet Usage", "Hour"))
      .setOutputCol("features")
    //use random split to create a train test split of 70/30
    //70% of data is used for training 30% of data is used for testing
    val Array(training, test) =
    logregdata.randomSplit(Array(0.7, 0.3), seed = 12345)

    /* setup pipeline */

    //create new logisticregression object called lr
    val lr = new LogisticRegression()

    //create new pipeline with the stages assembler, lr
    val pipeline = new Pipeline().setStages(Array(assembler, lr))
    //fit the pipeline to training set
    val model = pipeline.fit(training)
    //get results on test set with transform method
    val results = model.transform(test)

    //display prediction results
    println("Prediction Result")
    results.printSchema()
    results.show()
    println()

    //for metrics and evaluation import multiclassmetrics
    import org.apache.spark.mllib.evaluation.MulticlassMetrics
    import spark.implicits._
    //convert the test results to an RDD using .as and .rdd
    //convert dataframe to rdd
    val predictionAndLabels =
      results.select($"prediction", $"label").as[(Double, Double)].rdd

    //build a new multiclassmetrics object
    val metrics = new MulticlassMetrics(predictionAndLabels)
    //print out confusion matrix
    println()
    //this data only mislabel 8 points since this data is well separated
    println("Confusion matrix:")
    println(metrics.confusionMatrix)

    //print out the prediction and original label
    val predictionResultsArray = predictionAndLabels.collect()
    for ((pred, label) <- predictionResultsArray) {
      if(pred != label)
        println("label: " + label + " prediction: " + pred)
    }
    //data is well separated
  } //end of main
  //in this homework we discuss how to predict whether the user will click on the ads
}
