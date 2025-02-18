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

package v2.endpoints

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import support.V2IntegrationBaseSpec
import v2.models.errors._
import v2.stubs._

class CrystallisationControllerISpec extends V2IntegrationBaseSpec {

  private trait Test {

    val nino: String              = "AA123456A"
    val mtdTaxYear: String        = "2019-20"
    val downstreamTaxYear: String = "2020"
    val calculationId: String     = "4557ecb5-fd32-48cc-81f5-e6acd1099f3c"
    val requestBody: JsObject     = Json.obj("calculationId" -> calculationId)
    val correlationId: String     = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"

    def uri: String           = s"/crystallisation/$nino/$mtdTaxYear/crystallise"
    def downstreamUri: String = s"/income-tax/calculation/nino/$nino/$downstreamTaxYear/$calculationId/crystallise"

    def setupStubs(): StubMapping

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.2.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

  }

  "declaring crystallisation for a tax year" should {
    "return a 204 status code" when {
      "valid request is made with a successful NRS call" in new Test {

        val nrsSuccess: JsValue = Json.parse(
          s"""
             |{
             |  "nrSubmissionId":"2dd537bc-4244-4ebf-bac9-96321be13cdc",
             |  "cadesTSignature":"30820b4f06092a864886f70111111111c0445c464",
             |  "timestamp":""
             |}
         """.stripMargin
        )

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          NrsStub.onSuccess(NrsStub.POST, s"/mtd-api-nrs-proxy/$nino/itsa-crystallisation", ACCEPTED, nrsSuccess)
          DownstreamStub.onSuccess(DownstreamStub.POST, downstreamUri, NO_CONTENT)
        }

        val response: WSResponse = await(request().post(requestBody))

        response.status shouldBe NO_CONTENT
        response.header("Content-Type") shouldBe Some("application/json")
        response.body shouldBe ""
      }

      "any valid request is made with a failed NRS call" in new Test {

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          NrsStub.onError(NrsStub.POST, s"/mtd-api-nrs-proxy/$nino/itsa-crystallisation", INTERNAL_SERVER_ERROR, "An internal server error occurred")
        }

        val response: WSResponse = await(request().post(requestBody))

        response.status shouldBe NO_CONTENT
        response.header("Content-Type") shouldBe Some("application/json")
        response.body shouldBe ""
      }
    }

    "return error according to spec" when {
      "validation error" when {
        def validationErrorTest(requestNino: String,
                                requestTaxYear: String,
                                testRequestBody: JsObject,
                                expectedStatus: Int,
                                expectedBody: MtdError): Unit = {
          s"validation fails with ${expectedBody.code} error" in new Test {

            override val nino: String          = requestNino
            override val mtdTaxYear: String    = requestTaxYear
            override val requestBody: JsObject = testRequestBody

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
            }

            val response: WSResponse = await(request().post(requestBody))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
            response.header("Content-Type") shouldBe Some("application/json")
          }
        }

        val validRequestBody: JsObject   = Json.obj("calculationId" -> "4557ecb5-fd32-48cc-81f5-e6acd1099f3c")
        val invalidRequestBody: JsObject = Json.obj("calculationId" -> "notValidId")

        val input = Seq(
          ("AA1123A", "2017-18", validRequestBody, BAD_REQUEST, NinoFormatError),
          ("AA123456A", "20177", validRequestBody, BAD_REQUEST, TaxYearFormatError),
          ("AA123456A", "2015-16", validRequestBody, BAD_REQUEST, RuleTaxYearNotSupportedError),
          ("AA123456A", "2020-22", validRequestBody, BAD_REQUEST, RuleTaxYearRangeInvalidError),
          ("AA123456A", "2017-18", JsObject.empty, BAD_REQUEST, RuleIncorrectOrEmptyBodyError),
          ("AA123456A", "2017-18", invalidRequestBody, BAD_REQUEST, CalculationIdFormatError)
        )

        input.foreach(args => (validationErrorTest _).tupled(args))
      }

      "service error" when {
        def errorBody(code: String): String =
          s"""
             |{
             |  "code": "$code",
             |  "message": "backend message"
             |}
            """.stripMargin

        def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"backend returns an $downstreamCode error and status $downstreamStatus" in new Test {

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              DownstreamStub.onError(DownstreamStub.POST, downstreamUri, downstreamStatus, errorBody(downstreamCode))
            }

            val response: WSResponse = await(request().post(requestBody))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
            response.header("Content-Type") shouldBe Some("application/json")
          }
        }

        val input = Seq(
          (BAD_REQUEST, "INVALID_IDTYPE", INTERNAL_SERVER_ERROR, DownstreamError),
          (BAD_REQUEST, "INVALID_IDVALUE", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_TAXYEAR", BAD_REQUEST, TaxYearFormatError),
          (BAD_REQUEST, "INVALID_CALCID", BAD_REQUEST, CalculationIdFormatError),
          (NOT_FOUND, "NOT_FOUND", NOT_FOUND, NotFoundError),
          (CONFLICT, "INCOME_SOURCES_CHANGED", FORBIDDEN, RuleIncomeSourcesChangedError),
          (CONFLICT, "RECENT_SUBMISSIONS_EXIST", FORBIDDEN, RuleRecentSubmissionsExistError),
          (CONFLICT, "RESIDENCY_CHANGED", FORBIDDEN, RuleResidencyChangedError),
          (UNPROCESSABLE_ENTITY, "INVALID_INCOME_SOURCES", FORBIDDEN, RuleIncomeSourcesInvalid),
          (UNPROCESSABLE_ENTITY, "INCOME_SUBMISSIONS_NOT_EXIST", FORBIDDEN, RuleNoIncomeSubmissionsExistError),
          (UNPROCESSABLE_ENTITY, "BUSINESS_VALIDATION", FORBIDDEN, RuleSubmissionFailed),
          (CONFLICT, "FINAL_DECLARATION_RECEIVED", FORBIDDEN, RuleFinalDeclarationReceivedError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, DownstreamError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, DownstreamError),
          (NOT_FOUND, "UNMATCHED_STUB_ERROR", BAD_REQUEST, RuleIncorrectGovTestScenarioError)
        )

        input.foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }

}
