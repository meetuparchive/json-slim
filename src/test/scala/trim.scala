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
    |  }]
    |}
    """.stripMargin

  describe ("trim#only") {
    it ("should return only the defined paths") {
      val names = Trim.only("people.name")(JsonDoc)
      assert(names === Some("""{"people":[{"name":"bob"}]}"""))
    }
  }
}
