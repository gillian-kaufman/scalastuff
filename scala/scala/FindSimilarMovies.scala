//This lab will find similar movies based on rating of users
//This lab will demonstrate relatively complex scala and spark programming

//if lots of users rate two movies similarly, these two movies are considered similar
//this application is useful for a recommendation system and is called item-based
//collaborative filtering

import org.apache.spark._
import org.apache.log4j._
import scala.io.Source
import java.nio.charset.CodingErrorAction
import scala.io.Codec
import scala.math.sqrt

object FindSimilarMovies
{
  //load up a map of movie ids to movie names
  def loadMovieNames(): Map[Int, String] = {
    //Handle character encoding issues
    //strange code that might not be easy to convert to ascii code
    implicit val codec = Codec("UTF-8")
    codec.onMalformedInput(CodingErrorAction.REPLACE)
    codec.onUnmappableCharacter(CodingErrorAction.REPLACE)

    //create a map of ints to strings, and populate it from u.item
    //create empty immutable map
    var movieNames: Map[Int, String] = Map()
    val lines = Source.fromFile("../Downloads/data_functional/data/ml-100k/u.item").getLines()
    for (line <- lines) {
      val fields = line.split('|')
      if (fields.length > 1)
        movieNames += (fields(0).toInt -> fields(1))
    }
    movieNames
  }
  //create two new types: movie rating and user rating pair
  type MovieRating = (Int, Double)  //movieID, rating
  type UserRatingPair = (Int, (MovieRating, MovieRating))

  //input: (919, ((50, 3.0), (276, 5.0)))
  //output: ((50, 276), (3.0, 5.0))
  //output: ((340, 347), (5.0, 4.0))
  //given UserRatingPair, i will produce a new key value pair
  //key is movie pair and value is rating pair
  def makePairs(userRatings: UserRatingPair):
  ((Int, Int), (Double, Double)) = {
    val movieRating1 = userRatings._2._1
    val movieRating2 = userRatings._2._2

    val movie1 = movieRating1._1
    val rating1 = movieRating1._2
    val movie2 = movieRating2._1
    val rating2 = movieRating2._2

    ((movie1, movie2), (rating1, rating2))
  }

  //this function will remove the duplicate
  //for A B and B A pair, only one combination will survive
  //we only get one direction of every possible movie combination where
  //movie 1 is less than movie 2
  def filterDuplicates(userRatings: UserRatingPair): Boolean = {
    val movieRating1 = userRatings._2._1
    val movieRating2 = userRatings._2._2

    val movie1 = movieRating1._1
    val movie2 = movieRating2._1

    movie1 < movie2
  }
  //define two new variable types
  type RatingPair = (Double, Double)
  type RatingPairs = Iterable[RatingPair]

  //computeCosineSimilarity between 2 movies is based on all possible pairs of ratings
  //all possible pairs of ratings are organized into a vector
  //((51, 341),CompactBuffer((2.0,3.0), (1.0,1.0), (3.0,2.0)))
  def computerCosineSimilarity(ratingPairs: RatingPairs): (Double, Int) = {
    var numPairs: Int = 0
    var sum_xx: Double = 0.0
    var sum_yy: Double = 0.0
    var sum_xy: Double = 0.0

    for (pair <- ratingPairs) {
      val ratingx = pair._1
      val ratingy = pair._2

      sum_xx += ratingx * ratingx
      sum_yy += ratingy * ratingy
      sum_xy += ratingx * ratingy
      numPairs += 1
    }

    val numerator: Double = sum_xy
    val denominator = sqrt(sum_xx) * sqrt(sum_yy)

    var score: Double = 0.0
    if(denominator != 0)
      score = numerator / denominator

    (score, numPairs)
  }

  def main(args: Array[String]): Unit = {
    //set log level to only print error otherwise it will print too much information
    Logger.getLogger("org").setLevel(Level.ERROR)

    //create sparkcontext using every core of the local machine.
    val sc = new SparkContext("local[*]", "MovieSimilarities")

    println("\nLoading movie names...")

    //load the dictionary, key is movie id and value is movie name
    val nameDict = loadMovieNames()

    //verify nameDict results
    println()
    println("size of movie dictionary " + nameDict.size)
    val res0 = nameDict.take(3)
    println("print 5 sample more movie dictionary")
    res0 foreach {case (key, value) => println(key + " --> " + value)}
    println("sample movie name for 28 " + nameDict(28))
    println()

    //load movie rating from u.data
    val data = sc.textFile("../Downloads/data_functional/data/ml-100k/u.data")

    //map ratings to key value pairs:
    //user id (key) => (movie id, rating) (value)
    //(488, (357, 4.0))
    val ratings = data.map(l => l.split("\t"))
      .map(l => (l(0).toInt, (l(1).toInt, l(2).toDouble)))

    //verify ratings data
    println()
    println("print sample data for ratings")
    ratings.takeSample(false, 3).foreach(println)
    println()

    //self join will produce every pair (combination) of movies rated together by the same user

    //if user watches movies a, b, and c
    //this is every possible pair: ab, ba, ac, ca, aa, bb, cc
    //one sample output
    //(919, ((50, 3.0),(276,5.0)))

    val joinedRatings = ratings.join(ratings)
    println()
    println("print sample data for joinedRating")
    joinedRatings.takeSample(false, 3).foreach(println)
    println("joinedRatings      count " + joinedRatings.count())
    println()

    //RDD consists of userID => ((movieID1, rating1), (movieID2, rating2))
    //(919, ((50, 3.0),(276,5.0)))
    //filter out duplicate pairs
    //(movieID1, movieID2) is the same as (movieID2, movieID1)
    val uniqueJoinedRatings = joinedRatings.filter(filterDuplicates)
    //print out how many items in uniqueJoinedRatings
    println("uniqueJoinedRatings count " + uniqueJoinedRatings.count())

    //produce new key value pair
    //now key is (movie1, movie2) pairs
    //((50, 276), (3.0, 5.0))
    //((340, 347), (5.0, 4.0))
    val moviePairs = uniqueJoinedRatings.map(makePairs)
    println()
    println("print sample data for moviePairs")
    moviePairs.takeSample(false, 3).foreach(println)
    println()

    //we now have (movie1, movie2) => (rating1, rating2)
    //now collect all ratings for each movie pair

    //we can improve the performance by using groupByKey
    //((51, 341),CompactBuffer((2.0,3.0), (1.0,1.0), (3.0,2.0)))
    val moviePairRatings = moviePairs.groupByKey()

    println("\nprint sample data for moviePairRatings")
    moviePairRatings.takeSample(false, 3).foreach(println)
    println()

    //we now have (movie1, movie2) => (rating1, rating2), (rating1, rating2) ...
    //computer similarity between two movies based on all pairs of ratings
    //any time you will perform more than one action on an RDD you must cache it!!!
    //((97,211),(0.9573246821544463, 124)) sample output
    val moviePairsSimilarities = moviePairRatings.mapValues(computerCosineSimilarity)
      .cache()

    println("\nprint sample data for moviePairSimilarities")
    moviePairsSimilarities.takeSample(false, 3).foreach(println)
    println()
    //save results if desired
    //val sorted = moviePairSimilarities.sortByKey()
    //sorted.saveAsTextFile("movie-sims")

    //this is similarity strength threshold
    val scoreThreshold = 0.97
    //number of co occurrence
    //only movies which are watched by 50 people count
    val coOccurrenceThreshold = 50.0

    //we will find similar movies for star wars
    val movieID: Int = 50
    println("movieID " + movieID)

    //keep the related movie pairs which are above the given similarity strength
    //and number of occurrences
    //key: (movieID, movieID)
    //value: (similarityscore, number of occurrences)
    //((97,211),(0.9573246821544463, 124))
    val filteredResults = moviePairsSimilarities.filter(x => {

        val pair = x._1
        val sim = x._2
        (pair._1 == movieID || pair._2 == movieID) && sim._1 > scoreThreshold &&
          sim._2 > coOccurrenceThreshold
      }
    )
    println("\nfiltered results count "+ filteredResults.count())
    println()
    //swap key value pairs and sort by similarity score using sortByKey
    //we will take the top 10 results
    //sortByKey is an expensive operation requiring shuffling
    val results = filteredResults.map(x => (x._2, x._1)).sortByKey(false).take(10)

    println("\nprint sorted results ")
    //((0.9573246821544463, 124)),(97,211))
    results.foreach(println)
    println()

    //print top 10 similar movies for star wars (id 50)
    println("\nTop 10 similar movies for " + nameDict(movieID))
    for(result <- results) {
      val sim = result._1
      val pair = result._2

      //display the similarity result that isnt the movie were looking at
      val similarMovieID = if (movieID == pair._1) pair._2 else pair._1
        //Empire Strikes Back, The (1980)
        //score: 0.9895522078385338 strength: 345
        //((0.9895522078385338, 345),(50,172))
      println(nameDict(similarMovieID) + "\tscore: " + sim._1 + "\tstrength: " + sim._2)
      println(result)
      //movieID 50 is star wars
      //in this lab we discuss how to find similar movies using item based collaborative filtering
    }
  }
}
