package jdbc

import org.scalatest._
import flatspec._
import matchers._
import java.sql.*;
import scala.util.Try

abstract class JDBCSpec(
    val vendor: String,
    val url: String,
    val user: Option[String],
    val password: String
) extends AnyFlatSpec
    with should.Matchers {

  s"""
  Connecting to the $vendor vendor using url: ${url}, user: ${user.orNull}, and pass: ${password}
  """ should "succeed" in {
    noException should be thrownBy {
      DriverManager.getConnection(url, user.orNull, password).close()
    }
  }
}
