package VersionControlSystem

import VersionControlSystem.VersionHistory.saveVersionHistory

import java.io.{File, FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}

/*
  Contains information about branches
*/
class VersionHistory(filePath : String) extends Serializable:
  var currentCommit : Commit = null
  private var commits : Array[Commit] = Array()

  def commitChanges(commit : Commit) = {
    currentCommit = commit
    commits = commits :+ commit
    saveVersionHistory(this, filePath)
  }

  def getCommitById(identifier : String) : Commit =
  {
    val matches : Array[Commit] = commits.filter(c => c.identifier == identifier)
    if(matches.nonEmpty) matches(0) else null
  }


object VersionHistory:
  def loadVersionHistory(vcssPath : String) : VersionHistory = {
    if(!new File(vcssPath + "/versionHistory").exists()) {
      println("Version History not found")
      return null
    }

    val fIS : FileInputStream = FileInputStream(vcssPath + "/versionHistory")
    val oIS : ObjectInputStream = ObjectInputStream(fIS)
    oIS.readObject.asInstanceOf[VersionHistory]
  }

  def saveVersionHistory(versionHistory : VersionHistory, vcssPath : String) : Unit =
  {
    val fOS : FileOutputStream = FileOutputStream(vcssPath + "/versionHistory")
    val oOS : ObjectOutputStream = ObjectOutputStream(fOS)
    oOS.writeObject(versionHistory)
  }



