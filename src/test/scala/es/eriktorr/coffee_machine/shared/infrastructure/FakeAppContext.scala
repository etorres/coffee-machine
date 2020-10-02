package es.eriktorr.coffee_machine.shared.infrastructure

import es.eriktorr.coffee_machine.AppContext

trait FakeAppContext {
  val appContext: AppContext = AppContext()
}
