#!/bin/bash
curl --data "username=<username>" http://localhost:8080/kidpcctrl/track/update

curl -X GET http://localhost:8080/kidpcctrl/track/info/<username>
