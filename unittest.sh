#!/bin/bash

baseName=$1
echo "unitTest('$baseName')." | swipl -f unittest.pl

