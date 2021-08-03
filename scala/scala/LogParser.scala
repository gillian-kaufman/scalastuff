/* In this lab, we will show top URL's visited over a 5 minute window
 * from a stream of apache access logs on port 9999
 * In other words, we are listening to real click stream data on a real network.
 * This lab will demonstrate Spark streaming.
 *
 * At first, we will run ncat to publish the log data
 * access_log.txt is located in data folder
 * -i secs Delay interval for lines sent, ports scanned
 * nc -kl -i 1 9999 < access_log.txt
 *
 * wp-login.php hacker try every password in the book here
 * there are also vulnerabilities of Wordpress XML-RPC.php file.
 * Hacker try to get through
 * looking at this information is enlightening. I am under hacker attacker
 * homepage / is also popular request. */

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.storage.StorageLevel

import java.util.regex.Pattern
import java.util.regex.Matcher

import Utilities._

object LogParser
{
  def main(args: Array[String]): Unit = {
    //create the context with a 3 second batch size
    val ssc = new StreamingContext("local[*]", "LogParser", Seconds(3))

    setupLogging()

    //construct a regular expression (regex)
    //to extract fields from raw apache log lines
    val pattern = apacheLogPattern()

    //create a socket stream to read log data
    //published via netcat on port 9999 locally
    val lines = ssc.socketTextStream("127.0.0.1", 9999, StorageLevel.MEMORY_AND_DISK_SER)

    //extract the request field from each log line
    //if the record is invalid, discard
    //this is sample request field
    //http command, url and protocol
    //post /wp-login.php http/1.1
    val requests = lines.map(x =>
    {
      val matcher: Matcher = pattern.matcher(x)
      if (matcher.matches()) matcher.group(5)
    })
    //extract url from the request
    val urls = requests.map(x =>
    {
      val arr = x.toString.split(" ")
      if (arr.size == 3) arr(1)
      else "[error]"
    })

    //calculate url request count over a 5 minute window sliding every 3 seconds
    val urlCounts = urls.map(x => (x,1)).reduceByKeyAndWindow(_+_, _-_, Seconds(300), Seconds(3))

    //sort by url counts and print the results
    val sortedResults = urlCounts.transform(rdd =>
      rdd.sortBy(x => x._2, false))
    sortedResults.print()

    ////POST /wp-login.php HTTP/1.1
    /* requests.foreachRDD( (rdd, time) => {
     *
     *  println("\nsample data for requests")
     *  rdd.take(10).foreach(println)
     *  println()
     *
     * }) */
    //robots.txt
    ///blog/
    /* urls.foreachRDD( (rdd, time) => {
     *
     *  println("\nsample data for urls")
     *  rdd.take(10).foreach(println)
     *  println()
     *
     * }) */

    //set a checkpoint directory and kick it off
    //please create checkpoint directory
    ssc.checkpoint("../Downloads/checkpoint/")
    ssc.start()
    ssc.awaitTermination()
  }
}
