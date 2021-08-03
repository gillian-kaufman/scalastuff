/* Define an immutable object using functional style programming
 * Since scala allows an operator as the method name, this can potentially make your
 * code more understandable.
 *
 * x and y is called the class parameter, which is the parameter of the primary constructor
 * in front of x or y, we never put val/var. If you put val/var in front of x or y, x and y
 * become the field of the object. */

class Rational (x: Int, y: Int)
{
  // Figure out what is the greatest common divisor so that qw can reduce a rational to its
  // simplest form.
  // This is a recursive function without mutation
  private def gcd(a: Int, b: Int): Int = {
    if (b == 0) a //base case
    else gcd(b, a % b) //recursive case
  }
  // We define the private instance variable (field) holding gcd
  // The following two statements are executed when you build the object. In other words,
  // they are part of your primary constructor
  private val g = gcd(x, y)
  //println("g: " + g)

  // Auxiliary constructor
  // x is numerator, denominator is 1
  def this(x: Int) = this(x, 1)

  // You can omit parentheses if parameter list is empty
  def numer: Int = x/g
  def denom: Int = y/g

  // print rational in the nice format
  override def toString: String = numer + "/" + denom

  // Add operation
  // The left operand of add is the rational number itself.
  // Operators can be used as method names in Scala.
  // We return a new copy of the rational number. The original copy is untouched.
  def + (that: Rational): Rational = {
    new Rational(
      numer * that.denom + that.numer * denom,
      denom * that.denom
    )
  }

  // Compare two rational values
  def < (that: Rational): Boolean = {
    numer * that.denom < that.numer * denom
  }

  // take the maximum of two rational values
  def max(that: Rational): Rational = {
    if (this < that) that else this
  }

  // define negative of rational number
  def unary_- : Rational = new Rational(-numer, denom)

  // Subtraction operation for rational values
  def - (that: Rational): Rational = this + -that
}

object testRational
{
  def main(args: Array[String]): Unit = {
    val rat1 = new Rational(6, 8)
    println("rat1: " + rat1)

    val y = new Rational(6)
    println("y: " + y)

    val res1 = new Rational(3, 4)
    val res2 = new Rational(5, 7)
    val res3 = res1 + res2
    //res3 = res1.add(res2) <-- Java rational addition
    //res4 = res1 + res2 + res3
    //res4 = res1.add(res2).add(res3) <-- Java rational addition
    println("res3: " + res3)

    val res5 = new Rational(1, 3)
    val res6 = new Rational(3, 2)
    val res7 = res5 < res6
    println("res7: " + res7)

    val res8 = res5.max(res6)
    println("res8: " + res8)

    val res9 = res5 - res6
    println("res9: " + res9)

    //rat1: 3/4
    //y: 6/1
    //res3: 41/28
    //res7: true
    //res8: 3/2
    //res9: 7/-6
  }
}