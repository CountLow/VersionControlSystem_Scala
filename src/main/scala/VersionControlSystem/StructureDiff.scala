package VersionControlSystem

import java.io.*
import collection.mutable.Stack
import collection.mutable.Set

/*
  Represents changes in folder structure.
*/
class StructureDiff(val sourcePath : String, val previousDiff : StructureDiff = null) extends Serializable {
  private var addedFiles : List[String] = List()
  private var deletedFiles : List[String] = List()

  private var addedDirectories : List[String] = List()
  private var deletedDirectories : List[String] = List()

  /*

  */
  def getString() : String =
  {
    val sB : StringBuilder = StringBuilder("")
    sB ++= addedDirectories.map(f => "\tAdded:\t\t" + f).mkString("\n")
    sB ++= deletedDirectories.map(f => "\tDeleted:\t\t" + f).mkString("\n")
    sB ++= addedFiles.map(f => "\tAdded:\t\t" + f).mkString("\n")
    sB ++= deletedFiles.map(f => "\tDeleted:\t\t" + f).mkString("\n")
    sB.toString()
  }

}

object StructureDiff:
  /*
    Generates the version of a StructureDiff.
    Then the difference to the current state is generated and saved.
  */
  def generateDiff(baseDiff: StructureDiff): StructureDiff = {
    val directory: File = new File(baseDiff.sourcePath)
    val childrenFilePaths: Set[String] = Set()
    val childrenDirectoryPaths: Set[String] = Set()
    val (currentFiles, currentDirectories): (Set[String], Set[String]) = StructureDiff.generateVersion(baseDiff)

    // Find all subdirectories and their files and add them with DFS
    val unsearched: Stack[File] = Stack(directory)
    while (unsearched.nonEmpty) {
      if (unsearched.top.isFile)
        childrenFilePaths.add(unsearched.pop.getPath)
      else if(unsearched.top.getPath == baseDiff.sourcePath)
        unsearched.pop()
      else if (unsearched.top.isDirectory && unsearched.top.getPath != baseDiff.sourcePath + "\\.vcss")  // Use IgnoreFilter
      {
        childrenDirectoryPaths.add(unsearched.top.getPath)
        unsearched.pushAll(unsearched.pop.listFiles())
      }
      else
        unsearched.pop()
    }

    val structureDiff : StructureDiff = StructureDiff(baseDiff.sourcePath, baseDiff)

    // Save changes in this diff for all directories
    structureDiff.addedFiles = childrenFilePaths.toList.diff(currentFiles.toList)
    structureDiff.addedDirectories = childrenDirectoryPaths.toList.diff(currentDirectories.toList)
    structureDiff.deletedFiles = currentFiles.toList.diff(childrenFilePaths.toList)
    structureDiff.deletedDirectories = currentDirectories.toList.diff(childrenDirectoryPaths.toList)

    return structureDiff
  }

  /*
    Generates version x by applying all previous structureDiff on base version (empty).
    Version is:
      currentFiles
      currentDirectories
  */
  def generateVersion(baseDiff: StructureDiff): (Set[String], Set[String]) = {
    val currentFiles: Set[String] = Set()
    val currentDictionaries: Set[String] = Set()
    val stack: Stack[StructureDiff] = Stack()
    var currentDiff: StructureDiff = baseDiff

    if (currentDiff == null) // Early termination
      return (currentFiles, currentDictionaries)

    while (currentDiff != null) {
      stack.push(currentDiff)
      currentDiff = currentDiff.previousDiff
    }

    currentDiff = stack.pop
    while (currentDiff != null) {
      for (aD <- currentDiff.addedDirectories)
        currentDictionaries.add(aD)

      for (dD <- currentDiff.deletedDirectories)
        currentDictionaries.remove(dD)

      for (aF <- currentDiff.addedFiles)
        currentFiles.add(aF)

      for (dF <- currentDiff.deletedFiles)
        currentFiles.remove(dF)

      currentDiff = if (stack.nonEmpty) stack.pop else null
    }

    return (currentFiles, currentDictionaries)
  }