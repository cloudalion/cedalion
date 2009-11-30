#!/bin/bash

baseName=$1
echo Hello, $baseName
echo "buildResults('$baseName')." | swipl -f unittest.pl

