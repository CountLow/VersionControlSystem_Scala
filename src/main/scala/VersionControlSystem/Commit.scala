package VersionControlSystem

import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}

/*
  Class that represents the changes between two versions.
  Each files change is stored as a corresponding instance of 'Diff'.
*/
class Commit(val fileDiffs : Array[FileDiff], val structureDiff : StructureDiff,
             val previousCommit : Commit = null) extends Serializable:

  val commitNumberOnBranch : Int = if(previousCommit == null) 0 else previousCommit.commitNumberOnBranch + 1
  val identifier : String = Commit.generateIdentifier(this)
  var isHead : Boolean = false

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


object Commit {
  def saveToFile(commit : Commit, sourcePath: String, name: String): Unit = {
    val path = sourcePath + name + ".commit"
    val fOS: FileOutputStream = new FileOutputStream(path)
    val oOS: ObjectOutputStream = new ObjectOutputStream(fOS)
    oOS.writeObject(commit)
    oOS.close()
  }

  def loadFromFile(sourcePath: String, name: String): Commit = {
    val path = sourcePath + "/" + name + ".commit"
    val fIS: FileInputStream = new FileInputStream(path)
    val oIS: ObjectInputStream = new ObjectInputStream(fIS)
    val data: Commit = oIS.readObject().asInstanceOf[Commit]
    oIS.close()

    data
  }

  def generateIdentifier(commit : Commit) : String =
  {
//    val branch : String = VersionHistory.getBranch(commit)
    return commit.commitNumberOnBranch.toHexString
  }
}
