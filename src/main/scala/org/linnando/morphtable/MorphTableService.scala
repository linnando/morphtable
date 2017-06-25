package org.linnando.morphtable

import angulate2.std._
import rxjs.RxPromise

import scala.scalajs.js

@Injectable
class MorphTableService {
  val elements: js.Array[Element] = @@@(
    Element("Code editor", @@@("Syntax highlighting only", "Tooling information", "Test and debugger integration", "Tooling, test and debugger")),
    Element("Code analyser", @@@("Within compiler, only formal checks", "Within compiler, checks and warnings", "Within compiler, change suggestions", "Standalone, only formal checks", "Standalone, checks and warnings", "Standalone, change suggestions")),
    Element("Compiler", @@@("Machine code, manual implementation", "Bytecode, manual implementation", "Machine code, compiler generator", "Bytecode, compiler generator")),
    Element("Build system", @@@("Tools invocation", "Tools invocation and package management", "Tools invocation, package management and deployment", "Continuous integration")),
    Element("Runtime environment", @@@("Machine code-based", "Existing bytecode-based", "Own bytecode-based, bytecode interpretation", "Own bytecode-based, ahead-of-time compilation", "Own bytecode-based, just-in-time compilation")),
    Element("Test execution system", @@@("Test execution, no remote execution", "Test execution, remote execution", "Test execution and code coverage, no remote execution", "Test execution and code coverage, remote execution")),
    Element("Debugger", @@@("Standalone, single thread debugging", "Standalone, multi-thread debugging", "Debug server, single thread debugging", "Debug server, multi-thread debugging")),
    Element("Version control system", @@@("Centralised storage, no event processing", "Centralised storage, event processing", "Distributed storage, no event processing", "Distributed storage, event processing"))
  )
  var constraint: String = "('Code editor' NOT IN ('Tooling information', 'Tooling, test and debugger') || 'Code analyser' IN ('Standalone, only formal checks', 'Standalone, checks and warnings', 'Standalone, change suggestions')) && !('Code editor' IN ('Test and debugger integration', 'Tooling, test and debugger') && 'Debugger' IN ('Standalone, single thread debugging', 'Standalone, multi-thread debugging')) && (('Compiler' IN ('Machine code, manual implementation', 'Machine code, compiler generator') && 'Runtime environment' = 'Machine code-based') || ('Compiler' IN ('Bytecode, manual implementation', 'Bytecode, compiler generator') && 'Runtime environment' != 'Machine code-based')) && ('Build system' != 'Continuous integration' || ('Test execution system' IN ('Test execution, remote execution', 'Test execution and code coverage, remote execution') && 'Version control system' IN ('Centralised storage, event processing', 'Distributed storage, event processing')))"

  def getElements: RxPromise[js.Array[Element]] = RxPromise.resolve(elements.jsSlice())

  def addElement(elementName: String): RxPromise[Element] = {
    val element = Element(elementName, @@@())
    elements.push(element)
    RxPromise.resolve(element)
  }

  def removeElement(elementIndex: Int): RxPromise[Unit] = {
    elements.splice(elementIndex, 1)
    RxPromise.resolve(Unit)
  }

  def addOption(elementIndex: Int, option: String): RxPromise[Element] = {
    elements(elementIndex).options.push(option)
    RxPromise.resolve(elements(elementIndex))
  }

  def removeOption(elementIndex: Int, optionIndex: Int): RxPromise[Element] = {
    elements(elementIndex).options.splice(optionIndex, 1)
    RxPromise.resolve(elements(elementIndex))
  }

  def getConstraint: RxPromise[String] = RxPromise.resolve(constraint)
}
