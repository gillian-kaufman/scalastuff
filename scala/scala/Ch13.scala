import scala.collection.mutable.ArrayBuffer

/* In chapter 13, we will introduce the powerful collection library and discuss how to apply the functional
 * style of programming to the collection.
 *
 * The root of collection library is Iterable, meaning that you can iterate through your collection.
 * For iterable, there are three subclasses: seq, set, and map.
 *
 * Seq is an ordered sequence of values such as an array, a list, or a vector. In other words, for sequence,
 * the order matters. IndexedSeq is a subclass of Seq. IndexedSeq allows fast random access through an int index.
 *
 * Set is an unordered collection of values. Order does not matter. Each element of a set has to be unique,
 * whereas sequence allows for duplicates.
 *
 * Map is a set of (key, value) pairs. Each key of the map must be unique. */

object Ch13
{
  def main(args: Array[String]): Unit = {
    val coll1 = Array(1, 7, 2, 9)
    //Iterable actually stems from imperative style of programming
    val iter = coll1.iterator

    //This loop goes through each element, one by one, which is imperative style
    while (iter.hasNext)
      print(iter.next() + " ")
    println()
    //1 7 2 9

    /* Universal Creating Principle for collection library
     *
     * Each Scala collection class has a companion object with an apply method for
     * constructing an instance of the collection. */

    import java.awt.Color

    val res0 = Set(Color.RED, Color.GREEN, Color.BLACK)
    val res1 = Vector(Color.RED, Color.GREEN, Color.BLACK)

    //ArrayBuffer is array whose size can be changed
    val res2 = ArrayBuffer(Color.RED, Color.GREEN, Color.BLACK)

    /* 13.2 Mutable and Immutable Collections
     *
     * Scala supports both mutable and immutable collections.
     * An immutable collection can never change or be updated. The advantage of immutable collection
     * is that you can safely share a reference to it, even in a multi-threaded program.
     * Scala gives a preference to immutable collection. */

    //immutable map
    val res3 = Map("Hello" -> 42)

    //this is equivalent syntax
    //Here, Map is the companion object of the Map class. apply is usually used to create
    //instances of the class
    val res4 = Map.apply("Hello" -> 42)

    /* If you want to create new key value pairs, like res4.put("Fred", 29), it is impossible because
     * immutable data structures cant be updated.
     * mutable structures are convenient to program and can be updated,
     * however it is a nightmare for multi-threads. */

    //mutable map
    import scala.collection.mutable
    //empty mutable map
    val res5 = new mutable.HashMap[String, Int]
    //create mutable map with one key value pair
    val res5a = mutable.HashMap("Fred" -> 29)
    //Mutable maps can use put() to update the maps
    res5.put("Fred", 29)
    res5.put("Jacky", 21)
    println("res5: " + res5)
    //res5: Map(Fred -> 29, Jacky -> 21)

    /* If you had no prior experience with immutable collections, you may wonder how
     * you can do useful work with them.
     *
     * The key is that you can create new collections out of old ones.
     * This is particularly natural in recursive computations. For example,
     * here we compute the set of all digits of an integer. */

    //Set here is the immutable version
    //for each recursive call, I never touch the old set. I only create a new set for updating.
    def digits(n: Int): Set[Int] = {
      if (n<0) digits(-n)
      //this is the base case where n is a single digit
      else if (n < 10) Set(n)
      else digits(n/10) + (n%10)
    }
    val res5b = digits(553322211)
    println("res5b: " + res5b)
    //res5b: Set(5, 3, 2, 1)

    /* Vector is an immutable indexed sequence. List is an immutable non-indexed sequence.
     * ArrayBuffer is a mutable indexed sequence. These are the three main data structures
     * for sequence. */
    val vec1 = (1 to 10000000) map (_ % 100)
    //convert vector to list
    val list1 = vec1.toList

    /* run block, measure the running time, and return the results.
     * T is called the type parameter, which is similar to java's generic type
     * the type parameter makes your code more flexible since T can be any objects
     *
     * block is call by name parameter since you want to delay evaluation on demand. */
    def time[T](block: => T): T = {
      val start = System.nanoTime()
      //running the block
      val result = block

      //running time to execute block
      val elapsed = System.nanoTime() - start
      println(elapsed + " nanoseconds ")
      result
    }

    //Comparing running time for indexedseq and non indexedseq
    //I access the element of vector at the position of 800,000
    //Since list is a recursive structure, you have to traverse one by one to get to the end
    //Vector is a shallow tree, you just need a few hops to access any element
    //This is why vector is more efficient than list.
    val r5 = time(vec1(800000))
    val r6 = time(list1(800000))

    //174501 nanoseconds (vec1)
    //196495446 nanoseconds (list1)

    //13.4 Lists, which are immutable data structures
    val r7 = List(4, 2)
    //equivalent representation. Nil means end of list
    val r7a = 4 :: 2 :: Nil
    val r8 = 9 :: r7  //add an element to the front of the list
    //List is the fundamental functional data structure
    println("r8: " + r8)
    //r8: List(9, 4, 2)
    /* In Java or C++, one uses an iterator to traverse a linked list, but it is often more
     * natural to use recursion since list is a recursive data structure.
     * For example, the following function computes the sum of all elements in a list of integers. */
    def sum1(lst: List[Int]): Int = {
      if (lst == Nil) 0
      else lst.head + sum1(lst.tail)
    }
    val r11 = sum1(List(9, 4, 2))
    println("r11: " + r11)
    //r11: 15

    /* A better way to process the list is to use pattern matching. Pattern matching is a
     * very powerful mechanism in functional programming.
     *
     * The :: operator in the second pattern is used to destructure the list into head
     * and tail. lst is the target, h :: t is the pattern. */
    def sum2(lst: List[Int]): Int = {
      lst match
      {
        case Nil => 0
        case h :: t => h + sum2(t)
      }
    }
    val r12 = sum2(List(9, 4, 2))
    println("r12: " + r12)
    //r12: 15

    /* 13.6 Set
     * A set is a collection of distinct elements. Set by default is immutable.
     * You can create a new set and original Set(2, 0, 1) is untouched. */
    val r15 = Set(2, 0, 1) + 4

    /* Unlike lists, sets do not retain the order in which elements are inserted.
     * By default, sets are implemented as hash sets in which elements are
     * organized by the value of the hashcode method. */
    val r16 = Set(1, 2, 3, 4, 5, 6)
    for(i <- r16) print(i + " ")
    println()
    //5 1 6 2 3 4

    //Linked hashsets remember the order in which elements were inserted.
    //It keeps a linked list for this purpose.

    val weekdays = scala.collection.mutable.LinkedHashSet("Mo", "Tu", "We", "Th", "Fr")
    for(i <- weekdays) print(i + " ")
    println()
    //Mo Tu We Th Fr

    //several useful set operations
    val digit3 = Set(1, 7, 2, 9)

    //check if digit3 set has 0
    val r17 = digit3 contains 0
    println("r17: " + r17)
    //r17: false

    //create second set
    val primes = Set(2, 3, 5, 7)
    //union operation
    val r18 = digit3 ++ primes
    println("r18: " + r18)
    //r18: Set(5, 1, 9, 2, 7, 3)

    //intersect operation
    val r19 = digit3 & primes
    println("r19: " + r19)
    //r19: Set(7, 2)

    //difference operation
    val r20 = digit3 -- primes
    println("r20: " + r20)
    //r20: Set(1, 9)

    /* 13.7 operator for adding or removing elements
     *
     * Vector is immutable.
     * +: will add an element to the beginning of an ordered collection. */
    val r21 = 1 +: Vector(1, 2, 3)
    println("r21: " + r21)
    //r21: Vector(1, 1, 2, 3)

    //:+ adds an element to the end of an ordered collection
    val r22 = Vector(1, 2, 3) :+ 5
    println("r22: " + r22)
    //r22: Vector(1, 2, 3, 5)

    //The beauty of immutable structures is that they can share anything without concerns.
    //immutable structure doesnt mean they are not efficient.

    //mutable collections have += operator that mutates the left side
    import scala.collection.mutable.ArrayBuffer
    //numbers is the immutable reference
    //ArrayBuffer is a mutable object.
    val numbers = ArrayBuffer(1, 2, 3)
    numbers += 5
    println("numbers: "+numbers)
    //numbers: ArrayBuffer(1, 2, 3, 5)

    //for immutable collection, you can use += like this
    //r23 is mutable, set object is immutable.
    var r23 = Set(1, 2, 3)
    r23 += 5

    //similarly -- operator removes multiple elements
    val r25 = numbers -- Vector(1, 2, 7, 9)
    println("r25: "+r25)
    println("numbers: "+numbers)
    //r25: ArrayBuffer(3, 5)
    //val r25a = Vector(1, 2, 7, 9) -- numbers

    /* 13.8 Common methods for iterable trait.
     * You can think about a trait like an interface. */
    val coll = Range(1, 10)
    println("coll: "+coll)
    //coll: Range(1, 2, 3, 4, 5, 6, 7, 8, 9)

    //head: get first element
    val r26 = coll.head
    println("r26: "+r26)
    //r26: 1

    //get the last element
    val r27 = coll.last
    println("r27: "+r27)
    //r27: 9

    /* headOption is used in case the collection is empty.
     * This way you can avoid nasty nullpointer exceptions
     * Option has 2 subclasses: None and Some(result). None means computation failed.
     * Some(result) will wrap the result into Some object. */

    //If you have Nil for return value. you will pay a big price
    val r28 = coll.headOption

    //if r28 contains no results, the default value -1 will be returned
    //In order to get the computation results back, the user is forced to deal with an error
    //The user has to consider how to recover if the program fails
    //This provides a very clean universal solution for Nil
    //This is the functional style answer to Nil
    val r28r = r28.getOrElse(-1)
    println("r28r: "+r28r)
    //r28r: 1

    //Count how many even numbers you have
    //declarative API: what I want to do vs. Java: How to do it.
    //declarative API is concise,, expressive, functional
    val r29 = coll.count(_ % 2 == 0)
    //count how many negative numbers in the collection
    val r29a = coll.count(_ < 0)

    //whether all elements are even
    val r30 = coll.forall(_ % 2 == 0)
    println("r30: "+r30)
    //r30: false

    //whether at least one even number exists in the collection
    val r31 = coll.exists(_ % 2 == 0)

    //this is an advantage of functional style programming since it brings so many convenient
    //little toolboxes for you vs Java where you have one big hammer called "for loop"

    //return all even numbers
    val r32 = coll.filter(_ % 2 == 0)
    //return all odd numbers
    val r32a = coll.filterNot(_%2 == 0)

    //partition = (filter, filterNot)
    //returns the vector with even numbers and the vector with odd numbers
    val r33 = coll.partition(_%2 == 0)
    println("r33: "+r33)
    //r33: (Vector(2, 4, 6, 8), Vector(1, 3, 5, 7, 9))

    //takeWhile returns the prefix satisfying predicate
    //return the number less than 3 in the prefix
    val r34 = coll.takeWhile(_ < 3)
    println("r34: "+r34)
    //r34: Range(1, 2)

    //drop prefix which is less then 3
    val r35 = coll.dropWhile(_ < 3)
    println("r35: "+r35)
    //r35: Range(3, 4, 5, 6, 7, 8, 9)

    def fun1(x: Int): Boolean = {
      (x < 3) && (x > 5)
    }

    //take the prefix of numbers satisfying the condition defined by fun1
    val r35a = coll.takeWhile(x => fun1(x))
    println("r35a: "+r35a)
    //r35a: Range()

    //span produce the results based on takeWhile and dropWhile
    //span = (takeWhile, dropWhile)
    val r36 = coll.span(_<3)
    println("r36: "+r36)
    //r36: (Range(1, 2), Range(3, 4, 5, 6, 7, 8, 9))

    //take first five numbers
    val r37 = coll.take(5)
    println("r37: "+r37)
    //r37: Range(1, 2, 3, 4, 5)

    //this API works for all iterables, including vector, string, array, list, and arraybuffer
    //drop first 4 numbers
    val r38 = coll.drop(4)
    println("r38: "+r38)
    //r38: Range(5, 6, 7, 8, 9)

    //take last 4 numbers
    val r38a = coll.takeRight(4)

    //splitAt = (take, drop)
    val r39 = coll.splitAt(4)
    println("r39: "+r39)
    //r39: (Range(1, 2, 3, 4), Range(5, 6, 7, 8, 9))

    //select the interval of numbers
    val r40 = coll.slice(2, 8)
    println("r40: "+r40)
    //r40: Vector(3, 4, 5, 6, 7, 8)
    /* Note: slice excludes 2 and includes 8 */

    val a = Vector(1, 2, 3, 4, 5, 6, 7, 8, 9)
    val r40a = a.slice(2, 8)
    println("r40a: "+r40a)
    //r40a: Vector(3, 4, 5, 6, 7, 8)

    val b = Array(1, 2, 3, 4, 5, 6, 7, 8, 9)
    val r40b = b.slice(2, 8)
    println("r40b: "+r40b.mkString(","))
    //r40a: Vector(3, 4, 5, 6, 7, 8)
    println()

    //partition the collection in fixed size
    val r41 = coll.grouped(3).toArray
    for(i <- r41)
      println(i.mkString(" "))
    println()

    //1 2 3
    //4 5 6
    //7 8 9

    //group the elements in a fixed size by passing the sliding window
    val r42 = coll.sliding(3).toArray
    for(i <- r42)
      println(i.mkString(" "))
    println()

    //1 2 3
    //2 3 4
    //3 4 5
    //4 5 6
    //5 6 7
    //6 7 8
    //7 8 9

    //split the string based on spaces
    val words = "Mary had a little lamb".split(" ")
    //reverse the array
    val r43 = words.reverse
    println(r43.mkString(" "))
    //lamb little a had Mary

    //sort the array in increasing order of string length
    val r44 = words.sortBy(_.length)
    println(r44.mkString(" "))
    //output: a had Mary lamb little

    //sort array in decreasing order of string length
    val r45 = words.sortWith(_.length > _.length)
    println(r45.mkString(" "))
    //output: little lamb Mary had a

    /* 13.9 Mapping function (higher order function)
     * higher order function means that you can take the function as a parameter or
     * return the function. In other words, the function is a first class citizen. */

    //by default scala prefers immutable data structures.
    val names = List("Peter", "Paul", "Mary")

    //convert each element to the upper case
    //you may want to transform all elements of a collection without looping
    val r46 = names.map(_.toUpperCase)
    println(r46.mkString(" "))
    println()
    //output: PETER PAUL MARY

    def ulcases(s: String) = Vector(s.toUpperCase, s.toLowerCase)

    //map each element to vector of uppercase and lowercase
    val r47 = names.map(ulcases)
    println("r47: " + r47)
    //r47: List(Vector(PETER, peter), Vector(PAUL, paul), Vector(MARY, mary))

    /* If the function yields a collection such as ulcases, you may want to
     * concatenate all results. After concatenation, results are easier to manipulate.
     * ulcases is called a collection valve function. */

    val r48 = names.flatMap(ulcases)
    println("r48: " + r48)
    //r47: List(PETER, peter, PAUL, paul, MARY, mary)

    /* 13.10 reducing and folding
     *
     * reducing and folding want to combine elements with a binary function.
     * Binary function is the function taking 2 parameters
     *
     * reduce doesnt have an initial value. Fold does start with an initial value. */
    val lst = List(1, 7, 2, 9)

    //applies a binary operator to all elements of collection going left to right
    val r49 = lst.reduceLeft(_-_)
    println("r49: " + r49)
    //r49: -17

    //applies a binary operator to all elements of collection going right to left
    val r50 = lst.reduceRight(_-_)
    println("r50: " + r50)

    //val r51 = lst.foldLeft(0)(_-_)
    //println("r51: " + r51)

    //val r52 = lst.foldRight(0)(_-_)
    //println("r52: " + r52)

    //13.11 Zipping
    //Zipping means that you want to combine corresponding elements
    val prices = List(5.0, 20.0, 9.95)
    val quantities = List(10, 2, 1)

    //zip produces list of tuples
    val r52 = prices zip quantities
    println("r52: " + r52)
    //r52: List((5.0, 10), (20.0, 2), (9.95, 1))

    //each element is the total value of that product
    val r53 = (r52 map {p => p._1 * p._2})
    println("r53: " + r53)
    //r53: List(50.0, 40.0, 9.95)

    val r54 = r53.sum

    //you can combine previous two steps
    val r54a = (r52 map {p => p._1 * p._2}).sum

    //equivalent and better notation
    //total value of your products
    val r54c = (r52 map {case (price, amount) => price * amount}).sum
  }
}
