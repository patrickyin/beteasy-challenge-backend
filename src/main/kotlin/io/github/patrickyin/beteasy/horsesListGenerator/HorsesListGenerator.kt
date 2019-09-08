package io.github.patrickyin.beteasy.horsesListGenerator

import io.github.patrickyin.beteasy.horsesListGenerator.model.Horse
import io.reactivex.SingleObserver
import io.reactivex.schedulers.Schedulers
import java.io.File
import javax.inject.Inject

class HorsesListGenerator @Inject constructor(
  private val horsesListPipeline: HorsesListPipeline,
  private val horsesListObserver: SingleObserver<List<Horse>>
) {
  fun generateFrom(files: List<File>) {
    horsesListPipeline
      .pipeline
      .subscribeOn(Schedulers.trampoline())
      .subscribe(horsesListObserver)

    files.forEach { horsesListPipeline.producer.onNext(it) }

    horsesListPipeline.producer.onComplete()
  }
}
