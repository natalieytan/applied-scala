package com.reagroup.appliedscala.urls.fetchenrichedmovie

import io.circe.{Decoder, DecodingFailure}
import io.circe.Decoder.Result

import scala.util.{Failure, Success, Try}

case class Metascore(value: Int)

object Metascore {

  /**
    * Add a Decoder instance here to decode a JSON containing "Metascore" into a `Metascore`, e.g.
    *
    * Convert:
    *
    * {
    * ..
    * ..
    * "Metascore": "75",
    * ..
    * ..
    * }
    *
    * into:
    *
    * `Metascore(75)`
    */

  implicit val metascoreDecoder: Decoder[Metascore] = cursor => {
    for {
      metascore <- cursor.downField("Metascore").as[Int]
    } yield Metascore(metascore)
  }
}
