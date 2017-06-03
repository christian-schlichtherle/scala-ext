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
import global.namespace.scala.plus.ResourceLoan.LoanStatement

/** A mix-in trait which provides Java's basic "try-with-resources" statement.
  *
  * Usage example:
  * {{{
  * import net.java.truecommons3.shed.ResourceLoan_
  * val out: OutputStream = ...
  * loan(new PrintWriter(out)) to { w: PrintWriter => w.println("Hello world!") }
  * }}}
  * In this example, `w.close()` is guaranteed to get called even if the
  * to-function terminates with a [[Throwable]].
  *
  * In general, if the to-function throws a throwable `t1` and the
  * `AutoCloseable.close()` method throws another throwable `t2`, then the
  * throwable `t2` gets added to the throwable `t1` using
  * `t1.addSuppressed(t2)`.
  *
  * @see <a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-14.html#jls-14.20.3.1">The Java Language Specification: Java SE 7 Edition: 14.20.3.1 Basic try-with-resources</a>
  * @author Christian Schlichtherle
  */
trait ResourceLoan {

  /** Starts a `loan(resource) to { ... }` statement.
   *
   * @param resource the autocloseable resource.
   * @tparam A the type of the autocloseable resource.
   */
  def loan[A <: AutoCloseable](resource: A): LoanStatement[A] = new LoanStatement(resource)
}

/** An object which provides Java's basic "try-with-resources" statement.
  *
  * Usage example:
  * {{{
  * import net.java.truecommons3.shed.ResourceLoan_
  * val out: OutputStream = ...
  * loan(new PrintWriter(out)) to { w: PrintWriter => w.println("Hello world!") }
  * }}}
  * In this example, `w.close()` is guaranteed to get called even if the
  * to-function terminates with a [[Throwable]].
  *
  * In general, if the to-function throws a throwable `t1` and the
  * `AutoCloseable.close()` method throws another throwable `t2`, then the
  * throwable `t2` gets added to the throwable `t1` using
  * `t1.addSuppressed(t2)`.
  *
  * @see <a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-14.html#jls-14.20.3.1">The Java Language Specification: Java SE 7 Edition: 14.20.3.1 Basic try-with-resources</a>
  * @author Christian Schlichtherle
  */
object ResourceLoan extends ResourceLoan {

  class LoanStatement[A <: AutoCloseable](resource: A) {

    /** Applies the loan pattern to the given function.
      * This is a straightforward translation of Java's
      * <a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-14.html#jls-14.20.3.1">basic <code>try</code>-with-resources</a>
      * statement.
      *
      * @param fun the function with the nullable resource parameter.
      */
    final def to[B](fun: A => B): B = {
      onTry {
        fun(resource)
      } onFinally {
        if (null != resource) {
          resource close()
        }
      }
    }
  }
}
