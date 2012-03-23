package clairvoyance.examples

import org.specs2.clairvoyance.{ProducesCapturedInputsAndOutputs, ClairvoyantSpec}
import org.specs2.clairvoyance.plugins.SequenceDiagram
import org.specs2.execute.Success

class SequenceDiagramExample extends ClairvoyantSpec {

  "The Web Site" should {
    "authenticate the user using LDAP" in new context {
      whenTheUserLogsInToTheWebSiteUsingTheCredentials("mario", "luigi")
      thenTheUserIsShownSecrets()
    }
  }

  trait context extends ClairvoyantContext with SequenceDiagram {
    val ldap = new Ldap
    val webServer = new WebServer(ldap)

    def whenTheUserLogsInToTheWebSiteUsingTheCredentials(user: String, password: String) {
      webServer.login(user, password)
    }

    def thenTheUserIsShownSecrets() {
      Success
    }

    override def capturedInputsAndOutputs = Seq(webServer, ldap)
  }

  class WebServer(ldap: Ldap) extends ProducesCapturedInputsAndOutputs {
    def login(user: String, password: String) {
      captureValue("Login from User to WebServer" -> "user: %s, password: %s".format(user, password))
      ldap.authenticate(user, password)
      captureValue("Response from WebServer to User" -> "Forwarding to showSecrets.html")
    }
  }

  class Ldap extends ProducesCapturedInputsAndOutputs {
    def authenticate(user: String, password: String) {
      captureValue("Username and Password from WebServer to LDAP" -> "user: %s, password: %s".format(user, password))
      // do something
      captureValue("User's Credentials from LDAP to WebServer" -> "user")
    }
  }

}