package VersionControlSystem

import VersionControlSystem.VersionHistory.saveVersionHistory

import java.io.{File, FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}

/*
  Contains information about branches
  The parameter 'file path' specifies the location of the recent versionHistory
  There are two attributes
      currenCommit saves the current Commit
      commits saves every commit in an Array
*/
class VersionHistory(filePath : String) extends Serializable:
  var currentCommit : Commit = null
  private var commits : Array[Commit] = Array()

  /*
    sets the new current Commit and adds the new Commit to the array
  */
  def commitChanges(commit : Commit) = {
    currentCommit = commit
    commits = commits :+ commit
    saveVersionHistory(this, filePath)
  }

  /*
    filters the commits array to find the commit with the spcified identifier
  */
  def getCommitById(identifier : String) : Commit =
  {
    val matches : Array[Commit] = commits.filter(c => c.identifier == identifier)
    if(matches.nonEmpty) matches(0) else null
  }


object VersionHistory:
  /*
    loads the last versionHistory if the specified path exists
  */
  def loadVersionHistory(vcssPath : String) : VersionHistory = {
    if(!new File(vcssPath + "/versionHistory").exists()) {
      println("Version History not found")
      return null
    }

    val fIS : FileInputStream = FileInputStream(vcssPath + "/versionHistory")
    val oIS : ObjectInputStream = ObjectInputStream(fIS)
    oIS.readObject.asInstanceOf[VersionHistory]
  }

  /*
    saves the new versionHistory in the specified path
  */
  def saveVersionHistory(versionHistory : VersionHistory, vcssPath : String) : Unit =
  {
    val fOS : FileOutputStream = FileOutputStream(vcssPath + "/versionHistory")
    val oOS : ObjectOutputStream = ObjectOutputStream(fOS)
    oOS.writeObject(versionHistory)
  }



