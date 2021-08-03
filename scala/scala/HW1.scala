import scala.math.BigInt.probablePrime
import scala.math._
import scala.util.Random
object HW1
{
  def main(args: Array[String]): Unit = {
    /* Write a function called product(s: String) that computes the product of the unicode values of all
     * letters in a string. For example, the product of the characters in "Hello" is 9415087488. */
    def product(s: String): BigInt = {
      s map(i => BigInt(i)) product
    }
    println(product("Hello") + "\n")

    /* Write the Scala equivalent for the Java for loop:
     * for (int i = 10; i >= 0; i--) System.out.println(i); */
    for (i <- 10 to 1 by -1)
      println(i)
    println()

    /* Using BigInt, compute 2^1024 */
    println((BigInt(2) pow 1024) + "\n")

    /* Write a function countdown(n: Int) that prints the numbers from n to 0 */
    def countdown(n: Int): Unit = {
      for (i <- n to 1 by -1)
        println(i)
    }
    //println(countdown(5))
    countdown(5)
    println()

    /* One way to create random file or directory names is to produce a random BigInt and convert it to base 36,
     * yielding a string such as qsnvbevtomcj38o06kul. Poke around Scaladoc to find a way of doing this in Scala. */
    val randomnum: BigInt = probablePrime(100, Random)
    val name: String = randomnum.toString(36)
    println(name + "\n")

    /* The signum of a number is 1 if it is positive, -1 if it is negative, and 0 if it is 0.
     * Write a function that computes this value. */
    def signum(n: Int): Int = {
      if (n == 0) 0
      else if(n < 0) -1
      else 1
    }
    println(signum(5))
    println(signum(-5))
    println(signum(0) + "\n")
  }
}
