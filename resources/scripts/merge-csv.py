#!/usr/bin/python
#
# Script:
# Purpose:
# Author: Jochen Wuttke, wuttkej@gmail.com
# Date:

import sys, os

def merge(out, file):
	f = open(file)
	f.readline() #skip header
	for line in f.readlines():
		out.write(line)

def main():
	files = os.listdir( "." )
	output = open("data.log", "w")
	output.write("Nodes, Cars, Car, Hops, Time, Strategy\n" )
	for file in files:
		if file.endswith( ".csv" ):
			merge(output, file)	
	os.rename("data.log", "data.csv")

if __name__ == "__main__":
	#args = parse_arguments()
	ret = main()
