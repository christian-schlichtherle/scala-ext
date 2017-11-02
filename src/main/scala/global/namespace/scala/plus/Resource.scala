/*
 * Copyright © 2017 Schlichtherle IT Services
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package global.namespace.scala.plus

import global.namespace.scala.plus.OnTryFinally._
import global.namespace.scala.plus.Resource.Loan

/** A mix-in trait which provides resource functions.
  *
  * @author Christian Schlichtherle
  */
trait Resource {

  /** Starts a `loan(resource) to { ... }` statement.
    *
    * Usage example:
    * {{{
    * loan(new PrintWriter(new FileOutputStream("hello-world.txt"))) to { w: PrintWriter => w.println("Hello world!") }
    * }}}
    * In this example, `w.close()` is guaranteed to get called even if the to-function terminates with a [[Throwable]].
    *
    * In general, if the to-function throws a throwable `t1` and the `AutoCloseable.close()` method throws another
    * throwable `t2`, then the throwable `t2` gets added to the throwable `t1` using `t1.addSuppressed(t2)`.
    *
    * @param resource the auto-closeable resource.
    * @tparam A the type of the auto-closeable resource.
    * @see <a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-14.html#jls-14.20.3.1">The Java Language Specification: Java SE 7 Edition: 14.20.3.1 Basic try-with-resources</a>
    */
  def loan[A <: AutoCloseable](resource: => A): Loan[A] = new Loan(resource)
}

/** An object which provides resource functions.
  *
  * @author Christian Schlichtherle
  */
object Resource extends Resource {

  /** Encapsulates a `loan(resource) to { ... }` statement.
    *
    * Usage example:
    * {{{
    * loan(new PrintWriter(new FileOutputStream("hello-world.txt"))) to { w: PrintWriter => w.println("Hello world!") }
    * }}}
    * In this example, `w.close()` is guaranteed to get called even if the to-function terminates with a [[Throwable]].
    *
    * In general, if the to-function throws a throwable `t1` and the `AutoCloseable.close()` method throws another
    * throwable `t2`, then the throwable `t2` gets added to the throwable `t1` using `t1.addSuppressed(t2)`.
    *
    * @param resource the auto-closeable resource.
    * @tparam A the type of the auto-closeable resource.
    * @see <a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-14.html#jls-14.20.3.1">The Java Language Specification: Java SE 7 Edition: 14.20.3.1 Basic try-with-resources</a>
    */
  class Loan[A <: AutoCloseable](resource: => A) {

    /** Applies the loan pattern to the given function.
      * This is a straightforward translation of Java's
      * <a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-14.html#jls-14.20.3.1">basic <code>try</code>-with-resources</a>
      * statement.
      *
      * @param fun the function with the nullable resource parameter.
      */
    final def to[B](fun: A => B): B = {
      val r = resource
      onTry {
        fun(r)
      } onFinally {
        if (null != r) {
          r close ()
        }
      }
    }
  }
}
