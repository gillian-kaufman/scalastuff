/* We will get real movie rating data and use Spark to analyse it and produce a histogram
 * of distribution of movies rating.
 * How many people rate movies five stars vs one star
 * We will focus on u.data. u.data contains actual movie rating data.
 * Each row has userID, movieID, ratings, time stamps. */
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext

object MovieRatingsCounter
{
  def main(args: Array[String]): Unit = {
    //set log level to only print error otherwise it will print too much information
    Logger.getLogger("org").setLevel(Level.ERROR)

    //create sparkcontext using every core of the local machine.
    //master will specify which cluster you will run your big data application
    val sc = new SparkContext("local[*]", "MovieRatingsCounter")

    //Load up each line of ratings data into RDD
    //RDD is called resilient distributed dataset
    //resilient means if some of the machines fail, we can still recover the lost data
    //each element of RDD is one line of data
    //each line has userID, movieID, ratings, time stamps, split by tab
    val linesRDD = sc.textFile("../Downloads/data_functional/data/ml-100k/u.data")
    //for declarative style programming, implementation detail will depend on your app.
    //split each line by tabs and extract their field (the actual rating itself)
    val ratingsRDD = linesRDD.map(x => x.split("\t")(2))

    //count up how many times each value (rating) occurs
    val results = ratingsRDD.countByValue()

    //sort resulting map of (rating, count) tuples
    //Map doesnt have a sorting capability
    //thats why we will convert map to seq
    //val sortedResults = results.toSeq.sortBy(x => x._1)
    val sortedResults1 = results.toSeq.sortBy{case (rating, count) => rating}

    //print each result
    sortedResults1.foreach(println)

    //map is a transformation operation, which is lazy evaluation
    //countByValue is action. Only action can trigger lazy evaluation

    //(1, 6110
    //(2, 11370)
    //(3, 27145)
    //(4, 34174)
    //(5, 21201)
  }
}
