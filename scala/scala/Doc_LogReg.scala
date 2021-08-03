//in this lab we will discuss classification
//classification is the process of putting something into a category
//classification has big impacts on our economy
//there are many classification examples in the real world
//given a picture you want to know whether the picture contains cat or not
//given customer profiles you want to know whether the customer will buy the product or not
//given patients test results you want to know whether the patient has cancer
//logistic regression is one type of classification algorithm
//before we use logistic regression algorithm we need to build or train logistic regression model
//using the training set
//the process of building logistic regression is very similar to how we build linear
//regression in the previous lecture
//official documentation for logistic regression

import org.apache.spark.sql.SparkSession
import Utilities._
import org.apache.spark.ml.classification.LogisticRegression

object Doc_LogReg
{
  def main(args: Array[String]): Unit = {
    //build spark session
    val spark = SparkSession.builder.master("local[*]").getOrCreate()

    setupLogging()
    //load training data
    val training = spark.read.format("libsvm")
      .load("../Downloads/data_functional/data/sample_libsvm_classification.txt")
    //define logistic regression model with a few parameters
    //in this lab this parameter is hardcoded
    //in the future we will show how to find the optimum parameter
    val lr_class = new LogisticRegression()
      .setMaxIter(10)
      .setRegParam(0.3)
      .setElasticNetParam(0.8)

    //build the logistic regression model using training set
    val lrModel_class = lr_class.fit(training)
    //print the coefficients and intercept for logistic regression
    println(s"Coefficients: ${lrModel_class.coefficients}" +
      s"\nIntercept: ${lrModel_class.intercept}")

    spark.stop()
    //in this lecture we discuss the basics of logistic regression in spark
  }
}
