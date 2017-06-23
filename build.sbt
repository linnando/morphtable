
lazy val commonSettings = Seq(
  organization := "org.linnando",
  version := "0.0.1",
  scalaVersion := "2.11.8",
  scalacOptions ++= Seq("-deprecation","-unchecked","-feature","-Xlint"),
  resolvers += Resolver.sonatypeRepo("snapshots"),
  libraryDependencies ++= Seq(
  ),
  publish := {},
  publishLocal := {}
)


lazy val root = project.in(file(".")).
  enablePlugins(Angulate2Plugin).
  settings(commonSettings: _*).
  settings(
    name := "morphtable",
    libraryDependencies ++= Seq(
      "com.github.karasiq" %%% "scalajs-bootstrap" % "2.0.0"
    ),
    ngBootstrap := Some("org.linnando.morphtable.AppModule")
    //resolvers += Resolver.sonatypeRepo("releases")
  )


