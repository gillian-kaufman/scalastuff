//Let us create a system that will actually look at the status codes that are returned on
//each access log line and raise some sort of alarm if we start to see too many errors
//being returned
//observe status code which is a 3 digit number. success code is 200 range
//failure code is 500 range
//raise alarm is too many errors are returned
//failure codes are due to invalid requests by hackers
//it is wrong because attack
//hacker get into the site and shut it down briefly
//slow down the broadcasting
//-i secs Delay interval for lines sent, ports scanned
//nc -kl -i 1 9999 < access_log.txt

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.storage.StorageLevel

import java.util.regex.Pattern
import java.util.regex.Matcher

import Utilities._

object LogAlarmer
{
  def main(args: Array[String]): Unit = {
    //create the context with a 3 second batch size
    val ssc = new StreamingContext("local[*]", "LogAlarmer", Seconds(3))

    setupLogging()
    //construct a regular expression (regex)
    //to extract fields from raw apache log lines
    val pattern = apacheLogPattern()
    //create a socket stream to read log data
    //published via netcat on port 9999 locally
    val lines = ssc.socketTextStream("127.0.0.1", 9999, StorageLevel.MEMORY_AND_DISK_SER)
    //extract the status field from each log line
    val statuses = lines.map( x =>
    {
      val matcher: Matcher = pattern.matcher(x)
      if (matcher.matches()) matcher.group(6)
      else "[error]"
    })
    //now map these status results to success and failure
    val successFailure = statuses.map( x => {
      //since not all strings can be converted to integer, we use
      //try function in util to recover from error
      val statusCode = util.Try(x.toInt) getOrElse 0
      //code between 200 and 300 is success
      if (statusCode >= 200 && statusCode < 300){
        "Success"
      } else if (statusCode >= 500 && statusCode < 600){
        "Failure"
      } else {
        "other"
      }
    })

    /*successFailure.foreachRDD( (rdd, time) => {
     *  println("\nsample data for successFailure")
     *  rdd.take(5).foreach(println)
     *  println()
     *})*/
    //tally up statuses over a 3 minute window sliding every 3 seconds
    val statusCounts = successFailure.countByValueAndWindow(Seconds(300), Seconds(3))

    //for each batch, get the RDD's representing data from our current window
    statusCounts.foreachRDD((rdd, time) => {
      //keep track of total success and error codes from each RDD
      var totalSuccess: Long = 0
      var totalError: Long = 0

      if (rdd.count() > 0) {
        //size of each rdd is small
        val elements = rdd.collect()
        for (element <- elements) {
          val result = element._1
          val count = element._2
          if (result == "Success")
            totalSuccess += count
          if (result == "Failure")
            totalError += count
        }
      }//end of if
      //println totals from current window
      println("Total success: "+ totalSuccess + " Total failure: "+ totalError)
      //dont alarm unless we have some minimum amount of data to work with
      if (totalError + totalSuccess > 100) {
        //comput error rate
        //note use of util.Try to handle potential divide by zero exception
        val ratio: Double = util.Try(totalError.toDouble / totalSuccess.toDouble) getOrElse 1.0
        //if there are more errors than successes, wake someone up
        if (ratio > 0.5) {
          println("HORRIBLY WRONG.")
        } else
          println("All systems go.")
      }
    }) //end of foreachRDD

    /* statuses.foreachRDD( (rdd, time) => {
     *    println("\nsample data for statuses")
     *    rdd.take(10).foreach(println)
     *    println()
     * })
     *
     * successFailure.foreachRDD( (rdd, time) => {
     *    println("\nsample data for successFailure")
     *    rdd.take(10).foreach(println)
     *    println()
     * })
     *
     * statusCounts.foreachRDD( (rdd, time) => {
     *    println("\nsample data for statusCounts")
     *    rdd.take(10).foreach(println)
     *    println()
     * }) */

    //kick it all off
    //set a checkpoint directory and kick it all off
    ssc.checkpoint("../Downloads/checkpoint/")
    ssc.start()
    ssc.awaitTermination()
  }
}
