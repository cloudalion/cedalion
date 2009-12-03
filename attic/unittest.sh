#!/bin/bash

baseName=$1
echo "unitTest('$baseName')." | yap -l unittest.pl

