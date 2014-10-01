package net.bmjames.opts

import java.io.File
import net.bmjames.opts.builder._
import net.bmjames.opts.extra.execParser
import net.bmjames.opts.types.{ParserPrefs, ReadM, Parser}
import scalaz.{\/-, -\/, \/, Apply}

/** [[https://github.com/scopt/scopt/blob/3.2.0/src/test/scala/scopt/ImmutableParserSpec.scala#L397-L413]] */
object ScoptExample {
  case class Config(foo: Int = -1, out: File, xyz: Boolean = false,
                    libName: String, maxCount: Int, verbose: Boolean, debug: Boolean,
                    mode: String = "", files: Seq[File] = Seq(), keepalive: Boolean = false)

  import scalaz.syntax.bind._

  val tuple2ReadM: ReadM[(String, String)] =
    ReadM.ask.flatMap{ _.split("=") match {
      case Array(k, v) =>
        ReadM.readMMonadPlus.point(k -> v)
      case other =>
        ReadM.error("invalid argument")
    }}

  val stringIntPair: ReadM[(String, Int)] =
    tuple2ReadM.flatMap{ t =>
      eitherReader{ _ =>
        try{
          \/-(t._2.toInt).ensure("must be >0")(_ > 0).map(t._1 -> _)
        } catch {
          case _: NumberFormatException =>
            -\/("must be number")
        }
      }
    }

  val parser = Apply[Parser].apply5(
    intOption(short('f'), long("foo"), help("foo is an integer property")) <|> Parser.pure(-1),
    strOption(short('o'), long("out"), help("out is a required file property"), metavar("<file>")).map(new File(_)),
    option(stringIntPair, long("max"), help("maximum count for <libname>"), metavar("<libname>=<max>")) <|> Parser.pure("" -> -1),
    switch(long("verbose"), help("verbose is a flag")),
    switch(long("debug"), help("this option is hidden in the usage text"), hidden)
  )((foo, out, max, verbose, debug) =>
    Config(foo = foo, out = out, libName = max._1, maxCount = max._2, verbose = verbose, debug = debug)
  )

  def main(args: Array[String]): Unit = {
    import scalaz.syntax.show._
    println
    println(helpdoc.parserHelp(ParserPrefs(""), parser).show)
    println

    val config1 = execParser(Array("-oaaa"), "", info(parser))
    println(config1)
    assert(config1 == Config(out = new File("aaa"), foo = -1, maxCount = -1, libName = "", verbose = false, debug = false))

    val config2 = execParser("--out aaa --foo 11 --max bbb=22 --verbose --debug".split(' '), "", info(parser))
    println(config2)
    assert(config2 == Config(out = new File("aaa"), foo = 11, maxCount = 22, libName = "bbb", verbose = true, debug = true))
  }
}
