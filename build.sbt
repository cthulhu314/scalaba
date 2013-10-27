organization  := "com.github.cthulhu314"

version       := "0.1"

scalaVersion  := "2.10.2"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io/"
)

resolvers += "repo.novus rels" at "http://repo.novus.com/releases/"

libraryDependencies ++= Seq(
  "io.spray"            %   "spray-can"     % "1.2-M8",
  "io.spray"            %   "spray-routing" % "1.2-M8",
  "io.spray"            %   "spray-caching" % "1.2-M8",
  "io.spray"            %   "spray-testkit" % "1.2-M8" % "test",
  "com.typesafe.akka"   %%  "akka-actor"    % "2.2.0-RC1",
  "com.typesafe.akka"   %%  "akka-testkit"  % "2.2.0-RC1" % "test",
  "org.specs2"          %%  "specs2"        % "1.14" % "test"
)

libraryDependencies ++= List(
  "com.typesafe.slick" %% "slick" % "1.0.1",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.h2database" % "h2" % "1.3.166"
)

libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.2.5"

libraryDependencies += "com.escalatesoft.subcut" %% "subcut" % "2.0"

libraryDependencies += "org.mongodb" %% "casbah" % "2.6.3"

libraryDependencies += "com.novus" %% "salat" % "1.9.4"

seq(Revolver.settings: _*)

