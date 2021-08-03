//Dataframe GroupBy and aggregate functions
//group rows by column and perform aggregate functions on it
import Utilities.setupLogging
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object GroupBy_Agg
{
  def main(args: Array[String]): Unit = {
    //start a simple spark session
    val spark = SparkSession.builder.master("local[*]").getOrCreate()

    setupLogging()
    //create a dataframe from spark session
    val df = spark.read.option("header", "true").option("inferSchema", "true")
      .csv("../Downloads/data_functional/data/Sales.csv")

    println("\nShow schema")
    df.printSchema()
    println()

    df.show()
    println()

    //GroupBy Categorical columns (group rows by column)
    //perform aggregate function in chunks based on what you group by
    //spark will take any numerical column and take average

    //mean sales across different companies
    df.groupBy("Company").mean().show()
    println()
    df.groupBy("Company").count().show()
    println()
    df.groupBy("Company").max().show()
    println()
    df.groupBy("Company").min().show()
    println()
    df.groupBy("Company").sum().show()
    println()

    //other aggregate functions
    //perform aggregate function on every single row on that column
    //http://spark.apache.org/docs/leatest/api/scala/index.html#org.apache.spark.sql.functions$

    //perform aggregate function on particular column and produce single result
    println("perform aggregate function on particular column")
    df.select(countDistinct("Sales")).show()
    df.select(sumDistinct("Sales")).show()
    df.select(variance("Sales")).show()
    df.select(stddev("Sales")).show()
    //df.select(("Sales")).show()
    println()

    //arrange sales in increasing order
    println("Order by Sales Ascending")
    df.orderBy("Sales").show()
    println()

    import spark.implicits.StringToColumn

    //arrange sales in decreasing order
    println("Order By Sale Descending")
    df.orderBy($"Sales".desc).show()

    //in this lecture we discuss groupby operations
    //and perform aggregate functions for dataframe
  }
}
