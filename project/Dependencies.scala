import sbt._

object Version {
  val akka = "2.5.6"
  val leveldb = "0.7"
  val leveldbjni = "1.8"
  val µtest = "0.6.0"
  val scodec = "1.10.3"
}

object Dependencies {

  lazy val rootDependencies: Seq[ModuleID] =
    Seq(
      "com.typesafe.akka" %% "akka-typed" % Version.akka,
      "com.typesafe.akka" %% "akka-persistence" % Version.akka,
      "org.iq80.leveldb" % "leveldb" % Version.leveldb,
      "org.fusesource.leveldbjni" % "leveldbjni-all" % Version.leveldbjni,
      "org.scodec" %% "scodec-core" % Version.scodec
    ) ++ Seq(
      "com.lihaoyi" %% "utest" % Version.µtest
    ).map(_ % Test)
}
