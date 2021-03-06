package org.http4s
package jawn

import cats.effect.IO

trait JawnDecodeSupportSpec[J] extends Http4sSpec with JawnInstances {
  def testJsonDecoder(decoder: EntityDecoder[IO, J]) =
    "json decoder" should {
      "return right when the entity is valid" in {
        val resp = Response[IO](Status.Ok).withEntity("""{"valid": true}""")
        decoder.decode(resp, strict = false).value.unsafeRunSync must beRight
      }

      "return a ParseFailure when the entity is invalid" in {
        val resp = Response[IO](Status.Ok).withEntity("""garbage""")
        decoder.decode(resp, strict = false).value.unsafeRunSync must beLeft.like {
          case MalformedMessageBodyFailure("Invalid JSON", _) => ok
        }
      }

      "return a ParseFailure when the entity is empty" in {
        val resp = Response[IO](Status.Ok).withEntity("")
        decoder.decode(resp, strict = false).value.unsafeRunSync must beLeft.like {
          case MalformedMessageBodyFailure("Invalid JSON: empty body", _) => ok
        }
      }
    }
}
