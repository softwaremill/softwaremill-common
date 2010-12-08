#!/bin/bash

# Configuration
###############

# where the backups will be uploaded
S3_ACCESS_KEY_ID=
S3_SECRET_ACCESS_KEY=
S3_BUCKET=

# what to backup - SimpleDB
SIMPLEDB_ACCESS_KEY_ID=
SIMPLEDB_SECRET_ACCESS_KEY=
SIMPLEDB_REGION=US_EAST
SIMPLEDB_DOMAINS=

# what to backup - MySQL
MYSQL_USER=
MYSQL_PASSWORD=
MYSQL_HOST=
MYSQL_DB=

# path to the backup jar
SDB_BACKUP_PATH=softwaremill-backup-1-jar-with-dependencies.jar

#########