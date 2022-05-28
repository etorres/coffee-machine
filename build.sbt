ThisBuild / organization := "es.eriktorr"
ThisBuild / version := "1.0.0"
ThisBuild / idePackagePrefix := Some("es.eriktorr.coffee_machine")
Global / excludeLintKeys += idePackagePrefix

ThisBuild / scalaVersion := "3.1.2"

Global / cancelable := true
Global / fork := true
Global / onChangedBuildSource := ReloadOnSourceChanges

Compile / compile / wartremoverErrors ++= Warts.unsafe.filter(
  !List(Wart.DefaultArguments, Wart.IterableOps, Wart.Null).contains(_),
)
Test / compile / wartremoverErrors ++= Warts.unsafe.filter(_ != Wart.DefaultArguments)

ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

scalacOptions ++= Seq(
  "-Xfatal-warnings",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Yexplicit-nulls", // https://docs.scala-lang.org/scala3/reference/other-new-features/explicit-nulls.html
  "-Ysafe-init", // https://docs.scala-lang.org/scala3/reference/other-new-features/safe-initialization.html
)

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "coffee-machine",
    Universal / maintainer := "https://eriktorr.es",
    Compile / mainClass := Some("es.eriktorr.coffee_machine.CoffeeMachineApp"),
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "0.7.29" % Test,
      "org.scalameta" %% "munit-scalacheck" % "0.7.29" % Test,
      "org.typelevel" %% "cats-core" % "2.7.0",
      "org.typelevel" %% "cats-kernel" % "2.7.0",
      "org.typelevel" %% "cats-effect" % "3.3.12",
      "org.typelevel" %% "cats-effect-kernel" % "3.3.12",
      "org.typelevel" %% "kittens" % "3.0.0-M4",
      "org.typelevel" %% "munit-cats-effect-3" % "1.0.7" % Test,
      "org.typelevel" %% "scalacheck-effect" % "1.0.4" % Test,
      "org.typelevel" %% "scalacheck-effect-munit" % "1.0.4" % Test,
      "org.typelevel" %% "squants" % "1.8.3",
    ),
    onLoadMessage := {
      s"""Custom tasks:
         |check - run all project checks
         |""".stripMargin
    },
  )

addCommandAlias(
  "check",
  "; undeclaredCompileDependenciesTest; unusedCompileDependenciesTest; scalafixAll; scalafmtSbtCheck; scalafmtCheckAll",
)
