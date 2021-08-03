//handle missing data
//schema of data: ID, Name, Sales
//the sample file has only last row complete

import org.apache.spark.sql.SparkSession
import Utilities._

object MissingData
{
  def main(args: Array[String]): Unit = {
    //start a simple spark session
    val spark = SparkSession.builder.master("local[*]").getOrCreate()

    setupLogging()

    val df = spark.read.option("header", "true").option("inferSchema", "true")
      .csv("../Downloads/data_functional/data/ContainsNull.csv")

    println()
    df.printSchema()
    println()

    //notice missing values!
    df.show()

    //you basically have 3 options with null values
    //1. just keep them, maybe only let a certain percentage through
    //2. drop them
    //3. fill them in with some other value
    //no correct answer, youll have to make the decision based on the data!

    //dropping values
    //technically still experimental but it has been around since 1.3
    println("Drop any rows with any amount of null values")
    df.na.drop().show()
    println()

    println("Drop any rows that have less than a minimum Number of NON null values ( < Int)")
    //drop any rows that have less than 2 NON null values
    df.na.drop(2).show()
    println()

    //interesting behavior!
    //what happens when using double/int vs strings

    //based on the data type you put in for fill, it will look for
    //all the columns that match that data type and fill it in

    println("Fill in the Na values with Int")
    //fill in the missing value with 100
    df.na.fill(100).show()
    println()

    println("Fill in String will only go to all string columns")
    //fill in the missing value with "missing name"
    df.na.fill("Missing Name").show()
    println()

    println("Be more specific, pass an array of string column names")
    //fill in missing value on column name with "new name"
    df.na.fill("New Name", Array("Name")).show()
    println()

    //Exercise: Fill in sales with average sales
    //how to get averages? for now, a simple way is to use describe!

    df.describe().show()
    println()

    println("Fill in missing value in sales column with average sales")
    df.na.fill(400.5, Array("Sales")).show()

    println("fill in missing value for sales and name column")
    val df2 = df.na.fill(400.5, Array("Sales"))
    df2.na.fill("missing name", Array("Name")).show()

    //In this lecture we discuss how to handle missing value
    //using drop or fill option
  }
}
