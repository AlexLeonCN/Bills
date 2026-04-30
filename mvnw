#!/usr/bin/env bash
set -euo pipefail

MVN_VERSION="${MVN_VERSION:-3.9.9}"
MVN_DIST="apache-maven-${MVN_VERSION}"
MVN_ARCHIVE="${MVN_DIST}-bin.tar.gz"
MVN_URL="https://archive.apache.org/dist/maven/maven-3/${MVN_VERSION}/binaries/${MVN_ARCHIVE}"
BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
MVN_HOME="${BASE_DIR}/.mvn/wrapper/${MVN_DIST}"

if [ ! -x "${MVN_HOME}/bin/mvn" ]; then
  mkdir -p "${BASE_DIR}/.mvn/wrapper"
  TMP_FILE="$(mktemp)"
  cleanup() {
    rm -f "${TMP_FILE}"
  }
  trap cleanup EXIT

  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "${MVN_URL}" -o "${TMP_FILE}"
  elif command -v wget >/dev/null 2>&1; then
    wget -q "${MVN_URL}" -O "${TMP_FILE}"
  else
    echo "curl or wget is required to download Maven ${MVN_VERSION}." >&2
    exit 1
  fi

  tar -xzf "${TMP_FILE}" -C "${BASE_DIR}/.mvn/wrapper"
fi

exec "${MVN_HOME}/bin/mvn" "$@"
