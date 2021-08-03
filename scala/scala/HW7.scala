//HW7
//Count up how many of each star rating exists in the MovieLens 100K data set.
import org.apache.spark.sql._
import org.apache.log4j._
import org.apache.spark.sql.types.{IntegerType, LongType, StructType}

object HW7
{
  // Create case class with schema of u.data
  // userID: Int, movieID: Int, rating: Int, timestamp: Long
  case class DataSchema(userID: Int, movieID: Int, rating: Int, timestamp: Long)

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)
    val spark = SparkSession.builder.master("local[*]").getOrCreate()

    // Create schema when reading u.data
    val userRatingsSchema = new StructType()
      .add("userID", IntegerType, nullable = true)
      .add("movieID", IntegerType, nullable = true)
      .add("rating", IntegerType, nullable = true)
      .add("timestamp", LongType, nullable = true)

    // Load up the data into spark dataset
    // Use tab as separator "\t",
    // load schema from userRatingsSchema and
    // force case class to read it as dataset
    import spark.implicits._
    val ratingDS = spark.read.option("sep", "\t")
      .schema(userRatingsSchema)
      .csv("../Downloads/data_functional/data/ml-100k/u.data")
      .as[DataSchema]

    // Select only ratings column
    // (The file format is userID, movieID, rating, timestamp)
    ratingDS.select("rating").show()

    // Count up how many times each value (rating) occurs using groupBy and count
    val ratingsCount = ratingDS.groupBy("rating").count()

    // Sort the resulting dataset by count column
    // Print results from the dataset
    ratingsCount.sort("count").show()
  }
}
