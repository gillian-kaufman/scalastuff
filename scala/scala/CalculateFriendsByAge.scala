/* This lab will compute the average number of friends by age in a social network.
 * Each row in our dataset contains id, firstname, age, and number of friends. */
import CalculateFriendsByAge_template.parseLine
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext
object CalculateFriendsByAge_template
{
  //A function that splits a line of input into (age, numFriends) tuples.
  //please implement this function by yourself
  def parseLine(Line: String): (Int, Int) = {
    //Split the line by commas
    val splitLine = Line.split(",")
    //Extract the age and numFriends fields and convert to integers
    val age = splitLine(2).toInt
    val numFriends = splitLine(3).toInt
    //return key/value pairs key is age and value is numFriends
    (age, numFriends)
  }
}
object CalculateFriendsByAge
{
  def main(args: Array[String]): Unit = {
    //set log level to only print error otherwise it will print too much information
    Logger.getLogger("org").setLevel(Level.ERROR)

    //create sparkcontext and we want to use all local cores
    val sc = new SparkContext("local[*]", "FriendsByAge")

    //Load each line of the source data into an RDD
    //every row of RDD is the line of input
    val lines = sc.textFile("../Downloads/data_functional/data/friends.csv")

    //Using our parseLines function to convert each line to (age, numFriends) tuples
    //finally we produce key/value RDD
    //key is age and value is numFriends
    val rdd = lines.map(parseLine)

    //lots going on here
    //we are starting with an RDD of form (age, numFriends) where age is the key and numFriends is the value
    //we use mapValues to convert each numFriends value to a tuple of (numFriends, 1)
    //Then we use reduceByKey to sum up the total numFriends and total instances for each age.
    val totalsByAge = rdd.mapValues(x => (x,1)).reduceByKey((x, y) => (x._1 + y._1, x._2 + y._2))

    //so now we have tuples of (age, (totalFriends, totalInstances))
    //age is key, tuple (totalFriends, totalInstances) is value

    //To compute the average number of friends, we divide totalFriends/totalInstances for each age
    val averageByAge = totalsByAge.mapValues(x => x._1 / x._2)

    //collect the results from RDD
    //(this kicks off computing the DAG and actually executes the job)
    val results = averageByAge.collect()

    //sort and print final results
    results.sorted.foreach(println)

    //EXERCISE
    //show the average number of friends by the first name
  }
}
//Solution for EXERCISE (I hope)
object CalculateFriendsByFirstName_template
{
  //A function that splits a line of input into (firstname, numFriends) tuples.
  //please implement this function by yourself
  def parseLine(Line: String): (String, Int) = {
    //Split the line by commas
    val splitLine = Line.split(",")
    //Extract the firstname and numFriends fields
    val firstname = splitLine(1)
    val numFriends = splitLine(3).toInt   //convert to integer
    //return key/value pairs key is firstname and value is numFriends
    (firstname, numFriends)
  }
}
object CalculateFriendsByFirstName
{
  def main(args: Array[String]): Unit = {
    //set log level to only print error otherwise it will print too much information
    Logger.getLogger("org").setLevel(Level.ERROR)

    //create sparkcontext and we want to use all local cores
    val sc = new SparkContext("local[*]", "FriendsByAge")

    //Load each line of the source data into an RDD
    //every row of RDD is the line of input
    val lines = sc.textFile("../Downloads/data_functional/data/friends.csv")

    //Using our parseLines function to convert each line to (firstname, numFriends) tuples
    //finally we produce key/value RDD
    //key is first name and value is numFriends
    val rdd = lines.map(parseLine)

    //lots going on here
    //we are starting with an RDD of form (firstname, numFriends) where firstname is the key and numFriends is the value
    //we use mapValues to convert each numFriends value to a tuple of (numFriends, 1)
    //Then we use reduceByKey to sum up the total numFriends and total instances for each first name.
    val totalsByFirstName = rdd.mapValues(x => (x,1)).reduceByKey((x, y) => (x._1 + y._1, x._2 + y._2))

    //so now we have tuples of (firstname, (totalFriends, totalInstances))
    //firstname is key, tuple (totalFriends, totalInstances) is value

    //To compute the average number of friends, we divide totalFriends/totalInstances for each firstname
    val averageByFirstName = totalsByFirstName.mapValues(x => x._1 / x._2)

    //collect the results from RDD
    //(this kicks off computing the DAG and actually executes the job)
    val results = averageByFirstName.collect()

    //sort and print final results
    results.sorted.foreach(println)
  }
}
