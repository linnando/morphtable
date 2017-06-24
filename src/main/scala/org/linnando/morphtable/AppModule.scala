package org.linnando.morphtable

import angulate2.forms.FormsModule
import angulate2.platformBrowser.BrowserModule
import angulate2.router.RouterModule
import angulate2.std._

import scala.scalajs.js

@NgModule(
  imports = @@[BrowserModule, FormsModule] :+
    RouterModule.forRoot(@@@(
      Route(path = "", redirectTo = "/morphtable", pathMatch = "full"),
      Route(path = "morphtable", component = %%[MorphTableComponent]),
      Route(path = "combinations", component = %%[CombinationsComponent])
    ), js.Dynamic.literal(useHash = true)),
  declarations = @@[
    AppComponent,
    MorphTableComponent,
    CombinationsComponent
  ],
  providers = @@[MorphTableService],
  bootstrap = @@[AppComponent]
)
class AppModule {
}
