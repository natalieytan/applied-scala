package com.reagroup.appliedscala.urls.savemovie

import cats.data.Validated._
import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.implicits._

object NewMovieValidator {

  /**
    * How do we combine two validations together? Refer to `ValidationExercises` for a refresher!
    *
    * Hint: `Validated` has an Applicative instance.
    */
  def validate(newMovie: NewMovieRequest): ValidatedNel[MovieValidationError, ValidatedMovie] = {
    val validatedNameNel = validateMovieName(newMovie.name)
    val validatedSynopsisNel = validateMovieSynopsis(newMovie.synopsis)
    (validatedNameNel, validatedSynopsisNel)
      .mapN(
        (validatedName, validatedSynposis) => ValidatedMovie(validatedName, validatedSynposis)
      )
  }

  /**
    * If `name` is empty, return an `InvalidNel` containing `MovieNameTooShort`,
    * else return a `Valid` containing the `name`.
    *
    * Hint: You can use `.isEmpty` or `.nonEmpty` on `String`
    */
  private def validateMovieName(name: String): ValidatedNel[MovieValidationError, String] =
    if (name.isEmpty) {
      MovieNameTooShort.invalidNel
    } else {
      name.validNel
    }

  /**
    * If `synopsis` is empty, return an `InvalidNel` containing `MovieSynopsisTooShort`,
    * else return a `Valid` containing the `synopsis`.
    *
    * Hint: You can use `.isEmpty` or `.nonEmpty` on `String`
    */
  private def validateMovieSynopsis(synopsis: String): ValidatedNel[MovieValidationError, String] =
    if (synopsis.isEmpty) {
      MovieSynopsisTooShort.invalidNel
    } else {
      synopsis.validNel
    }

}
