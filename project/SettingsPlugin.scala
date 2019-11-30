import sbt._
import sbt.Keys._
import sbt.PluginTrigger.AllRequirements

object SettingsPlugin extends AutoPlugin {
  override def trigger = AllRequirements

  override def projectSettings = List(
    // scaladoc enhancements
    scalacOptions in (Compile, doc) ++= Seq(
      "-groups",  // enable support for grouped members
      "-diagrams" // generate type hierarchy diagrams
    )
  )
}
