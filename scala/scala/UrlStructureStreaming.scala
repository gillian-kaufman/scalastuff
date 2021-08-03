//in this lab we will keep track of top urls in our apache access log using 30 seconds window
import org.apache.log4j._
import org.apache.spark.sql._
import org.apache.spark.sql.functions._

object UrlStructureStreaming
{
  def main(args: Array[String]): Unit = {
    //set log level to only print error otherwise it will print too much information
    Logger.getLogger("org").setLevel(Level.ERROR)

    // use new sparksession interface in spark 2.0
    val spark = SparkSession.builder.appName("StructuredStreaming").master("local[*]").getOrCreate()

    //read in streaming data from data/logs directory as a dataframe
    val accessLines = spark.readStream.text("../Downloads/data_functional/data/logs")

    //regular expressions to extract pieces of apache access log lines
    val contentSizeExp = "\\s(\\d+)$"
    val statusExp = "\\s(\\d{3})\\s"
    val generalExp = "\"(\\S+)\\s(\\S+)\\s*(\\S*)\""
    val timeExp = "\\[(\\d{2}/\\w{3}/\\d{4}:\\d{2}:\\d{2}:\\d{2} -\\d{4})]"
    val hostExp = "(^\\S+\\.[\\S+\\.]+\\S+)\\s"

    //apply these regular expressions to create structure from the unstructured text
    val logsDF = accessLines.select(regexp_extract(col("value"), hostExp, 1).alias("host"),
      regexp_extract(col("value"), timeExp, 1).alias("timestamp"),
      regexp_extract(col("value"), generalExp, 1).alias("method"),
      regexp_extract(col("value"), generalExp, 2).alias("endpoint"),
      regexp_extract(col("value"), generalExp, 3).alias("protocol"),
      regexp_extract(col("value"), statusExp, 1).cast("Integer").alias("status"),
      regexp_extract(col("value"), contentSizeExp, 1).cast("Integer").alias("content_size"))

    //so far these codes are the same as the previous lab
    //here is new function for this lab
    //add the new column with current timestamp
    //this timestamp is used to show when the data is ingested
    val logsDF2 = logsDF.withColumn("eventTime", current_timestamp())
    println("logsDF2 schema")
    logsDF2.printSchema()

    //keep a running count of endpoints with 30 second window sliding 10 seconds
    //count up the occurrence of each url every 10 seconds in 30 second window
    //endpoints is url
    val endpointCounts = logsDF2.groupBy(window(col("eventTime"),"30 seconds",
      "10 seconds"), col("endpoint")).count()
    println("endpointCounts schema")
    endpointCounts.printSchema()

    //sort by count
    val sortedEndpointCounts = endpointCounts.orderBy(col("count").desc)

    //display the endpointCounts stream to the console
    val query = endpointCounts.writeStream.outputMode("complete").format("console")
      .queryName("counts1").start()

    //display the sortedEndpointCounts stream to the console
    val query1 = sortedEndpointCounts.writeStream.outputMode("complete").format("console")
      .queryName("counts2").start()

    //wait until we terminate the scripts
    query.awaitTermination()

    //stop the session
    spark.stop()
  }
}
