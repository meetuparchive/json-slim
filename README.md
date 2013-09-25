# json slim

> make your JSON look good in those skinny jeans

Json slim trims your JSON of unwanted adornments and attributes.

## usage

You can ask for `only` a set of attributes or, if you are feeling
more conservative, you can ask to `omit` attributes.

Target attributes are specified as simple `.` delimited paths.

For a JSON object

```json
{
  "people": [{
    "name": "bill",
    "age": 30,
    "titles":["foo", "bar"]
  }]
}
```

Let's say you don't care for titles.

```scala
json.slim.Trim.omit("people.titles")(jsonStr)
```

Let's say you only want the names of people

```scala
json.slim.Trim.only("people.names")(jsonStr)
```

You can also combine multiple paths for each for a slim party

```scala
val trim = json.slim.Trim.only("people.names", "foo.bar")
                         .omit("people.titles", "baz.boom")_
val slim = jsonDocuments.map(trim)
```