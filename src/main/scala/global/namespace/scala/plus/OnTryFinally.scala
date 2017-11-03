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

import global.namespace.scala.plus.OnTryFinally.OnTryStatement

import scala.util.control.NonFatal

/** A mix-in trait which provides an `onTry { ... } onFinally { ... }` and
  * `onTry { ... } onThrowable { ... }` statement.
  * These statements chain exceptions from both code blocks via
  * `Throwable.addSuppressed(Throwable)`.
  *
  * @author Christian Schlichtherle
  */
trait OnTryFinally {

  /** Starts an `onTry { tryBlock } onFinally { ... }` or
    * `onTry { tryBlock } onThrowable { ... }` statement.
    *
    * @param tryBlock the code block to execute first.
    * @tparam A the return type of the code block.
    */
  def onTry[A](tryBlock: => A): OnTryStatement[A] = new OnTryStatement(tryBlock)
}

/** An object which provides an `onTry { ... } onFinally { ... }` and
  * `onTry { ... } onThrowable { ... }` statement.
  * These statements chain exceptions from both code blocks via
  * `Throwable.addSuppressed(Throwable)`.
  *
  * @author Christian Schlichtherle
  */
object OnTryFinally extends OnTryFinally {

  class OnTryStatement[A](tryBlock: => A) {

    /** Executes an `onTry { tryBlock } onFinally { finallyBlock }` statement.
      *
      * This statement has the same semantics like the built-in `try { tryBlock } finally { finallyBlock }` statement
      * except that if the `finallyBlock` throws a non-fatal [[Throwable]] `t2`, then this throwable gets added to the
      * throwable `t1` from the `tryBlock` using `t1.addSuppressed(t2)`.
      *
      * @param finallyBlock the code block to unconditionally execute after the `tryBlock`.
      * @return the return value of the `tryBlock`.
      */
    final def onFinally(finallyBlock: => Any): A = {
      var t1: Throwable = null
      try {
        tryBlock
      } catch {
        case t: Throwable =>
          t1 = t
          throw t
      } finally {
        try {
          finallyBlock
        } catch {
          case NonFatal(t2) =>
            if (null == t1) {
              throw t2
            }
            t1 addSuppressed t2
        }
      }
    }

    /** Executes an `onTry { tryBlock } onThrowable { throwBlock }` statement.
      *
      * This statement executes the `throwBlock` if and only if the `tryBlock` throws a [[Throwable]].
      * If the `throwBlock` throws a non-fatal [[Throwable]] `t2`, then this throwable gets added to the throwable `t1`
      * from the `tryBlock` using `t1.addSuppressed(t2)`.
      *
      * @param throwBlock the code block to execute if and only if the `tryBlock` throws a [[Throwable]].
      * @return the return value of the `tryBlock`.
      */
    final def onThrowable(throwBlock: => Any): A = {
      try {
        tryBlock
      } catch {
        case t1: Throwable =>
          try {
            throwBlock
          } catch {
            case NonFatal(t2) => t1 addSuppressed t2
          }
          throw t1
      }
    }

    /** Executes an `onTry { tryBlock } onNonFatal { throwBlock }` statement.
      *
      * This statement executes the `throwBlock` if and only if the `tryBlock` throws a non-fatal [[Throwable]].
      * If the `throwBlock` throws a non-fatal [[Throwable]] `t2`, then this throwable gets added to the throwable `t1`
      * from the `tryBlock` using `t1.addSuppressed(t2)`.
      *
      * @param throwBlock the code block to execute if and only if the `tryBlock` throws a non-fatal [[Throwable]].
      * @return the return value of the `tryBlock`.
      */
    final def onNonFatal(throwBlock: => Any): A = {
      try {
        tryBlock
      } catch {
        case NonFatal(t1) =>
          try {
            throwBlock
          } catch {
            case NonFatal(t2) =>
              t1 addSuppressed t2
          }
          throw t1
      }
    }
  }
}
