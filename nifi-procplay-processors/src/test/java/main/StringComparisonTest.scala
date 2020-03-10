package main

import org.apache.nifi.flowfile.FlowFile
import org.apache.nifi.util.MockFlowFile
import org.apache.nifi.util.TestRunner
import org.apache.nifi.util.TestRunners
import org.junit.Before
import org.junit.Test

import scala.collection.JavaConverters._
import scala.collection.mutable

class StringComparisonTest {

	private var testRunner: TestRunner = _

	@Before
	def init(): Unit = {
		testRunner = TestRunners.newTestRunner(classOf[StringComparisonProcessor])
	}

	@Test
	def equalStringsAreEqual(): Unit = {
		val first = "abc"
		val second = "abc"

		val result = createAndTest(first, second)

		assert(result._1.size == 1)
		assert(result._2.isEmpty)
	}

	@Test
	def unequalStringsAreUnequal(): Unit = {
		val first = "abc"
		val second = "bca"

		val result = createAndTest(first, second)

		assert(result._1.isEmpty)
		assert(result._2.nonEmpty)
	}

	@Test
	def bomStripped(): Unit = {
		val first = "abc"
		val second = "abc"  + "\uFEFF"

		val result = createAndTest(first, second)

		assert(result._1.nonEmpty)
		assert(result._2.isEmpty)
	}

	@Test
	def bomStrippedAtMiddle(): Unit = {
		val first = "abc"
		val second = "abc".patch(1, "\uFEFF", 0)

		val result = createAndTest(first, second)

		assert(result._1.nonEmpty)
		assert(result._2.isEmpty)
	}

	@Test
	def whitespaceRemoved(): Unit = {
		val first = "abc"
		val second = "abc   "

		val result = createAndTest(first, second)

		assert(result._1.nonEmpty)
		assert(result._2.isEmpty)
	}

	@Test
	def whiteSpaceRemoved2(): Unit = {
		val first = "    abc "
		val second = "   abc   "

		val result = createAndTest(first, second)

		assert(result._1.nonEmpty)
		assert(result._2.isEmpty)
	}

	@Test
	def spaceInMiddleNotRemoved(): Unit = {
		val first = "abc"
		val second = "a bc"

		val result = createAndTest(first, second)

		assert(result._1.isEmpty)
		assert(result._2.nonEmpty)
	}

	@Test
	def whitespaceAndBom(): Unit = {
		val first = "abc"
		val second = " abc" + "\uFEFF"

		val result = createAndTest(first, second)

		assert(result._1.nonEmpty)
		assert(result._2.isEmpty)
	}

	@Test
	def whitespaceAndBom2(): Unit = {
		val first = " abc   "
		val second = " abc" + "\uFEFF"

		val result = createAndTest(first, second)

		assert(result._1.nonEmpty)
		assert(result._2.isEmpty)
	}

	@Test
	def whitespaceAndBom3(): Unit = {
		val first = " abc   "
		val second = " abc" + "\uFEFF"

		val result = createAndTest(first, second)

		assert(result._1.nonEmpty)
		assert(result._2.isEmpty)
	}

	// Note that we remove special characters then trim.
	@Test
	def whitespaceAndBom4(): Unit = {
		val first = " abc"
		val second = " abc " + "\uFEFF"

		val result = createAndTest(first, second)

		assert(result._1.nonEmpty)
		assert(result._2.isEmpty)
	}


	def createAndTest(first: String, second: String): (mutable.Buffer[MockFlowFile], mutable.Buffer[MockFlowFile]) = {
		testRunner.setProperty(StringComparisonProcessor.FIRST, first)
		testRunner.setProperty(StringComparisonProcessor.SECOND, second)

		testRunner.enqueue(new MockFlowFile((first + second).hashCode))
		testRunner.run()

		val succeededFlows = testRunner.getFlowFilesForRelationship(StringComparisonProcessor.REL_SUCCESS)
		val failedFlows = testRunner.getFlowFilesForRelationship(StringComparisonProcessor.REL_FAILURE)

		(succeededFlows.asScala, failedFlows.asScala)
	}
}
