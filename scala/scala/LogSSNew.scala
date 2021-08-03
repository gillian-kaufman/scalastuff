//in the lab we will practice structure streaming using the dataset api instead of RDD api
//we will count occurrences of each status code in our streaming data (apache access log)
//our log file is located in the data/log directory
//at first you need to put access_log.txt into this directory
//after it finishes the first batch you can add access_log2.txt into the data/log directory

import org.apache.log4j._
import org.apache.spark.sql._
import org.apache.spark.sql.functions._

object LogSSNew
{
  def main(args: Array[String]): Unit = {
    //set log level to only print error otherwise it will print too much information
    Logger.getLogger("org").setLevel(Level.ERROR)

    // use new sparksession interface in spark 2.0
    val spark = SparkSession.builder.appName("StructuredStreaming").master("local[*]").getOrCreate()
    //read in streaming data from data/logs directory as a dataframe
    //we will keep monitoring the data/logs directory for the new text files
    //when new text files are discovered there it will append each line from the new
    //text file into accessLogLines dataframe
    val accessLogLines = spark.readStream.text("../Downloads/data_functional/data/logs")
    println("accessLogLines schema")
    accessLogLines.printSchema()

    //regular expressions to extract pieces of apache access log lines
    //you can download regular expression from blackboard
    val contentSizeExp = "\\s(\\d+)$"
    val statusExp = "\\s(\\d{3})\\s"
    val generalExp = "\"(\\S+)\\s(\\S+)\\s*(\\S*)\""
    val timeExp = "\\[(\\d{2}/\\w{3}/\\d{4}:\\d{2}:\\d{2}:\\d{2} -\\d{4})]"
    val hostExp = "(^\\S+\\.[\\S+\\.]+\\S+)\\s"

    //apply these regular expressions to create structure from the unstructured text
    //convert accessLogLines with only one value column into new dataframe with new column
    //such as host, timestamp, method
    val logsDF = accessLogLines.select(regexp_extract(col("value"), hostExp, 1).alias("host"),
      regexp_extract(col("value"), timeExp, 1).alias("timestamp"),
      regexp_extract(col("value"), generalExp, 1).alias("method"),
      regexp_extract(col("value"), generalExp, 2).alias("endpoint"),
      regexp_extract(col("value"), generalExp, 3).alias("protocol"),
      regexp_extract(col("value"), statusExp, 1).cast("Integer").alias("status"),
      regexp_extract(col("value"), contentSizeExp, 1).cast("Integer").alias("content_size"))

    println("logsDF schema")
    logsDF.printSchema()

    //keep a running count of status codes from input stream
    //in other words we want to know how many times each status code appears in the log file over time
    val statusCountsDF = logsDF.groupBy("status").count()
    println("statusCountsDF schema")
    statusCountsDF.printSchema()

    //display stream to the console
    //we will write stream out to the console
    val query = statusCountsDF.writeStream.outputMode("complete").format("console")
      .queryName("counts").start()

    //wait until we terminate the scripts
    query.awaitTermination()

    //stop the session
    spark.stop()
    //this lab shows how to use structured streaming
    //using structure streaming you can analyze the data in real time
    //instead of batch fashion
  }
}
