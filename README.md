# MTA-orchestration

#Install rabbitmq locally
sudo apt-get install rabbitmq-server

#start the rabbitmq
service rabbitmq-server start

#open the file test/resources/MockRestService-soapui-project.xml using SOAPUI
#run the mock service using SOAPUI

#run the MTAOrchestrationApplication



send a post request using postman to
http://localhost:9280/mta-orchestration-api/v1/mta
with body
{
	"name":"test"
}

you will get the response
{
    "name": "test",
    "tax": 12.86,
    "postcode": "sm2 9jk",
    "refData": "the is a ref data"
}

The Routing Strategy
====================


Request from postman --> Rest endpoint  --->  (parallel multicast to IPT and Postcode rest services) ---->Aggregated Result ---> Ref Data Rest Call ---> Response to postman

