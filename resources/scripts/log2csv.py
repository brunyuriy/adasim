# Script: run-sim.py
# Purpose: Runs a number of randomly generated simulations. 
# Author: Jochen Wuttke, wuttkej@gmail.com
# Date:
#
# The number and kind of simulations to run are defined in a simple text file.
# The script takes a filename as argument

import argparse
import sys
import subprocess
import xml.dom.minidom
import os
import re

class CarData:
	id = ""
	path = []
	path_length = 0
	hops = 0
	time = 0
	strategy = ""

def parse_arguments():
	'''
	Parse and return the command line arguments.
	'''
	parser = argparse.ArgumentParser(description='Data directory and output file')
	parser.add_argument('-f', dest='OUTPUT_FILE', required=True, help='Output file name.')
	parser.add_argument('-d', dest='DATA_DIR', required=True, help='Directory containing simulation config and output.')
	return parser.parse_args()

def get_cmd_output(cmd, args):
	'''
	Returns the standard output from running:
	$ cmd args[0] args[1] .. args[n]

	Where cmd is the command name (e.g., 'svn') and args is a list of
	arguments to the command (e.g., ['help', 'log']).
	'''
	return subprocess.Popen([cmd] + args,
							stdout=subprocess.PIPE,
							stderr=subprocess.STDOUT).communicate()[0]

def run_simulation(nodes, cars, iteration):
	print str(iteration) + ": Running simulation from " + file_prefix(nodes, cars, iteration) + ".xml"
	log = get_cmd_output( "java", ["traffic.TrafficMain", "-I", file_prefix(nodes, cars, iteration) + ".xml"])
	out = open( file_prefix(nodes, cars, iteration) + ".log", "w")
	out.write( log );
	out.close()

def process_path(line):
	#print line
	#split_re = re.compile(".+Car: (\d+) From: \d+ To: \d+ Path: [(.+)]")
	split_re = re.compile(".+Car: (\d+).+\[(.*)]")
	result = split_re.match(line)
	car_id = result.group(1)
	#print "ID: " + car_id
	path = result.group(2)
	#print "Path: " + path
	car = CarData()
	car.id = car_id
	car.path = path
	car.path_length = len(path.split(', '))
	return car

def process_move(line):
	split_re = re.compile(".+Car: (\d+).+")
	return split_re.match(line).group(1)

def process_log(file, prefix):
	cars = {}
	time = 0
	path_re = re.compile(".+- PATH: .+")
	update_re = re.compile( ".+UPDATE: .+")
	move_re = re.compile(".+MOVE: .+")
	stop_re = re.compile(".+STOP: .+")
	time_re = re.compile(".+SIMULATION: .+" )

	for line in file:
		if path_re.match( line ):
			car = process_path(line)
			car.id = car.id + prefix
			cars[ car.id ] = car
		#elif update_re.match(line):
		#	print "Matched UPDATE"
		elif move_re.match(line):
			moved_car_id = process_move(line) + prefix
			cars[moved_car_id].hops = cars[moved_car_id].hops + 1
			
		elif stop_re.match(line):
			moved_car_id = process_move(line) + prefix
			cars[moved_car_id].time = time
			
		elif time_re.match(line):
			time += 1
		#else:
		#	print line
	return cars

def process_xml(file, prefix, cars):
	doc = xml.dom.minidom.parse(file)
	car_nodes = doc.documentElement.getElementsByTagName("car")
	for car in car_nodes:
		#print "Car " + car.getAttribute("id") + " Strategy: " + car.getAttribute("strategy") 
		car_id = car.getAttribute("id") + prefix
		if cars.has_key(car_id):
			cars[car_id].strategy = car.getAttribute("strategy")
	return cars

def process_files(dir, prefix):
	log_file = open( dir + "/" + prefix + ".log" )
	xml_file = open( dir + "/" + prefix + ".xml" )
	cars = process_log(log_file, prefix)
	cars = process_xml(xml_file, prefix, cars)
	print "Checking " + dir + "/" + prefix
	return cars

def write_line( file, nums, car):
	write_line2( file, nums[0] , nums[1] , car.id , str(car.hops) , 
		str(car.time), car.strategy ) 

def write_line2( file, nodes, cars, id, pl, time, strategy):
	file.write( nodes + "," + cars + "," + id + "," + 
		pl + "," + time + "," + strategy + "\n") 
	

def write_cars(file, prefix, cars):
	nums = prefix.split('-')
	for car in cars.values():
		write_line(file, nums, car)

def main(args):
	'''Main cycle'''
	output_file = open( args.OUTPUT_FILE, "w")
	write_line2(output_file, "Nodes", "Cars", "Car", "Hops", "Time", "Strategy" )
	for file in os.listdir( args.DATA_DIR ) :
		if file.endswith(".xml"):
			prefix = file.split('.')[0]
			cars = process_files(args.DATA_DIR, prefix )
			write_cars(output_file, prefix, cars)
	output_file.close()

if __name__ == "__main__":
	args = parse_arguments()
	ret = main(args)

