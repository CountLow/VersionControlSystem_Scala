package VersionControlSystem

/*
  Class that represents the changes between two versions.
  Each files change is saved as a corresponding instance of 'Diff'.
*/
class Commit:
  private val directoryPath : String = ""
  private val diffs : List[Diff] = List()

  def applyCommit() : Unit =
  {

  }

  def applyDiff() : Unit =
  {

  }

  def saveToFile(sourcePath : String, name : String) : Unit =
  {

  }

  def loadFromFile(sourcePath : String, name : String) : Unit =
  {

  }


