import sbt.*
import sbt.Keys.*
import sbt.PluginTrigger.AllRequirements

object SettingsPlugin extends AutoPlugin {
  override def trigger = AllRequirements

  override def projectSettings =
    List(
      // scaladoc enhancements
      Compile / doc / scalacOptions ++= Seq(
        "-groups",  // enable support for grouped members
        "-diagrams" // generate type hierarchy diagrams
      )
    )
}
