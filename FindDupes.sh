#!/bin/bash
cat primerci_bgb.csv | sed -e 's/"//g' | sort > sorted.csv
cut -d, -f1 sorted.csv | uniq -d > dupes.txt
grep -f dupes.txt sorted.csv > dupRn.csv
rm -f dupes.txt
rm -f sorted.csv
wc -l dupRn.csv
