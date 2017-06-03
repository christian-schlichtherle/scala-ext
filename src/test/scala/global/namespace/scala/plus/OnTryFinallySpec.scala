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
import org.scalatest.prop.PropertyChecks._

import scala.util.control.ControlThrowable

/** @author Christian Schlichtherle */
class OnTryFinallySpec extends WordSpec {

  private def fatalThrowables = {
    Table(
      "throwable",
      () => new VirtualMachineError { },
      () => new ThreadDeath,
      () => new InterruptedException,
      () => new LinkageError,
      () => new ControlThrowable { }
    )
  }

  private def nonFatalThrowables = {
    Table(
      "throwable",
      () => new Throwable
    )
  }

  private def anyThrowables = {
    Table(
      "throwable",
      // Fatal throwables:
      () => new VirtualMachineError { },
      () => new ThreadDeath,
      () => new InterruptedException,
      () => new LinkageError,
      () => new ControlThrowable { },
      // Non-fatal Throwables:
      () => new Throwable
    )
  }

  "The `onTry { ... } onFinally { ... }` statement" when {
    "not throwing any exception" should {
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

    "throwing any `Throwable` in the first code block" should {
      "execute both code blocks" in {
        forAll(anyThrowables) { tf =>
          val t = tf()
          var list = List.empty[Int]
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
    }

    "throwing any `Throwable` in the second code block" should {
      "execute both code blocks" in {
        forAll(anyThrowables) { tf =>
          val t = tf()
          var list = List.empty[Int]
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
    }

    "throwing any `Throwable` in the first code block and a fatal `Throwable` in the second code block" should {
      "execute both code blocks and throw only the exception from the second code block" in {
        forAll(anyThrowables) { tf1 =>
          forAll(fatalThrowables) { tf2 =>
            val t1 = tf1()
            val t2 = tf2()
            var list = List.empty[Int]
            intercept[Throwable] {
              onTry {
                list ::= 1
                throw t1
              } onFinally {
                list ::= 2
                throw t2
              }
            } should be theSameInstanceAs t2
            t1.getSuppressed shouldBe Array()
            t2.getSuppressed shouldBe Array()
            list shouldBe List(2, 1)
          }
        }
      }
    }

    "throwing any `Throwable` in the first code block and a non-fatal `Throwable` in the second code block" should {
      "execute both code blocks and chain the exceptions via `Throwable.addSuppressed(Throwable)`" in {
        forAll(anyThrowables) { tf1 =>
          forAll(nonFatalThrowables) { tf2 =>
            val t1 = tf1()
            val t2 = tf2()
            var list = List.empty[Int]
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
            t2.getSuppressed shouldBe Array()
            list shouldBe List(2, 1)
          }
        }
      }
    }
  }

  "The `onTry { ... } onThrowable { ... }` statement" when {
    "not throwing any exception" should {
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

    "throwing any `Throwable` in the first code block" should {
      "execute both code blocks" in {
        forAll(anyThrowables) { tf =>
          val t = tf()
          var list = List.empty[Int]
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
    }

    "throwing any `Throwable` in the first code block and a fatal `Throwable` in the second code block" should {
      "throw only the exception from the second code block" in {
        forAll(anyThrowables) { tf1 =>
          forAll(fatalThrowables) { tf2 =>
            val t1 = tf1()
            val t2 = tf2()
            var list = List.empty[Int]
            intercept[Throwable] {
              onTry {
                list ::= 1
                throw t1
              } onThrowable {
                list ::= 2
                throw t2
              }
            } should be theSameInstanceAs t2
            t1.getSuppressed shouldBe Array()
            t2.getSuppressed shouldBe Array()
            list shouldBe List(2, 1)
          }
        }
      }
    }

    "throwing any `Throwable` in the first code block and a non-fatal `Throwable` in the second code block" should {
      "chain the exceptions via `Throwable.addSuppressed(Throwable)`" in {
        forAll(anyThrowables) { tf1 =>
          forAll(nonFatalThrowables) { tf2 =>
            val t1 = tf1()
            val t2 = tf2()
            var list = List.empty[Int]
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
            t2.getSuppressed shouldBe Array()
            list shouldBe List(2, 1)
          }
        }
      }
    }
  }

  "The `onTry { ... } onNonFatal { ... }` statement" when {
    "not throwing any exception" should {
      "execute only the first code block" in {
        var list = List.empty[Int]
        onTry {
          list ::= 1
          list
        } onNonFatal {
          list ::= 2
          list
        } shouldBe List(1)
        list shouldBe List(1)
      }
    }

    "throwing a fatal `Throwable` in the first code block" should {
      "execute only the first code block" in {
        forAll(fatalThrowables) { tf =>
          val t = tf()
          var list = List.empty[Int]
          intercept[Throwable] {
            onTry {
              list ::= 1
              throw t
            } onNonFatal {
              list ::= 2
            }
          } should be theSameInstanceAs t
          t.getSuppressed shouldBe Array()
          list shouldBe List(1)
        }
      }
    }

    "throwing a non-fatal `Throwable` in the first code block" should {
      "execute both code blocks" in {
        forAll(nonFatalThrowables) { tf =>
          val t = tf()
          var list = List.empty[Int]
          intercept[Throwable] {
            onTry {
              list ::= 1
              throw t
            } onNonFatal {
              list ::= 2
            }
          } should be theSameInstanceAs t
          t.getSuppressed shouldBe Array()
          list shouldBe List(2, 1)
        }
      }
    }

    "throwing a non-fatal `Throwable` in the first code block and a fatal `Throwable` in the second code block" should {
      "throw only the exception from the second code block" in {
        forAll(nonFatalThrowables) { tf1 =>
          forAll(fatalThrowables) { tf2 =>
            val t1 = tf1()
            val t2 = tf2()
            var list = List.empty[Int]
            intercept[Throwable] {
              onTry {
                list ::= 1
                throw t1
              } onNonFatal {
                list ::= 2
                throw t2
              }
            } should be theSameInstanceAs t2
            t1.getSuppressed shouldBe Array()
            t2.getSuppressed shouldBe Array()
            list shouldBe List(2, 1)
          }
        }
      }
    }

    "throwing a non-fatal `Throwable` in the first code block and a non-fatal `Throwable` in the second code block" should {
      "chain the exceptions via `Throwable.addSuppressed(Throwable)`" in {
        forAll(nonFatalThrowables) { tf1 =>
          forAll(nonFatalThrowables) { tf2 =>
            val t1 = tf1()
            val t2 = tf2()
            var list = List.empty[Int]
            intercept[Throwable] {
              onTry {
                list ::= 1
                throw t1
              } onNonFatal {
                list ::= 2
                throw t2
              }
            } should be theSameInstanceAs t1
            t1.getSuppressed shouldBe Array(t2)
            t2.getSuppressed shouldBe Array()
            list shouldBe List(2, 1)
          }
        }
      }
    }
  }
}
