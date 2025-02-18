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

package v2.services

import cats.data.EitherT
import cats.implicits._
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging
import v2.connectors.IntentToCrystalliseConnector
import v2.controllers.EndpointLogContext
import v2.models.errors._
import v2.models.outcomes.ResponseWrapper
import v2.models.request.intentToCrystallise.IntentToCrystalliseRequest
import v2.models.response.intentToCrystallise.IntentToCrystalliseResponse
import v2.support.BackendResponseMappingSupport

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class IntentToCrystalliseService @Inject() (connector: IntentToCrystalliseConnector) extends BackendResponseMappingSupport with Logging {

  def submitIntentToCrystallise(request: IntentToCrystalliseRequest)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      logContext: EndpointLogContext,
      correlationId: String): Future[Either[ErrorWrapper, ResponseWrapper[IntentToCrystalliseResponse]]] = {

    val result = for {
      downstreamResponseWrapper <- EitherT(connector.submitIntentToCrystallise(request)).leftMap(mapDownstreamErrors(downstreamErrorMap))
    } yield downstreamResponseWrapper

    result.value
  }

  private def downstreamErrorMap: Map[String, MtdError] =
    Map(
      "INVALID_NINO"            -> NinoFormatError,
      "INVALID_TAX_YEAR"        -> TaxYearFormatError,
      "INVALID_TAX_CRYSTALLISE" -> DownstreamError,
      "INVALID_REQUEST"         -> DownstreamError,
      "NO_SUBMISSION_EXIST"     -> RuleNoSubmissionsExistError,
      "CONFLICT"                -> RuleFinalDeclarationReceivedError,
      "SERVER_ERROR"            -> DownstreamError,
      "SERVICE_UNAVAILABLE"     -> DownstreamError,
      "UNMATCHED_STUB_ERROR"    -> RuleIncorrectGovTestScenarioError
    )

}
