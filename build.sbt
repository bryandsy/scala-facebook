import scalariform.formatter.preferences._

name := "facebook"

organization := "spryly"

version := "0.1.0"

scalacOptions := Seq("-deprecation", "-unchecked")

libraryDependencies += "io.spray"           %   "spray-can"     %  "1.2.0"

libraryDependencies += "io.spray"           %   "spray-httpx"   %  "1.2.0"

libraryDependencies += "io.spray"           %%  "spray-json"    %  "1.2.5"

libraryDependencies += "com.typesafe.akka"  %%  "akka-actor"    %  "2.2.3"

libraryDependencies += "com.github.nikita-volkov" % "sext" % "0.2.3"

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies += "org.scala-lang" %% "scala-pickling" % "0.8.0-SNAPSHOT"

scalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(RewriteArrowSymbols, true)
  .setPreference(AlignParameters, true)
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, true)
  .setPreference(MultilineScaladocCommentsStartOnFirstLine, true)