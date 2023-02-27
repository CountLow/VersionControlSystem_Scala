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

  /*
    saves the commit in the specified path with its specified name (numbers)
  */
  def saveToFile(commit : Commit, sourcePath: String, name: String): Unit = {
    val path = sourcePath + name + ".commit"
    val fOS: FileOutputStream = new FileOutputStream(path)
    val oOS: ObjectOutputStream = new ObjectOutputStream(fOS)
    oOS.writeObject(commit)
    oOS.close()
  }

  /*
    loads the commit from its path and name
  */
  def loadFromFile(sourcePath: String, name: String): Commit = {
    val path = sourcePath + "/" + name + ".commit"
    val fIS: FileInputStream = new FileInputStream(path)
    val oIS: ObjectInputStream = new ObjectInputStream(fIS)
    val data: Commit = oIS.readObject().asInstanceOf[Commit]
    oIS.close()

    data
  }

  /*
    Generates from the number of the commit a string
  */
  def generateIdentifier(commit : Commit) : String =
  {
    commit.commitNumberOnBranch.toHexString
  }
}
