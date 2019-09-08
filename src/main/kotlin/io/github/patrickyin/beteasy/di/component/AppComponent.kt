package io.github.patrickyin.beteasy.di.component

import dagger.Component
import io.github.patrickyin.beteasy.App
import io.github.patrickyin.beteasy.di.module.AppModule

@Component(modules = [AppModule::class])
interface AppComponent {
  fun inject(app: App)
}
