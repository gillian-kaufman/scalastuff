import org.apache.log4j.Level
import java.util.regex.Pattern
import java.util.regex.Matcher

object Utilities {
  //Makes sure only ERROR messages get logged to avoid log spam.
  def setupLogging() = {
    import org.apache.log4j.{Level, Logger}
    val rootLogger = Logger.getRootLogger
    rootLogger.setLevel(Level.ERROR)
  }

  //Configures Twitter service credentials
  //using twiter.txt in the main workspace directory
  def setupTwitter () = {
    import scala.io.Source

    for(line <- Source.fromFile("/home/jetzhong/data/twitter.txt")
      .getLines) {
      val fields = line.split(" ")
      println(fields(0))
      println(fields(1))
      if(fields.length == 2) {
        System.setProperty("twitter4j.oauth." + fields(0), fields(1))
      }

    }

  } //end of setupTwitter

  /** Retrieves a regex Pattern for parsing Apache access logs. */
  def apacheLogPattern():Pattern = {
    val ddd = "\\d{1,3}"
    val ip = s"($ddd\\.$ddd\\.$ddd\\.$ddd)?" //ip address
    val client = "(\\S+)"
    val user = "(\\S+)"
    val dateTime = "(\\[.+?\\])"
    val request = "\"(.*?)\""
    val status = "(\\d{3})"   //error or successful
    val bytes = "(\\S+)"   //number of bytes
    val referer = "\"(.*?)\""  //came form another website
    val agent = "\"(.*?)\""      //what brower
    val regex = s"$ip $client $user $dateTime $request $status $bytes $referer $agent"
    Pattern.compile(regex)
  }
}
