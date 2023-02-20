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

  def saveToFile(path : String, name : String) : Unit =
  {

  }

  def loadFromFile(path : String, name : String) : Unit =
  {

  }


