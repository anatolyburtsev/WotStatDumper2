#!/bin/bash


sqlite3 $1 <<!
.headers on
.mode csv
.output out.csv
select * from $2;
!
