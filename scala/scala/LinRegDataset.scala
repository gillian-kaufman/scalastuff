//in this lab we will introduce how to use linear regression in sparks machine learning library
//linear regression will predict real valued variables given several input variables
//input variables are called independent variables and predicted variable is called dependable variable
//basically we will build the model to understand the relationship
//between independent and dependent variables
//for linear regression all features need to be normalized before training
//in regression.txt the first column is how much the customer spent
//the second column is page speed
//one simple example is given page speed we want to predict how much the customer will
//spend in the online store

import org.apache.log4j._
import org.apache.spark.sql._
import org.apache.spark.sql.types._
import org.apache.spark.ml.regression.LinearRegression
import org.apache.spark.ml.feature.VectorAssembler

object LinRegDataset
{
  //case class to define schema of our dataset
  //label is how much the customer spent
  //feature_raw is page speed
  case class RegressionSchema(label: Double, features_raw: Double)

  def main(args: Array[String]): Unit = {
    //set log level to only print error otherwise it will print too much information
    Logger.getLogger("org").setLevel(Level.ERROR)

    // use new sparksession interface in spark 2.0
    val spark = SparkSession.builder.appName("LinearRegressionDF").master("local[*]").getOrCreate()

    //in machine learning world "label" is just the value youre trying to predict
    //and "feature" is the data youre given to make a prediction with
    //so in this example the labels are the first column of our data and
    //features are the second column.
    //you can have more than one feature which is why a vector is required

    //create schema so that we can convert dataframe to dataset since
    //we do not have header row in our text file
    val regressionSchema = new StructType().add("label", DoubleType, nullable = true)
      .add("features_raw", DoubleType, nullable = true)

    //read regression.txt and turn it into dataset
    import spark.implicits._
    val dsRaw = spark.read.option("sep",",")
      .schema(regressionSchema)
      .csv("../Downloads/data_functional/data/regression.txt")
      .as[RegressionSchema]
    //print dsRaw schema
    dsRaw.printSchema()

    //spark machine learning library expects label column and features column
    //we will transform our data into that format
    //construct VectorAssembler to produce features column
    val assembler = new VectorAssembler()
      .setInputCols(Array("features_raw"))
      .setOutputCol("features")

    //produce dataset with label column and features column
    val df = assembler.transform(dsRaw).select("label", "features")
    //print df schema
    df.printSchema()

    //lets split our data into training dataset and testing dataset
    //training set is used to produce the model
    //testing set is used to test the performance of our model
    val trainTest = df.randomSplit(Array(0.5, 0.5))
    val trainingDF = trainTest(0)
    val testDF = trainTest(1)

    //create new linearregression object with several parameters
    //in this course we will not get into the details of these parameters
    val lir = new LinearRegression()
      .setRegParam(0.3)   //regularization
      .setElasticNetParam(0.8)    //elastic net mixing
      .setMaxIter(100)    //max iterations
      .setTol(1E-6)   //convergence tolerance

    //train the model using our training data
    val model = lir.fit(trainingDF)

    //now see if we can predict values in our test data
    //generate predictions using our linear regression model for all features in our
    //test dataframe
    //this basically adds a "prediction" column to our testDF dataframe
    val fullPredictions = model.transform(testDF).cache()
    fullPredictions.show()
    //extract the predictions and the "known" correct labels
    val predictionAndLabel = fullPredictions.select("prediction", "label").collect()
    //print out the predicted and actual values for each sample
    for (prediction <- predictionAndLabel) {
      println(prediction)
    }
    //stop the session
    spark.stop()

    //in this lab we learn how to apply dataset and dataframe to build linear regression model
  }
}
