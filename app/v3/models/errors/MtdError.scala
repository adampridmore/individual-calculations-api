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

package v3.models.errors

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class MtdError(code: String, message: String, paths: Option[Seq[String]] = None)

object MtdError {
  implicit val writes: OWrites[MtdError] = Json.writes[MtdError]

  implicit def genericWrites[T <: MtdError]: OWrites[T] =
    writes.contramap[T](c => c: MtdError)

  implicit val reads: Reads[MtdError] = (
    (__ \ "code").read[String] and
      (__ \ "reason").read[String] and
      Reads.pure(None)
  )(MtdError.apply _)

}

object CustomMtdError {
  def unapply(arg: MtdError): Option[String] = Some(arg.code)
}

// Format Errors
object NinoFormatError
    extends MtdError(
      code = "FORMAT_NINO",
      message = "The provided NINO is invalid"
    )

object TaxYearFormatError
    extends MtdError(
      code = "FORMAT_TAX_YEAR",
      message = "The provided tax year is invalid"
    )

object CalculationIdFormatError
    extends MtdError(
      code = "FORMAT_CALCULATION_ID",
      message = "The provided calculationId is invalid"
    )

object TypeFormatError
    extends MtdError(
      code = "FORMAT_TYPE",
      message = "The provided type value is invalid"
    )

object FinalDeclarationFormatError
    extends MtdError(
      code = "FORMAT_FINAL_DECLARATION",
      message = "The provided finalDeclaration is invalid"
    )

// Specific Not Found Errors
object NoMessagesExistError
    extends MtdError(
      code = "NO_MESSAGES_PRESENT",
      message = "No messages present"
    )

object EndOfYearEstimateNotPresentError
    extends MtdError(
      code = "END_OF_YEAR_ESTIMATE_NOT_PRESENT",
      message = "End of year estimate data will only be returned for an in-year calculation"
    )

// Rule Errors
object RuleTaxYearNotSupportedError
    extends MtdError(
      code = "RULE_TAX_YEAR_NOT_SUPPORTED",
      message = "The specified tax year is not supported. The tax year specified is before the minimum tax year value"
    )

object RuleIncorrectOrEmptyBodyError
    extends MtdError(
      code = "RULE_INCORRECT_OR_EMPTY_BODY_SUBMITTED",
      message = "An empty or non-matching body was submitted"
    )

object RuleTaxYearRangeInvalidError
    extends MtdError(
      code = "RULE_TAX_YEAR_RANGE_INVALID",
      message = "Tax year range invalid. A tax year range of one year is required"
    )

object RuleNoIncomeSubmissionsExistError
    extends MtdError(
      code = "RULE_NO_INCOME_SUBMISSIONS_EXIST",
      message = "No income submissions exist for the tax year"
    )

object RuleCalculationErrorMessagesExistError
    extends MtdError(
      code = "RULE_CALCULATION_ERROR_MESSAGES_EXIST",
      message = "Calculation error messages exist for the supplied calculation ID"
    )

object RuleNoSubmissionsExistError
    extends MtdError(
      code = "RULE_NO_SUBMISSIONS_EXIST",
      message = "No periodic or annual income data has been submitted"
    )

object RuleSubmissionFailedError
    extends MtdError(
      code = "RULE_SUBMISSION_FAILED",
      message = "The submission cannot be completed due to validation failures"
    )

object RuleFinalDeclarationReceivedError
    extends MtdError(
      code = "RULE_FINAL_DECLARATION_RECEIVED",
      message = "Final declaration has already been received"
    )

object RuleIncomeSourcesChangedError
    extends MtdError(
      code = "RULE_INCOME_SOURCES_CHANGED",
      message = "Income sources data has changed. Perform an Intent to submit a final declaration"
    )

object RuleIncomeSourcesInvalidError
    extends MtdError(
      code = "RULE_INCOME_SOURCES_INVALID",
      message = "No valid income sources could be found"
    )

object RuleRecentSubmissionsExistError
    extends MtdError(
      code = "RULE_RECENT_SUBMISSIONS_EXIST",
      message = "More recent submissions exist. Perform an Intent to submit a final declaration"
    )

object RuleResidencyChangedError
    extends MtdError(
      code = "RULE_RESIDENCY_CHANGED",
      message = "Residency has changed. Perform an Intent to submit a final declaration"
    )

// Standard Errors
object NotFoundError
    extends MtdError(
      code = "MATCHING_RESOURCE_NOT_FOUND",
      message = "Matching resource not found"
    )

object DownstreamError
    extends MtdError(
      code = "INTERNAL_SERVER_ERROR",
      message = "An internal server error occurred"
    )

object BadRequestError
    extends MtdError(
      code = "INVALID_REQUEST",
      message = "Invalid request"
    )

object BVRError
    extends MtdError(
      code = "BUSINESS_ERROR",
      message = "Business validation error"
    )

object ServiceUnavailableError
    extends MtdError(
      code = "SERVICE_UNAVAILABLE",
      message = "Internal server error"
    )

object InvalidBodyTypeError
    extends MtdError(
      code = "INVALID_BODY_TYPE",
      message = "Expecting text/json or application/json body"
    )

//Stub errors
object RuleIncorrectGovTestScenarioError
    extends MtdError(
      code = "RULE_INCORRECT_GOV_TEST_SCENARIO",
      message = "The Gov-Test-Scenario was not found"
    )

// Authorisation Errors
object UnauthorisedError
    extends MtdError(
      code = "CLIENT_OR_AGENT_NOT_AUTHORISED",
      message = "The client and/or agent is not authorised"
    )

object InvalidBearerTokenError
    extends MtdError(
      code = "UNAUTHORIZED",
      message = "Bearer token is missing or not authorized"
    )

// Accept header Errors
object InvalidAcceptHeaderError
    extends MtdError(
      code = "ACCEPT_HEADER_INVALID",
      message = "The accept header is missing or invalid"
    )

object UnsupportedVersionError
    extends MtdError(
      code = "NOT_FOUND",
      message = "The requested resource could not be found"
    )
