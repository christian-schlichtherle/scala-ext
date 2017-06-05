# Scala+

[![Build Status](https://travis-ci.org/christian-schlichtherle/scala-plus.svg?branch=master)](https://travis-ci.org/christian-schlichtherle/scala-plus)

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

Second, run

    sbt +publishSigned

Third, tag the commit with `scala-plus-<version>` where `<version>` is the value of the setting key `version`.

Fourth, increment the value of the setting key `version` to the next [semantic version number](https://semver.org) and 
append `-SNAPSHOT` again.

Finally, browse to [OSS Nexus Repository Manager](https://oss.sonatype.org/#stagingRepositories), find the staging 
repository, close and release it.
