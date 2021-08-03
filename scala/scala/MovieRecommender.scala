//today we will discuss the recommendation system
//recommendation system is very important for any e-commerce website
//in this lab we will run als (alternative least square) recommendations with spark machine learning library
//we will use movielens dataset to perform movie recommendation
import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.recommendation.ALS
import org.apache.spark.sql.SparkSession
import Utilities.setupLogging

object MovieRecommender
{
  def main(args: Array[String]): Unit = {
    //build spark session
    val spark = SparkSession.builder.master("local[*]").getOrCreate()

    setupLogging()

    //load dataframe from movie_ratings.csv
    val ratings = spark.read.option("header", "true")
      .option("inferSchema", "true")
      .csv("../Downloads/data_functional/data/movie_ratings.csv")
    //show the first several ratings objects
    ratings.head()
    ratings.printSchema()

    //create training and testing datasets
    val Array(training, test) =ratings.randomSplit(Array(0.8, 0.2))
    //build the recommendation model using ALS on the training data
    val als = new ALS()
      .setMaxIter(5)
      .setRegParam(0.01)
      .setUserCol("userid")
      .setItemCol("movieid")
      .setRatingCol("rating")
    //fit out model on the training set
    val model = als.fit(training)
    //produce our prediction of user rating using testset
    val predictions = model.transform(test)
    //display our predicted rating
    predictions.show()

    import spark.implicits._
    //import to use abs()
    import org.apache.spark.sql.functions._

    //to calculate difference between predicted value and rent rating
    val error = predictions.select(abs($"rating" - $"prediction"))
    //drop null value and show statistics of our error data
    error.na.drop().describe().show()

    spark.stop()
  }
}
