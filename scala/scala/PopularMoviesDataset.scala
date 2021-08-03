//in this lab use datasets instead of RDDs
//datasets can optimize sql queries better than plain old RDDs
//datasets can also represent data more efficiently than RDD when data is in the structure format
//structure format means data is organized by column (such as excel or databse files)
//an example of unstructured data is text, voice file, or picture
//we convert our previous RDD solution to the dataset solution
import org.apache.spark.sql._
import org.apache.log4j._
import scala.io.{Codec, Source}
import java.nio.charset.CodingErrorAction
import org.apache.spark.sql.functions._

object PopularMoviesDataset
{
  //load up a map of movie IDs to movie names
  def loadMovieNames: Map [Int, String] = {
    //this will handle the bad encoding
    implicit val codec = Codec("UTF-8")
    codec.onMalformedInput(CodingErrorAction.REPLACE)
    codec.onUnmappableCharacter(CodingErrorAction.REPLACE)

    //create a map of ints to strings and populate it from u.item
    //each  line of u.item has movieID and movie title
    //Map is immutable but your reference is mutable
    var movieNames: Map[Int, String] = Map()
    //i am reading line by line from u.item each element of lines is one line
    val lines = Source.fromFile("../Downloads/data_functional/data/ml-100k/u.item").getLines()
    //going through each line and populate the map
    for(line <- lines) {
      val field = line.split('|')
      //confirm there is data in the row (no blank lines)
      if (field.length > 1) {
        //mutate reference for each iteration create new map object for each time
        movieNames += (field(0).toInt -> field(1))
      }
    }
    movieNames //the map object
  } //end of loadMovieNames

  //here we use case class so that we can get a column name for our movie ID
  //final means there is no subclass
  //movie has one immutable instance variable called movieID
  final case class Movie(movieID: Int)

  def main(args: Array[String]): Unit = {
    //set log level to only print error otherwise it will print too much information
    Logger.getLogger("org").setLevel(Level.ERROR)

    // use new sparksession interface in spark 2.0
    val spark = SparkSession.builder.appName("PopularMoviesDataset").master("local[*]").getOrCreate()
    //we extract movieID and construct RDD of movie objects
    //we have to do it the old fashion way using RDD since data is unstructured
    //if we have json data or actual database to import data in
    //we can produce a dataset directly without the intermediate step
    val movieObjectRDD = spark.sparkContext.textFile("../Downloads/data_functional/data/ml-100k/u.data")
      .map(x => Movie(x.split("\t")(1).toInt))
    println("\nCollect sample data for movieObjectRDD")
    movieObjectRDD.takeSample(false, 4).foreach(println)
    println()

    //Convert RDD to dataset
    import spark.implicits._
    val moviesDS = movieObjectRDD.toDS()

    //this is sql style magic to sort all movies by popularity in one line
    //remember for RDD we get key value RDD then use reduce operation
    //group the movie and count number of movie per group
    //then sort movie based on number
    val topMovieIDs = moviesDS.groupBy("movieID").count().orderBy(desc("count")).cache()
    topMovieIDs.show()
    println()

    //grab top 10 movies
    val top10 = topMovieIDs.take(10)

    //load up the movie ID -> name map
    val names = loadMovieNames

    val res0 = names.take(4)
    println("print 4 samples for movie dictionary map")
    res0 foreach {case (movieID, name) => println (movieID + " --> " + name)}

    //print the results for top 10 movies
    println()
    for (result <- top10) {
      //result is just a row at this point we need to cast it back to int
      println(names(result(0).asInstanceOf[Int]) + ": "+ result(1))
    }
    //stop the session
    spark.stop()
  }
}
