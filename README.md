# json slim

> make your JSON look good in those skinny jeans

Json slim trims your JSON of unwanted adornments and attributes.


## Install

using sbt add the following to your build definition

```scala
resolvers += "softprops-bintray" at "http://dl.bintray.com/softprops/maven"

libraryDependencies += "com.meetup" % "json-slim" % "0.1.0"
```

or if you are using [ls-sbt](https://github.com/softprops/ls-sbt)

just do

```scala
ls-install json-slim
```

## usage

`jsonslim` provides a way to negotiate the presence ( or absence ) of properties within JSON-encoded objects. The vocabulary for doing is comprised to operations: `only` and `omit`. 
You can ask for `only` a set of attributes or, if you are feeling more conservative, you can ask to `omit` attributes.

Target attributes of JSON-encoded object are referenced by simple `.` delimited strings which represent traversable `path` to the attribute.

Assume you have given the following JSON-encoded object

```json
{
  "people": [{
    "name": "bill",
    "age": 30,
    "titles":["foo", "bar"]
  }]
}
```

Let's say you don't care for titles. Omit them with

```scala
jsonslim.Trim.omit("people.titles")(jsonStr) // Some({"people":[{"name":"bill","age":30}]})
```

Let's say you only want the names of people. Include only names with

```scala
jsonslim.Trim.only("people.name")(jsonStr) // Some({"people":[{"name":"bill"}]})
```

## inputs

A `Trim` is an exported function that you can assign and apply to multiple inputs. Inputs for a `Trim` are defined as the following typeclass `jsonslim.Src[T]`.

```scala
trait Src[T] {
  def apply(a: T): Option[JValue]
  def unapply(jv: JValue): T
}
```

A `Src[T]` needs a way to be lifted into an `org.json4s.JValue`, the intermediary format used to manipulate the JSON ast, and a way to go from an `org.json4s.JValue` back into type `T`. Since the lifting into a `JValue` may
not be possible, failure may be represented as `None`. `Src` conversions for `Strings` and direct `JVaules` are provided. If you use an alternative type representation
of JSON you will need to bring an implicit implementation of a `Src` into scope.

## combining operations

You can also combine multiple attribute paths for each operation for a slim party

```scala
val trim = jsonslim.Trim.only("people.name", "foo.bar")
                        .omit("people.titles", "baz.boom")
val slim = jsonDocuments.map(trim(_)).flatten
```



# Authors

* Doug Tangren [@softprops](http://github.com/softprops)

# License

Copyright 2013 Meetup inc

Licensed under [MIT](https://github.com/meetup/json-slim/blob/master/LICENSE)