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
import org.scalatest.Matchers._
import org.scalatest.WordSpec

/** @author Christian Schlichtherle */
class OnTryFinallySpec extends WordSpec {

  "The `onTry { ... } onFinally { ... }` statement" when {
    "not throwing a Throwable" should {
      "execute both code blocks" in {
        var list = List.empty[Int]
        onTry {
          list ::= 1
          list
        } onFinally {
          list ::= 2
          list
        } shouldBe List(1)
        list shouldBe List(2, 1)
      }
    }

    "throwing a Throwable in the first code block" should {
      "execute both code blocks" in {
        var list = List.empty[Int]
        val t = new Throwable
        intercept[Throwable] {
          onTry {
            list ::= 1
            throw t
          } onFinally {
            list ::= 2
          }
        } should be theSameInstanceAs t
        t.getSuppressed shouldBe Array()
        list shouldBe List(2, 1)
      }
    }

    "throwing a Throwable in the second code block" should {
      "execute both code blocks" in {
        var list = List.empty[Int]
        val t = new Throwable
        intercept[Throwable] {
          onTry {
            list ::= 1
          } onFinally {
            list ::= 2
            throw t
          }
        } should be theSameInstanceAs t
        t.getSuppressed shouldBe Array()
        list shouldBe List(2, 1)
      }
    }

    "throwing a Throwable in both code blocks" should {
      "chain the Throwables via `Throwable.addSuppressed(Throwable)`" in {
        var list = List.empty[Int]
        val t1 = new Throwable
        val t2 = new Throwable
        intercept[Throwable] {
          onTry {
            list ::= 1
            throw t1
          } onFinally {
            list ::= 2
            throw t2
          }
        } should be theSameInstanceAs t1
        t1.getSuppressed shouldBe Array(t2)
        list shouldBe List(2, 1)
      }
    }
  }

  "The `onTry { ... } onThrowable { ... }` statement" when {
    "not throwing a Throwable" should {
      "execute only the first code block" in {
        var list = List.empty[Int]
        onTry {
          list ::= 1
          list
        } onThrowable {
          list ::= 2
          list
        } shouldBe List(1)
        list shouldBe List(1)
      }
    }

    "throwing a Throwable in the first code block" should {
      "execute both code blocks" in {
        var list = List.empty[Int]
        val t = new Throwable
        intercept[Throwable] {
          onTry {
            list ::= 1
            throw t
          } onThrowable {
            list ::= 2
          }
        } should be theSameInstanceAs t
        t.getSuppressed shouldBe Array()
        list shouldBe List(2, 1)
      }
    }

    "throwing a Throwable in both code blocks" should {
      "chain the Throwables via `Throwable.addSuppressed(Throwable)`" in {
        var list = List.empty[Int]
        val t1 = new Throwable
        val t2 = new Throwable
        intercept[Throwable] {
          onTry {
            list ::= 1
            throw t1
          } onThrowable {
            list ::= 2
            throw t2
          }
        } should be theSameInstanceAs t1
        t1.getSuppressed shouldBe Array(t2)
        list shouldBe List(2, 1)
      }
    }
  }
}
