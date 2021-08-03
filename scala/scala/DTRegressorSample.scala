//in this lab we try to predict price per unit area with decision tree regressor
//the relevant features are "house age", "distance to mrt", and "number of convenience stores"
//decision tree regressor can handle features in different scales better

import org.apache.log4j._
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.regression.DecisionTreeRegressor
import org.apache.spark.sql._

object DTRegressorSample
{
  def main(args: Array[String]): Unit = {
    //set log level to only print error otherwise it will print too much information
    Logger.getLogger("org").setLevel(Level.ERROR)

    // use new sparksession interface in spark 2.0
    val spark = SparkSession.builder.appName("DTRegressor").master("local[*]").getOrCreate()

    import spark.implicits._

    //load data source into dataframe
    val realEstateDS = spark.read
      .option("sep",",")
      .option("header", "true")
      .option("inferSchema", "true")
      .csv("../Downloads/data_functional/data/realestate.csv")
    realEstateDS.show()

    //for spark machine learning you need label column where value you are trying to predict
    //and feature column
    //construct vectorassembler to produce feature column
    val assembler = new VectorAssembler()
      .setInputCols(Array("HouseAge", "DistanceToMRT", "NumberConvenienceStores"))
      .setOutputCol("features")
    //select both label and feature column
    val df = assembler.transform(realEstateDS)
      .select("PriceOfUnitArea", "features")

    df.show(10)
    //split our dataset into training set and test set
    val trainTest = df.randomSplit(Array(0.5, 0.5))
    val trainingDF = trainTest(0)
    val testDF = trainTest(1)

    //construct tree regressor
    //decisiontreeregressor can do without hyperparameters
    //setlabelcol specifies a label column (what you are predicting)
    val TreeRegressor = new DecisionTreeRegressor()
      .setFeaturesCol("features")
      .setLabelCol("PriceOfUnitArea")

    //build decision tree regression model using fit function
    val dtModel = TreeRegressor.fit(trainingDF)
    //generate our prediction using the testdata based on decisiontree regressor model
    val completePredictions = dtModel.transform(testDF).cache()
    completePredictions.show()
    //extract the prediction and correct labels
    val predictionAndLabel = completePredictions.select("prediction", "priceOfUnitArea").collect()

    //print out the predicted and actual values for each sample
    for(prediction <- predictionAndLabel) {
      println(prediction)
    }
    spark.stop()
    //in this lab we practice decisiontree regressor for spark prediction problem
  }
}
