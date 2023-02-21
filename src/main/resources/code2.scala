@main
def main(): Unit = {
  val vcs: VCS = VCS()
  //Addition

  vcs.generateDiffForFile("/Text1.txt", "/Text2.txt")
  vcs.generateDiffForFile("/code1.scala", "/code2.scala") // Change

  print("1")
  print("2")
}
// Final addition