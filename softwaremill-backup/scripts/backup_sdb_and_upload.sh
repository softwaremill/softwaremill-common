#!/bin/bash

# Configuration
###############

source backup_conf.sh

#########

CURRENT_YEAR_MONTH=`date +"%Y_%m"`
CURRENT_DATE=`date +"%d_%m_%Y"`
CURRENT_DATE_WITH_HOUR=`date +"%d_%m_%Y__%H_%M"`
TAR_FILE_NAME=simpledb_backup_${CURRENT_DATE_WITH_HOUR}.tar.gz

# Creating backup files
java -jar $SDB_BACKUP_PATH $SIMPLEDB_ACCESS_KEY_ID $SIMPLEDB_SECRET_ACCESS_KEY $SIMPLEDB_REGION "$SIMPLEDB_DOMAINS"

# Packing
tar -czf $TAR_FILE_NAME simpledb_backup*${CURRENT_DATE}*.txt

# Removing backup files
rm simpledb_backup*${CURRENT_DATE}*.txt

# Uploading to S3
java -cp $SDB_BACKUP_PATH pl.softwaremill.common.backup.S3Upload $S3_ACCESS_KEY_ID $S3_SECRET_ACCESS_KEY $S3_BUCKET $TAR_FILE_NAME $S3_PREFIX$CURRENT_YEAR_MONTH

# Cleaning up
rm $TAR_FILE_NAME
