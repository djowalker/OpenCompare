package org.opencompare.io.wikipedia.io

import java.io.File
import java.util

import org.opencompare.api.java.PCMContainer
import org.opencompare.api.java.io.PCMLoader
import org.opencompare.io.wikipedia.export.PCMModelExporter
import org.opencompare.io.wikipedia.parser.PageVisitor
import org.opencompare.io.wikipedia.pcm.Page
import org.sweble.wikitext.engine.utils.DefaultConfigEnWp
import org.sweble.wikitext.parser.utils.SimpleParserConfig
import org.sweble.wikitext.parser.{WikitextParser, WikitextPreprocessor}

import scala.io.Source

/**
 * Created by  on 26/11/14.
 */
class WikiTextLoader(
                    val templateProcessor: WikiTextTemplateProcessor = new WikiTextTemplateProcessor()
                      )  extends PCMLoader {

  // Constructor for Java compatibility with default parameters
  def this() {
    this(new WikiTextTemplateProcessor())
  }

  private val parserConfig = new SimpleParserConfig()
  val preprocessor = new WikitextPreprocessor(parserConfig)
  val parser = new WikitextParser(parserConfig)

  private val wikiConfig = DefaultConfigEnWp.generate()

  private val exporter = new PCMModelExporter

  override def load(code: String): util.List[PCMContainer] = {
    mine(code, "")
  }

  override def load(file: File): util.List[PCMContainer] = {
    this.load(Source.fromFile(file).mkString)
  }


  def mineInternalRepresentation(code : String, title : String): Page = {
    val ast = parser.parseArticle(code, title)
    val structuralVisitor = new PageVisitor(wikiConfig, preprocessor, templateProcessor, parser)
    val page = structuralVisitor.extractPage(ast, title)
    page
  }

  def mine(code : String, title : String) : util.List[PCMContainer] = {
    val page = mineInternalRepresentation(code, title)
    exporter.export(page)
  }

}
