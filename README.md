# kb-http-client

KnowledgeBase (KB) HTTP client library.

The target for the KB HTTP client library is support of KB API and KB Match-as-a-service (MaaS) endpoints.

## Requirements

The KB HTTP client library requires Java 11 or later.

## Dependencies

* [Guava](https://github.com/google/guava)
* [Jackson](https://github.com/FasterXML/jackson)
* [Apache HTTP Client](https://hc.apache.org/httpcomponents-client-5.2.x)

## License

Licensed under the [MIT License](https://github.com/blackducksoftware/kb-http-client/blob/main/LICENSE).

## Notes and caveats

The KB HTTP client makes certain assumptions in favor of convention.

* HTTP client configuration defaults - Users are encouraged to review configuration and carefully tailor configuration to their needs.
* Value presence and absence - Representation values received in response from the KB are tailored to the [Black Duck KB REST API](https://kbtest.blackducksoftware.com/docs/index.html) documentation contents.  Fallbacks to default values generally are not assumed and absence of value is preferred in alignment to existing documentation.
* BDSA feature flags - Requests for BDSA vulnerability data given a license with disabled BDSA feature flag can result in a `HTTP 402 Payment Required` or `HTTP 403 Forbidden` response code from KB services.   The KB HTTP client gracefully accounts for these response codes for applicable endpoints by translating the response as absent rather than error as a means of graceful handling.   This is done to bolster reliability.   Users still have the option to check for these response codes and handle them in an alternate manner if desired.
* Authentication and authorization - The KB HTTP client will automatically authenticate to the KB to provide an authorization token.   The KB HTTP client will automatically reauthenticate to the KB when an existing, provided authorization token expires.

## Supported endpoints

+ Component APIs
    - Find component
    - Find component versions by component.
    - Find component version summaries by component.
    - Find ongoing version by component.
    - Search component (package identifier component matching)
+ Component version APIs
    - Find component version
    - Find next version by component version
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
    - Find license text    
    - Find many licenses
    - Find licenses by license term
    - Find license term
    - Find many license terms
    - Find license terms by license
    - Find many license VSLs
    - Find many license family VSLs
+ Vulnerability APIs
    - Find CVE vulnerability
    - Find BDSA vulnerability    
    - Find BDSA vulnerability ranges
+ Activity APIs
    - Find component activities
    - Find component ongoing version activities
    - Find component version activities
    - Find component version license activities
    - Find component version CVE vulnerability activities
    - Find component version BDSA vulnerability activities
    - Find component version upgrade guidance activities
    - Find component variant activities
    - Find component variant CVE vulnerability activities        
    - Find component variant BDSA vulnerability activities
    - Find component variant upgrade guidance activities
    - Find component variant transitive upgrade guidance activities
    - Find license activities
    - Find license license term activities
    - Find CVE vulnerability activities
    - Find BDSA vulnerability activities
                            
## Build

+ [TestNG](https://testng.org) is used as the test library harness.
    - Functional tests are conditionally ran if a JVM parameter of `synopsys_kb_httpclient_license_key_path` is provided with the path to a present file that contains a single line with the KB license key credential.
+ Javadoc is linted and created.
+ [Jacoco](https://github.com/jacoco/jacoco) is used for code coverage. 

```
% ./gradlew clean build
...
BUILD SUCCESSFUL in 22s
```

## Release

+ Development (SNAPSHOT) publication

Publishing the library with a "-SNAPSHOT" version suffix will create a development release within the internal Synopsys artifact manager.

[Snapshots (internal)](https://artifactory.internal.synopsys.com/artifactory/bds-integrations-snapshot/com/synopsys/integration/kb-http-client/)

```
% export ARTIFACTORY_DEPLOYER_PASSWORD=...
% export ARTIFACTORY_DEPLOYER_USER=...
% ./gradlew clean build deployLibrary --refresh-dependencies -x signMavenJavaPublication
```

+ Release publication

Publishing the library without a "-SNAPSHOT" version suffix will create a production release within the internal Synopsys artifact manager.

[Release (internal)](https://artifactory.internal.synopsys.com/artifactory/bds-integrations-release/com/synopsys/integration/kb-http-client/)

```
% export ARTIFACTORY_DEPLOYER_PASSWORD=...
% export ARTIFACTORY_DEPLOYER_USER=...
% ./gradlew clean build deployLibrary --refresh-dependencies -x signMavenJavaPublication
``` 

## TODOs

* Add SLF4J API to support debug logging.
* Add support for more KB endpoints.
* Add parameters to conditionally add Cache-Control header of 'no-cache' to request up-to-date contents for migration responses and relationships. 

## Quick Start (code example)

Quick-start for using the KB HTTP client library with an emphasis on using defaults and access to the production KB.  

```java
// Initialization of the HTTP clients and APIs.
HttpClientConfiguration httpClientConfiguration = HttpClientConfigurationBuilder.create().userAgent("MyApplication/1.0").build();
KbConfiguration kbConfiguration = new KbConfiguration("my_license_key");
IKbHttpApi kbHttpApi = new KbHttpClientFactory().create(httpClientConfiguration, kbConfiguration);

/* 
 * Use the license API to find a license by its license id.
 * 
 * HTTP result contains the request method, request URI, HTTP response if available, and exception cause if available.
 * 
 * HTTP response contains response code, expected response codes, message body if available, and migration metadata if available.
 */
ILicenseApi licenseApi = kbHttpApi.getLicenseApi();
HttpResult<License> httpResult = licenseApi.findLicenseV4(licenseId);
Optional<HttpResponse<License>> httpResponse = httpResult.getHttpResponse();
```
