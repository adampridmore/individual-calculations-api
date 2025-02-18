/*
 * Copyright 2022 HM Revenue & Customs
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

package v2.models.response.getEoyEstimate.detail

import play.api.libs.json.{Json, OFormat}
import EoyEstimateIncomeSourceSummary._

case class EoyEstimateDetail(selfEmployments: Option[Seq[EoyEstimateSelfEmployments]],
                             ukPropertyFhl: Option[IncomeSourceSummaryWithFinalised],
                             ukPropertyNonFhl: Option[IncomeSourceSummaryWithFinalised],
                             ukSavings: Option[Seq[EoyEstimateUkSavings]],
                             ukDividends: Option[IncomeSourceSummary],
                             otherDividends: Option[IncomeSourceSummary],
                             foreignCompanyDividends: Option[IncomeSourceSummary],
                             stateBenefits: Option[IncomeSourceSummary],
                             ukSecurities: Option[IncomeSourceSummary],
                             foreignProperty: Option[IncomeSourceSummaryWithFinalised],
                             eeaPropertyFhl: Option[IncomeSourceSummary],
                             foreignInterest: Option[IncomeSourceSummary],
                             otherIncome: Option[IncomeSourceSummary],
                             foreignPension: Option[IncomeSourceSummary])

object EoyEstimateDetail {
  implicit val format: OFormat[EoyEstimateDetail] = Json.format[EoyEstimateDetail]
}
