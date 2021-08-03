/* This chapter introduces pattern matching.
 * Functional programming offers a very powerful pattern matching mechanism. */
object Ch14
{
  def main(args: Array[String]): Unit = {
    //14.1 Better switch
    //C style switch statement in Scala
    for (ch <- "+-!") {
      var sign = 0
      //ch is the target, + is the pattern
      /* Pattern matching is an expression, which produces a value.
       * Functional languages prefer expressions over statements since expressions
       * have no side effects. */
      sign = ch match {
        case '+' => 1
        case '-' => -1
        //_ represents the default pattern
        case _ => 0
      }
      print(sign + " ")
    }
    println()
    //1 -1 0

    //you can use the match statement with any type not just numbers or chars
    import java.awt._

    val color = SystemColor.textText
    //pattern matching for color
    val res0 = color match {
      //in this example your pattern is constant
      case Color.RED => "Text is red"
      case Color.BLACK => "Text is black"
      case _ => "Not red or black"
    }
    println("res0: " + res0)
    //res0: text is black

    /* 14.3 variables in patterns
     *
     * If the case keyword is followed by a variable name, then the match expressions is assigned
     * to that variable.*/
    val str1 = "+-3!"
    for(i <- 0 until str1.length) {
      var sign1 = 0
      var digit1 = 0

      str1(i) match {
        case '+' => sign1 = 1
        case '-' => sign1 = -1
        //ch is a variable, if the pattern is a variable its value is the value from target
        //in this situation ch will match to anything
        case ch => digit1 = 10
        case _ =>
      }
      println(str1(i) + " " + sign1 + " " + digit1)
    }
    println()
    println()
    //+ 1 0
    //- -1 0
    //3 0 10
    //! 0 10

    /* 14.4 Type patterns
     *
     * You can match on the type of an expression.
     * BigInt(42) produces the instance of a class
     * BigInt is the object (class with single instance) */
    for (obj <- Array(42, "42", BigInt(42), BigInt, 42.0)) {
      val result = obj match {
        //In the first pattern, the target is bound to x as an Int
        case x: Int => x
        case s: String => Integer.parseInt(s)
        //_ means I do not care about the name and value of this variable
        case _ : BigInt => Int.MaxValue
        case BigInt => -1
        case _ => 0
      }
      println(result)
    }
    println()
    println()

    /* This example demonstrates that pattern matching is more powerful and flexible
     * than the isInstanceOf (check type) and asInstanceOf (type casting) mechanisms.
     *
     * When you match against a type, you must supply a variable's name.
     * Otherwise you match against the object (single instance of the class)
     *
     * In Scala, this pattern matching form is preferred over using the isInstanceOf operator.
     * Always arrange your pattern from specific to general. */
    for (obj <- Array(Map("Fred" -> 42), Map(42 -> "Fred"), Array(42), Array("Fred")))
    {
      val result = obj match {
        case m: Map[_,_] => "it is a map"
        case a: Array[Int] => "It is an array[Int]"
        case b: Array[_] => "it is an array of something other than int"
      }
      println(result)
    }
    println()

    //it is a map
    //it is a map
    //it is an array[Int]
    //it is an array of something other than int

    /* 14.5 Matching Arrays, Lists, and Tuples
     * To match an array against its contents, use Array expression in the pattern. */
    for (arr <- Array(Array(0), Array(1, 0), Array(0, 1, 0), Array(1, 1, 0)))
    {
      val result = arr match {
        case Array(0, _*) => "0.."    //match array starting with 0
        case Array(0) => "0"          //match array containing only 0
        case Array(x, y) => x+" "+y   //match array with two elements
        //third pattern matches any array starting with 0
        case _ => "something else"
      }
      println(result)
    }
    println()
    println()

    //You can match lists in the same way, with the list expression
    //An Array is a mutable data structure. Here, List is immutable
    import scala.collection.immutable.List

    for (lst <- Array(List(0), List(1, 0), List(2, 0, 0), List(1, 0, 0)))
    {
      val result = lst match {
        case 0 :: Nil => "0"          //list with only 0
        case x :: y :: Nil => x+" +y" //lists with 2 elements
        case x :: tail => x + " ..."  //list with x as the head
        case _ => "something else"    //default case
      }
      println(result)
    }
    println()
    println()

    //Matching Tuples.
    //Tuples are immutable. Tuples are basically several items grouped together.
    //Tuples are denoted by ()
    for (pair <- Array((0,1),(1,0),(1,1))) {
      //matching is the expression, which produces a value
      val result = pair match {
        case (0,_) => "0."      //first item is 0
        case (y,0) => y + " 0"  //second item is 0
        case _ => "neither is 0"
      }
      println(result)
    }
    println()
    println()

    /* Pattern matching with regular expression
     *
     * A bunch of digits, one white space, and a bunch of letters. */
    val pattern = "([0-9]+) ([a-z]+)".r
    val res1 = "99 bottles" match {
      case pattern(num, item) => (num.toInt, item)
    }
    println("res1: "+ res1 + "\n")
    //res1: (99,bottles)

    /* 14.7 Patterns in Variable Declaration
     *
     * You can use these patterns inside variable declarations. */
    val (x,y) = (1,2)
    println("x: " + x)
    println("y: " + y)
    println()
    //x: 1
    //y: 2

    //tuples are very useful for functions that return a pair
    val (q,r) = BigInt(10) /% 3
    println("q: " + q)
    println("r: " + r)
    //q: 3
    //r: 1

    //same syntaxworks for any patterns with variable names
    val arr1 = Array(1, 7, 2, 9, 10)
    //_* represents the rest of the array
    val Array(first, second, _*) = arr1
    println("first: "+ first)
    println("second: "+ second)
    println()
    println()
    //first: 1
    //second: 2

    /* 14.8 Patterns in for expressions
     *
     * I want to convert java properties objects as a Scala map. */
    import scala.collection.JavaConversions.propertiesAsScalaMap
    //k is the key and v is the value for each item in the map
    for ((k,v) <- System.getProperties()) {
      println(k +" -> "+ v)
    }
    println()
    println()
    //print all keys with empty value, skipping over all the others
    for ((k,"") <- System.getProperties())
      println(k)

    /* 14.9 Case Classes
     *
     * A case class is a special kind of class that is optimixed for use in pattern matching.
     * Why we use case classes: case classes are useful for functional decomposition with pattern matching.
     *
     * Given an object of Amount (superclass), Amount has 3 subclasses: Dollar, Currency, and Nothing.
     * You need to answer 2 questions:
     * 1) which subclass was used (what kind of constructor was used to build subclass? whether its
     * Dollar, Currency, or Nothing)
     * 2) what were tge arguments of the constructors or what is the value of its instance variable?
     *
     * This situation is so common that many functional languages automate it. Technical term
     * for this situation is called pattern matching. */

    //abstract class does not contain anything. It is ideally case for superclass
    abstract class Amount

    //Dollar has one immutable field called value
    case class Dollar(value: Double) extends Amount

    //Currency has two immutable fields: value, unit
    case class Currency(value: Double, unit: String) extends Amount

    case object Nothing extends Amount

    for (amt <- Array(Dollar(10000.0), Currency(1000.0, "EUR"), Nothing))
    {
      val result = amt match {
        //v is value to build the object
        case Dollar(v) => "$" + v
        case Currency(_,u) => "Oh noes, I got " + u
        case Nothing => ""
      }
      println(amt + ": " + result)
    }
    //Dollar(1000.0): $1000.0
    //Currency(1000.0, EUR): Oh noes, I got EUR
    //Nothing:

    //example of immutable list operation

    //how to reverse a list:
    //T is a type parameter just like javas generic type
    //type parameter makes functional code more flexible
    def reverse[T](xs: List[T]): List[T] =
      xs match {
        case Nil => xs  //empty list
        case y :: ys => reverse(ys) ++ List(y)
        //y is the head and ys is the tail
      }
    val res3 = List(1, 2, 3)
    val res4 = reverse(res3)
    println("res4: "+ res4)
    //res4: List(3, 2, 1)

    //Remove n'th element of the list
    def removeAt[T](xs: List[T], n: Int): List[T] = {
      //we take first n and drop first n+1 element
      //combine results together
      (xs take n) ++ (xs drop n+1)
    }

    val res5 = List(4,10,35,21,11,34,45)
    val res5a = res5.take(2)
    println("res5a: "+ res5a)
    //res5a: List(4, 10)

    val res5b = res5.drop(3)
    println("res5b: "+ res5b)
    //res5b: List(21, 11, 34, 45)

    //combine 2 lists
    val res5c = res5a ++ res5b
    println("res5c: "+ res5c)
    //res5c: List(4, 10, 21, 11, 34, 45)

    //remove element at index 2
    val res5d = removeAt(res5, 2)
    println("res5d: "+ res5d)
    //res5d: List(4, 10, 21, 11, 34, 45)

    //higher order list function

    //multiple each element of list by the same factor
    def scaleList(xs: List[Double], factor: Double): List[Double] = {
      xs map (x => x * factor)
    }

    //you want to square each element of the list
    def squareList(xs: List[Int]): List[Int] = {
      xs map (x => x*x)
    }

    //select all elements of a list that are positive
    //each tool is specialized for each task
    def posElements(xs: List[Int]): List[Int] = {
      xs filter (x => x > 0)
    }
    //each tool in functional programming is specialized for each task
    val nums = List(7, 3, -2, -4, 5, 7, 1)

    //same as (xs filter p, xs filterNot p)
    //separate list into positive list and negative list
    val res6 = nums partition(x => x > 0)
    println("res6: "+ res6)
    //res6: (List(7, 3, 5, 7, 1), List(-2, -4))

    //same as (xs takeWhile p, xs dropWhile p)
    //all my prefixes have to satisfy this condition
    val res7 = nums span (x => x>0)
    println("res7: "+ res7)
    //res7: (List(7, 3), List(-2, -4, 5, 7, 1))

    /* Write a function that packs consecutive duplicates of the list elements into sublists. */
    def pack[T](xs: List[T]): List[List[T]] = {
      xs match {
        case Nil => Nil
        case x :: xs1 =>
          //first is the consecutive letters at the front
          val (first, rest) = xs span (y => y == x)
          //recursive call to find duplicates in the rest
          first :: pack(rest)
      }
    } //end of pack

    val res8 = List("a", "a", "a", "b", "c", "c", "a")
    val res9 = pack(res8)
    println("res9: "+ res9)
    //res9: List(List(a, a, a), List(b), List(c, c), List(a))

    /* Using pack, write a function that produces a run length encoding of list.
     * Run length encoding is used in compression of images and other files. */
    //I get the letter and its length
    /*def encode[T](xs: List[T]): List[(T, Int)] = {
      pack(xs) map (ys -> (ys.head, ys.length))
    }*/

    /* Arrays and Strings
     *
     * Arrays and Strings can be implicitly converted to a sequence where needed. As a result,
     * Arrays and Strings can support the same operations as Seq. They cant be subclasses of
     * Seq because they come from the Java world. */
    val xs2 = Array(1, 2, 3, 44)
    //double each element of array
    val res13 = xs2 map (x => x*2)
    val s2 = "Hello World"
    //give us all upper case character in String
    val res14 = s2 filter (c => c.isUpper)
    println("res14: "+ res14)
    //res14: HW

    //whether I have an uppercase letter
    val res15 = s2 exists (c => c.isUpper)
    println("res15: "+ res15)

    //whether all letters are upper case
    val res16 = s2 forall (c => c.isUpper)
    println("res16: "+ res16)

    //zip list and string
    val pairs1 = List(1, 2, 3) zip s2
    println("pairs1: "+ pairs1)
    //pairs1: List((1,h), (2,e), (3,l))

    val res16a = List("Hello", "World")
    val pairs2 = List(1, 2, 3) zip res16a
    println("pairs2: "+ pairs2)
    //pairs2: List((1,Hello), (2,World))

    val res17 = pairs1.unzip
    println("res17: "+ res17)
    //res17: (List(1, 2, 3), List(H, e, l))

    //xs flatMap f   f is collection-valued function
    //applied f to each element of xs and concatenates the results
    val res18 = s2 flatMap(c => List('.',c))
    println("res18: "+ res18)
    //res18: .H.e.l.l.o. .W.o.r.l.d

    //compute scalar product of vector
    def scalsrProduct(xs: Vector[Double], ys: Vector[Double]): Double = {
      //after zip, each element is a tuple
      (xs zip ys).map(xy => xy._1 * xy._2).sum
    }

    //better notation
    def scalarProduct_2(xs: Vector[Double], ys: Vector[Double]): Double = {
      (xs zip ys).map {case (x,y) => x*y}.sum
    }
    //list all combinations of numbers x and y where x is drawn from 1 .. M
    //and y is drawn from 1 .. N
    val M = 8
    val N = 2

    val res19 =
      for {
        x <- 1 to M
        y <- 1 to N //second generator moves faster than first generator
      } yield (x,y)

    println("res19: "+ res19)
    //res19: Vector((1,1), (1,2), (2,1), (2,2), (3,1), (3,2), (4,1),
    // (4,2), (5,1), (5,2), (6,1), (6,2), (7,1), (7,2), (8,1), (8,2))

    /* A number n is prime if the only divisors of n are 1 and n itself.
     * Functional programming can faithfully translate your math idea into code literally. */
    def isPrime(n: Int): Boolean = {
      //check the number from 2 to n-1 one by one to see if they are divisible by d
      (2 until n) forall (d => n%d != 0)
    }
  }
}
