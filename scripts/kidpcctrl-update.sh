#!/bin/bash

USERXY=$(users | grep userxy)
if [ -n "$USERXY" ]; then
  curl --data "username=<username>" localhost:8080/kidpcctrl/rest/track/update
fi

# add more users... as above
