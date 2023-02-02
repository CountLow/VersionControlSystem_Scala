import VersionControlSystem.VCS
@main
def main(): Unit = {
  val vcsaa : VCS = VCS()
  println("VCS started!")

  // vcs.generateDiffForFile("/Text1.txt", "/Text2.txt") More Comment
  vcs.generateDiffForFile("/code1.scala", "/code2.scala")
  // Additional line
}
// HUUGEE
// Overhead