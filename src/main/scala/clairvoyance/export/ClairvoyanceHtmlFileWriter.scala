package clairvoyance.export

import clairvoyance.export.ClairvoyanceHtmlFileWriter.{copyResourcesOnlyOnce, outputDir}
import clairvoyance.io.ClasspathResources
import java.io.Writer
import java.util.concurrent.atomic.AtomicBoolean
import scala.util.Properties.{lineSeparator, propOrElse, userDir}
import scala.xml.{Xhtml, NodeSeq}
import scalax.file.Path

trait ClairvoyanceHtmlFileWriter {

  def writeFiles = (htmlFiles: Seq[ClairvoyanceHtml]) => {
    copyResources()
    htmlFiles foreach writeFile
  }

  protected def writeFile = (file: ClairvoyanceHtml) => {
    val reportFile = Path.fromString(outputDir + file.url)
    reportFile.write(
      s"""<?xml version="1.0" encoding="UTF-8"?>
          |<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
          |
          |${Xhtml.toXhtml(file.xml)}""".stripMargin)
    println(s"Output:$lineSeparator${reportFile.toAbsolute.path}")
  }

  protected def writeXml(xml: NodeSeq)(out: Writer): Unit = out.write(Xhtml.toXhtml(xml))
  protected def copyResources(): Unit = copyResourcesOnlyOnce()
}

private object ClairvoyanceHtmlFileWriter {
  private val outputDir = propOrElse("clairvoyance.output.dir", s"$userDir/target/clairvoyance-reports/")
  private[this] val resourcesNotCopied = new AtomicBoolean(true)

  private def copyResourcesOnlyOnce(): Unit = {
    if (resourcesNotCopied.getAndSet(false)) {
      Seq("css", "javascript").foreach(ClasspathResources.copyResource(_, outputDir))
    }
  }
}
