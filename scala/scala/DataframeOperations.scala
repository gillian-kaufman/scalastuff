/* Dataframe operations can use either scala syntax or sql syntax */

import org.apache.spark.sql.SparkSession
import Utilities._

object DataframeOperations
{
  def main(args: Array[String]): Unit = {
    //start a simple spark session
    val spark = SparkSession.builder.master("local[*]").getOrCreate()

    setupLogging()

    val df = spark.read.option("header", "true").option("inferSchema", "true")
      .csv("../Downloads/data_functional/data/CitiGroup2006_2008")

    println("Schema")
    df.printSchema()
    println()

    //this import is needed to use the $ notation
    //abstract class SQLImplicits
    import spark.implicits.StringToColumn

    println("Filtering Operation\nGrabbing all rows where a column meets a condition")

    //there are two main ways of filtering out data
    //one way is to use spark sql syntax
    //the other way is to use scala syntax
    //we mainly stick to scala notation

    //get rows with closing price greater than $480
    df.filter($"Close" > 480).show(5)
    println("\nSQL notation")
    //can also use sql notation (you pass in where condition)
    df.filter("Close > 480").show(5)

    println("\nCount how many results")
    println(df.filter($"Close" > 480).count() + "\n")

    //note the use of triple === this may change in the future!
    println("Get only one row satisfying certain condition")
    df.filter($"High" === 484.40).show(4)
    //df.filter("High = 484.40").show() sql notation
    //can also use sql notation for count
    //df.filter("High = 484.40").count()

    //multiple filters
    println("\nMultiple Filters with Multiple Conditions")
    //select rows with both close and high less than 480
    df.filter($"Close" < 480 && $"High" < 480).show(5)

    println("\nSQL notation")
    df.filter("Close < 480 AND High < 484.40").show(5)

    println("Collect results into a scala object (Array)")
    //row object can be indexed to access each column
    val High484 = df.filter($"High" === 484.40).collect()
    High484.foreach(println)
    println()

    import org.apache.spark.sql.functions.corr

    //operations and useful functions
    //http://spark.apache.org/docs/latest/api/scala/index.html#org.apache.spark.sql.functions$

    //high and low are highly correlated (0.999)
    //within certain days, high price and low price are roughly going up and down together
    println("Pearson Correlation between two columns")
    df.select(corr("High", "Low")).show()
  }
  //in this lecture we discuss filter operation
  //you can use either scala or sql syntax
}
