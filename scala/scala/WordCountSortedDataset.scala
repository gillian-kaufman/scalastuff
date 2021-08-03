//in this lab we will count up how many of each word occurs in a book
//using regular expressions and sorting the final results

//in our previous example we used RDD only
//in this example we use combination of RDD and datasets
//RDD is better suited to load the raw unstructured data. the dataset is used to perform sql operations
//as a result we get the best of both worlds
import org.apache.log4j._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object WordCountSortedDataset
{
  def main(args: Array[String]): Unit = {
    //set log level to only print error otherwise it will print too much information
    Logger.getLogger("org").setLevel(Level.ERROR)

    // use new sparksession interface in spark 2.0
    val spark = SparkSession.builder.appName("WordCount").master("local[*]").getOrCreate()

    //read each line of book into bookRDD
    val bookRDD = spark.sparkContext.textFile("../Downloads/data_functional/data/book.txt")

    //split each line using a regular expression that extracts words
    val wordsRDD = bookRDD.flatMap(x => x.split("\\W+"))

    import spark.implicits._

    //convert rdd to dataset
    //our dataset has one column named value
    //rdd is good at loading raw unstructured data
    //dataset is better for sql analysis is because of sql operations and optimatization
    val wordsDS = wordsRDD.toDS()

    println("show wordsDS")
    wordsDS.show(4)

    //normalize everything to lowercase using lower function
    //rename column name from value to word
    val lowercaseWordDS = wordsDS.select(lower($"value").alias("word"))

    //count up the occurrences of each word using groupby and count
    //groupby will group by all unique words together
    val wordCountsDS = lowercaseWordDS.groupBy("word").count()

    println("Schema for wordCountsDS")
    wordCountsDS.printSchema()
    wordCountsDS.show(4)

    //sort by counts
    val wordCountSortedDS = wordCountsDS.sort("count")
    //show complete sort results
    wordCountSortedDS.show(wordCountSortedDS.count.toInt)

    //in this lab we provide sql query solution to count how many of each word occurs in the book
  }
}
