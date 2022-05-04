package jdbc

import graceql.context.jdbc.VendorTreeCompiler

import scala.quoted.*
import graceql.core.*
import graceql.context.jdbc.*
import scala.annotation.targetName

object ParseOnlyCompiler extends VendorTreeCompiler[GenSQL]:
  val encoders: Encoders = new Encoders:
    @targetName("booleanLit")
    def lit(l: Expr[Boolean])(using Quotes): Expr[String] = '{ $l.toString }
    @targetName("charLit")
    def lit(l: Expr[Char])(using Quotes): Expr[String] = '{ $l.toString }
    @targetName("byteLit")
    def lit(l: Expr[Byte])(using Quotes): Expr[String] = '{ $l.toString }
    @targetName("shortLit")
    def lit(l: Expr[Short])(using Quotes): Expr[String] = '{ $l.toString }
    @targetName("intLit")
    def lit(l: Expr[Int])(using Quotes): Expr[String] = '{ $l.toString }
    @targetName("longLit")
    def lit(l: Expr[Long])(using Quotes): Expr[String] = '{ $l.toString }
    @targetName("floatLit")
    def lit(l: Expr[Float])(using Quotes): Expr[String] = '{ $l.toString }
    @targetName("doubleLit")
    def lit(l: Expr[Double])(using Quotes): Expr[String] = '{ $l.toString }
    @targetName("stringLit")
    def lit(l: Expr[String])(using Quotes): Expr[String] = '{s"\"${$l.replace("\"","")}\""}
    def alias(l: Expr[String])(using Quotes): Expr[String] = l
    def tableName(l: Expr[String])(using Quotes): Expr[String] = l
  override protected def compileNative[C[X[+_]] <: Capabilities[X], S[+X] <: Iterable[X], A](
      expr: Expr[C[Statement] => A]
  )(using
      q: Quotes,
      tv: Type[GenSQL],
      tc: Type[C],
      ta: Type[A],
      ts: Type[S],
  ): Node[Expr,Type] =
    import q.reflect.{Tree => _, Statement => _, *}
    val e = preprocess(expr)
    e match
      case '{ (ev: C[Statement]) =>
            ev.fromNative(
              ev.native($sc: StringContext)(using $ns: NativeSupport[Statement])(${ Varargs(args) }: _*)
            )
          } =>
        parseNative(args)(sc)
      case _ =>
        throw Exception(
          s"""
          Only direct 'fromNative' call followed by the 'native' interpolator are allowed for this test spec.
          Found tree was: ${e.asTerm.show(using Printer.TreeAnsiCode)}
          """
        )