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

import org.mockito.Mockito._
import org.scalatest.Matchers._
import org.scalatest.WordSpec
import org.scalatest.mockito.MockitoSugar.mock

/** @author Christian Schlichtherle */
class LoanSpec extends WordSpec {

  "The Loan(...) { ... } statement" should {
    "create the resource lazily, but exactly only once" in {
      val fun = mock[() => AutoCloseable]
      when(fun()) thenReturn mock[AutoCloseable]
      val loan = Loan(fun())
      verify(fun, times(0))()
      loan { closeable => }
      verify(fun)()
    }
  }

  "The loan ... to ... statement" when {
    "not throwing a `Throwable` in its code block" should {
      "call `AutoCloseable.close()`" in {
        val resource = mock[AutoCloseable]
        Loan(resource) { param =>
          param should be theSameInstanceAs resource
        }
        verify(resource) close ()
        verifyNoMoreInteractions(resource)
      }
    }

    "throwing a `Throwable` in its code block" should {
      "call `AutoCloseable.close()`" in {
        val resource = mock[AutoCloseable]
        intercept[Throwable] {
          Loan(resource) { param =>
            param should be theSameInstanceAs resource
            throw new Throwable
          }
        }
        verify(resource) close ()
        verifyNoMoreInteractions(resource)
      }
    }

    "catching an `Exception` from `AutoCloseable.close()`" should {
      "pass on the exception" in {
        val resource = mock[AutoCloseable]
        when(resource close ()) thenThrow new Exception
        intercept[Exception] {
          Loan(resource) { param =>
            param should be theSameInstanceAs resource
          }
        }
        verify(resource) close ()
        verifyNoMoreInteractions(resource)
      }
    }

    "throwing a `Throwable` in its code block and catching a `Throwable` from `AutoCloseable.close()`" should {
      "chain the exceptions via `Throwable.addSuppressed(Throwable)`" in {
        val resource = mock[AutoCloseable]
        val t1 = new Throwable
        val t2 = new Exception
        when(resource close ()) thenThrow t2
        intercept[Throwable] {
          Loan(resource) { param =>
            param should be theSameInstanceAs resource
            throw t1
          }
        } should be theSameInstanceAs t1
        t1.getSuppressed shouldBe Array(t2)
        verify(resource) close ()
        verifyNoMoreInteractions(resource)
      }
    }

    "dealing with a null resource" should {
      "just call the resource handler" in {
        var called = false
        Loan(null: AutoCloseable) { param =>
          param shouldBe null
          called = true
        }
        called shouldBe true
      }
    }
  }
}
