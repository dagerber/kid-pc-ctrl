kid-pc-ctrl
===========

Control the time your kids are on your linux box

The older my kids get, the more they are attracted by the PC.
They love watching youtube videos and lately, they play a lot of minecraft.
I think that's OK, but I want to restrict the time.
My boys are 9 and 10 years old, and my wife and I think, that for their age, 20min per day should be enough.

There are programs around, that help control all that, but I didn't find any that would suit my needs. First, only very few are available on linux, second they help only restrict the time on a day basis.
Problem is, that on a sunny day, I'd rather see my kids playing outside and not touching the PC at all. None of the software I found can cope with 'saving the time to another day'.

I want my kids to organize their time by themselfes, stay away for few day and consume their time on a rainy day, it should be possible to produce some debt (negativ balance) on one day and stay away the next days.

They should be able to control their time with a simple webpage running on the same Server, that does the tracking.

That's why I came up with my own solution.

- My kids each have their own account on my linux box
- A cron job checks every minute, if one of the kids is logged on

bash-script: called from cron
USERXY=$(users | grep userxy)
if [ -n "$USERXY" ]; then
 curl --data "username=<username>" localhost:8080/kidpcctrl/rest/track/update
fi

- If so, it sends a http POST to a simple Restful Service (running on Tomcat)
- A mysql database stores the state
- the service adds 20min (or any other amount of time) per day

Additional services:
- no time tracking for a specified amount of time (e.g. homeworks)
- add extra time (for whatever reason)
- remove time (no comment - if you have kids, you know what I mean)
- reset time (after holidays)

As for now, no security is implemented... but that could very easily be implemented... I just need to protect the resources in the web.xml
If the kids should discover that themselves, they really deserve some bonus...

I have to access the additional function on the console with curl but probably will implement a very simple Android App for those tasks:

_curl --data "username=<username>&minutes=10" http://localhost:8080/kidpcctrl/rest/track/extracredit_

_curl --data "username=<username>&minutes=10" http://localhost:8080/kidpcctrl/rest/track/notrack_

_curl --data "username=<username>" http://localhost:8080/kidpcctrl/rest/track/reset_

_curl http://localhost:8080/kidpcctrl/rest/track/infos_

_curl http://localhost:8080/kidpcctrl/rest/track/info/<user>_

Webpage with infos: http://localhost:8080/kidpcctrl/static

Feel free to use the source, if you want to
https://github.com/dagerber/kid-pc-ctrl
