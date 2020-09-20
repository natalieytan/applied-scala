package com.reagroup.appliedscala

import cats.data.Kleisli
import cats.effect.IO
import cats.effect.ContextShift
import cats.effect.Timer
import cats.effect.IO
import cats.implicits._
import com.reagroup.appliedscala.config.Config
import com.reagroup.appliedscala.urls.repositories.{Http4sMetascoreRepository, PostgresqlRepository}
import com.reagroup.appliedscala.urls.fetchallmovies.{FetchAllMoviesController, FetchAllMoviesService}
import com.reagroup.appliedscala.urls.fetchenrichedmovie.{FetchEnrichedMovieController, FetchEnrichedMovieService}
import com.reagroup.appliedscala.urls.fetchmovie.{FetchMovieController, FetchMovieService}
import com.reagroup.appliedscala.urls.savemovie.{SaveMovieController, SaveMovieService}
import com.reagroup.appliedscala.urls.savereview.{SaveReviewController, SaveReviewService}
import org.http4s._
import org.http4s.client.Client

class AppRuntime(config: Config, httpClient: Client[IO], contextShift: ContextShift[IO], timer: Timer[IO]) {

  /**
    * This is the repository that talks to Postgresql
    */
  private val pgsqlRepo = PostgresqlRepository(config.databaseConfig, contextShift)
  private val metascoreRepo = new Http4sMetascoreRepository(httpClient, config.omdbApiKey)

  /**
    * This is where we instantiate our `Service` and `Controller` for each endpoint.
    * We will need to write a similar block for each endpoint we write.
    */
  private val fetchAllMoviesController: FetchAllMoviesController = {
    val fetchAllMoviesService: FetchAllMoviesService = new FetchAllMoviesService(pgsqlRepo.fetchAllMovies)
    new FetchAllMoviesController(fetchAllMoviesService.fetchAll)
  }

  private val fetchMovieController: FetchMovieController = {
    val fetchMovieService: FetchMovieService = new FetchMovieService(pgsqlRepo.fetchMovie)
    new FetchMovieController(fetchMovieService.fetch)
  }

  private val fetchEnrichedMovieController: FetchEnrichedMovieController = {
    val fetchEnrichedMovieService = new FetchEnrichedMovieService(pgsqlRepo.fetchMovie, metascoreRepo.apply)
    new FetchEnrichedMovieController(fetchEnrichedMovieService.fetch)
  }

  private val saveMovieController: SaveMovieController = {
    val saveMovieService = new SaveMovieService(pgsqlRepo.saveMovie)
    new SaveMovieController(saveMovieService.save)
  }

  private val saveReviewController: SaveReviewController = {
    val saveReviewService = new SaveReviewService(pgsqlRepo.saveReview, pgsqlRepo.fetchMovie)
    new SaveReviewController(saveReviewService.save)
  }

  private val appRoutes = new AppRoutes(
    fetchAllMoviesHandler = fetchAllMoviesController.fetchAll,
    fetchMovieHandler = (movieId: Long) => fetchMovieController.fetch(movieId),
    fetchEnrichedMovieHandler = (movieId: Long) => fetchEnrichedMovieController.fetch(movieId),
    saveMovieHandler = (request: Request[IO]) => saveMovieController.save(request),
    saveReviewHandler = (movieId: Long, request: Request[IO]) => saveReviewController.save(movieId, request)
  )

  /*
   * All routes that make up the application are exposed by AppRuntime here.
   */
  def routes: HttpApp[IO] = HttpApp((req: Request[IO]) => appRoutes.openRoutes(req).getOrElse(Response[IO](status = Status.NotFound)))

}
