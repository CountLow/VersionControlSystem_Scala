import VersionControlSystem.VCS
@main
def main(): Unit = {
  val vcs : VCS = VCS()
  println("VCS started!")

 // vcs.generateDiffForFile("/Text1.txt", "/Text2.txt")
 // vcs.generateDiffForFile("/img1.jpg", "/img2.jpg")
  vcs.generateDiffForFile("/code1.scala", "/code2.scala")
}