package VersionControlSystem

import java.io.BufferedReader
import collection.mutable

/*
  Contains paths to elements that should be ignored
*/

class IgnoreFilter(val paths : Array[String], val fileTypes : Array[String])


object IgnoreFilter:
  def generateFromFile(vcssPath : String) : Unit =
  {
    val bufferedReader : BufferedReader = io.Source.fromFile(vcssPath + "/.vcssIgnore").bufferedReader()
    var line : String = bufferedReader.readLine()

    val fileTypes : mutable.Set[String] = mutable.Set("")
    val paths : mutable.Set[String] = mutable.Set(".vcss")

    while(line != null)
    {
      val splittedString : Array[String] = line.split(" : ")
      splittedString(0) match
        case "fileType" => fileTypes.add(splittedString(1))
        case "path" => paths.add(splittedString(1))
        case _ => println("Cant find type")

      line = bufferedReader.readLine()
    }

    return IgnoreFilter(paths.toArray, fileTypes.toArray)
  }