package com.reagroup.appliedscala.urls.savereview

import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.effect.IO
import cats.implicits._
import com.reagroup.appliedscala.models.{Movie, MovieId}

class SaveReviewService(saveReview: (MovieId, ValidatedReview) => IO[ReviewId],
                        fetchMovie: MovieId => IO[Option[Movie]]) {

  /**
    * Before saving a `NewReviewRequest`, we want to check that the movie exists and then
    * validate the request in order to get a `ValidatedReview`.
    * Complete `NewReviewValidator`, then use it here before calling `saveReview`.
    * Return all errors encountered whether the movie exists or not.
    *
    */
  def save(movieId: MovieId, review: NewReviewRequest): IO[ValidatedNel[ReviewValidationError, ReviewId]] = {
    fetchMovie(movieId).flatMap(movieOp => {
      val validatedMovie = validateMovie(movieOp)
      val validatedReview = validateReview(review)
      val combinedValidatedReview = (validatedMovie).productR(validatedReview)
      combinedValidatedReview.traverse(validatedReview => saveReview(movieId, validatedReview))
    })
  }

  private def validateMovie(movieOp: Option[Movie]): ValidatedNel[ReviewValidationError, Movie] = movieOp match {
    case Some(movie) => movie.validNel
    case None => MovieDoesNotExist.invalidNel
  }

  private def validateReview(review: NewReviewRequest): ValidatedNel[ReviewValidationError, ValidatedReview] =
    NewReviewValidator.validate(review)

}
