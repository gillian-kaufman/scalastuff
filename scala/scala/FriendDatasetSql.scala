//in this lab we will practice sql functions for dataset
//we will use our friend.csv file as the example
//friend.csv has four columns (id, name, age, num of friends)

import org.apache.spark.sql._
import org.apache.log4j._

object FriendDatasetSql
{
  //case class is used to define schema for our database
  //each field corresponds to the column name and column type in our database
  //this is required to convert dataframe to dataset for compile time type check and optimization
  case class Person(id: Int, name: String, age: Int, friends: Int)

  def main(args: Array[String]): Unit = {
    //set log level to only print error otherwise it will print too much information
    Logger.getLogger("org").setLevel(Level.ERROR)

    //use new sparksession interface in spark 2.0
    val spark = SparkSession.builder.appName("SparkSQL").master("local[*]").getOrCreate()

    //convert our csv file to dataset using our person case
    //class to infer the schema
    //this is used to implicitly infer schema
    import spark.implicits._
    val friendDataset = spark.read.option("header", "true")
      .option("inferSchema", "true")
      .csv("../Downloads/data_functional/data/friends-header.csv")
      .as[Person] //convert dataframe to dataset
    //there are lots of other ways to make a dataframe/dataset
    //for example, spark.read.json("file path")
    //or sqlContext.table("hive table name")

    println("Here is our inferred schema: ")
    friendDataset.printSchema()

    println("Let's select the name column:")
    friendDataset.select("name").show()

    println("Filter out anyone over 21:")
    //in scala, you can pass the expression as parameter to the function
    friendDataset.filter(friendDataset("age") < 21).show()

    println("Group by age and count num of friends by age:")
    //sql function is much simpler than RDD solution
    friendDataset.groupBy("age").count().show()

    println("Make everyone 10 years older:")
    friendDataset.select(friendDataset("name"), friendDataset("age") + 10).show()

    //stop spark session when we are done
    spark.stop()
  }
}
