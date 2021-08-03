// HOMEWORK 5 (DATAFRAME)
// Use the Netflix_2011_2016.csv file to Answer and complete
// the commented tasks below!
import org.apache.log4j._
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
object HW5
{
  def main(args: Array[String]): Unit = {
    //set log level to only print error otherwise it will print too much information
    Logger.getLogger("org").setLevel(Level.ERROR)

    // Start a simple Spark Session
    val spark = SparkSession.builder.appName("HW5").master("local[*]").getOrCreate()

    // Load the Netflix Stock CSV File, have Spark infer the data types.
    val ds = spark.read.option("header", "true")
      .option("inferSchema", "true")
      .csv("../Downloads/data_functional/data/Netflix_2011_2016.csv")

    // What are the column names?
    println("Column Names: "+ ds.columns.mkString(", ") +"\n")

    // What does the Schema look like?
    println("Schema:")
    ds.printSchema()

    // Print out the first 5 columns.
    ds.select(ds("Date"), ds("Open"), ds("High"), ds("Low"), ds("Close")).show(5)

    // Use describe() to learn about the DataFrame.
    ds.describe().show()

    // Create a new dataframe with a column called HV Ratio that
    // is the ratio of the High Price versus volume of stock traded
    // for a day.
    val ds2 = ds.withColumn("HV Ratio", ds("High")/ds("Volume"))
    ds2.show(5)

    // For Scala/Spark $ Syntax
    import spark.implicits._

    // What day had the Peak High in Price?
    ds.groupBy($"Date").agg(max($"High")).show()

    // What is the mean of the Close column?
    ds.groupBy($"Date").agg(mean($"Close")).show()

    // What is the max and min of the Volume column?
    ds.agg(min($"Volume"), max($"Volume")).show()

    // How many days was the Close lower than $ 600?
    println("How many days was Close lower than $600? " + ds.filter("Close < 600").count())

    // What percentage of the time was the High greater than $500 ?
    println("What percentage of the time was the high greater than $500?")
    print(ds.filter("High > 500").count() * 100/ds.count()+"%\n")

    // What is the Pearson correlation between High and Volume?
    ds.select(corr($"High", $"Volume")).show()

    // What is the max High per year?
    ds.groupBy(year($"Date")).agg(max($"High")).show()

    // What is the average Close for each Calender Month?
    ds.groupBy(month($"Date")).agg(mean($"Close")).show()

    spark.stop()
  }
}
