/* In the next few labs, we will discuss dataframe, which is the foundation for machine learning.
 * library offered by Spark.
 * In Spark, Dataframes are the distributed collections of data, organized into rows and columns.
 * Each column in a Dataframe has a name and an associated type.
 * Dataframes are similar to traditional database tables, which are structured and concise.
 * We can say that dataframes are relational databases with better optimization techniques.
 * RDD is resilient distributed dataset. It is less efficient in terms of storage and computation
 * compared to dataframe. However, RDD is the foundation of dataframe operations. */

/* Getting started with dataframes! */
//dataset include stock information for citigroup from 2006 to 2008
//most important link
//http://spark.apache.org/docslatest/api/scala/index.html#org.apache.spark.sql.Dataset
import org.apache.spark.sql.SparkSession
import Utilities._

object Dataframe_Overview
{
  def main(args: Array[String]): Unit = {
    //start a simple spark session
    val spark = SparkSession.builder.master("local[*]").getOrCreate()

    setupLogging()

    //create dataframe from spark session
    //header = true will treat first row as header (get column name)
    //inferSchema" = "true will infer type for each column
    //spark can infer the schema based on objects or elements inside
    //data source
    val df = spark.read.option("header", "true").option("inferSchema","true")
      .csv("../Downloads/data_functional/data/CitiGroup2006_2008")

    //find out datatypes
    //print schema (column name and its data type)
    println("Schema")
    df.printSchema()

    println("\nDescribe Show Statistics of Numerical Columns")
    df.describe().show()

    println("\nColumn Name")
    println(df.columns.mkString(", ") + "\n")

    //get first 10 rows
    println("first 10 rows")
    for (row <- df.head(10)) {
      println(row)
    }
    println()

    println("\nSelect columns called Volumes")
    df.select("Volume").show(5)
    println()

    //use $ sign
    import spark.implicits.StringToColumn

    println("\nSelect multiple columns Date and Close")
    df.select($"Date", $"Close").show(2)

    println("\nCreating New Columns with New Dataframe")

    val df2 = df.withColumn("HighPlusLow", $"High" + $"Low")
    println("\n" + df2.columns.mkString(", ") + "\n")
    df2.printSchema()
    println()

    df2.head(5).foreach(println)
    println()

    println("Renaming Columns (and selecting close column)")
    //you can use $ notation $"Close" or df2("Close") to select column
    df2.select(df2("HighPlusLow").as("HPL"), df2("Close")).show()

    //in this lecture we discuss how to create dataframe from csv file
    //how to select rename and create new columns for dataframe
  }
}
