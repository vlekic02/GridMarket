#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail
if [[ "${TRACE-0}" == "1" ]]; then
    set -o xtrace
fi

main() {
  gcloud beta emulators pubsub start --project=gridmarket-dev --host-port='0.0.0.0:8085' &
  sleep 10
  initPip
  createTopics
  tail -f /dev/null
}

initPip() {
  apt-get update
  apt-get install python3-pip -y
}

createTopics() {
  cd pubsub/samples/snippets
  pip install -r requirements.txt
  export PUBSUB_EMULATOR_HOST=localhost:8085
  python3 publisher.py gridmarket-dev create user
}

main "$@"