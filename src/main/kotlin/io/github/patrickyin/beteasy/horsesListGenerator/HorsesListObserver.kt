package io.github.patrickyin.beteasy.horsesListGenerator

import io.github.patrickyin.beteasy.horsesListGenerator.model.Horse
import io.reactivex.observers.DisposableSingleObserver

class HorsesListObserver() : DisposableSingleObserver<List<Horse>>() {
  override fun onSuccess(list: List<Horse>) {
    list.sortedBy { it.price }
      .map { it.name }
      .forEach { println(it) }
  }

  override fun onError(e: Throwable) {
    e.printStackTrace()
  }
}
