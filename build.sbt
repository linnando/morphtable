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
      "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.6",
      "org.scala-lang.modules" %%% "scala-parser-combinators" % "1.0.6",
      "com.github.karasiq" %%% "scalajs-bootstrap" % "2.0.0",
      "org.specs2" %% "specs2-core" % "3.9.1" % "test"
    ),
    ngBootstrap := Some("org.linnando.morphtable.AppModule"),
    scalacOptions in Test ++= Seq("-Yrangepos")
    //resolvers += Resolver.sonatypeRepo("releases")
  )

val stage = taskKey[Unit]("Stage task")

stage := (fastOptJS in Compile).value
