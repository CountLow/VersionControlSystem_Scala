package VersionControlSystem

import VersionControlSystem.VCS.vcs

import java.io.{BufferedReader, File, FileInputStream, FileWriter}
import scala.collection.mutable.Stack

/*
  Class containing the main functionality of the Version Control System.
  The parameter 'sourcePath' specifies the root of the repository.
  There are three attributes
      versionHistory is storing the last version
      currentCommit is the latest commit from the versionHistory
      stagingArea is a Set representing the stagingArea
*/
class VCS(val sourcePath : String):
  private var versionHistory : VersionHistory = VersionHistory.loadVersionHistory(sourcePath + "/.vcss")
  private var currentCommit : Commit = if (versionHistory != null) versionHistory.currentCommit else null
  private val stagingArea : collection.mutable.Set[String] = collection.mutable.Set()

  val ignoreFilter : IgnoreFilter = IgnoreFilter.generateFromFile(sourcePath)

  /*
    Creates the folder structure
  */
  def initializeVCS() : Unit =
  {
    new File(sourcePath + "/.vcss").mkdir()

    // Create version history if doesn't already exist
    val versionHistoryFile : File = new File(sourcePath + "/.vcss/versionHistory")
    if(versionHistoryFile.exists())
    {
      println("Repository already initialized!")
      return
    }
    else
    {
      new File(sourcePath + "/.vcss/versionHistory").createNewFile()
      val path : String = sourcePath + "/.vcss"
      versionHistory = VersionHistory(path)

    }

    // Create commit directory
    new File(sourcePath + "/.vcss/commits").mkdir()
    // Create first base commit so commit != null
    val commit: Commit = Commit(Array(), StructureDiff(sourcePath, null), null)
    commit.isHead = true
    Commit.saveToFile(commit, sourcePath + "/.vcss/commits/", commit.identifier)
    // save first commit in version History
    versionHistory.commitChanges(commit)
    VersionHistory.saveVersionHistory(versionHistory, sourcePath + "/.vcss")

    println("Initialized vcss repository.")

    // Create .vcssIgnore
    val vcssIgnore : File = new File(sourcePath + "/.vcss/.vcssIgnore")
    val fileWriter : FileWriter = FileWriter(vcssIgnore)
    fileWriter.write("path : .vcss\npath : VersionControlSystem\npath : VCS_S$.class\npath : VCS_S.class\npath : VCS_S.tasty")
    fileWriter.close()
  }

  /*
    prints the structural differences in the working copy
    e.g. when a file or directory was added
  */
  def status() : Unit =
  {
    if(currentCommit.isHead)
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
  def stage(paths : Array[String]) : Unit =
  {
    for
      path <- paths
    do
      val data = new File(path)

      if(data.exists())
      {
        if(data.isDirectory) // Add all files in directory to staging area
        {
          val unsearched: Stack[File] = Stack(data)
//          unsearched.pushAll(data.listFiles())

          while (unsearched.nonEmpty) {
            if (unsearched.top.isFile && !ignoreFilter.shouldIgnore(unsearched.top.getAbsolutePath))
              stagingArea.add(unsearched.pop.getPath)
            else if (unsearched.top.isDirectory && !ignoreFilter.shouldIgnore(unsearched.top.getAbsolutePath)) {
              stagingArea.add(unsearched.top.getPath)
              unsearched.pushAll(unsearched.pop.listFiles())
            }
            else {
              println("Ignored: " + unsearched.top.getPath)
              unsearched.pop()
            }
          }
        }
        else if(data.isFile && !ignoreFilter.shouldIgnore(data.getAbsolutePath))
        {
          stagingArea.add(data.getPath)
        }
      }

    println("Staged the following files for commit:")
    stagingArea.map(f => println(f))

    saveStagingArea()
  }

  /*
    save changes of current staging area as a commit
  */
  def commitChanges() =
  {
    loadStagingArea()
    println(stagingArea)

    val commitDirectory : String = sourcePath + "/.vcss/commits/"
    var fileDiffs : Array[FileDiff] = Array()

    val dummyStructureDiff : StructureDiff = StructureDiff(sourcePath, null)
    val structureDiff : StructureDiff = StructureDiff.generateDiff(if(currentCommit != null)
                                                                   currentCommit.structureDiff else dummyStructureDiff)

    val commit : Commit = Commit(fileDiffs, structureDiff, currentCommit)
    Commit.saveToFile(commit, commitDirectory, commit.identifier)

    currentCommit = commit
    println(commit.identifier)
    versionHistory.commitChanges(commit)
    VersionHistory.saveVersionHistory(versionHistory, sourcePath + "/.vcss")

    clearStagingArea()
  }

  /*
    Loads the data of a previous commit
  */
  def checkoutVersion(commitName : String) =
  {
    // Maybe implement warning



    // Load commit
    val commit : Commit = versionHistory.getCommitById(commitName)
    println("Commit is: " + commit)
    // Delete and add all changed files
    val structureDiff : StructureDiff = StructureDiff.generateDiff(commit.structureDiff)

    for (dD <- structureDiff.deletedDirectories)
      new File(sourcePath + dD).mkdir()

    for (dF <- structureDiff.deletedFiles)
      new File(sourcePath + dF).createNewFile()

    for (aF <- structureDiff.addedFiles)
      new File(sourcePath + aF).delete()

    for (aD <- structureDiff.addedDirectories)
      new File(sourcePath + aD).delete()


    var fileVersions : Array[List[(Int,Operation,String)]] = commit.fileDiffs.map((x) => x.getChanges())
    //flip Operation
    fileVersions = fileVersions.map((x) => x.map((y) => if (y._2 == Operation.Insertion) (y._1, Operation.Deletion, y._3) else (y._1, Operation.Insertion, y._3)))


  }
  /*
    Saves the staging area as a text document in the vcss directory
  */
  def saveStagingArea() : Unit =
  {
    val file : File = new File(sourcePath + "/.vcss/stagingArea.txt")
    val fileWriter : FileWriter = FileWriter(file)
    fileWriter.write(stagingArea.mkString("\n"))
    fileWriter.close()
  }

  /*
    generate the staging area from the text file
  */
  def loadStagingArea() : Unit =
  {
    stagingArea.clear()
    val bufferedReader : BufferedReader = io.Source.fromFile(sourcePath + "/.vcss/stagingArea.txt").bufferedReader()
    var input: String = bufferedReader.readLine()

    while(input != null)
    {
      stagingArea.add(input)
      input = bufferedReader.readLine()
    }
  }

  /*
    clears the staging area by creating a new empty text file
  */
  def clearStagingArea() : Unit =
  {
    val file: File = new File(sourcePath + "/.vcss/stagingArea.txt")
    val fileWriter: FileWriter = FileWriter(file)
    fileWriter.write("")
    fileWriter.close()
  }

  /*
  DEBUG FEATURE
  */
  def testFeature(args : Array[String]) =
  {
    val commit : Commit = Commit.loadFromFile(sourcePath + "/.vcss/commits/", currentCommit.identifier)
    println(commit)
  }

/*
  called by the main function
*/
object VCS:
  var vcs : VCS = null
  def createVCS(sourcePath : String) : VCS =
  {
    vcs = VCS(sourcePath)
    return vcs
  }