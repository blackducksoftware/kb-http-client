# bd-kb-http-client

Black Duck-centric KnowledgeBase (KB) HTTP client library.

This is a wrapper library for the KnowledgeBase (KB) HTTP client library and provides 
utility APIs and classes for translating KB data in a Black Duck-centric manner. 

## Requirements

The Black Duck-centric KB HTTP client library requires Java 11 or later.

## Dependencies

* KB HTTP client

## Notes and caveats

The Black Duck-centric KB HTTP client makes certain assumptions in favor of convention...

+ Component
   - Follows moved links present in migrated responses.
+ Component version
   - Adds a default version of 'unknown' when no value is present.
   - Adds a default license definition of Unknown License when no value is present.
   - Follows moved links present in migrated responses.
+ Component variant
   - Adds a default external id of 'unknown' when no value is present.
   - Adds a default external namespace of 'unknown' when no value is present.
+ License
   - Adds a license family field that enables support of the RESTRICTED_PROPRIETARY license family.
   
* VulnerabilityMerger merges collections of vulnerabilities from different sources into a common format.
   
## Supported operations

+ Component
   - Find component (automatically follows migrations and converts to BD-centric component result).
   - Find component versions (automatically follows migrations and converts to BD-centric component version page result).
+ Component version
   - Find component version (automatically follows migrations and converts to BD-centric component version result).
   - Find component version CVE vulnerabilities (automatically follows migrations).
   - Find component version BDSA vulnerabilities (automatically follows migrations).
   - Find component version upgrade guidance (automatically follows migrations).
+ Component variant
   - Find component variant (converts to BD-centric component variant result).
+ License
   - Find license (converts to BD-centric license result).  
   - Find many licenses (converts to BD-centric license page result). 
   
## TODOs   

## Quick Start (code example)

Quick-start for using the Black Duck-centric KB HTTP client library with an emphasis on using defaults and access to the production KnowledgeBase.  

```java
// Initializtion
HttpClientConfiguration httpClientConfiguration = HttpClientConfigurationBuilder.create().userAgent("MyApplication/1.0").build();
KbConfiguration kbConfiguration = new KbConfiguration("my_license_key");
IBdKbHttpApi bdKbHttpApi = new BdKbHttpClientFactory().create(httpClientConfiguration, kbConfiguration);
IBdLicenseApi bdLicenseApi = bdKbHttpApi.getBdLicenseApi();
// Result contains the request method, request URI, HTTP response if available, and exception cause if available.
Result<BdLicense> result = bdLicenseApi.findBd(licenseId);
// HTTP response contains response code, expected response codes, message body if available, and migration metadata if available.
Optional<HttpResponse<BdLicense>> httpResponse = result.getHttpResponse();
```
