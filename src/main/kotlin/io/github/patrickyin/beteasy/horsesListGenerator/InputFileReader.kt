package io.github.patrickyin.beteasy.horsesListGenerator

import io.github.patrickyin.beteasy.horsesListGenerator.model.ProcessingData
import io.reactivex.Single
import java.io.File
import java.io.FileNotFoundException

class InputFileReader {
  fun read(file: File): Single<ProcessingData> =
    Single.create { emitter ->
      if (!file.exists()) {
        emitter.onError(FileNotFoundException())
        return@create
      }
      val content = file.readText() // Default charset is UTF_8
      emitter.onSuccess(content to emptyList())
    }
}
