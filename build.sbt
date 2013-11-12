organization := "com.meetup"

name := "json-slim"

version := "0.1.0"

scalaVersion := "2.10.3"

description := "Trims your JSON of unwanted adornments and attributes."

homepage := Some(url(s"https://github.com/meetup/${name.value}/"))

licenses += ("MIT", url(s"https://github.com/meetup/${name.value}/blob/${version.value}/LICENSE"))

libraryDependencies += ("org.json4s" %% "json4s-native" % "3.2.5").exclude("com.thoughtworks.paranamer", "paranamer").exclude("org.codehaus","codehaus-parent")

libraryDependencies += "org.scalatest" %% "scalatest" % "1.9.1" % "test"

bintraySettings

bintray.Keys.packageLabels in bintray.Keys.bintray := Seq("json")

lsSettings

LsKeys.tags in LsKeys.lsync := Seq("json")

externalResolvers in LsKeys.lsync := resolvers.value
