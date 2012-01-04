#!/bin/bash
#
#Script:
#Purpose:
#Project:
#Author: Jochen Wuttke	jochen.wuttke@gmx.de
#Date:
#Version:
#

CP=lib/jdom-1.1.2.jar:lib/jopt-simple-3.2.jar:lib/log4j-1.2.16.jar:bin:

java -cp $CP traffic.generator.Generator -N 100 -C 50 -D 4 -o random.xml -d 3:6 \
-S traffic.strategy.LookaheadShortestPathCarStrategy,traffic.strategy.AdaptiveCarStrategy --one-way-prob 0.05 --congested

java -cp $CP traffic.TrafficMain -I random.xml

