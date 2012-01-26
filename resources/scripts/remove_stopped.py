#!/usr/bin/python
#
# Script:
# Purpose:
# Author: Jochen Wuttke, wuttkej@gmail.com
# Date:

import sys
import re
import os

print sys.argv[0]

stopped = open(sys.argv[1])

for s in stopped.readlines():
	result = re.search("STOP: Car: (\d+)", s) 	
	if ( result ) :
		print "sed -i -e '/Car: " + result.group(1) + " /d' test.log"
		os.system( "sed -i -e '/Car: " + result.group(1) + " /d' test.log")
