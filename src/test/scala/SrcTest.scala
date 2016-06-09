package jsonslim

import org.scalatest.FunSpec
import org.json4s.JsonDSL._
import org.json4s.{JArray, JValue}

class SrcTest extends FunSpec {
  describe("Src") {
    it ("should apply to strings") {
      assert(Src("[]") == Some(JArray(Nil)))
    }
    it ("should apply to jvalues") {
      val value: JValue = ("foo" -> 1) ~ ("bar" -> "boom")
      assert(Src(value) == Some(value))
    }
  }
}
