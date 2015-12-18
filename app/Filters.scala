import javax.inject.Inject

import filters.BasicAuthFilter
import play.api.http.HttpFilters


class Filters @Inject()(
                         auth: BasicAuthFilter
                         ) extends HttpFilters {
  val filters = Seq(auth)
}