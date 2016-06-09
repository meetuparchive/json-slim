package jsonslim

import org.scalatest.FunSpec
import org.json4s.JsonDSL._

class TrimSpec extends FunSpec {
  val JsonDoc =
    """
    |{
    |  "people":[{
    |    "name": "bob",
    |    "titles": ["boss","senior manager"]
    |    "badges": [{"color":"green"}]
    |  }]
    |}
    """.stripMargin

  describe ("Trim") {
    it ("should return only the defined paths when requested") {
      val names = Trim.only("people.name")(JsonDoc)
      assert(names === Some("""{"people":[{"name":"bob"}]}"""))
    }
    it ("should omit defined paths when requested") {
      val names = Trim.omit("people.titles")(JsonDoc)
      assert(names == Some("""{"people":[{"name":"bob","badges":[{"color":"green"}]}]}"""))
    }
    it ("should work with arrays") {
      val names = Trim.omit("people.titles")(s"[$JsonDoc]")
      assert(names == Some("""[{"people":[{"name":"bob","badges":[{"color":"green"}]}]}]"""))
    }
    it ("should return an array of empty objects when nothing matches given an array input") {
      val names = Trim.only("bogus")(s"[$JsonDoc]")
      assert(names == Some("""[{}]"""))
    }
    it ("should return an empty obj when nothing matches given an obj input") {
      val names = Trim.only("bogus")(JsonDoc)
      assert(names == Some("""{}"""))
    }
    it ("should not affect input with no only filter") {
      val names = Trim.only()(JsonDoc)
      assert(names == Some("""{"people":[{"name":"bob","titles":["boss","senior manager"],"badges":[{"color":"green"}]}]}"""))
    }
    it ("should not affect input with no omit filter") {
      val names = Trim.only()(JsonDoc)
      assert(names == Some("""{"people":[{"name":"bob","titles":["boss","senior manager"],"badges":[{"color":"green"}]}]}"""))
    }
    it ("should honor multiple only lists") {
      val names = Trim.only("people.name", "people.titles")(JsonDoc)
      assert(names === Some("""{"people":[{"titles":["boss","senior manager"],"name":"bob"}]}"""))
    }
    it ("should handle case of single nesting") {
      val fooBar = Trim.only("foo.bar")("""{"foo":{"bar":"boom"}}""")
      assert(fooBar === Some("""{"foo":{"bar":"boom"}}"""))
    }
  }
}
