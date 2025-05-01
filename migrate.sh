#!/bin/bash

# Define usage function
usage() {
  echo "Usage: migrate.sh [options]"
  echo "Options:"
  echo "  -d, --dry-run   Dry Run migration"
  echo "  -p, --prod      Migration to production"
  echo "  -i, --info      Print info"
  echo "  -h, --help      Display this help message"
  exit 1
}

CMD="migrate"

# Parse arguments
for arg in "$@"; do
  case $arg in
    --dry-run | -d)
      if [ -n "$CONF" ]; then
        echo "Error: Select only one configuration"
        usage
      fi
      CONF="conf/test.conf"
      ;;
    --prod | -p)
      if [ -n "$CONF" ]; then
        echo "Error: Select only one configuration"
        usage
      fi
      CONF="conf/prod.conf"
      ;;
    --info | -i)
      CMD="info"
      ;;
    --help | -h)
      usage
      ;;
    *)
      echo "Error: Invalid argument $arg"
      usage
      ;;
  esac
done

# Must select one of the options
if [ -z "$CONF" ]; then
    echo "Error: Must select a migration option"
    usage
fi

echo "mvn clean flyway:$CMD -pl :db -Dflyway.configFiles=$CONF"
mvn flyway:$CMD -pl :db -Dflyway.configFiles=$CONF