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

CURRENT_DATE=`date +"%d_%m_%Y"`
CURRENT_DATE_WITH_HOUR=`date +"%d_%m_%Y__%H_%M"`
TAR_FILE_NAME=simpledb_backup_${CURRENT_DATE_WITH_HOUR}.tar.gz

# Creating backup files
java -cp $SDB_BACKUP_PATH pl.softwaremill.common.backup.RunRestore $SIMPLEDB_ACCESS_KEY_ID $SIMPLEDB_SECRET_ACCESS_KEY $SIMPLEDB_REGION $1 $2
