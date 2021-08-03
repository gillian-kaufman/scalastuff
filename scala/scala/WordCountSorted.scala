/* We want to count up how many times each word occurs in a book. Then sort the final results.
 * Based on final results, we want to know which is the most popular and least popular words. */
import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.log4j._

object WordCountSorted
{
  def main(args: Array[String]): Unit = {
    //set log level to only print error otherwise it will print too much information
    Logger.getLogger("org").setLevel(Level.ERROR)

    //create sparkcontext and we want to use all local cores
    val sc = new SparkContext("local[*]", "WordCountSorted")

    //load each line of the book into an RDD
    val inputRDD = sc.textFile("../Downloads/data_functional/data/book.txt")

    //split each line using a regular expression that extract words
    //we use the same functional API of scala. you feel like you go home
    //"\\W+" means multiple spaces
    val wordsRDD = inputRDD.flatMap(x => x.split("\\W+"))

    //normalize everything to lowercase
    val lowercaseWordsRDD = wordsRDD.map(x => x.toLowerCase())

    //lets do countByValue the hard way
    //first map each word to word count tuple
    //reduceByKey will combine information based on each key
    //v1 and v2 is the value for the same key
    //each element of wordsCountRDD is the tuple containing words and frequency of those words
    val wordsCountRDD = lowercaseWordsRDD.map(x => (x,1)).reduceByKey((v1, v2) => v1 + v2)

    //at first i switch key value pair
    //sort by key
    //collect results to local machine
    //use collect very carefully
    val wordCountSorted = wordsCountRDD.map { case(word, count) => (count, word)}
      .sortByKey().collect()

    //going through wordCountSorted array to figure out each word and its count
    for (result <- wordCountSorted) {
      val count = result._1
      val word = result._2
      println(s"$word: $count")
    }
    //your: 1420
    //to: 1825
    //you: 1878
  }
}
