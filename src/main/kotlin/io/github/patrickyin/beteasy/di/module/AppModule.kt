package io.github.patrickyin.beteasy.di.module

import dagger.Module
import dagger.Provides
import io.github.patrickyin.beteasy.horsesListGenerator.HorsesListGenerator
import io.github.patrickyin.beteasy.horsesListGenerator.HorsesListObserver
import io.github.patrickyin.beteasy.horsesListGenerator.HorsesListPipeline
import io.github.patrickyin.beteasy.horsesListGenerator.Processor
import io.github.patrickyin.beteasy.horsesListGenerator.caulfieldrace.CaulfieldRaceDataParser
import io.github.patrickyin.beteasy.horsesListGenerator.InputFileReader
import io.github.patrickyin.beteasy.horsesListGenerator.model.ProcessingData
import io.github.patrickyin.beteasy.horsesListGenerator.wolferhamptonrace.WolferhamptonRaceDataParser
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.dom4j.io.SAXReader

@Module
class AppModule {
  private val jsonSerializer: Json by lazy {
    Json(JsonConfiguration.Stable.copy(strictMode = false))
  }

  private val saxReader: SAXReader by lazy { SAXReader() }

  private val processors: ArrayList<Processor<ProcessingData>> by lazy {
    arrayListOf(
      CaulfieldRaceDataParser(saxReader),
      WolferhamptonRaceDataParser(jsonSerializer)
    )
  }

  @Provides
  internal fun provideInputFileReader(): InputFileReader = InputFileReader()

  @Provides
  internal fun provideHorsesListObserver(): HorsesListObserver = HorsesListObserver()

  @Provides
  internal fun provideProcessors(): ArrayList<Processor<ProcessingData>> = processors

  @Provides
  internal fun provideHorsesListPipeline(fileReader: InputFileReader, processors: ArrayList<Processor<ProcessingData>>): HorsesListPipeline = HorsesListPipeline(fileReader, processors)

  @Provides
  internal fun provideHorsesListGenerator(horsesListPipeline: HorsesListPipeline, horsesListObserver: HorsesListObserver): HorsesListGenerator =
    HorsesListGenerator(horsesListPipeline, horsesListObserver)
}
