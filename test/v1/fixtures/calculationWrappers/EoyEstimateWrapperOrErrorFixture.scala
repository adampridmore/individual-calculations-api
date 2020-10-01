/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package v1.fixtures.calculationWrappers

import play.api.libs.json.{JsValue, Json}
import v1.fixtures.getEndOfYearEstimate.EoyEstimateResponseFixture.eoyEstimateResponseJson
import v1.models.response.calculationWrappers.EoyEstimateWrapperOrError
import v1.models.response.calculationWrappers.EoyEstimateWrapperOrError.EoyEstimateWrapper

object EoyEstimateWrapperOrErrorFixture {

  def backendJson(endOfYearEstimateResponse: JsValue, calculationType: Option[String], calculationErrorCount: Option[Int]): JsValue = Json.obj(
    "data" -> Json.obj("endOfYearEstimate" -> endOfYearEstimateResponse,
    "metadata" -> Seq(("calculationType" -> calculationType), ("calculationErrorCount" -> calculationErrorCount))
      .filter(_._2.isDefined)
      .foldLeft(Json.obj())((acc, curr) => {
        curr match {
          case (k, Some(v: String)) => acc ++ Json.obj(k -> v)
          case (k, Some(v: Int))    => acc ++ Json.obj(k -> v)
          case _                    => acc
        }
      })
    )
  )

  def wrappedEoyEstimateModel(calculationType: Option[String] = None, calculationErrorCount: Option[Int] = None): EoyEstimateWrapperOrError =
    EoyEstimateWrapper(backendJson(eoyEstimateResponseJson, calculationType, calculationErrorCount))

  val eoyEstimateWrapperJsonWithErrors: JsValue = Json.parse(
    s"""
       |{
       |  "data": {
       |    "endOfYearEstimate": $eoyEstimateResponseJson,
       |    "metadata": {
       |      "calculationType": "inYear",
       |      "calculationErrorCount": 1
       |    }
       |  }
       |}
    """.stripMargin
  )

  val eoyEstimateWrapperJsonWithoutErrorCount: JsValue = Json.parse(
    s"""
       |{
       |  "data": {
       |    "endOfYearEstimate": $eoyEstimateResponseJson,
       |    "metadata": {
       |      "calculationType": "inYear"
       |    }
       |  }
       |}
     """.stripMargin)

  val eoyEstimateWrapperJsonWithoutErrors: JsValue = Json.parse(
    s"""{
       |  "data": {
       |    "endOfYearEstimate": $eoyEstimateResponseJson,
       |    "metadata": {
       |      "calculationType": "inYear",
       |      "calculationErrorCount": 0
       |    }
       |  }
       |}
    """.stripMargin)

  val eoyEstimateWrapperJsonWithoutCalculationType: JsValue = Json.parse(
    s"""{
       |  "data": {
       |    "endOfYearEstimate": $eoyEstimateResponseJson,
       |    "metadata": {
       |    }
       |  }
       |}
    """.stripMargin)

  val edyEstimateWrapperJsonCrystallised: JsValue = Json.parse(
    s"""{
       |  "data": {
       |    "endOfYearEstimate": $eoyEstimateResponseJson,
       |    "metadata": {
       |      "calculationType": "crystallisation",
       |      "calculationErrorCount": 0
       |    }
       |  }
       |}
    """.stripMargin)
}
