package org.specs2.clairvoyance.plugins

import org.specs2.clairvoyance.Imports._

object CapturedValues {
  def collectCollaborators(capturedValues: KeyValueSequence, defaultActor: String) = {
    capturedValues.map(toCapturedValueCollaborators(defaultActor)).flatten
  }

  val fullyQualifiedMessageSend = """(?i)(.*) from (.*) to (.*)""".r
  val messageSendWithDefaultReceiver = """(?i)(.*) from (.*)""".r
  val messageSendWithDefaultSender = """(?i)(.*) to (.*)""".r

  def toCapturedValueCollaborators(defaultActor: String): (KeyValue) => Option[CapturedValueCollaborators] = {
    case (capturedValueKey, value) =>
      capturedValueKey match {
        case fullyQualifiedMessageSend(what, from, to) => Some(CapturedValueCollaborators(capturedValueKey, from, to, what, value))
        case messageSendWithDefaultReceiver(what, from) => Some(CapturedValueCollaborators(capturedValueKey, from, defaultActor, what, value))
        case messageSendWithDefaultSender(what, to) => Some(CapturedValueCollaborators(capturedValueKey, defaultActor, to, what, value))
        case _ => None
      }
  }
}

case class CapturedValueCollaborators(key: String, from: String, to: String, what: String, rawMessage: Any) {
  def id = key.replaceAll(" ", "_").replaceAll("\\(", "_").replaceAll("\\)", "_")
}

