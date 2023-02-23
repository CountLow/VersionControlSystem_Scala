package VersionControlSystem

/*
  Class that represents the changes between two versions.
  Each files change is stored as a corresponding instance of 'Diff'.
*/
class Commit(val fileDiffs : Array[FileDiff], val structureDiff : StructureDiff,
             val previousCommit : Commit = null) extends Serializable:

  def applyCommit() : Unit =
  {

  }

  /*

  */
  def getDiffForFile(path : String) : FileDiff =
  {
    val option: Option[FileDiff] = fileDiffs.find(f => f.sourcePath == path)
    if(option.nonEmpty) option.get else null
  }


