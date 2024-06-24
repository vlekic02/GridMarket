#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail
if [[ "${TRACE-0}" == "1" ]]; then
    set -o xtrace
fi

INSTANCE_NAME=$1
IMAGE=$2
ZONE=$3

instanceExist() {
  if [ -z "$(gcloud compute instances list --filter="name=($INSTANCE_NAME)" --format="value(name)")" ]; then
      return 1
    else
      return 0
    fi
}

updateInstance() {
  gcloud compute instances update-container "$INSTANCE_NAME" --container-image="$IMAGE"
  echo "Existing instance updated"
}

createInstance() {
  gcloud compute instances create-with-container "$INSTANCE_NAME" \
      --zone="$ZONE" \
      --machine-type=e2-small \
      --network-interface=network-tier=PREMIUM,stack-type=IPV4_ONLY,subnet=default \
      --metadata=startup-script='#! /bin/bash
      docker image prune -af' \
      --maintenance-policy=MIGRATE \
      --provisioning-model=STANDARD \
      --service-account=542366028313-compute@developer.gserviceaccount.com \
      --scopes=https://www.googleapis.com/auth/devstorage.read_only,https://www.googleapis.com/auth/logging.write,https://www.googleapis.com/auth/monitoring.write,https://www.googleapis.com/auth/servicecontrol,https://www.googleapis.com/auth/service.management.readonly,https://www.googleapis.com/auth/trace.append \
      --tags=http-server-8080 \
      --image=projects/cos-cloud/global/images/cos-stable-113-18244-85-36 \
      --boot-disk-size=10GB \
      --boot-disk-type=pd-balanced \
      --boot-disk-device-name="$INSTANCE_NAME" \
      --container-image="$IMAGE" \
      --container-restart-policy=always \
      --container-env=SPRING_PROFILES_ACTIVE=cloud \
      --no-shielded-secure-boot \
      --shielded-vtpm \
      --shielded-integrity-monitoring \
      --labels=goog-ec-src=vm_add-gcloud,container-vm=cos-stable-113-18244-85-36
  echo "New instance created"
}

main() {
  if instanceExist; then
    updateInstance
  else
    createInstance
  fi
}



main "$@"