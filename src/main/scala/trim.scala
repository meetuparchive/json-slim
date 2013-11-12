package jsonslim

import org.json4s._
import org.json4s.native.JsonMethods.{ compact, render }
import org.json4s.JsonDSL._

trait Builder {
  def trimmed: Trim
  /** omits json fields identified by period-delimited paths */
  def only(paths: String*) = trimmed.copy(_only = paths.toList)
  /** selects only the json fields identified by a list of period-delimited paths */
  def omit(paths: String*) = trimmed.copy(_omit = paths.toList)
  def delimiter(d: String) = trimed.copy(_delimiter = d)
}

object Trim extends Builder {
  def trimmed: Trim = Trim()
}

case class Trim(
  _omit: List[String] = List.empty[String],
  _only: List[String] = List.empty[String],
  _delimiter: String = ".")
  extends TrimOps with Builder {
  def trimmed = this
  /** trims some arbitrary JSON src to provided specification */
  def apply[T: Src](in: T): Option[T] =
    Src(in).map(trim(_only, _omit)).map(implicitly[Src[T]].unapply(_))
}

trait TrimOps {
  private def append(target: JValue, field: (String, JValue)) =
    target match {
      case JObject(fs) => JObject(field :: fs)
      case xs => xs ++ field
    }

  /** Trims json value down to essentials by only including onlys the omiting omits */
  protected def trim(onlys: List[String], omits: List[String])(js: JValue): JValue =
    js match {
      case JArray(ary) =>
        JArray(ary.map(trim(onlys, omits)))
      case value =>
        try omit(only(value, onlys), omits)
        catch {
          case e: Throwable =>
            e.printStackTrace()
            js
        }
    }

  /** Reduces a json value's attributes by excluding a list of path specs
   *  @param js The source value
   *  @param specs list of period (.) separated path specs. Path specs
   *               correlate to paths to values in json objects
   */
  private def omit(js: JValue, specs: List[String]): JValue = {
    // much like jvalue#replace(list, repl) except jarrays are taken into account
    def replace(src: JValue, l: List[String], replacement: JValue): JValue = {
      def rep(l: List[String], in: JValue): JValue = {
        l match {
          case x :: xs => in match {
            case JObject(fields) => JObject(
              fields.map {
                case (`x`, value) => (x, if (xs == Nil) replacement else rep(xs, value))
                case field => field
              }
            )
            case JArray(ary) =>
              JArray(ary.map(replace(_, l, replacement)))
            case other => other
          }
          case Nil => in
        }
      }
      rep(l, src)
    }
    (js /: specs.map(_.split('.').toList))(replace(_, _, JNothing)) remove {
      case JNothing => true
      case _ => false
    }
  }

  /** Reduces a json value's attributes by only including a list of path specs
   *  @param js The source value
   *  @specs specs list of period (.) separated path specs. Path specs correlate
   *               to paths to values in json objects.
   *  @return the reduced object
   */
  private def only(js: JValue, specs: List[String]): JValue = {
    def only0(
      target: JValue,
      keys: List[String],
      src: JValue
    ): JValue = {
      keys match {
        case Nil =>
          target
        case key :: Nil =>
          append(target, JField(key, (src \ key)))
        case key :: tail =>
          (src \ key) match {
            case JNothing =>
              target
            case obj: JObject =>
              val field = JField(key, only0((target \ key) match {
                case JNothing =>
                  JObject(Nil)
                case tv =>
                  tv
              }, tail, obj))
              append(target, field)
            case JArray(ary) =>
              val (targets, update) = (target \ key) match {
                case JNothing =>
                  (ary.map(_ => JObject(Nil)), { updated: List[JValue] =>
                    append(target, (key, JArray(updated)))
                  })
                case JArray(tv) =>
                  (tv, { updated: List[JValue] =>
                    target.replace(key :: Nil, JArray(updated))
                  })
                case ut =>
                  (ary.map(_ => JObject(Nil)), { updated: List[JValue] =>
                    target
                  })
              }
              update(ary.zip(targets) map {
                case (jsrc, jtar) => only0(jtar, tail, jsrc)
              })
            case _ =>
              // key binds to a primative value and a tail remains,
              // this is an invalid query
              target
          }
      }
    }
    specs match {
      case Nil => js
      case xs => ((JObject(Nil): JValue) /: xs.map(_.split('.').toList))(only0(_, _, js))
    }
  }
}
