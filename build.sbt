/*
 * Copyright © 2017 Schlichtherle IT Services
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

crossScalaVersions := Seq("2.10.2", "2.11.0", "2.12.0")

fork in Test := true // required to make `javaOptions` effective.

javacOptions in compile := javacOptions.value ++ Seq("-target", "1.8", "-deprecation")

javacOptions := Seq("-source", "1.8") // unfortunately, this is used for running javadoc, e.g. in the `packageDoc` task key?!

javaOptions += "-ea"

homepage := Some(url("https://github.com/christian-schlichtherle/scala-plus"))

libraryDependencies ++= Seq(
  "org.mockito" % "mockito-core" % "2.8.9",
  "org.scalacheck" %% "scalacheck" % "1.13.5",
  "org.scalatest" %% "scalatest" % "3.0.3"
)

licenses := Seq("Apache License, Version 2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0"))

name := "Scala+"

normalizedName := "scala-plus"

organization := "global.namespace"

pomExtra :=
  <developers>
    <developer>
      <name>Christian Schlichtherle</name>
      <email>christian AT schlichtherle DOT de</email>
      <organization>Schlichtherle IT Services</organization>
      <timezone>Europe/Berlin</timezone>
      <roles>
        <role>owner</role>
      </roles>
      <properties>
        <picUrl>http://www.gravatar.com/avatar/e2f69ddc944f8891566fc4b18518e4e6.png</picUrl>
      </properties>
    </developer>
  </developers>

pomIncludeRepository := (_ => false)

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  Some(
    if (version(_ endsWith "-SNAPSHOT").value) {
      "snapshots" at nexus + "content/repositories/snapshots"
    } else {
      "releases" at nexus + "service/local/staging/deploy/maven2"
    }
  )
}

scalacOptions := Seq("-deprecation", "-explaintypes", "-feature", "-unchecked")

scalaVersion := "2.12.2"

scmInfo := Some(ScmInfo(
  browseUrl = url("https://github.com/christian-schlichtherle/scala-plus"),
  connection = "scm:git:git://github.com/christian-schlichtherle/scala-plus.git",
  devConnection = Some("scm:git:ssh://git@github.com/christian-schlichtherle/scala-plus.git")
))

testOptions += Tests.Argument(TestFrameworks.JUnit, "-a")

version := "0.1-SNAPSHOT"
