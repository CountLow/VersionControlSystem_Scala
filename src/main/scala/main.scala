import VersionControlSystem.VCS

object VCS_S {
  def main(args: Array[String]) : Unit = {
    val path : String = System.getProperty("user.dir")
    val vcs : VCS = VCS(path)

    if (args.length == 0)
      printHelp()

    else args(0) match
      case "init" => vcs.initializeVCS()
      case "path" => println(path)
      case "testFeature" => vcs.testFeature(args.drop(1))
      case "stage" => vcs.stage(args.drop(1))
      case "status" => vcs.status()
      case _ => printHelp()
  }

  private def printHelp() : Unit = {
    println("Possible commands:")
    println("   init                            Initialize vcss for this folder")
    println("   diff [PATH]                     Show the differences for a file")
    println("   stage [PATH_A] [PATH_B] [...]   Stage specified files for commit")
    println("   commit                          Commit all staged changes to create a new version")
    println("   ")
  }
}