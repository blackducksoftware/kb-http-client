# kb-http-client
KnowledgeBase (KB) HTTP client library.

The target for the KB HTTP client library is support of KB API and KB Match-as-a-service (MaaS) endpoints.

# Requirements

The KB HTTP client library requires Java 11 or later.

# Dependencies

* [Guava](https://github.com/google/guava)
* [Jackson](https://github.com/FasterXML/jackson)
* [Apache HTTP Client](https://hc.apache.org/httpcomponents-client-5.2.x)

# Notes and caveats

The KB HTTP client makes certain assumptions in favor of convention...

* HTTP client configuration defaults - Users are encouraged to review configuration and carefully tailor configuration to their needs.
* Value presence and absence - Representation values received in response from the KB are tailored to the [Black Duck KB REST API](https://kbtest.blackducksoftware.com/docs/index.html) documentation contents.  Fallbacks to default values generally are not assumed and absence of value is preferred in alignment to existing documentation.
* BDSA feature flags - Requests for BDSA vulnerability data given a license with disabled BDSA feature flag can result in a HTTP 402 Payment Required or HTTP 403 Forbidden response from KnowledgeBase services.   The KB HTTP client gracefully accounts for these response codes for applicable endpoints by translating the response as absent rather than error as a means of graceful handling.   This is done to bolster reliability.   Users still have the option to check for these response codes and handle them in an alternate manner if desired.
* Authentication and authorization - The KB HTTP client will automatically authenticate to the KnowledgeBase to provide an authorization token.   The KB HTTP client will automatically reauthenticate to the KnowledgeBase when an existing, provided authorization token expires.

# Supported endpoints

+ Component APIs
    - Find component
    - Search component (package identifier component matching)
+ Component version APIs
    - Find component version
    - Find component version CVE vulnerabilities
    - Find component version BDSA vulnerabilities
    - Find component version upgrade guidance
+ Component variant APIs
    - Find component variant
    - Find component variant CVE vulnerabilities
    - Find component variant BDSA vulnerabilities
    - Find component variant upgrade guidance
    - Find component variant transitive upgrade guidance
+ License APIs
    - Find license

# TODOs

* Add SLF4J API to support debug logging.
* Add configuration for retry policy backoff (linear, exponential, etc.).
* Add support for more KB endpoints.

# Quick Start (code example)

Quick-start for using the KB HTTP client library with an emphasis on using defaults and access to the production KnowledgeBase.  

```java
HttpClientConfiguration httpClientConfiguration = HttpClientConfigurationBuilder.create().userAgent("MyApplication/1.0").build();
KbConfiguration kbConfiguration = new KbConfiguration("my_license_key");
IKbHttpApi kbHttpApi = new KbHttpClientFactory().create(httpClientConfiguration, kbConfiguration);
ILicenseApi licenseApi = kbHttpApi.getLicenseApi();
// Result contains the request method, request URI, HTTP response if available, and exception cause if available.
Result<License> result = licenseApi.find(licenseId);
// HTTP response contains response code, expected response codes, message body if available, and migration metadata if available.
Optional<HttpResponse<License>> httpResponse = result.getHttpResponse();
```
