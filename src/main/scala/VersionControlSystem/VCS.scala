package VersionControlSystem

import VersionControlSystem.VCS.vcs

import java.io.{BufferedReader, File, FileInputStream, FileWriter}
import scala.collection.mutable.Stack

/*
  Class containing the main functionality of the Version Control System.
  The parameter 'sourcePath' specifies the root of the repository.
*/
class VCS(val sourcePath : String):
  private val versionHistory : VersionHistory = VersionHistory.loadVersionHistory(sourcePath + "/.vcss")
  private var currentCommit : Commit = if (versionHistory != null) versionHistory.currentCommit else null
  private val stagingArea : collection.mutable.Set[String] = collection.mutable.Set()

  val ignoreFilter : IgnoreFilter = IgnoreFilter.generateFromFile(sourcePath)

  /*
    Creates the folder structure
  */
  def initializeVCS() : Unit =
  {
    // Create version history if doesn't already exist
    val versionHistoryFile : File = new File(sourcePath + "/.vcss/versionHistory")
    if(versionHistoryFile.exists())
    {
      println("Repository already initialized!")
      return
    }

    // Create commit directory
    new File(sourcePath + "/.vcss/commits").mkdirs()

    println("Initialized vcss repository.")

    // Create .vcssIgnore
    val vcssIgnore : File = new File(sourcePath + "/.vcss/.vcssIgnore")
    val fileWriter : FileWriter = FileWriter(vcssIgnore)
    fileWriter.write("path : .vcss\npath : VersionControlSystem\npath : VCS_S$.class\npath : VCS_S.class\npath : VCS_S.tasty")
    fileWriter.close()
  }

  /*

  */
  def status() : Unit =
  {
    if(currentCommit == null)
    {
      println("Not commited anything yet")

//      val fileDiffs: List[FileDiff] =
      val dummyStructureDiff : StructureDiff = StructureDiff(sourcePath, null)
      val structureDiff: StructureDiff = StructureDiff.generateDiff(dummyStructureDiff)

      print(structureDiff.getString())
    }
    else {
      println("Current commit: " + currentCommit.identifier)
      val fileDiffs: Array[FileDiff] = currentCommit.fileDiffs
      val structureDiff: StructureDiff = StructureDiff.generateDiff(currentCommit.structureDiff)
      StructureDiff.generateDiff(currentCommit.structureDiff)

      print(structureDiff.getString())
    }
  }

  /*
    Adds all changed files which are either directly specified or within a specified directory to the
    staging area.
  */
  def stage(ps : Array[String]) : Unit =
  {
    var paths : Array[String] = ps

    if(paths(0) == "*") // Add all
      paths = new File(sourcePath).listFiles().map(f => f.getAbsolutePath)

    for
      path <- paths
    do
      val data = new File(path)

      if(data.exists())
      {
        if(data.isDirectory) // Add all files in directory to staging area
        {
          val unsearched: Stack[File] = Stack(data)
          while (unsearched.nonEmpty) {
            if (unsearched.top.isFile)
              stagingArea.add(unsearched.pop.getPath)
            else if (unsearched.top.isDirectory) {
              unsearched.pushAll(unsearched.pop.listFiles())
            }
            else
              unsearched.pop()
          }
        }
        else if(data.isFile)
        {
          stagingArea.add(data.getPath)
        }
      }

      println("Staged the following files for commit:")
      println(stagingArea)
  }

  /*

  */
  def commitChanges() =
  {
    val commitDirectory : String = sourcePath + "/.vcss/commits/"
    var fileDiffs : Array[FileDiff] = Array()

    for(path <- stagingArea) {
      val fileDiff : FileDiff = FileDiff(path, if(currentCommit != null )currentCommit.getDiffForFile(path) else null)
      fileDiff.generateDiff()
      fileDiffs = fileDiffs :+ fileDiff
    }

    val dummyStructureDiff : StructureDiff = StructureDiff(sourcePath, null)
    val structureDiff : StructureDiff = StructureDiff.generateDiff(if(currentCommit != null)
                                                                   currentCommit.structureDiff else dummyStructureDiff)

    val commit : Commit = Commit(fileDiffs, structureDiff, currentCommit)
    Commit.saveToFile(commit, commitDirectory, commit.identifier)
  }

  /*
    Loads the data of a previous commit
  */
  def checkoutVersion(commitName : String) =
  {
    // Maybe implement warning


  }

  /*
  DEBUG FEATURE
  */
  def testFeature(args : Array[String]) =
  {
    val commit : Commit = Commit.loadFromFile(sourcePath + "/.vcss/commits/", currentCommit.identifier)
    println(commit)
  }


object VCS:
  var vcs : VCS = null
  def createVCS(sourcePath : String) : VCS =
  {
    vcs = VCS(sourcePath)
    return vcs
  }