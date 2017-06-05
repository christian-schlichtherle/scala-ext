[![Build Status](https://travis-ci.org/christian-schlichtherle/scala-plus.svg?branch=master)](https://travis-ci.org/christian-schlichtherle/scala-plus)
[![Release Notes](https://img.shields.io/github/release/christian-schlichtherle/scala-plus.svg?maxAge=3600)](https://github.com/christian-schlichtherle/scala-plus/releases)
[![Scala 2.10](https://img.shields.io/maven-central/v/global.namespace.scala-plus/scala-plus_2.10.svg?label=Scala%202.10&maxAge=3600)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22global.namespace.scala-plus%22%20AND%20a%3A%22scala-plus_2.10%22)
[![Scala 2.11](https://img.shields.io/maven-central/v/global.namespace.scala-plus/scala-plus_2.11.svg?label=Scala%202.11&maxAge=3600)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22global.namespace.scala-plus%22%20AND%20a%3A%22scala-plus_2.11%22)
[![Scala 2.12](https://img.shields.io/maven-central/v/global.namespace.scala-plus/scala-plus_2.12.svg?label=Scala%202.12&maxAge=3600)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22global.namespace.scala-plus%22%20AND%20a%3A%22scala-plus_2.12%22)
[![Apache License 2.0](https://img.shields.io/github/license/christian-schlichtherle/neuron-di.svg?maxAge=3600)](https://www.apache.org/licenses/LICENSE-2.0)

# Scala+

Scala+ is a collection of utilities for Scala code.
It is cross-built for Scala 2.10, 2.11 and 2.12.

## How to use

To add it as a dependency, use the following coordinates:

    libraryDependencies += "global.namespace.scala-plus" %% "scala-plus" % "<version>"

For a valid version number, see [releases](releases).

## How to build and test

    sbt +test
    
## How to publish

First, edit the value of the setting key `version` in `build.sbt` to make sure that it does not end with `-SNAPSHOT`.

Second, make sure the credentials are configured in `~/.sbt/0.13/*.sbt` and run

    sbt +publishSigned

Third, tag the commit with `scala-plus-<version>` where `<version>` is the value of the setting key `version`.

Fourth, increment the value of the setting key `version` to the next [semantic version number](https://semver.org) and 
append `-SNAPSHOT` again.

Finally, browse to [OSS Nexus Repository Manager](https://oss.sonatype.org/#stagingRepositories), find the staging 
repository, close and release it.
