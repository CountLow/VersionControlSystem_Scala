import VersionControlSystem.VCS
import VersionControlSystem.Diff

object VCS_S {
  def main(args: Array[String]) = {
    if (args.length == 0)
      println("Possible commands:")
    else if (args.length == 3){
      // Test setup for creating and applying diff
      val pathA : String = args(0)
      val pathB : String = args(1)
      val pathC : String = args(2)

      val vcs: VCS = VCS()
      val result: Diff = vcs.generateDiffForFile(pathA, pathB)
      print(result.getString())

      vcs.applyDiffOnFile(pathC, result)
    }
    else
      println("Enter sourceA, sourceB, destA as arguments")
  }
}