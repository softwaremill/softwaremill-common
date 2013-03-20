#!/bin/bash

if [ $# -ne 2 ]
then
  echo "Usage: restrore_sdb [domain name] [file name]"
  exit 1
fi

# Configuration
###############

source backup_conf.sh

#########

# Creating backup files
java -cp $SDB_BACKUP_PATH com.softwaremill.common.backup.RunRestore $SIMPLEDB_ACCESS_KEY_ID $SIMPLEDB_SECRET_ACCESS_KEY $SIMPLEDB_REGION $1 $2
