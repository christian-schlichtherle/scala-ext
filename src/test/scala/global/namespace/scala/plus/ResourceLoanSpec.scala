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

import global.namespace.scala.plus.ResourceLoan._
import org.mockito.Mockito._
import org.scalatest.Matchers._
import org.scalatest.WordSpec
import org.scalatest.mockito.MockitoSugar.mock

/** @author Christian Schlichtherle */
class ResourceLoanSpec extends WordSpec {

  "The `loan(resource) to { resource => ... }` statement" when {
    "not throwing a `Throwable` in its code block" should {
      "call `AutoCloseable.close()`" in {
        val resource = mock[AutoCloseable]
        loan(resource) to { param =>
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
          loan(resource) to { param =>
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
          loan(resource) to { param =>
            param should be theSameInstanceAs resource
          }
        }
        verify(resource) close ()
        verifyNoMoreInteractions(resource)
      }
    }

    "throwing a `Throwable` in its code block and catching an `Exception` from `AutoCloseable.close()`" should {
      "chain the throwables via `Throwable.addSuppressed(Throwable)`" in {
        val resource = mock[AutoCloseable]
        val t1 = new Throwable
        val t2 = new Exception
        when(resource close ()) thenThrow t2
        intercept[Throwable] {
          loan(resource) to { param =>
            param should be theSameInstanceAs resource
            throw t1
          }
        } should be theSameInstanceAs t1
        t1.getSuppressed shouldBe Array(t2)
        verify(resource) close ()
        verifyNoMoreInteractions(resource)
      }
    }
  }
}
