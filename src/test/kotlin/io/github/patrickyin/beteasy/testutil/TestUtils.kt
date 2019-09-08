package io.github.patrickyin.beteasy.testutil

import java.io.File

object TestUtils {
  fun readTestingFile(fileName: String) = getResourceFile(fileName).readText()

  fun getResourceFile(fileName: String) = File(javaClass.classLoader.getResource(fileName).file)
}
