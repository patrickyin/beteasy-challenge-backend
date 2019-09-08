package io.github.patrickyin.beteasy

import io.github.patrickyin.beteasy.di.component.DaggerAppComponent
import io.github.patrickyin.beteasy.horsesListGenerator.HorsesListGenerator
import java.io.File
import javax.inject.Inject

class App {
  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      App().runWithInputFile(args)
    }
  }

  @Inject
  lateinit var horsesListGenerator: HorsesListGenerator

  // DI initialization
  init {
    DaggerAppComponent.create().inject(this)
  }

  // File input
  fun runWithInputFile(fileNames: Array<String>) {
    val files = fileNames.map { File(it) }
    horsesListGenerator.generateFrom(files)
  }
}
