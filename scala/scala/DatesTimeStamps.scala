//discuss how to handle dates and timestamps

import org.apache.spark.sql.SparkSession
import Utilities._
//for date time functions
import org.apache.spark.sql.functions._

object DatesTimeStamps
{
  def main(args: Array[String]): Unit = {
    //start a simple spark session
    val spark = SparkSession.builder.master("local[*]").getOrCreate()

    setupLogging()
    val df = spark.read.option("header", "true").option("inferSchema","true")
      .csv("../Downloads/data_functional/data/CitiGroup2006_2008")

    println()
    df.printSchema()
    println()

    //lots of options here
    //org.apache.spark.sql.functions
    println("\njust select month from timestamp object")
    df.select(month(df("Date"))).show(5)
    println("\n\n")
    //select year from date column
    df.select(year(df("Date"))).show(5)
    println()

    //you want to use that timestamp information to perform some operations
    //for example we want to find out avg closing price per year
    println("Adding Column")
    //add year column
    val df2= df.withColumn("Year", year(df("Date")))

    //this import is needed to use the $ notation
    //abstract class is SQLImplicits
    import spark.implicits.StringToColumn

    //group by rows based years and calculate avg value per year
    val dfavgs = df2.groupBy("year").mean()
    dfavgs.show()
    println("\nMean per year, noticing large 2008 drop!")
    //finally select avg close price per year
    dfavgs.select($"Year", $"avg(Close)").show()
    println()

    //see floor price per year
    //this reflects the financial crisis for 2008
    //group by year and calculate min price
    val dfmins = df2.groupBy("Year").min()
    //select min(Close) column
    dfmins.select($"Year", $"min(Close)").show()

    //in this lecture we discuss how to handle dates and timestamps
  }
}
