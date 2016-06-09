package jsonslim

import org.json4s._
import org.json4s.native.JsonMethods._ // for parseOpt

/** Interface for parsing json from arbitrary sources */
trait Src[T] {
  def apply(a: T): Option[JValue]
  def unapply(jv: JValue): T
}

object Src {
  implicit object Str extends Src[String] {
    def apply(in: String) = parseOpt(in)
    def unapply(jv: JValue) = compact(render(jv))
  }
  implicit object JVal extends Src[JValue] {
    def apply(in: JValue) = Some(in)
    def unapply(jv: JValue) = jv
  }
  def apply[T: Src](in: T): Option[JValue] = implicitly[Src[T]].apply(in)
}
