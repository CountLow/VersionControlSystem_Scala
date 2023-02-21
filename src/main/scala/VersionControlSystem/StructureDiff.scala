package VersionControlSystem

import java.io.*
import collection.mutable.Stack
import collection.mutable.Set
import scala.collection.Searching

class StructureDiff(val previousDiff : StructureDiff = null) {
  private var addedFiles : List[String] = List()
  private var deletedFiles : List[String] = List()

  private var addedDirectories : List[String] = List()
  private var deletedDirectories : List[String] = List()

  /*
    Generates the version of StructureDiff 'previousDiff'.
    Then the difference to the current state is generated and saved.
  */
  def generateDiff(path : String) =
  {
    val directory : File = new File(path)
    val childrenFilePaths : Set[String] = Set()
    val childrenDirectoryPaths : Set[String] = Set()
    val (currentFiles, currentDirectories) : (Set[String], Set[String]) = generateVersion(previousDiff)

    // Find all subdirectories and their files and add them with DFS
    val unsearched : Stack[File] = Stack(directory)
    while(unsearched.nonEmpty)
    {
      if(unsearched.top.isFile)
        childrenFilePaths.add(unsearched.pop.getPath)
      else if(unsearched.top.isDirectory)
      {
        childrenDirectoryPaths.add(unsearched.top.getPath)
        unsearched.pushAll(unsearched.pop.listFiles())
      }
      else
        unsearched.pop()
    }

    // Save changes in this diff for all directories
    addedFiles = childrenFilePaths.toList.diff(currentFiles.toList)
    addedDirectories = childrenDirectoryPaths.toList.diff(currentDirectories.toList)

    deletedFiles = currentFiles.toList.diff(childrenFilePaths.toList)
    deletedDirectories = currentDirectories.toList.diff(childrenDirectoryPaths.toList)

  }

  /*
    Generates version x by applying all previous structureDiff on base version (empty).
    Version is:
      currentFiles
      currentDirectories
  */
  def generateVersion(latestDiff : StructureDiff) : (Set[String], Set[String]) =
  {
    val currentFiles : Set[String] = Set()
    val currentDictionaries : Set[String] = Set()
    val stack : Stack[StructureDiff] = Stack()
    var currentDiff : StructureDiff = latestDiff

    if(currentDiff == null) // Early termination
      return (currentFiles, currentDictionaries)

    while(currentDiff != null)
    {
      stack.push(currentDiff)
      currentDiff = currentDiff.previousDiff
    }

    currentDiff = stack.pop
    while(currentDiff != null)
    {
      for(aD <- currentDiff.addedDirectories)
        currentDictionaries.add(aD)

      for (dD <- currentDiff.deletedDirectories)
        currentDictionaries.remove(dD)

      for(aF <- currentDiff.addedFiles)
        currentFiles.add(aF)

      for(dF <- currentDiff.deletedFiles)
        currentFiles.remove(dF)

      currentDiff = if (stack.nonEmpty) stack.pop else null
    }

    (currentFiles, currentDictionaries)
  }
}
