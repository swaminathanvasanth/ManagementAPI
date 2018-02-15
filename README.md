# ManagementAPI
APIs for Management of Devices and Applications in Middleware of Smart City (0.1.0).

Entity Registration
Using the Provider API key, you can register a device/application with the smart city middleware. This registration process is done once per device/application by the Provider. With each registration, you will receive a Device/Application API key with which you can start publishing data or subscribing data.

Curl command
curl -i -X GET "https://smartcity.rbccps.org/api/0.1.0/register" -H 'apikey: USER_API_KEY' -H 'resourceID: APPLICATION_OR_DEVICE_NAME' -H 'serviceType: publish,subscribe,historicData' 

Example: 

curl -i -X GET "https://smartcity.rbccps.org/api/0.1.0/register" -H 'apikey: beee99bb9d024fbf97800be726f85a57' -H 'resourceID: testDemo' -H 'serviceType: publish,subscribe,historicData'


