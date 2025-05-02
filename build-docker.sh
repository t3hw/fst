#!/bin/bash

# Define usage function
usage() {
  echo "Usage: script.sh [options]"
  echo "Options:"
  echo "  -s, --skipTests     Skip tests"
  echo "  -q, --quiet         Quiet mode"
  echo "  -o, --offline       Offline mode"
  echo "  -h, --help          Display this help message"
  exit 1
}

# Parse arguments
for arg in "$@"; do
  case $arg in
    --skipTests | -s)
      SKIP_TESTS="-DskipTests"
      ;;
    --quiet | -q)
      QUIET_MODE="-q"
      ;;
    --offline | -o)
      OFFLINE_MODE="-o"
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


if command -v mvnd &> /dev/null; then
    CMD="mvnd"
else
    CMD="mvn"
fi


if [ -n "$FIRST_STAGE" ]; then
    echo "$CMD $FIRST_STAGE"
    $CMD $FIRST_STAGE
    if [ $? -ne 0 ]; then
        echo "First stage build failed"
        exit 1
    fi
fi

# Execute the command with the specified options
echo "$CMD package $SKIP_TESTS $QUIET_MODE $OFFLINE_MODE $BUILD_MODE"
$CMD package $SKIP_TESTS $QUIET_MODE $OFFLINE_MODE $BUILD_MODE
if [ $? -ne 0 ]; then
    echo "Build failed"
    exit 1
fi

docker buildx bake $BUILD_TARGET
