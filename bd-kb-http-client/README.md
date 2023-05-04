# bd-kb-http-client

Black Duck-centric KnowledgeBase (KB) HTTP client library.

This is a wrapper library for the KnowledgeBase (KB) HTTP client library and provides 
utility APIs and classes for translating KB data in a Black Duck-centric manner. 

## Requirements

The Black Duck-centric KB HTTP client library requires Java 11 or later.

## Dependencies

* KB HTTP client

## License

The bd-kb-http-client is licensed under the [MIT License](https://github.com/blackducksoftware/kb-http-client/blob/main/LICENSE).

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
   - Adds a license family field that enables support of the `RESTRICTED_PROPRIETARY` license family.

* `BdComponentFinder` finds hierarchies of component version and component variant entities.
* `BdLicenseDefinitionFinder` finds complete license definition metadata.
* `BdVulnerabilityMerger` contains utility methods for merging collections of NVD and BDSA vulnerabilities together (presumably for the same component version or component variant).
   
## Supported operations

+ Component
   - Find component (automatically follows migrations).
   - Find component versions by component (automatically follows migrations and converts to BD-centric component version page result).
   - Find component version summaries by component (automatically follows migrations and converts to BD-centric component version summary page result).
   - Find ongoing version by component (automatically follows migrations).
+ Component version
   - Find component version (automatically follows migrations and converts to BD-centric component version result).
   - Find next version by component version (automatically follows migrations).
   - Find component version CVE vulnerabilities (automatically follows migrations).
   - Find component version BDSA vulnerabilities (automatically follows migrations).
   - Find component version upgrade guidance (automatically follows migrations).
+ Component variant
   - Find component variant (converts to BD-centric component variant result).
+ License
   - Find license (converts to BD-centric license result).  
   - Find many licenses (converts to BD-centric license page result). 
   - Find licenses by license term (converts to BD-centric license page result).
   
## TODOs   

## Quick Start (code example)

Quick-start for using the Black Duck-centric KB HTTP client library with an emphasis on using defaults and access to the production KB.  

```java
// Initialization of the HTTP clients and APIs.
HttpClientConfiguration httpClientConfiguration = HttpClientConfigurationBuilder.create().userAgent("MyApplication/1.0").build();
KbConfiguration kbConfiguration = new KbConfiguration("my_license_key");
IBdKbHttpApi bdKbHttpApi = new BdKbHttpClientFactory().create(httpClientConfiguration, kbConfiguration);

/* 
 * Use the Black Duck-centric license API to find a Black Duck-centric license by its license id.
 * 
 * HTTP result contains the request method, request URI, HTTP response if available, and exception cause if available.
 * 
 * HTTP response contains response code, expected response codes, message body if available, and migration metadata if available.
 */
IBdLicenseApi bdLicenseApi = bdKbHttpApi.getBdLicenseApi();
HttpResult<BdLicense> httpResult = bdLicenseApi.findLicenseV4(licenseId);
Optional<HttpResponse<BdLicense>> httpResponse = httpResult.getHttpResponse();

/* 
 * Use the Black Duck-centric component API to find a component by its component id.   The Black Duck-centric component API 
 * automatically follows migration links to find a destination component given the original, source component id.
 * 
 * Migratable HTTP result contains the request method, request URI, migratable HTTP response if available, and exception cause if available.
 * 
 * Migratable HTTP response contains response code, expected response codes, message body if available, migration metadata if available, and migrated meta history if migration links were followed to retrieve a final result.
 */
IBdComponentApi bdComponentApi = bdKbHttpApi.getBdComponentApi();
MigratableHttpResult<Component> migratableHttpResult = bdComponentApi.findComponentV4(componentId);
Optional<MigratableHttpResponse<Component>> migratableHttpResponse = migratableHttpResult.getMigratableHttpResponse();
```
