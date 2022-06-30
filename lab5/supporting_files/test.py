#!/bin/python

import sys
import os
import zipfile
import shutil
import filecmp
import subprocess
from threading import Timer

submissions_temp_dir = "./scratch/" 

if not os.path.exists(submissions_temp_dir):
	os.mkdir(submissions_temp_dir)

shutil.copyfile("build.xml", submissions_temp_dir + "/build.xml")
shutil.copytree("src", submissions_temp_dir + "/src") 

os.chdir(submissions_temp_dir)

stdout_file = open("./tmp.output", 'a')
popen_args = ["ant", "make-jar"]
proc = subprocess.Popen(popen_args, stdout = stdout_file, stderr = stdout_file)
timer = Timer(5, proc.kill)
try:
	timer.start()
	stdout, stderr = proc.communicate()
finally:
	timer.cancel()
stdout_file.close()

if not os.path.exists("jars/simulator.jar"):
	print("Compilation failed. jar file not created")
	sys.exit(0)

test_cases_dir = "../test_cases"
total_marks = 0
scored_marks = 0
for testcase in os.listdir(test_cases_dir):
	if ".out" in testcase:
		total_marks = total_marks + 1

		stdout_file = open("./" + testcase.split(".")[0] + ".observedoutput", 'w')
		popen_args = ["java", "-Xmx1g", "-jar", "jars/simulator.jar", "./src/configuration/config.xml", "./" + testcase.split(".")[0] + ".observedstat", test_cases_dir + "/" + testcase]
		proc = subprocess.Popen(popen_args, stdout = stdout_file, stderr = stdout_file)
		timer = Timer(5, proc.kill)
		try:
			timer.start()
			stdout, stderr = proc.communicate()
		finally:
			timer.cancel()
		stdout_file.close()

		if os.path.exists("./" + testcase.split(".")[0] + ".observedoutput"):
			expectedoutput_file = open(test_cases_dir + "/" + testcase.split(".")[0] + ".expected")
			expected_hash = expectedoutput_file.readline()
			expectedoutput_file.close()

			correct = False
			observedoutput_file = open("./" + testcase.split(".")[0] + ".observedoutput")
			for line in observedoutput_file:
				if "Hash" in line:
					print("computed = " + line)
					print("expected = " + expected_hash)
				if line == expected_hash:
					correct = True
					break
			observedoutput_file.close()
			if correct == True:
				scored_marks = scored_marks + 1
				print(testcase + " : PASSED!")
			else:
				print(testcase + " : FAILED - incorrect hash")
		else:
			print(testcase + " : FAILED - standard output file not created")

os.chdir("..")
shutil.rmtree(submissions_temp_dir)
print("\ntotal score = " + str(scored_marks) + " out of " + str(total_marks))
