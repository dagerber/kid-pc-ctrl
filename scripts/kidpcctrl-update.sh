#!/bin/bash

USERXY=$(users | grep userxy)
if [ -n "$DANIEL" ]; then
  curl --data "username=Daniel" localhost:8080/kidpcctrl/rest/track/update
fi

# add more users... as above
