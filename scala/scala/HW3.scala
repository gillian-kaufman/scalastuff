import scala.util.Random
object HW3
{
  def main(args: Array[String]): Unit = {
    //How do you get the largest element of an array with reduceLeft? In order to test the code,
    //you need first randomly generated array using the functional programming and print out the content of array (1)
    val randArr = Seq.fill(10) (Random.nextInt(20))
    println(randArr)
    val largest = randArr.reduceLeft((x, y) => if(x > y) x else y)
    println(largest)

    //Write the factorial function using to and reduceLeft, without a loop or recursion. You need add
    // a special case when n < 1. (2)
    def factorial(n: Int) = n match {
      case _ if n < 1 => 0
      case _ => (1 to n).reduceLeft(_ * _)
    }
    println(factorial(10))
    println(factorial(0))
    println(factorial(5))

    //Write a program that reads words from a text ﬁle. Use a mutable map to count how often each word appears. To read the words, simply use a java.util.Scanner:
    //val in = new java.util.Scanner(new java.io.File("myfile.txt"))
    //while (in.hasNext()) process in.next()
    //Or look at the documentations for a Scalaesque way. At the end, print out all words and their counts. (3)
    def countWords(ws: Array[String]): Map[String, Int] = {
      val map = scala.collection.mutable.Map.empty[String, Int]
      for(word <- ws) {
        val n = map.getOrElse(word, 0)
        map += (word -> (n + 1))
      }
      map.toMap
    }
    def readFile(filename: String): Array[String] = {
      val buf = scala.io.Source.fromFile(filename)
      val lines = (for (line <- buf.getLines()) yield line).mkString.toLowerCase.split("\\W+")
      buf.close
      lines
    }
    val words = readFile("../Downloads/text.txt")
    println(countWords(words))
    //Repeat the preceding exercise with an immutable map. (4)
    def countWords2(ws: Array[String]): Map[String, Int] = {
      var map = Map.empty[String, Int]
      for(word <- ws) {
        val n = map.getOrElse(word, 0)
        map += (word -> (n + 1))
      }
      map
    }
    def readFile2(filename: String): Array[String] = {
      val buf = scala.io.Source.fromFile(filename)
      val lines = (for (line <- buf.getLines()) yield line).mkString.toLowerCase.split("\\W+")
      buf.close
      lines
    }
    val words2 = readFile2("../Downloads/text.txt")
    println(countWords2(words2))
  }
}
