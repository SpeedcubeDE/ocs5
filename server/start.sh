#!/bin/bash
parent_path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
cd "$parent_path"
RESTART=1;
while [ $RESTART -eq 1 ]; do
  java -jar ocs5.jar 1;
  RESTART=$?;
done
