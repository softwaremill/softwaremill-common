#!/bin/bash

# Configuration
###############

source backup_conf.sh

#########

CURRENT_DATE_WITH_HOUR=`date +"%d_%m_%Y__%H_%M"`
DUMP_FILE_NAME=mysql_${MYSQL_DB}_backup_${CURRENT_DATE_WITH_HOUR}.sql
TAR_FILE_NAME=mysql_${MYSQL_DB}_backup_${CURRENT_DATE_WITH_HOUR}.tar.gz

# Creating backup files
mysqldump -u $MYSQL_USER -h $MYSQL_HOST --password=$MYSQL_PASSWORD $MYSQL_DB > $DUMP_FILE_NAME

# Packing
tar -czf $TAR_FILE_NAME $DUMP_FILE_NAME

# Removing backup files
rm $DUMP_FILE_NAME

# Uploading to S3
java -cp $SDB_BACKUP_PATH pl.softwaremill.common.backup.S3Upload $S3_ACCESS_KEY_ID $S3_SECRET_ACCESS_KEY $S3_BUCKET $TAR_FILE_NAME

# Cleaning up
rm $TAR_FILE_NAME