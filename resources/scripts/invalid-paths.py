#!/usr/bin/python
#
# Script:
# Purpose:
# Author: Jochen Wuttke, wuttkej@gmail.com
# Date:

import sys
import re
import math

log = open(sys.argv[1])
line_num = 0
for line in log.readlines():
	line_num = line_num +1
	result = re.search( "Path: \[(.*)\]", line)
	if ( result ):
#		print result.group(1)
		nodes = result.group(1).split(", ")
		n = 0
		while( n < len(nodes) -1 ):
			if ( n < len(nodes)-2 and math.fabs( int(nodes[n]) - int(nodes[n+1])) == 84 ):
				print "INVALID PATH on line " + str(line_num) + ": " + result.group(1) + ": [" + nodes[n] + ", " + nodes[n+1] + "]"
				break
			n = n+1
