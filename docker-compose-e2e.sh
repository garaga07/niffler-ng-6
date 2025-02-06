#!/bin/bash
source ./docker.properties
export COMPOSE_PROFILES=test
export PROFILE=docker
export PREFIX="${IMAGE_PREFIX}"
export ALLURE_DOCKER_API=http://allure:5050/
export HEAD_COMMIT_MESSAGE="local build"
export ARCH=$(uname -m)

# Значения по умолчанию
BROWSER="chrome"
SKIP_BUILD=false

while [[ $# -gt 0 ]]; do
  case "$1" in
    chrome|firefox)
      BROWSER="$1"
      ;;
    --skip-build)
      SKIP_BUILD=true
      ;;
  esac
  shift
done

echo "### Selected browser: $BROWSER ###"
export SELENOID_BROWSER=$BROWSER

echo "### Skip build: $SKIP_BUILD ###"

echo '### Java version ###'
java --version

if [ "$SKIP_BUILD" = false ]; then
  echo "### Stopping and removing old containers ###"
  docker compose down
  docker_containers=$(docker ps -a -q)

  if [ -n "$docker_containers" ]; then
    docker stop $docker_containers
    docker rm $docker_containers
  fi

  docker_images=$(docker images --format '{{.Repository}}:{{.Tag}}' | grep 'niffler')
  if [ -n "$docker_images" ]; then
    echo "### Removing images: $docker_images ###"
    docker rmi $docker_images
  fi

  echo "### Running build ###"
  bash ./gradlew clean
  bash ./gradlew jibDockerBuild -x :niffler-e-2-e-tests:test

  echo "### Starting all containers ###"
  docker compose up -d
else
  echo "### Skipping build and image cleanup ###"

  echo "### Recreating test container with new browser ###"
  docker compose rm -f niffler-e-2-e
  docker compose up -d niffler-e-2-e
fi

docker ps -a