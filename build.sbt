scalaVersion := "2.13.1"

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full)

name := "weaver-test-examples"
organization := "io.github.dimitarg"
version := "1.0"

resolvers += Resolver.bintrayRepo("dimitarg", "maven")
libraryDependencies +=  "io.github.dimitarg"  %%  "weaver-test-extra" % "0.3.0" % "test"

testFrameworks += new TestFramework("weaver.framework.TestFramework")



