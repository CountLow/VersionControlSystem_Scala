import VersionControlSystem.VCS
import VersionControlSystem.Diff

@main
def main(): Unit = {
  val vcs : VCS = VCS()
  println("VCS started!")

  val fileNameA = "/code1.scala"
  val fileNameB = "/code2.scala"
//  val fileNameA = "/Text1.txt"
//  val fileNameB = "/Text2.txt"
//  val fileNameA = "/img1.jpg"
//  val fileNameB = "/img2.jpg"

  val pathA: String = getClass.getResource(fileNameA).getFile
  val pathB: String = getClass.getResource(fileNameB).getFile

  val result : Diff = vcs.generateDiffForFile(pathA, pathB)
  print(result.getChanges())
}