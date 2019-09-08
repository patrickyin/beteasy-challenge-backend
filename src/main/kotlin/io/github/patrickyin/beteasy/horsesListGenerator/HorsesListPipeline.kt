package io.github.patrickyin.beteasy.horsesListGenerator

import io.github.patrickyin.beteasy.horsesListGenerator.model.Horse
import io.github.patrickyin.beteasy.horsesListGenerator.model.ProcessingData
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.io.File
import javax.inject.Inject

class HorsesListPipeline @Inject constructor(
  private val fileReader: InputFileReader,
  private val processors: Collection<Processor<ProcessingData>>
) {
  val producer: Subject<File> = PublishSubject.create()

  val pipeline: Single<List<Horse>>
    get() = processors
      .fold(producer.flatMapSingle { fileReader.read(it) }, ::attachProcessor)
      .flatMapIterable { it.second }
      .toList()

  private fun <T> attachProcessor(observable: Observable<T>, processor: Processor<T>): Observable<T> =
    observable.flatMapSingle { processor.process(it) }
}
