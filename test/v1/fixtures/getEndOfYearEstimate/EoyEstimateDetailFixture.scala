/*
 * Copyright 2019 HM Revenue & Customs
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

package v1.fixtures.getEndOfYearEstimate

import play.api.libs.json.{JsValue, Json}
import v1.fixtures.getEndOfYearEstimate.detail._
import v1.models.response.getEndOfYearEstimate.detail.EoyEstimateDetail

object EoyEstimateDetailFixture {

  val json: JsValue = Json.parse(
    s"""
      |{
      | "selfEmployments" : [
      |   ${EoyEstimateSelfEmploymentsFixture.json}
      | ],
      | "ukPropertyFhl" : ${EoyEstimateUkPropertyFhlFixture.json},
      | "ukPropertyNonFhl" : ${EoyEstimateUkPropertyNonFhlFixture.json},
      | "ukSavings" : [
      |   ${EoyEstimateUkSavingsFixture.json}
      | ],
      | "ukDividends" : ${EoyEstimateUkDividendsFixture.json}
      |}
    """.stripMargin)

  val model = EoyEstimateDetail(
    Some(Seq(EoyEstimateSelfEmploymentsFixture.model)),
    Some(EoyEstimateUkPropertyFhlFixture.model),
    Some(EoyEstimateUkPropertyNonFhlFixture.model),
    Some(Seq(EoyEstimateUkSavingsFixture.model)),
    Some(EoyEstimateUkDividendsFixture.model)
  )
}
