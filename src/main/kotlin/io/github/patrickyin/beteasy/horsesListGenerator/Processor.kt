package io.github.patrickyin.beteasy.horsesListGenerator

import io.reactivex.Single

interface Processor<T> {
  fun process(input: T): Single<T>
}
