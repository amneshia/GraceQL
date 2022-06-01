package jdbc

import org.scalatest._
import flatspec._
import matchers._
import graceql.context.jdbc.*

class SQLRepSpec extends AnyFlatSpec with should.Matchers {
  case class ContactInfo(phone: String, email: String) derives SQLRow
  case class UserId(underying: Int) derives SQLValueClass
  case class User(id: UserId, name: String, contactInfo: Option[ContactInfo]) derives SQLRow

  def mirrorEquals[A, M](using rep: SQLMirror.Of[A])(using ev: M =:= rep.Mirror): Unit = ()
  
  """SQLMirror instances""" should "accurately infer the underlying flat SQL representations of Scala types" in {

    """
    mirrorEquals[Int, Int]
    mirrorEquals[Option[(String, String)], (Option[String], Option[String])]
    mirrorEquals[ContactInfo, (String, String)]
    mirrorEquals[User, (Int, String, Option[String], Option[String])]
    mirrorEquals[Option[User], (Option[Int], Option[String], Option[Option[String]], Option[Option[String]])]
    """ should compile
  }
}
