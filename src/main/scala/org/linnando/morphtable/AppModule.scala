package org.linnando.morphtable

import angulate2.forms.FormsModule
import angulate2.platformBrowser.BrowserModule
import angulate2.std._

@NgModule(
  imports = @@[BrowserModule, FormsModule],
  declarations = @@[
    AppComponent,
    MorphTableComponent
  ],
  providers = @@[MorphTableService],
  bootstrap = @@[AppComponent]
)
class AppModule {

}
