package com.reagroup.appliedscala.models

import io.circe.{Decoder, Encoder, Json}

case class MovieId(value: Long)

object MovieId {

  /**
    * Add an Encoder instance here
    *
    * We want the resulting Json to look like this:
    *
    * {
    *   "id": 1
    * }
    *
    * Hint: You don't want to use `deriveEncoder` here
    */

  implicit val movieIdEncoder: Encoder[MovieId] = (movieId: MovieId) => {
    Json.obj(
      ("id", Json.fromLong(movieId.value))
    )
  }

  implicit val movieIdDecoder: Decoder[MovieId] = Decoder.decodeLong.map(MovieId(_))
}
