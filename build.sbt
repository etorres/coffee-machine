import org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtOnCompile
import sbt.Keys._
import sbt._
import wartremover.Wart
import wartremover.WartRemover.autoImport._

organization := "es.eriktorr"
name := "coffee-machine"
version := (version in ThisBuild).value

scalaVersion := "2.13.3"

val catsCoreVersion = "2.2.0"
val catsEffectsVersion = "2.2.0"
val newtypeVersion = "0.4.4"
val refinedVersion = "0.9.15"
val scalaTestVersion = "3.2.2"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % catsCoreVersion,
  "org.typelevel" %% "cats-effect" % catsEffectsVersion,
  "io.estatico" %% "newtype" % newtypeVersion,
  "eu.timepit" %% "refined-cats" % refinedVersion,
  "eu.timepit" %% "refined-scalacheck" % refinedVersion % Test,
  "org.scalatest" %% "scalatest" % scalaTestVersion % Test
)

scalacOptions ++= Seq(
  "-encoding",
  "utf8",
  "-Xfatal-warnings",
  "-Xlint",
  "-deprecation",
  "-unchecked"
)

javacOptions ++= Seq(
  "-g:none",
  "-source",
  "11",
  "-target",
  "11",
  "-encoding",
  "UTF-8"
)

scalafmtOnCompile := true

val warts: Seq[Wart] = Warts.unsafe

wartremoverErrors in (Compile, compile) ++= warts
wartremoverErrors in (Test, compile) ++= warts

lazy val root = (project in file("."))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "es.eriktorr.coffee-machine",
    buildInfoOptions := Seq(BuildInfoOption.BuildTime)
  )
