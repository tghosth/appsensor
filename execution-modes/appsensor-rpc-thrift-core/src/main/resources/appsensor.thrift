# appsensor.thrift

namespace * org.owasp.appsensor.rpc.thrift.generated

typedef i32 int

exception NotAuthenticatedException {
  1: string message
}

exception NotAuthorizedException {
  1: string message
}

struct GeoLocation {
	1:int id,
    2:double latitude,
    3:double longitude
}

struct IPAddress {
	1:int id,
	2:string address,
  	3:GeoLocation geoLocation
}

struct DetectionSystem {
	1:int id,
	2:string detectionSystemId,
  	3:IPAddress ipAddress
}

struct User {
  1:int id,
  2:string username,
  3:IPAddress ipAddress
}

struct Interval {
  1:int id,
  2:int duration,
  3:string unit
}

struct Threshold {
  1:int id,
  2:int count = 0,
  3:Interval interval
}

struct Resource {
  1:int id,
  2:string location,
}

struct Response {
  1:int id,
  2:User user,
  3:string timestamp,
  4:string action,
  5:Interval interval,
  6:DetectionSystem detectionSystem
}

struct DetectionPoint {
  1:int id,
  2:string category,
  3:string label,
  4:Threshold threshold,
  5:list<Response> responses
}

struct Event {
  1:int id,
  2:User user,
  3:DetectionPoint detectionPoint,
  4:string timestamp,
  5:DetectionSystem detectionSystem,
  6:Resource resource
}

struct Attack {
  1:int id,
  2:User user,
  3:DetectionPoint detectionPoint,
  4:string timestamp,
  5:DetectionSystem detectionSystem,
  6:Resource resource
}

service AppSensorApi
{
  void addEvent( 1: Event event, 2:string clientApplicationName ) throws (1:NotAuthenticatedException notAuthenticatedException, 2:NotAuthorizedException notAuthorizedException),
  void addAttack( 1: Attack attack, 2:string clientApplicationName ) throws (1:NotAuthenticatedException notAuthenticatedException, 2:NotAuthorizedException notAuthorizedException),
  list<Response> getResponses(1:string earliest, 2:string clientApplicationName) throws (1:NotAuthenticatedException notAuthenticatedException, 2:NotAuthorizedException notAuthorizedException)
}