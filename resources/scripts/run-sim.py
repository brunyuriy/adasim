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

def parse_arguments():
    '''
    Parse and return the command line arguments.
    '''
    parser = argparse.ArgumentParser(description='Simulation config file')
    parser.add_argument('-f', dest='CONFIG_FILE', required=True, help='Config file to read. Example: -t svn')
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

def file_prefix(n,c,i):
	return str(n) + "-" + str(c) + "-" + str(i)

def build_simulation(nodes, cars, iteration):
	print str(iteration) + ": Building simulation with " + nodes + " nodes, " + cars + " cars "
	print get_cmd_output( "java", ["adasim.generator.Generator", "-N", nodes, "-C", cars, "-D", "4", "-o", file_prefix(nodes, cars, iteration) + ".xml",
		"-d", "3:6", "-S", "adasim.algorithm.routing.ShortestPathRoutingAlgorithm,adasim.algorithm.routing.TrafficLookaheadRoutingAlgorithm",
		"--one-way-prob", "0.05" ] )

def run_simulation(nodes, cars, iteration):
	print str(iteration) + ": Running simulation from " + file_prefix(nodes, cars, iteration) + ".xml"
	log = get_cmd_output( "java", ["adasim.TrafficMain", "-I", file_prefix(nodes, cars, iteration) + ".xml"])
	out = open( file_prefix(nodes, cars, iteration) + ".log", "w")
	out.write( log );
	out.close()

def main(args):
    '''Main cycle'''
    cfg_file = open(args.CONFIG_FILE, "r")
    for line in cfg_file:
    	params = line.split(':')
    	for i in range(int(params[2])):
    		build_simulation(params[0], params[1], i)
    		run_simulation(params[0], params[1], i)

if __name__ == "__main__":
    args = parse_arguments()
    ret = main(args)

