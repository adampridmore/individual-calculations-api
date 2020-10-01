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

package v1.controllers

import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Result
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import v1.fixtures.getIncomeTaxAndNics.IncomeTaxAndNicsResponseFixture
import v1.handler.RequestDefn
import v1.mocks.hateoas.MockHateoasFactory
import v1.mocks.requestParsers.MockGetCalculationParser
import v1.mocks.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService, MockStandardService}
import v1.models.audit.{AuditError, AuditEvent, AuditResponse, GenericAuditDetail}
import v1.models.errors._
import v1.models.hateoas.{HateoasWrapper, Link}
import v1.models.hateoas.Method.GET
import v1.models.outcomes.ResponseWrapper
import v1.models.request.{GetCalculationRawData, GetCalculationRequest}
import v1.models.response.getIncomeTaxAndNics.IncomeTaxAndNicsHateoasData
import v1.models.response.calculationWrappers.CalculationWrapperOrError

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GetIncomeTaxAndNicsControllerSpec
    extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockGetCalculationParser
    with MockStandardService
    with MockHateoasFactory
    with MockAuditService
    with GraphQLQuery {

  override val query: String = INCOME_TAX_AND_NICS_QUERY

  trait Test {
    val hc = HeaderCarrier()

    val controller = new GetIncomeTaxAndNicsController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockGetCalculationParser,
      service = mockStandardService,
      hateoasFactory = mockHateoasFactory,
      auditService = mockAuditService,
      cc = cc
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
  }

  private val nino          = "AA123456A"
  private val correlationId = "X-123"

  private val rawData     = GetCalculationRawData(nino, IncomeTaxAndNicsResponseFixture.calculationId)
  private val requestData = GetCalculationRequest(Nino(nino), IncomeTaxAndNicsResponseFixture.calculationId)

  private def uri = s"/$nino/self-assessment/${IncomeTaxAndNicsResponseFixture.calculationId}"
  private def queryUri = "/input/uri"

  val testHateoasLink = Link(href = "/foo/bar", method = GET, rel = "test-relationship")

  val linksJson: JsObject = Json.parse(
    """
      |{
      |    "links": [
      |      {
      |       "href": "/foo/bar",
      |       "method": "GET",
      |       "rel": "test-relationship"
      |      }
      |    ]
      |}
      |""".stripMargin).as[JsObject]

  "handleRequest" should {
    "return OK with the calculation" when {
      "happy path" in new Test {
        MockGetCalculationParser
          .parse(rawData)
          .returns(Right(requestData))

        MockStandardService
          .doService(RequestDefn.GraphQl(uri, query), OK)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, CalculationWrapperOrError.CalculationWrapper(IncomeTaxAndNicsResponseFixture.incomeTaxAndNicsResponseJsonFromBackend)))))

        MockHateoasFactory
            .wrap(IncomeTaxAndNicsResponseFixture.incomeTaxAndNicsResponseJson, IncomeTaxAndNicsHateoasData(nino, IncomeTaxAndNicsResponseFixture.calculationId))
          .returns(HateoasWrapper(IncomeTaxAndNicsResponseFixture.incomeTaxAndNicsResponseJson, Seq(testHateoasLink)))


        val result: Future[Result] = controller.getIncomeTaxAndNics(nino, IncomeTaxAndNicsResponseFixture.calculationId)(fakeGetRequest(queryUri))

        status(result) shouldBe OK
        val responseBody: JsObject = IncomeTaxAndNicsResponseFixture.incomeTaxAndNicsResponseJson.as[JsObject].deepMerge(linksJson)
        contentAsJson(result) shouldBe responseBody
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val detail = GenericAuditDetail(
          "Individual", None, Map("nino" -> nino, "calculationId" -> IncomeTaxAndNicsResponseFixture.calculationId), None, correlationId,
          AuditResponse(OK, None, Some(responseBody)))
        val event = AuditEvent("retrieveSelfAssessmentTaxCalculationIncomeTaxNicsCalculated",
          "retrieve-self-assessment-tax-calculation-income-tax-nics-calculated", detail)
        MockedAuditService.verifyAuditEvent(event).once
      }
    }

    "return FORBIDDEN with the error message" when {
      "error count is greater than zero" in new Test {
        MockGetCalculationParser
          .parse(rawData)
          .returns(Right(requestData))

        MockStandardService
          .doService(RequestDefn.GraphQl(uri, query), OK)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, CalculationWrapperOrError.ErrorsInCalculation))))

        val result: Future[Result] = controller.getIncomeTaxAndNics(nino, IncomeTaxAndNicsResponseFixture.calculationId)(fakeGetRequest(queryUri))

        status(result) shouldBe FORBIDDEN
        contentAsJson(result) shouldBe Json.toJson(RuleCalculationErrorMessagesExist)
        header("X-CorrelationId", result) shouldBe Some(correlationId)

        val detail = GenericAuditDetail(
          "Individual", None, Map("nino" -> nino, "calculationId" -> IncomeTaxAndNicsResponseFixture.calculationId), None, correlationId,
          AuditResponse(FORBIDDEN, Some(Seq(AuditError(RuleCalculationErrorMessagesExist.code))), None))
        val event = AuditEvent("retrieveSelfAssessmentTaxCalculationIncomeTaxNicsCalculated",
          "retrieve-self-assessment-tax-calculation-income-tax-nics-calculated", detail)
        MockedAuditService.verifyAuditEvent(event).once
      }
    }
  }
}
