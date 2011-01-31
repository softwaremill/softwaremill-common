#!/bin/bash

# Configuration
###############

# where the backups will be uploaded
S3_ACCESS_KEY_ID=
S3_SECRET_ACCESS_KEY=
S3_BUCKET=
S3_PREFIX=

# what to backup - SimpleDB
SIMPLEDB_ACCESS_KEY_ID=
SIMPLEDB_SECRET_ACCESS_KEY=
SIMPLEDB_REGION=EUROPE_WEST
# * for all
SIMPLEDB_DOMAINS=

# what to backup - MySQL
MYSQL_USER=
MYSQL_PASSWORD=
MYSQL_HOST=
# * for all
MYSQL_DB=

# path to the backup jar
SDB_BACKUP_PATH=softwaremill-backup-45-SNAPSHOT-jar-with-dependencies.jar

#########