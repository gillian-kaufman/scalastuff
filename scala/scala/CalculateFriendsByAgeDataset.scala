//in this lab we will use sql functions in the dataset
//to compute the average number of friends by age in a social network
//in the previous lab we used the RDD to solve the same problem

import org.apache.log4j._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object CalculateFriendsByAgeDataset
{
  //create case class to define schema of friends.csv
  case class Friends(id: Int, name: String, age: Int, friends: Long)

  def main(args: Array[String]): Unit = {
    //set log level to only print error otherwise it will print too much information
    Logger.getLogger("org").setLevel(Level.ERROR)

    //use new sparksession interface in spark 2.0
    val spark = SparkSession.builder.appName("FriendsByAge").master("local[*]").getOrCreate()

    //convert our csv file to dataset using our person case
    //class to infer the schema
    //this is used to implicitly infer schema
    import spark.implicits._
    val ds = spark.read.option("header", "true")
      .option("inferSchema", "true")
      .csv("../Downloads/data_functional/data/friends-header.csv")
      .as[Friends] //convert dataframe to dataset

    //select only age and numFriends columns
    val friendsByAge = ds.select("age", "friends")

    //from friendsByAge we group by "age" and then compute avg
    //here we use one line of sql code to finish the complex job using RDD
    //this example show that sql function of dataset can make your tacks much simpler
    //but under the hood everything is implemented by RDD function in the lower level
    friendsByAge.groupBy("age").avg("friends").show()

    //sort by age
    friendsByAge.groupBy("age").avg("friends").sort("age").show()

    //round the avg number to 2 decimal places to produce better format
    friendsByAge.groupBy("age").agg(round(avg("friends"), 2))
      .sort("age").show()

    //we can customize the column name as well using alias
    friendsByAge.groupBy("age").agg(round(avg("friends"), 2))
      .alias("friends_avg").sort("age").show()

    spark.stop()
  }
}
