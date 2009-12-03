#!/bin/sh
version=`date "+%Y.%m.%d.%H.%M"`
mkdir -p bak/$version
for file in *; do
	if test -f $file; then
		cp $file bak/$version
	fi
done

