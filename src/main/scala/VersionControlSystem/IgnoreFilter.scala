package VersionControlSystem

import java.io.{BufferedReader, File}
import java.nio.file.{Path, Paths}
import collection.mutable

/*
  Contains paths to elements that should be ignored
*/

class IgnoreFilter(val paths : Array[String], val fileTypes : Array[String]):
  def shouldIgnore(path : String) : Boolean =
  {
    var ignored : Boolean = false

    // Test if ignored because of paths
    for(p <- paths)
    {
      val possibleParentPath : Path = Paths.get(p).normalize()
      val testPath : Path = Paths.get(path).normalize()

      if(testPath.startsWith(possibleParentPath))
        ignored = true
    }

    return ignored
  }


object IgnoreFilter:
  def generateFromFile(sourcePath : String) : IgnoreFilter =
  {
    if(!new File(sourcePath + "/.vcss/.vcssIgnore").exists())
      return null

    val bufferedReader : BufferedReader = io.Source.fromFile(sourcePath + "/.vcss/.vcssIgnore").bufferedReader()
    var line : String = bufferedReader.readLine()

    val fileTypes : mutable.Set[String] = mutable.Set("")
    val paths : mutable.Set[String] = mutable.Set()

    while(line != null)
    {
      val splittedString : Array[String] = line.split(" : ")
      splittedString(0) match
        case "fileType" => fileTypes.add(sourcePath + "/" + splittedString(1))
        case "path" => paths.add(sourcePath + "/" + splittedString(1))
        case _ => println("Cant find type")

      line = bufferedReader.readLine()
    }

    return IgnoreFilter(paths.toArray, fileTypes.toArray)
  }